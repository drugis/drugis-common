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

import java.util.Arrays;
import java.util.List;

import com.jgoodies.binding.value.ValueModel;

/**
 * Listens to nested ValueModels, and converts to true iff one is true. Converts to null if either one is null, or not a Boolean.
 */
public class BooleanOrModel extends AbstractBooleanModel {
	private static final long serialVersionUID = 8591942709442108053L;
	
	public BooleanOrModel(List<ValueModel> models) {
		super(models);
	}

	public BooleanOrModel(ValueModel bool1, ValueModel bool2) {
		this(Arrays.asList(bool1, bool2));
	}


	protected Boolean calc() {
		for (ValueModel model : d_models) {
			if (!isBoolean(model)) {
				return null;
			} else if ((Boolean)model.getValue()) {
				return true;
			}
		}
		return false;
	}
}

