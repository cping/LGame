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
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class LDTKEntity {

	private final String _id;

	private final int _defUid;

	private final float _x, _y;

	private final float _pivotX, _pivotY;

	private final int _width, _height;

	private final LColor _smartColor;

	private final ObjectMap<String, Object> _values;

	private final TArray<String> _tags;

	private final LDTKTileSetUid _tileuid;

	private final Vector2f _grid;

	public LDTKEntity(Json.Object v, LDTKTypes types, LDTKLayer layer) {
		_values = new ObjectMap<String, Object>();
		_tags = new TArray<String>();
		_id = v.getString("__identifier");
		_x = v.getArray("px").getNumber(0);
		_y = v.getArray("px").getNumber(1);
		_width = v.getInt("width");
		_height = v.getInt("height");
		Json.Array grid = v.getArray("__grid");
		if (grid != null) {
			_grid = new Vector2f(grid.getNumber(0), grid.getNumber(1));
		} else {
			_grid = new Vector2f();
		}
		_defUid = v.getInt("defUid");
		_smartColor = new LColor(v.getString("__smartColor"));
		Json.Array tags = v.getArray("__tags");
		for (int i = 0; i < tags.length(); i++) {
			_tags.add(tags.getString(i));
		}
		_tileuid = new LDTKTileSetUid(v.getObject("__tile"));
		final String key = "__pivot";
		if (v.isArray(key)) {
			Json.Array pivot = v.getArray(key);
			_pivotX = pivot.getNumber(0);
			_pivotY = pivot.getNumber(1);
		} else {
			_pivotX = _pivotY = 0f;
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

	public boolean hasSprite() {
		return _tileuid != null && _tileuid._id != -1;
	}

	public int getValueCount() {
		return _values.size;
	}

	public int getDefaultUid() {
		return _defUid;
	}

	public Vector2f getGridPosition() {
		return _grid;
	}

	public LDTKTileSetUid getTileSetUid() {
		return _tileuid;
	}

	public LColor getSmartColor() {
		return _smartColor;
	}

	public String getId() {
		return _id;
	}

	public TArray<String> getTags() {
		return _tags;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
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
