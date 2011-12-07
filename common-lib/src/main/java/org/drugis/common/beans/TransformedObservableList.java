/**
 * 
 */
package org.drugis.common.beans;

import org.drugis.common.event.ListDataEventProxy;

import com.jgoodies.binding.list.ObservableList;

public class TransformedObservableList<A, B> extends AbstractObservableList<B> {
	public interface Transform<A, B> {
		public B transform(A a);
	}
	
	private final ObservableList<? extends A> d_list;
	private final Transform<A, B> d_transform;

	public TransformedObservableList(ObservableList<? extends A> list, Transform<A, B> transform) {
		d_list = list;
		d_transform = transform;
		d_list.addListDataListener(new ListDataEventProxy(d_manager));
	}
	
	@Override
	public B get(int index) {
		return d_transform.transform(d_list.get(index));
	}

	@Override
	public int size() {
		return d_list.size();
	}
}