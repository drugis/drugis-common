package org.drugis.common.gui.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.JProgressBar;

import org.drugis.common.threading.IterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.junit.Ignore;
import org.junit.Test;

public class TaskProgressBarTest {
	static class ShortComputation implements IterativeComputation {
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
	}
	
	@Test
	public void testSimpleProgress() {
		IterativeTask task = new IterativeTask(new ShortComputation(10000));
		task.setReportingInterval(10);
		
		JProgressBar bar = new TaskProgressBar(task);
		assertEquals(task.toString() + " (waiting)", bar.getString());
		assertTrue(bar.isIndeterminate());
		
		task.run();
		assertEquals(task.toString(), bar.getString());
		assertFalse(bar.isIndeterminate());
		assertEquals(10000, bar.getMaximum());
		assertEquals(10000, bar.getValue());
	}
	
	@Test @Ignore
	public void testSimpleProgressEvents() {
		fail();
	}
	
	@Test @Ignore
	public void testPhasedProgress() {
		fail();
	}

	@Test @Ignore
	public void testParallelProgress() {
		fail();
	}
}
