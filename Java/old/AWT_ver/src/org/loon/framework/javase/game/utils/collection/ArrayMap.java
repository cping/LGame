package org.loon.framework.javase.game.utils.collection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.loon.framework.javase.game.utils.CollectionUtils;


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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */

@SuppressWarnings("rawtypes")
public class ArrayMap extends AbstractMap implements Externalizable, Map,
		Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final private float LOAD_FACTOR = 0.75f;

	private int threshold;

	private transient Entry[] mapTable;

	private transient Entry[] listTable;

	private transient int length = 0;

	private transient Set entrySet = null;

	public ArrayMap() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public ArrayMap(int initialCapacity) {
		if (initialCapacity <= 0) {
			initialCapacity = CollectionUtils.INITIAL_CAPACITY;
		}
		mapTable = new Entry[initialCapacity];
		listTable = new Entry[initialCapacity];
		threshold = (int) (initialCapacity * LOAD_FACTOR);
	}

	public Iterator iterator() {
		return new ArrayMapIterator();
	}

	public ArrayMap(Map map) {
		this((int) (map.size() / LOAD_FACTOR) + 1);
		putAll(map);
	}

	public final int size() {
		return length;
	}

	public final boolean isEmpty() {
		return length == 0;
	}

	public final boolean containsValue(Object value) {
		return indexOf(value) >= 0;
	}

	public final int indexOf(Object value) {
		if (value != null) {
			for (int i = 0; i < length; i++) {
				if (value.equals(listTable[i].value)) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < length; i++) {
				if (listTable[i].value == null) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean containsKey(final Object key) {
		Entry[] table = mapTable;
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
		Entry[] table = mapTable;
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

	public final Object get(final int index) {
		return getEntry(index).value;
	}

	public final Object getKey(final int index) {
		return getEntry(index).key;
	}

	public final Entry getEntry(final int index) {
		if (index >= length) {
			throw new IndexOutOfBoundsException("Index:" + index + ", Size:"
					+ length);
		}
		return listTable[index];
	}

	public Object put(final Object key, final Object value) {
		int hashCode = 0;
		int index = 0;
		if (key != null) {
			hashCode = key.hashCode();
			index = (hashCode & 0x7FFFFFFF) % mapTable.length;
			for (Entry e = mapTable[index]; e != null; e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					return swapValue(e, value);
				}
			}
		} else {
			for (Entry e = mapTable[0]; e != null; e = e.next) {
				if (e.key == null) {
					return swapValue(e, value);
				}
			}
		}
		ensureCapacity();
		index = (hashCode & 0x7FFFFFFF) % mapTable.length;
		Entry e = new Entry(hashCode, key, value, mapTable[index]);
		mapTable[index] = e;
		listTable[length++] = e;
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

	public void putAll(Map map) {
		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			put(e.getKey(), e.getValue());
		}
	}

	public final void clear() {
		for (int i = 0; i < mapTable.length; i++) {
			mapTable[i] = null;
		}
		for (int i = 0; i < listTable.length; i++) {
			listTable[i] = null;
		}
		length = 0;
	}

	public final Object[] toArray() {
		Object[] array = new Object[length];
		for (int i = 0; i < array.length; i++) {
			array[i] = get(i);
		}
		return array;
	}

	public final boolean equals(Object o) {
		if (!getClass().isInstance(o)) {
			return false;
		}
		ArrayMap e = (ArrayMap) o;
		if (length != e.length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			if (!listTable[i].equals(e.listTable[i])) {
				return false;
			}
		}
		return true;
	}

	public final Set entrySet() {
		if (entrySet == null) {
			entrySet = new AbstractSet() {
				public Iterator iterator() {
					return new ArrayMapIterator();
				}

				public boolean contains(Object o) {
					if (!(o instanceof Entry)) {
						return false;
					}
					Entry entry = (Entry) o;
					int index = (entry.hashCode & 0x7FFFFFFF)
							% mapTable.length;
					for (Entry e = mapTable[index]; e != null; e = e.next) {
						if (e.equals(entry)) {
							return true;
						}
					}
					return false;
				}

				public boolean remove(Object o) {
					if (!(o instanceof Entry)) {
						return false;
					}
					Entry entry = (Entry) o;
					return ArrayMap.this.remove(entry.key) != null;
				}

				public int size() {
					return length;
				}

				public void clear() {
					ArrayMap.this.clear();
				}
			};
		}
		return entrySet;
	}

	public ArrayMap clone() {
		ArrayMap copy = new ArrayMap();
		copy.threshold = threshold;
		copy.mapTable = mapTable;
		copy.listTable = listTable;
		copy.length = length;
		return copy;
	}

	private final int indexOf(final Entry entry) {
		for (int i = 0; i < length; i++) {
			if (listTable[i] == entry) {
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
			index = (hashCode & 0x7FFFFFFF) % mapTable.length;
			for (Entry e = mapTable[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && key.equals(e.key)) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						mapTable[index] = e.next;
					}
					return e;
				}
			}
		} else {
			for (Entry e = mapTable[index], prev = null; e != null; prev = e, e = e.next) {
				if ((e.hashCode == hashCode) && e.key == null) {
					if (prev != null) {
						prev.next = e.next;
					} else {
						mapTable[index] = e.next;
					}
					return e;
				}
			}
		}
		return null;
	}

	private final Entry removeList(int index) {
		Entry e = listTable[index];
		int numMoved = length - index - 1;
		if (numMoved > 0) {
			System
					.arraycopy(listTable, index + 1, listTable, index,
							numMoved);
		}
		listTable[--length] = null;
		return e;
	}

	private final void ensureCapacity() {
		if (length >= threshold) {
			Entry[] oldTable = listTable;
			int newCapacity = oldTable.length * 2 + 1;
			Entry[] newMapTable = new Entry[newCapacity];
			Entry[] newListTable = new Entry[newCapacity];
			threshold = (int) (newCapacity * LOAD_FACTOR);
			System.arraycopy(oldTable, 0, newListTable, 0, length);
			for (int i = 0; i < length; i++) {
				Entry old = oldTable[i];
				int index = (old.hashCode & 0x7FFFFFFF) % newCapacity;
				Entry e = old;
				old = old.next;
				e.next = newMapTable[index];
				newMapTable[index] = e;
			}
			mapTable = newMapTable;
			listTable = newListTable;
		}
	}

	private final Object swapValue(final Entry entry, final Object value) {
		Object old = entry.value;
		entry.value = value;
		return old;
	}

	private class ArrayMapIterator implements Iterator {

		private int current = 0;

		private int last = -1;

		public boolean hasNext() {
			return current != length;
		}

		public Object next() {
			try {
				Object n = listTable[current];
				last = current++;
				return n;
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			if (last == -1) {
				throw new IllegalStateException();
			}
			ArrayMap.this.remove(last);
			if (last < current) {
				current--;
			}
			last = -1;
		}
	}

	private static class Entry implements Map.Entry, Externalizable {

		private static final long serialVersionUID = -6625980241350717177L;

		transient int hashCode;

		transient Object key;

		transient Object value;

		transient Entry next;

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

		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			Entry e = (Entry) o;
			return (key != null ? key.equals(e.key) : e.key == null)
					&& (value != null ? value.equals(e.value) : e.value == null);
		}

		public int hashCode() {
			return hashCode;
		}

		public String toString() {
			return key + "=" + value;
		}

		public void writeExternal(final ObjectOutput s) throws IOException {
			s.writeInt(hashCode);
			s.writeObject(key);
			s.writeObject(value);
			s.writeObject(next);
		}

		public void readExternal(final ObjectInput s) throws IOException,
				ClassNotFoundException {
			hashCode = s.readInt();
			key = s.readObject();
			value = s.readObject();
			next = (Entry) s.readObject();
		}
	}

	public final void writeExternal(final ObjectOutput out) throws IOException {
		out.writeInt(listTable.length);
		out.writeInt(length);
		for (int i = 0; i < length; i++) {
			out.writeObject(listTable[i].key);
			out.writeObject(listTable[i].value);
		}
	}

	public final void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {

		int num = in.readInt();
		mapTable = new Entry[num];
		listTable = new Entry[num];
		threshold = (int) (num * LOAD_FACTOR);
		int size = in.readInt();
		for (int i = 0; i < size; i++) {
			Object key = in.readObject();
			Object value = in.readObject();
			put(key, value);
		}
	}

}
