package org.drugis.common.threading;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.drugis.common.threading.event.TaskAbortedEvent;
import org.drugis.common.threading.event.TaskFailedEvent;
import org.drugis.common.threading.event.TaskFinishedEvent;
import org.drugis.common.threading.event.TaskStartedEvent;
import org.junit.Test;

public class SimpleSuspendableTaskTest {
	abstract class FakeSuspendable implements Suspendable {
		public boolean isSuspended() { return false; }
		public void suspend() {}
		public void wakeUp() {}
		public void abort() {}
	}
	
	private AbortedException d_abort = new AbortedException();
	private RuntimeException d_fail = new RuntimeException("FAIL");
	
	class SuccessfulTask extends FakeSuspendable {
		public void run() {
			
		}
	}
	
	class AbortedTask extends FakeSuspendable {
		public void run() {
			throw d_abort;
		}
	}
	
	class FailedTask extends FakeSuspendable {
		public void run() {
			throw d_fail;
		}
	}
	
	@Test
	public void testInitialValues() {
		Task task = new SimpleSuspendableTask(new SuccessfulTask());
		assertFalse(task.isStarted());
		assertFalse(task.isFinished());
		assertFalse(task.isAborted());
		assertFalse(task.isFailed());
		assertNull(task.getFailureCause());
	}
	
	@Test
	public void testSuccessfulTask() {
		SimpleTask task = new SimpleSuspendableTask(new SuccessfulTask());
		
		TaskListener mockListener =  createStrictMock(TaskListener.class);
		mockListener.taskEvent(new TaskStartedEvent(task));
		mockListener.taskEvent(new TaskFinishedEvent(task));
		replay(mockListener);
		
		task.addTaskListener(mockListener);
		task.run();
		verify(mockListener);
		
		assertTrue(task.isStarted());
		assertTrue(task.isFinished());
		assertFalse(task.isAborted());
		assertFalse(task.isFailed());
		assertNull(task.getFailureCause());
	}
	
	@Test
	public void testAbortedTask() {
		SimpleTask task = new SimpleSuspendableTask(new AbortedTask());
		
		TaskListener mockListener =  createStrictMock(TaskListener.class);
		mockListener.taskEvent(new TaskStartedEvent(task));
		mockListener.taskEvent(new TaskAbortedEvent(task));
		replay(mockListener);
		
		task.addTaskListener(mockListener);
		task.run();
		verify(mockListener);
		
		assertTrue(task.isStarted());
		assertFalse(task.isFinished());
		assertTrue(task.isAborted());
		assertFalse(task.isFailed());
		assertNull(task.getFailureCause());
	}
	
	@Test
	public void testFailedTask() {
		SimpleTask task = new SimpleSuspendableTask(new FailedTask());
		
		TaskListener mockListener =  createStrictMock(TaskListener.class);
		mockListener.taskEvent(new TaskStartedEvent(task));
		mockListener.taskEvent(new TaskFailedEvent(task, d_fail));
		replay(mockListener);
		
		task.addTaskListener(mockListener);
		task.run();
		verify(mockListener);
		
		assertTrue(task.isStarted());
		assertFalse(task.isFinished());
		assertFalse(task.isAborted());
		assertTrue(task.isFailed());
		assertEquals(d_fail, task.getFailureCause());
	}
	
	@Test
	public void suspendSuspendsNested() {
		Suspendable mockSuspendable = createStrictMock(Suspendable.class);
		expect(mockSuspendable.isSuspended()).andReturn(false);
		mockSuspendable.suspend();
		expect(mockSuspendable.isSuspended()).andReturn(true);
		replay(mockSuspendable);
		
		SimpleTask task = new SimpleSuspendableTask(mockSuspendable);
		assertFalse(task.isSuspended());
		task.suspend();
		assertTrue(task.isSuspended());
		verify(mockSuspendable);
	}
	
	@Test
	public void wakeUpWakesNested() {
		Suspendable mockSuspendable = createStrictMock(Suspendable.class);
		mockSuspendable.wakeUp();
		replay(mockSuspendable);
		
		SimpleTask task = new SimpleSuspendableTask(mockSuspendable);
		task.wakeUp();
		verify(mockSuspendable);
	}
	
	@Test
	public void wakeUpAbortsNested() {
		Suspendable mockSuspendable = createStrictMock(Suspendable.class);
		mockSuspendable.abort();
		replay(mockSuspendable);
		
		SimpleTask task = new SimpleSuspendableTask(mockSuspendable);
		task.abort();
		verify(mockSuspendable);
	}
}
