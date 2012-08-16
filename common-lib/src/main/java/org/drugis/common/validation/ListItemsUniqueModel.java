package org.drugis.common.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.drugis.common.beans.ContentAwareListModel;
import org.drugis.common.event.IndifferentListDataListener;

import com.jgoodies.binding.beans.BeanUtils;
import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public class ListItemsUniqueModel<E extends Observable> extends AbstractValueModel {
	private static final long serialVersionUID = -5584835642904300645L;
	private ObservableList<E> d_list;
	private PropertyDescriptor d_propertyDescriptor;
	private boolean d_value = true;
	
	public ListItemsUniqueModel(ObservableList<E> list, Class<E> beanClass, String propertyName) {
		d_list = new ContentAwareListModel<E>(list, new String[] {propertyName});
		try {
			d_propertyDescriptor = new PropertyDescriptor(propertyName, beanClass);
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException(e);
		}
		d_list.addListDataListener(new IndifferentListDataListener() {
			protected void update() {
				boolean oldVal = d_value;
				d_value = calculate();
				fireValueChange(oldVal, d_value);
			}
		});
	}

	@Override
	public Object getValue() {
		return d_value;
	}
	
	public boolean calculate() {
		Set<Object> objects = new HashSet<Object>();
		for(E item : d_list) { 
			Object value = BeanUtils.getValue(item, d_propertyDescriptor);
			if (objects.contains(value)) {
				return false;
			}
			objects.add(value);
		}
		return true;
	}

	@Override
	public void setValue(Object newValue) {
		throw new UnsupportedOperationException("ListItemsUniqueModel is read-only");
	}

}
