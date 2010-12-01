package org.drugis.common.gui.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.gui.TextProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.PhaseEvent;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskProgressEvent;

public class TaskProgressModel extends AbstractObservable implements TextProgressModel {
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

	public static final String DONE_TEXT = "Done!";
	public static final String WAITING_TEXT = "waiting";
	public static final String FAILED_TEXT = "failed";
	public static final String ABORTED_TEXT = "Aborted";
	private Task d_task;
	private Double d_progress;
	private Map<Task, Double> d_phaseProgress = new HashMap<Task, Double>();
	private List<Task> d_phases = new ArrayList<Task>();
	private boolean d_determinate;
	private PhaseListener d_phaseListener = new PhaseListener();
	private Object d_lock = new Object();
	private TaskListener d_taskListener = new MyTaskListener();
	
	public TaskProgressModel(Task task) {
		setTask(task);
	}

	public void setTask(Task task) {
		if (d_task != null) {
			d_task.removeTaskListener(d_taskListener);
		}
		d_task = task;
		removePhases();
		setDeterminate(false);
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
	}

	private boolean calcDeterminate() {
		if (d_phases.isEmpty()) {
			return false;
		}
		for(Task p : d_phases) {
			if (d_phaseProgress.get(p) == null)
				return false;
		}
		return true;
	}

	protected Double calcProgress() {
		Set<Double> values = new HashSet<Double>(d_phaseProgress.values());
		values.remove(null);
		if(!values.isEmpty()) {
			return Collections.min(values);
		}
		return 0.0;
	}

	public boolean getDeterminate() {
		return d_determinate;
	}

	public Double getProgress() {
		return d_progress;
	}

	private double calcProgress(TaskProgressEvent evt) {
		return (double) evt.getIteration() / evt.getTotalIterations();
	}

	public String getText() {
		String taskName = d_task.toString();
		
		List<String> phaseStrings = getPhaseStrings();
		
		String phaseString = " (" + StringUtils.join(phaseStrings, ", ") + ")";
		
		if (!d_task.isStarted()) {
			return taskName + " (" + WAITING_TEXT + ")";
		}
		if (d_task.isFinished()) {
			return DONE_TEXT;
		} 
		if (d_task.isFailed()) {
			return taskName + " "+FAILED_TEXT +": " + d_task.getFailureCause().getMessage();
		}
		if (d_task.isAborted()) {
			return taskName + ": " + ABORTED_TEXT;
		}
		if (!d_phases.isEmpty()) {
			return taskName + phaseString;
		}
		return taskName + ": " + formatProgress(getDeterminate() ? d_progress : null);
	}

	private List<String> getPhaseStrings() {
		List<String> phaseStrings = new ArrayList<String>();
		synchronized (d_lock) {
			for (Task p : d_phases) {
				phaseStrings.add(p.toString() + ": " + formatProgress(d_phaseProgress.get(p)));
			}			
		}
		return phaseStrings;
	}

	private String formatProgress(Double progress) {
		if(progress == null) return "?";
		Long round = (Long)Math.round(100.0 * progress);
		return round.toString() + "%";
	}

	private void setProgress(Double progress) {
		Double oldValue = d_progress;
		d_progress = progress;
		firePropertyChange(PROPERTY_PROGRESS, oldValue, d_progress);
		firePropertyChange(PROPERTY_TEXT, null, getText());
	}

	private void setDeterminate(boolean determinate) {
		boolean oldValue = d_determinate;
		d_determinate = determinate;
		firePropertyChange(PROPERTY_DETERMINATE, oldValue, d_determinate);
	}
	
	private class MyTaskListener implements TaskListener {
		public void taskEvent(TaskEvent e) {
			synchronized(d_lock) {
				if (e.getType().equals(TaskEvent.EventType.TASK_STARTED)) {
					setProgress(0.0);
				} else if (e.getType().equals(TaskEvent.EventType.TASK_PROGRESS)) {
					setDeterminate(true);
					setProgress(calcProgress((TaskProgressEvent) e));
				} else if (e.getType().equals(TaskEvent.EventType.TASK_FINISHED)) {
					setDeterminate(true);
					setProgress(1.0);
				} else if (e.getType().equals(TaskEvent.EventType.TASK_FAILED)) {
					setDeterminate(true);
					setProgress(0.0);
				} else if (e.getType().equals(TaskEvent.EventType.TASK_ABORTED)) {
					setDeterminate(true);
					setProgress(0.0);
				} else if (e instanceof PhaseEvent) {
					PhaseEvent evt = (PhaseEvent) e;
					Task phase = evt.getPhase();
					if (e.getType().equals(TaskEvent.EventType.PHASE_STARTED)) {
						d_phases.add(phase);
						phase.addTaskListener(d_phaseListener);
						d_phaseProgress.put(phase, null);
					} else if (e.getType().equals(TaskEvent.EventType.PHASE_FINISHED)) {
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
