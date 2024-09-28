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
package loon.geom;

import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;

public class Plane implements XY {

	public enum Side {
		FRONT, BACK, ON_PLANE
	}

	private Vector3f _normal;
	
	private float _dot;

	public Plane() {
		this(Vector3f.ZERO(), 0);
	}

	public Plane(Vector3f normal, float d) {
		this._normal = new Vector3f(normal);
		this._dot = d;
	}

	public Plane(float a, float b, float c, float d) {
		this._normal = new Vector3f(a, b, c);
		this._dot = d;

		float length = _normal.length();
		_normal.scaleSelf(1 / length);
		this._dot /= length;
	}

	public Plane(Plane plane) {
		this(plane._normal, plane._dot);
	}

	public static Vector3f intersection(Plane p1, Plane p2, Plane p3, Vector3f dest) {
		if (dest == null) {
			dest = new Vector3f();
		}

		float c23x, c23y, c23z;
		float c31x, c31y, c31z;
		float c12x, c12y, c12z;

		c23x = p2._normal.y * p3._normal.z - p2._normal.z * p3._normal.y;
		c23y = p2._normal.z * p3._normal.x - p2._normal.x * p3._normal.z;
		c23z = p2._normal.x * p3._normal.y - p2._normal.y * p3._normal.x;

		c31x = p3._normal.y * p1._normal.z - p3._normal.z * p1._normal.y;
		c31y = p3._normal.z * p1._normal.x - p3._normal.x * p1._normal.z;
		c31z = p3._normal.x * p1._normal.y - p3._normal.y * p1._normal.x;

		c12x = p1._normal.y * p2._normal.z - p1._normal.z * p2._normal.y;
		c12y = p1._normal.z * p2._normal.x - p1._normal.x * p2._normal.z;
		c12z = p1._normal.x * p2._normal.y - p1._normal.y * p2._normal.x;

		float dot = p1._normal.dot(c23x, c23y, c23z);
		dest.x = (-c23x * p1._dot - c31x * p2._dot - c12x * p3._dot) / dot;
		dest.y = (-c23y * p1._dot - c31y * p2._dot - c12y * p3._dot) / dot;
		dest.z = (-c23z * p1._dot - c31z * p2._dot - c12z * p3._dot) / dot;

		return dest;
	}

	public Side testPoint(Vector3f point) {
		return testPoint(point.x, point.y, point.z);
	}

	public Side testPoint(float x, float y, float z) {
		float test = _normal.dot(x, y, z) + _dot;

		if (test == 0)
			return Side.ON_PLANE;

		if (test > 0)
			return Side.FRONT;

		return Side.BACK;
	}

	public Plane set(Vector3f normal, float d) {
		this._normal.set(normal);
		this._dot = d;

		return this;
	}

	public Plane set(float a, float b, float c, float d) {
		this._normal.set(a, b, c);
		this._dot = d;

		float length = _normal.length();
		_normal.scaleSelf(1 / length);
		this._dot /= length;

		return this;
	}

	public Plane set(Plane plane) {
		this._normal.set(plane._normal);
		this._dot = plane._dot;

		return this;
	}

	@Override
	public int hashCode() {
		int result = _normal.hashCode();
		result = 31 * result + (_dot != +0.0f ? NumberUtils.floatToIntBits(_dot) : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Plane plane = (Plane) o;
		return NumberUtils.compare(plane._dot, _dot) == 0 && _normal.equals(plane._normal);
	}

	@Override
	public float getX() {
		return _normal.x;
	}

	@Override
	public float getY() {
		return _normal.y;
	}
	
	public float getDot() {
		return this._dot;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Plane");
		builder.kv("normal", _normal).comma().kv("dot", _dot);
		return builder.toString();
	}

}
