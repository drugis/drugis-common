package org.drugis.common.gui.task;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.status.AbstractProgressModel;
import org.drugis.common.threading.status.TaskProgressModel;

@SuppressWarnings("serial")
public class TaskProgressBar extends TextProgressBar {
	public TaskProgressBar(AbstractProgressModel model) {
		super(model);
	}

	public TaskProgressBar(Task task) {
		super(new TaskProgressModel(task));
	}
}
