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
package loon.action;

import loon.utils.TArray;

abstract class ActionTweenPool<T> {

	public interface Callback<T> extends loon.utils.reply.Callback<T> {
		
		void onPool(T obj);

		void onUnPool(T obj);
		
	}

	private final TArray<T> _objects;
	private final Callback<T> _callback;

	public ActionTweenPool(int initCapacity, Callback<T> _callback) {
		this._objects = new TArray<T>(initCapacity);
		this._callback = _callback;
	}

	protected abstract T create();

	public T get() {
		T obj = null;
		try {
			obj = _objects.isEmpty() ? create() : _objects.removeIndex(0);
		} catch (Throwable e) {
		}
		if (obj == null) {
			obj = create();
		}
		if (_callback != null) {
			_callback.onUnPool(obj);
		}
		return obj;
	}

	public void free(T obj) {
		if (obj == null)
			return;
		if (!_objects.contains(obj)) {
			if (_callback != null) {
				_callback.onPool(obj);
			}
			_objects.add(obj);
		}
	}

	public void clear() {
		_objects.clear();
	}

	public int size() {
		return _objects.size;
	}

	public void resize(int minCapacity) {
		_objects.ensureCapacity(minCapacity);
	}

}