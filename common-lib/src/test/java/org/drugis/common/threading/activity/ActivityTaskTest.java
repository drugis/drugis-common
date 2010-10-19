package org.drugis.common.threading.activity;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.not;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.PhaseFinishedEvent;
import org.drugis.common.threading.event.PhaseStartedEvent;
import org.drugis.common.threading.event.TaskFinishedEvent;
import org.drugis.common.threading.event.TaskStartedEvent;
import org.junit.Ignore;
import org.junit.Test;

public class ActivityTaskTest {
	@Test
	public void testObeysModel() {
		MockTask start = new MockTask();
		start.start();
		MockTask end = new MockTask();
		List<MockTask> fork = new ArrayList<MockTask>();
		fork.add(new MockTask());
		fork.add(new MockTask());
		List<MockTask> join = new ArrayList<MockTask>();
		join.add(new MockTask());
		join.add(new MockTask());
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.add(new ForkTransition(start, fork));
		transitions.add(new JoinTransition(join, end));
		transitions.add(new DirectTransition(fork.get(0), join.get(1)));
		transitions.add(new DirectTransition(fork.get(1), join.get(0)));
		
		ActivityModel model = new ActivityModel(start, end, transitions);
		ActivityTask task = new ActivityTask(model);
		assertFalse(task.isStarted());
		assertAllAndOnly(Collections.emptySet(), task.getNextTasks());
		task.start();
		assertTrue(task.isStarted());
		assertAllAndOnly(Collections.singleton(start), task.getNextTasks());
		
		start.finish();
		assertAllAndOnly(fork, task.getNextTasks());
		
		fork.get(0).start();
		fork.get(1).start();
		fork.get(0).finish();
		fork.get(1).finish();
		assertAllAndOnly(join, task.getNextTasks());
		
		join.get(0).start();
		join.get(1).start();
		join.get(0).finish();
		join.get(1).finish();
		assertAllAndOnly(Collections.singleton(end), task.getNextTasks());
		
		end.start();
		end.finish();
		assertTrue(task.isFinished());
		assertAllAndOnly(Collections.emptySet(), task.getNextTasks());
	}
	
	@Test
	public void testNotifiesStartFinish() {
		MockTask start = new MockTask();
		MockTask end = new MockTask();
		Transition trans = new DirectTransition(start, end);
		
		ActivityModel model = new ActivityModel(start, end, Collections.singleton(trans));
		ActivityTask task = new ActivityTask(model);
		
		TaskListener listener = createStrictMock(TaskListener.class);
		listener.taskEvent(new TaskStartedEvent(task));
		listener.taskEvent(not(eq(new TaskFinishedEvent(task))));
		expectLastCall().anyTimes();
		listener.taskEvent(new TaskFinishedEvent(task));
		replay(listener);
		
		task.addTaskListener(listener);
		task.start();
		start.start();
		start.finish();
		end.start();
		end.finish();
		verify(listener);
	}
	
	@Test
	public void testNotifiesPhases() {
		MockTask start = new MockTask();
		MockTask end = new MockTask();
		Transition trans = new DirectTransition(start, end);
		
		ActivityModel model = new ActivityModel(start, end, Collections.singleton(trans));
		ActivityTask task = new ActivityTask(model);
		task.start();
		
		TaskListener listener = createStrictMock(TaskListener.class);
		listener.taskEvent(new PhaseStartedEvent(task, start));
		listener.taskEvent(new PhaseFinishedEvent(task, start));
		replay(listener);
		
		task.addTaskListener(listener);
		start.start();
		start.finish();
		verify(listener);
	}
	
	@Test @Ignore
	public void testHandlesNested() {
		// FIXME: test for handling of CompositeTask nested into ActivityTask.
		// This should result in the CompositeTask.getNextTasks() being added. 
		fail();
	}
}
