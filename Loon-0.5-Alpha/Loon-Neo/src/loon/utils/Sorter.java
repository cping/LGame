package loon.utils;

import java.util.Comparator;
import java.util.List;

public abstract class Sorter<T> {

	public abstract void sort(final T[] arrays, final int s, final int e, final Comparator<T> c);
	
	public abstract void sort(final List<T> list, final int s, final int e, final Comparator<T> c);

	public abstract void sort(final TArray<T> list, final int s, final int e, final Comparator<T> c);
	
	public final void sort(final T[] arrays, final Comparator<T> c){
		this.sort(arrays, 0, arrays.length, c);
	}

	public final void sort(final List<T> list, final Comparator<T> c){
		this.sort(list, 0, list.size(), c);
	}
	
	public final void sort(final TArray<T> list, final Comparator<T> c){
		this.sort(list, 0, list.size, c);
	}
}
