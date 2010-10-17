package org.drugis.common.threading.activity;

import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;

/**
 * A DirectTransition simply moves from state A to B. 
 */
public class DirectTransition implements Transition {

	private final Task d_source;
	private final Task d_target;

	public DirectTransition(Task source, Task target) {
		d_source = source;
		d_target = target;
	}

	public List<Task> getSources() {
		return Collections.singletonList(d_source);
	}

	public List<Task> getTargets() {
		return Collections.singletonList(d_target);
	}

	public boolean isReady() {
		return d_source.isFinished();
	}

	public List<Task> transition() {
		if (!isReady()) {
			throw new RuntimeException("Not ready for transition.");
		}
		return Collections.singletonList(d_target);
	}

}
