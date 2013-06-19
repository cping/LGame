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

import loon.core.LRelease;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

/*
 * 0.3.3新增类，以非数组，而是对象链表的方式构建集合.
 * 
 * 此方式在插入数据,删除数据,或者检索索引ID时速度损耗较大.而在检索对象，遍历特定对象的线性关系时速度较快.
 */
public class Array<T> implements LRelease {

	public static class ArrayNode<T> {

		public ArrayNode<T> next;
		public ArrayNode<T> previous;
		public T data;

		public ArrayNode() {
			this.next = null;
			this.previous = null;
			this.data = null;
		}
	}

	private ArrayNode<T> _items = null;

	private int _length;

	private boolean _close;

	private ArrayNode<T> _next_tmp = null, _previous_tmp = null;

	private int _next_count = 0, _previous_count = 0;

	public Array() {
		clear();
	}

	public void insertBetween(Array<T> previous, Array<T> next, Array<T> newNode) {
		insertBetween(previous._items, next._items, newNode._items);
	}

	public void insertBetween(ArrayNode<T> previous, ArrayNode<T> next,
			ArrayNode<T> newNode) {
		if (_close) {
			return;
		}
		if (previous == this._items && next != this._items) {
			this.addFront(newNode.data);
		} else if (previous != this._items && next == this._items) {
			this.addBack(newNode.data);
		} else {
			newNode.next = next;
			newNode.previous = previous;
			previous.next = newNode;
			next.previous = newNode;
		}
	}

	public void add(T data) {
		ArrayNode<T> newNode = new ArrayNode<T>();
		ArrayNode<T> o = this._items.next;
		newNode.data = data;
		if (o == this._items) {
			this.addFront(data);
		} else {
			for (; o != this._items;) {
				o = o.next;
			}
			if (o == this._items) {
				this.addBack(newNode.data);
			}
		}
	}

	public void addFront(T data) {
		if (_close) {
			return;
		}
		ArrayNode<T> newNode = new ArrayNode<T>();
		newNode.data = data;
		newNode.next = this._items.next;
		this._items.next.previous = newNode;
		this._items.next = newNode;
		newNode.previous = this._items;
		_length++;
	}

	public void addBack(T data) {
		if (_close) {
			return;
		}
		ArrayNode<T> newNode = new ArrayNode<T>();
		newNode.data = data;
		newNode.previous = this._items.previous;
		this._items.previous.next = newNode;
		this._items.previous = newNode;
		newNode.next = this._items;
		_length++;
	}

	public T get(int idx) {
		if (_close) {
			return null;
		}
		int size = _length - 1;
		if (0 <= idx && idx <= size) {
			ArrayNode<T> o = this._items.next;
			int count = 0;
			for (; count < idx;) {
				o = o.next;
				count++;
			}
			return o.data;
		} else if (idx == size) {
			return _items.data;
		}
		return null;
	}

	public void set(int idx, T v) {
		if (_close) {
			return;
		}
		int size = _length - 1;
		if (0 <= idx && idx <= size) {
			ArrayNode<T> o = this._items.next;
			int count = 0;
			for (; count < idx;) {
				o = o.next;
				count++;
			}
			o.data = v;
		} else if (idx == size) {
			_items.data = v;
		}
	}

	public ArrayNode<T> node() {
		return _items;
	}

	public boolean contains(T data) {
		return contains(data, false);
	}

	public boolean contains(T data, boolean identity) {
		if (_close) {
			return false;
		}
		ArrayNode<T> o = this._items.next;
		for (; o != this._items;) {
			if ((identity || data == null) && o.data == data) {
				return true;
			} else if (data.equals(o.data)) {
				return true;
			}
			o = o.next;
		}
		return false;
	}

	public int indexOf(T data) {
		return indexOf(data, false);
	}

	public int indexOf(T data, boolean identity) {
		if (_close) {
			return -1;
		}
		int count = 0;
		ArrayNode<T> o = this._items.next;
		for (; o != this._items && count < _length;) {
			if ((identity || data == null) && o.data == data) {
				return count;
			} else if (data.equals(o.data)) {
				return count;
			}
			o = o.next;
			count++;
		}
		return -1;
	}

	public int lastIndexOf(T data) {
		return lastIndexOf(data, false);
	}

	public int lastIndexOf(T data, boolean identity) {
		if (_close) {
			return -1;
		}
		int count = _length - 1;
		ArrayNode<T> o = this._items.previous;
		for (; o != this._items && count > 0;) {
			if ((identity || data == null) && o.data == data) {
				return count;
			} else if (data.equals(o.data)) {
				return count;
			}
			o = o.previous;
			count--;
		}
		return -1;
	}

	public ArrayNode<T> find(T data) {
		if (_close) {
			return null;
		}
		ArrayNode<T> o = this._items.next;
		for (; o != this._items && !data.equals(o.data);) {
			o = o.next;
		}
		if (o == this._items) {
			return null;
		}
		return o;
	}

