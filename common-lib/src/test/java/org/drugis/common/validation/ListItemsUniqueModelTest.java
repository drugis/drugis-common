package org.drugis.common.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.ValueHolder;

public class ListItemsUniqueModelTest {

	@Test
	public void testValidation() {
		ObservableList<ValueHolder> list = new ArrayListModel<ValueHolder>();
		list.add(new ValueHolder("A"));
		list.add(new ValueHolder("B"));
		
		ListItemsUniqueModel<ValueHolder> unique = new ListItemsUniqueModel<ValueHolder>(list, ValueHolder.class, "value");
		assertTrue((Boolean)unique.getValue());
		
		ValueHolder v = new ValueHolder("A");
		list.add(v);
		assertFalse((Boolean)unique.getValue());
		
		v.setValue("C");
		assertTrue((Boolean)unique.getValue());
	}
	
	@Test
	public void testEventsOnAddRemove() {
		ObservableList<ValueHolder> list = new ArrayListModel<ValueHolder>();
		list.add(new ValueHolder("A"));
		ListItemsUniqueModel<ValueHolder> unique = new ListItemsUniqueModel<ValueHolder>(list, ValueHolder.class, "value");

		PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(unique, "value", true, false)));
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(unique, "value", false, true)));
		EasyMock.replay(listener);
		
		unique.addPropertyChangeListener(listener);
		
		list.add(new ValueHolder("B"));
		list.add(new ValueHolder("A"));
		list.remove(0);
		
		EasyMock.verify(listener);
	}
	
	@Test
	public void testEventsOnPropertyChange() {
		ObservableList<ValueHolder> list = new ArrayListModel<ValueHolder>();
		ValueHolder v1 = new ValueHolder("A");
		list.add(v1);
		ValueHolder v2 = new ValueHolder("B");
		list.add(v2);
		ListItemsUniqueModel<ValueHolder> unique = new ListItemsUniqueModel<ValueHolder>(list, ValueHolder.class, "value");

		PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(unique, "value", true, false)));
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(
				new PropertyChangeEvent(unique, "value", false, true)));
		EasyMock.replay(listener);
		
		unique.addPropertyChangeListener(listener);

		v1.setValue("B");
		v1.setValue("Bar");
		
		EasyMock.verify(listener);
	}
	
}
