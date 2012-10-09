package org.drugis.common.threading.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.PredicateUtils;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.TaskUtil;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.common.threading.event.TaskProgressEvent;

public class ActivityTaskProgressModel extends AbstractProgressModel {

	private final class PhaseListener implements TaskListener {
		public void taskEvent(TaskEvent event) {
			synchronized(d_lock) {
				Task source = event.getSource();
				if(d_phaseProgress.containsKey(source)) {
					updateTaskProgress(source, event);
				}
			}
		}

		private void updateTaskProgress(Task source, TaskEvent event) {

			EventType type = event.getType();
			switch (type) {
			case TASK_FINISHED:
				d_phaseProgress.put(source, 1.0);
				break;
			case TASK_PROGRESS:
				d_phaseProgress.put(source, calcProgress((TaskProgressEvent) event));
				break;
			case TASK_RESTARTED:
			case TASK_STARTED:
				d_phaseProgress.put(source, 0.0);
			default:
				break;
			}
			setProgress(calcProgress());
			setDeterminate(calcDeterminate());
		}

	}

	private List<Task> d_phases = new ArrayList<Task>();
	private HashMap<Task, Double> d_phaseProgress = new HashMap<Task, Double>();
	private PhaseListener d_phaseListener = new PhaseListener();
	private Object d_lock = new Object();
	private int d_numberOfIterables = 0;


	public ActivityTaskProgressModel(ActivityTask activity) {
		d_task = activity;
		if(!d_task.isFinished()) {
			Set<Task> states = activity.getModel().getStates();
			for(Task state : states) {
				d_phaseProgress.put(state, 0.0);
				state.addTaskListener(d_phaseListener);
				d_phases.add(state);
			}
		}
		d_numberOfIterables = getIterables(d_phases).size();
	}

	@SuppressWarnings("unchecked")
	private Collection<? extends IterativeTask> getIterables(Collection<? extends Task> collection) {
		return CollectionUtils.select(collection, PredicateUtils.instanceofPredicate(IterativeTask.class));
	}

	@Override
	protected Double calcProgress() {
		double progress = 0;

		for(IterativeTask task : getIterables(d_phases)) {
			Double value = d_phaseProgress.get(task);
			progress = progress + ((value == null) ? 0 : value);
		}
		return progress / d_numberOfIterables;
	}


	protected List<String> getPhaseStrings() {
		List<String> phaseStrings = new ArrayList<String>();
		synchronized (d_lock) {
			for (Task p : d_phases) {
				if(TaskUtil.isRunning(p) && p.isStarted()) {
					phaseStrings.add(p.toString() + ": " + formatProgress(d_phaseProgress.get(p)));
				}
			}
		}
		return phaseStrings;
	}

	@Override
	protected boolean calcDeterminate() {
		return true;
	}

}
