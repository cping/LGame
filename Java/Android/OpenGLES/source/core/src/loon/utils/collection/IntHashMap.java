/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.utils.collection;

import loon.utils.CollectionUtils;

public class IntHashMap {

	Entry[] valueTables;

	int _size;

	int _threshold;

	int _modCount;

	final float _loadFactor;

	public IntHashMap(int initialCapacity, float _loadFactor) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal initial capacity: "
					+ initialCapacity);
		}
		if (initialCapacity > 1 << 30) {
			initialCapacity = 1 << 30;
		}
		if (_loadFactor <= 0 || Float.isNaN(_loadFactor)) {
			throw new IllegalArgumentException("Illegal load factor: "
					+ _loadFactor);
		}
		int capacity = 1;
		while (capacity < initialCapacity) {
			capacity <<= 1;
		}

		this._loadFactor = _loadFactor;
		_threshold = (int) (capacity * _loadFactor);
		valueTables = new Entry[capacity];
		reset();
	}

	public IntHashMap() {
		_loadFactor = 0.75f;
		_threshold = (int) (CollectionUtils.INITIAL_CAPACITY * 0.75f);
		valueTables = new Entry[CollectionUtils.INITIAL_CAPACITY];
		reset();
	}

	protected void reset() {
	}

	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	public int size() {
		return _size;
	}

	public boolean isEmpty() {
		return _size == 0;
	}

	public Object get(int key) {
		int i = indexFor(key, valueTables.length);
		Entry e = valueTables[i];
		while (true) {
			if (e == null) {
				return null;
			}
			if (key == e.key) {
				return e.value;
			}
			e = e.next;
		}
	}

	public boolean containsKey(int key) {
		int i = indexFor(key, valueTables.length);
		Entry e = valueTables[i];
		while (e != null) {
			if (key == e.key) {
				return true;
			}
			e = e.next;
		}
		return false;
	}

	public Entry getEntry(int key) {
		int i = indexFor(key, valueTables.length);
		Entry e = valueTables[i];
		while (e != null && !(key == e.key)) {
			e = e.next;
		}
		return e;
	}

	public Object put(int key, Object value) {
		int i = indexFor(key, valueTables.length);
		for (Entry e = valueTables[i]; e != null; e = e.next) {
			if (key == e.key) {
				Object oldValue = e.value;
				e.value = value;
				return oldValue;
			}
		}
		_modCount++;
		addEntry(key, value, i);
		return null;
	}

	private void putForCreate(int key, Object value) {
		int i = indexFor(key, valueTables.length);
		for (Entry e = valueTables[i]; e != null; e = e.next) {
			if (key == e.key) {
				e.value = value;
				return;
			}
		}

		createEntry(key, value, i);
	}

	void putAllForCreate(IntHashMap m) {
		for (int i = 0; i < _size; i++) {
			Entry e = valueTables[i];
			putForCreate(e.getKey(), e.getValue());
		}
	}

	void resize(int newCapacity) {
		Entry[] oldTable = valueTables;
		int oldCapacity = oldTable.length;
		if (oldCapacity == 1 << 30) {
			_threshold = Integer.MAX_VALUE;
			return;
		}
		Entry[] newTable = new Entry[newCapacity];
		transfer(newTable);
		valueTables = newTable;
		_threshold = (int) (newCapacity * _loadFactor);
	}

	void transfer(Entry[] newTable) {
		Entry[] src = valueTables;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			Entry e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					Entry next = e.next;
					int i = indexFor(e.key, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	public void putAll(IntHashMap m) {
		int numKeysToBeAdded = m._size;
		if (numKeysToBeAdded == 0) {
			return;
		}
		if (numKeysToBeAdded > _threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / _loadFactor + 1);
			if (targetCapacity > 1 << 30) {
				targetCapacity = 1 << 30;
			}
			int newCapacity = valueTables.length;
			while (newCapacity < targetCapacity) {
				newCapacity <<= 1;
			}
			if (newCapacity > valueTables.length) {
				resize(newCapacity);
			}
		}
		for (int i = 0; i < _size; i++) {
			Entry e = valueTables[i];
			put(e.getKey(), e.getValue());
		}
	}

	public Entry[] toEntrys() {
		Entry[] lists = (Entry[]) CollectionUtils.copyOf(valueTables, _size);
		return lists;
	}

	public Object remove(int key) {
		Entry e = removeEntryForKey(key);
		return (e == null ? null : e.value);
	}

	Entry removeEntryForKey(int key) {
		int i = indexFor(key, valueTables.length);
		Entry prev = valueTables[i];
		Entry e = prev;

		while (e != null) {
			Entry next = e.next;
			if (key == e.key) {
				_modCount++;
				_size--;
				if (prev == e) {
					valueTables[i] = next;
				} else {
					prev.next = next;
				}
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}

	Entry removeMapping(Object o) {
		if (!(o instanceof Entry)) {
			return null;
		}

		Entry entry = (Entry) o;
		int key = entry.getKey();
		int i = indexFor(key, valueTables.length);
		Entry prev = valueTables[i];
		Entry e = prev;

		while (e != null) {
			Entry next = e.next;
			if (e.key == key && e.equals(entry)) {
				_modCount++;
				_size--;
				if (prev == e) {
					valueTables[i] = next;
				} else {
					prev.next = next;
				}
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}

	public void clear() {
		_modCount++;
		Entry tab[] = valueTables;
		for (int i = 0; i < tab.length; i++) {
			tab[i] = null;
		}
		_size = 0;
	}

	public boolean containsValue(Object value) {
		if (value == null) {
			return containsNullValue();
		}

		Entry tab[] = valueTables;
		for (int i = 0; i < tab.length; i++) {
			for (Entry e = tab[i]; e != null; e = e.next) {
				if (value.equals(e.value)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean containsNullValue() {
		Entry tab[] = valueTables;
		for (int i = 0; i < tab.length; i++) {
			for (Entry e = tab[i]; e != null; e = e.next) {
				if (e.value == null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IntHashMap clone() throws CloneNotSupportedException {
		IntHashMap result = null;
		try {
			result = (IntHashMap) super.clone();
			result.valueTables = new Entry[valueTables.length];
			result._modCount = 0;
			result._size = 0;
			result.reset();
			result.putAllForCreate(this);
		} catch (CloneNotSupportedException e) {
		}
		return result;
	}

	public static class Entry {

		final int key;
		Object value;
		Entry next;

		Entry(int k, Object v, Entry n) {
			value = v;
			next = n;
			key = k;
		}

		public int getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public Object setValue(Object newValue) {
			Object oldValue = value;
			value = newValue;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry e = (Entry) o;
			int k1 = getKey();
			int k2 = e.getKey();
			if (k1 == k2) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2))) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return key ^ (value == null ? 0 : value.hashCode());
		}

		@Override
		public String toString() {
			return getKey() + "=" + getValue();
		}
	}

	void addEntry(int key, Object value, int bucketIndex) {
		valueTables[bucketIndex] = new Entry(key, value,
				valueTables[bucketIndex]);
		if (_size++ >= _threshold) {
			resize(2 * valueTables.length);
		}
	}

	void createEntry(int key, Object value, int bucketIndex) {
		valueTables[bucketIndex] = new Entry(key, value,
				valueTables[bucketIndex]);
		_size++;
	}

	public int capacity() {
		return valueTables.length;
	}

	public float loadFactor() {
		return _loadFactor;
	}

	/*
	 * public static void main(String[]args){ IntHashMap ints=new IntHashMap();
	 * ints.put(9, "111"); ints.remove(9); ints.put(6, "000");
	 * System.out.println(ints.get(6)); }
	 */
}
