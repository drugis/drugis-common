package org.drugis.common.threading.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.PredicateUtils;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.event.PhaseEvent;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.common.threading.event.TaskProgressEvent;

public class ActivityTaskProgressModel extends AbstractProgressModel {

	private final class PhaseListener implements TaskListener {
		public void taskEvent(TaskEvent event) {
			synchronized(d_lock) {
				Task source = event.getSource();
				if(d_progress.containsKey(source)) {
					updateTaskProgress(source, event);
				}
			}
		}

		private void updateTaskProgress(Task source, TaskEvent event) {
			EventType type = event.getType();
			switch (type) {
				case TASK_FINISHED:
					d_progress.put(source, 1.0);
					break;
				case TASK_PROGRESS:
					d_progress.put(source, calcProgress((TaskProgressEvent) event));
					break;
				case TASK_RESTARTED:
				case TASK_STARTED:
					d_progress.put(source, 0.0);
				default:
					break;
			}
			setProgress(calcProgress());
			setDeterminate(calcDeterminate());
		}

	}

	private Set<Task> d_runningTasks = new HashSet<Task>();
	private Set<Task> d_iterables = new HashSet<Task>();
	private HashMap<Task, Double> d_progress = new HashMap<Task, Double>();
	private PhaseListener d_listener = new PhaseListener();
	private Object d_lock = new Object();


	public ActivityTaskProgressModel(ActivityTask activity) {
		d_task = activity;
		Set<Task> states = activity.getModel().getStates();

		if(!d_task.isFinished()) {
			for(Task state : states) {
				d_progress.put(state, 0.0);
				state.addTaskListener(d_listener);
			}
		}
		d_task.addTaskListener(new TaskListener() {
			public void taskEvent(TaskEvent e) {
				if (e instanceof PhaseEvent) {
					Task phase = ((PhaseEvent) e).getPhase();
					if (e.getType().equals(TaskEvent.EventType.PHASE_STARTED)) {
						d_runningTasks.add(phase);
					} else if (e.getType().equals(TaskEvent.EventType.PHASE_FINISHED)) {
						d_runningTasks.remove(phase);
					}
					setDeterminate(calcDeterminate());
					setProgress(calcProgress());
				}
			}
		});
		d_iterables.addAll(findIterables(states));
	}

	@SuppressWarnings("unchecked")
	private Collection<? extends IterativeTask> findIterables(Collection<? extends Task> collection) {
		return CollectionUtils.select(collection, PredicateUtils.instanceofPredicate(IterativeTask.class));
	}

	@Override
	protected Double calcProgress() {
		double progress = 0;

		for(Task task : d_iterables) {
			Double value = d_progress.get(task);
			progress = progress + ((value == null) ? 0 : value);
		}
		return progress / d_iterables.size();
	}


	protected List<String> getPhaseStrings() {
		List<String> phaseStrings = new ArrayList<String>();
		synchronized (d_lock) {
			for (Task p : d_runningTasks) {
				phaseStrings.add(p.toString() + ": " + formatProgress(d_progress.get(p)));
			}
		}
		return phaseStrings;
	}

	@Override
	protected boolean calcDeterminate() {
		return !CollectionUtils.intersection(d_runningTasks, d_iterables).isEmpty();
	}

}
