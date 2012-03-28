/**
 * 
 */
package org.drugis.common;

import java.beans.PropertyChangeEvent;

import org.easymock.IArgumentMatcher;

class PropertyChangeEventMatcher implements IArgumentMatcher {
	private PropertyChangeEvent d_expected;
	private final boolean d_ignoreValues;

	public PropertyChangeEventMatcher(PropertyChangeEvent expected, boolean ignoreValues) {
		d_expected = expected;
		d_ignoreValues = ignoreValues;
	}

	public void appendTo(StringBuffer buffer) {
		buffer.append("PropertyChangeEventMatcher(");
		buffer.append("source = " + d_expected.getSource() + ", ");
		buffer.append("property = " + d_expected.getPropertyName() + ", ");
		buffer.append("oldValue = " + d_expected.getOldValue() + ", ");
		buffer.append("newValue = " + d_expected.getNewValue() + ")");
	}
	
	public boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	public boolean matches(Object a) {
		if (!(a instanceof PropertyChangeEvent)) {
			return false;
		}
		PropertyChangeEvent actual = (PropertyChangeEvent)a;
		
		return eq(actual.getSource(), d_expected.getSource()) &&
			eq(actual.getPropertyName(), d_expected.getPropertyName()) &&
			( d_ignoreValues || eq(actual.getOldValue(), d_expected.getOldValue()) ) &&
			( d_ignoreValues || eq(actual.getNewValue(), d_expected.getNewValue()) );
	}
}