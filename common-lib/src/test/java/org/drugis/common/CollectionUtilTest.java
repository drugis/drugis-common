package org.drugis.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class CollectionUtilTest {
	private SortedSet<String> d_set;
	
	@Before
	public void setUp() {
		d_set = new TreeSet<String>();
		d_set.add("B");
		d_set.add("A");
	}

	@Test
	public void testGetElementAtIndex() {
		assertEquals("A", CollectionUtil.getElementAtIndex(d_set, 0));
		assertEquals("B", CollectionUtil.getElementAtIndex(d_set, 1));
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetElementAtIndexTooHigh() {
		CollectionUtil.getElementAtIndex(d_set, 2);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetElementAtIndexNegative() {
		CollectionUtil.getElementAtIndex(d_set, -1);
	}
	
	@Test
	public void testGetIndexOfElement() {
		assertEquals(0, CollectionUtil.getIndexOfElement(d_set, "A"));
		assertEquals(1, CollectionUtil.getIndexOfElement(d_set, "B"));
		assertEquals(-1, CollectionUtil.getIndexOfElement(d_set, "C"));
	}

	@Test
	public void testNextLexicographicElement() {
		int c[] = new int[] {1, 2, 3, 1};
		int x[] = new int[] {0, 0, 0, 0};
		
		assertTrue(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 0, 1, 0}, x);
		assertTrue(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 0, 2, 0}, x);
		assertTrue(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 1, 0, 0}, x);
		assertTrue(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 1, 1, 0}, x);
		assertTrue(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 1, 2, 0}, x);
		assertFalse(CollectionUtil.nextLexicographicElement(x, c));
		assertArrayEquals(new int[] {0, 0, 0, 0}, x);
		
		assertFalse(CollectionUtil.nextLexicographicElement(new int[] { 0 }, new int[] { 1 }));
		assertTrue(CollectionUtil.nextLexicographicElement(new int[] { 0 }, new int[] { 2 }));
	}
}
