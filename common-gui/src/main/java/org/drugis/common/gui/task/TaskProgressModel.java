package org.drugis.common.gui.task;

public class TaskProgressModel {
	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_DETERMINATE = "determinate";
	public static final String PROPERTY_PROGRESS = "progress";

	/**
	 * Progress in [0, 1], or null if not determinate.
	 */
	public Double getProgress() {
		return null; 
	}
	
	/**
	 * Get whether the progress is determinate (defined in [0, 1]).
	 */
	public boolean getDeterminate() {
		return false;
	}
	
	/**
	 * Get the text to display.
	 */
	public String getText() {
		return "";
	}
}
