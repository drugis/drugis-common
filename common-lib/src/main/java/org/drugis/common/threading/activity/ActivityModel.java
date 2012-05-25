package org.drugis.common.threading.activity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.common.threading.Task;

/**
 * Represents a complex (possibly multi-threaded) task as a number of interrelated SimpleTasks.
 * The representation mirrors UML 2 activity diagrams.
 */
public class ActivityModel {
	private final Task d_start;
	private final Task d_end;
	private Map<Task, Transition> d_transitions = new HashMap<Task, Transition>();

	public ActivityModel(Task start, Task end, Collection<? extends Transition> transitions) {
		d_start = start;
		d_end = end;
		for (Transition t : transitions) {
			for (Task s : t.getSources()) {
				d_transitions.put(s, t);
			}
		}
	}
	
	/**
	 * Builds a list of all states that are accessible, but not yet finished.
	 * @return A list of tasks to run.
	 */
	public Set<Task> getNextStates() {
		if (isFinished()) {
			return Collections.emptySet();
		}
		return findAccessibleStates(d_start);
	}
	
	private Set<Task> findAccessibleStates(Task state) {
		// if this state is not finished yet, no successors need to be evaluated
		if (!state.isFinished()) {
			return Collections.singleton(state);
		}
		
		// find the relevant transition and evaluate it if ready
		Transition trans = d_transitions.get(state);
		if (!trans.isReady()) {
			return Collections.emptySet();
		}
		
		Set<Task> accessible = new HashSet<Task>();
		List<Task> next = trans.transition();
		for (Task task : next) {
			accessible.addAll(findAccessibleStates(task));
		}
		
		return accessible;
	}

	public boolean isFinished() {
		return d_end.isFinished();
	}
	
	public Task getStartState() {
		return d_start;
	}
	
	public Task getEndState() {
		return d_end;
	}

	public Set<Task> getStates() {
		Set<Task> tasks = new HashSet<Task>(d_transitions.keySet());
		tasks.add(d_end);
		return tasks;
	}
	
	public Task getStateByName(String name) { 
		for (Task t : getStates()) { 		
			if(name.equals(t.toString())) { 
				return t;
			}
		}
		return null;
	}
	
	public Set<Transition> getTransitions() {
		return new HashSet<Transition>(d_transitions.values());
	}
}
