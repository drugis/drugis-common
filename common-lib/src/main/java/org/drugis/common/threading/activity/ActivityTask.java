package org.drugis.common.threading.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.CompositeTask;
import org.drugis.common.threading.SimpleTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.ListenerManager;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskFailedEvent;
import org.drugis.common.threading.event.TaskFinishedEvent;
import org.drugis.common.threading.event.TaskRestartedEvent;
import org.drugis.common.threading.event.TaskStartedEvent;

public class ActivityTask implements CompositeTask {
	/**
	 * Listens to sub-tasks to notify phase start/finish and task finish.
	 */
	private final class PhaseListener implements TaskListener {
		public void taskEvent(TaskEvent event) {
			if (event instanceof TaskStartedEvent) {
				d_mgr.firePhaseStarted(event.getSource());
			} else if (event instanceof TaskFinishedEvent) {
				d_mgr.firePhaseFinished(event.getSource());
				if (event.getSource() == d_model.getEndState()) {
					d_mgr.fireTaskFinished();
				}
			} else if (event instanceof TaskFailedEvent) {
				d_mgr.fireTaskFailed(((TaskFailedEvent) event).getCause());
			} else if (event instanceof TaskRestartedEvent) {
				d_mgr.firePhaseRestarted(event.getSource());
			}
		}
	}

	private ListenerManager d_mgr = new ListenerManager(this);
	private final ActivityModel d_model;
	private boolean d_started = false;
	private String d_str;
	
	public ActivityTask(ActivityModel model, String str) {
		d_model = model;
		d_str = str;
		final PhaseListener listener = new PhaseListener();
		for (Task t : d_model.getStates()) {
			t.addTaskListener(listener);
		}
	}
	
	public ActivityTask(ActivityModel model) {
		this(model, model.toString());
	}

	public void addTaskListener(TaskListener l) {
		d_mgr.addTaskListener(l);
	}

	public void removeTaskListener(TaskListener l) {
		d_mgr.removeTaskListener(l);
	}

	public boolean isStarted() {
		return d_started ;
	}

	public boolean isFinished() {
		return d_model.isFinished();
	}

	public boolean isFailed() {
		return getFailureCause() != null;
	}

	public Throwable getFailureCause() {
		if (!isStarted() || isFinished()) {
			return null;
		}
		for (SimpleTask t : getNextTasks()) {
			if (t.isFailed()) {
				return t.getFailureCause();
			}
		}
		return null;
	}

	public boolean isAborted() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<SimpleTask> getNextTasks() {
		if (!isStarted()) {
			return Collections.emptyList();
		}
		ArrayList<SimpleTask> list = new ArrayList<SimpleTask>();
		for (Task t : d_model.getNextStates()) {
			if (t instanceof SimpleTask) {
				list.add((SimpleTask)t);
			} else {
				throw new RuntimeException("Unhandled Task type " + t.getClass());
			}
		}
		return list;
	}

	public void start() {
		d_started = true;
		d_mgr.fireTaskStarted();
	}

	public ActivityModel getModel() {
		return d_model;
	}
	
	@Override
	public String toString() {
		return d_str;
	}
}
