package org.drugis.common.beans;

import org.drugis.common.event.ListDataEventProxy;

import com.jgoodies.binding.list.ObservableList;

/**
 * Read-only view on an ObservableList. Proxies all events and accessors but doesn't allow modification of the underlying list.
 */
public class ReadOnlyObservableList<E> extends AbstractObservableList<E> {
	private final ObservableList<E> d_nested;

	public ReadOnlyObservableList(ObservableList<E> nested) {
		d_nested = nested;
		d_nested.addListDataListener(new ListDataEventProxy(d_manager));
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
