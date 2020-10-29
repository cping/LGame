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

import loon.LSysException;
import loon.events.QueryEvent;

public class BoolArray implements IArray {

	/**
	 * 产生一组指定范围的数据
	 * 
	 * @param start
	 * @param end
	 * @param value
	 * @return
	 */
	public static BoolArray range(int start, int end, boolean value) {
		BoolArray array = new BoolArray(end - start);
		for (int i = start; i < end; i++) {
			array.add(value);
		}
		return array;
	}

	/**
	 * 产生一组指定范围的随机数据
	 * 
	 * @param begin
	 * @param end
	 * @param size
	 * @return
	 */
	public static BoolArray rangeRandom(int begin, int end) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		int size = end - begin;
		boolean[] boolArrays = new boolean[size];
		for (int i = 0; i < size; i++) {
			boolArrays[i] = MathUtils.randomBoolean();
		}
		return new BoolArray(boolArrays);
	}

	public boolean[] items;
	public int length;
	public boolean ordered;

	public BoolArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public BoolArray(int capacity) {
		this(true, capacity);
	}

	public BoolArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new boolean[capacity];
	}

	public BoolArray(BoolArray array) {
		this.ordered = array.ordered;
		length = array.length;
		items = new boolean[length];
		System.arraycopy(array.items, 0, items, 0, length);
	}

	public BoolArray(boolean[] array) {
		this(true, array, 0, array.length);
	}

	public BoolArray(boolean ordered, boolean[] array, int startIndex, int count) {
		this(ordered, count);
		length = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void unshift(boolean value) {
		if (length > 0) {
			boolean[] items = this.items;
			boolean[] newItems = new boolean[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
		} else {
			add(value);
		}
	}

	public void push(boolean value) {
		add(value);
	}

	public void add(boolean value) {
		boolean[] items = this.items;
		if (length == items.length) {
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(BoolArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(BoolArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(boolean... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(boolean[] array, int offset, int len) {
		boolean[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.length) {
			items = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, this.length, len);
		this.length += len;
	}

	public boolean get(int index) {
		if (index >= length) {
			return false;
		}
		return items[index];
	}

	public void set(int index, boolean value) {
		if (index >= length) {
			int size = length;
			for (int i = size; i < index + 1; i++) {
				add(false);
			}
			items[index] = value;
			return;
		}
		items[index] = value;
	}

	public void incr(int index, boolean value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] = !value;
	}

	public void mul(int index, boolean value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] |= value;
	}

	public void insert(int index, boolean value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		boolean[] items = this.items;
		if (length == items.length)
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, length - index);
		else
			items[length] = items[index];
		length++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new LSysException("first can't be >= length: " + first + " >= " + length);
		if (second >= length)
			throw new LSysException("second can't be >= length: " + second + " >= " + length);
		boolean[] items = this.items;
		boolean firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(boolean value) {
		int i = length - 1;
		boolean[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(boolean value) {
		boolean[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(boolean value) {
		boolean[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(boolean value) {
		boolean[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeIndex(int index) {
		if (index >= length) {
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		boolean[] items = this.items;
		boolean value = items[index];
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
			throw new LSysException("end can't be >= length: " + end + " >= " + length);
		}
		if (start > end) {
			throw new LSysException("start can't be > end: " + start + " > " + end);
		}
		boolean[] items = this.items;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(items, start + count, items, start, length - (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		length -= count;
	}

	public boolean removeAll(BoolArray array) {
		int length = this.length;
		int startlength = length;
		boolean[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			boolean item = array.get(i);
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

	public boolean pop() {
		return items[--length];
	}

	public boolean shift() {
		return removeIndex(0);
	}

	public boolean peek() {
		return items[length - 1];
	}

	public boolean first() {
		if (length == 0) {
			throw new LSysException("Array is empty.");
		}
		return items[0];
	}

	@Override
	public void clear() {
		length = 0;
	}

	public boolean[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public boolean[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(MathUtils.max(8, lengthNeeded));
		return items;
	}

	protected boolean[] relength(int newlength) {
		boolean[] newItems = new boolean[newlength];
		boolean[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public BoolArray sort() {
		for (int i = 0; i < length; i++) {
			boolean swap = false;
			for (int j = 0; j < length - i; j++) {
				if (items[j + 1]) {
					boolean temp = items[j + 1];
					items[j + 1] = items[j];
					items[j] = temp;
					swap = true;
				}
			}
			if (!swap) {
				break;
			}
		}
		return this;
	}

	public void reverse() {
		boolean[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			boolean temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle() {
		boolean[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			boolean temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public boolean random() {
		if (length == 0) {
			return false;
		}
		return items[MathUtils.random(0, length - 1)];
	}

	public BoolArray randomBoolArray() {
		return new BoolArray(randomArrays());
	}

	public boolean[] randomArrays() {
		if (length == 0) {
			return new boolean[0];
		}
		boolean v = false;
		boolean[] newArrays = CollectionUtils.copyOf(items, length);
		for (int i = 0; i < length; i++) {
			v = random();
			for (int j = 0; j < i; j++) {
				if (newArrays[j] == v) {
					v = random();
					j = -1;
				}

			}
			newArrays[i] = v;
		}
		return newArrays;
	}
	
	public boolean[] toArray() {
		boolean[] array = new boolean[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof BoolArray))
			return false;
		BoolArray array = (BoolArray) o;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
	}

	static public BoolArray with(boolean... array) {
		return new BoolArray(array);
	}

	public BoolArray splice(int begin, int end) {
		BoolArray longs = new BoolArray(slice(begin, end));
		if (end - begin >= length) {
			items = new boolean[0];
			length = 0;
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static boolean[] slice(boolean[] array, int begin, int end) {
		if (begin > end) {
			throw new LSysException("BoolArray begin > end");
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		boolean[] ret = new boolean[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static boolean[] slice(boolean[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public BoolArray slice(int size) {
		return new BoolArray(slice(this.items, size, this.length));
	}

	public BoolArray slice(int begin, int end) {
		return new BoolArray(slice(this.items, begin, end));
	}

	public static boolean[] concat(boolean[] array, boolean[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static boolean[] concat(boolean[] array, int alen, boolean[] other, int blen) {
		boolean[] ret = new boolean[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public BoolArray concat(BoolArray o) {
		return new BoolArray(concat(this.items, this.length, o.items, o.length));
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0 || items == null;
	}

	public byte[] getBytes() {
		return getBytes(0);
	}

	public byte[] getBytes(int order) {
		boolean[] items = this.items;
		ArrayByte bytes = new ArrayByte(items.length);
		bytes.setOrder(order);
		for (int i = 0; i < items.length; i++) {
			bytes.writeBoolean(items[i]);
		}
		return bytes.getBytes();
	}

	public BoolArray where(QueryEvent<Boolean> test) {
		BoolArray list = new BoolArray();
		for (int i = 0; i < length; i++) {
			Boolean t = Boolean.valueOf(get(i));
			if (test.hit(t)) {
				list.add(t);
			}
		}
		return list;
	}

	public boolean find(QueryEvent<Boolean> test) {
		for (int i = 0; i < length; i++) {
			Boolean t = Boolean.valueOf(get(i));
			if (test.hit(t)) {
				return t;
			}
		}
		return false;
	}

	public boolean remove(QueryEvent<Boolean> test) {
		for (int i = length - 1; i > -1; i--) {
			Boolean t = Boolean.valueOf(get(i));
			if (test.hit(t)) {
				return removeValue(t);
			}
		}
		return false;
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		boolean[] items = this.items;
		StrBuilder buffer = new StrBuilder(32);
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

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = hashCode * 31 + (items[i] ? 1231 : 1237);
		}
		return hashCode;
	}
}
