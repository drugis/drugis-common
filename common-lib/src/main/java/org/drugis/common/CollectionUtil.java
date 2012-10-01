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

	/**
	 * Let x[] be an array where 0 <= x[i] < c[i].
	 * Increment x[] to the lexicographically next item.
	 * If x[] is the lexicographically largest possible array, return false.
	 */
	public static boolean nextLexicographicElement(int x[], int c[]) {
		assert(x.length == c.length);
		final int l = x.length;
		if (l < 1) {
			return false;
		}
		
		// iterate over the elements starting at the end, until we find an element that can be incremented.
		for (int i = l - 1; i >= 0; --i) {
			++x[i];
			if (x[i] == c[i]) {
				x[i] = 0;
			} else {
				return true; // greater permutation found
			}
		}
		return false; // no greater permutation found (and array reset to {0, ...})
	}
}
