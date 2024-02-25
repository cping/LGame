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

import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.TArray;

/**
 * 帧状态保存用类
 */
public class RollbackVar<T> {

	private final IntMap<T> _caches;

	public RollbackVar() {
		this._caches = new IntMap<T>();
	}

	public boolean removeTickBigger(int tick) {
		return removeTick(tick, true);
	}

	public boolean removeTickSmaller(int tick) {
		return removeTick(tick, false);
	}

	protected boolean removeTick(int tick, boolean flag) {
		final IntArray list = new IntArray();
		final int[] keys = _caches.keys();
		for (int i = keys.length - 1; i > -1; i--) {
			final int key = keys[i];
			if (flag) {
				if (tick < key) {
					list.add(key);
				}
			} else {
				if (tick > key) {
					list.add(key);
				}
			}
		}
		final int size = list.length;
		for (int i = 0; i < size; i++) {
			_caches.remove(list.get(i));
		}
		return size > 0;
	}

	public TArray<T> getTicksBigger(int tick) {
		return getTicks(tick, true);
	}

	public TArray<T> getTicksSmaller(int tick) {
		return getTicks(tick, false);
	}

	protected TArray<T> getTicks(int tick, boolean flag) {
		final IntArray list = new IntArray();
		final int[] keys = _caches.keys();
		for (int i = keys.length - 1; i > -1; i--) {
			final int key = keys[i];
			if (flag) {
				if (tick < key) {
					list.add(key);
				}
			} else {
				if (tick > key) {
					list.add(key);
				}
			}
		}
		final int size = list.length;
		final TArray<T> result = new TArray<T>(size);
		for (int i = 0; i < size; i++) {
			result.add(_caches.get(list.get(i)));
		}
		return result;
	}

	public RollbackVar<T> add(int tick, T v) {
		_caches.put(tick, v);
		return this;
	}

	public T remove(int tick) {
		return _caches.remove(tick);
	}

	public T get(int tick) {
		return _caches.get(tick);
	}

	public T up(int tick) {
		final int[] keys = _caches.keys();
		final int size = keys.length;
		for (int i = size - 1; i > -1; i--) {
			final int key = keys[i];
			if (key == tick && i > 0) {
				return _caches.get(keys[i - 1]);
			}
		}
		return null;
	}

	public T down(int tick) {
		final int[] keys = _caches.keys();
		final int size = keys.length;
		for (int i = 0; i < size; i++) {
			final int key = keys[i];
			if (key == tick && i < size - 1) {
				return _caches.get(keys[i + 1]);
			}
		}
		return null;
	}

	public boolean has(int tick) {
		return _caches.containsKey(tick);
	}

	public RollbackVar<T> clear() {
		_caches.clear();
		return this;
	}

	@Override
	public String toString() {
		return _caches.toString();
	}

}
