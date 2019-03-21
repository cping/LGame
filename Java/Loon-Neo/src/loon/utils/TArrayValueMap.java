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

public class TArrayValueMap<K, V> {

	private final ObjectMap<K, V> map;
	private final TArray<V> values;

	private final TArray<K> tmpKeyArray;

	public TArrayValueMap() {
		this(16);
	}

	public TArrayValueMap(int capacity) {
		map = new ObjectMap<>(capacity);
		values = new TArray<>(true, capacity);
		tmpKeyArray = new TArray<>(capacity);
	}

	public void put(K key, V value) {
		map.put(key, value);
		values.add(value);
	}

	public V get(K key) {
		return map.get(key);
	}

	public boolean contains(K key) {
		return map.containsKey(key);
	}

	public V getValueAt(int valueIndex) {
		return values.get(valueIndex);
	}

	public V remove(K key) {
		V value = map.remove(key);
		if (value != null) {
			values.removeValue(value, true);
		}
		return value;
	}

	public V removeByValue(V value) {
		K key = findKey(value);
		return remove(key);
	}

	public K findKey(V value) {
		for (ObjectMap.Entry<K, V> entry : map.entries()) {
			if (entry.value == value) {
				return entry.key;
			}
		}
		return null;
	}

	public void clear() {
		map.clear();
		values.clear();
	}

	public int size() {
		return map.size();
	}

	public TArray<V> getValues() {
		return values;
	}

	public TArray<K> getKeys() {
		TArray<K> result = tmpKeyArray;
		result.clear();
		for (K key : map.keys()) {
			result.add(key);
		}
		return result;
	}
}
