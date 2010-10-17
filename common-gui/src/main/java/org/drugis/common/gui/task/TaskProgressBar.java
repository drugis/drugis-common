package org.drugis.common.gui.task;

import javax.swing.JProgressBar;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.drugis.common.threading.event.TaskStartedEvent;

public class TaskProgressBar extends JProgressBar {
	private static final long serialVersionUID = -4003048586663833970L;

	public TaskProgressBar(Task task) {
		setString(task.toString() + (task.isStarted() ? "" : " (waiting)"));
		setStringPainted(true);
		setMinimum(0);
		setIndeterminate(true);
		
		task.addTaskListener(new TaskListener() {
			public void taskEvent(TaskEvent event) {
				if (event instanceof TaskStartedEvent) {
					setString(event.getSource().toString());
				}
				if (event instanceof TaskProgressEvent) {
					TaskProgressEvent progress = (TaskProgressEvent)event;
					setIndeterminate(false);
					setMaximum(progress.getTotalIterations());
					setValue(progress.getIteration());
				}
			}
		});
	}
}
