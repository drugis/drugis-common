/**
 * 
 */
package org.drugis.common.beans;

import java.util.AbstractList;

import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataEventProxy;

import com.jgoodies.binding.list.ObservableList;

public class TransformedObservableList<A, B> extends AbstractList<B> implements ObservableList<B> {
	public interface Transform<A, B> {
		public B transform(A a);
	}
	
	private final ObservableList<? extends A> d_list;
	private final Transform<A, B> d_transform;
	private final ListDataEventProxy d_listenerManager;

	public TransformedObservableList(ObservableList<? extends A> list, Transform<A, B> transform) {
		d_list = list;
		d_transform = transform;
		d_listenerManager = new ListDataEventProxy(this, d_list);
	}
	
	@Override
	public B get(int index) {
		return d_transform.transform(d_list.get(index));
	}

	@Override
	public int size() {
		return d_list.size();
	}

	public Object getElementAt(int index) {
		return get(index);
	}

	public int getSize() {
		return size();
	}

	public void addListDataListener(ListDataListener l) {
		d_listenerManager.addListDataListener(l);
	}

	public void removeListDataListener(ListDataListener l) {
		d_listenerManager.removeListDataListener(l);
	}
}