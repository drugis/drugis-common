package org.drugis.common.threading.activity;

import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;

/**
 * A ForkTransition initiates parallel activities.
 */
public class ForkTransition implements Transition {

	private final Task d_source;
	private final List<? extends Task> d_target;

	public ForkTransition(Task source, List<? extends Task> target) {
		d_source = source;
		d_target = target;
	}

	public List<Task> getSources() {
		return Collections.singletonList(d_source);
	}

	public List<Task> getTargets() {
		return Collections.unmodifiableList(d_target);
	}

	public boolean isReady() {
		return d_source.isFinished();
	}

	public List<Task> transition() {
		if (!isReady()) {
			throw new RuntimeException("Not ready for transition.");
		}
		return getTargets();
	}

}
