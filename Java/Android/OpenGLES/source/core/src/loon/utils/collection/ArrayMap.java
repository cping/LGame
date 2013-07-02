package loon.utils.collection;

import java.util.ArrayList;

import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

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
public class ArrayMap {

	static final private float LOAD_FACTOR = 0.75f;

	private int threshold;

	private Entry[] keyTables;

	private Entry[] valueTables;

	private int size = 0;

	public ArrayMap() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public ArrayMap(int initialCapacity) {
		if (initialCapacity <= 0) {
			initialCapacity = CollectionUtils.INITIAL_CAPACITY;
		}
		keyTables = new Entry[initialCapacity];
		valueTables = new Entry[initialCapacity];
		threshold = (int) (initialCapacity * LOAD_FACTOR);
	}

	public final int size() {
		return size;
	}

	public final boolean isEmpty() {
		return size == 0;
	}

	public final boolean containsValue(Object value) {
		return indexOf(value) >= 0;
	}

	public final int indexOf(Object value) {
		if (value != null) {
			for (int i = 0; i < size; i++) {
				if (value.equals(valueTables[i].value)) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				if (valueTables[i].value == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean containsKey(final Object key) {
		Entry[] table = keyTables;
		if (key != null) {
			int hashCode = key.hashCode();
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
		Entry[] table = keyTables;
		if (key != null) {
			int hashCode = key.hashCode();
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
			throw new IndexOutOfBoundsException("Index:" + index + ", Size:"
					+ size);
		}
		return valueTables[index];
	}

	public Object put(final Object key, final Object value) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = key.hashCode();
			index = (hashCode & 0x7FFFFFFF) % keyTables.length;
			for (Entry e = keyTables[index]; e != null; e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					return swapValue(e, value);
				}
			}
		} else {
			for (Entry e = keyTables[0]; e != null; e = e.next) {
				if (e.key == null) {
					return swapValue(e, value);
				}
			}
		}
		ensureCapacity();
		index = (hashCode & 0x7FFFFFFF) % keyTables.length;
		Entry e = new Entry(hashCode, key, value, keyTables[index]);
		keyTables[index] = e;
		valueTables[size++] = e;
		return null;
	}

	public final void set(final int index, final Object value) {
		getEntry(index).setValue(value);
	}

	public Object remove(final Object key) {
		Entry e = removeMap(key);
		if (e != null) {
			Object value = e.value;
			removeList(indexOf(e));
			e.clear();
			return value;
		}
		return null;
	}

	public final Object remove(int index) {
		Entry e = removeList(index);
		Object value = e.value;
		removeMap(e.key);
		e.value = null;
		return value;
	}

	public final void clear() {
		int length = keyTables.length;
		for (int i = 0; i < length; i++) {
			keyTables[i] = null;
			valueTables[i] = null;
		}
		size = 0;
	}

	public Entry[] toEntrys() {
		Entry[] lists = (Entry[]) CollectionUtils.copyOf(valueTables, size);
		return lists;
	}

	public ArrayList<Entry> toList() {
		ArrayList<Entry> lists = new ArrayList<ArrayMap.Entry>(size);
		for (int i = 0; i < size; i++) {
			lists.add(valueTables[i]);
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
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(Object o) {
		if (!getClass().isInstance(o)) {
			return false;
		}
		ArrayMap e = (ArrayMap) o;
		if (size != e.size) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!valueTables[i].equals(e.valueTables[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object clone() {
		ArrayMap copy = new ArrayMap();
		copy.threshold = threshold;
		copy.keyTables = keyTables;
		copy.valueTables = valueTables;
		copy.size = size;
		return copy;
	}

	private final int indexOf(final Entry entry) {
		for (int i = 0; i < size; i++) {
			if (valueTables[i] == entry) {
				return i;
			}
		}
		return -1;
	}

	private final Entry removeMap(Object key) {
		int hashCode = 0;
		int index = 0;

		if (key != null) {
			hashCode = key.hashCode();
			index = (hashCode & 0x7FFFFFFF) % keyTables.length;
			for (Entry e = keyTables[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						keyTables[index] = e.next;
					}
					return e;
				}
			}
		} else {
			for (Entry e = keyTables[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && e.key == null) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						keyTables[index] = e.next;
					}
					return e;
				}
			}
		}
		return null;
	}

	private final Entry removeList(int index) {
		Entry e = valueTables[index];
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(valueTables, index + 1, valueTables, index,
					numMoved);
		}
		valueTables[--size] = null;
		return e;
	}

	private final void ensureCapacity() {
		if (size >= threshold) {
			Entry[] oldTable = valueTables;
			int newCapacity = oldTable.length * 2 + 1;
			Entry[] newMapTable = new Entry[newCapacity];
			Entry[] newListTable = new Entry[newCapacity];
			threshold = (int) (newCapacity * LOAD_FACTOR);
			System.arraycopy(oldTable, 0, newListTable, 0, size);
			for (int i = 0; i < size; i++) {
				Entry old = oldTable[i];
				int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
				Entry e = old;
				old = old.next;
				e.next = newMapTable[index];
				newMapTable[index] = e;
			}
			keyTables = newMapTable;
			valueTables = newListTable;
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
			Entry tempKey = keyTables[i];
			keyTables[i] = keyTables[ii];
			keyTables[ii] = tempKey;
			Entry tempValue = valueTables[i];
			valueTables[i] = valueTables[ii];
			valueTables[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			Entry tempKey = keyTables[i];
			keyTables[i] = keyTables[ii];
			keyTables[ii] = tempKey;
			Entry tempValue = valueTables[i];
			valueTables[i] = valueTables[ii];
			valueTables[ii] = tempValue;
		}
	}

	public void truncate(int newSize) {
		if (size <= newSize) {
			return;

		}
		for (int i = newSize; i < size; i++) {
			keyTables[i] = null;
			valueTables[i] = null;
		}
		size = newSize;
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char split) {
		if (size == 0) {
			return "[]";
		}
		Entry[] values = this.valueTables;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
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

	public class Entry {

		int hashCode;

		Object key;

		Object value;

		Entry next;

		public Entry(final int hashCode, final Object key, final Object value,
				final Entry next) {

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

		public void clear() {
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
}
