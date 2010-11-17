package org.drugis.common.gui.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private List<Task> d_phases;
	private HashMap<Task, Integer> d_tasks;

	public TaskProgressBar(Task task) {
		d_task = task;
		
		setStringPainted(true);
		setMinimum(0);
		setMaximum(100);
		setIndeterminate(true);
		setTitle(task.toString() + ( task.isStarted() ? ")" : " (" + WAITING_TEXT ));
		setString(calcString());
		
		d_phases = new ArrayList<Task>();
		d_tasks = new HashMap<Task, Integer>();
		
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
					d_phases.add(((PhaseStartedEvent)event).getPhase());
					
					String title = event.getSource().toString() + " (";
					int c=0;
					
					for(Task t : d_phases) {
						++c;
						title += (c > 1 ? ", " : "") + t.toString();
						setIndeterminate(true);
						t.addTaskListener(d_phaseListener);
					}
					
					/*
					d_tasks.put(((PhaseStartedEvent)event).getPhase(), new Integer(getValue()));
					for (Entry<Task, Integer> entry : d_tasks.entrySet()) {
					    Task key = entry.getKey();
					    Object value = entry.getValue();
					    ++c;
						title += (c > 1 ? ", " : "") + key.toString();
						setIndeterminate(true);
						key.addTaskListener(d_phaseListener);
					}
					*/
					setTitle(title);
					setString(calcString());
				}
				if (event instanceof PhaseFinishedEvent) {				
					String title = event.getSource().toString() + " (";
					
					for( Task t : d_phases ) {
						if(t.equals(((PhaseFinishedEvent) event).getPhase())) {
							title += t.toString() + ": " + DONE_TEXT;
							setIndeterminate(true);
							t.removeTaskListener(d_phaseListener);
						}
					}
					
					if(d_phases.contains(((PhaseFinishedEvent) event).getPhase())) { 
						d_phases.remove(((PhaseFinishedEvent) event).getPhase());
					}
					
					for( Task t : d_phases) {
						title += " " + t.toString();
					}
					setTitle(title);
					setString(calcString());
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
		String string = d_title + (isIndeterminate() ? ")" : ": " + getValue() + "%)");
		return string;
	}
	
	private int percent(int value, int maximum) {
		return (value * 100) / maximum;
	}
}
