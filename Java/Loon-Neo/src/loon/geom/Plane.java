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

public class Plane implements XY {

	public enum Side {
		FRONT, BACK, ON_PLANE
	}

	public Vector3f normal;
	public float d;

	public Plane() {
		this(Vector3f.ZERO(), 0);
	}

	public Plane(Vector3f normal, float d) {
		this.normal = new Vector3f(normal);
		this.d = d;
	}

	public Plane(float a, float b, float c, float d) {
		this.normal = new Vector3f(a, b, c);
		this.d = d;

		float length = normal.length();
		normal.scaleSelf(1 / length);
		this.d /= length;
	}

	public Plane(Plane plane) {
		this(plane.normal, plane.d);
	}

	public static Vector3f intersection(Plane p1, Plane p2, Plane p3,
			Vector3f dest) {
		if (dest == null) {
			dest = new Vector3f();
		}

		float c23x, c23y, c23z;
		float c31x, c31y, c31z;
		float c12x, c12y, c12z;

		c23x = p2.normal.y * p3.normal.z - p2.normal.z * p3.normal.y;
		c23y = p2.normal.z * p3.normal.x - p2.normal.x * p3.normal.z;
		c23z = p2.normal.x * p3.normal.y - p2.normal.y * p3.normal.x;

		c31x = p3.normal.y * p1.normal.z - p3.normal.z * p1.normal.y;
		c31y = p3.normal.z * p1.normal.x - p3.normal.x * p1.normal.z;
		c31z = p3.normal.x * p1.normal.y - p3.normal.y * p1.normal.x;

		c12x = p1.normal.y * p2.normal.z - p1.normal.z * p2.normal.y;
		c12y = p1.normal.z * p2.normal.x - p1.normal.x * p2.normal.z;
		c12z = p1.normal.x * p2.normal.y - p1.normal.y * p2.normal.x;

		float dot = p1.normal.dot(c23x, c23y, c23z);
		dest.x = (-c23x * p1.d - c31x * p2.d - c12x * p3.d) / dot;
		dest.y = (-c23y * p1.d - c31y * p2.d - c12y * p3.d) / dot;
		dest.z = (-c23z * p1.d - c31z * p2.d - c12z * p3.d) / dot;

		return dest;
	}

	public Side testPoint(Vector3f point) {
		return testPoint(point.x, point.y, point.z);
	}

	public Side testPoint(float x, float y, float z) {
		float test = normal.dot(x, y, z) + d;

		if (test == 0)
			return Side.ON_PLANE;

		if (test > 0)
			return Side.FRONT;

		return Side.BACK;
	}

	public Plane set(Vector3f normal, float d) {
		this.normal.set(normal);
		this.d = d;

		return this;
	}

	public Plane set(float a, float b, float c, float d) {
		this.normal.set(a, b, c);
		this.d = d;

		float length = normal.length();
		normal.scaleSelf(1 / length);
		this.d /= length;

		return this;
	}

	public Plane set(Plane plane) {
		this.normal.set(plane.normal);
		this.d = plane.d;

		return this;
	}

	@Override
	public int hashCode() {
		int result = normal.hashCode();
		result = 31 * result + (d != +0.0f ? NumberUtils.floatToIntBits(d) : 0);
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
		return NumberUtils.compare(plane.d, d) == 0
				&& normal.equals(plane.normal);
	}

	@Override
	public float getX() {
		return normal.x;
	}

	@Override
	public float getY() {
		return normal.y;
	}

	@Override
	public String toString() {
		return "Plane{" + "normal=" + normal + ", d=" + d + '}';
	}

}
