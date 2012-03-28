package org.drugis.common;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;

import org.junit.Test;

public class PropertyChangeEventMatcherTest {
	private final Object d_source = new Object();
	private final String d_property = "drie";

	@Test
	public void testIgnoreValues() {
		PropertyChangeEvent expected = new PropertyChangeEvent(d_source, d_property, 0, 1);
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher(expected, true);
		assertFalse(matcher.matches(null));
		assertFalse(matcher.matches(new Object()));
		assertFalse(matcher.matches(new PropertyChangeEvent(new Object(), d_property, 0, 1)));
		assertFalse(matcher.matches(new PropertyChangeEvent(d_source, "3", 0, 1)));
		assertTrue(matcher.matches(new PropertyChangeEvent(d_source, d_property, 1, 0)));
	}

	@Test
	public void testMatcher() {
		PropertyChangeEvent expected = new PropertyChangeEvent(d_source, d_property, 0, 1);
		PropertyChangeEventMatcher matcher = new PropertyChangeEventMatcher(expected, false);
		assertFalse(matcher.matches(null));
		assertFalse(matcher.matches(new Object()));
		assertFalse(matcher.matches(new PropertyChangeEvent(new Object(), d_property, 0, 1)));
		assertFalse(matcher.matches(new PropertyChangeEvent(d_source, "3", 0, 1)));
		assertFalse(matcher.matches(new PropertyChangeEvent(d_source, d_property, 1, 1)));
		assertFalse(matcher.matches(new PropertyChangeEvent(d_source, d_property, 0, 0)));
		assertTrue(matcher.matches(new PropertyChangeEvent(d_source, d_property, 0, 1)));
	}	
}
