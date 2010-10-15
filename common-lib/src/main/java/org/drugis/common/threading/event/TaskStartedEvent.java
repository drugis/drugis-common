package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskStartedEvent extends TaskEvent {
	public TaskStartedEvent(Task source) {
		super(source, EventType.TASK_STARTED);
	}
}
