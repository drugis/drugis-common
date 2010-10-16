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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.threading.SuspendableThreadWrapper;

public class ThreadHandler extends AbstractObservable {
	
	public static final String PROPERTY_RUNNING_THREADS = "runningThreads";
	public static final String PROPERTY_QUEUED_THREADS = "queuedThreads"; // FIXME: rename to queuedTasks
	
	/* Separate thread to start/suspend threads */
	private class RunQueueCleaner implements Runnable {
		public void run(){
			while(true) {
				synchronized (d_runningTasks) {
					List<SuspendableThreadWrapper> toRun = getThreadsToRun(d_numCores);
					List<SuspendableThreadWrapper> toStop = new LinkedList<SuspendableThreadWrapper>(d_runningTasks);
					toStop.removeAll(toRun);
					toRun.removeAll(d_runningTasks);
					for (SuspendableThreadWrapper t : toStop) {
						if (!t.isTerminated()) {
							if (t.suspend()) {
								d_runningTasks.remove(t);
							}
						} else {
							d_runningTasks.remove(t);
						}
					}
					int availableSlots = d_numCores - d_runningTasks.size(); 
					for (int i = 0; i < availableSlots && i < toRun.size(); ++i) {
						SuspendableThreadWrapper t = toRun.get(i);
						t.start();
						d_runningTasks.add(t);
					}
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	} 

	private final int d_numCores;
	LinkedList<SimpleTask> d_scheduledTasks;
	LinkedList<SuspendableThreadWrapper> d_runningTasks;
	Thread d_cleaner;
	private static ThreadHandler d_singleton;
	
	private ThreadHandler() {
		d_numCores = Runtime.getRuntime().availableProcessors();
		d_scheduledTasks = new LinkedList<SimpleTask>();
		d_runningTasks = new LinkedList<SuspendableThreadWrapper>();
		d_cleaner = new Thread(new RunQueueCleaner());
		d_cleaner.start();
	}
	
	public List<SuspendableThreadWrapper> getThreadsToRun(int n) {
		List<SimpleTask> toRun = new ArrayList<SimpleTask>(n);
		for (int i = 0; i < n && i < d_scheduledTasks.size(); ) {
			SimpleTask task = (SimpleTask)d_scheduledTasks.get(i);
			if (task.isFinished()) {
				d_scheduledTasks.remove(i);
			} else {
				toRun.add(d_scheduledTasks.get(i));
				++i;
			}
		}
		return getWrappers(toRun);
	}

	public static ThreadHandler getInstance() {
		if (d_singleton == null)
			d_singleton = new ThreadHandler();
		return d_singleton;
	}
	
	public int getRunningThreads() {
		return d_runningTasks.size();
	}
	
	public int getQueuedThreads() {
		return d_scheduledTasks.size();
	}
	
	public void scheduleTask(SimpleTask r) {
		scheduleTasks(Collections.singleton(r));
	}
	
	public synchronized void scheduleTasks(Collection<SimpleTask> newTasks) {
		synchronized (d_runningTasks) {
			// If tasks already present, reschedule to running or to head of queue
			d_scheduledTasks.removeAll(newTasks);

			d_scheduledTasks.addAll(0, newTasks);
			
			firePropertyChange(PROPERTY_QUEUED_THREADS, null, d_scheduledTasks.size());
			firePropertyChange(PROPERTY_RUNNING_THREADS, null, d_runningTasks.size());
		}
	}
	
	Map<SimpleTask, SuspendableThreadWrapper> d_wrappers = new HashMap<SimpleTask, SuspendableThreadWrapper>();
	
	private LinkedList<SuspendableThreadWrapper> getWrappers(Collection<SimpleTask> newRunnables) {
		vacuumWrappers();
		
		/* Check whether Runnable already has a wrapper, otherwise create it */
		LinkedList<SuspendableThreadWrapper> newList = new LinkedList<SuspendableThreadWrapper>();
		for (SimpleTask r : newRunnables) {
			SuspendableThreadWrapper w = d_wrappers.get(r);
			if (w == null) {
				w = new SuspendableThreadWrapper(r);
				d_wrappers.put(r, w);
			}
			newList.add(w);
		}
		
		return newList;
	}
	
	private void vacuumWrappers() {
		List<Task> toRemove = new ArrayList<Task>(d_wrappers.keySet().size());
		for (Task r : d_wrappers.keySet()) {
			if (r.isFinished()) {
				toRemove.add(r);
			}
		}
		for (Task r : toRemove) {
			d_wrappers.remove(r);
		}
	}
	
	protected List<SimpleTask> getRunningTasks() {
		List<SimpleTask> tasks = new ArrayList<SimpleTask>();
		synchronized(d_runningTasks) {
			for (SuspendableThreadWrapper w : d_runningTasks) {
				tasks.add((SimpleTask)w.getRunnable());
			}
		}
		return tasks;
	}
	
	protected List<SimpleTask> getScheduledTasks() {
		return Collections.unmodifiableList(d_scheduledTasks);
	}

	public void clear() {
		synchronized(d_runningTasks) {
			terminateTasks(d_wrappers.values());
			d_scheduledTasks.clear();
			vacuumWrappers();
		}
		firePropertyChange(PROPERTY_QUEUED_THREADS, null, d_scheduledTasks.size());
		firePropertyChange(PROPERTY_RUNNING_THREADS, null, d_runningTasks.size());
	}

	private void terminateTasks(Collection<SuspendableThreadWrapper> tasks) {
		for (SuspendableThreadWrapper thread : tasks) {
			thread.terminate();
		}
	}
	

}
