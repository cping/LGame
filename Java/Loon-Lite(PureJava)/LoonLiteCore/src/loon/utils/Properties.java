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
package loon.utils;

public class Properties<K, V> extends ObjectMap<K, V> {

	public V getProperty(K name) {
		return getProperty(name, null);
	}

	public V getProperty(K name, V d) {
		V result = get(name);
		if (result == null) {
			result = d;
		}
		return result;
	}

	public boolean hasProperty(K name) {
		return containsKey(name);
	}

	public boolean setProperty(K name, V value) {
		return put(name, value) == value;
	}

}
