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

import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;

public class Plane implements XY {

	public static enum PlaneIntersection {
		Back, Front, Intersecting
	}

	public static Vector3f intersectionPointThreePlanes(Plane p1, Plane p2, Plane p3) {
		Vector3f p1Nor = p1.getNormal();
		Vector3f p2Nor = p2.getNormal();
		Vector3f p3Nor = p3.getNormal();
		Vector3f vec30 = p2Nor.cross(p3Nor);
		Vector3f vec31 = p3Nor.cross(p1Nor);
		Vector3f vec32 = p1Nor.cross(p2Nor);
		float a = -p1Nor.dot(vec30);
		float b = -p2Nor.dot(vec31);
		float c = -p3Nor.dot(vec32);
		vec30.scaleSelf(p1._distance / a);
		vec31.scaleSelf(p2._distance / b);
		vec32.scaleSelf(p3._distance / c);
		return vec30.add(vec31).addSelf(vec32);
	}

	public static void fromPoints(Vector3f point0, Vector3f point1, Vector3f point2, Plane o) {

		final float x0 = point0.x;
		final float y0 = point0.y;
		final float z0 = point0.z;
		final float x1 = point1.x - x0;
		final float y1 = point1.y - y0;
		final float z1 = point1.z - z0;
		final float x2 = point2.x - x0;
		final float y2 = point2.y - y0;
		final float z2 = point2.z - z0;
		final float yz = y1 * z2 - z1 * y2;
		final float xz = z1 * x2 - x1 * z2;
		final float xy = x1 * y2 - y1 * x2;
		final float invPyth = 1f / MathUtils.sqrt(yz * yz + xz * xz + xy * xy);
		final float x = yz * invPyth;
		final float y = xz * invPyth;
		final float z = xy * invPyth;
		o._normal.x = x;
		o._normal.y = y;
		o._normal.z = z;
		o._distance = -(x * x0 + y * y0 + z * z0);
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
		dest.x = (-c23x * p1._distance - c31x * p2._distance - c12x * p3._distance) / dot;
		dest.y = (-c23y * p1._distance - c31y * p2._distance - c12y * p3._distance) / dot;
		dest.z = (-c23z * p1._distance - c31z * p2._distance - c12z * p3._distance) / dot;

		return dest;
	}

	public enum Side {
		FRONT, BACK, ON_PLANE
	}

	private final Vector3f _normal = new Vector3f();

	private float _distance;

	public Plane() {
		this(Vector3f.ZERO(), 0);
	}

	public Plane(Vector3f normal, float d) {
		this._normal.set(normal);
		this._distance = d;
	}

	public Plane(float a, float b, float c, float d) {
		this._normal.set(a, b, c);
		this._distance = d;

		float length = _normal.length();
		_normal.scaleSelf(1 / length);
		this._distance /= length;
	}

	public Plane(Plane plane) {
		this(plane._normal, plane._distance);
	}

	public Vector3f getNormal() {
		return _normal;
	}

	public Plane setDistance(float d) {
		this._distance = d;
		return this;
	}

	public float distancePoint(Vector3f p) {
		return this._normal.dot(p) + this._distance;
	}

	public PlaneIntersection intersectsPoint(Vector3f p) {
		float distance = distancePoint(p);
		if (distance > 0) {
			return PlaneIntersection.Front;
		}
		if (distance < 0) {
			return PlaneIntersection.Back;
		}
		return PlaneIntersection.Intersecting;
	}

	public PlaneIntersection intersectsSphere(Sphere s) {
		Vector3f center = s.getCenter();
		float r = s.getRadius();
		float distance = distancePoint(center);
		if (distance > r) {
			return PlaneIntersection.Front;
		}
		if (distance < -r) {
			return PlaneIntersection.Back;
		}
		return PlaneIntersection.Intersecting;
	}

	public PlaneIntersection intersectsAABB(AABB aabb) {
		final Vector3f min = aabb.min();
		final Vector3f max = aabb.max();
		final Vector3f front = new Vector3f();
		final Vector3f back = new Vector3f();
		if (_normal.x >= 0) {
			front.x = max.x;
			back.x = min.x;
		} else {
			front.x = min.x;
			back.x = max.x;
		}
		if (_normal.y >= 0) {
			front.y = max.y;
			back.y = min.y;
		} else {
			front.y = min.y;
			back.y = max.y;
		}
		if (_normal.z >= 0) {
			front.z = max.z;
			back.z = min.z;
		} else {
			front.z = min.z;
			back.z = max.z;
		}
		if (distancePoint(front) < 0) {
			return PlaneIntersection.Back;
		}
		if (distancePoint(back) > 0) {
			return PlaneIntersection.Front;
		}
		return PlaneIntersection.Intersecting;
	}

	public void fromPoints(Vector3f point0, Vector3f point1, Vector3f point2) {
		Plane.fromPoints(point0, point1, point2, this);
	}

	public void normalize() {
		this.normalize(this);
	}

	public void normalize(Plane o) {
		final float factor = 1f / _normal.length();
		o._normal.scaleSelf(factor);
		o._distance = this._distance * factor;
	}

	public Side testPoint(Vector3f point) {
		return testPoint(point.x, point.y, point.z);
	}

	public Side testPoint(float x, float y, float z) {
		float test = _normal.dot(x, y, z) + _distance;

		if (test == 0)
			return Side.ON_PLANE;

		if (test > 0)
			return Side.FRONT;

		return Side.BACK;
	}

	public Plane set(Vector3f normal, float d) {
		this._normal.set(normal);
		this._distance = d;

		return this;
	}

	public Plane set(float a, float b, float c, float d) {
		this._normal.set(a, b, c);
		this._distance = d;

		float length = _normal.length();
		_normal.scaleSelf(1 / length);
		this._distance /= length;

		return this;
	}

	public Plane set(Plane plane) {
		this._normal.set(plane._normal);
		this._distance = plane._distance;

		return this;
	}

	public Plane copyFrom(Plane src) {
		return set(src);
	}

	@Override
	public int hashCode() {
		int result = _normal.hashCode();
		result = 31 * result + (_distance != +0.0f ? NumberUtils.floatToIntBits(_distance) : 0);
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
		return NumberUtils.compare(plane._distance, _distance) == 0 && _normal.equals(plane._normal);
	}

	@Override
	public float getX() {
		return _normal.x;
	}

	@Override
	public float getY() {
		return _normal.y;
	}

	public float getDistance() {
		return this._distance;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Plane");
		builder.kv("normal", _normal).comma().kv("distance", _distance);
		return builder.toString();
	}

}
