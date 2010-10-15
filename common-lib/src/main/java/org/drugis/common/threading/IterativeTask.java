package org.drugis.common.threading;

import org.drugis.common.threading.event.ListenerManager;

public class IterativeTask extends SimpleSuspendableTask {
	static class IterativeSuspendable extends AbstractSuspendable {
		private ListenerManager d_mgr;
		private final IterativeComputation d_computation;
		private int d_interval = 0;

		public IterativeSuspendable(IterativeComputation computation) {
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

		public void setListenerManager(ListenerManager mgr) {
			d_mgr = mgr;
			
		}

		public void setReportingInterval(int interval) {
			d_interval  = interval;
		}
	}
	
	public IterativeTask(IterativeComputation computation) {
		super(new IterativeSuspendable(computation));
		((IterativeSuspendable)d_suspendable).setListenerManager(d_mgr);
	}
	
	public void setReportingInterval(int interval) {
		((IterativeSuspendable)d_suspendable).setReportingInterval(interval);
	}

}
