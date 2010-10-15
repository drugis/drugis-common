package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class TaskProgressEvent extends TaskEvent {
	private final int d_iter;
	private final int d_max;

	public TaskProgressEvent(Task source, int iter, int max) {
		super(source, EventType.TASK_PROGRESS);
		d_iter = iter;
		d_max = max;
	}

	public int getIteration() {
		return d_iter;
	}

	public int getTotalIterations() {
		return d_max;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			TaskProgressEvent other = (TaskProgressEvent) o;
			return other.d_iter == d_iter && other.d_max == d_max;
		}
		return false;
	}
}
