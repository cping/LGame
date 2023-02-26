/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

import java.util.Iterator;

public class IntMap<T> implements IArray, Iterable<T> {

	public static class Entry<T> {

		public final long key;
		public T value;

		public Entry(long k, T v) {
			key = k;
			value = v;
		}

		public long getKey() {
			return key;
		}

		public T getValue() {
			return value;
		}

		public T setValue(T newValue) {
			T oldValue = value;
			value = newValue;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Entry<T> e = (Entry<T>) o;
			long k1 = getKey();
			long k2 = e.getKey();
			if (k1 == k2) {
				T v1 = getValue();
				T v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (int) (key ^ (value == null ? 0 : value.hashCode()));
		}

		@Override
		public String toString() {
			return getKey() + "=" + getValue();
		}
	}

	private static final int EMPTY = 0;

	private float loader_factor;

	private boolean locked;
	private int capacity;
	private long[] keysTable;
	private T[] valuesTable;

	public int size;

	public IntMap() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public IntMap(final int capacity) {
		this(MathUtils.nextPowerOfTwo(capacity), 0.85f);
	}

	public IntMap(final int capacity, final float factor) {
		this.loader_factor = factor;
		this.resize(MathUtils.nextPowerOfTwo(capacity));
	}

	public IntMap(final IntMap<T> data) {
		this(data, 0.85f);
	}

	public IntMap(final IntMap<T> data, final float factor) {
		this.loader_factor = factor;
		int neededCapacity = MathUtils.nextPowerOfTwo(data.size);
		if ((float) data.size / neededCapacity > loader_factor) {
			neededCapacity *= 2;
		}
		this.size = data.size;
		this.keysTable = data.keysTable;
		this.valuesTable = data.valuesTable;
		resize(neededCapacity);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof IntMap)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final IntMap<T> other = (IntMap<T>) obj;
		if (other.size != size) {
			return false;
		}
		int found = 0;
		for (int i = 0; found < size; ++i) {
			final long kh = keysTable[i];
			if ((int) kh != 0) {
				final int j = other.find(kh);
				if (j < 0 || !valuesTable[i].equals(other.valuesTable[j])) {
					return false;
				}
				++found;
			}
		}
		return true;
	}

	public int[] keys() {
		final int[] keys = new int[size];
		int found = 0;
		for (int i = 0; found < size; ++i) {
			final long kh = keysTable[i];
			if ((int) kh != 0) {
				keys[found++] = (int) (kh >> 32);
			}
		}
		return keys;
	}

	public Iterable<T> values() {
		return new IntMapIterator<T>(this);
	}

	@Override
	public int size() {
		return size;
	}

	public int capacity() {
		return capacity;
	}

	public boolean containsKey(final Object key) {
		if (key == null) {
			return false;
		}
		return containsKey(key.hashCode());
	}

	public boolean containsKey(final int key) {
		return find(CollectionUtils.getHashKey(key)) >= 0;
	}

	public T get(final Object key) {
		if (key == null) {
			return null;
		}
		return get(key.hashCode(), null);
	}

	public T get(final Object key, final T defaultValue) {
		if (key == null) {
			return null;
		}
		return get(key.hashCode(), defaultValue);
	}

	public T get(final int key) {
		return get(key, null);
	}

	public T get(final int key, final T defaultValue) {
		final int index = find(CollectionUtils.getHashKey(key));
		if (index >= 0) {
			return valuesTable[index];
		}
		return defaultValue;
	}

	public Entry<T>[] getEntrys() {
		@SuppressWarnings("unchecked")
		final Entry<T>[] entrys = new Entry[size];
		int found = 0;
			for (int i = 0; i < capacity; i++) {
				final long key = keysTable[i];
				if (key != EMPTY) {
					entrys[found] = new Entry<T>(key, valuesTable[i]);
					found++;
				}
			}
		return entrys;
	}

	@Override
	public void clear() {
		if (locked) {
			return;
		}
		for (int i = 0; i < capacity; ++i) {
			keysTable[i] = 0;
			valuesTable[i] = null;
		}
		size = 0;
	}

	public void lockArray() {
		locked = true;
	}

	public void unlockArray() {
		locked = false;
	}

	public void put(final Object key, final T value) {
		if (key == null) {
			return;
		}
		put(key.hashCode(), value);
	}

