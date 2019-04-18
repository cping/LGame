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

import loon.LSysException;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public class ArrayMap implements IArray {

	public static class Entry {

		protected int index;

		protected int hashCode;

		public Object key;

		public Object value;

		protected Entry next;

		protected Entry(final int hashCode, final Object key, final Object value, final Entry next) {
			this(-1, hashCode, key, value, next);
		}

		public Entry(final int index, final int hashCode, final Object key, final Object value, final Entry next) {
			this.index = index;
			this.hashCode = hashCode;
			this.key = key;
			this.value = value;
			this.next = next;
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public Object setValue(final Object value) {
			Object oldValue = value;
			this.value = value;
			return oldValue;
		}

		public int getIndex() {
			return this.index;
		}

		protected void clear() {
			key = null;
			value = null;
			next = null;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			Entry e = (Entry) o;
			return (key != null ? key.equals(e.key) : e.key == null)
					&& (value != null ? value.equals(e.value) : e.value == null);
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}

	}

	private int threshold;

	private Entry[] keysTable;

	private Entry[] valuesTable;

	private int size = 0;

	private float loadFactor;

	private int removed = 0;

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
		this.keysTable = new Entry[initialCapacity];
		this.valuesTable = new Entry[initialCapacity];
		this.threshold = (int) (initialCapacity * factor);
		this.loadFactor = factor;
	}

	@Override
	public final int size() {
		return size;
	}

	@Override
	public final boolean isEmpty() {
		return size == 0;
	}

	public final boolean containsValue(Object value) {
		return indexOf(value) >= 0;
	}

	protected final int indexOf(final Entry entry) {
		if (entry != null) {
			Entry value;
			int start = 0;
			int len = size - 1;
			for (; start <= len;) {
				int mid = start + (len - start) / 2;
				value = valuesTable[mid];
				if (entry.index < value.index) {
					len = mid - 1;
				} else if (entry.index > value.index) {
					start = mid + 1;
				} else {
					if (entry == value) {
						return mid;
					} else {
						break;
					}
				}
			}
			for (int i = 0; i < size; i++) {
				value = valuesTable[i];
				if (value == entry) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (valuesTable[i] == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public final int indexOf(Object value) {
		if (value != null) {
			Object data = null;
			for (int i = 0; i < size; i++) {
				data = valuesTable[i].value;
				if (data == value || data.equals(value)) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (valuesTable[i].value == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean containsKey(final Object key) {
		Entry[] table = keysTable;
		if (key != null) {
			int hashCode = CollectionUtils.getLimitHash(key.hashCode());
			int index = (hashCode & 0x7FFFFFFF) % table.length;
			for (Entry e = table[index]; e != null; e = e.next) {
				if (e.hashCode == hashCode && key.equals(e.key)) {
					return true;
				}
			}
		} else {
			for (Entry e = table[0]; e != null; e = e.next) {
				if (e.key == null) {
					return true;
				}
			}
		}
		return false;
	}

	public Object get(final Object key) {
		Entry[] table = keysTable;
		if (key != null) {
			int hashCode = CollectionUtils.getLimitHash(key.hashCode());
			int index = (hashCode & 0x7FFFFFFF) % table.length;
			for (Entry e = table[index]; e != null; e = e.next) {
				if (e.hashCode == hashCode && key.equals(e.key)) {
					return e.value;
				}
			}
		} else {
			for (Entry e = table[0]; e != null; e = e.next) {
				if (e.key == null) {
					return e.value;
				}
			}
		}
		return null;
	}

	public Object getValue(final Object key) {
		return get(key);
	}

	public final Object get(final int index) {
		if (index < 0 || index >= size) {
			return null;
		}
		Entry entry = getEntry(index);
		if (entry != null) {
			return entry.value;
		}
		return null;
	}

	public final Object getKey(final int index) {
		if (index < 0 || index >= size) {
			return null;
		}
		Entry entry = getEntry(index);
		if (entry != null) {
			return entry.key;
		}
		return null;
	}

	public final Entry getEntry(final int index) {
		if (index >= size) {
			throw new LSysException("Index:" + index + ", Size:" + size);
		}
		return valuesTable[index];
	}

	public void putAll(ArrayMap map) {
		ensureCapacity();
		for (int i = 0; i < map.size; i++) {
			Entry e = map.getEntry(i);
			put(e.key, e.value);
		}
	}

	public Object put(final Object key, final Object value) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = CollectionUtils.getLimitHash(key.hashCode());
			index = (hashCode & 0x7FFFFFFF) % keysTable.length;
			for (Entry e = keysTable[index]; e != null; e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					return swapValue(e, value);
				}
			}
		} else {
			for (Entry e = keysTable[0]; e != null; e = e.next) {
				if (e.key == null) {
					return swapValue(e, value);
				}
			}
		}
		ensureCapacity();
		index = (hashCode & 0x7FFFFFFF) % keysTable.length;
		Entry e = null;
		if (removed < 0) {
			removed = 0;
		}
		if (removed == 0) {
			e = new Entry(size, hashCode, key, value, keysTable[index]);
		} else {
			e = new Entry(removed + size, hashCode, key, value, keysTable[index]);
		}
		keysTable[index] = e;
		valuesTable[size++] = e;
		return null;
	}

	public final void set(final int index, final Object value) {
		getEntry(index).setValue(value);
	}

	public Object remove(final Object key) {
		Entry e = removeMap(key);
		if (e != null) {
			Object value = e.value;
			int index = indexOf(e);
			removeList(index);
			e.clear();
			return value;
		}
		return null;
	}

	public final Object remove(int index) {
		Entry e = removeList(index);
		Object value = e.value;
		removeMap(e.key);
		e.clear();
		return value;
	}

	@Override
	public final void clear() {
		int length = keysTable.length;
		for (int i = 0; i < length; i++) {
			keysTable[i] = null;
			valuesTable[i] = null;
		}
		size = 0;
		removed = 0;
	}

	public int getRemoved() {
		return removed;
	}

	public Entry[] toEntrys() {
		Entry[] lists = CollectionUtils.copyOf(valuesTable, size);
		return lists;
	}

	public TArray<Entry> toList() {
		TArray<Entry> lists = new TArray<ArrayMap.Entry>(size);
		for (int i = 0; i < size; i++) {
			lists.add(valuesTable[i]);
		}
		return lists;
	}

	public final Object[] toArray() {
		Object[] array = new Object[size];
		for (int i = 0; i < size; i++) {
			array[i] = get(i);
		}
		return array;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof ArrayMap)) {
			return false;
		}
		ArrayMap e = (ArrayMap) o;
		if (size != e.size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!valuesTable[i].equals(e.valuesTable[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object clone() {
		ArrayMap copy = new ArrayMap();
		copy.threshold = threshold;
		copy.keysTable = keysTable;
		copy.valuesTable = valuesTable;
		copy.size = size;
		return copy;
	}

	private final Entry removeMap(Object key) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = CollectionUtils.getLimitHash(key.hashCode());
			index = (hashCode & 0x7FFFFFFF) % keysTable.length;
			for (Entry e = keysTable[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						keysTable[index] = e.next;
					}
					return e;
				}
			}
		} else {
			for (Entry e = keysTable[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && e.key == null) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						keysTable[index] = e.next;
					}
					return e;
				}
			}
		}
		return null;
	}

	private final Entry removeList(int index) {
		Entry e = valuesTable[index];
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(valuesTable, index + 1, valuesTable, index, numMoved);
		}
		valuesTable[--size] = null;
		removed++;
		return e;
	}

	private final void ensureCapacity() {
		if (size >= threshold) {
			Entry[] oldTable = valuesTable;
			int newCapacity = oldTable.length * 2 + 1;
			Entry[] newMapTable = new Entry[newCapacity];
			Entry[] newListTable = new Entry[newCapacity];
			threshold = (int) (newCapacity * loadFactor);
			System.arraycopy(oldTable, 0, newListTable, 0, size);
			for (int i = 0; i < size; i++) {
				Entry old = oldTable[i];
				int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
				Entry e = old;
				old = old.next;
				e.next = newMapTable[index];
				newMapTable[index] = e;
				newListTable[i].index = i;
			}
			keysTable = newMapTable;
			valuesTable = newListTable;
			removed = 0;
		}
	}

	private final Object swapValue(final Entry entry, final Object value) {
		Object old = entry.value;
		entry.value = value;
		return old;
	}

	public void reverse() {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			Entry tempKey = keysTable[i];
			keysTable[i] = keysTable[ii];
			keysTable[ii] = tempKey;
			Entry tempValue = valuesTable[i];
			valuesTable[i] = valuesTable[ii];
			valuesTable[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			Entry tempKey = keysTable[i];
			keysTable[i] = keysTable[ii];
			keysTable[ii] = tempKey;
			Entry tempValue = valuesTable[i];
			valuesTable[i] = valuesTable[ii];
			valuesTable[ii] = tempValue;
		}
	}

	public void truncate(int newSize) {
		if (size <= newSize) {
			return;

		}
		for (int i = newSize; i < size; i++) {
			keysTable[i] = null;
			valuesTable[i] = null;
		}
		size = newSize;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = size - 1; i > -1; i--) {
			hashCode = 31 * hashCode + (keysTable[i] == null ? 0 : keysTable[i].hashCode());
			hashCode = 31 * hashCode + (valuesTable[i] == null ? 0 : valuesTable[i].hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char split) {
		if (size == 0) {
			return "[]";
		}
		Entry[] values = this.valuesTable;
		StringBuilder buffer = new StringBuilder(CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		for (int i = 0; i < size; i++) {
			Object key = values[i].key;
			Object value = values[i].value;
			buffer.append(key == this ? "(this Map)" : key);
			buffer.append('=');
			buffer.append(value == this ? "(this Map)" : value);
			if (i < size - 1) {
				buffer.append(split).append(' ');
			}
		}
		buffer.append(']');
		return buffer.toString();
	}

}
