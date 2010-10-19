package org.drugis.common.gui.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.drugis.common.threading.CompositeTask;
import org.drugis.common.threading.IterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.SimpleTask;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.ListenerManager;
import org.junit.Ignore;
import org.junit.Test;

public class TaskProgressBarTest {
	private final class MockComposite implements CompositeTask {
		private boolean d_started = false;
		public ListenerManager d_mgr = new ListenerManager(this);
		private boolean d_finished = false;
		private String d_str = null;
		
		public MockComposite() { }
		public MockComposite(String str) { d_str = str; }
		@Override public String toString() { return d_str != null ? d_str : super.toString(); }

		public void addTaskListener(TaskListener l) { d_mgr.addTaskListener(l); }
		public void removeTaskListener(TaskListener l) { d_mgr.removeTaskListener(l); }

		public boolean isStarted() { return d_started; }
		public void start() { d_started = true; d_mgr.fireTaskStarted(); }

		public boolean isFinished() { return d_finished ; }
		public void finish() { d_finished = true; d_mgr.fireTaskFinished(); }

		// NI
		public boolean isFailed() { return false; }
		public boolean isAborted() { return false; }
		public Throwable getFailureCause() { return null; }
		public List<SimpleTask> getNextTasks() { return null; }
	}

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
		assertEquals(TaskProgressBar.DONE_TEXT, bar.getString());
		assertFalse(bar.isIndeterminate());
		assertEquals(1, bar.getMaximum());
		assertEquals(1, bar.getValue());
	}

	@Test
	public void testSimpleProgressEvents() {
		final IterativeTask task = new IterativeTask(new ShortComputation(10000));
		task.setReportingInterval(2000);
		
		final JProgressBar bar = new TaskProgressBar(task);
		final List<Integer> events = new ArrayList<Integer>();
		final List<String> strings = new ArrayList<String>();
		bar.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				BoundedRangeModel model = (BoundedRangeModel)e.getSource();
				events.add(model.getValue());
				strings.add(bar.getString());
			}
		});
		
		task.run();
		Integer[] expected = new Integer[] {0, 2000, 4000, 6000, 8000, 10000, 1};
		assertEquals(Arrays.asList(expected), events);
		
		String[] expectedStr = new String[] {
			task.toString() + ": 0%",
			task.toString() + ": 20%",
			task.toString() + ": 40%",
			task.toString() + ": 60%",
			task.toString() + ": 80%",
			task.toString() + ": 100%",
			TaskProgressBar.DONE_TEXT
		};
		assertEquals(Arrays.asList(expectedStr), strings);
	}
	
	@Test
	public void testPhasedProgress() {
		MockComposite ctask = new MockComposite();
		IterativeTask phase1 = new IterativeTask(new ShortComputation(10000));
		IterativeTask phase2 = new IterativeTask(new ShortComputation(10000));
		phase1.setReportingInterval(3000);
		
		final JProgressBar bar = new TaskProgressBar(ctask);
		
		assertEquals(ctask.toString() + " (waiting)", bar.getString());
		ctask.start();
		assertEquals(ctask.toString(), bar.getString());
		final List<String> strings = new ArrayList<String>();
		bar.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (strings.size() < 1 || !strings.get(strings.size() - 1).equals(bar.getString())) {
					strings.add(bar.getString());
				}
			}
		});
		
		ctask.d_mgr.firePhaseStarted(phase1);
		phase1.run();
		String title = ctask.toString() + " (" + phase1.toString() + ")";
		String[] expected = new String[] {
				title,
				title + ": 0%",
				title + ": 30%",
				title + ": 60%",
				title + ": 90%",
				title + ": 100%",
				ctask.toString()
		};
		ctask.d_mgr.firePhaseFinished(phase1);
		assertEquals(Arrays.asList(expected), strings);
		
		ctask.d_mgr.firePhaseStarted(phase1);
		assertEquals(true, bar.isIndeterminate());
		ctask.d_mgr.firePhaseFinished(phase2);
		ctask.finish();
		assertEquals(TaskProgressBar.DONE_TEXT, bar.getString());
		assertFalse(bar.isIndeterminate());
	}

	@Test @Ignore
	public void testParallelProgress() {
		MockComposite ctask = new MockComposite();
		MockComposite phase1 = new MockComposite("phase1");
		MockComposite phase2 = new MockComposite("phase2");
		
		final JProgressBar bar = new TaskProgressBar(ctask);
		final List<String> strings = new ArrayList<String>();
		final List<Boolean> indeterminate = new ArrayList<Boolean>();
		bar.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (strings.size() < 1 || !strings.get(strings.size() - 1).equals(bar.getString())) {
					strings.add(bar.getString());
					indeterminate.add(bar.isIndeterminate());
				}
			}
		});

		final List<String> expStrings = new ArrayList<String>();
		final List<Boolean> expIndeterminate = new ArrayList<Boolean>();
		
		ctask.start();
		expStrings.add(ctask.toString());
		expIndeterminate.add(true);
		
		ctask.d_mgr.firePhaseStarted(phase1);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + ")");
		expIndeterminate.add(true);
		
		phase1.d_mgr.fireTaskProgress(300, 1000);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + "): 30%");
		expIndeterminate.add(false);
		
		ctask.d_mgr.firePhaseStarted(phase2);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + ", " + phase2.toString() + ")");
		expIndeterminate.add(true);
		
		//FIXME: fill in
		
		assertEquals(expStrings, strings);
		assertEquals(expIndeterminate, indeterminate);
	}
}
