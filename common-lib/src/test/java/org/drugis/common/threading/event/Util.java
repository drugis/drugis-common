package org.drugis.common.threading.event;

import org.drugis.common.threading.AbstractSuspendable;
import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.Task;

public class Util {

	static Task buildNullTask() {
		return new SimpleSuspendableTask(new AbstractSuspendable() {
			public void run() {
			}
		});
	}

}
