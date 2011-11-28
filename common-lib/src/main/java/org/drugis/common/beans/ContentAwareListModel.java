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

package org.drugis.common.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataListenerManager;

import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.list.ObservableList;

/**
 * ListModel that wraps an ObservableList. Will fire events when the ObservableList does.
 * In addition, it will fireContentsChanged when certain properties of the contained Observables change, or all properties if none are specified.
 */
public class ContentAwareListModel<T extends Observable> extends AbstractList<T> implements ObservableList<T> {
	private static final long serialVersionUID = 8722229007151818730L;
	
	private ObservableList<T> d_nested;
	@SuppressWarnings("unused") private ListPropertyChangeProxy<T> d_proxy;
	private ListDataListenerManager d_manager = new ListDataListenerManager(this);
	private List<String> d_properties;
	
	public ContentAwareListModel(ObservableList<T> list) {
		this(list, null);
	}

	public ContentAwareListModel(ObservableList<T> list, String[] properties) {
		d_nested = list;
		if (properties != null) {
			d_properties = Arrays.asList(properties);
		}

		d_nested.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				d_manager.fireIntervalRemoved(e.getIndex0(), e.getIndex1());
			}
			public void intervalAdded(ListDataEvent e) {
				d_manager.fireIntervalAdded(e.getIndex0(), e.getIndex1());
			}
			public void contentsChanged(ListDataEvent e) {
				d_manager.fireContentsChanged(e.getIndex0(), e.getIndex1());
			}
		});

		d_proxy = new ListPropertyChangeProxy<T>(d_nested, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (d_properties ==  null || d_properties.contains(evt.getPropertyName())) {
					int idx = d_nested.indexOf(evt.getSource());
					d_manager.fireContentsChanged(idx, idx);
				}
			}
		});
	}
	

	@Override
	public T get(int index) {
		return d_nested.get(index);
	}

	@Override
	public int size() {
		return d_nested.size();
	}

	public Object getElementAt(int idx) {
		return d_nested.getElementAt(idx);
	}

	public int getSize() {
		return d_nested.getSize();
	}
	
	public ObservableList<T> getList() {
		return d_nested;
	}


	public void addListDataListener(ListDataListener l) {
		d_manager.addListDataListener(l);
	}

	public void removeListDataListener(ListDataListener l) {
		d_manager.removeListDataListener(l);
	}
	
	@Override
	public void add(int index, T element) {
		d_nested.add(index, element);
	}
	
	@Override
	public T set(int index, T element) {
		return d_nested.set(index, element);
	}
	
	@Override
	public T remove(int index) {
		return d_nested.remove(index);
	}
}
