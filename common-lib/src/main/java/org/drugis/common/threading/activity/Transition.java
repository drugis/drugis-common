package org.drugis.common.threading.activity;

import java.util.List;

import org.drugis.common.threading.Task;

/**
 * A Transition in the ActivityModel. 
 */
public interface Transition {
	/**
	 * The states that flow into this transition.
	 */
	public List<Task> getSources();
	
	/**
	 * The states that flow out of this transition.
	 */
	public List<Task> getTargets();
	
	/**
	 * @return true if the Transition is ready to occur.
	 */
	public boolean isReady();
	
	/**
	 * The states that are transitioned to.
	 */
	public List<Task> transition();
}