package org.drugis.common.threading.activity;

import org.drugis.common.threading.SimpleTask;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.ListenerManager;

public class MockTask implements SimpleTask {
	private boolean d_started = false;
	private boolean d_finished = false;
	private Throwable d_failure = null;
	private boolean d_aborted = false;
	ListenerManager d_mgr = new ListenerManager(this);

	public void addTaskListener(TaskListener l) {
		d_mgr.addTaskListener(l);
	}

	public void removeTaskListener(TaskListener l) {
		d_mgr.removeTaskListener(l);
	}

	public boolean isStarted() {
		return d_started;
	}

	public boolean isFinished() {
		return d_finished ;
	}

	public boolean isFailed() {
		return d_failure  != null;
	}

	public Throwable getFailureCause() {
		return d_failure;
	}

	public boolean isAborted() {
		return d_aborted  ;
	}
	
	public void start() {
		d_started = true;
		d_mgr.fireTaskStarted();
	}
	
	public void finish() {
		d_finished = true;
		d_mgr.fireTaskFinished();
	}
	
	public void fail(Throwable cause) {
		d_failure = cause;
		d_mgr.fireTaskFailed(cause);
	}
	
	public boolean abort() {
		d_aborted = true;
		d_mgr.fireTaskAborted();
		return true;
	}
	
	public void progress(int iteration, int max) {
		d_mgr.fireTaskProgress(iteration, max);
	}

	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean suspend() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean wakeUp() {
		// TODO Auto-generated method stub
		return false;
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}