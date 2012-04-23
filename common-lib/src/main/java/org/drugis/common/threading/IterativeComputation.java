package org.drugis.common.threading;

/**
 * Defines an iterative computation. 
 */
public interface IterativeComputation {
	
	/**
	 * Perform initialization.
	 */
	public void initialize();
	/**
	 * Do one iteration. Should increase getIteration() by one.
	 */
	public void step();
	/**
	 * Perform finalization.
	 */
	public void finish();
	/**
	 * Return the current iteration.
	 */
	public int getIteration();
	/**
	 * Defines the desired number of iterations.
	 */
	public int getTotalIterations();
	
	/**
	 * Resets the internal iteration counter to 0
	 */
	public void reset();
}
