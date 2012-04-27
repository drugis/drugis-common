/*
 * This file is part of drugis.org MTC.
 * MTC is distributed from http://drugis.org/mtc.
 * Copyright (C) 2009-2011 Gert van Valkenhoef.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.common.validation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

/**
 * Listens to two nested ValueModels, and converts to true iff both are true. Converts to null if either one is null, or not a Boolean.
 */
public class BooleanAndModel extends AbstractValueModel {
	private static final long serialVersionUID = 8591942709442108053L;
	private List<ValueModel> d_models;
	private Boolean d_val;
	
	public BooleanAndModel(List<ValueModel> models) {
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

	public BooleanAndModel(ValueModel bool1, ValueModel bool2) {
		this(Arrays.asList(bool1, bool2));
	}

	public Boolean getValue() {
		return d_val;
	}

	public void setValue(Object value) {
		throw new UnsupportedOperationException();
	}

	private boolean isBoolean(ValueModel model) {
		return model.getValue() != null && model.getValue() instanceof Boolean;
	}

	private Boolean calc() {
		for (ValueModel model : d_models) {
			if (!isBoolean(model)) {
				return null;
			} else if (!(Boolean)model.getValue()) {
				return false;
			}
		}
		return true;
	}
}

