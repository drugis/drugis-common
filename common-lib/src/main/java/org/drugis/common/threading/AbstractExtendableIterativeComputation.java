package org.drugis.common.threading;

public abstract class AbstractExtendableIterativeComputation extends AbstractIterativeComputation implements IterativeComputation, ExtendableIterativeComputation {
	public AbstractExtendableIterativeComputation(int totalIterations) {
		super(totalIterations);
	}
	
	public void setTotalIterations(int it) {
		d_totalIterations = it;	
	}
}
