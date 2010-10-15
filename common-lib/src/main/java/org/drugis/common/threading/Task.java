package org.drugis.common.threading;

/**
 * A Task is an executable object that is scheduled by the TaskHandler.
 * Tasks can be either simple (a Suspendable that can be listened to) or more complex, with a state-machine like structure. 
 */
public interface Task {
	/**
	 * Subscribe l to receive status updates of this Task.
	 */
	public void addTaskListener(TaskListener l);
	/**
	 * Unsubscribe l to receive status updates of this Task.
	 */
	public void removeTaskListener(TaskListener l);
	/**
	 * Whether the task has been started.
	 */
	public boolean isStarted();
	/**
	 * Whether the task has finished.
	 */
	public boolean isFinished();
	/**
	 * Whether the task has failed (due to an internal error).
	 */
	public boolean isFailed();
	/**
	 * Get the reason for failure.
	 */
	public Throwable getFailureCause();
	/**
	 * Whether the task has been aborted (abort is initiated by a client).
	 */
	public boolean isAborted();
}
