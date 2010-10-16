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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ThreadHandlerIT {

	public static class SuspendableTestThread extends AbstractSuspendable {
		
		private final int d_ms;
		boolean d_done;

		public SuspendableTestThread(int ms) {
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
	}
	
	@Test
	public void testQueueingOrder() {
		LinkedList<SimpleTask> ToDo1 = new LinkedList<SimpleTask>();
		LinkedList<SimpleTask> ToDo2 = new LinkedList<SimpleTask>();
		
		int numCores = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < numCores; ++i) {
			ToDo1.add(new SimpleSuspendableTask(new SuspendableTestThread(400)));
			if (i < numCores - 1) {
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
						
		int numCores = Runtime.getRuntime().availableProcessors();
		final int NUMMODELS = numCores + 2;
		
		for(int i=0; i < NUMMODELS; ++i) {
			ToDo1.add(new SimpleSuspendableTask(new SuspendableTestThread((i+4) * 400)));
		}
		
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTasks(ToDo1);
		
		List<SimpleTask> nCoresHeadList = ToDo1.subList(0, numCores);
		List<SimpleTask> nCoresHeadListComplement = ToDo1.subList(numCores, numCores + (NUMMODELS - numCores));

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
		assertTrue(th.getRunningTasks().containsAll(nCoresHeadList.subList(0, NUMMODELS - numCores)));
		assertTrue(th.getScheduledTasks().containsAll(ToDo1));
	}
	
	@Test
	public void testReprioritiseDontTouchNonSuspendable() {
		int numCores = Runtime.getRuntime().availableProcessors();

		LinkedList<SimpleTask> ToDo1 = new LinkedList<SimpleTask>();
		LinkedList<SimpleTask> ToDo2 = new LinkedList<SimpleTask>();
		for(int i=0; i < numCores - 1; ++i) {
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
		int numCores = Runtime.getRuntime().availableProcessors();
		
		th.scheduleTask(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		for (int i=0; i<numCores -1; ++i)
			th.scheduleTask(new SimpleSuspendableTask(new NonSuspendableTestThread(300)));
		SimpleTask newTask = new SimpleSuspendableTask(new SuspendableTestThread(300));
		th.scheduleTask(newTask);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().contains(newTask));

		waitTillDone();
		
		for (int i=0; i<numCores -1; ++i)
			th.scheduleTask(new SimpleSuspendableTask(new NonSuspendableTestThread(300)));
		th.scheduleTask(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		newTask = new SimpleSuspendableTask(new SuspendableTestThread(300));
		th.scheduleTask(newTask);
		sleepLongEnough();
		assertTrue(th.getRunningTasks().contains(newTask));

		waitTillDone();
	}
	
	@Test
	public void testClearRemovesWaitingTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		int numCores = Runtime.getRuntime().availableProcessors();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<numCores + 2; ++i) // 2 threads in waiting list.
			runthreads.add(new SimpleSuspendableTask(new SuspendableTestThread(100)));
		th.scheduleTasks(runthreads);
		assertEquals(numCores + 2, th.getQueuedThreads());
		
		th.clear();
		
		assertEquals(0, th.getQueuedThreads());
	}
	
	@Test
	public void testClearCantRemoveUnsuspendableRunningTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		int numCores = Runtime.getRuntime().availableProcessors();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<numCores; ++i) // numcore threads running.
			runthreads.add(new SimpleSuspendableTask(new NonSuspendableTestThread(600)));
		th.scheduleTasks(runthreads);
		
		sleepLongEnough();
		assertEquals(numCores, th.getRunningThreads());
		
		th.clear();
		
		sleepLongEnough();
		assertEquals(numCores, th.getRunningThreads());
	}

	private void sleepLongEnough() {
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
		}
	}
	
	@Test
	public void testClearRemovesSuspendableRunningTasks() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		int numCores = Runtime.getRuntime().availableProcessors();
		
		List<SimpleTask> runthreads = new ArrayList<SimpleTask>();
		for (int i=0; i<numCores + 2; ++i) // 2 threads in waiting list.
			runthreads.add(new SimpleSuspendableTask(new SuspendableTestThread(300)));
		th.scheduleTasks(runthreads);
		sleepLongEnough();
		assertEquals(numCores, th.getRunningThreads());
		
		th.clear();
		
		sleepLongEnough();
		assertEquals(0, th.getRunningThreads());
	}
	
	public static void waitTillDone() {
		ThreadHandler th = ThreadHandler.getInstance();
		while ((th.d_runningTasks.size() > 0) || (th.d_scheduledTasks.size() > 0)) {
//			System.out.println("running: " + th.d_runningTasks.size() + " scheduled: "+th.d_scheduledTasks.size());
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
