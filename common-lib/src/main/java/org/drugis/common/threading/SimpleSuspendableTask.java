package org.drugis.common.threading;

import org.drugis.common.threading.event.ListenerManager;

public class SimpleSuspendableTask implements SimpleTask {
	protected ListenerManager d_mgr;
	protected final Suspendable d_suspendable;

	private boolean d_started = false;
	private boolean d_finished = false;
	private boolean d_aborted = false;
	private Throwable d_failure;

	public SimpleSuspendableTask(Suspendable suspendable) {
		d_suspendable = suspendable;
		d_mgr = new ListenerManager(this);
	}

	public void addTaskListener(TaskListener l) {
		d_mgr.addTaskListener(l);
	}

	public void removeTaskListener(TaskListener l) {
		d_mgr.removeTaskListener(l);
	}

	public void run() {
		d_started = true;
		d_mgr.fireTaskStarted();
		
		try {
			d_suspendable.run();
		} catch (AbortedException e) {
			d_aborted = true;
			d_mgr.fireTaskAborted();
			return;
		} catch (Throwable e) {
			d_failure = e;
			d_mgr.fireTaskFailed(e);
			return;
		}
		
		d_finished = true;
		d_mgr.fireTaskFinished();
	}

	public boolean isStarted() {
		return d_started;
	}

	public boolean isFinished() {
		return d_finished;
	}

	public boolean isFailed() {
		return d_failure != null;
	}

	public Throwable getFailureCause() {
		return d_failure;
	}

	public boolean isAborted() {
		return d_aborted ;
	}

	public boolean isSuspended() {
		return d_suspendable.isSuspended();
	}

	public void suspend() {
		d_suspendable.suspend();
	}

	public void wakeUp() {
		d_suspendable.wakeUp();
	}

	public void abort() {
		d_suspendable.abort();		
	}

}
