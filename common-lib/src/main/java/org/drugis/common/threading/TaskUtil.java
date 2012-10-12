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

import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;

public class TaskUtil {
	public static void run(ActivityModel model) throws InterruptedException {
		run(new ActivityTask(model));
	}

	public static void run(Task task) throws InterruptedException {
		start(task);
		waitUntilReady(task);
	}

	private static void start(Task task) {
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTask(task);
	}

	public static void waitUntilReady(Task task) throws InterruptedException {
		while (isRunning(task)) {
			Thread.sleep(100);
		}
	}

	public static boolean isRunning(Task task) {
		if (task.isFailed()) throw new RuntimeException("Task failed", task.getFailureCause());
		return !task.isFinished() && !task.isAborted();
	}
}
