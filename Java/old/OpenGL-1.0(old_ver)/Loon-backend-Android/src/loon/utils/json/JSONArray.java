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

import java.util.List;
import java.util.Set;

import loon.utils.collection.Array;
import loon.utils.collection.Array.ArrayNode;

public class JSONArray {

	private Array<Object> list;

	JSONArray() {
		this.list = new Array<Object>();
	}

	public static JSONArray fromObject(Object... objs) {
		JSONArray json = new JSONArray();
		for (Object s : objs) {
			json.list.add(s);
		}
		return json;
	}

	public static JSONArray fromObject(List<?> objs) {
		JSONArray json = new JSONArray();
		for (Object s : objs) {
			json.list.add(s);
		}
		return json;
	}

	public static JSONArray fromObject(Set<?> objs) {
		JSONArray json = new JSONArray();
		for (Object s : objs) {
			json.list.add(s);
		}
		return json;
	}

	public int length() {
		return list.size();
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Object get(int index) {
		return list.get(index);
	}

	public Integer getInt(int index) {
		Object ob = list.get(index);
		return (ob instanceof Integer) ? (Integer) ob : null;
	}

	public Long getLong(int index) {
		Object ob = list.get(index);
		return (ob instanceof Long) ? (Long) ob : null;
	}

	public Double getDouble(int index) {
		Object ob = list.get(index);
		return (ob instanceof Double) ? (Double) ob : null;
	}

	public Boolean getBool(int index) {
		Object ob = list.get(index);
		return (ob instanceof Boolean) ? (Boolean) ob : null;
	}

	public JSONArray getArray(int index) {
		Object ob = list.get(index);
		return (ob instanceof JSONArray) ? (JSONArray) ob : null;
	}

	public JSONObject getObject(int index) {
		Object ob = list.get(index);
		return (ob instanceof JSONObject) ? (JSONObject) ob : null;
	}

	public boolean isNull(int index) {
		return list.get(index) == null;
	}

	public Array<Object> array() {
		return list.copy();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder().append('{');
		int size = list.size(), i = 0;
		
		ArrayNode<Object> o = this.list.node().next;
		for (; o != this.list.node();) {
			if (o.data instanceof String) {
				str.append('"').append(o.data).append('"');
			} else {
				str.append(o.data);
			}
			if (++i < size) {
				str.append(',');
			}
			o = o.next;
		}
		str.append('}');
		
		return str.toString();
	}

}
