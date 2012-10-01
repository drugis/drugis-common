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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Predicate;

import com.jgoodies.binding.list.ObservableList;

/**
 * An ObservableList that provides a filtered view on another ObservableList.
 */
public class FilteredObservableList<E> extends AbstractObservableList<E> {
	private final ObservableList<E> d_inner;
	private Predicate<E> d_filter;
	private final ArrayList<Integer> d_indices = new ArrayList<Integer>();

	public FilteredObservableList(final ObservableList<E> inner, final Predicate<E> filter) {
		d_inner = inner;
		d_filter = filter;
		initializeIndices();
		d_inner.addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(final ListDataEvent e) {
				FilteredObservableList.this.intervalRemoved(e.getIndex0(), e.getIndex1());
			}

			@Override
			public void intervalAdded(final ListDataEvent e) {
				FilteredObservableList.this.intervalAdded(e.getIndex0(), e.getIndex1());
			}

			@Override
			public void contentsChanged(final ListDataEvent e) {
				FilteredObservableList.this.contentsChanged(e.getIndex0(), e.getIndex1());
			}
		});
	}

	private void initializeIndices() {
		for (int i = 0; i < d_inner.size(); ++i) {
			if (d_filter.evaluate(d_inner.get(i))) {
				d_indices.add(i);
			}
		}
	}

	public void setFilter(final Predicate<E> filter) {
		d_filter = filter;
		final int oldSize = size();
		if(!isEmpty()) {
			d_indices.clear();
			fireIntervalRemoved(0, oldSize - 1);
		}
		initializeIndices();
		if(!isEmpty()) {
			fireIntervalAdded(0, size() - 1);
		}
	}

	protected <F> int findFirstIndex(final List<F> list, final Predicate<F> filter) {
		for (int i = 0; i < list.size(); ++i) {
			if (filter.evaluate(list.get(i))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public E get(final int index) {
		return d_inner.get(d_indices.get(index));
	}

	@Override
	public void add(final int index, final E element) {
		if(!d_filter.evaluate(element)) throw new IllegalArgumentException("Cannot add " + element + ", it does not pass the filter of " + this);
		if(index < d_indices.size()) {
			d_inner.add(d_indices.get(index), element);
		} else {
			d_inner.add(d_inner.size(), element);
		}
	}

	@Override
	public E set(final int index, final E element) {
		if(!d_filter.evaluate(element)) throw new IllegalArgumentException("Cannot add " + element + ", it does not pass the filter.");
		return d_inner.set(d_indices.get(index), element);
	}

	@Override
	public E remove(final int index) {
		return d_inner.remove((int) d_indices.get(index));
	}

	@Override
	public int size() {
		return d_indices.size();
	}

	private void intervalRemoved(final int lower, final int upper) {
		final int first = firstAtLeast(lower);
		if (first >= d_indices.size()) {
			return; // nothing to remove
		}
		final int last = firstOver(upper);
		d_indices.removeAll(new ArrayList<Integer>(d_indices.subList(first, last)));

		final int delta = upper - lower + 1;
		updateIndices(first, -delta); // decrement indices past removal point

		if (last > first) {
			fireIntervalRemoved(first, last - 1);
		}
	}

	private void intervalAdded(final int lower, final int upper) {
		final int delta = upper - lower + 1;
		final int first = firstAtLeast(lower);
		updateIndices(first, delta); // increment indices past insertion point

		final int oldSize = d_indices.size();
		for(int i = upper; i >= lower; --i) {
			if (d_filter.evaluate(d_inner.get(i))) {
				d_indices.add(first, i);
			}
		}
		final int inserted = d_indices.size() - oldSize;
		if (inserted > 0) {
			fireIntervalAdded(first, first + inserted - 1);
		}
	}


	private void contentsChanged(final int lower, final int upper) {
		for (int i = lower; i <= upper; ++i) {
			elementChanged(i);
		}
	}


	private void elementChanged(final int elm) {
		final int idx = Collections.binarySearch(d_indices, elm);
		if (idx >= 0) {
			if (d_filter.evaluate(d_inner.get(elm))) {
				fireContentsChanged(idx, idx);
			} else {
				d_indices.remove(idx);
				fireIntervalRemoved(idx, idx);
			}
		} else {
			if (d_filter.evaluate(d_inner.get(elm))) {
				d_indices.add(-(idx + 1), elm);
				fireIntervalAdded(-(idx + 1), -(idx + 1));
			} else {
				// no change
			}
		}
	}

	/**
	 * Add a delta to all elements after a certain point.
	 * @param first Index of first element to update
	 * @param delta Value to add to each element
	 */
	private void updateIndices(final int first, final int delta) {
		for(int idx = first; idx < d_indices.size(); ++idx) {
			d_indices.set(idx, d_indices.get(idx) + delta);
		}
	}

	/**
	 * @return The index i of the first item d_indices.get(i) > x, or d_indices.size() if none exists.
	 */
	private int firstOver(final int x) {
		final int last = findFirstIndex(d_indices, new Predicate<Integer>() {
			@Override
			public boolean evaluate(final Integer index) {
				return index > x;
			}
		});
		return last < 0 ? d_indices.size() : last;
	}

	/**
	 * @return The index i of the first item d_indices.get(i) >= x, or d_indices.size() if none exists.
	 */
	private int firstAtLeast(final int x) {
		final int first = findFirstIndex(d_indices, new Predicate<Integer>() {
			@Override
			public boolean evaluate(final Integer index) {
				return index >= x;
			}
		});
		return first < 0 ? d_indices.size() : first;
	}
}
