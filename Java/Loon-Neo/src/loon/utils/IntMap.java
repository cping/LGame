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

import loon.LSysException;

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

	private boolean _locked;

	private int _capacity;

	private long[] _keysTable;

	private T[] _valuesTable;

	private IntMapIterator<T> _mapIterator1, _mapIterator2;

	public int size;

	public IntMap() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public IntMap(final int _capacity) {
		this(MathUtils.nextPowerOfTwo(_capacity), 0.85f);
	}

	public IntMap(final int _capacity, final float factor) {
		this.loader_factor = factor;
		this.resize(MathUtils.nextPowerOfTwo(_capacity));
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
		this._keysTable = data._keysTable;
		this._valuesTable = data._valuesTable;
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
			final long kh = _keysTable[i];
			if ((int) kh != 0) {
				final int j = other.find(kh);
				if (j < 0 || !_valuesTable[i].equals(other._valuesTable[j])) {
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
			final long kh = _keysTable[i];
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

	public int _capacity() {
		return _capacity;
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
			return _valuesTable[index];
		}
		return defaultValue;
	}

	public Entry<T>[] getEntrys() {
		@SuppressWarnings("unchecked")
		final Entry<T>[] entrys = new Entry[size];
		int found = 0;
		for (int i = 0; i < _capacity; i++) {
			final long key = _keysTable[i];
			if (key != EMPTY) {
				entrys[found] = new Entry<T>(key, _valuesTable[i]);
				found++;
			}
		}
		return entrys;
	}

	@Override
	public void clear() {
		if (_locked) {
			return;
		}
		for (int i = 0; i < _capacity; ++i) {
			_keysTable[i] = 0;
			_valuesTable[i] = null;
		}
		size = 0;
	}

	public void lockArray() {
		_locked = true;
	}

	public void unlockArray() {
		_locked = false;
	}

	public void put(final Object key, final T value) {
		if (key == null) {
			return;
		}
		put(key.hashCode(), value);
	}

	public void put(final int key, final T value) {
		if (_locked) {
			return;
		}
		if (value == null) {
			return;
		}
		if ((float) size / _capacity > loader_factor) {
			resize(_capacity * 2);
		}
		put(CollectionUtils.getHashKey(key), value);
	}

	public T remove(final int key) {
		if (_locked) {
			return null;
		}
		final int index = find(CollectionUtils.getHashKey(key));
		if (index < 0) {
			return null;
		}
		return removeIndex(index);
	}

	public T removeIndex(final int index) {
		if (_locked) {
			return null;
		}
		for (int i = 0; i < _capacity; ++i) {
			final int curr = (index + i) & (_capacity - 1);
			final int next = (index + i + 1) & (_capacity - 1);
			final int h = (int) _keysTable[next];
			if (h == 0 || findIndex(h, next) == 0) {
				T data = _valuesTable[curr];
				_keysTable[curr] = 0;
				_valuesTable[curr] = null;
				--size;
				return data;
			}
			_keysTable[curr] = _keysTable[next];
			_valuesTable[curr] = _valuesTable[next];
		}
		return null;
	}

	private void put(long keyHash, T value) {
		final int startIndex = (int) keyHash & (_capacity - 1);
		int probe = 0;
		for (int i = 0; i < _capacity; ++i, ++probe) {
			final int index = (startIndex + i) & (_capacity - 1);
			final long kh = _keysTable[index];
			final int h = (int) kh;
			if (h == 0) {
				_keysTable[index] = keyHash;
				_valuesTable[index] = value;
				++size;
				return;
			}
			if (kh == keyHash) {
				_valuesTable[index] = value;
				return;
			}
			final int d = findIndex(h, index);
			if (probe > d) {
				probe = d;
				long tempHK = _keysTable[index];
				T tempVal = _valuesTable[index];
				_keysTable[index] = keyHash;
				_valuesTable[index] = value;
				keyHash = tempHK;
				value = tempVal;
			}
		}
	}

	private int find(final long keyHash) {
		final int startIndex = (int) keyHash & (_capacity - 1);
		for (int i = 0; i < _capacity; ++i) {
			final int index = (startIndex + i) & (_capacity - 1);
			final long kh = _keysTable[index];
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
		final long[] oldHashKeys = _keysTable;
		final T[] oldValues = _valuesTable;

		size = 0;
		_capacity = newCapacity;
		_keysTable = new long[newCapacity];
		_valuesTable = (T[]) new Object[newCapacity];

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
		final int startIndex = hash & (_capacity - 1);
		if (startIndex <= indexStored) {
			return indexStored - startIndex;
		}
		return indexStored + (_capacity - startIndex);
	}

	@Override
	public boolean isEmpty() {
		return _valuesTable == null || size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		if (_mapIterator1 == null) {
			_mapIterator1 = new IntMapIterator<T>(this);
			_mapIterator2 = new IntMapIterator<T>(this);
		}
		if (!_mapIterator1._valid) {
			_mapIterator1.reset();
			_mapIterator1._valid = true;
			_mapIterator2._valid = false;
			return _mapIterator1;
		}
		_mapIterator2.reset();
		_mapIterator2._valid = true;
		_mapIterator1._valid = false;
		return _mapIterator2;
	}

	public static final class IntMapIterator<T> implements LIterator<T>, Iterable<T> {

		public boolean _valid;
		int _index = 0;
		int _found = 0;
		IntMap<T> _map;

		IntMapIterator(IntMap<T> map) {
			this._map = map;
			this.reset();
		}

		public void reset() {
			this._index = 0;
			this._found = 0;
		}

		@Override
		public boolean hasNext() {
			if (!_valid) {
				return false;
			}
			return _found < _map.size;
		}

		@Override
		public T next() {
			if (!_valid) {
				return null;
			}
			for (; _index < _map._capacity; ++_index) {
				final T[] values = _map._valuesTable;
				if (_index < values.length) {
					final T value = values[_index];
					if (value != null) {
						++_index;
						++_found;
						return value;
					}
				}
			}
			return null;
		}

		@Override
		public void remove() {
			int i = _found;
			if (i < 0) {
				throw new LSysException("next must be called before remove.");
			} else {
				final long hashCode = _map._keysTable[i];
				final int idx = _map.find(hashCode);
				if (idx < 0) {
					return;
				}
				_map.removeIndex(idx);
			}
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
			hashCode = 31 * hashCode + (int) _keysTable[i];
			hashCode = 31 * hashCode + (_valuesTable[i] == null ? 0 : _valuesTable[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "[]";
		}
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		long[] keyTable = this._keysTable;
		T[] valueTable = this._valuesTable;
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
