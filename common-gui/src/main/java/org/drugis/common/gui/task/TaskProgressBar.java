package org.drugis.common.gui.task;

import javax.swing.JProgressBar;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.PhaseFinishedEvent;
import org.drugis.common.threading.event.PhaseStartedEvent;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskFinishedEvent;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.drugis.common.threading.event.TaskStartedEvent;

public class TaskProgressBar extends JProgressBar {
	public static final String DONE_TEXT = "Done!";
	public static final String WAITING_TEXT = "waiting";
	private static final long serialVersionUID = -4003048586663833970L;
	
	private String d_title = "";
	private final Task d_task;
	
	private final TaskListener d_phaseListener = new TaskListener() {
		public void taskEvent(TaskEvent event) {
			if (event instanceof TaskProgressEvent) {
				TaskProgressEvent progress = (TaskProgressEvent)event;
				setValue(percent(progress.getIteration(), progress.getTotalIterations()));
				setIndeterminate(false);
				setString(calcString());
			}
		}
	};

	public TaskProgressBar(Task task) {
		d_task = task;
		
		setStringPainted(true);
		setMinimum(0);
		setMaximum(100);
		setIndeterminate(true);
		setTitle(task.toString() + (task.isStarted() ? "" : " (" + WAITING_TEXT + ")"));
		setString(calcString());
		
		task.addTaskListener(new TaskListener() {
			public void taskEvent(TaskEvent event) {
				if (event instanceof TaskStartedEvent) {
					setTitle(event.getSource().toString());
					setString(calcString());
				}
				if (event instanceof TaskFinishedEvent) {
					setValue(100);
					setIndeterminate(false);
					setString(calcString());
				}
				if (event instanceof TaskProgressEvent) {
					TaskProgressEvent progress = (TaskProgressEvent)event;
					setValue(percent(progress.getIteration(), progress.getTotalIterations()));
					setIndeterminate(false);
					setString(calcString());
				}
				if (event instanceof PhaseStartedEvent) {
					PhaseStartedEvent phase = (PhaseStartedEvent)event;
					setTitle(event.getSource().toString() + " (" + phase.getPhase().toString() + ")");
					setIndeterminate(true);
					setString(calcString());
					phase.getPhase().addTaskListener(d_phaseListener);
				}
				if (event instanceof PhaseFinishedEvent) {
					PhaseFinishedEvent phase = (PhaseFinishedEvent)event;
					setTitle(event.getSource().toString());
					setIndeterminate(true);
					setString(calcString());
					phase.getPhase().removeTaskListener(d_phaseListener);
				}
			}
		});
	}
	
	private void setTitle(String title) {
		d_title = title;
	}
	
	public String calcString() {
		if (d_task.isFinished()) {
			return DONE_TEXT;
		}
		String string = d_title + (isIndeterminate() ? "" : ": " + getValue() + "%");
		return string;
	}
	
	private int percent(int value, int maximum) {
		return (value * 100) / maximum;
	}
}
