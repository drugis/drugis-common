package org.drugis.common.event;

import static org.junit.Assert.*;

import javax.swing.event.TreeModelEvent;

import org.junit.Test;

public class TreeModelEventMatcherTest {
	@Test
	public void testMatch() {
		Integer source1 = new Integer(1);
		Integer source2 = new Integer(2);
		Object[] path1 = {source1};
		Object[] path2 = {source1, source1};
		int[] indices1 = {37, 42};
		int[] indices2 = {37, 43};
		Object[] children1 = {source2};
		Object[] children2 = {source1};

		TreeModelEventMatcher matcher1 = new TreeModelEventMatcher(new TreeModelEvent(source1, path1));
		TreeModelEventMatcher matcher2 = new TreeModelEventMatcher(new TreeModelEvent(source1, path1, indices1, children1));
		
		assertTrue(matcher1.matches(new TreeModelEvent(source1, path1)));
		assertFalse(matcher1.matches(new TreeModelEvent(source1, path2)));
		assertFalse(matcher1.matches(new TreeModelEvent(source1, path2)));
		assertFalse(matcher1.matches(new TreeModelEvent(source2, path1)));
		assertFalse(matcher1.matches(new TreeModelEvent(source1, path1, indices1, children1)));
		assertTrue(matcher2.matches(new TreeModelEvent(source1, path1, indices1, children1)));
		assertFalse(matcher2.matches(new TreeModelEvent(source1, path1, indices2, children1)));
		assertFalse(matcher2.matches(new TreeModelEvent(source1, path1, indices1, children2)));
	}
}
