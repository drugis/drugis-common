package org.drugis.common.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class ToStringValueModel extends AbstractValueModel {
	private static final long serialVersionUID = 8068038905106812227L;
	
	private final ValueModel d_nested;

	public ToStringValueModel(ValueModel nested) {
		d_nested = nested;
		d_nested.addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				fireValueChange(null, getValue());				
			}
		});
	}

	@Override
	public Object getValue() {
		return d_nested.getValue().toString();
	}

	@Override
	public void setValue(Object newValue) {
		d_nested.setValue(newValue);
	}
	
	@Override
	public String toString() {
		return d_nested.getValue().toString();
	}
}
