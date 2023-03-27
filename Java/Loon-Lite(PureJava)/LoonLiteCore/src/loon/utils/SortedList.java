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

import java.util.Comparator;
import java.util.Iterator;

import loon.LSysException;

public class SortedList<E> implements Iterable<E>, IArray {

	public final static class SortedIterable<T> implements Iterable<T> {

		private final SortedList<T> array;
		private final boolean allowRemove;
		private ListItr<T> iterator1, iterator2;

		private int index;

		public SortedIterable(SortedList<T> array, int idx) {
			this(array, idx, true);
		}

		public SortedIterable(SortedList<T> array, int idx, boolean allowRemove) {
			this.array = array;
			this.index = idx;
			this.allowRemove = allowRemove;
		}

		@Override
		public LIterator<T> iterator() {
			if (iterator1 == null) {
				iterator1 = new ListItr<T>(array, index, allowRemove);
				iterator2 = new ListItr<T>(array, index, allowRemove);
			}
			if (!iterator1.valid) {
				iterator1.reset();
				iterator1.valid = true;
				iterator2.valid = false;
				return iterator1;
			}
			iterator2.reset();
			iterator2.valid = true;
			iterator1.valid = false;
			return iterator2;
		}
	}

	private static class ListItr<E> implements LIterator<E> {

		final boolean allowRemove;

		private SortedList<E> _list;
		private Node<E> lastReturned;
		private Node<E> next;

		private int expectedModCount;

		int nextIndex;
		boolean valid = true;

		ListItr(SortedList<E> l, int idx, boolean removed) {
			this._list = l;
			this.next = (idx == _list.size) ? null : _list.node(idx);
			this.nextIndex = idx;
			this.allowRemove = removed;
			expectedModCount = _list.modCount;
		}

		void reset() {
			this.nextIndex = 0;
			this.next = (nextIndex == _list.size) ? null : _list.node(nextIndex);
			this.expectedModCount = _list.modCount;
		}

		@Override
		public boolean hasNext() {
			if (!valid) {
				throw new LSysException("iterator() cannot be used nested.");
			}
			return nextIndex < _list.size;
		}

		@Override
		public E next() {
			checkForComodification();
			if (!hasNext()) {
				return null;
			}
			lastReturned = next;
			next = next.next;
			nextIndex++;
			return lastReturned.item;
		}

		@Override
		public void remove() {
			if (!allowRemove) {
				throw new LSysException("Remove not allowed.");
			}
			checkForComodification();
			if (lastReturned == null) {
				return;
			}
			Node<E> lastNext = lastReturned.next;
			_list.unlink(lastReturned);
			if (next == lastReturned)
				next = lastNext;
			else
				nextIndex--;
			lastReturned = null;
			expectedModCount++;
		}

		final void checkForComodification() {
			if (_list.modCount != expectedModCount)
				throw new LSysException("SortedList error!");
		}
	}

	private SortedIterable<E> _iterable;

	@Override
	public Iterator<E> iterator() {
		return listIterator(0);
	}

	public LIterator<E> newListIterator() {
		return new ListItr<E>(this, 0, true);
	}

	public LIterator<E> listIterator() {
		return listIterator(0);
	}

	public LIterator<E> listIterator(int index) {
		checkPositionIndex(index);
		if (_iterable == null) {
			_iterable = new SortedIterable<E>(this, index);
		}
		return _iterable.iterator();
	}

	public int modCount = 0;

	public int size = 0;

	Node<E> first;

	Node<E> last;

	public SortedList() {
	}

	public SortedList(final SortedList<? extends E> c) {
		this();
		addAll(c);
	}

	public SortedList(final E[] array) {
		this();
		for (int i = 0; i < array.length; i++) {
			E e = array[i];
			if (e != null)
				add(e);
		}
	}

	private void linkFirst(E e) {
		final Node<E> f = first;
		final Node<E> newNode = new Node<E>(null, e, f);
		first = newNode;
		if (f == null)
			last = newNode;
		else
			f.prev = newNode;
		size++;
		modCount++;
	}

