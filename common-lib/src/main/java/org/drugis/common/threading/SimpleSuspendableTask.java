package org.drugis.common.threading;

import org.drugis.common.threading.event.ListenerManager;

public class SimpleSuspendableTask implements SimpleTask {
	protected ListenerManager d_mgr;
	protected final Suspendable d_suspendable;

	protected boolean d_started = false;
	protected boolean d_finished = false;
	private Throwable d_failure;
	private String d_str;

	public SimpleSuspendableTask(Suspendable suspendable, String str) {
		d_suspendable = suspendable;
		d_str = str;
		d_mgr = new ListenerManager(this);
	}
	
	public SimpleSuspendableTask(Runnable runnable, String str) {
		this(wrap(runnable), str);
	}
	
	public SimpleSuspendableTask(Runnable runnable) {
		this(wrap(runnable), runnable.toString());
	}
	
	public SimpleSuspendableTask(Suspendable suspendable) {
		this(suspendable, suspendable.toString());
	}

	private static Suspendable wrap(Runnable runnable) {
		if (runnable instanceof Suspendable) {
			return (Suspendable)runnable;
		}
		return new NonSuspendable(runnable);
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
		return d_suspendable.isAborted();
	}

	public boolean isSuspended() {
		return d_suspendable.isSuspended();
	}

	public boolean suspend() {
		return d_suspendable.suspend();
	}

	public boolean wakeUp() {
		return d_suspendable.wakeUp();
	}

	public boolean abort() {
		return d_suspendable.abort();		
	}

	@Override
	public String toString() {
		return d_str;
	}
}
