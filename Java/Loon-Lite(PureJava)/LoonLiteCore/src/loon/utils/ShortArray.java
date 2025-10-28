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

import java.util.Comparator;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.events.QueryEvent;

public class ShortArray implements IArray, LRelease {

	/**
	 * 产生一组指定范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static ShortArray range(int start, int end) {
		ShortArray array = new ShortArray(end - start);
		for (int i = start; i < end; i++) {
			array.add((short) i);
		}
		return array;
	}

	/**
	 * 产生一组指定范围的随机数据
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static ShortArray rangeRandomArrays(int begin, int end) {
		return rangeRandom(begin, end, (end - begin));
	}

	/**
	 * 产生一组指定范围的随机数据
	 * 
	 * @param begin
	 * @param end
	 * @param size
	 * @return
	 */
	public static ShortArray rangeRandom(int begin, int end, int size) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		if ((end - begin) < size) {
			throw new LSysException("Size out Range between begin and end !");
		}
		short[] randSeed = new short[end - begin];
		for (int i = begin; i < end; i++) {
			randSeed[i - begin] = (short) i;
		}
		short[] ShortArrays = new short[size];
		for (int i = 0; i < size; i++) {
			final int len = randSeed.length - i - 1;
			int j = MathUtils.random(len);
			ShortArrays[i] = (Short) randSeed[j];
			randSeed[j] = (Short) randSeed[len];
		}
		return new ShortArray(ShortArrays);
	}

	public short[] items;

	public int length;

	public boolean ordered;

	private boolean _shortDirty;

	public ShortArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public ShortArray(int capacity) {
		this(true, capacity);
	}

	public ShortArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		this.items = new short[capacity];
		this.onShortDirty();
	}

	public ShortArray(ShortArray array) {
		if (array != null) {
			this.ordered = array.ordered;
			length = array.length;
			items = new short[length];
			System.arraycopy(array.items, 0, items, 0, length);
		} else {
			items = new short[0];
		}
		this.onShortDirty();
	}

	public ShortArray(short[] array) {
		this(true, array, 0, array.length);
	}

	public ShortArray(short[] array, int size) {
		this(true, array, 0, size);
	}

	public ShortArray(boolean ordered, short[] array, int startIndex, int count) {
		this(ordered, count);
		this.length = count;
		System.arraycopy(array, startIndex, items, 0, count);
		this.onShortDirty();
	}

	protected void onShortDirty() {
		onShortDirty(true);
	}

	protected void onShortDirty(boolean d) {
		_shortDirty = d;
	}

	public void unshift(short value) {
		if (length > 0) {
			short[] items = this.items;
			short[] newItems = new short[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
			this.onShortDirty();
		} else {
			add(value);
		}
	}

	public void push(short value) {
		add(value);
	}

	public void add(short value) {
		short[] items = this.items;
		if (length == items.length) {
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
		this.onShortDirty();
	}

	public void addAll(ShortArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(ShortArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(short... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(short[] array, int offset, int len) {
		short[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.length) {
			items = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, this.length, len);
		this.length += len;
		this.onShortDirty();
	}

	public Short get(int index) {
		if (index >= length) {
			return 0;
		}
		return items[index];
	}

	public void set(int index, short value) {
		if (index >= length) {
			int size = length;
			for (int i = size; i < index + 1; i++) {
				add((short) ' ');
			}
			items[index] = value;
			return;
		}
		items[index] = value;
		this.onShortDirty();
	}

	public void incr(int index, short value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] += value;
		this.onShortDirty();
	}

	public void mul(int index, short value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] *= value;
		this.onShortDirty();
	}

	public void insert(int index, short value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		short[] items = this.items;
		if (length == items.length)
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, length - index);
		else
			items[length] = items[index];
		length++;
		items[index] = value;
		this.onShortDirty();
	}

	public void swap(int first, int second) {
		if (first >= length)
			throw new LSysException("first can't be >= length: " + first + " >= " + length);
		if (second >= length)
			throw new LSysException("second can't be >= length: " + second + " >= " + length);
		short[] items = this.items;
		Short firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
		this.onShortDirty();
	}

	public boolean contains(short value) {
		int i = length - 1;
		short[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(short value) {
		short[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(short value) {
		short[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(short value) {
		short[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public int removeIndex(int index) {
		if (index >= length) {
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		short[] items = this.items;
		short value = items[index];
		length--;
		if (ordered) {
			System.arraycopy(items, index + 1, items, index, length - index);
		} else {
			items[index] = items[length];
		}
		this.onShortDirty();
		return value;
	}

	public void removeRange(int start, int end) {
		if (end >= length) {
			throw new LSysException("end can't be >= length: " + end + " >= " + length);
		}
		if (start > end) {
			throw new LSysException("start can't be > end: " + start + " > " + end);
		}
		short[] items = this.items;
		int count = end - start + 1;
		if (ordered) {
			System.arraycopy(items, start + count, items, start, length - (start + count));
		} else {
			int lastIndex = this.length - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		length -= count;
		this.onShortDirty();
	}

	public boolean removeAll(ShortArray array) {
		int length = this.length;
		int startlength = length;
		short[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			int item = array.get(i);
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

	public boolean replace(Short src, Short dst) {
		int index1 = indexOf(src);
		int index2 = indexOf(dst);
		if (index1 != -1 && index2 == -1) {
			items[index1] = dst;
			this.onShortDirty();
			return true;
		}
		return false;
	}

	public boolean replaceFirst(Short src, Short dst) {
		final int idx = indexOf(src);
		if (idx != -1) {
			items[idx] = dst;
			this.onShortDirty();
			return true;
		}
		return false;
	}

	public boolean replaceLast(Short src, Short dst) {
		final int idx = lastIndexOf(src);
		if (idx != -1) {
			items[idx] = dst;
			this.onShortDirty();
			return true;
		}
		return false;
	}

	public int replaceAll(Short src, Short dst) {
		int count = -1;
		final short[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (src == items[i]) {
				items[i] = dst;
				count++;
			}
		}
		if (count != -1) {
			this.onShortDirty();
		}
		return count;
	}

	public int pop() {
		return items[--length];
	}

	public int shift() {
		return removeIndex(0);
	}

	public int peek() {
		return items[length - 1];
	}

	public int first() {
		if (length == 0) {
			throw new LSysException("Array is empty.");
		}
		return items[0];
	}

	@Override
	public void clear() {
		this.length = 0;
		this.onShortDirty();
	}

	public short[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public short[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(MathUtils.max(8, lengthNeeded));
		return items;
	}

	protected short[] relength(int newlength) {
		short[] newItems = new short[newlength];
		short[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.items = newItems;
		this.onShortDirty();
		return newItems;
	}

	public ShortArray sort(Comparator<Short> c) {
		final Short[] newItems = new Short[length];
		for (int i = 0; i < length; i++) {
			newItems[i] = this.items[i];
		}
		SortUtils.defaultSort(newItems, c);
		for (int i = 0; i < length; i++) {
			this.items[i] = newItems[i].shortValue();
		}
		this.onShortDirty();
		return this;
	}

	public ShortArray sort() {
		SortUtils.defaultSort(items, 0, length);
		this.onShortDirty();
		return this;
	}

	public void reverse() {
		short[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			Short temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
		this.onShortDirty();
	}

	public void shuffle() {
		short[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			Short temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
		this.onShortDirty();
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}

	public Short random() {
		if (length == 0) {
			return 0;
		}
		return items[MathUtils.random(0, length - 1)];
	}

	public ShortArray randomShortArray() {
		return new ShortArray(randomArrays());
	}

	public short[] randomArrays() {
		if (length == 0) {
			return new short[0];
		}
		short v = (short) -1;
		short[] newArrays = CollectionUtils.copyOf(items, length);
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

	public short[] toArray() {
		short[] array = new short[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	public short[] getThisArray() {
		if (items.length == length) {
			return items;
		}
		return toArray();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof ShortArray))
			return false;
		ShortArray array = (ShortArray) o;
		return equals(array.items);
	}

	public boolean equals(short[] cs) {
		if (cs == null)
			return true;
		final int n = length;
		if (n != cs.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != cs[i])
				return false;
		return true;
	}

	static public ShortArray with(short... array) {
		return new ShortArray(array);
	}

	public ShortArray splice(int begin, int end) {
		ShortArray longs = new ShortArray(slice(begin, end));
		if (end - begin >= length) {
			items = new short[0];
			length = 0;
			this.onShortDirty();
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static short[] slice(short[] array, int begin, int end) {
		if (begin > end) {
			throw new LSysException("ShortArray begin > end");
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		short[] ret = new short[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static short[] slice(short[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public ShortArray slice(int size) {
		return new ShortArray(slice(this.items, size, this.length));
	}

	public ShortArray slice(int begin, int end) {
		return new ShortArray(slice(this.items, begin, end));
	}

	public static short[] concat(short[] array, short[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static short[] concat(short[] array, int alen, short[] other, int blen) {
		short[] ret = new short[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public ShortArray concat(ShortArray o) {
		return new ShortArray(concat(this.items, this.length, o.items, o.length));
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0 || items == null;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public ShortArray cpy() {
		return new ShortArray(this);
	}

	public byte[] getBytes() {
		return getBytes(0);
	}

	public byte[] getBytes(int order) {
		short[] items = this.items;
		int size = items.length;
		ArrayByte bytes = new ArrayByte(size * 2);
		bytes.setOrder(order);
		for (int i = 0; i < size; i++) {
			bytes.writeShort(items[i]);
		}
		return bytes.getBytes();
	}

	public ShortArray where(QueryEvent<Short> test) {
		ShortArray list = new ShortArray();
		for (int i = 0; i < length; i++) {
			Short t = Short.valueOf(get(i));
			if (test.hit(t)) {
				list.add(t);
			}
		}
		return list;
	}

	public Short find(QueryEvent<Short> test) {
		for (int i = 0; i < length; i++) {
			Short t = Short.valueOf(get(i));
			if (test.hit(t)) {
				return t;
			}
		}
		return null;
	}

	public boolean remove(QueryEvent<Short> test) {
		for (int i = length - 1; i > -1; i--) {
			Short t = get(i);
			if (test.hit(t)) {
				return removeValue(t);
			}
		}
		return false;
	}

	public boolean isDirty() {
		return this._shortDirty;
	}

	public ShortArray setDirty(boolean d) {
		this._shortDirty = d;
		return this;
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		final short[] items = this.items;
		final StrBuilder buffer = new StrBuilder(32);
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
	public int hashCode() {
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = 36 * hashCode + items[i];
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(LSystem.COMMA);
	}

	@Override
	public void close() {
		this.items = null;
		this.length = 0;
	}
}