	void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<E>(l, e, null);
		last = newNode;
		if (l == null)
			first = newNode;
		else
			l.next = newNode;
		size++;
		modCount++;
	}

	void linkBefore(E e, Node<E> succ) {
		final Node<E> pred = succ.prev;
		final Node<E> newNode = new Node<E>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null)
			first = newNode;
		else
			pred.next = newNode;
		size++;
		modCount++;
	}

	private E unlinkFirst(Node<E> f) {
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null;
		first = next;
		if (next == null)
			last = null;
		else
			next.prev = null;
		size--;
		modCount++;
		return element;
	}

	private E unlinkLast(Node<E> l) {
		final E element = l.item;
		final Node<E> prev = l.prev;
		l.item = null;
		l.prev = null;
		last = prev;
		if (prev == null)
			first = null;
		else
			prev.next = null;
		size--;
		modCount++;
		return element;
	}

	E unlink(Node<E> x) {
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) {
			first = next;
		} else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			last = prev;
		} else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		size--;
		modCount++;
		return element;
	}

	public E getFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new LSysException("SortedList error!");
		return f.item;
	}

	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new LSysException("SortedList error!");
		return l.item;
	}

	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new LSysException("SortedList error!");
		return unlinkFirst(f);
	}

	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new LSysException("SortedList error!");
		return unlinkLast(l);
	}

	public void addFirst(E e) {
		linkFirst(e);
	}

	public void addLast(E e) {
		linkLast(e);
	}

	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	public int size() {
		return size;
	}

	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	public boolean removeAll(TArray<E> c) {
		boolean modified = false;
		LIterator<E> it = listIterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean removeAll(SortedList<E> c) {
		boolean modified = false;
		LIterator<E> it = listIterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	public boolean remove(Object o) {
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	public boolean addAll(SortedList<? extends E> c) {
		return addAll(size, c);
	}

	public boolean addAll(int index, SortedList<? extends E> c) {
		checkPositionIndex(index);

		Object[] a = c.toArray();
		int numNew = a.length;
		if (numNew == 0)
			return false;

		Node<E> pred, succ;
		if (index == size) {
			succ = null;
			pred = last;
		} else {
			succ = node(index);
			pred = succ.prev;
		}

		for (Object o : a) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			Node<E> newNode = new Node<E>(pred, e, null);
			if (pred == null)
				first = newNode;
			else
				pred.next = newNode;
			pred = newNode;
		}

		if (succ == null) {
			last = pred;
		} else {
			pred.next = succ;
			succ.prev = pred;
		}

		size += numNew;
		modCount++;
		return true;
	}

	public SortedList<E> concat(SortedList<E> array) {
		SortedList<E> all = new SortedList<E>(array);
		all.addAll(all);
		return all;
	}

	public boolean enqueue(E node) {
		return remove(node);
	}

	public E dequeue() {
		return pop();
	}

	@Override
	public void clear() {
		for (Node<E> x = first; x != null;) {
			Node<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		first = last = null;
		size = 0;
		modCount++;
	}

	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	public E set(int index, E element) {
		checkElementIndex(index);
		Node<E> x = node(index);
		E oldVal = x.item;
		x.item = element;
		return oldVal;
	}

	public void add(int index, E element) {
		checkPositionIndex(index);

		if (index == size)
			linkLast(element);
		else
			linkBefore(element, node(index));
	}

	public E remove(int index) {
		checkElementIndex(index);
		return unlink(node(index));
	}

	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new LSysException(outOfBoundsMsg(index));
	}

	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new LSysException(outOfBoundsMsg(index));
	}

	Node<E> node(int index) {
		if (index < (size >> 1)) {
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null)
					return index;
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item))
					return index;
				index++;
			}
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		int index = size;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (x.item == null)
					return index;
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item))
					return index;
			}
		}
		return -1;
	}

	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	public E element() {
		return getFirst();
	}

	public E poll() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	public E remove() {
		return removeFirst();
	}

	public boolean offer(E e) {
		return add(e);
	}

	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	public E peekFirst() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	public E peekLast() {
		final Node<E> l = last;
		return (l == null) ? null : l.item;
	}

	public E pollFirst() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	public E pollLast() {
		final Node<E> l = last;
		return (l == null) ? null : unlinkLast(l);
	}

	public void push(E e) {
		addFirst(e);
	}

	public E pop() {
		return removeFirst();
	}

	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}

	public boolean removeLastOccurrence(Object o) {
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	public void sort(Comparator<E> compar) {
		if (size <= 1) {
			return;
		}
		Node<E> headData = first, dstData = null;
		if (headData == null) {
			return;
		} else {
			E temp;
			for (; headData != null;) {
				dstData = headData.next;
				for (; dstData != null;) {
					if (compar.compare(headData.item, dstData.item) > 0) {
						temp = headData.item;
						headData.item = dstData.item;
						dstData.item = temp;
					}
					dstData = dstData.next;
				}
				headData = headData.next;
			}
		}
	}

	public int getNodeCount() {
		int count = 0;
		Node<E> headData = first;
		for (; headData != null;) {
			count++;
			headData = headData.next;
		}
		return count;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SortedList)) {
			return false;
		}
		SortedList<?> list = (SortedList<?>) o;
		if (size != list.size) {
			return false;
		}
		Node<?> items1 = this.first;
		Node<?> items2 = list.first;
		if (items1 == items2) {
			return true;
		}
		if (items1 == null || items2 == null) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			Object o1 = items1.item;
			Object o2 = items2.item;
			items1 = items1.next;
			items2 = items2.next;
			if (!(o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (Node<E> x = first; x != null; x = x.next) {
			hashCode = 31 * hashCode + (x.item == null ? 0 : x.item.hashCode());
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(',');
	}

	public String toString(char separator) {
		if (size == 0) {
			return "[]";
		}
		StrBuilder buffer = new StrBuilder(32);
		buffer.append('[');
		for (Node<E> x = first; x != null; x = x.next) {
			E o = x.item;
			if (o != null) {
				buffer.append(x.item);
				if (x.next != null) {
					buffer.append(separator);
				}
			}
		}
		buffer.append(']');
		return buffer.toString();
	}
}
