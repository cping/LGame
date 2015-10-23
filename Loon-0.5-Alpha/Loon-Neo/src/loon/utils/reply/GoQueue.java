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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class GoQueue<E> extends GoCollection<E> implements Queue<E> {
	protected Queue<E> _impl;

	protected static final Listener<Object> DEF = new Listener<Object>() {
	};

	public static abstract class Listener<E> implements Bypass.GoListener {
		public void onOffer(E elem) {
		}

		public void onPoll(E elem) {
		}
	}

	public static <E> GoQueue<E> create() {
		return create(new LinkedList<E>());
	}

	public static <E> GoQueue<E> create(Queue<E> impl) {
		return new GoQueue<E>(impl);
	}

	public GoQueue(Queue<E> impl) {
		_impl = impl;
	}

	public Connection connect(Listener<? super E> listener) {
		return addConnection(listener);
	}

	public Connection connectNotify(Listener<? super E> listener) {
		for (E elem : _impl)
			listener.onOffer(elem);
		return connect(listener);
	}

	public void disconnect(Listener<? super E> listener) {
		removeConnection(listener);
	}

	@Override
	public boolean offer(E element) {
		checkMutate();
		if (!_impl.offer(element)) {
			return false;
		}
		emitOffer(element);
		return true;
	}

	@Override
	public boolean add(E element) {
		checkMutate();
		_impl.add(element);
		emitOffer(element);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> elements) {
		checkMutate();
		for (E elem : elements) {
			add(elem);
		}
		return true;
	}

	@Override
	public E poll() {
		checkMutate();
		E elem = _impl.poll();
		if (elem != null) {
			emitPoll(elem);
		}
		return elem;
	}

	@Override
	public E remove() {
		checkMutate();
		E elem = _impl.remove();
		emitPoll(elem);
		return elem;
	}

	@Override
	public void clear() {
		while (!isEmpty()) {
			remove();
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final Iterator<E> _iter = _impl.iterator();

			public boolean hasNext() {
				return _iter.hasNext();
			}

			public E next() {
				return _iter.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public boolean equals(Object other) {
		return other == this || _impl.equals(other);
	}

	@Override
	public String toString() {
		return "RQueue(" + _impl + ")";
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object object) {
		throw new UnsupportedOperationException();
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
	public E peek() {
		return _impl.peek();
	}

	@Override
	public E element() {
		return _impl.element();
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
		Listener<E> p = (Listener<E>) DEF;
		return p;
	}

	protected void emitOffer(E elem) {
		notify(OFFER, elem, null, null);
	}

	protected void emitPoll(E elem) {
		notify(POLL, elem, null, null);
	}

	@SuppressWarnings("unchecked")
	protected static final Notifier OFFER = new Notifier() {
		public void notify(Object lner, Object elem, Object ignored0,
				Object ignored1) {
			((Listener<Object>) lner).onOffer(elem);
		}
	};

	@SuppressWarnings("unchecked")
	protected static final Notifier POLL = new Notifier() {
		public void notify(Object lner, Object elem, Object ignored0,
				Object ignored1) {
			((Listener<Object>) lner).onPoll(elem);
		}
	};

}
