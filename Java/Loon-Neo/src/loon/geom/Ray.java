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
package loon.geom;

import loon.utils.MathUtils;

public class Ray {

	private final Vector3f _origin = new Vector3f();

	private final Vector3f _direction = new Vector3f();

	public Ray(Vector3f origin, Vector3f direction) {
		set(origin, direction);
	}

	public Ray set(Vector3f origin, Vector3f direction) {
		if (origin != null) {
			_origin.set(origin);
		}
		if (direction != null) {
			_direction.set(direction);
		}
		return this;
	}

	public Ray set(Ray src) {
		if (src == null) {
			return this;
		}
		return set(src._direction, src._origin);
	}

	public Ray copyFrom(Ray src) {
		return set(src);
	}

	public Vector3f getOrigin() {
		return _origin;
	}

	public Vector3f getDirection() {
		return _direction;
	}

	public Vector3f getPoint(float distance) {
		Vector3f dir = this._direction.scale(distance);
		return dir.addSelf(this._origin);
	}

	public float intersectsPlane(Plane p) {
		Vector3f normal = p.getNormal();
		float dir = normal.dot(this._direction);
		if (MathUtils.abs(dir) < MathUtils.ZEROTOLERANCE) {
			return -1f;
		}
		float position = normal.dot(this._origin);
		float distance = (-p.getDistance() - position) / dir;
		if (distance < 0) {
			if (distance < -MathUtils.ZEROTOLERANCE) {
				return -1f;
			}
			distance = 0f;
		}
		return distance;
	}

	public float intersectsAABB(AABB aabb) {
		final Vector3f min = aabb.min();
		final Vector3f max = aabb.max();
		float dirX = _direction.x;
		float dirY = _direction.y;
		float dirZ = _direction.z;
		float oriX = _origin.x;
		float oriY = _origin.y;
		float oriZ = _origin.z;
		float distance = 0;
		float tmax = Float.MAX_VALUE;
		if (MathUtils.abs(dirX) < MathUtils.ZEROTOLERANCE) {
			if (oriX < min.x || oriX > max.x) {
				return -1;
			}
		} else {
			float inverse = 1f / dirX;
			float t1 = (min.x - oriX) * inverse;
			float t2 = (max.x - oriX) * inverse;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			distance = MathUtils.max(t1, distance);
			tmax = MathUtils.min(t2, tmax);
			if (distance > tmax) {
				return -1;
			}
		}
		if (MathUtils.abs(dirY) < MathUtils.ZEROTOLERANCE) {
			if (oriY < min.y || oriY > max.y) {
				return -1;
			}
		} else {
			float inverse = 1f / dirY;
			float t1 = (min.y - oriY) * inverse;
			float t2 = (max.y - oriY) * inverse;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			distance = MathUtils.max(t1, distance);
			tmax = MathUtils.min(t2, tmax);
			if (distance > tmax) {
				return -1;
			}
		}
		if (MathUtils.abs(dirZ) < MathUtils.ZEROTOLERANCE) {
			if (oriZ < min.z || oriZ > max.z) {
				return -1;
			}
		} else {
			float inverse = 1f / dirZ;
			float t1 = (min.z - oriZ) * inverse;
			float t2 = (max.z - oriZ) * inverse;
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			distance = MathUtils.max(t1, distance);
			tmax = MathUtils.min(t2, tmax);
			if (distance > tmax) {
				return -1;
			}
		}
		return distance;
	}

	public float intersectsSphere(Sphere s) {
		Vector3f center = s.getCenter();
		float r = s.getRadius();
		Vector3f m = _origin.sub(center);
		float b = m.dot(_direction);
		float c = m.dot(m) - r * r;
		if (b > 0 && c > 0) {
			return -1f;
		}
		float discriminant = b * b - c;
		if (discriminant < 0) {
			return -1f;
		}
		float distance = -b - MathUtils.sqrt(discriminant);
		if (distance < 0) {
			distance = 0;
		}
		return distance;
	}
}
