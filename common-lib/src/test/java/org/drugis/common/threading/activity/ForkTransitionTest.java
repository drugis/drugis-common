package org.drugis.common.threading.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;
import org.junit.Test;

public class ForkTransitionTest {
	@Test
	public void testConstruct() {
		MockTask source = new MockTask();
		List<Task> target = new ArrayList<Task>(3);
		target.add(new MockTask());
		target.add(new MockTask());
		target.add(new MockTask());
		
		ForkTransition trans = new ForkTransition(source, target);
		source.start();
		
		assertEquals(Collections.singletonList(source), trans.getSources());
		assertEquals(target, trans.getTargets());
		assertFalse(trans.isReady());
		
		source.finish();
		assertTrue(trans.isReady());
		assertEquals(target, trans.transition());
	}
}
