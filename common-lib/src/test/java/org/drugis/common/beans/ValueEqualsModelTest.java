package org.drugis.common.beans;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Test;

import com.jgoodies.binding.value.ValueHolder;

public class ValueEqualsModelTest {
	@Test
	public void testEqualsExpected() {
		ValueHolder valueModel = new ValueHolder("name");
		ValueEqualsModel equalsModel = new ValueEqualsModel(valueModel, "name");
		assertEquals(Boolean.TRUE, equalsModel.getValue());

		PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(equalsModel, "value", true, false)));
		EasyMock.replay(listener);

		equalsModel.addPropertyChangeListener(listener);
		valueModel.setValue("naem");
		EasyMock.verify(listener);
	}

	@Test
	public void testChangeExpected() {
		ValueHolder valueModel = new ValueHolder("name");
		ValueEqualsModel equalsModel = new ValueEqualsModel(valueModel, "name");
		assertEquals(Boolean.TRUE, equalsModel.getValue());

		PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(equalsModel, "value", true, false)));
		EasyMock.replay(listener);

		equalsModel.addPropertyChangeListener(listener);
		equalsModel.setExpected(15);
		EasyMock.verify(listener);
	}
}
