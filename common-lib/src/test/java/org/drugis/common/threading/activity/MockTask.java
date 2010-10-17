package org.drugis.common.threading.activity;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;

class MockTask implements Task {
	private boolean d_started = false;
	private boolean d_finished = false;
	private Throwable d_failure = null;
	private boolean d_aborted = false;

	public void addTaskListener(TaskListener l) {
	}

	public void removeTaskListener(TaskListener l) {
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
	}
	
	public void finish() {
		d_finished = true;
	}
	
	public void fail(Throwable cause) {
		d_failure = cause;
	}
	
	public void abort() {
		d_aborted = true;
	}
}