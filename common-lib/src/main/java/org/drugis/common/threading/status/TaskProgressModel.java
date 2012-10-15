package org.drugis.common.threading.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.PhaseEvent;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.common.threading.event.TaskProgressEvent;

public class TaskProgressModel extends AbstractProgressModel {
	private final class PhaseListener implements TaskListener {
		public void taskEvent(TaskEvent event) {
			synchronized(d_lock) {
				if (event.getType().equals(TaskEvent.EventType.TASK_PROGRESS)) {
					d_phaseProgress.put(event.getSource(), calcProgress((TaskProgressEvent) event));
					setDeterminate(calcDeterminate());
					setProgress(calcProgress());
				}
			}
		}
	}

	private Map<Task, Double> d_phaseProgress = new HashMap<Task, Double>();
	List<Task> d_phases = new ArrayList<Task>();
	private PhaseListener d_phaseListener = new PhaseListener();
	private Object d_lock = new Object();
	private TaskListener d_taskListener = new PhaseTaskListener();

	public TaskProgressModel(Task task) {
		setTask(task);
	}

	public void setTask(Task task) {
		if (d_task != null) {
			d_task.removeTaskListener(d_taskListener);
		}
		d_task = task;
		if(task.isFinished()) {
			setProgress(1.0);
		}
		removePhases();
		setDeterminate(task.isFinished());
		task.addTaskListener(d_taskListener);
	}

	public Task getTask() {
		return d_task;
	}

	private void removePhases() {
		for (Task t : d_phases) {
			t.removeTaskListener(d_phaseListener);
		}
		d_phases.clear();
		d_phaseProgress.clear();
	}

	@Override
	protected boolean calcDeterminate() {
		if (d_phases.isEmpty()) {
			return false;
		}
		for(Task p : d_phases) {
			if (d_phaseProgress.get(p) == null)
				return false;
		}
		return true;
	}

	@Override
	protected Double calcProgress() {
		Set<Double> values = new HashSet<Double>(d_phaseProgress.values());
		values.remove(null);
		if(!values.isEmpty()) {
			return Collections.min(values);
		}
		return 0.0;
	}


	protected List<String> getPhaseStrings() {
		List<String> phaseStrings = new ArrayList<String>();
		synchronized (d_lock) {
			for (Task p : d_phases) {
				phaseStrings.add(p.toString() + ": " + formatProgress(d_phaseProgress.get(p)));
			}
		}
		return phaseStrings;
	}

	protected class PhaseTaskListener implements TaskListener {
		public void taskEvent(TaskEvent e) {
			synchronized(d_lock) {
				EventType type = e.getType();
				if (type.equals(TaskEvent.EventType.TASK_STARTED)) {
					setProgress(0.0);
				} else if (type.equals(TaskEvent.EventType.TASK_PROGRESS)) {
					setDeterminate(true);
					setProgress(calcProgress((TaskProgressEvent) e));
				} else if (type.equals(TaskEvent.EventType.TASK_FINISHED)) {
					setDeterminate(true);
					setProgress(1.0);
				} else if (type.equals(TaskEvent.EventType.TASK_FAILED)) {
					setDeterminate(true);
					setProgress(0.0);
				} else if (type.equals(TaskEvent.EventType.TASK_ABORTED)) {
					setDeterminate(true);
					setProgress(0.0);
				} else if (type.equals(TaskEvent.EventType.TASK_RESTARTED)) {
					setDeterminate(true);
					setProgress(0.0);
				} else if (e instanceof PhaseEvent) {
					PhaseEvent evt = (PhaseEvent) e;
					Task phase = evt.getPhase();
					if (type.equals(TaskEvent.EventType.PHASE_STARTED)) {
						d_phases.add(phase);
						phase.addTaskListener(d_phaseListener);
						d_phaseProgress.put(phase, null);
					} else if (type.equals(TaskEvent.EventType.PHASE_FINISHED)) {
						phase.removeTaskListener(d_phaseListener);
						d_phaseProgress.remove(phase);
						d_phases.remove(phase);
					}
					setDeterminate(calcDeterminate());
					setProgress(calcProgress());
				}
			}
		}
	}
}
