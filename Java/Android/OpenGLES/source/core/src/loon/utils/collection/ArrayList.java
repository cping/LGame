/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.utils.collection;

import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public class ArrayList {

	private Object[] _items;
	private boolean _full;
	private int _size;

	public ArrayList() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public ArrayList(int length) {
		this._items = new Object[length + (length / 2)];
		this._full = false;
		this._size = length;
	}

	public void addAll(ArrayList array) {
		addAll(array, 0, array._size);
	}

	public void addAll(ArrayList array, int offset, int length) {
		if (offset + length > array._size) {
			throw new IllegalArgumentException(
					"offset + length must be <= size: " + offset + " + "
							+ length + " <= " + array._size);
		}
		addAll(array._items, offset, length);
	}

	public void addAll(Object[] array, int offset, int length) {
		Object[] items = this._items;
		int sizeNeeded = _size + length;
		if (sizeNeeded > items.length) {
			items = expandCapacity(MathUtils.max(8, (_size + 1) * 2));
		}
		System.arraycopy(array, offset, items, _size, length);
		_size += length;
	}

	public void add(int index, Object element) {
		if (index >= this._items.length) {
			Object[] items = this._items;
			if (_size == items.length) {
				items = expandCapacity(MathUtils.max(8, (_size + 1) * 2));
			}
		} else {
			this._items[index] = element;
		}
		this._size++;
	}

	public void add(Object element) {
		if (this._full) {
			Object[] items = this._items;
			if (_size == items.length) {
				items = expandCapacity(MathUtils.max(8, (_size + 1) * 2));
			}
			items[_size] = element;
		} else {
			int size = this._items.length;
			for (int i = 0; i < size; i++) {
				if (this._items[i] == null) {
					this._items[i] = element;
					if (i == size - 1) {
						this._full = true;
					}
					break;
				}

			}
		}
		this._size++;
	}

	private Object[] expandCapacity(int newSize) {
		Object[] items = this._items;
		Object[] obj = (Object[]) java.lang.reflect.Array.newInstance(items
				.getClass().getComponentType(), newSize);
		System.arraycopy(items, 0, obj, 0, MathUtils.min(_size, obj.length));
		this._items = obj;
		return obj;
	}

	@Override
	public Object clone() {
		return this;
	}

	public boolean contains(Object elem) {
		for (int i = 0; i < this._items.length; i++) {
			if (this._items[i].equals(elem)) {
				return true;
			}
		}
		return false;
	}

	public Object set(int index, Object value) {
		if (index >= _size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		Object old = this._items[index];
		_items[index] = value;
		return old;
	}

	public Object get(int index) {
		if (index >= _size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		return this._items[index];
	}

	public void swap(int first, int second) {
		if (first >= _size) {
			throw new IndexOutOfBoundsException(String.valueOf(first));
		}
		if (second >= _size) {
			throw new IndexOutOfBoundsException(String.valueOf(second));
		}
		Object[] items = this._items;
		Object firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public int indexOfIdenticalObject(Object elem) {
		for (int i = 0; i < this._items.length; i++) {
			if (this._items[i] == elem) {
				return i;
			}
		}
		return -1;
	}

	public int indexOf(Object elem) {
		for (int i = 0; i < this._items.length; i++) {
			if (this._items[i].equals(elem)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isEmpty() {
		if (this._size == 0) {
			return true;
		} else {
			return false;
		}
	}

	public int lastIndexOf(Object elem) {
		for (int i = this._items.length - 1; i >= 0; i--) {
			if (this._items[i].equals(elem)) {
				return i;
			}
		}
		return -1;
	}

	public boolean remove(Object value) {
		return remove(value, false);
	}

	public boolean remove(Object value, boolean identity) {
		Object[] items = this._items;
		if (identity || value == null) {
			for (int i = 0; i < _size; i++) {
				if (items[i] == value) {
					remove(i);
					return true;
				}
			}
		} else {
			for (int i = 0; i < _size; i++) {
				if (value.equals(items[i])) {
					remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public Object remove(int index) {
		if (index >= _size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		Object[] items = this._items;
		Object elem = items[index];
		_size--;
		System.arraycopy(_items, index + 1, _items, index, _size - index);
		items[_size] = null;
		return elem;
	}

	public void removeRange(int fromIndex, int toIndex) {
		for (int i = fromIndex; i <= toIndex; i++) {
			this.remove(fromIndex);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof ArrayList)) {
			return false;
		}
		ArrayList array = (ArrayList) object;
		int n = _size;
		if (n != array._size) {
			return false;
		}
		Object[] items1 = this._items;
		Object[] items2 = array._items;
		for (int i = 0; i < n; i++) {
			Object o1 = items1[i];
			Object o2 = items2[i];
			if (!(o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}
		return true;
	}

	public Object last() {
		return _items[_size < 1 ? 0 : _size - 1];
	}

	public Object first() {
		return _items[0];
	}

	public Object pop() {
		--_size;
		Object item = _items[_size];
		_items[_size] = null;
		return item;
	}

	public Object random() {
		if (_size == 0) {
			return null;
		}
		return _items[MathUtils.random(0, _size - 1)];
	}

	public void clear() {
		Object[] items = this._items;
		for (int i = 0; i < items.length; i++) {
			items[i] = null;
		}
		_size = 0;
	}

	public int size() {
		return _size;
	}

	public Object[] toArray() {
		Object[] result = (Object[]) java.lang.reflect.Array.newInstance(_items
				.getClass().getComponentType(), _size);
		System.arraycopy(_items, 0, result, 0, _size);
		return result;
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char split) {
		if (_size == 0) {
			return "[]";
		}
		Object[] items = this._items;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < _size; i++) {
			buffer.append(split);
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

}
