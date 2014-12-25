/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.utils.collection;

import java.math.BigInteger;
import java.util.Arrays;

import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public class LongArray {
	public long[] items;
	public int length;
	public boolean ordered;

	public LongArray() {
		this(true, 16);
	}

	public LongArray(int capacity) {
		this(true, capacity);
	}

	public LongArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new long[capacity];
	}

	public LongArray(LongArray array) {
		this.ordered = array.ordered;
		length = array.length;
		items = new long[length];
		System.arraycopy(array.items, 0, items, 0, length);
	}

	public LongArray(long[] array) {
		this(true, array, 0, array.length);
	}

	public LongArray(boolean ordered, long[] array, int startIndex, int count) {
		this(ordered, count);
		length = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void unshift(long value) {
		if (length > 0) {
			long[] items = this.items;
			long[] newItems = new long[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
		} else {
			add(value);
		}
	}

	public void push(long value) {
		add(value);
	}

	public void add(long value) {
		long[] items = this.items;
		if (length == items.length) {
			items = relength(Math.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(LongArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(LongArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new IllegalArgumentException(
					"offset + length must be <= length: " + offset + " + "
							+ length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(long... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(long[] array, int offset, int length) {
		long[] items = this.items;
		int lengthNeeded = length + length;
		if (lengthNeeded > items.length) {
			items = relength(Math.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, length, length);
		length += length;
	}

	public long get(int index) {
		if (index >= length) {
			return 0;
		}
		return items[index];
	}

	public void set(int index, long value) {
		if (index >= length) {
			int size = length;
			for (int i = size; i < index + 1; i++) {
				add(0);
			}
			items[index] = value;
			return;
		}
		items[index] = value;
	}

	public void incr(int index, int value) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		items[index] += value;
	}

	public void mul(int index, int value) {
		if (index >= length)
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		items[index] *= value;
	}

	public void insert(int index, long value) {
		if (index > length) {
			throw new IndexOutOfBoundsException("index can't be > length: "
					+ index + " > " + length);
		}
		long[] items = this.items;
		if (length == items.length)
			items = relength(Math.max(8, (int) (length * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, length - index);
		else
			items[length] = items[index];
		length++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new IndexOutOfBoundsException("first can't be >= length: "
					+ first + " >= " + length);
		if (second >= length)
			throw new IndexOutOfBoundsException("second can't be >= length: "
					+ second + " >= " + length);
		long[] items = this.items;
		long firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(long value) {
		int i = length - 1;
		long[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(long value) {
		long[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(long value) {
		long[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(long value) {
		long[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public long removeIndex(int index) {
		if (index >= length) {
			throw new IndexOutOfBoundsException("index can't be >= length: "
					+ index + " >= " + length);
		}
		long[] items = this.items;
		long value = items[index];
		length--;
		if (ordered) {
			System.arraycopy(items, index + 1, items, index, length - index);
		} else {
			items[index] = items[length];
		}
		return value;
	}

	public void removeRange(int start, int end) {
		if (end >= length) {
			throw new IndexOutOfBoundsException("end can't be >= length: "
					+ end + " >= " + length);
		}
		if (start > end) {
			throw new IndexOutOfBoundsException("start can't be > end: "
					+ start + " > " + end);
		}
		long[] items = this.items;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(items, start + count, items, start, length
					- (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		length -= count;
	}

	public boolean removeAll(LongArray array) {
		int length = this.length;
		int startlength = length;
		long[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			long item = array.get(i);
			for (int ii = 0; ii < length; ii++) {
				if (item == items[ii]) {
					removeIndex(ii);
					length--;
					break;
				}
			}
		}
		return length != startlength;
	}

	public long pop() {
		return items[--length];
	}

	public long shift() {
		return removeIndex(0);
	}

	public long peek() {
		return items[length - 1];
	}

	public long first() {
		if (length == 0) {
			throw new IllegalStateException("Array is empty.");
		}
		return items[0];
	}

	public void clear() {
		length = 0;
	}

	public long[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public long[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(Math.max(8, lengthNeeded));
		return items;
	}

	protected long[] relength(int newlength) {
		long[] newItems = new long[newlength];
		long[] items = this.items;
		System.arraycopy(items, 0, newItems, 0,
				Math.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public void sort() {
		Arrays.sort(items, 0, length);
	}

	public void reverse() {
		long[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			long temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle() {
		long[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			long temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public long random() {
		if (length == 0) {
			return 0;
		}
		return items[MathUtils.random(0, length - 1)];
	}

	public int[] toArray() {
		int[] array = new int[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof LongArray))
			return false;
		LongArray array = (LongArray) object;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}

	public String toString(String separator) {
		if (length == 0)
			return "";
		long[] items = this.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
			buffer.append(separator);
			buffer.append(items[i]);
		}
		return buffer.toString();
	}

	static public LongArray with(long... array) {
		return new LongArray(array);
	}

	public LongArray splice(int begin, int end) {
		LongArray longs = new LongArray(slice(begin, end));
		if (end - begin >= length) {
			items = new long[0];
			length = 0;
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static long[] slice(long[] array, int begin, int end) {
		if (begin > end) {
			throw new RuntimeException();
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		long[] ret = new long[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static long[] slice(long[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public LongArray slice(int size) {
		return new LongArray(slice(this.items, size, this.length));
	}

	public LongArray slice(int begin, int end) {
		return new LongArray(slice(this.items, begin, end));
	}

	public static long[] concat(long[] array, long[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static long[] concat(long[] array, int alen, long[] other, int blen) {
		long[] ret = new long[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public LongArray concat(LongArray o) {
		return new LongArray(concat(this.items, this.length, o.items, o.length));
	}

	public byte[] getBytes() {
		long[] items = this.items;
		byte[] bytes = new byte[this.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = BigInteger.valueOf(items[i]).byteValue();
		}
		return bytes;
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		long[] items = this.items;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
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
