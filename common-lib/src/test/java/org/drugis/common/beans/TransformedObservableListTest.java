package org.drugis.common.beans;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.beans.TransformedObservableList.Transform;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class TransformedObservableListTest {
	private ObservableList<String> d_nested;
	private ObservableList<Integer> d_transformed;

	@Before
	public void setUp() {
		d_nested = new ArrayListModel<String>(Arrays.asList("12", "15", "100"));
		d_transformed = new TransformedObservableList<String, Integer>(d_nested, new Transform<String, Integer>() {
			public Integer transform(String s) {
				return Integer.parseInt(s);
			}
		});
	}
	
	@Test
	public void testTransform() {
		assertEquals(Arrays.asList(12, 15, 100), d_transformed);
	}
	
	@Test
	public void testEvents() {
		ListDataListener mock = EasyMock.createStrictMock(ListDataListener.class);
		d_transformed.addListDataListener(mock);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_transformed, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_transformed, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		mock.contentsChanged(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_transformed, ListDataEvent.CONTENTS_CHANGED, 2, 2)));
		EasyMock.replay(mock);
		d_nested.remove(0);
		d_nested.add(1, "18");
		d_nested.set(2, "99");
		d_transformed.removeListDataListener(mock);
		d_nested.add("8");
		EasyMock.verify(mock);
		assertEquals(Arrays.asList(15, 18, 99, 8), d_transformed);
	}
}
