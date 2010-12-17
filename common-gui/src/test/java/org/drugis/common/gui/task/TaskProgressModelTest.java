package org.drugis.common.gui.task;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.common.JUnitUtil;
import org.drugis.common.gui.TextProgressModel;
import org.drugis.common.threading.AbstractIterativeComputation;
import org.drugis.common.threading.CompositeTask;
import org.drugis.common.threading.FailureException;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.NullTask;
import org.drugis.common.threading.SimpleTask;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.activity.MockTask;
import org.drugis.common.threading.event.ListenerManager;
import org.junit.Test;

public class TaskProgressModelTest {
	private final class MockComposite implements CompositeTask {
		private boolean d_started = false;
		public ListenerManager d_mgr = new ListenerManager(this);
		private boolean d_finished = false;
		private String d_str = null;
		
		public MockComposite() { }
		public MockComposite(String str) { d_str = str; }
		@Override public String toString() { return d_str != null ? d_str : mkString(); }

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
		
		public String mkString() {
			return super.toString().substring(super.toString().indexOf('@'));
		}
	}

	static class ShortComputation extends AbstractIterativeComputation {
		public ShortComputation(int max) {
			super(max);
		}
		public void doStep() {}
		
		@Override
		public String toString() {
			return super.toString().substring(super.toString().indexOf('@'));
		}
	}
	
	@Test
	public void testGetSetTask() {
		ShortComputation comp = new ShortComputation(100);
		IterativeTask task = new IterativeTask(comp);
		TaskProgressModel mod = new TaskProgressModel(new NullTask());
		mod.setTask(task);
		assertTrue(mod.getTask()== task);
	}
	
	@Test
	public void testSimpleProgress() {
		IterativeTask task = new IterativeTask(new ShortComputation(10000));
		task.setReportingInterval(10);
		
		TextProgressModel model = new TaskProgressModel(task);
		assertEquals(task.toString() + " (waiting)", model.getText());
		assertFalse(model.getDeterminate());
		
		task.run();
		assertEquals(TaskProgressModel.DONE_TEXT, model.getText());
		assertTrue(model.getDeterminate());
		assertEquals(1.0, model.getProgress(), 0.0);
	}

