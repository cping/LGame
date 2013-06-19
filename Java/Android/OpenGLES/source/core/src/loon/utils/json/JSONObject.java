/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.utils.json;

import loon.utils.collection.ArrayMap;

public class JSONObject {

	private ArrayMap _map = new ArrayMap();

	public Object get(String key) {
		return _map.get(key);
	}

	public Object get(int idx) {
		return _map.get(idx);
	}

	public ArrayMap.Entry[] entrys() {
		return _map.toEntrys();
	}

	public boolean hasKey(String key) {
		return (_map.get(key) != null);
	}

	public JSONObject put(String key, Object value) {
		_map.put(key, value);
		return this;
	}

	public Object remove(String key) {
		return _map.remove(key);
	}

	public Object remove(int idx) {
		return _map.remove(idx);
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		boolean first = true;
		for (int i = 0; i < _map.size(); i++) {
			Object value = _map.get(i);
			if (!first) {
				buf.append(",");
			}
			first = false;
			buf.append("\"").append(_map.getKey(i)).append("\"").append(":");
			if (value instanceof String) {
				buf.append("\"").append(value.toString()).append("\"");
			} else {
				buf.append(value.toString());
			}
		}
		buf.append("}");
		return buf.toString();
	}
}
