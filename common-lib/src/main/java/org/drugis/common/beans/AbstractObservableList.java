package org.drugis.common.beans;

import java.util.AbstractList;

import javax.swing.event.ListDataListener;

import org.drugis.common.event.ListDataListenerManager;

import com.jgoodies.binding.list.ObservableList;

public abstract class AbstractObservableList<E> extends AbstractList<E> implements ObservableList<E> {
	protected final ListDataListenerManager d_manager = new ListDataListenerManager(this);

	public abstract E get(int index);
	public abstract int size();

	public Object getElementAt(int index) {
		return get(index);
	}

	public int getSize() {
		return size();
	}

	public void addListDataListener(ListDataListener l) {
		d_manager.addListDataListener(l);
	}
	
	public void removeListDataListener(ListDataListener l) {
		d_manager.removeListDataListener(l);
	}
	
	protected void fireIntervalAdded(int index0, int index1) {
		d_manager.fireIntervalAdded(index0, index1);
	}
	
	protected void fireIntervalRemoved(int index0, int index1) {
		d_manager.fireIntervalRemoved(index0, index1);
	}
	
	protected void fireContentsChanged(int index0, int index1) {
		d_manager.fireContentsChanged(index0, index1);
	}
}