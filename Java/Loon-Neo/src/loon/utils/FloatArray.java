/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import java.util.Arrays;

import loon.LSysException;
import loon.event.QueryEvent;

public class FloatArray implements IArray {

	public static FloatArray range(int start, int end) {
		FloatArray array = new FloatArray(end - start);
		for (int i = start; i < end; i++) {
			array.add(i);
		}
		return array;
	}

	public float[] items;
	public int length;
	public boolean ordered;

	public FloatArray() {
		this(true, CollectionUtils.INITIAL_CAPACITY);
	}

	public FloatArray(int capacity) {
		this(true, capacity);
	}

	public FloatArray(boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new float[capacity];
	}

	public FloatArray(FloatArray array) {
		this.ordered = array.ordered;
		length = array.length;
		items = new float[length];
		System.arraycopy(array.items, 0, items, 0, length);
	}

	public FloatArray(float[] array) {
		this(true, array, 0, array.length);
	}

	public FloatArray(boolean ordered, float[] array, int startIndex, int count) {
		this(ordered, count);
		length = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void unshift(float value) {
		if (length > 0) {
			float[] items = this.items;
			float[] newItems = new float[length + 1];
			newItems[0] = value;
			System.arraycopy(items, 0, newItems, 1, length);
			this.length = newItems.length;
			this.items = newItems;
		} else {
			add(value);
		}
	}

	public void push(float value) {
		add(value);
	}

	public void add(float value) {
		float[] items = this.items;
		if (length == items.length) {
			items = relength(MathUtils.max(8, (int) (length * 1.75f)));
		}
		items[length++] = value;
	}

	public void addAll(FloatArray array) {
		addAll(array, 0, array.length);
	}

	public void addAll(FloatArray array, int offset, int length) {
		if (offset + length > array.length)
			throw new LSysException(
					"offset + length must be <= length: " + offset + " + " + length + " <= " + array.length);
		addAll(array.items, offset, length);
	}

	public void addAll(float... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(float[] array, int offset, int length) {
		float[] items = this.items;
		int lengthNeeded = length + length;
		if (lengthNeeded > items.length) {
			items = relength(MathUtils.max(8, (int) (lengthNeeded * 1.75f)));
		}
		System.arraycopy(array, offset, items, length, length);
		length += length;
	}

	public float get(int index) {
		if (index >= length) {
			return 0;
		}
		return items[index];
	}

	public void set(int index, float value) {
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

	public void incr(int index, float value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] += value;
	}

	public void mul(int index, float value) {
		if (index >= length)
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		items[index] *= value;
	}

	public void insert(int index, float value) {
		if (index > length) {
			throw new LSysException("index can't be > length: " + index + " > " + length);
		}
		float[] items = this.items;
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
		float[] items = this.items;
		float firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains(float value) {
		int i = length - 1;
		float[] items = this.items;
		while (i >= 0)
			if (items[i--] == value)
				return true;
		return false;
	}

	public int indexOf(float value) {
		float[] items = this.items;
		for (int i = 0, n = length; i < n; i++)
			if (items[i] == value)
				return i;
		return -1;
	}

	public int lastIndexOf(float value) {
		float[] items = this.items;
		for (int i = length - 1; i >= 0; i--)
			if (items[i] == value)
				return i;
		return -1;
	}

	public boolean removeValue(float value) {
		float[] items = this.items;
		for (int i = 0, n = length; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public float removeIndex(int index) {
		if (index >= length) {
			throw new LSysException("index can't be >= length: " + index + " >= " + length);
		}
		float[] items = this.items;
		float value = items[index];
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
		float[] items = this.items;
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

	public boolean removeAll(FloatArray array) {
		int length = this.length;
		int startlength = length;
		float[] items = this.items;
		for (int i = 0, n = array.length; i < n; i++) {
			float item = array.get(i);
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

	public float pop() {
		return items[--length];
	}

	public float shift() {
		return removeIndex(0);
	}

	public float peek() {
		return items[length - 1];
	}

	public float first() {
		if (length == 0) {
			throw new LSysException("Array is empty.");
		}
		return items[0];
	}

	public void clear() {
		length = 0;
	}

	public float[] shrink() {
		if (items.length != length)
			relength(length);
		return items;
	}

	public float[] ensureCapacity(int additionalCapacity) {
		int lengthNeeded = length + additionalCapacity;
		if (lengthNeeded > items.length)
			relength(MathUtils.max(8, lengthNeeded));
		return items;
	}

	protected float[] relength(int newlength) {
		float[] newItems = new float[newlength];
		float[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, MathUtils.min(length, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public FloatArray sort() {
		Arrays.sort(items, 0, length);
		return this;
	}

	public boolean isSorted(boolean order) {
		final float[] arrays = this.items;
		int orderCount = 0;
		float temp = -1;
		float v = order ? Float.MIN_VALUE : Float.MAX_VALUE;
		for (int i = 0; i < length; i++) {
			temp = v;
			v = arrays[i];
			if (order) {
				if (temp <= v) {
					orderCount++;
				}
			} else {
				if (temp >= v) {
					orderCount++;
				}
			}
		}
		return orderCount == length;
	}

	public void reverse() {
		float[] items = this.items;
		for (int i = 0, lastIndex = length - 1, n = length / 2; i < n; i++) {
			int ii = lastIndex - i;
			float temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle() {
		float[] items = this.items;
		for (int i = length - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			float temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void truncate(int newlength) {
		if (length > newlength)
			length = newlength;
	}
	
	public float random() {
		if (length == 0) {
			return 0;
		}
		return items[MathUtils.random(0, length - 1)];
	}

	public FloatArray randomFloatArray() {
		return new FloatArray(randomArrays());
	}

	public float[] randomArrays() {
		if (length == 0) {
			return new float[0];
		}
		float v = 0f;
		float[] newArrays = CollectionUtils.copyOf(items, length);
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

	public float[] toArray() {
		float[] array = new float[length];
		System.arraycopy(items, 0, array, 0, length);
		return array;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof FloatArray))
			return false;
		FloatArray array = (FloatArray) object;
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
		float[] items = this.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(items[0]);
		for (int i = 1; i < length; i++) {
			buffer.append(separator);
			buffer.append(items[i]);
		}
		return buffer.toString();
	}

	static public FloatArray with(float... array) {
		return new FloatArray(array);
	}

	public FloatArray splice(int begin, int end) {
		FloatArray longs = new FloatArray(slice(begin, end));
		if (end - begin >= length) {
			items = new float[0];
			length = 0;
			return longs;
		} else {
			removeRange(begin, end - 1);
		}
		return longs;
	}

	public static float[] slice(float[] array, int begin, int end) {
		if (begin > end) {
			throw new LSysException("FloatArray begin > end");
		}
		if (begin < 0) {
			begin = array.length + begin;
		}
		if (end < 0) {
			end = array.length + end;
		}
		int elements = end - begin;
		float[] ret = new float[elements];
		System.arraycopy(array, begin, ret, 0, elements);
		return ret;
	}

	public static float[] slice(float[] array, int begin) {
		return slice(array, begin, array.length);
	}

	public FloatArray slice(int size) {
		return new FloatArray(slice(this.items, size, this.length));
	}

	public FloatArray slice(int begin, int end) {
		return new FloatArray(slice(this.items, begin, end));
	}

	public static float[] concat(float[] array, float[] other) {
		return concat(array, array.length, other, other.length);
	}

	public static float[] concat(float[] array, int alen, float[] other, int blen) {
		float[] ret = new float[alen + blen];
		System.arraycopy(array, 0, ret, 0, alen);
		System.arraycopy(other, 0, ret, alen, blen);
		return ret;
	}

	public FloatArray concat(FloatArray o) {
		return new FloatArray(concat(this.items, this.length, o.items, o.length));
	}

	@Override
	public int size() {
		return length;
	}

	@Override
	public boolean isEmpty() {
		return length == 0 || items == null;
	}

	public FloatArray where(QueryEvent<Float> test) {
		FloatArray list = new FloatArray();
		for (int i = 0; i < length; i++) {
			Float t = Float.valueOf(get(i));
			if (test.hit(t)) {
				list.add(t);
			}
		}
		return list;
	}

	public Float find(QueryEvent<Float> test) {
		for (int i = 0; i < length; i++) {
			Float t = Float.valueOf(get(i));
			if (test.hit(t)) {
				return t;
			}
		}
		return null;
	}

	public boolean remove(QueryEvent<Float> test) {
		for (int i = length - 1; i > -1; i--) {
			float t = get(i);
			if (test.hit(t)) {
				return removeValue(t);
			}
		}
		return false;
	}

	public float sum() {
		if (length == 0) {
			return 0;
		}
		float total = 0;
		for (int i = length - 1; i > -1; i--) {
			total += items[i];
		}
		return total;
	}

	public float average() {
		if (length == 0) {
			return 0;
		}
		return this.sum() / length;
	}

	public float min() {
		float v = this.items[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			float n = this.items[i];
			if (n < v) {
				v = n;
			}
		}
		return v;
	}

	public float max() {
		float v = this.items[0];
		final int size = this.length;
		for (int i = size - 1; i > -1; i--) {
			float n = this.items[i];
			if (n > v) {
				v = n;
			}
		}
		return v;
	}

	public byte[] getBytes() {
		return getBytes(0);
	}

	public byte[] getBytes(int order) {
		float[] items = this.items;
		ArrayByte bytes = new ArrayByte(items.length * 4);
		bytes.setOrder(order);
		for (int i = 0; i < items.length; i++) {
			bytes.writeFloat(items[i]);
		}
		return bytes.getBytes();
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = length - 1; i > -1; i--) {
			hashCode = 31 * hashCode + NumberUtils.floatToIntBits(items[i]);
		}
		return hashCode;
	}

	public String toString(char split) {
		if (length == 0) {
			return "[]";
		}
		float[] items = this.items;
		StringBuilder buffer = new StringBuilder(CollectionUtils.INITIAL_CAPACITY);
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
