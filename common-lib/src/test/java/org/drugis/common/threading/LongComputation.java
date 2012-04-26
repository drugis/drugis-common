package org.drugis.common.threading;

class LongComputation implements IterativeComputation {
	private final int d_max;
	private int d_step;
	public LongComputation(int max) {
		d_max = max;
		d_step = 0;
	}
	public void initialize() {}
	public void step() { 
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		} 
		++d_step;
	}
	public void finish() {}
	public int getIteration() { return d_step; }
	public int getTotalIterations() { return d_max; }
}