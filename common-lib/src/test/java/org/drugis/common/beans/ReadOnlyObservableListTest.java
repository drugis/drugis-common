package org.drugis.common.beans;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class ReadOnlyObservableListTest {
	private ObservableList<String> d_origStrings;
	private ReadOnlyObservableList<String> d_view;

	@Before
	public void setUp() {
		d_origStrings = new ArrayListModel<String>(Arrays.asList("Jammer", "Helaas", "schade"));
		d_view = new ReadOnlyObservableList<String>(d_origStrings);
	}
	
	@Test
	public void testInit() {
		assertEquals(d_origStrings, d_view);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testCannotSet() {
		d_view.set(0, "hoera");
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testCannotAdd() {
		d_view.add(2, "hoera");
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testCannotRemove() {
		d_view.remove(2);
	}
	
	@Test
	public void testEventsProxied() {
		ListDataListener listener = EasyMock.createStrictMock(ListDataListener.class);
		listener.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_view, ListDataEvent.INTERVAL_ADDED, 2, 2)));
		listener.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_view, ListDataEvent.INTERVAL_REMOVED, 2, 2)));
		listener.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_view, ListDataEvent.CONTENTS_CHANGED, 1, 1)));
		EasyMock.replay(listener);
		d_view.addListDataListener(listener);
		d_origStrings.add(2, "yoipi");
		d_origStrings.remove(2);
		d_origStrings.set(1, "obstinatie");
		EasyMock.verify(listener);
	}
}
