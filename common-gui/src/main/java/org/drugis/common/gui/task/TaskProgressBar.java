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

	public TaskProgressBar(Task task) {
		d_task = task;
		setStringPainted(true);
		setMinimum(0);
		setIndeterminate(true);
		setTitle(task.toString() + (task.isStarted() ? "" : " (" + WAITING_TEXT + ")"));
		
		task.addTaskListener(new TaskListener() {
			private TaskListener d_phaseListener;

			public void taskEvent(TaskEvent event) {
				if (event instanceof TaskStartedEvent) {
					setTitle(event.getSource().toString());
				}
				if (event instanceof TaskFinishedEvent) {
					setIndeterminate(false);
					setMaximum(1);
					setValue(1);
				}
				if (event instanceof TaskProgressEvent) {
					TaskProgressEvent progress = (TaskProgressEvent)event;
					setIndeterminate(false);
					setMaximum(progress.getTotalIterations());
					setValue(progress.getIteration());
				}
				if (event instanceof PhaseStartedEvent) {
					PhaseStartedEvent phase = (PhaseStartedEvent)event;
					setTitle(event.getSource().toString() + " (" + phase.getPhase().toString() + ")");
					setIndeterminate(true);
					setMaximum(0);
					d_phaseListener = new TaskListener() {
						public void taskEvent(TaskEvent event) {
							if (event instanceof TaskProgressEvent) {
								TaskProgressEvent progress = (TaskProgressEvent)event;
								setIndeterminate(false);
								getModel().setMaximum(progress.getTotalIterations());
								getModel().setValue(progress.getIteration());
							}
						}
					};
					phase.getPhase().addTaskListener(d_phaseListener);
				}
				if (event instanceof PhaseFinishedEvent) {
					PhaseFinishedEvent phase = (PhaseFinishedEvent)event;
					setTitle(event.getSource().toString());
					setIndeterminate(true);
					setValue(0);
					phase.getPhase().removeTaskListener(d_phaseListener);
				}
			}
		});
	}
	
	private void setTitle(String title) {
		d_title = title;
	}
	
	@Override
	public String getString() {
		if (d_task.isFinished()) {
			return DONE_TEXT;
		}
		return d_title + (isIndeterminate() ? "" : ": " + percent(getValue(), getMaximum()) + "%");
	}
	
	private int percent(int value, int maximum) {
		return (value * 100) / maximum;
	}

	@Override
	public void setString(String str) {	}
}
