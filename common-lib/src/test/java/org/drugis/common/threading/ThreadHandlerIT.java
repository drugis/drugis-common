/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.common.threading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ThreadHandlerIT {

	private int d_numCores;


	public static class SuspendableTestThread extends AbstractSuspendable {
		
		private final int d_ms;
		boolean d_done;

		public SuspendableTestThread(int ms) {
			d_ms = ms;
		}
		
		public synchronized boolean getDone() {
			return d_done;
		}
		
		public void run() {
			if (d_done)
				throw new IllegalStateException("Thread already done.");
			try {
				Thread.sleep(d_ms);
				waitIfSuspended();
			} catch (InterruptedException e) {
			} catch (AbortedException t) {
			}
			d_done = true;
		}
		
		// FIXME: Hacky.
		@Override
		public boolean equals(Object o) {
			if (o instanceof SuspendableThreadWrapper)
				return ((SuspendableThreadWrapper) o).getRunnable().equals(this);
			else if (o instanceof SuspendableTestThread)
				return super.equals(o);
			return false;
		}
	};
	
	class NonSuspendableTestThread implements Runnable {
		
		private final int d_ms;
		boolean d_done;

		public NonSuspendableTestThread(int ms) {
			d_ms = ms;
		}
		
		public synchronized boolean getDone() {
			return d_done;
		}
		
		@Override
		public String toString() {
			return ""+d_ms;
		}
		
		public void run() {
			if (d_done)
				throw new IllegalStateException("Thread already done.");
			try {
				Thread.sleep(d_ms);
			} catch (InterruptedException e) {
			}
			d_done = true;
		}
	};
	
	@Before
	public void setUp() {
		ThreadHandler.getInstance().clear();
		d_numCores = Runtime.getRuntime().availableProcessors();
	}
	
	@Test
	public void testQueueingOrder() {
		LinkedList<SimpleTask> ToDo1 = new LinkedList<SimpleTask>();
		LinkedList<SimpleTask> ToDo2 = new LinkedList<SimpleTask>();
		
		for (int i = 0; i < d_numCores; ++i) {
			ToDo1.add(new SimpleSuspendableTask(new SuspendableTestThread(400)));
			if (i < d_numCores - 1) {
				ToDo2.add(new SimpleSuspendableTask(new SuspendableTestThread(400)));
			}
		}
		ThreadHandler th = ThreadHandler.getInstance();
		
		th.scheduleTasks(ToDo1); // fills the available cores
		th.scheduleTasks(ToDo2); // should push back every task except ToDo1[0].

		sleepLongEnough();
		List<SimpleTask> expected = new ArrayList<SimpleTask>(ToDo2);
		expected.add(ToDo1.get(0));
		assertEquals(expected, th.getRunningTasks());
	}
	

	
	@Test
	public void testReprioritise() {
		LinkedList<SimpleTask> ToDo1 = new LinkedList<SimpleTask>();
						
		final int NUMMODELS = d_numCores + 2;
		
		for(int i=0; i < NUMMODELS; ++i) {
			ToDo1.add(new SimpleSuspendableTask(new SuspendableTestThread((i+4) * 400)));
		}
		
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTasks(ToDo1);
		
		List<SimpleTask> nCoresHeadList = ToDo1.subList(0, d_numCores);
		List<SimpleTask> nCoresHeadListComplement = ToDo1.subList(d_numCores, d_numCores + (NUMMODELS - d_numCores));

		sleepLongEnough();
		assertTrue(th.getRunningTasks().containsAll(nCoresHeadList));
		assertTrue(th.getScheduledTasks().containsAll(ToDo1));
		
		// Note: NOP; rescheduling already-running tasks should not change anything
		th.scheduleTasks(nCoresHeadList);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().containsAll(nCoresHeadList));
		assertTrue(th.getScheduledTasks().containsAll(ToDo1));

		// reprioritise scheduled tasks by re-adding them; should displace running tasks
		th.scheduleTasks(nCoresHeadListComplement);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().containsAll(nCoresHeadListComplement));
		assertTrue(th.getRunningTasks().containsAll(nCoresHeadList.subList(0, d_numCores - 2)));
		assertTrue(th.getScheduledTasks().containsAll(ToDo1));
	}
	
	@Test
	public void testReprioritiseDontTouchNonSuspendable() {

		LinkedList<SimpleTask> ToDo1 = new LinkedList<SimpleTask>();
		LinkedList<SimpleTask> ToDo2 = new LinkedList<SimpleTask>();
		for(int i=0; i < d_numCores - 1; ++i) {
			ToDo1.add(new SimpleSuspendableTask(new SuspendableTestThread(600)));
			ToDo2.add(new SimpleSuspendableTask(new SuspendableTestThread(600)));
		}
		ToDo1.add(new SimpleSuspendableTask(new NonSuspendableTestThread(600)));
		ToDo2.add(new SimpleSuspendableTask(new SuspendableTestThread(400)));
		
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTasks(ToDo1);

		sleepLongEnough();
		assertEquals(th.getRunningTasks(), ToDo1);

		LinkedList<SimpleTask> expected = new LinkedList<SimpleTask>(ToDo2.subList(0, ToDo2.size() - 1));
		expected.addFirst(ToDo1.getLast());
		th.scheduleTasks(ToDo2);
				
		sleepLongEnough();
		assertEquals(expected, th.getRunningTasks());
	}
	
	@Test
	public void testHighLoad() {
		final int NUMTHREADS = 100;
		LinkedList<SimpleTask> runnables = new LinkedList<SimpleTask>();
		ThreadHandler th = ThreadHandler.getInstance();
		ArrayList<SimpleTask> threadList = new ArrayList<SimpleTask>(NUMTHREADS);
		
		for (int i=0; i<NUMTHREADS; ++i) {
			SimpleTask mod = new SimpleSuspendableTask(new SuspendableTestThread((int) (Math.random() * 100)));
			threadList.add(mod);
			runnables.add(mod);
			if ((Math.random() > 0.75) || (i == (NUMTHREADS-1))) {
				th.scheduleTasks(runnables);
				runnables.clear();
			}
			sleep((int) (Math.random() * 50));
			assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_runningTasks.size());
		}
		
		assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_runningTasks.size());
		waitTillDone();
		for (SimpleTask mod : threadList) {
			assertTrue(mod.isFinished());
		}
	}
	
	@Test
	public void testTakeSuspendableSlot() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		th.scheduleTask(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		for (int i=0; i<d_numCores -1; ++i)
			th.scheduleTask(new SimpleSuspendableTask(new NonSuspendableTestThread(300)));
		SimpleTask newTask = new SimpleSuspendableTask(new SuspendableTestThread(300));
		th.scheduleTask(newTask);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().contains(newTask));

		waitTillDone();
		
		for (int i=0; i<d_numCores -1; ++i)
			th.scheduleTask(new SimpleSuspendableTask(new NonSuspendableTestThread(300)));
		th.scheduleTask(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		newTask = new SimpleSuspendableTask(new SuspendableTestThread(300));
		th.scheduleTask(newTask);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().contains(newTask));

		waitTillDone();
	}
	

	@Test
	public void testRemoveWaitingSimpleTask() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		SimpleTask t = new SimpleSuspendableTask(new SuspendableTestThread(100));
		for (int i=0; i<d_numCores + 1; ++i) {
			runthreads.add(new SimpleSuspendableTask(new SuspendableTestThread(100))); 
		}
		runthreads.add(t);// 2 threads in waiting list.
		th.scheduleTasks(runthreads);
		assertEquals(d_numCores + 2, th.getQueuedThreads());
		
		th.remove(t);
		assertEquals(d_numCores + 1, th.getQueuedThreads());
		
		waitTillDone();
		assertFalse(t.isFinished());
	}
	
	@Test
	public void testClearRemovesWaitingTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<d_numCores + 2; ++i) // 2 threads in waiting list.
			runthreads.add(new SimpleSuspendableTask(new SuspendableTestThread(100)));
		th.scheduleTasks(runthreads);
		assertEquals(d_numCores + 2, th.getQueuedThreads());
		
		th.clear();
		
		assertEquals(0, th.getQueuedThreads());
	}
	
	@Test
	public void testRemoveSuspendableRunningTask() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		SimpleTask task = new SimpleSuspendableTask(new SuspendableTestThread(10000));
		th.scheduleTask(task);
		sleepLongEnough();
		assertEquals(1, th.getRunningThreads());
		
		th.remove(task);
		
		sleepLongEnough();
		assertEquals(0, th.getRunningThreads());
		assertFalse(task.isFinished());
		assertTrue(task.isAborted());
		assertFalse(task.isSuspended());
	}
	
	@Test
	public void testClearRemovesSuspendableRunningTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<d_numCores + 2; ++i) // 2 threads in waiting list.
			runthreads.add(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		th.scheduleTasks(runthreads);
		sleepLongEnough();
		assertEquals(d_numCores, th.getRunningThreads());
		
		th.clear();
		
		sleepLongEnough();
		assertEquals(0, th.getRunningThreads());
	}
	
	@Test
	public void testRemoveCantRemoveUnsuspendableRunningTask() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		SimpleSuspendableTask t = new SimpleSuspendableTask(new NonSuspendableTestThread(1000));
		th.scheduleTask(t);
		sleepLongEnough();
		assertEquals(1, th.getRunningThreads());
		
		assertFalse(th.remove(t));

		sleepLongEnough();
		assertEquals(1, th.getRunningThreads());
	}
	
	
	@Test
	public void testClearCantRemoveUnsuspendableRunningTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<d_numCores; ++i) // numcore threads running.
			runthreads.add(new SimpleSuspendableTask(new NonSuspendableTestThread(600)));
		th.scheduleTasks(runthreads);
		
		sleepLongEnough();
		assertEquals(d_numCores, th.getRunningThreads());
		
		th.clear();
		
		sleepLongEnough();
		assertEquals(d_numCores, th.getRunningThreads());
	}

	private void sleepLongEnough() {
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
	}
	
	class SomeCompositeTask implements CompositeTask {
		boolean d_started = false;
		
		SimpleTask d_first = new SimpleSuspendableTask(new SuspendableTestThread(300));
		SimpleTask d_middle1 = new SimpleSuspendableTask(new SuspendableTestThread(300));
		SimpleTask d_middle2 = new SimpleSuspendableTask(new SuspendableTestThread(300));
		SimpleTask d_last = new SimpleSuspendableTask(new SuspendableTestThread(300));
		
		public List<SimpleTask> getNextTasks() {
			if (!d_started) {
				throw new RuntimeException("Can't get tasks if not started");
			} else if (!d_first.isFinished()) {
				return Collections.singletonList(d_first);
			} else if (!d_middle1.isFinished() || !d_middle2.isFinished()) {
				List<SimpleTask> list = new ArrayList<SimpleTask>();
				if (!d_middle1.isFinished()) list.add(d_middle1);
				if (!d_middle2.isFinished()) list.add(d_middle2);
				return list ;
			} else if (!d_last.isFinished()) {
				return Collections.singletonList(d_last);
			} else {
				return Collections.<SimpleTask>emptyList();
			}
		}

		public void start() {
			d_started = true;
		}

		public boolean isStarted() {
			return d_started;
		}

		public boolean isFinished() {
			return d_last.isFinished();
		}

		public void addTaskListener(TaskListener l) {}
		public void removeTaskListener(TaskListener l) {}
		public boolean isFailed() { return false; }
		public Throwable getFailureCause() { return null; }
		public boolean isAborted() { return false; }
		
	}
	
	@Test
	public void testScheduleCompositeTask() {
		waitTillDone();
		
		ThreadHandler threadHandler = ThreadHandler.getInstance();

		SomeCompositeTask task = new SomeCompositeTask();
		threadHandler.scheduleTask(task);
		
		sleepLongEnough();
		assertEquals(Collections.singletonList(task.d_first), threadHandler.getRunningTasks());
		assertEquals(Collections.singletonList(task), threadHandler.getScheduledTasks());
		
		// wait for first task to finish and subsequent tasks to be enqueued
		waitTillDone(task.d_first);
		sleepLongEnough();

		List<Task> expected = new ArrayList<Task>();
		expected.add(task.d_middle1);
		if (d_numCores >= 2) {
			expected.add(task.d_middle2);
		}
		assertEquals(expected, threadHandler.getRunningTasks());
		
		// wait for last task to be finished and its finishing to be detected
		waitTillDone(task.d_last);
		sleepLongEnough();

		assertTrue(task.d_last.isFinished());
		assertTrue(task.isFinished());
		assertEquals(Collections.<Task>emptyList(), threadHandler.getScheduledTasks());
	}
	
	private void waitTillDone(SimpleTask task) {
		try {
			TaskUtil.waitUntilReady(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testScheduleCompositeTaskWithDuplicateTask() {
		ThreadHandler threadHandler = ThreadHandler.getInstance();
		SomeCompositeTask task = new SomeCompositeTask();
		SimpleTask duplicate = task.d_first;
		
		threadHandler.scheduleTask(duplicate);
		threadHandler.scheduleTask(task);
		
		sleepLongEnough();
		List<Task> expected = new ArrayList<Task>();
		expected.add(task);
		expected.add(duplicate);
		assertEquals(Collections.singletonList(duplicate), threadHandler.getRunningTasks());
		assertEquals(expected, threadHandler.getScheduledTasks());
	}
	
	@Test
	public void testScheduleCompositeTaskWithDuplicateTaskVV() {
		ThreadHandler threadHandler = ThreadHandler.getInstance();
		SomeCompositeTask task = new SomeCompositeTask();
		SimpleTask duplicate = task.d_first;
		
		threadHandler.scheduleTask(task);
		threadHandler.scheduleTask(duplicate);
		
		sleepLongEnough();
		List<Task> expected = new ArrayList<Task>();
		expected.add(duplicate);
		expected.add(task);
		assertEquals(Collections.singletonList(duplicate), threadHandler.getRunningTasks());
		assertEquals(expected, threadHandler.getScheduledTasks());
	}
	
	public static void waitTillDone() {
		ThreadHandler th = ThreadHandler.getInstance();
		while ((th.d_runningTasks.size() > 0) || (th.d_scheduledTasks.size() > 0)) {
			sleep(100);
		}
	}
	

	private static void sleep(int ms ) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
