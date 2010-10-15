package org.drugis.common.threading;

/**
 * Runnable that can suspend and terminate itself.
 */
public interface Suspendable extends Runnable {
	/**
	 * @return true if suspension has been requested.
	 */
	public boolean isSuspended();

	/**
	 * Request the run to be suspended.
	 */
	public void suspend();

	/**
	 * Request the run to be taken out of suspension.
	 */
	public void wakeUp();

	/**
	 * Request the run to be terminated.
	 */
	public void abort();
}
