package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskRestartedEvent extends TaskEvent {
	public TaskRestartedEvent(Task source) {
		super(source, EventType.TASK_RESTARTED);
	}
}
