package org.drugis.common.threading.event;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.junit.Before;
import org.junit.Test;

public class ListenerManagerTest {
	private Task d_task;
	private Task d_phase;
	private Exception d_cause;
	private ListenerManager d_mgr;

	@Before
	public void setUp() {
		d_task = Util.buildNullTask();
		d_cause = new Exception("X");
		d_phase = Util.buildNullTask();
		d_mgr = new ListenerManager(d_task);
	}
	
	@Test
	public void testFiresCorrectEvents() {
		TaskListener mockListener = createStrictMock(TaskListener.class);
		mockListener.taskEvent(new TaskStartedEvent(d_task));
		mockListener.taskEvent(new PhaseStartedEvent(d_task, d_phase));
		mockListener.taskEvent(new TaskProgressEvent(d_task, 10, 1000));
		mockListener.taskEvent(new PhaseFinishedEvent(d_task, d_phase));
		mockListener.taskEvent(new TaskProgressEvent(d_task, 900, 1000));
		mockListener.taskEvent(new TaskFinishedEvent(d_task));
		mockListener.taskEvent(new TaskFailedEvent(d_task, d_cause));
		mockListener.taskEvent(new TaskAbortedEvent(d_task));
		replay(mockListener);
		
		d_mgr.addTaskListener(mockListener);
		d_mgr.fireTaskStarted();
		d_mgr.firePhaseStarted(d_phase);
		d_mgr.fireTaskProgress(10, 1000);
		d_mgr.firePhaseFinished(d_phase);
		d_mgr.fireTaskProgress(900, 1000);
		d_mgr.fireTaskFinished();
		d_mgr.fireTaskFailed(d_cause);
		d_mgr.fireTaskAborted();
		verify(mockListener);
	}
	
	@Test
	public void testMultipleListeners() {
		TaskListener mockListener1 = createStrictMock(TaskListener.class);
		mockListener1.taskEvent(new TaskStartedEvent(d_task));
		replay(mockListener1);
		TaskListener mockListener2 = createStrictMock(TaskListener.class);
		mockListener2.taskEvent(new TaskStartedEvent(d_task));
		replay(mockListener2);
		
		d_mgr.addTaskListener(mockListener1);
		d_mgr.addTaskListener(mockListener2);
		d_mgr.fireTaskStarted();
		verify(mockListener1);
		verify(mockListener2);
	}
	
	@Test
	public void testRemoveListener() {
		TaskListener mockListener = createStrictMock(TaskListener.class);
		mockListener.taskEvent(new TaskStartedEvent(d_task));
		replay(mockListener);
		
		d_mgr.addTaskListener(mockListener);
		d_mgr.fireTaskStarted();
		d_mgr.removeTaskListener(mockListener);
		d_mgr.fireTaskFinished();
		verify(mockListener);
	}
}
