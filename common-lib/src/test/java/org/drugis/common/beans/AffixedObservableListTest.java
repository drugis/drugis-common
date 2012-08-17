package org.drugis.common.beans;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;

public class AffixedObservableListTest {
	
	private ArrayListModel<String> d_list;
	private AffixedObservableList<String> d_suffix;
	private AffixedObservableList<String> d_prefix;

	@Before
	public void setUp() { 
		d_list = new ArrayListModel<String>(Arrays.asList("Kidney bean", "Green bean"));
		
		d_suffix = AffixedObservableList.createSuffixed(d_list, "Beeeaaaans", "B");
		d_prefix = AffixedObservableList.createPrefixed(d_list, "Beeeaaaans", "B");
	}
	
	@Test
	public void testSuffixAccess() {
		assertEquals(4, d_suffix.size());
		assertEquals("Kidney bean", d_suffix.get(0));
		assertEquals("Green bean", d_suffix.get(1));
		assertEquals("Beeeaaaans", d_suffix.get(2));
		assertEquals("B", d_suffix.get(3));
	}
	
	@Test
	public void testSuffixListDataEvents() {
		// Test add
		ListDataListener mockListenerAdd = createMock(ListDataListener.class);
		mockListenerAdd.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_suffix, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mockListenerAdd);
		
		d_suffix.addListDataListener(mockListenerAdd);
		
		d_list.add(1, "His MyBean!");
		verify(mockListenerAdd);
		d_suffix.removeListDataListener(mockListenerAdd);
		
		// Test remove
		ListDataListener mockListenerRemove = createMock(ListDataListener.class);
		mockListenerRemove.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_suffix, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mockListenerRemove);
		
		d_suffix.addListDataListener(mockListenerRemove);
		
		d_list.remove(0);
		verify(mockListenerRemove);
		d_suffix.removeListDataListener(mockListenerRemove);
	}
	
	@Test
	public void testPrefixAccess() {
		assertEquals(4, d_prefix.size());
		assertEquals("Kidney bean", d_prefix.get(2));
		assertEquals("Green bean", d_prefix.get(3));
		assertEquals("Beeeaaaans", d_prefix.get(0));
		assertEquals("B", d_prefix.get(1));
	}
	
	@Test
	public void testPrefixListDataEvents() {
		// Test add
		ListDataListener mockListenerAdd = createMock(ListDataListener.class);
		mockListenerAdd.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_prefix, ListDataEvent.INTERVAL_ADDED, 3, 3)));
		replay(mockListenerAdd);
		
		d_prefix.addListDataListener(mockListenerAdd);
		
		d_list.add(1, "His MyBean!");
		verify(mockListenerAdd);
		d_prefix.removeListDataListener(mockListenerAdd);
		
		// Test remove
		ListDataListener mockListenerRemove = createMock(ListDataListener.class);
		mockListenerRemove.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_prefix, ListDataEvent.INTERVAL_REMOVED, 2, 2)));
		replay(mockListenerRemove);
		
		d_prefix.addListDataListener(mockListenerRemove);
		
		d_list.remove(0);
		verify(mockListenerRemove);
		d_prefix.removeListDataListener(mockListenerRemove);
	}
	
}
