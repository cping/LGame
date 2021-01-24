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
import java.util.Iterator;

import loon.LSysException;
import loon.events.QueryEvent;
import loon.utils.ObjectMap.Keys;
import loon.utils.ObjectMap.Values;

@SuppressWarnings({ "unchecked" })
public class TArray<T> implements Iterable<T>, IArray {

	public final static class ArrayIterable<T> implements Iterable<T> {

		private final TArray<T> array;
		private final boolean allowRemove;
		private ArrayIterator<T> iterator1, iterator2;

		public ArrayIterable(TArray<T> array) {
			this(array, true);
		}

		public ArrayIterable(TArray<T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator1 == null) {
				iterator1 = new ArrayIterator<T>(array, allowRemove);
				iterator2 = new ArrayIterator<T>(array, allowRemove);
			}
			if (!iterator1.valid) {
				iterator1.index = 0;
				iterator1.valid = true;
				iterator2.valid = false;
				return iterator1;
			}
			iterator2.index = 0;
			iterator2.valid = true;
			iterator1.valid = false;
			return iterator2;
		}
	}

	public final static class ArrayIterator<T> implements LIterator<T>, Iterable<T> {

		private final TArray<T> array;
		private final boolean allowRemove;
		int index;
		boolean valid = true;

		public ArrayIterator(TArray<T> array) {
			this(array, true);
		}

		public ArrayIterator(TArray<T> array, boolean allowRemove) {
			this.array = array;
			this.allowRemove = allowRemove;
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new LSysException("iterator() cannot be used nested.");
			}
			return index < array.size;
		}

		@Override
		public T next() {
			if (index >= array.size) {
				return null;
			}
			if (!valid) {
				throw new LSysException("iterator() cannot be used nested.");
			}
			return array.items[index++];
		}

		@Override
		public void remove() {
			if (!allowRemove) {
				throw new LSysException("Remove not allowed.");
			}
			index--;
			array.removeIndex(index);
		}

		public void reset() {
			index = 0;
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
	}

	public static final <T> TArray<T> at(int capacity) {
		return new TArray<T>(capacity);
	}

	public static final <T> TArray<T> at(TArray<? extends T> array) {
		return new TArray<T>(array);
	}

	public static final <T> TArray<T> at() {
		return at(0);
	}

	public final static <T> TArray<T> with(T... array) {
		return new TArray<T>(array);
	}

	public T[] items;

	public int size;
	
	public boolean ordered;

	public TArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public TArray(int capacity) {
		this(true, capacity);
	}

	public TArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = (T[]) new Object[capacity];
	}

	public TArray(TArray<? extends T> array) {
		this(array.ordered, array.size);
		size = array.size;
		System.arraycopy(array.items, 0, items, 0, size);
	}

	public TArray(T... array) {
		this(true, array, 0, array.length);
	}

	protected TArray(int size, T... array) {
		this(true, array, 0, size);
	}

	public TArray(boolean ordered, T[] array, int start, int count) {
		this(ordered, count);
		size = count;
		System.arraycopy(array, start, items, 0, size);
	}

	public TArray(SortedList<T> vals) {
		this();
		for (LIterator<T> it = vals.listIterator(); it.hasNext();) {
			add(it.next());
		}
	}

	public TArray(Array<T> vals) {
		this();
		for (; vals.hashNext();) {
			add(vals.next());
		}
		vals.stopNext();
	}

	public TArray(Keys<T> vals) {
		this();
		for (T t : vals) {
			add(t);
		}
	}

	public TArray(Values<T> vals) {
		this();
		for (T t : vals) {
			add(t);
		}
	}

	public boolean add(T value) {
		T[] items = this.items;
		if (size == items.length) {
			items = resize(MathUtils.max(8, (int) (size * 1.75f)));
		}
		items[size++] = value;
		return true;
	}

	public void addAll(TArray<? extends T> array) {
		addAll(array, 0, array.size);
	}

	public void addAll(TArray<? extends T> array, int start, int count) {
		if (start + count > array.size) {
			throw new LSysException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
		}
		addAll(array.items, start, count);
	}

	public void addAll(T... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(T[] array, int start, int count) {
		T[] items = this.items;
		int sizeNeeded = size + count;
		if (sizeNeeded > items.length) {
			items = resize(MathUtils.max(8, (int) (sizeNeeded * 1.75f)));
		}
		System.arraycopy(array, start, items, size, count);
		size += count;
	}

	public T get(int index) {
		if (index >= size)
			throw new LSysException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	public void set(int index, T value) {
		if (index >= size)
			throw new LSysException("index can't be >= size: " + index + " >= " + size);
		items[index] = value;
	}

	public void insert(int index, T value) {
		if (index > size)
			throw new LSysException("index can't be > size: " + index + " > " + size);
		T[] items = this.items;
		if (size == items.length)
			items = resize(MathUtils.max(8, (int) (size * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, size - index);
		else
			items[size] = items[index];
		size++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= size)
			throw new LSysException("first can't be >= size: " + first + " >= " + size);
		if (second >= size)
			throw new LSysException("second can't be >= size: " + second + " >= " + size);
		T[] items = this.items;
		T firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(T value) {
		return contains(value, false);
	}

	public boolean contains(T value, boolean identity) {
		T[] items = this.items;
		int i = size - 1;
		if (identity || value == null) {
			while (i >= 0)
				if (items[i--] == value)
					return true;
		} else {
			while (i >= 0)
				if (value.equals(items[i--]))
					return true;
		}
		return false;
	}

	public int indexOf(T value) {
		return indexOf(value, false);
	}

	public int indexOf(T value, boolean identity) {
		T[] items = this.items;
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++)
				if (items[i] == value)
					return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (value.equals(items[i]))
					return i;
		}
		return -1;
	}

	public int lastIndexOf(T value, boolean identity) {
		T[] items = this.items;
		if (identity || value == null) {
			for (int i = size - 1; i >= 0; i--)
				if (items[i] == value)
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (value.equals(items[i]))
					return i;
		}
		return -1;
	}

	public boolean removeValue(T value) {
		T[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (items[i] == value || value.equals(items[i])) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeValue(T value, boolean identity) {
		T[] items = this.items;
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (items[i] == value) {
					removeIndex(i);
					return true;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (value.equals(items[i])) {
					removeIndex(i);
					return true;
				}
			}
		}
		return false;
	}

	public T removeIndex(int index) {
		if (index >= size)
			throw new LSysException("index can't be >= size: " + index + " >= " + size);
		T[] items = this.items;
		T value = (T) items[index];
		size--;
		if (ordered)
			System.arraycopy(items, index + 1, items, index, size - index);
		else
			items[index] = items[size];
		items[size] = null;
		return value;
	}

	public void removeRange(int start, int end) {
		if (end >= size)
			throw new LSysException("end can't be >= size: " + end + " >= " + size);
		if (start > end)
			throw new LSysException("start can't be > end: " + start + " > " + end);
		T[] items = this.items;
		int count = end - start + 1;
		if (ordered)
			System.arraycopy(items, start + count, items, start, size - (start + count));
		else {
			int lastIndex = this.size - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		size -= count;
	}

	public boolean remove(T value) {
		return remove(value, false);
	}

	public boolean remove(T value, boolean identity) {
		T[] items = this.items;
		if (identity || value == null) {
			for (int i = 0; i < size; i++) {
				if (items[i] == value) {
					removeIndex(i);
					return true;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (value.equals(items[i])) {
					removeIndex(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeAll(TArray<? extends T> array) {
		return removeAll(array, false);
	}

	public boolean removeAll(TArray<? extends T> array, boolean identity) {
		if (array.size == 0) {
			return true;
		}
		int size = this.size;
		int startSize = size;
		T[] items = this.items;
		if (identity) {
			for (int i = 0, n = array.size; i < n; i++) {
				T item = array.get(i);
				for (int ii = 0; ii < size; ii++) {
					if (item == items[ii]) {
						removeIndex(ii);
						size--;
						break;
					}
				}
			}
		} else {
			for (int i = 0, n = array.size; i < n; i++) {
				T item = array.get(i);
				for (int ii = 0; ii < size; ii++) {
					if (item.equals(items[ii])) {
						removeIndex(ii);
						size--;
						break;
					}
				}
			}
		}
		return size != startSize;
	}

	public T pop() {
		if (size == 0)
			throw new LSysException("TArray is empty.");
		--size;
		T item = items[size];
		items[size] = null;
		return item;
	}

	public T peek() {
		if (size == 0)
			throw new LSysException("TArray is empty.");
		return items[size - 1];
	}

	public T first() {
		if (size == 0)
			throw new LSysException("TArray is empty.");
		return items[0];
	}

	@Override
	public void clear() {
		T[] items = this.items;
		for (int i = 0, n = size; i < n; i++)
			items[i] = null;
		size = 0;
	}

	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	public T[] shrink() {
		if (items.length != size)
			resize(size);
		return items;
	}

	public T[] ensureCapacity(int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded > items.length)
			resize(MathUtils.max(8, sizeNeeded));
		return items;
	}

	protected T[] resize(int newSize) {
		T[] items = this.items;
		T[] newItems = (T[]) new Object[newSize];
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(size, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public TArray<T> reverse() {
		T[] items = this.items;
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			T temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
		return this;
	}

	public TArray<T> shuffle() {
		T[] items = this.items;
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			T temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
		return this;
	}

	public TArray<T> unshift(T o) {
		T[] items = this.items;
		int len = items.length;
		T[] newItems = (T[]) new Object[len + 1];
		newItems[0] = o;
		System.arraycopy(items, 0, newItems, 1, items.length);
		this.items = newItems;
		this.size++;
		return this;
	}

	public void truncate(int newSize) {
		if (size <= newSize)
			return;
		for (int i = newSize; i < size; i++)
			items[i] = null;
		size = newSize;
	}

	public T last() {
		return items[size < 1 ? 0 : size - 1];
	}

	public T removeFirst() {
		return removeIndex(0);
	}

	public T removeLast() {
		return removeIndex(size < 1 ? 0 : size - 1);
	}

	public T random() {
		if (size == 0)
			return null;
		return items[MathUtils.random(0, size - 1)];
	}

	public TArray<T> randomArrays() {
		if (size == 0) {
			return new TArray<T>();
		}
		T v = null;
		TArray<T> newArrays = new TArray<T>(size);
		for (int i = 0; i < size; i++) {
			newArrays.add(get(i));
		}
		for (int i = 0; i < size; i++) {
			v = random();
			for (int j = 0; j < i; j++) {
				if (newArrays.get(j) == v) {
					v = random();
					j = -1;
				}

			}
			newArrays.set(i, v);
		}
		return newArrays;
	}

	public Object[] toArray() {
		Object[] result = new Object[size];
		System.arraycopy(items, 0, result, 0, size);
		return result;
	}

	public T[] toArray(T[] a) {
		int length = this.size;
		if (a.length < size) {
			a = (T[]) new Object[length];
		}
		Object[] result = a;
		for (int i = 0; i < length; ++i) {
			result[i] = get(i);
		}
		if (a.length > length) {
			a[length] = null;
		}
		return a;
	}

	public TArray<T> cpy() {
		return new TArray<T>(this);
	}

	private ArrayIterable<T> _iterable;

	@Override
	public Iterator<T> iterator() {
		if (_iterable == null) {
			_iterable = new ArrayIterable<T>(this);
		}
		return _iterable.iterator();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TArray))
			return false;
		TArray<?> array = (TArray<?>) o;
		int n = size;
		if (n != array.size)
			return false;
		Object[] items1 = this.items;
		Object[] items2 = array.items;
		for (int i = 0; i < n; i++) {
			Object o1 = items1[i];
			Object o2 = items2[i];
			if (!(o1 == null ? o2 == null : o1.equals(o2)))
				return false;
		}
		return true;
	}

	public TArray<T> concat(TArray<? extends T> array) {
		TArray<T> all = new TArray<T>(this);
		all.addAll(array);
		return all;
	}

	public TArray<T> where(QueryEvent<T> test) {
		TArray<T> list = new TArray<T>();
		for (T t : this) {
			if (test.hit(t)) {
				list.add(t);
			}
		}
		return list;
	}

	public T find(QueryEvent<T> test) {
		for (T t : this) {
			if (test.hit(t)) {
				return t;
			}
		}
		return null;
	}

	public boolean remove(QueryEvent<T> test) {
		for (T t : this) {
			if (test.hit(t)) {
				return remove(t);
			}
		}
		return false;
	}

	public void sort(Comparator<T> compar) {
		if (size <= 1) {
			return;
		}
		T[] obj = CollectionUtils.copyOf(items, 0, size);
		SortUtils.quickSort(obj, compar);
		int count = 0;
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				items[count++] = obj[i];
			}
		}
	}

	public boolean retainAll(TArray<T> array) {
		final T[] elementData = this.items;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++)
				if (array.contains(elementData[r])) {
					elementData[w++] = elementData[r];
				}
		} finally {
			if (r != size) {
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				for (int i = w; i < size; i++) {
					elementData[i] = null;
				}
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	public SwappableArray<T> GetSwappableArray() {
		return new SwappableArray<T>(this);
	}

	@Override
	public int hashCode() {
		if (!ordered) {
			return super.hashCode();
		}
		int hashCode = 1;
		for (int i = size - 1; i > -1; i--) {
			hashCode = 31 * hashCode + (items[i] == null ? 0 : items[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char separator) {
		if (size == 0)
			return "[]";
		T[] items = this.items;
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(separator);
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}
}