	public boolean remove(int idx) {
		if (_close) {
			return false;
		}
		int size = _length - 1;
		if (0 <= idx && idx <= size) {
			ArrayNode<T> o = this._items.next;
			int count = 0;
			for (; count < idx;) {
				o = o.next;
				count++;
			}
			return remove(o.data);
		} else if (idx == size) {
			return remove(_items.data);
		}
		return false;
	}

	public boolean remove(T data) {
		if (_close) {
			return false;
		}
		ArrayNode<T> toDelete = this.find(data);
		if (toDelete != this._items && toDelete != null) {
			toDelete.previous.next = toDelete.next;
			toDelete.next.previous = toDelete.previous;
			_length--;
			return true;
		}
		return false;
	}

	public T pop() {
		T o = null;
		if (!isEmpty()) {
			o = this._items.previous.data;
			remove(o);
		}
		return o;
	}

	public boolean isFirst(Array<T> o) {
		if (o._items.previous == this._items) {
			return true;
		}
		return false;
	}

	public boolean isLast(Array<T> o) {
		if (o._items.next == this._items) {
			return true;
		}
		return false;
	}

	public T random() {
		if (_length == 0) {
			return null;
		}
		return get(MathUtils.random(0, _length - 1));
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public T next() {
		if (isEmpty()) {
			return null;
		}
		if (_next_count == 0) {
			_next_tmp = this._items.next;
			_next_count++;
			return _next_tmp.data;
		}
		if (_next_tmp != this._items && _next_count < _length) {
			_next_tmp = _next_tmp.next;
			_next_count++;
			return _next_tmp.data;
		} else {
			stopNext();
			return null;
		}
	}

	public int idxNext() {
		return _next_count;
	}

	public void stopNext() {
		_next_tmp = null;
		_next_count = 0;
	}

	public T previous() {
		if (isEmpty()) {
			return null;
		}
		if (_previous_count == 0) {
			_previous_tmp = this._items.previous;
			_previous_count++;
			return _previous_tmp.data;
		}
		if (_previous_tmp != this._items && _previous_count < _length) {
			_previous_tmp = _previous_tmp.previous;
			_previous_count++;
			return _previous_tmp.data;
		} else {
			stopPrevious();
			return null;
		}
	}

	public int idxPrevious() {
		return _previous_count;
	}

	public void stopPrevious() {
		_previous_tmp = null;
		_previous_count = 0;
	}

	public String toString(char split) {
		if (isEmpty()) {
			return "[]";
		}
		ArrayNode<T> o = this._items.next;
		StringBuilder buffer = new StringBuilder(
				CollectionUtils.INITIAL_CAPACITY);
		buffer.append('[');
		int count = 0;
		for (; o != this._items;) {
			buffer.append(o.data);
			if (count != _length - 1) {
				buffer.append(split);
			}
			o = o.next;
			count++;
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Array)) {
			return false;
		}
		Array<?> array = (Array<?>) o;
		int n = _length;
		if (n != array._length) {
			return false;
		}
		ArrayNode<?> items1 = this._items;
		ArrayNode<?> items2 = array._items;
		for (int i = 0; i < n; i++) {
			Object o1 = items1.next;
			Object o2 = items2.next;
			if (!(o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}
		return true;
	}

	public T first() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this._items.next.data;
		}
	}

	public T last() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this._items.previous.data;
		}
	}

	public void clear() {
		this._close = false;
		this._length = 0;
		this.stopNext();
		this.stopPrevious();
		this._items = null;
		this._items = new ArrayNode<T>();
		this._items.next = this._items;
		this._items.previous = this._items;
	}

	public int size() {
		return _length;
	}

	public boolean isEmpty() {
		return _close || _length == 0 || this._items.next == this._items;
	}

	public boolean isClose() {
		return _close;
	}

	public Array<T> copy() {
		Array<T> newlist = new Array<T>();
		newlist._items.next = this._items;
		newlist._items.previous = this._items;
		return newlist;
	}

	@Override
	public void dispose() {
		_close = true;
		_length = 0;
		_items = null;
	}

	/*
	 * public static void main(String[] args) { Array<String> s = new
	 * Array<String>(); s.add("A"); s.add("B"); s.add("C"); s.add("X");
	 * 
	 * System.out.println(s.first()); System.out.println(s.last());
	 * 
	 * s.set(0, "D");
	 * 
	 * System.out.println(s.contains("A")); System.out.println(s.contains("D"));
	 * System.out.println(s.indexOf("B")); System.out.println(s.indexOf("Z"));
	 * System.out.println("last:" + s.lastIndexOf("C"));
	 * System.out.println(s.remove("X"));
	 * 
	 * for (; s.idxNext() < s.size();) { String t = s.next();
	 * System.out.println("1:" + t); } s.stopNext(); for (;;) { String t =
	 * s.previous(); if (t != null) { System.out.println("2:" + t); } else {
	 * break; } } for (;;) { String t = s.next(); if (t != null) {
	 * System.out.println("3:" + t); } else { break; } } for (;;) { String t =
	 * s.next(); if (t != null) { System.out.println("4:" + t); } else { break;
	 * } }
	 * 
	 * s.clear(); for (;;) { String t = s.next(); if (t != null) {
	 * System.out.println("5:" + t); } else { break; } } }
	 */

}
