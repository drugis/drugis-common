package org.drugis.common.threading;

public abstract class AbstractExtendableIterativeComputation extends AbstractIterativeComputation implements IterativeComputation, IterativeExtendableComputation {

	public AbstractExtendableIterativeComputation(int totalIterations) {
		super(totalIterations);
	}
	
	public void reset() { 
		d_iteration = 0;
	}
	
	public void setTotalIterations(int it) {
		d_totalIterations = it;
	}	
}
