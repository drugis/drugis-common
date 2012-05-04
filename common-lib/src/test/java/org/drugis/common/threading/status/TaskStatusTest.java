package org.drugis.common.threading.status;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.common.threading.activity.MockTask;
import org.drugis.common.threading.activity.Transition;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class TaskStatusTest {

	
	@Test
	public void testTaskTerminatedModel() {
		MockTask task = new MockTask();
		ValueModel taskTerminated = new TaskTerminatedModel(task);
		
		assertFalse((Boolean)taskTerminated.getValue());
		
		task.finish();
		assertTrue((Boolean)taskTerminated.getValue());
		
		task.abort();
		assertTrue((Boolean)taskTerminated.getValue());

	}
	
	@Test(expected = IllegalAccessError.class)
	public void testTaskStartableSetValue() throws IllegalAccessError {
		MockTask task = new MockTask();
		ValueModel taskStartable = new TaskStartableModel(task);
		taskStartable.setValue(null);
	}
	
	@Test(expected = IllegalAccessError.class)
	public void testTaskFinishedSetValue() throws IllegalAccessError {
		MockTask task = new MockTask();
		ValueModel taskFinished = new TaskTerminatedModel(task);
		taskFinished.setValue(null);
	}


	@Test
	public void testTaskStartable() {
		MockTask task = new MockTask();
		ValueModel taskStartable = new TaskStartableModel(task);
		
		assertTrue((Boolean)taskStartable.getValue());
		
		task.finish();
		assertFalse((Boolean)taskStartable.getValue());

	}

	@Test
	public void testTaskInPhase() {
		MockTask source = new MockTask();
		source.setName("source");
		MockTask target = new MockTask();
		target.setName("target");
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.add(new DirectTransition(source,target));
		
		ActivityModel model = new ActivityModel(source, target, transitions);
		ActivityTask task = new ActivityTask(model);
		
		ValueModel inPhase = new  ActivityTaskInPhase(task, target);
		source.start();
		assertFalse((Boolean)inPhase.getValue());
		source.finish();
		target.start();
		assertTrue((Boolean)inPhase.getValue());

	}
	
}
