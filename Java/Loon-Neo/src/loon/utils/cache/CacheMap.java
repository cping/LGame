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
package loon.utils.cache;

import loon.LRelease;
import loon.utils.ObjectMap;

public abstract class CacheMap<T extends LRelease> implements LRelease {

	private final ObjectMap<String, T> _caches;

	protected CacheMap() {
		_caches = new ObjectMap<String, T>();
	}

	public abstract T create(String key);

	public T get(String key) {
		if (_caches.containsKey(key)) {
			return _caches.get(key);
		}

		T v = create(key);
		_caches.put(key, v);
		return v;
	}

	public boolean put(String key, T v) {
		if (_caches.containsKey(key)) {
			return false;
		}
		_caches.put(key, v);
		return true;
	}

	public boolean containsKey(String key) {
		return _caches.containsKey(key);
	}

	public ObjectMap<String, T> getCacheMap() {
		return _caches;
	}

	public CacheMap<T> clear() {
		for (T v : _caches.values()) {
			if (v != null) {
				v.close();
			}
		}
		_caches.clear();
		return this;
	}

	public int size() {
		return _caches.size();
	}

	public CacheMap<T> remove(String key) {
		if (_caches.containsKey(key)) {
			T v = _caches.get(key);
			if (v != null) {
				v.close();
			}
			_caches.remove(key);
		}
		return this;
	}

	@Override
	public void close() {
		clear();
	}
}
