package org.drugis.common.threading.activity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.common.threading.Task;
import org.junit.Test;

public class JoinTransitionTest {
	@Test
	public void testConstruct() {
		List<MockTask> source = new ArrayList<MockTask>(3);
		source.add(new MockTask());
		source.add(new MockTask());
		source.add(new MockTask());
		Task target = new MockTask();
		
		JoinTransition trans = new JoinTransition(source, target);
		
		for (MockTask t : source) {
			t.start();
		}
		
		assertEquals(source, trans.getSources());
		assertEquals(Collections.singletonList(target), trans.getTargets());
		assertFalse(trans.isReady());
		
		source.get(0).finish();
		assertFalse(trans.isReady());
		
		source.get(2).finish();
		assertFalse(trans.isReady());

		source.get(1).finish();
		assertTrue(trans.isReady());
		assertEquals(Collections.singletonList(target), trans.transition());
	}
}
