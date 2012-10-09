package org.drugis.common.threading.status;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.event.TaskProgressEvent;

public abstract class AbstractProgressModel extends AbstractObservable implements TextProgressModel {


	public static final String DONE_TEXT = "Done!";
	public static final String WAITING_TEXT = "waiting";
	public static final String FAILED_TEXT = "failed";
	public static final String ABORTED_TEXT = "Aborted";

	protected Task d_task;
	protected Double d_progress;
	protected boolean d_determinate;

	protected abstract Double calcProgress();
	protected abstract boolean calcDeterminate();

	protected double calcProgress(TaskProgressEvent evt) {
		return (double) evt.getIteration() / evt.getTotalIterations();
	}

	public boolean getDeterminate() {
		return d_determinate;
	}

	public Double getProgress() {
		return d_progress;
	}

	String formatProgress(Double progress) {
		if(progress == null) return "?";
		Long round = (Long)Math.round(100.0 * progress);
		return round.toString() + "%";
	}


	public String getText() {
		String taskName = d_task.toString();

		List<String> phaseStrings = getPhaseStrings();

		String phaseString = " (" + StringUtils.join(phaseStrings, ", ") + ")";

		if (!d_task.isStarted()) {
			return taskName + " (" + WAITING_TEXT + ")";
		}
		if (d_task.isFinished()) {
			return DONE_TEXT;
		}
		if (d_task.isFailed()) {
			return taskName + " "+FAILED_TEXT +": " + d_task.getFailureCause().getMessage();
		}
		if (d_task.isAborted()) {
			return taskName + ": " + ABORTED_TEXT;
		}
		if (!phaseStrings.isEmpty()) {
			return taskName + phaseString;
		}
		return taskName + ": " + formatProgress(getDeterminate() ? d_progress : null);
	}

	abstract protected List<String> getPhaseStrings();


	protected void setProgress(Double progress) {
		Double oldValue = d_progress;
		d_progress = progress;
		firePropertyChange(PROPERTY_PROGRESS, oldValue, d_progress);
		firePropertyChange(PROPERTY_TEXT, null, getText());
	}

	protected void setDeterminate(boolean determinate) {
		boolean oldValue = d_determinate;
		d_determinate = determinate;
		firePropertyChange(PROPERTY_DETERMINATE, oldValue, d_determinate);
	}

}