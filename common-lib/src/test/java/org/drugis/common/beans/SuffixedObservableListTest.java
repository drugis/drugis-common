package org.drugis.common.beans;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;

public class SuffixedObservableListTest {
	
	private ArrayListModel<MyBean> d_list;
	private SuffixedObservableList<MyBean> d_suffixedList;

	@Before
	public void setUp() { 
		d_list = new ArrayListModel<MyBean>();
		d_list.add(new MyBean("My MyBean!"));
		
		d_suffixedList = new SuffixedObservableList<MyBean>(d_list, new MyBean("Beeeaaaans"));
	}
	
	@Test
	public void testAddElementsFiresChange() {
		ListDataListener mockListenerAdd = createMock(ListDataListener.class);
		mockListenerAdd.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_suffixedList, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mockListenerAdd);
		
		d_suffixedList.addListDataListener(mockListenerAdd);
		
		d_list.add(new MyBean("His MyBean!"));
		verify(mockListenerAdd);
		d_suffixedList.removeListDataListener(mockListenerAdd);
		
		ListDataListener mockListenerRemove = createMock(ListDataListener.class);
		mockListenerRemove.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_suffixedList, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mockListenerRemove);
		
		d_suffixedList.addListDataListener(mockListenerRemove);
		
		d_list.remove(0);
		verify(mockListenerRemove);
		d_suffixedList.removeListDataListener(mockListenerRemove);
	}

}
