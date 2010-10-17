package org.drugis.common.threading.activity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.drugis.common.threading.Task;
import org.junit.Test;

public class DirectTransitionTest {
	@Test
	public void testConstruct() {
		MockTask source = new MockTask();
		Task target = new MockTask();
		
		Transition trans = new DirectTransition(source, target);
		source.start();
		
		assertEquals(Collections.singletonList(source), trans.getSources());
		assertEquals(Collections.singletonList(target), trans.getTargets());
		assertFalse(trans.isReady());
		
		source.finish();
		assertTrue(trans.isReady());
		assertEquals(Collections.singletonList(target), trans.transition());
	}
}
