package org.drugis.common.threading;


public class RestartableIterativeTask extends IterativeTask {
	private final String d_str;
	private final IterativeComputation d_computation; 
	
	public RestartableIterativeTask(IterativeComputation computation, String str) {
		super(computation, str);
		d_computation = computation;
		d_str = str;
		((IterativeSuspendable)d_suspendable).setListenerManager(d_mgr);
	}
	
	public RestartableIterativeTask(IterativeComputation computation) {
		this(computation, computation.toString());
	}
	
	public void restart() {
		d_finished = false;
		d_computation.reset();
		d_mgr.fireTaskRestarted();
	}
	
	@Override
	public String toString() {
		return d_str;
	}
}
