package org.drugis.common.threading;

public abstract class AbstractExtendableIterativeComputation extends AbstractIterativeComputation implements IterativeComputation, IterativeExtendableComputation {
	
	private int d_progressIteratrion = 0;
	
	public AbstractExtendableIterativeComputation(int totalIterations) {
		super(totalIterations);
	}
	
	public void reset() { 
		d_iteration = 0;
		setProgressIteration(0);
	}
	
	public void setTotalIterations(int it) {
		setProgressIteration(d_totalIterations);
		d_totalIterations = it;	
	}
	
	public int getProgressIteration() {
		return d_iteration - getTotalProgressIteration();
	}

	public int getTotalProgressIteration() {
		return d_progressIteratrion;
	}

	private void setProgressIteration(int progressIteratrion) {
		d_progressIteratrion = progressIteratrion;
	}
	
}
