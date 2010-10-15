package org.drugis.common.threading.event;

import org.drugis.common.threading.Task;

final public class PhaseFinishedEvent extends PhaseEvent {
	public PhaseFinishedEvent(Task source, Task phase) {
		super(source, EventType.PHASE_FINISHED, phase);
	}
}
