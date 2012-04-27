package org.drugis.common.threading;


public class IterativeTask extends SimpleSuspendableTask {
	private final String d_str;
	
	public IterativeTask(IterativeComputation computation, String str) {
		super(new IterativeSuspendable(computation));
		d_str = str;
		((IterativeSuspendable)d_suspendable).setListenerManager(d_mgr);
	}
	
	public IterativeTask(IterativeComputation computation) {
		this(computation, computation.toString());
	}
	
	public void setReportingInterval(int interval) {
		((IterativeSuspendable)d_suspendable).setReportingInterval(interval);
	}

	@Override
	public String toString() {
		return d_str;
	}
}
