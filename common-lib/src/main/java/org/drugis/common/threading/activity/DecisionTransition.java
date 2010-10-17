package org.drugis.common.threading.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;

/**
 * A DecisionTransition chooses one of its target states based on a conditional. 
 */
public class DecisionTransition implements Transition {
	private final Task d_source;
	private final Task d_ifTask;
	private final Task d_elTask;
	private final Condition d_condition;

	/**
	 * Transition to ifTask if condition evaluates to true, and to elTask otherwise.
	 * @param source Prior state.
	 * @param ifTask Target if condition.evaluate() == true.
	 * @param elTask Target if condition.evaluate() == false.
	 * @param condition The condition to be evaluated.
	 */
	public DecisionTransition(Task source, Task ifTask, Task elTask, Condition condition) {
		d_source = source;
		d_ifTask = ifTask;
		d_elTask = elTask;
		d_condition = condition;
	}

	public List<Task> getSources() {
		return Collections.singletonList(d_source);
	}

	public List<Task> getTargets() {
		List<Task> l = new ArrayList<Task>();
		l.add(d_ifTask);
		l.add(d_elTask);
		return l;
	}

	public boolean isReady() {
		return d_source.isFinished();
	}

	public List<Task> transition() {
		if (!isReady()) {
			throw new RuntimeException("Not ready for transition.");
		}
		if (d_condition.evaluate()) {
			return Collections.singletonList(d_ifTask);
		} else {
			return Collections.singletonList(d_elTask);
		}
	}
}