	public void put(final int key, final T value) {
		if (locked) {
			return;
		}
		if (value == null) {
			return;
		}
		if ((float) size / capacity > loader_factor) {
			resize(capacity * 2);
		}
		put(CollectionUtils.getHashKey(key), value);
	}

	public T remove(final int key) {
		if (locked) {
			return null;
		}
		final int index = find(CollectionUtils.getHashKey(key));
		if (index < 0) {
			return null;
		}
		for (int i = 0; i < capacity; ++i) {
			final int curr = (index + i) & (capacity - 1);
			final int next = (index + i + 1) & (capacity - 1);

			final int h = (int) keysTable[next];
			if (h == 0 || findIndex(h, next) == 0) {
				T data = valuesTable[curr];
				keysTable[curr] = 0;
				valuesTable[curr] = null;
				--size;
				return data;
			}
			keysTable[curr] = keysTable[next];
			valuesTable[curr] = valuesTable[next];
		}
		return null;
	}

	private void put(long keyHash, T value) {
		final int startIndex = (int) keyHash & (capacity - 1);
		int probe = 0;
		for (int i = 0; i < capacity; ++i, ++probe) {
			final int index = (startIndex + i) & (capacity - 1);
			final long kh = keysTable[index];
			final int h = (int) kh;
			if (h == 0) {
				keysTable[index] = keyHash;
				valuesTable[index] = value;
				++size;
				return;
			}
			if (kh == keyHash) {
				valuesTable[index] = value;
				return;
			}
			final int d = findIndex(h, index);
			if (probe > d) {
				probe = d;
				long tempHK = keysTable[index];
				T tempVal = valuesTable[index];
				keysTable[index] = keyHash;
				valuesTable[index] = value;
				keyHash = tempHK;
				value = tempVal;
			}
		}
	}

	private int find(final long keyHash) {
		final int startIndex = (int) keyHash & (capacity - 1);
		for (int i = 0; i < capacity; ++i) {
			final int index = (startIndex + i) & (capacity - 1);
			final long kh = keysTable[index];
			if (kh == keyHash) {
				return index;
			}
			final int h = (int) kh;
			if (h == 0) {
				return -1;
			}
			int d = findIndex(h, index);
			if (i > d) {
				return -1;
			}
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private void resize(int newCapacity) {
		if (newCapacity < size) {
			return;
		}

		final int oldSize = size;
		final long[] oldHashKeys = keysTable;
		final T[] oldValues = valuesTable;

		size = 0;
		capacity = newCapacity;
		keysTable = new long[newCapacity];
		valuesTable = (T[]) new Object[newCapacity];

		int found = 0;
		for (int i = 0; found < oldSize; ++i) {
			final long kh = oldHashKeys[i];
			if ((int) kh != 0) {
				put(kh, oldValues[i]);
				++found;
			}
		}
	}

	private int findIndex(int hash, int indexStored) {
		final int startIndex = hash & (capacity - 1);
		if (startIndex <= indexStored) {
			return indexStored - startIndex;
		}
		return indexStored + (capacity - startIndex);
	}

	@Override
	public boolean isEmpty() {
		return valuesTable == null || size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new IntMapIterator<T>(this);
	}

	public static final class IntMapIterator<T> implements LIterator<T>, Iterable<T> {

		int _index = 0;
		int _found = 0;
		IntMap<T> _map;

		IntMapIterator(IntMap<T> map) {
			this._map = map;
		}

		@Override
		public boolean hasNext() {
			return _found < _map.size;
		}

		@Override
		public T next() {
			for (; _index < _map.capacity; ++_index) {
				final T value = _map.valuesTable[_index];
				if (value != null) {
					++_index;
					++_found;
					return value;
				}
			}
			return null;
		}

		@Override
		public void remove() {
		}

		@Override
		public Iterator<T> iterator() {
			return this;
		}
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = size - 1; i > -1; i--) {
			hashCode = 31 * hashCode + (int) keysTable[i];
			hashCode = 31 * hashCode + (valuesTable[i] == null ? 0 : valuesTable[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "[]";
		}
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('[');
		long[] keyTable = this.keysTable;
		T[] valueTable = this.valuesTable;
		int i = keyTable.length;
		while (i-- > 0) {
			long key = keyTable[i];
			if (key == EMPTY)
				continue;
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
			break;
		}
		while (i-- > 0) {
			long key = keyTable[i];
			if (key == EMPTY)
				continue;
			buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

}
