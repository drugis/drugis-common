package org.drugis.common.threading;

import org.drugis.common.threading.event.ListenerManager;

class IterativeSuspendable extends AbstractSuspendable {
	private ListenerManager d_mgr;
	private final IterativeComputation d_computation;
	private int d_interval = 0;

	public IterativeSuspendable(final IterativeComputation computation) {
		d_computation = computation;
	}

	public void run() {
		d_computation.initialize();
		waitIfSuspended();
		while (d_computation.getIteration() < d_computation.getTotalIterations()) {				
			if (d_interval > 0 && d_computation.getIteration() % d_interval == 0) {
				fireProgress();
			}
			d_computation.step();
			waitIfSuspended();
		}
		if (d_interval > 0) {
			fireProgress();
		}
		d_computation.finish();
	}
	
	private void fireProgress() {
		d_mgr.fireTaskProgress(d_computation.getIteration(), d_computation.getTotalIterations());
	}

	public void setListenerManager(final ListenerManager mgr) {
		d_mgr = mgr;
		
	}
	public void setReportingInterval(final int interval) {
		d_interval  = interval;
	}
}
