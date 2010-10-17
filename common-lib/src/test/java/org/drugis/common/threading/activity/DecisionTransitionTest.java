package org.drugis.common.threading.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;
import org.junit.Test;

public class DecisionTransitionTest {
	@Test
	public void testConstruct() {
		MockTask source = new MockTask();
		Task ifTask = new MockTask();
		Task elTask = new MockTask();

		Condition condition = createStrictMock(Condition.class);
		
		Transition trans = new DecisionTransition(source, ifTask, elTask, condition);
		source.start();
		
		assertEquals(Collections.singletonList(source), trans.getSources());
		List<Task> targets = new ArrayList<Task>();
		targets.add(ifTask);
		targets.add(elTask);
		assertEquals(targets , trans.getTargets());
		assertFalse(trans.isReady());
		
		source.finish();
		assertTrue(trans.isReady());
	}
	
	@Test
	public void testTrueCondition() {
		MockTask source = new MockTask();
		Task ifTask = new MockTask();
		Task elTask = new MockTask();

		Condition condition = createStrictMock(Condition.class);
		expect(condition.evaluate()).andReturn(true);
		replay(condition);
		
		Transition trans = new DecisionTransition(source, ifTask, elTask, condition);
		source.start();
		source.finish();
		
		assertEquals(Collections.singletonList(ifTask), trans.transition());
		verify(condition);
	}
	
	@Test
	public void testFalseCondition() {
		MockTask source = new MockTask();
		Task ifTask = new MockTask();
		Task elTask = new MockTask();

		Condition condition = createStrictMock(Condition.class);
		expect(condition.evaluate()).andReturn(false);
		replay(condition);
		
		Transition trans = new DecisionTransition(source, ifTask, elTask, condition);
		source.start();
		source.finish();
		
		assertEquals(Collections.singletonList(elTask), trans.transition());
		verify(condition);
	}
}
