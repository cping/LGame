/**
 * 
 * Copyright 2008 - 2009
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
 * @version 0.1.1
 */
package loon.utils;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;

public class ArrayMap implements IArray, LRelease {

	public static class Entry {

		protected int _index;

		protected int _hashCode;

		protected Object _key;

		protected Object _value;

		protected Entry _next;

		protected Entry(int hashCode, Object key, Object v, Entry next) {
			this(-1, hashCode, key, v, next);
		}

		public Entry(int index, int hashCode, Object key, Object v, Entry next) {
			this._index = index;
			this._hashCode = hashCode;
			this._key = key;
			this._value = v;
			this._next = next;
		}

		public Object getKey() {
			return _key;
		}

		public Object getValue() {
			return _value;
		}

		public Object setValue(Object v) {
			Object oldValue = v;
			this._value = v;
			return oldValue;
		}

		public int getIndex() {
			return this._index;
		}

		protected void clear() {
			_key = null;
			_value = null;
			_next = null;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (this == o) {
				return true;
			}
			if (o instanceof Entry) {
				Entry e = (Entry) o;
				return (_key != null ? _key.equals(e._key) : e._key == null)
						&& (_value != null ? _value.equals(e._value) : e._value == null);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return _hashCode;
		}

		@Override
		public String toString() {
			return _key + "=" + _value;
		}

	}

	private int _threshold;

	private Entry[] _keysTable;

	private Entry[] _valuesTable;

	private int _size = 0;

	private float _loadFactor;

	private int _removed = 0;

	public ArrayMap() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public ArrayMap(int initialCapacity) {
		this(initialCapacity, 0.85f);
	}

	public ArrayMap(ArrayMap map) {
		this();
		putAll(map);
	}

	public ArrayMap(int initialCapacity, float factor) {
		if (initialCapacity <= 0) {
			initialCapacity = CollectionUtils.INITIAL_CAPACITY;
		}
		this._keysTable = new Entry[initialCapacity];
		this._valuesTable = new Entry[initialCapacity];
		this._threshold = (int) (initialCapacity * factor);
		this._loadFactor = factor;
	}

	@Override
	public int size() {
		return _size;
	}

