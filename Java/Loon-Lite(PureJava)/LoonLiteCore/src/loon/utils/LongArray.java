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
package loon.utils;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;

public class LongArray implements IArray, LRelease {

	public static LongArray of(long... list) {
		return new LongArray(list);
	}

	/**
	 * 产生一组指定范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static LongArray range(int start, int end) {
		LongArray array = new LongArray(end - start);
		for (int i = start; i < end; i++) {
			array.add(i);
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
	public static LongArray rangeRandom(int begin, int end) {
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
	public static LongArray rangeRandom(int begin, int end, int size) {
		if (begin > end) {
			int temp = begin;
			begin = end;
			end = temp;
		}
		if ((end - begin) < size) {
			throw new LSysException("Size out Range between begin and end !");
		}
		long[] randSeed = new long[end - begin];
		for (int i = begin; i < end; i++) {
			randSeed[i - begin] = i;
		}
		long[] longArrays = new long[size];
		for (int i = 0; i < size; i++) {
			final int len = randSeed.length - i - 1;
			int j = MathUtils.random(len);
			longArrays[i] = randSeed[j];
			randSeed[j] = randSeed[len];
		}
		return new LongArray(longArrays);
	}

	public long[] items;
	public int length;
	public boolean ordered;

	public LongArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
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

	public LongArray(long[] array, int size) {
		this(true, array, 0, size);
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
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(LongArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(LongArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(long... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(long[] array, int offset, int len) {
		long[] items = this.items;
		int lengthNeeded = this.length + len;
		if (lengthNeeded > items.length) {
			items = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, this.length, len);
		this.length += len;
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
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] += value;
	}

	public void mul(int index, int value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] *= value;
	}

	public void insert(int index, long value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		long[] items = this.items;
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

	public LongArray randomFloatArray() {
		return new LongArray(randomArrays());
	}

	public long[] randomArrays() {
		if (length == 0) {
			return new long[0];
		}
		long v = 0L;
		long[] newArrays = CollectionUtils.copyOf(items, length);
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
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
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
			throw new LSysException("end can't be >= length: " + end + " >= " + length);
		}
		if (start > end) {
			throw new LSysException("start can't be > end: " + start + " > " + end);
		}
		long[] items = this.items;
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

	public boolean replace(long src, long dst) {
		int index1 = indexOf(src);
		int index2 = indexOf(dst);
		if (index1 != -1 && index2 == -1) {
			items[index1] = dst;
			return true;
		}
		return false;
	}

	public boolean replaceFirst(long src, long dst) {
		final int idx = indexOf(src);
		if (idx != -1) {
			items[idx] = dst;
			return true;
		}
		return false;
	}

	public boolean replaceLast(long src, long dst) {
		final int idx = lastIndexOf(src);
		if (idx != -1) {
			items[idx] = dst;
			return true;
		}
		return false;
	}

	public int replaceAll(long src, long dst) {
		int count = -1;
		final long[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (src == items[i]) {
				items[i] = dst;
				count++;
			}
		}
		return count;
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
			throw new LSysException("Array is empty.");
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
			relength(MathUtils.max(8, lengthNeeded));
		return items;
	}

	protected long[] relength(int newlength) {
		long[] newItems = new long[newlength];
		long[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public void sort() {
		SortUtils.defaultSort(items, 0, length);
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

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof LongArray))
			return false;
		LongArray array = (LongArray) o;
		int n = length;
		if (n != array.length)
			return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i])
				return false;
		return true;
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
			throw new LSysException("LongArray begin > end");
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

	public long sum() {
		if (length == 0) {
			return 0;
		}
		long total = 0;
		for (int i = length - 1; i > -1; i--) {
			total += items[i];
		}
		return total;
	}

	public long average() {
		if (length == 0) {
			return 0;
		}
		return this.sum() / length;
	}

	public long min() {
		if (this.length == 0) {
			return -1;
		}
		long v = this.items[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long n = this.items[i];
			if (n < v) {
				v = n;
			}
		}
		return v;
	}

	public long max() {
		if (this.length == 0) {
			return -1;
		}
		long v = this.items[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long n = this.items[i];
			if (n > v) {
				v = n;
			}
		}
		return v;
	}

	public LongArray plus(LongArray target) {
		if (target == null) {
			return null;
		}
		if (target.length != this.length) {
			return null;
		}
		final long[] list = target.items;
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v + list[i];
		}
		return this;
	}

	public LongArray plus(final long nv) {
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v + nv;
		}
		return this;
	}

	public LongArray sub(LongArray target) {
		if (target == null) {
			return null;
		}
		if (target.length != this.length) {
			return null;
		}
		final long[] list = target.items;
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v - list[i];
		}
		return this;
	}

	public LongArray sub(final long nv) {
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v - nv;
		}
		return this;
	}

	public LongArray mul(LongArray target) {
		if (target == null) {
			return null;
		}
		if (target.length != this.length) {
			return null;
		}
		final long[] list = target.items;
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v * list[i];
		}
		return this;
	}

	public LongArray mul(final long nv) {
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v * nv;
		}
		return this;
	}

	public LongArray div(LongArray target) {
		if (target == null) {
			return null;
		}
		if (target.length != this.length) {
			return null;
		}
		final long[] list = target.items;
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v / list[i];
		}
		return this;
	}

	public LongArray div(final long nv) {
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v / nv;
		}
		return this;
	}

	public LongArray mod(LongArray target) {
		if (target == null) {
			return null;
		}
		if (target.length != this.length) {
			return null;
		}
		final long[] list = target.items;
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v % list[i];
		}
		return this;
	}

	public LongArray mod(final long nv) {
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			long v = this.items[i];
			this.items[i] = v % nv;
		}
		return this;
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

	@Override
	public int hashCode() {
		long hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = 31 * hashCode + items[i];
		}
		return (int) hashCode;
	}

	public byte[] getBytes(int order) {
		final int size = items.length;
		final long[] items = this.items;
		ArrayByte bytes = new ArrayByte(size * 8);
		bytes.setOrder(order);
		for (int i = 0; i < size; i++) {
			bytes.writeLong(items[i]);
		}
		return bytes.getBytes();
	}

	public LongArray cpy() {
		return new LongArray(this);
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		long[] items = this.items;
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
		return toString(LSystem.COMMA);
	}

	@Override
	public void close() {
		this.length = 0;
		this.items = null;
	}
}
