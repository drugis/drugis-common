package org.drugis.common.threading.status;

import com.jgoodies.binding.beans.Observable;

public interface TextProgressModel extends Observable {
	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_DETERMINATE = "determinate";
	public static final String PROPERTY_PROGRESS = "progress";

	/**
	 * Progress in [0, 1], or null if not determinate.
	 */
	public Double getProgress();
	
	/**
	 * Get whether the progress is determinate (defined in [0, 1]).
	 */
	public boolean getDeterminate();
	
	/**
	 * Get the text to display.
	 */
	public String getText();
}
