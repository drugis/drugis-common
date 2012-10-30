package org.drugis.common.beans;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public class ValueEqualsModel extends AbstractConverter {
	private static final long serialVersionUID = 7371154626359770150L;

	private Object d_expectedValue;

	public ValueEqualsModel(final ValueModel model, final Object expectedValue) {
		super(model);
		d_expectedValue = expectedValue;
	}

	public void setValue(final Object newValue) {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " is read-only");
	}

	public Object convertFromSubject(final Object subjectValue) {
		return EqualsUtil.equal(d_expectedValue, subjectValue);
	}

	public void setExpected(Object expectedValue) {
		Object oldVal = getValue();
		d_expectedValue = expectedValue;
		firePropertyChange("value", oldVal, getValue());
	}
}
