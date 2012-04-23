package org.drugis.common.threading;

class ShortComputation implements IterativeComputation {
	private final int d_max;
	private int d_step;
	public ShortComputation(int max) {
		d_max = max;
		d_step = 0;
	}
	public void initialize() {}
	public void step() { ++d_step; }
	public void finish() {}
	public int getIteration() { return d_step; }
	public int getTotalIterations() { return d_max; }
	public void reset() {}
}