package org.drugis.common.threading.event;

import java.util.LinkedList;
import java.util.List;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;

/**
 * Utility class to implement TaskListener.
 */
public class ListenerManager {
	private final Task d_source;
	private List<TaskListener> d_listeners = new LinkedList<TaskListener>();

	public ListenerManager(Task source) {
		d_source = source;
	}
	
	public void addTaskListener(TaskListener l) {
		d_listeners.add(l);
	}
	
	public void removeTaskListener(TaskListener l) {
		d_listeners.remove(l);
	}
	
	public void fireEvent(TaskEvent event) {
		TaskListener[] listeners = d_listeners.toArray(new TaskListener[] {});
		for (TaskListener l : listeners) {
			l.taskEvent(event);
		}
	}
	
	public void fireTaskStarted() {
		fireEvent(new TaskStartedEvent(d_source));
	}
	
	public void fireTaskFinished() {
		fireEvent(new TaskFinishedEvent(d_source));
	}

	public void fireTaskAborted() {
		fireEvent(new TaskAbortedEvent(d_source));
	}

	public void fireTaskFailed(Throwable cause) {
		fireEvent(new TaskFailedEvent(d_source, cause));
	}

	public void fireTaskProgress(int iter, int max) {
		fireEvent(new TaskProgressEvent(d_source, iter, max));
	}
	
	public void firePhaseStarted(Task phase) {
		fireEvent(new PhaseStartedEvent(d_source, phase));
	}

	public void firePhaseFinished(Task phase) {
		fireEvent(new PhaseFinishedEvent(d_source, phase));
	}	
}
