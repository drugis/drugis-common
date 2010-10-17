package org.drugis.common.threading.activity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.common.threading.Task;
import org.junit.Test;

public class ActivityModelTest {
	@Test
	public void testInitialState() {
		MockTask start = new MockTask();
		start.start();
		MockTask end = new MockTask();
		Transition trans = new DirectTransition(start, end);
		
		ActivityModel model = new ActivityModel(start, end, Collections.singleton(trans));
		assertEquals(Collections.singleton(start), model.getNextStates());
	}
	
	@Test
	public void testSimpleTransition() {
		MockTask start = new MockTask();
		start.start();
		MockTask end = new MockTask();
		Transition trans = new DirectTransition(start, end);
		
		ActivityModel model = new ActivityModel(start, end, Collections.singleton(trans));
		start.finish();
		
		assertEquals(Collections.singleton(end), model.getNextStates());
	}
	
	@Test
	public void testIsFinished() {
		MockTask start = new MockTask();
		start.start();
		MockTask end = new MockTask();
		Transition trans = new DirectTransition(start, end);
		
		ActivityModel model = new ActivityModel(start, end, Collections.singleton(trans));
		start.finish();
		end.start();
		end.finish();
		
		assertTrue(model.isFinished());
		assertEquals(Collections.<Task>emptySet(), model.getNextStates());
	}
	
	@Test
	public void testForkJoinModel() {
		MockTask start = new MockTask();
		start.start();
		MockTask end = new MockTask();
		List<MockTask> parallel = new ArrayList<MockTask>();
		parallel.add(new MockTask());
		parallel.add(new MockTask());
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.add(new ForkTransition(start, parallel));
		transitions.add(new JoinTransition(parallel, end));
		
		ActivityModel model = new ActivityModel(start, end, transitions);
		assertEquals(Collections.singleton(start), model.getNextStates());
		
		start.finish();
		assertEquals(new HashSet<Task>(parallel), model.getNextStates());
		
		parallel.get(1).finish();
		assertEquals(Collections.singleton(parallel.get(0)), model.getNextStates());
		
		parallel.get(0).finish();
		assertEquals(Collections.singleton(end), model.getNextStates());
		
		end.finish();
		assertTrue(model.isFinished());
	}
	
	@Test
	public void testForkJoinComplexModel() {
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
		assertEquals(Collections.singleton(start), model.getNextStates());
		
		start.finish();
		assertEquals(new HashSet<Task>(fork), model.getNextStates());
		
		fork.get(1).finish();
		Set<Task> expected = new HashSet<Task>();
		expected.add(fork.get(0));
		expected.add(join.get(0));
		assertEquals(expected, model.getNextStates());
		
		join.get(0).finish();
		assertEquals(Collections.singleton(fork.get(0)), model.getNextStates());
		
		fork.get(0).finish();
		assertEquals(Collections.singleton(join.get(1)), model.getNextStates());
		
		join.get(1).finish();
		assertEquals(Collections.singleton(end), model.getNextStates());
		
		end.finish();
		assertTrue(model.isFinished());
	}
}
