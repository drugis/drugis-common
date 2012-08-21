package org.drugis.common.beans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.jgoodies.binding.list.ObservableList;

public class AffixedObservableList<E> extends AbstractObservableList<E> {
	public static <E> AffixedObservableList<E> createSuffixed(ObservableList<E> list, E... suffix) {
		return new AffixedObservableList<E>(list, Collections.<E>emptyList(), Arrays.asList(suffix));
	}
	public static <E> AffixedObservableList<E> createPrefixed(ObservableList<E> list, E... prefix) {
		return new AffixedObservableList<E>(list, Arrays.asList(prefix), Collections.<E>emptyList());
	}

	private final ObservableList<E> d_nested;
	private final List<E> d_prefix;
	private final List<E> d_suffix;

	private AffixedObservableList(final ObservableList<E> list, final List<E> prefix, final List<E> suffix) {
		d_nested = list;
		d_prefix = prefix;
		d_suffix = suffix;

		d_nested.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				d_manager.fireIntervalRemoved(d_prefix.size() + e.getIndex0(), d_prefix.size() + e.getIndex1());
			}
			
			public void intervalAdded(ListDataEvent e) {
				d_manager.fireIntervalAdded(d_prefix.size() + e.getIndex0(), d_prefix.size() + e.getIndex1());				
			}
			
			public void contentsChanged(ListDataEvent e) {
				d_manager.fireContentsChanged(d_prefix.size() + e.getIndex0(), d_prefix.size() + e.getIndex1());
			}
		});
	}

	@Override
	public E get(int index) {
		if (index < d_prefix.size()) {
			return d_prefix.get(index);
		}
		index -= d_prefix.size();
		if (index < d_nested.size()) {
			return d_nested.get(index);
		}
		index -= d_nested.size();
		return d_suffix.get(index);
	}

	@Override
	public int size() {
		return d_prefix.size() + d_nested.size() + d_suffix.size();
	}

}
