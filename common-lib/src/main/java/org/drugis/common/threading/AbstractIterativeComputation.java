package org.drugis.common.threading;

public abstract class AbstractIterativeComputation implements IterativeComputation {
	protected int d_totalIterations;
	protected int d_iteration;

	public AbstractIterativeComputation(int totalIterations) {
		d_totalIterations = totalIterations;
		d_iteration = 0;
	}

	public void finish() {
	}

	public int getIteration() {
		return d_iteration;
	}

	public int getTotalIterations() {
		return d_totalIterations;
	}

	public void initialize() {
	}

	public void step() {
		doStep();
		++d_iteration;
	}
	
	public void reset() { 
		d_iteration = 0;
	}
	
	public int getProgressIteration() {
		return getIteration();
	}
	
	public abstract void doStep();
}
