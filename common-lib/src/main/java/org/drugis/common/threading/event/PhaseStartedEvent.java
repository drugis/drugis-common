package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class PhaseStartedEvent extends PhaseEvent {
	public PhaseStartedEvent(Task source, Task phase) {
		super(source, EventType.PHASE_STARTED, phase);
	}
}
