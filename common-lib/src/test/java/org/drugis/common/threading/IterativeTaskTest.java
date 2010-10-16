package org.drugis.common.threading;

import static org.drugis.common.JUnitUtil.assertNotEquals;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.common.threading.event.TaskFinishedEvent;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.drugis.common.threading.event.TaskStartedEvent;
import org.junit.Test;

public class IterativeTaskTest {
	@Test
	public void testRunIteratesComputation() {
		IterativeComputation comp = new ShortComputation(10);
		IterativeTask iterativeTask = new IterativeTask(comp);
		iterativeTask.run();
		assertEquals(comp.getTotalIterations(), comp.getIteration());
	}
	
	@Test
	public void testInitializationAndFinishCalled() {
		IterativeComputation comp = createStrictMock(IterativeComputation.class);
		comp.initialize();
		expect(comp.getIteration()).andReturn(0);
		expect(comp.getTotalIterations()).andReturn(0);
		comp.finish();
		replay(comp);
		
		IterativeTask iterativeTask = new IterativeTask(comp);
		iterativeTask.run();
		verify(comp);
	}
	
	@Test
	public void testSuspend() {
		IterativeComputation comp = new LongComputation(20);
		IterativeTask iterativeTask = new IterativeTask(comp);
		
		SuspendableThreadWrapper thread = new SuspendableThreadWrapper(iterativeTask);
		thread.start();
		sleep(10);
		iterativeTask.suspend();
		sleep(20);
		assertTrue(iterativeTask.isSuspended());
		int i = comp.getIteration();
		sleep(30);
		assertEquals(i, comp.getIteration());
		iterativeTask.wakeUp();
		sleep(20);
		assertNotEquals(i, comp.getIteration());
		thread.terminate();
	}
	
	@Test
	public void testAbort() {
		IterativeComputation comp = new LongComputation(20);
		IterativeTask iterativeTask = new IterativeTask(comp);
		SuspendableThreadWrapper thread = new SuspendableThreadWrapper(iterativeTask);
		thread.start();
		sleep(10);
		iterativeTask.abort();
		sleep(20);
		assertTrue(iterativeTask.isAborted());
	}
	
	@Test
	public void testNotifyProgress() {
		IterativeComputation comp = new ShortComputation(10);
		IterativeTask task = new IterativeTask(comp);
		task.setReportingInterval(3);
		TaskListener listener = createStrictMock(TaskListener.class);
		listener.taskEvent(new TaskStartedEvent(task));
		listener.taskEvent(new TaskProgressEvent(task, 0, 10));
		listener.taskEvent(new TaskProgressEvent(task, 3, 10));
		listener.taskEvent(new TaskProgressEvent(task, 6, 10));
		listener.taskEvent(new TaskProgressEvent(task, 9, 10));
		listener.taskEvent(new TaskProgressEvent(task, 10, 10));
		listener.taskEvent(new TaskFinishedEvent(task));
		replay(listener);
		
		task.addTaskListener(listener);
		task.run();
		verify(listener);
	}
	
	@Test
	public void testNotifyProgress2() {
		IterativeComputation comp = new ShortComputation(9);
		IterativeTask task = new IterativeTask(comp);
		task.setReportingInterval(3);
		TaskListener listener = createStrictMock(TaskListener.class);
		listener.taskEvent(new TaskStartedEvent(task));
		listener.taskEvent(new TaskProgressEvent(task, 0, 9));
		listener.taskEvent(new TaskProgressEvent(task, 3, 9));
		listener.taskEvent(new TaskProgressEvent(task, 6, 9));
		listener.taskEvent(new TaskProgressEvent(task, 9, 9));
		listener.taskEvent(new TaskFinishedEvent(task));
		replay(listener);
		
		task.addTaskListener(listener);
		task.run();
		verify(listener);
	}
	
	private void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}
}
