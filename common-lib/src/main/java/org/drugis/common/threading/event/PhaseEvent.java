package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

public class PhaseEvent extends TaskEvent {
	private final Task d_phase;

	protected PhaseEvent(Task source, EventType type, Task phase) {
		super(source, type);
		d_phase = phase;
	}

	public Task getPhase() {
		return d_phase;
	}
	
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			PhaseEvent other = (PhaseEvent) o;
			return other.d_phase == d_phase;
		}
		return false;
	}
}
