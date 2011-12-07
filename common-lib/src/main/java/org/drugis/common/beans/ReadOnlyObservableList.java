package org.drugis.common.beans;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;

/**
 * Read-only view on an ObservableList. Proxies all events and accessors but doesn't allow modification of the underlying list.
 */
public class ReadOnlyObservableList<E> extends AbstractObservableList<E> {
	private final ObservableList<E> d_nested;

	public ReadOnlyObservableList(ObservableList<E> nested) {
		d_nested = nested;
		d_nested.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				fireIntervalRemoved(e.getIndex0(), e.getIndex1());
			}
			public void intervalAdded(ListDataEvent e) {
				fireIntervalAdded(e.getIndex0(), e.getIndex1());
			}
			public void contentsChanged(ListDataEvent e) {
				fireContentsChanged(e.getIndex0(), e.getIndex1());
			}
		});
	}

	@Override
	public E get(int index) {
		return d_nested.get(index);
	}

	@Override
	public int size() {
		return d_nested.size();
	}
}
