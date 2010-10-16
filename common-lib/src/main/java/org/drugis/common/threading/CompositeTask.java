package org.drugis.common.threading;

import java.util.List;

public interface CompositeTask extends Task {
	/**
	 * Get the tasks that can now be run, but are not finished yet. This may include currently running tasks.
	 * @return A list of SimpleTasks that are ready to be run.
	 */
	public List<SimpleTask> getNextTasks();
	
	/**
	 * Indicate that execution has started.
	 */
	public void start();
}
