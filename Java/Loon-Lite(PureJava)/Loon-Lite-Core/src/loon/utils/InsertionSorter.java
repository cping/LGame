package loon.utils;

import java.util.Comparator;
import java.util.List;

public class InsertionSorter<T> extends Sorter<T> {

	@Override
	public void sort(final T[] arrays, final int start, final int end, final Comparator<T> c) {
		for(int i = start + 1; i <end; i++) {
			final T current = arrays[i];
			T prev = arrays[i - 1];
			if(c.compare(current, prev) < 0) {
				int j = i;
				do {
					arrays[j--] = prev;
				} while(j > start && c.compare(current, prev = arrays[j - 1]) < 0);
				arrays[j] = current;
			}
		}
		return;
	}

	@Override
	public void sort(final List<T> list, final int start, final int end, final Comparator<T> c) {
		for(int i = start + 1; i < end; i++) {
			final T current = list.get(i);
			T prev = list.get(i - 1);
			if(c.compare(current, prev) < 0) {
				int j = i;
				do {
					list.set(j--, prev);
				} while(j > start && c.compare(current, prev = list.get(j - 1)) < 0);
				list.set(j, current);
			}
		}
		return;
	}
	
	@Override
	public void sort(final TArray<T> list, final int start, final int end, final Comparator<T> c) {
		for(int i = start + 1; i < end; i++) {
			final T current = list.get(i);
			T prev = list.get(i - 1);
			if(c.compare(current, prev) < 0) {
				int j = i;
				do {
					list.set(j--, prev);
				} while(j > start && c.compare(current, prev = list.get(j - 1)) < 0);
				list.set(j, current);
			}
		}
		return;
	}
}