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

	/**
	 * Java本地的List接口基本都被我删了，只有这个特意留下，用来移植后提供给用户自定义C#和C++中的List排序
	 * 
	 * @param list
	 * @param c
	 */
	public final void sort(final List<T> list, final Comparator<T> c){
		this.sort(list, 0, list.size(), c);
	}
	
	public final void sort(final TArray<T> list, final Comparator<T> c){
		this.sort(list, 0, list.size, c);
	}
}
