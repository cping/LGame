package loon.utils;

import java.util.Comparator;
import java.util.List;

public class InsertionSorter<T> extends Sorter<T> {

	@Override
	public void sort(final T[] arrays, final int start, final int end, final Comparator<T> c) {
		final int len = end - start;
		if (len == 2) {
			if (c.compare(arrays[start], arrays[start + 1]) > 0) {
				T x = arrays[start];
				arrays[start] = arrays[start + 1];
				arrays[start + 1] = x;
			}
		} else if (len == 3) {
			if (c.compare(arrays[start], arrays[start + 1]) > 0) {
				T x = arrays[start];
				arrays[start] = arrays[start + 1];
				arrays[start + 1] = x;
			}
			if (c.compare(arrays[start + 1], arrays[start + 2]) > 0) {
				if (c.compare(arrays[start], arrays[start + 2]) > 0) {
					T x = arrays[start + 2];
					arrays[start + 2] = arrays[start + 1];
					arrays[start + 1] = arrays[start];
					arrays[start] = x;
				} else {
					T x = arrays[start + 1];
					arrays[start + 1] = arrays[start + 2];
					arrays[start + 2] = x;
				}
			}
		} else {
			for (int i = start + 1; i < end; i++) {
				final T current = arrays[i];
				T prev = arrays[i - 1];
				if (c.compare(current, prev) < 0) {
					int j = i;
					do {
						arrays[j--] = prev;
					} while (j > start && c.compare(current, prev = arrays[j - 1]) < 0);
					arrays[j] = current;
				}
			}
		}
	}

	@Override
	public void sort(final List<T> list, final int start, final int end, final Comparator<T> c) {
		for (int i = start + 1; i < end; i++) {
			final T current = list.get(i);
			T prev = list.get(i - 1);
			if (c.compare(current, prev) < 0) {
				int j = i;
				do {
					list.set(j--, prev);
				} while (j > start && c.compare(current, prev = list.get(j - 1)) < 0);
				list.set(j, current);
			}
		}
		return;
	}

	@Override
	public void sort(final TArray<T> list, final int start, final int end, final Comparator<T> c) {
		for (int i = start + 1; i < end; i++) {
			final T current = list.get(i);
			T prev = list.get(i - 1);
			if (c.compare(current, prev) < 0) {
				int j = i;
				do {
					list.set(j--, prev);
				} while (j > start && c.compare(current, prev = list.get(j - 1)) < 0);
				list.set(j, current);
			}
		}
		return;
	}
}