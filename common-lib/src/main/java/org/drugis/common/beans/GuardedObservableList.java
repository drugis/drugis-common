package org.drugis.common.beans;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.collections15.Predicate;

import com.jgoodies.binding.list.ObservableList;

/**
 * An ObservableList that restricts the allowed elements to only those
 * for which the given Predicate is true.
 */
public class GuardedObservableList<E> extends AbstractObservableList<E> {
	ObservableList<E> d_nested;
	private final Predicate<? super E> d_predicate;
	
	/**
	 * Wrap a guard around the nested list to restrict what elements can be added.
	 * @param nested An empty ObservableList
	 * @param predicate The predicate that all objects in this list must satisfy.
	 */
	public GuardedObservableList(ObservableList<E> nested, Predicate<? super E> predicate) {
		if (!nested.isEmpty()) {
			throw new IllegalArgumentException("Initial nested list must be empty.");
		}
		
		d_nested = nested;
		d_predicate = predicate;
		
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
	
	@Override
	public void add(int index, E element) {
		check(element);
		d_nested.add(index, element);
	}

	@Override
	public E set(int index, E element) {
		check(element);
		return d_nested.set(index, element);
	}
	
	@Override
	public E remove(int index) {
		return d_nested.remove(index);
	}
	
	private void check(E element) {
		if (!d_predicate.evaluate(element)) {
			throw new IllegalArgumentException("The element " + element + " does not fulfill the guard predicate: " + d_predicate + ".");
		}
	}
}
