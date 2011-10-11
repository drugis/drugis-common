package org.drugis.common.threading;

/**
 * Wraps a non-suspendable runnable in a Suspendable.
 */
public class NonSuspendable implements Suspendable {
	private final Runnable d_runnable;

	public NonSuspendable(Runnable runnable) {
		this.d_runnable = runnable;
	}

	public void run() {
		d_runnable.run();
	}

	public boolean isSuspended() {
		return false;
	}

	public boolean suspend() {
		return false;
	}

	public boolean wakeUp() {
		return false;
	}

	public boolean abort() {
		return false;
	}

	public boolean isAborted() {
		return false;  // cannot be terminated by definition
	}
}
