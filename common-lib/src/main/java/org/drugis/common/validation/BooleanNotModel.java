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

import com.jgoodies.binding.value.ValueModel;

/**
 * Listens to a ValueModel, and converts to true if false and false to true. Remains null if already null, or not a Boolean.
 */
public class BooleanNotModel extends AbstractBooleanModel {
	private static final long serialVersionUID = 8591942709442108053L;
	
	public BooleanNotModel(ValueModel model) {
		super(Arrays.asList(model));
	}

	protected Boolean calc() {
		if (!isBoolean(d_models.get(0))) {
			return null;
		}
		return !((Boolean)d_models.get(0).getValue());
	}
}

