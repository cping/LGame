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

import java.util.AbstractList;

public class ArraySlice<T> extends AbstractList<T> {

	private final T[] array;

	private int left, right, start, stride;

	public T[] getArray() {
		return array;
	}

	public int getStart() {
		return start;
	}

	public int getLeft() {
		return left;
	}

	public int getRight() {
		return right;
	}

	public int getStride() {
		return stride;
	}

	public ArraySlice(T[] array) {
		this(array, 0, array.length);
	}

	public ArraySlice(T[] array, int start, int end) {
		this(array, start, end, start, 1);
	}

	public ArraySlice(T[] array, int left, int right, int start, int stride) {
		this.array = array;
		this.stride = stride;
		this.start = start = absoluteIndex(start, array.length);
		this.left = left = absoluteIndex(left, array.length);
		this.right = right = absoluteIndex(right, array.length);
		if (right < left) {
			throw new NegativeArraySizeException(
					"Trying to make a slice with left bound " + left
							+ " and right bound" + right + ".");
		}
		if (left != right && (start < left || right <= start)) {
			throw new IndexOutOfBoundsException("Start of slice is at " + start
					+ ", while the bounds are [" + left + ", " + right + "[.");
		}
	}

	private int absoluteIndex(int index, int size) {
		return (index < 0) ? (size + index) : (index);
	}

	@Override
	public int size() {
		return right - left;
	}

	private int index(int _index) {
		int index = _index;
		index = stride * index;
		index = absoluteIndex(index, size());
		if (index < 0 || size() <= index) {
			throw new IndexOutOfBoundsException("index " + _index
					+ " (stride: " + stride + ") in "
					+ this.getClass().getName() + " instance of size " + size());
		}
		return (start + index) % array.length;
	}

	@Override
	public T get(int index) {
		return array[index(index)];
	}

	@Override
	public T set(int index, T element) {
		T out = get(index);
		array[index(index)] = element;
		return out;
	}

	public void shiftLeft() {
		start = (size() > 1) ? index(-1) : start;
	}

	public void shiftRight() {
		start = (size() > 1) ? index(1) : start;
	}

	public void shiftLeftBound() {
		if (left - 1 < 0) {
			throw new IndexOutOfBoundsException();
		}
		left = left - 1;
	}

	public void shiftRightBound() {
		if (right > array.length) {
			throw new IndexOutOfBoundsException();
		}
		right = right + 1;
	}

	public void growSlice() {
		if (right < array.length) {
			shiftRightBound();
		} else if (left > 0) {
			shiftLeftBound();
		} else {
			throw new IllegalStateException(
					"Trying to grow a slice that already occupies the whole array.");
		}
	}

	public void setStride(int stride) {
		this.stride = stride;
	}
}