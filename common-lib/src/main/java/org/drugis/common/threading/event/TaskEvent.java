package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

/**
 * TaskEvent is used to notify listeners of Task status.
 * If a Task notifies progress, it should send a TaskProgressEvent(0, max) just after the TaskStartedEvent.
 * Similarly, it should send a TaskProgressEvent(max, max) just before the TaskFinishedEvent. 
 */
public class TaskEvent {
	private final Task d_source;
	private final EventType d_type;
	public enum EventType {
		TASK_STARTED,
		TASK_FINISHED,
		TASK_PROGRESS,
		PHASE_STARTED,
		PHASE_FINISHED,
		TASK_ABORTED,
		TASK_FAILED
	}

	protected TaskEvent(Task source, EventType type) {
		d_source = source;
		d_type = type;
	}

	public Task getSource() {
		return d_source;
	}

	public EventType getType() {
		return d_type;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TaskEvent) {
			TaskEvent other = (TaskEvent)o;
			return other.d_type == d_type && other.d_source == d_source;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return EventType.TASK_STARTED.hashCode() * 31 + getSource().hashCode();
	}
}
