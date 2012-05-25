package org.drugis.common.threading;


public class ExtendableIterativeTask extends IterativeTask {
	private final String d_str;
	private final ExtendableIterativeComputation d_computation; 
	
	public ExtendableIterativeTask(IterativeComputation computation, String str) {
		super(computation, str);
		if(computation instanceof ExtendableIterativeComputation) { 
			d_computation = (ExtendableIterativeComputation) computation;
		} else {
			throw new IllegalArgumentException("Computation not an instanceof IterativeExtendableComputation");
		}
		d_str = str;
		((IterativeSuspendable)d_suspendable).setListenerManager(d_mgr);
	}
	
	public ExtendableIterativeTask(IterativeComputation computation) {
		this(computation, computation.toString());
	}
	
	public void extend(int iterations) { 
		d_finished = false;
		d_started = false;
		int oldIterations = d_computation.getTotalIterations();
		d_computation.setTotalIterations(oldIterations + iterations);
		d_mgr.fireTaskRestarted();
	}
	
	@Override
	public String toString() {
		return d_str;
	}
}
