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
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.BoolArray;
import loon.utils.FloatArray;
import loon.utils.IntArray;
import loon.utils.TArray;

/**
 * ldtk类型转换用类，本质上用java反射方便，但反射跨平台有坑，所以还是手动实现接口吧……
 */
public class LDTKTypes {

	private LDTKTypeConvert _conver;

	private Object convertEnum(String typename, Object o) {
		if (!typename.startsWith(LDTKField.LocalEnumType) && !typename.startsWith(LDTKField.EnumType)) {
			typename = LDTKField.LocalEnumType + LSystem.DOT + typename;
		}
		return _conver != null ? _conver.convert(typename, o) : o;
	}

	private Object convertEntity(String typename, Object o) {
		if (!typename.startsWith(LDTKField.EntityRefType)) {
			typename = LDTKField.EntityRefType + LSystem.DOT + typename;
		}
		return _conver != null ? _conver.convert(typename, o) : o;
	}

	private Object convertTile(String typename, Object o) {
		if (!typename.startsWith(LDTKField.TileType)) {
			typename = LDTKField.TileType + LSystem.DOT + typename;
		}
		return _conver != null ? _conver.convert(typename, o) : o;
	}

	public Object convert(Json.Object o) {
		String ldtkType = o.getString("__type", LSystem.UNKNOWN);
		String valueName = "__value";
		if (ldtkType.equals(LDTKField.IntType)) {
			return o.getInt(valueName);
		} else if (ldtkType.equals(LDTKField.IntArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			IntArray results = new IntArray(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(arrays.getInt(i));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.FloatType)) {
			return o.getInt(valueName);
		} else if (ldtkType.equals(LDTKField.FloatArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			FloatArray results = new FloatArray(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(arrays.getNumber(i));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.BoolType)) {
			return o.getBoolean(valueName);
		} else if (ldtkType.equals(LDTKField.BoolArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			BoolArray results = new BoolArray(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(arrays.getBoolean(i));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.EnumType) || ldtkType.equals(LDTKField.LocalEnumType)) {
			return convertEnum(ldtkType, o.getObject(valueName));
		} else if (ldtkType.equals(LDTKField.EnumArrayType) || ldtkType.equals(LDTKField.LocalEnumArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<Object> results = new TArray<Object>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(convertEnum(ldtkType, arrays.getObject(i)));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.StringType)) {
			return o.getInt(valueName);
		} else if (ldtkType.equals(LDTKField.StringArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<String> results = new TArray<String>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(arrays.getString(i));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.FilePathType)) {
			return o.getString(valueName);
		} else if (ldtkType.equals(LDTKField.FilePathArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<String> results = new TArray<String>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(arrays.getString(i));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.ColorType)) {
			return new LColor(o.getString(valueName));
		} else if (ldtkType.equals(LDTKField.ColorArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<LColor> results = new TArray<LColor>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(new LColor(arrays.getString(i)));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.PointType) || ldtkType.equals(LDTKField.Vector2Type)) {
			return new Vector2f(o.getNumber("cx"), o.getNumber("cy"));
		} else if (ldtkType.equals(LDTKField.PointArrayType) || ldtkType.equals(LDTKField.Vector2ArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<Vector2f> results = new TArray<Vector2f>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(new Vector2f(o.getNumber("cx"), o.getNumber("cy")));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.EntityRefType)) {
			return convertEntity(ldtkType, o.getObject(valueName));
		} else if (ldtkType.equals(LDTKField.EntityRefArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<Object> results = new TArray<Object>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(convertEntity(ldtkType, arrays.getObject(i)));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.TileType)) {
			return convertTile(ldtkType, o.getObject(valueName));
		} else if (ldtkType.equals(LDTKField.TileArrayType)) {
			Json.Array arrays = o.getArray(valueName);
			TArray<Object> results = new TArray<Object>(arrays.length());
			for (int i = 0; i < arrays.length(); i++) {
				results.add(convertTile(ldtkType, arrays.getObject(i)));
			}
			return results;
		} else if (ldtkType.equals(LDTKField.RectangleType)) {
			if (o.isArray(valueName)) {
				Json.Array arrays = o.getArray(valueName);
				return new RectBox(arrays.getNumber(0), arrays.getNumber(1), arrays.getNumber(2), arrays.getNumber(3));
			}
			return new RectBox();
		}
		if (_conver == null) {
			return LSystem.UNKNOWN;
		}
		return _conver.convert(ldtkType, o.isArray(valueName) ? o.getArray(valueName) : o.getObject(valueName));
	}

	public LDTKTypeConvert getConverFilter() {
		return _conver;
	}

	public LDTKTypes setConverFilter(LDTKTypeConvert c) {
		this._conver = c;
		return this;
	}

}
