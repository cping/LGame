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
package loon.teavm.utils;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.teavm.jso.browser.Storage;

public class StorageMap extends AbstractMap<String, String> {

	private class StorageEntry implements Map.Entry<String, String> {
		private final String key;

		public StorageEntry(String key) {
			this.key = key;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Map.Entry)) {
				return false;
			}

			Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
			return eq(key, e.getKey()) && eq(getValue(), e.getValue());
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getValue() {
			return get(key);
		}

		@Override
		public int hashCode() {
			return hashCode(key) ^ hashCode(getValue());
		}

		@Override
		public String setValue(String value) {
			return put(key, value);
		}

		private boolean eq(Object a, Object b) {
			return (a == b) || (a != null && a.equals(b));
		}

		private int hashCode(Object o) {
			return o != null ? o.hashCode() : 0;
		}
	}

	private class StorageEntryIterator implements Iterator<Map.Entry<String, String>> {
		private int index = -1;
		private String lastKey;

		@Override
		public boolean hasNext() {
			return index < size() - 1;
		}

		@Override
		public Map.Entry<String, String> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			index++;
			lastKey = storage.key(index);
			return new StorageEntry(lastKey);
		}

		@Override
		public void remove() {
			if (lastKey == null) {
				throw new IllegalStateException();
			}

			storage.removeItem(lastKey);
			lastKey = null;
			index--;
		}
	}

	private class StorageEntrySet extends AbstractSet<Map.Entry<String, String>> {
		@Override
		public void clear() {
			StorageMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
			Object key = e.getKey();
			Object value = e.getValue();
			return key != null && value != null && value.equals(get(key));
		}

		@Override
		public Iterator<Map.Entry<String, String>> iterator() {
			return new StorageEntryIterator();
		}

		@Override
		public boolean remove(Object o) {
			if (!contains(o)) {
				return false;
			}
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
			return StorageMap.this.remove(entry.getKey()) != null;
		}

		@Override
		public int size() {
			return StorageMap.this.size();
		}
	}

	private final Storage storage;

	public StorageMap(Storage storage) {
		assert storage != null : "storage cannot be null";
		this.storage = storage;
	}

	@Override
	public void clear() {
		storage.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}

		int s = size();
		for (int i = 0; i < s; i++) {
			if (value.equals(storage.getItem(storage.key(i)))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set<Map.Entry<String, String>> entrySet() {
		return new StorageEntrySet();
	}

	@Override
	public String get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}

		return storage.getItem(key.toString());
	}

	@Override
	public String put(String key, String value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}

		String old = storage.getItem(key);
		storage.setItem(key, value);
		return old;
	}

	@Override
	public String remove(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}

		String k = key.toString();
		String old = storage.getItem(k);
		storage.removeItem(k);
		return old;
	}

	@Override
	public int size() {
		return storage.getLength();
	}
}