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
package loon.action.map.ldtk;

import loon.Json;
import loon.LSysException;
import loon.utils.ObjectMap;

public class LDTKEntity {

	private final String _id;

	private final float _x, _y;

	private float _pivotX, _pivotY;

	private final ObjectMap<String, Object> _values;

	public LDTKEntity(Json.Object v, LDTKTypes types, LDTKLayer layer) {
		_values = new ObjectMap<String, Object>();
		_id = v.getString("__identifier");
		_x = v.getArray("px").getNumber(0);
		_y = v.getArray("px").getNumber(1);
		final String key = "__pivot";
		if (v.isArray(key)) {
			Json.Array pivot = v.getArray(key);
			_pivotX = pivot.getNumber(0);
			_pivotY = pivot.getNumber(1);
		}
		Json.Array fields = v.getArray("fieldInstances");
		for (int i = 0; i < fields.length(); i++) {
			parseField(fields.getObject(i), types);
		}
	}

	private void parseField(Json.Object jsonValue, LDTKTypes types) {
		String valueName = jsonValue.getString("__identifier");
		try {
			_values.put(valueName, types.convert(jsonValue));
		} catch (LSysException e) {
			throw new LSysException("Could not convert field `" + valueName + "` for entity `" + _id + "`!", e);
		}
	}

	public String getId() {
		return _id;
	}

	public float getX() {
		return _x;
	}

	public float getY() {
		return _y;
	}

	public float getPivotX() {
		return _pivotX;
	}

	public float getPivotY() {
		return _pivotY;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name) {
		if (!_values.containsKey(name)) {
			throw new LSysException("Entity of type `" + _id + "` does not contain a field named `" + name + "`");
		}
		try {
			return (T) _values.get(name);
		} catch (Exception e) {
			throw new LSysException("Could not cast field `" + name + "` to given type!", e);
		}
	}

}
