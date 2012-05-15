package org.drugis.common.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractBooleanModel extends AbstractValueModel {
	private static final long serialVersionUID = -9103843138797710602L;
	protected Boolean d_val;
	protected final List<ValueModel> d_models;
	
	public AbstractBooleanModel(List<ValueModel> models) {
		d_models = models;
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object oldVal = d_val;
				d_val = calc();
				fireValueChange(oldVal, d_val);
			}
		};
		for (ValueModel model : d_models) {
			model.addValueChangeListener(listener);
		}
		d_val = calc();
	}

	public Boolean getValue() {
		return d_val;
	}

	public void setValue(Object value) {
		throw new UnsupportedOperationException();
	}
	
	protected boolean isBoolean(ValueModel model) {
		return model.getValue() != null && model.getValue() instanceof Boolean;
	}
	
	abstract protected Boolean calc();

}
