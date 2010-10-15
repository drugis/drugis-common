package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskFailedEvent extends TaskEvent {
	private final Throwable d_cause;

	public TaskFailedEvent(Task source, Throwable cause) {
		super(source, EventType.TASK_FAILED);
		d_cause = cause;
	}

	public Throwable getCause() {
		return d_cause;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			TaskFailedEvent other = (TaskFailedEvent) o;
			return other.d_cause == d_cause;
		}
		return false;
	}
}
