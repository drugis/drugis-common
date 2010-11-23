package org.drugis.common.gui.task;

import org.drugis.common.gui.TextProgressBar;
import org.drugis.common.threading.Task;

@SuppressWarnings("serial")
public class TaskProgressBar extends TextProgressBar {
	public TaskProgressBar(Task task) {
		super(new TaskProgressModel(task));
	}	
}
