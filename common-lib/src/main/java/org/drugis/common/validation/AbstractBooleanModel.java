package org.drugis.common.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractBooleanModel extends AbstractValueModel {
	private static final long serialVersionUID = -9103843138797710602L;
	protected Boolean d_val = null;
	protected final List<ValueModel> d_models;
	private PropertyChangeListener d_listener;
	
	public AbstractBooleanModel(List<ValueModel> models) {
		d_models = new ArrayList<ValueModel>(models);
		d_listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				update();
			}
		};
		
		for (ValueModel model : d_models) {
			model.addValueChangeListener(d_listener);
		}
		update();
	}
	
	private void update() {
		Object oldVal = d_val;
		d_val = calc();
		fireValueChange(oldVal, d_val);
	}
	
	public void add(ValueModel vm) {
		if (!d_models.contains(vm)) {
			d_models.add(vm);
			vm.addValueChangeListener(d_listener);
			update();
		}
	}
	
	public void remove(ValueModel vm) {
		boolean removed = d_models.remove(vm);
		if (removed) {
			vm.removeValueChangeListener(d_listener);
			update();
		}
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
