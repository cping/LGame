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
package loon.cport.bridge;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import loon.LSystem;

public class VMWeakMap<K, V> {

	private static class WeakKey<K> extends WeakReference<K> {

		private final int hashCodeV;

		WeakKey(K key, ReferenceQueue<K> queue) {
			super(key, queue);
			this.hashCodeV = key.hashCode();
		}

		@Override
		public int hashCode() {
			return hashCodeV;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof WeakKey) {
				Object thisKey = get();
				Object otherKey = ((WeakKey<?>) obj).get();
				return thisKey != null && thisKey.equals(otherKey);
			}
			return false;
		}
	}

	private static class CacheEntry<V> {

		final V value;
		final long expireAt;

		CacheEntry(V value, long ttlMillis) {
			this.value = value;
			this.expireAt = (ttlMillis > 0) ? System.currentTimeMillis() + ttlMillis : -1;
		}

		boolean isExpired() {
			return expireAt > 0 && System.currentTimeMillis() > expireAt;
		}
	}

	private final ConcurrentHashMap<WeakKey<K>, CacheEntry<V>> _map = new ConcurrentHashMap<WeakKey<K>, CacheEntry<V>>();
	private final ReferenceQueue<K> _refQueue = new ReferenceQueue<K>();

	private final int _maxCapacity;

	private final LinkedHashMap<WeakKey<K>, Boolean> _accessOrderMap;

	public VMWeakMap() {
		this(LSystem.HOUR);
	}

	public VMWeakMap(long cleanIntervalMillis) {
		this(LSystem.DEFAULT_MAX_CACHE_SIZE, cleanIntervalMillis);
	}

	public VMWeakMap(int maxCapacity, long cleanIntervalMillis) {
		this._maxCapacity = maxCapacity;
		this._accessOrderMap = new LinkedHashMap<WeakKey<K>, Boolean>(16, 0.75f, true);
	}

	@SuppressWarnings("unchecked")
	public void cleanup() {
		WeakKey<K> wk;
		while ((wk = (WeakKey<K>) _refQueue.poll()) != null) {
			_map.remove(wk);
			_accessOrderMap.remove(wk);
		}
		Iterator<Map.Entry<WeakKey<K>, CacheEntry<V>>> it = _map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<WeakKey<K>, CacheEntry<V>> entry = it.next();
			if (entry.getValue().isExpired()) {
				it.remove();
				_accessOrderMap.remove(entry.getKey());
			}
		}
		while (_accessOrderMap.size() > _maxCapacity) {
			Iterator<WeakKey<K>> lruIt = _accessOrderMap.keySet().iterator();
			if (lruIt.hasNext()) {
				WeakKey<K> oldestKey = lruIt.next();
				lruIt.remove();
				_map.remove(oldestKey);
			}
		}
	}

	public void put(K key, V value) {
		put(key, value, LSystem.MINUTE * 30);
	}

	public void put(K key, V value, long millis) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Key or Value is null !");
		}
		WeakKey<K> wk = new WeakKey<K>(key, _refQueue);
		_map.put(wk, new CacheEntry<V>(value, millis));
		_accessOrderMap.put(wk, Boolean.TRUE);
		cleanup();
	}

	public V get(K key) {
		WeakKey<K> wk = new WeakKey<K>(key, null);
		CacheEntry<V> entry = _map.get(wk);
		if (entry == null) {
			return null;
		}
		if (entry.isExpired()) {
			remove(key);
			return null;
		}
		_accessOrderMap.put(wk, Boolean.TRUE);
		return entry.value;
	}

	public void remove(K key) {
		WeakKey<K> wk = new WeakKey<K>(key, null);
		_map.remove(wk);
		_accessOrderMap.remove(wk);
	}

	public int size() {
		cleanup();
		return _map.size();
	}

	public void clear() {
		_map.clear();
		_accessOrderMap.clear();
		for (; _refQueue.poll() != null;) {
		}
	}

	@Override
	public String toString() {
		cleanup();
		final StringBuilder sbr = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<WeakKey<K>, CacheEntry<V>> e : _map.entrySet()) {
			CacheEntry<V> entry = e.getValue();
			if (!entry.isExpired()) {
				if (!first) {
					sbr.append(", ");
				}
				sbr.append(e.getKey().get()).append("=").append(entry.value);
				first = false;
			}
		}
		sbr.append("}");
		return sbr.toString();
	}
}
