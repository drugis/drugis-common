package org.drugis.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionUtil {

	public static <E> E getElementAtIndex(Collection<E> set, int idx) {
		if (idx >= set.size() || idx < 0) {
			throw new IndexOutOfBoundsException();
		}
		Iterator<E> it = set.iterator();
		for (int i = 0; i < idx; ++i) {
			it.next();
		}
		return it.next();
	}

	public static <E> int getIndexOfElement(Collection<E> set, Object child) {
		int i = 0;
		for (E e : set) {
			if (e.equals(child)) {
				return i;
			}
			++i;
		}
		return -1;
	}

	public static boolean containsAllAndOnly(List<?> c1, List<?> c2) {
		if (c1 == null || c2 == null)
			return false;
		if (c1.size() != c2.size())
			return false;
		if (!c1.containsAll(c2))
			return false;
		return true;
	}
}
