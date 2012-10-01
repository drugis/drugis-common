/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataListenerManager;

import com.jgoodies.binding.list.ObservableList;

public class SortedSetModel<E> extends AbstractList<E> implements ObservableList<E> {
	private final SortedSet<E> d_set;
	private final ListDataListenerManager d_listenerManager = new ListDataListenerManager(this);

	public SortedSetModel() {
		d_set = new TreeSet<E>();
	}
	
	public SortedSetModel(Comparator<? super E> comparator) {
		d_set = new TreeSet<E>(comparator);
	}

	public SortedSetModel(Collection<? extends E> c) {
		d_set = new TreeSet<E>(c);
	}

	//// List<E> methods

	@Override
	public int size() {
		return d_set.size();
	}
	
	@Override
	public E get(int index) {
		int i = 0;
		for (E e : d_set) {
			if (i == index) {
				return e;
			}
			++i;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void add(int index, E element) {
		if (!d_set.contains(element)) {
			d_set.add(element);
			int idx = indexOf(element);
			d_listenerManager.fireIntervalAdded(idx, idx);
		}
	}

	@Override
    public E remove(int index) {
		if (index >= 0 && index < size()) {
			E e = get(index);
			d_set.remove(e);
			d_listenerManager.fireIntervalRemoved(index, index);
			return e;
		}
		throw new IndexOutOfBoundsException();
    }
	
	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index >= 0) {
			remove(index);
			return true;
		}
		return false;
	}
	
	//// ListModel methods
	public int getSize() {
		return size();
	}

	public Object getElementAt(int index) {
		return get(index);
	}

	public void addListDataListener(ListDataListener l) {
		d_listenerManager.addListDataListener(l);
	}
	
	public void removeListDataListener(ListDataListener l) {
		d_listenerManager.removeListDataListener(l);
	}
	
	public SortedSet<E> getSet() {
		return new TreeSet<E>(d_set);
	}
}
