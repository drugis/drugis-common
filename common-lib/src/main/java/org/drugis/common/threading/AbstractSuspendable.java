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
	
	public synchronized void suspend() {
		d_threadSuspended = true;
	}
	
	public synchronized void wakeUp() {
		d_threadSuspended = false;
		notify();
	}
	
	public void terminate() {
		d_threadTerminated = true;
		wakeUp();
	}

	/**
	 * @return true if termination has been requested.
	 */
	protected boolean isTerminated() {
		return d_threadTerminated;
	}
	
	protected void waitIfSuspended() throws TerminatedException {
		while(isSuspended()) {
			synchronized(this) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}

		if (isTerminated()) {
			throw new TerminatedException();
		}
	}
}