	@Test
	public void testSimpleProgressEvents() {
		final IterativeTask task = new IterativeTask(new ShortComputation(10000));
		task.setReportingInterval(2000);
		
		final TextProgressModel model = new TaskProgressModel(task);
		final List<Double> progressEvents = new ArrayList<Double>();
		final List<String> textEvents = new ArrayList<String>();
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(TextProgressModel.PROPERTY_PROGRESS)) {
					progressEvents.add((Double)evt.getNewValue());
				}
				if (evt.getPropertyName().equals(TextProgressModel.PROPERTY_TEXT)) {
					textEvents.add((String)evt.getNewValue());
				}
			}
		});
		
		task.run();
		Double[] expected = new Double[] {0.0, 0.0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.0};
		assertEquals(Arrays.asList(expected), progressEvents);
		
		String[] expectedStr = new String[] {
			task.toString() + ": ?",
			task.toString() + ": 0%",
			task.toString() + ": 20%",
			task.toString() + ": 40%",
			task.toString() + ": 60%",
			task.toString() + ": 80%",
			task.toString() + ": 100%",
			TaskProgressModel.DONE_TEXT
		};
		assertEquals(Arrays.asList(expectedStr), textEvents);
	}

	@Test
	public void testDeterminate() {
		final MockTask task = new MockTask();
		
		final TextProgressModel model = new TaskProgressModel(task);
		task.start();
		
		PropertyChangeListener mock = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_DETERMINATE, false, true);
		model.addPropertyChangeListener(mock);
		task.progress(0, 1000);
		verify(mock);
	}
	
	@Test
	public void testFinishedDeterminate() {
		final MockTask task = new MockTask();
		
		final TextProgressModel model = new TaskProgressModel(task);
		task.start();
		
		PropertyChangeListener mock = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_DETERMINATE, false, true);
		model.addPropertyChangeListener(mock);
		task.finish();
		verify(mock);
	}
	
	@Test
	public void testFailed() {
		final MockTask task = new MockTask();
		
		final TextProgressModel model = new TaskProgressModel(task);
		task.start();
		
		String failText = task + " " + TaskProgressModel.FAILED_TEXT +": " + "Argh!"; 
		PropertyChangeListener mock1 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_DETERMINATE, false, true);
		PropertyChangeListener mock2 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_TEXT, null,	failText);
		PropertyChangeListener mock3 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_PROGRESS, 0.0, 0.0);
		model.addPropertyChangeListener(mock1);
		model.addPropertyChangeListener(mock2);
		model.addPropertyChangeListener(mock3);
		task.fail(new FailureException("Argh!"));
		verify(mock1);
		verify(mock2);
		verify(mock3);
	}
	
	@Test
	public void testAborted() {
		final MockTask task = new MockTask();
		
		final TextProgressModel model = new TaskProgressModel(task);
		task.start();
		
		PropertyChangeListener mock1 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_DETERMINATE, false, true);
		PropertyChangeListener mock2 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_TEXT, null, 
				task.toString() + ": " + TaskProgressModel.ABORTED_TEXT);
		PropertyChangeListener mock3 = JUnitUtil.mockListener(model, TextProgressModel.PROPERTY_PROGRESS, 0.0, 0.0);
		model.addPropertyChangeListener(mock1);
		model.addPropertyChangeListener(mock2);
		model.addPropertyChangeListener(mock3);
		task.abort();
		verify(mock1);
		verify(mock2);
		verify(mock3);
	}
	
	@Test
	public void testPhasedProgress() {
		MockComposite ctask = new MockComposite();
		IterativeTask phase1 = new IterativeTask(new ShortComputation(10000));
		IterativeTask phase2 = new IterativeTask(new ShortComputation(10000));
		phase1.setReportingInterval(3000);
		
		final TextProgressModel model = new TaskProgressModel(ctask);
		
		assertEquals(ctask.toString() + " (waiting)", model.getText());
		ctask.start();
		assertEquals(ctask.toString() + ": ?", model.getText());
		final List<String> textEvents = new ArrayList<String>();
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(TextProgressModel.PROPERTY_TEXT)) {
					textEvents.add(model.getText());
				}
			}
		});
		
		ctask.d_mgr.firePhaseStarted(phase1);
		phase1.run();
		String title1 = ctask.toString() + " (" + phase1.toString() + ": ";
		String title2 = ctask.toString() + " (" + phase2.toString() + ": ";
		String[] expected = new String[] {
				title1 + "?)",
				title1 + "0%)",
				title1 + "30%)",
				title1 + "60%)",
				title1 + "90%)",
				title1 + "100%)",
				ctask.toString() + ": ?",
				title2 + "?)",
				ctask.toString() + ": ?",
				TaskProgressModel.DONE_TEXT
		};
		ctask.d_mgr.firePhaseFinished(phase1);
		
		ctask.d_mgr.firePhaseStarted(phase2);
		assertEquals(false, model.getDeterminate());
		ctask.d_mgr.firePhaseFinished(phase2);
		ctask.finish();
		assertTrue(model.getDeterminate());
		assertEquals(Arrays.asList(expected), textEvents);
	}

	@Test
	public void testParallelProgress() {
		MockComposite ctask = new MockComposite();
		MockComposite phase1 = new MockComposite("phase1");
		MockComposite phase2 = new MockComposite("phase2");
		
		final TextProgressModel model = new TaskProgressModel(ctask);
		final List<String> text = new ArrayList<String>();
		final List<Boolean> determinate = new ArrayList<Boolean>();
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(TaskProgressModel.PROPERTY_TEXT)) {
					text.add(model.getText());
					determinate.add(model.getDeterminate());
				}
			}
		});

		final List<String> expStrings = new ArrayList<String>();
		final List<Boolean> expDeterminate = new ArrayList<Boolean>();
		
		ctask.start();
		expStrings.add(ctask.toString() + ": ?");
		expDeterminate.add(false);
		
		ctask.d_mgr.firePhaseStarted(phase1);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + ": ?)");
		expDeterminate.add(false);
		
		phase1.d_mgr.fireTaskProgress(30, 100);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + ": 30%)");
		expDeterminate.add(true);
		
		ctask.d_mgr.firePhaseStarted(phase2);
		expStrings.add(ctask.toString() + " (" + phase1.toString() + ": 30%, " + phase2.toString() + ": ?)");
		expDeterminate.add(false);
		
		assertEquals(expStrings, text);
		assertEquals(expDeterminate, determinate);
	}
}
