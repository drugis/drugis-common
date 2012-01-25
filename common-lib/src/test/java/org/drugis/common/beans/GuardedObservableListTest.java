package org.drugis.common.beans;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Predicate;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class GuardedObservableListTest {
	private GuardedObservableList<Integer> d_list;

	@Before
	public void setUp() {
		d_list = new GuardedObservableList<Integer>(new ArrayListModel<Integer>(), new Predicate<Number>() {
			public boolean evaluate(Number object) {
				return object.doubleValue() > 0.0;
			}
		});
		d_list.addAll(Arrays.asList(5, 1, 2));
	}

	@Test
	public void testInit() {
		assertEquals(Arrays.asList(5, 1, 2), d_list);
	}
	
	@Test
	public void testModifications() {
		d_list.add(7);
		assertEquals(Arrays.asList(5, 1, 2, 7), d_list);
		d_list.set(0, 2);
		assertEquals(Arrays.asList(2, 1, 2, 7), d_list);
		d_list.remove(0);
		assertEquals(Arrays.asList(1, 2, 7), d_list);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructionWithNonEmptyList() {
		ObservableList<Integer> list = new ArrayListModel<Integer>();
		list.add(1);
		d_list = new GuardedObservableList<Integer>(list, new Predicate<Number>() {
			public boolean evaluate(Number object) {
				return object.doubleValue() > 0.0;
			}
		});
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddBadValue() {
		d_list.add(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetBadValue() {
		d_list.set(1, -1);
	}
	
	@Test
	public void testEvents() {
		ListDataListener mockListener = createMock(ListDataListener.class);
		mockListener.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_list, ListDataEvent.INTERVAL_ADDED, 3, 3)));
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_list, ListDataEvent.CONTENTS_CHANGED, 2, 2)));
		mockListener.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_list, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mockListener);
		
		d_list.addListDataListener(mockListener);
		d_list.add(7);
		d_list.set(2, 2);
		d_list.remove(0);
		verify(mockListener);
	}
	
}
