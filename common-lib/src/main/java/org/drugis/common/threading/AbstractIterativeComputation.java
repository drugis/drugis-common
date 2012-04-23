package org.drugis.common.threading;

public abstract class AbstractIterativeComputation implements IterativeComputation {
	private final int d_totalIterations;
	private int d_iteration;

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
	
	public abstract void doStep();
}
