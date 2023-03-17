/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.LRelease;
import loon.events.ActionUpdate;
import loon.events.Updateable;
import loon.utils.LIterator;
import loon.utils.ObjectMap.Keys;
import loon.utils.OrderedMap;
import loon.utils.SortedList;

public class Emitter<T> implements LRelease {

	protected OrderedMap<T, SortedList<Updateable>> _emitterTable;

	protected int _maxFrameTask;

	protected boolean _active;

	public Emitter(int max) {
		this._emitterTable = new OrderedMap<>();
		this._active = true;
		_maxFrameTask = max;
	}

	public Emitter() {
		this(32);
	}

	public boolean contains(T eventType) {
		return _emitterTable.containsKey(eventType);
	}

	public Updateable getObserverFirst(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			return list.getFirst();
		}
		return null;
	}

	public Updateable getObserverLast(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			return list.getLast();
		}
		return null;
	}

	public Updateable removeObserverFirst(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			return list.removeFirst();
		}
		return null;
	}

	public Updateable removeObserverLast(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			return list.removeLast();
		}
		return null;
	}

	public Emitter<T> addObserver(T eventType, Updateable handler) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list == null) {
			list = new SortedList<>();
		}
		if (!list.contains(handler)) {
			list.add(handler);
		}
		_emitterTable.put(eventType, list);
		return this;
	}

	public boolean removeObserver(T eventType, Updateable handler) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			return list.remove(handler);
		}
		return false;
	}

	public Emitter<T> clearObserver(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (list != null) {
			list.clear();
			return this;
		}
		return this;
	}

	public Emitter<T> onEmits() {
		for (Keys<T> it = _emitterTable.keys(); it.hasNext();) {
			T key = it.next();
			if (null != key) {
				onEmit(key);
			}
		}
		return this;
	}

	public Emitter<T> onEmit(T eventType) {
		if (!_active) {
			return this;
		}
		int taskCount = 0;
		SortedList<Updateable> list = _emitterTable.get(eventType);
		if (null != list) {
			for (LIterator<Updateable> it = list.listIterator(); it.hasNext();) {
				Updateable update = it.next();
				if (update != null) {
					update.action(eventType);
					if (update instanceof ActionUpdate) {
						ActionUpdate au = (ActionUpdate) update;
						if (au.completed()) {
							list.remove(au);
						}
					}
				}
				taskCount++;
				if (taskCount >= _maxFrameTask) {
					break;
				}
			}
		}
		return this;
	}

	public Emitter<T> setMaxFrameTasks(int i) {
		this._maxFrameTask = i;
		return this;
	}

	public int getMaxFrameTasks() {
		return this._maxFrameTask;
	}

	public Emitter<T> start() {
		this._active = true;
		return this;
	}

	public Emitter<T> stop() {
		this._active = false;
		return this;
	}

	public Emitter<T> pause() {
		return stop();
	}

	public Emitter<T> unpause() {
		return start();
	}

	public boolean isActive() {
		return this._active;
	}

	public int size() {
		return _emitterTable.size;
	}

	public int getObserverSize(T eventType) {
		SortedList<Updateable> list = _emitterTable.get(eventType);
		return list == null ? 0 : list.size;
	}

	@Override
	public void close() {
		_active = false;
		if (_emitterTable != null) {
			_emitterTable.clear();
		}
	}
}
