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
package loon.utils.reply;

import java.util.Iterator;

import loon.utils.SortedList;

public class Signal<T> {

	public final static <T> Signal<T> create() {
		return new Signal<T>();
	}

	public static interface Listener<T> {
		boolean onSignal(T param1T);
	}

	private final SortedList<Listener<T>> _listeners = new SortedList<>();

	private boolean _stackMode;

	public Signal() {
		this(false);
	}

	public Signal(boolean stackMode) {
		this._stackMode = stackMode;
	}

	public synchronized void add(Listener<T> listener) {
		if (!this._listeners.contains(listener))
			if (this._stackMode) {
				this._listeners.addFirst(listener);
			} else {
				this._listeners.addLast(listener);
			}
	}

	public synchronized void remove(Listener<T> listener) {
		this._listeners.remove(listener);
	}

	public synchronized void removeAll() {
		this._listeners.clear();
	}

	public synchronized void replace(Listener<T> listener) {
		removeAll();
		add(listener);
	}

	public synchronized int getListenerSize() {
		return this._listeners.size();
	}

	public synchronized void dispatch(T t) {
		for (Iterator<Listener<T>> it = _listeners.iterator(); it.hasNext();) {
			Listener<T> listener = it.next();
			if (this._listeners.contains(listener) && listener.onSignal(t))
				return;
		}
	}

}
