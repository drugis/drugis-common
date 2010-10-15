package org.drugis.common.threading;

import org.drugis.common.threading.event.TaskEvent;

public interface TaskListener {
	public void taskEvent(TaskEvent event);
}
