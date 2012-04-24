package org.drugis.common.threading;

public interface IterativeExtendableComputation extends IterativeComputation {
	
	/**
	 * Resets the internal iteration counter to 0
	 */
	public void reset();
	
	/**
	 * Sets the total amount of iterations 
	 * @param it the amount of total iterations to be set
	 */
	public void setTotalIterations(int it);

}
