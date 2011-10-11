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
	 * @return false if not suspendable.
	 */
	public boolean suspend();

	/**
	 * Request the run to be taken out of suspension.
	 * @return false if not suspendable.
	 */
	public boolean wakeUp();

	/**
	 * Request the run to be terminated.
	 * @return false if not suspendable.
	 */
	public boolean abort();
	
	/**
	 * @return whether the run is aborted or not.
	 */
	public boolean isAborted();
}
