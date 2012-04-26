package org.drugis.common.threading;

public abstract class AbstractExtendableIterativeComputation extends AbstractIterativeComputation implements IterativeComputation, ExtendableIterativeComputation {
	
	private int d_amountExtended = 0;
	
	public AbstractExtendableIterativeComputation(int totalIterations) {
		super(totalIterations);
	}
	
	
	public void setTotalIterations(int it) {
		d_amountExtended = d_amountExtended + it;
		d_totalIterations = it;	
	}
	
	public int getAmountExtended() { 
		return d_amountExtended;
	}
}
