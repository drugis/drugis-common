package org.drugis.common.threading.activity;

import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;

/**
 * A JoinTransition ends parallel processing (all source activities must terminate before it can be evaluated). 
 */
public class JoinTransition implements Transition {

	private final List<? extends Task> d_source;
	private final Task d_target;

	public JoinTransition(List<? extends Task> source, Task target) {
		d_source = source;
		d_target = target;
	}

	public List<Task> getSources() {
		return Collections.unmodifiableList(d_source);
	}

	public List<Task> getTargets() {
		return Collections.singletonList(d_target);
	}

	public boolean isReady() {
		for (Task t : d_source) {
			if (!t.isFinished()) {
				return false;
			}
		}
		return true;
	}

	public List<Task> transition() {
		if (!isReady()) {
			throw new RuntimeException("Not ready for transition.");
		}
		return getTargets();
	}
}
