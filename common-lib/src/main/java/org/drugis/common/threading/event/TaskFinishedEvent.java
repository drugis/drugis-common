package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskFinishedEvent extends TaskEvent {
	public TaskFinishedEvent(Task source) {
		super(source, EventType.TASK_FINISHED);
	}
}
