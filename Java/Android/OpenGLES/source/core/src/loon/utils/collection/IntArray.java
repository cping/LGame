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

import java.util.Arrays;

import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class IntArray {

	public int[] items;
	public int size;
	public boolean ordered;

	public IntArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public IntArray(String ctx) {
		String intString = ctx;
		if (intString != null) {
			if (intString.indexOf(',') != -1) {
				intString = StringUtils.replace(intString, ",", "")
						.replace(" ", "").trim();
			}
			if (!MathUtils.isNan(intString)) {
				throw new RuntimeException('[' + intString + ']'
						+ " not number ! ");
			}
		} else {
			throw new RuntimeException('[' + intString + ']' + " is NULL ! ");
		}
		char[] tmp = intString.toCharArray();
		int len = tmp.length;
		items = new int[len];
		for (int i = 0; i < len; i++) {
			switch (tmp[i]) {
			case '0':
				items[i] = 0;
				break;
			case '1':
				items[i] = 1;
				break;
			case '2':
				items[i] = 2;
				break;
			case '3':
				items[i] = 3;
				break;
			case '4':
				items[i] = 4;
				break;
			case '5':
				items[i] = 5;
				break;
			case '6':
				items[i] = 6;
				break;
			case '7':
				items[i] = 7;
				break;
			case '8':
				items[i] = 8;
				break;
			case '9':
				items[i] = 9;
				break;
			default:
				items[i] = -1;
				break;
			}
		}
		size = len;
		tmp = null;
	}

	public IntArray(int capacity) {
		this(true, capacity);
	}

	public IntArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new int[capacity];
	}

	public IntArray(IntArray array) {
		this.ordered = array.ordered;
		size = array.size;
		items = new int[size];
		System.arraycopy(array.items, 0, items, 0, size);
	}

	public IntArray(int... array) {
		this(true, array);
	}

	public IntArray(boolean ordered, int[] array) {
		this(ordered, array.length);
		size = array.length;
		System.arraycopy(array, 0, items, 0, size);
	}

	public void add(int value) {
		int[] items = this.items;
		if (size == items.length) {
			items = resize(MathUtils.max(8, (int) (size * 1.75f)));
		}
		items[size++] = value;
	}

	public void addAll(IntArray array) {
		addAll(array, 0, array.size);
	}

	public void addAll(IntArray array, int offset, int length) {
		if (offset + length > array.size) {
			throw new IllegalArgumentException(
					"offset + length must be <= size: " + offset + " + "
							+ length + " <= " + array.size);
		}
		addAll(array.items, offset, length);
	}

	public void addAll(int[] array) {
		addAll(array, 0, array.length);
	}

	public void addAll(int[] array, int offset, int length) {
		int[] items = this.items;
		int sizeNeeded = size + length - offset;
		if (sizeNeeded >= items.length) {
			items = resize(MathUtils.max(8, (int) (sizeNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, size, length);
		size += length;
	}

	public int get(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		return items[index];
	}

	public void set(int index, int value) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		items[index] = value;
	}

	public void insert(int index, int value) {
		int[] items = this.items;
		if (size == items.length)
			items = resize(Math.max(8, (int) (size * 1.75f)));
		if (ordered) {
			System.arraycopy(items, index, items, index + 1, size - index);
		} else {
			items[size] = items[index];
		}
		size++;
		items[index] = value;
	}

	public boolean contains(int value) {
		int i = size - 1;
		int[] items = this.items;
		while (i >= 0) {
			if (items[i--] == value)
				return true;
		}
		return false;
	}

	public boolean containsAll(IntArray c) {
		for (int i = 0; i < c.size; i++) {
			if (!contains(c.items[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean part(IntArray c) {
		int cc = 0;
		for (int i = 0; i < c.size; i++) {
			if (contains(c.items[i])) {
				cc++;
			}
		}
		return cc != 0;
	}

	public int indexOf(int value) {
		int[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (items[i] == value) {
				return i;
			}
		}
		return -1;
	}

	public int count(int value) {
		int cc = 0;
		int[] items = this.items;
		for (int i = 0; i < size; i++) {
			if (items[i] == value) {
				cc++;
			}
		}
		return cc;
	}

	public IntArray copy() {
		return new IntArray(this);
	}

	public boolean retainAll(IntArray c) {
		return batchRemove(c, true);
	}

	public boolean removeAll(IntArray c) {
		return batchRemove(c, false);
	}

	private boolean batchRemove(IntArray c, boolean complement) {
		final int[] data = this.items;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++) {
				if (c.contains(data[r]) == complement) {
					data[w++] = data[r];
				}
			}
		} finally {
			if (r != size) {
				System.arraycopy(data, r, data, w, size - r);
				w += size - r;
			}
			if (w != size) {
				for (int i = w; i < size; i++) {
					data[i] = -1;
				}
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	public boolean removeValue(int value) {
		int[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public int removeIndex(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}
		int[] items = this.items;
		int value = items[index];
		size--;
		if (ordered) {
			System.arraycopy(items, index + 1, items, index, size - index);
		} else {
			items[index] = items[size];
		}
		return value;
	}

	public int pop() {
		return items[--size];
	}

	public int peek() {
		return items[size - 1];
	}

	public void clear() {
		size = 0;
	}

	public int[] ensureCapacity(int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= items.length) {
			resize(Math.max(8, sizeNeeded));
		}
		return items;
	}

	protected int[] resize(int newSize) {
		int[] newItems = new int[newSize];
		int[] items = this.items;
		System.arraycopy(items, 0, newItems, 0,
				Math.min(items.length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public void sort() {
		Arrays.sort(items, 0, size);
	}

	public int[] toArray() {
		int[] array = new int[size];
		System.arraycopy(items, 0, array, 0, size);
		return array;
	}

	public String toString(char split) {
		if (size == 0) {
			return "[]";
		}
		int[] items = this.items;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(split);
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public String toString() {
		return toString(',');
	}
}
