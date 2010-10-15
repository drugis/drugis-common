package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskAbortedEvent extends TaskEvent {
	public TaskAbortedEvent(Task source) {
		super(source, EventType.TASK_ABORTED);
	}
}
