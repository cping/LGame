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

import loon.LSystem;

@SuppressWarnings({ "unchecked" })
public class RingBuffer<T> {

	private int _head;
	private int _tail;
	private int _cap;
	private T[] _items;

	public RingBuffer() {
		this(LSystem.DEFAULT_MAX_CACHE_SIZE);
	}

	public RingBuffer(int capacity) {
		if (capacity < 4) {
			capacity = 4;
		} else if ((capacity & capacity - 1) > 0) {
			capacity--;
			capacity |= capacity >> 1;
			capacity |= capacity >> 2;
			capacity |= capacity >> 4;
			capacity |= capacity >> 8;
			capacity |= capacity >> 16;
			capacity++;
		}
		_cap = capacity - 1;
		_items = (T[]) new Object[_cap];
		reset();
	}

	public boolean isEmpty() {
		return this._head == this._tail;
	}

	public boolean isFull() {
		return (this._tail + 1) % _cap == _head;
	}

	public RingBuffer<T> clear() {
		CollectionUtils.fill(_items, 0, _cap, null);
		return reset();
	}

	public int cap() {
		return _cap;
	}

	public int count() {
		return (_head - _tail) & _cap;
	}

	public int space() {
		return (_tail - _head - 1) & _cap;
	}

	public RingBuffer<T> reset() {
		_head = 0;
		_tail = 0;
		return this;
	}

	public T shift() {
		T result = null;
		if (count() > 0) {
			result = _items[_tail];
			_tail = (_tail + 1) & _cap;
		}
		return result;
	}

	public T pop() {
		T result = null;
		if (count() > 0) {
			_head = (_head - 1) & _cap;
			result = _items[_head];
		}
		return result;
	}

	public T poll(int index) {
		T tmp = _items[index];
		_items[index] = null;
		return tmp;
	}

	public RingBuffer<T> push(T v) {
		if (space() == 0) {
			_tail = (_tail + 1) & _cap;
		}
		_items[_head] = v;
		_head = (_head + 1) & _cap;
		return this;
	}

	public RingBuffer<T> unshift(T v) {
		if (space() == 0) {
			_head = (_head - 1) & _cap;
		}
		_tail = (_tail - 1) & _cap;
		_items[_tail] = v;
		return this;
	}

}
