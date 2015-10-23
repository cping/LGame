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
package loon.utils.reply;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GoList<E> extends GoCollection<E> implements List<E> {
	public static abstract class Listener<E> implements Bypass.GoListener {

		public void onAdd(int index, E elem) {
			onAdd(elem);
		}

		public void onAdd(E elem) {

		}

		public void onSet(int index, E newElem, E oldElem) {
			onSet(index, newElem);
		}

		public void onSet(int index, E newElem) {

		}

		public void onRemove(int index, E elem) {
			onRemove(elem);
		}

		public void onRemove(E elem) {
		}
	}

	public static <E> GoList<E> create() {
		return create(new ArrayList<E>());
	}

	public static <E> GoList<E> create(List<E> impl) {
		return new GoList<E>(impl);
	}

	public GoList(List<E> impl) {
		_impl = impl;
	}

	public Connection connect(Listener<? super E> listener) {
		return addConnection(listener);
	}

	public Connection connectNotify(Listener<? super E> listener) {
		for (int ii = 0, ll = size(); ii < ll; ii++)
			listener.onAdd(ii, get(ii));
		return connect(listener);
	}

	public void disconnect(Listener<? super E> listener) {
		removeConnection(listener);
	}

	public boolean removeForce(E elem) {
		checkMutate();
		int index = _impl.indexOf(elem);
		if (index >= 0) {
			_impl.remove(index);
		}
		emitRemove(index, elem);
		return (index >= 0);
	}

	@Override
	public boolean add(E element) {
		add(size(), element);
		return true;
	}

	@Override
	public void add(int index, E element) {
		checkMutate();
		_impl.add(index, element);
		emitAdd(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return addAll(size(), collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> elements) {
		checkMutate();
		for (E elem : elements) {
			add(index++, elem);
		}
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}

	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		final ListIterator<E> iiter = _impl.listIterator();
		return new ListIterator<E>() {
			public void add(E elem) {
				checkMutate();
				int index = iiter.nextIndex();
				iiter.add(elem);
				emitAdd(index, elem);
			}

			public boolean hasNext() {
				return iiter.hasNext();
			}

			public boolean hasPrevious() {
				return iiter.hasPrevious();
			}

			public E next() {
				return (_current = iiter.next());
			}

			public int nextIndex() {
				return iiter.nextIndex();
			}

			public E previous() {
				return (_current = iiter.previous());
			}

			public int previousIndex() {
				return iiter.previousIndex();
			}

			public void remove() {
				checkMutate();
				int index = iiter.previousIndex();
				iiter.remove();
				emitRemove(index, _current);
			}

			public void set(E elem) {
				checkMutate();
				iiter.set(elem);
				emitSet(iiter.previousIndex(), elem, _current);
				_current = elem;
			}

			protected E _current;
		};
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean modified = false;
		for (Iterator<E> iter = iterator(); iter.hasNext();) {
			if (!collection.contains(iter.next())) {
				iter.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean modified = false;
		for (Object o : collection) {
			modified |= remove(o);
		}
		return modified;
	}

	@Override
	public boolean remove(Object object) {
		checkMutate();
		int index = _impl.indexOf(object);
		if (index < 0) {
			return false;
		}
		_impl.remove(index);
		@SuppressWarnings("unchecked")
		E elem = (E) object;
		emitRemove(index, elem);
		return true;
	}

	@Override
	public E remove(int index) {
		checkMutate();
		E removed = _impl.remove(index);
		emitRemove(index, removed);
		return removed;
	}

	@Override
	public E set(int index, E element) {
		checkMutate();
		E removed = _impl.set(index, element);
		emitSet(index, element, removed);
		return removed;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new GoList<E>(_impl.subList(fromIndex, toIndex));
	}

	@Override
	public boolean equals(Object other) {
		return other == this || _impl.equals(other);
	}

	@Override
	public String toString() {
		return "RList(" + _impl + ")";
	}

	@Override
	public int hashCode() {
		return _impl.hashCode();
	}

	@Override
	public int size() {
		return _impl.size();
	}

	@Override
	public boolean isEmpty() {
		return _impl.isEmpty();
	}

	@Override
	public E get(int index) {
		return _impl.get(index);
	}

	@Override
	public int indexOf(Object element) {
		return _impl.indexOf(element);
	}

	@Override
	public int lastIndexOf(Object element) {
		return _impl.lastIndexOf(element);
	}

	@Override
	public boolean contains(Object object) {
		return _impl.contains(object);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return _impl.containsAll(collection);
	}

	@Override
	public void clear() {
		while (!isEmpty())
			remove(0);
	}

	@Override
	public Object[] toArray() {
		return _impl.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return _impl.toArray(array);
	}

	@Override
	Listener<E> placeholderListener() {
		@SuppressWarnings("unchecked")
		Listener<E> p = (Listener<E>) NOOP;
		return p;
	}

	protected void emitAdd(int index, E elem) {
		notify(ADD, index, elem, null);
	}

	protected void emitSet(int index, E newElem, E oldElem) {
		notify(SET, index, newElem, oldElem);
	}

	protected void emitRemove(int index, E elem) {
		notify(REMOVE, index, elem, null);
	}

	protected List<E> _impl;

	protected static final Listener<Object> NOOP = new Listener<Object>() {
	};

	@SuppressWarnings("unchecked")
	protected static final Notifier ADD = new Notifier() {
		public void notify(Object lner, Object index, Object elem,
				Object ignored) {
			((Listener<Object>) lner).onAdd((Integer) index, elem);
		}
	};

	@SuppressWarnings("unchecked")
	protected static final Notifier SET = new Notifier() {
		public void notify(Object lner, Object index, Object newElem,
				Object oldElem) {
			((Listener<Object>) lner).onSet((Integer) index, newElem, oldElem);
		}
	};

	@SuppressWarnings("unchecked")
	protected static final Notifier REMOVE = new Notifier() {
		public void notify(Object lner, Object index, Object elem,
				Object ignored) {
			((Listener<Object>) lner).onRemove((Integer) index, elem);
		}
	};
}