	@Override
	public boolean isEmpty() {
		return _size == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean containsValue(Object v) {
		return indexOf(v) >= 0;
	}

	protected int indexOf(Entry entry) {
		if (entry != null) {
			Entry value;
			int start = 0;
			int len = _size - 1;
			for (; start <= len;) {
				int mid = start + (len - start) / 2;
				value = _valuesTable[mid];
				if (entry._index < value._index) {
					len = mid - 1;
				} else if (entry._index > value._index) {
					start = mid + 1;
				} else {
					if (entry == value) {
						return mid;
					} else {
						break;
					}
				}
			}
			for (int i = 0; i < _size; i++) {
				value = _valuesTable[i];
				if (value == entry) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < _size; i++) {
				if (_valuesTable[i] == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public int indexOf(Object v) {
		if (v != null) {
			Object data = null;
			for (int i = 0; i < _size; i++) {
				data = _valuesTable[i]._value;
				if (data == v || data.equals(v)) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < _size; i++) {
				if (_valuesTable[i]._value == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean containsKey(Object key) {
		Entry[] table = _keysTable;
		if (key != null) {
			int hashCode = CollectionUtils.getLimitHash(key.hashCode());
			int index = (hashCode & 0x7FFFFFFF) % table.length;
			for (Entry e = table[index]; e != null; e = e._next) {
				if (e._hashCode == hashCode && key.equals(e._key)) {
					return true;
				}
			}
		} else {
			for (Entry e = table[0]; e != null; e = e._next) {
				if (e._key == null) {
					return true;
				}
			}
		}
		return false;
	}

	public Object get(Object key) {
		Entry[] table = _keysTable;
		if (key != null) {
			int hashCode = CollectionUtils.getLimitHash(key.hashCode());
			int index = (hashCode & 0x7FFFFFFF) % table.length;
			for (Entry e = table[index]; e != null; e = e._next) {
				if (e._hashCode == hashCode && key.equals(e._key)) {
					return e._value;
				}
			}
		} else {
			for (Entry e = table[0]; e != null; e = e._next) {
				if (e._key == null) {
					return e._value;
				}
			}
		}
		return null;
	}

	public Object getValue(Object key) {
		return get(key);
	}

	public Object get(int index) {
		if (index < 0 || index >= _size) {
			return null;
		}
		Entry entry = getEntry(index);
		if (entry != null) {
			return entry._value;
		}
		return null;
	}

	public Object getKey(int index) {
		if (index < 0 || index >= _size) {
			return null;
		}
		Entry entry = getEntry(index);
		if (entry != null) {
			return entry._key;
		}
		return null;
	}

	public Entry getEntry(int index) {
		if (index >= _size) {
			throw new LSysException("Index:" + index + ", Size:" + _size);
		}
		return _valuesTable[index];
	}

	public void putAll(ArrayMap map) {
		ensureCapacity();
		for (int i = 0; i < map._size; i++) {
			Entry e = map.getEntry(i);
			put(e._key, e._value);
		}
	}

	public Object put(Object key, Object v) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = CollectionUtils.getLimitHash(key.hashCode());
			index = (hashCode & 0x7FFFFFFF) % _keysTable.length;
			for (Entry e = _keysTable[index]; e != null; e = e._next) {
				if ((e._hashCode == hashCode) && key.equals(e._key)) {
					return swapValue(e, v);
				}
			}
		} else {
			for (Entry e = _keysTable[0]; e != null; e = e._next) {
				if (e._key == null) {
					return swapValue(e, v);
				}
			}
		}
		ensureCapacity();
		index = (hashCode & 0x7FFFFFFF) % _keysTable.length;
		Entry e = null;
		if (_removed < 0) {
			_removed = 0;
		}
		if (_removed == 0) {
			e = new Entry(_size, hashCode, key, v, _keysTable[index]);
		} else {
			e = new Entry(_removed + _size, hashCode, key, v, _keysTable[index]);
		}
		_keysTable[index] = e;
		_valuesTable[_size++] = e;
		return null;
	}

	public void set(int index, Object v) {
		getEntry(index).setValue(v);
	}

	public Object remove(Object key) {
		Entry e = removeMap(key);
		if (e != null) {
			Object v = e._value;
			int index = indexOf(e);
			removeList(index);
			e.clear();
			return v;
		}
		return null;
	}

	public Object remove(int index) {
		Entry e = removeList(index);
		Object v = e._value;
		removeMap(e._key);
		e.clear();
		return v;
	}

	@Override
	public void clear() {
		int length = _keysTable.length;
		for (int i = 0; i < length; i++) {
			_keysTable[i] = null;
			_valuesTable[i] = null;
		}
		_size = 0;
		_removed = 0;
	}

	public int getRemoved() {
		return _removed;
	}

	public Entry[] toEntrys() {
		Entry[] lists = CollectionUtils.copyOf(_valuesTable, _size);
		return lists;
	}

	public TArray<Entry> toList() {
		TArray<Entry> lists = new TArray<ArrayMap.Entry>(_size);
		for (int i = 0; i < _size; i++) {
			lists.add(_valuesTable[i]);
		}
		return lists;
	}

	public Object[] toArray() {
		Object[] array = new Object[_size];
		for (int i = 0; i < _size; i++) {
			array[i] = get(i);
		}
		return array;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ArrayMap)) {
			return false;
		}
		ArrayMap e = (ArrayMap) o;
		if (_size != e._size) {
			return false;
		}
		for (int i = 0; i < _size; i++) {
			if (!_valuesTable[i].equals(e._valuesTable[i])) {
				return false;
			}
		}
		return true;
	}

	public ArrayMap cpy() {
		ArrayMap copy = new ArrayMap();
		copy._threshold = _threshold;
		copy._keysTable = _keysTable;
		copy._valuesTable = _valuesTable;
		copy._size = _size;
		return copy;
	}

	private Entry removeMap(Object key) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = CollectionUtils.getLimitHash(key.hashCode());
			index = (hashCode & 0x7FFFFFFF) % _keysTable.length;
			for (Entry e = _keysTable[index], prev = null; e != null; prev = e, e = e._next) {
				if ((e._hashCode == hashCode) && key.equals(e._key)) {
					if (prev != null) {
						prev._next = e._next;
					} else {
						_keysTable[index] = e._next;
					}
					return e;
				}
			}
		} else {
			for (Entry e = _keysTable[index], prev = null; e != null; prev = e, e = e._next) {
				if ((e._hashCode == hashCode) && e._key == null) {
					if (prev != null) {
						prev._next = e._next;
					} else {
						_keysTable[index] = e._next;
					}
					return e;
				}
			}
		}
		return null;
	}

	private Entry removeList(int index) {
		Entry e = _valuesTable[index];
		int numMoved = _size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(_valuesTable, index + 1, _valuesTable, index, numMoved);
		}
		_valuesTable[--_size] = null;
		_removed++;
		return e;
	}

	private void ensureCapacity() {
		if (_size >= _threshold) {
			Entry[] oldTable = _valuesTable;
			int newCapacity = oldTable.length * 2 + 1;
			Entry[] newMapTable = new Entry[newCapacity];
			Entry[] newListTable = new Entry[newCapacity];
			_threshold = (int) (newCapacity * _loadFactor);
			System.arraycopy(oldTable, 0, newListTable, 0, _size);
			for (int i = 0; i < _size; i++) {
				Entry old = oldTable[i];
				int index = (old._hashCode & 0x7FFFFFFF) % newCapacity;
				Entry e = old;
				old = old._next;
				e._next = newMapTable[index];
				newMapTable[index] = e;
				newListTable[i]._index = i;
			}
			_keysTable = newMapTable;
			_valuesTable = newListTable;
			_removed = 0;
		}
	}

	private Object swapValue(Entry entry, Object v) {
		Object old = entry._value;
		entry._value = v;
		return old;
	}

	public void reverse() {
		for (int i = 0, lastIndex = _size - 1, n = _size / 2; i < n; i++) {
			int ii = lastIndex - i;
			Entry tempKey = _keysTable[i];
			_keysTable[i] = _keysTable[ii];
			_keysTable[ii] = tempKey;
			Entry tempValue = _valuesTable[i];
			_valuesTable[i] = _valuesTable[ii];
			_valuesTable[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = _size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			Entry tempKey = _keysTable[i];
			_keysTable[i] = _keysTable[ii];
			_keysTable[ii] = tempKey;
			Entry tempValue = _valuesTable[i];
			_valuesTable[i] = _valuesTable[ii];
			_valuesTable[ii] = tempValue;
		}
	}

	public void truncate(int newSize) {
		if (_size <= newSize) {
			return;

		}
		for (int i = newSize; i < _size; i++) {
			_keysTable[i] = null;
			_valuesTable[i] = null;
		}
		_size = newSize;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = _size - 1; i > -1; i--) {
			hashCode = 31 * hashCode + (_keysTable[i] == null ? 0 : _keysTable[i].hashCode());
			hashCode = 31 * hashCode + (_valuesTable[i] == null ? 0 : _valuesTable[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(LSystem.COMMA);
	}

	public String toString(char split) {
		if (_size == 0) {
			return "[]";
		}
		Entry[] values = this._valuesTable;
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		for (int i = 0; i < _size; i++) {
			Object key = values[i]._key;
			Object v = values[i]._value;
			buffer.append(key == this ? "(this Map)" : key);
			buffer.append('=');
			buffer.append(v == this ? "(this Map)" : v);
			if (i < _size - 1) {
				buffer.append(split).append(' ');
			}
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public void close() {
		if (_keysTable != null) {
			CollectionUtils.fill(_keysTable, null);
			this._keysTable = null;
		}
		if (_valuesTable != null) {
			CollectionUtils.fill(_valuesTable, null);
			this._valuesTable = null;
		}
		this._size = 0;
	}

}
