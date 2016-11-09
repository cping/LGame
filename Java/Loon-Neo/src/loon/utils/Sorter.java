/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
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
