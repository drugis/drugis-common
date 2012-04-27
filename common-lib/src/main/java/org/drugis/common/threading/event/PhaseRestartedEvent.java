package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class PhaseRestartedEvent extends PhaseEvent {
	public PhaseRestartedEvent(Task source, Task phase) {
		super(source, EventType.PHASE_RESTARTED, phase);
	}
}
