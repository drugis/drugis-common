package org.drugis.common.threading;


/**
 * Helper implementation of Suspendable.
 */
public abstract class AbstractSuspendable implements Suspendable  {

	boolean d_threadSuspended = false;
	boolean d_threadTerminated = false;
	
	public synchronized boolean isSuspended() {
		return d_threadSuspended;
	}
	
	public synchronized boolean isAborted() {
		return d_threadTerminated;
	}
	
	public synchronized boolean suspend() {
		if (isAborted()) {
			throw new IllegalStateException("Attempt to suspend an aborted task");
		}
		d_threadSuspended = true;
		return true;
	}
	
	public synchronized boolean wakeUp() {
		d_threadSuspended = false;
		notify();
		return true;
	}
	
	public boolean abort() {
		d_threadTerminated = true;
		wakeUp();
		return true;
	}

	/**
	 * @return true if termination has been requested.
	 */
	protected boolean isTerminated() {
		return d_threadTerminated;
	}
	
	protected void waitIfSuspended() throws AbortedException {
		while(isSuspended()) {
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if (isTerminated()) {
			throw new AbortedException();
		}
	}
}
