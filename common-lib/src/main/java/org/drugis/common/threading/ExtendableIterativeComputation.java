package org.drugis.common.threading;

public interface ExtendableIterativeComputation extends IterativeComputation {
	
	/**
	 * Sets the total amount of iterations 
	 * @param it the amount of total iterations to be set
	 */
	public void setTotalIterations(int it);
	
	/**
	 * @return the amount of iterations this computation has been extendend with
	 */
	public int getAmountExtended();
}
