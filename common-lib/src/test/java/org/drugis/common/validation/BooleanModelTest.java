package org.drugis.common.validation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class BooleanModelTest {

	@Test
	public void testValuesBooleanNot() {
		assertNull(new BooleanNotModel(new ValueHolder(null)).getValue());
		assertNull(new BooleanNotModel(new ValueHolder(new Object())).getValue());
	
		assertFalse(new BooleanNotModel(new ValueHolder(true)).getValue());
		assertTrue(new BooleanNotModel(new ValueHolder(false)).getValue());
	}

	@Test
	public void testValuesBooleanAnd() {	
		ValueModel model1 = new ValueHolder(null);
		ValueModel model2 = new ValueHolder(null);
		assertNull(new BooleanAndModel(model1, model2).getValue());
		assertNull(new BooleanAndModel(Arrays.asList(new ValueModel[] {model1, model2})).getValue());
		
		model1.setValue(false);
		assertFalse(new BooleanAndModel(model1, model2).getValue());
		model2.setValue(false);
		assertFalse(new BooleanAndModel(model1, model2).getValue());
		model1.setValue(true);
		model2.setValue(true);
		assertTrue(new BooleanAndModel(model1, model2).getValue());
	}

	@Test
	public void testValuesBooleanOr() {	
		ValueModel model1 = new ValueHolder(null);
		ValueModel model2 = new ValueHolder(null);
		assertNull(new BooleanOrModel(model1, model2).getValue());
		assertNull(new BooleanOrModel(Arrays.asList(new ValueModel[] {model1, model2})).getValue());
		
		model1.setValue(false);
		assertNull(new BooleanOrModel(model1, model2).getValue());
		model2.setValue(false);
		assertFalse(new BooleanOrModel(model1, model2).getValue());
		model2.setValue(true);
		assertTrue(new BooleanOrModel(model1, model2).getValue());
		model1.setValue(true);
		assertTrue(new BooleanOrModel(model1, model2).getValue());
	}
	
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSetValueNotSupported() {
		new BooleanNotModel(new ValueHolder(null)).setValue("");
	}
	
	@Test
	public void testEventChainingNotModel() {
		ValueHolder holder = new ValueHolder(null);
		BooleanNotModel model = new BooleanNotModel(holder);

		PropertyChangeListener mock = JUnitUtil.mockStrictListener(model, "value", null, false);
		model.addValueChangeListener(mock);

		holder.setValue(true);
		verify(mock);

		model.removeValueChangeListener(mock);

		mock = JUnitUtil.mockStrictListener(model, "value", false, true);
		model.addValueChangeListener(mock);

		holder.setValue(false);
		verify(mock);
	}
}


