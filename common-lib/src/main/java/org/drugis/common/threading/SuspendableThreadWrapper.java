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

import java.lang.Thread.State;

public class SuspendableThreadWrapper {
	private Thread d_thread;
	private final Suspendable d_runnable;

	public SuspendableThreadWrapper(Suspendable runnable) {
		d_runnable = runnable;
	}
	
	public SuspendableThreadWrapper(Runnable runnable) {
		this(wrap(runnable));
	}
	
	private static Suspendable wrap(Runnable runnable) {
		if (runnable instanceof Suspendable) {
			return (Suspendable)runnable;
		}
		return new NonSuspendable(runnable);
	}

	public synchronized void start() {
		if (d_thread == null) {
			startAsNewThread();
		} else {
			resumeThread();
		}
	}
	
	public synchronized boolean suspend() {
		if (d_thread == null) {
			throw new IllegalStateException("Thread not started yet");
		} else {
			return d_runnable.suspend();
		}
	}

	public boolean isTerminated() {
		if (d_thread == null)
			return false;
		return d_thread.getState() == State.TERMINATED;
	}
	
	public boolean terminate() {
		if (d_thread == null)
			return true;
		else {
			return d_runnable.abort();
		}
	}
	
	private void startAsNewThread() {
		d_thread = new Thread(d_runnable);
		d_thread.start();
	}
	
	private void resumeThread() {
		if (d_runnable.isSuspended()) {
			d_runnable.wakeUp();
		} /*else {
			throw new RuntimeException("Thread already running.");
		}*/
	}
	
	public Runnable getRunnable() {
		return d_runnable;
	}
	
	@Override
	public String toString() {
		return d_runnable.toString();
	}

	public boolean isAborted() {
		return d_runnable.isAborted();
	}
}
