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

import loon.LSysException;
import loon.utils.MathUtils;

public class Sphere {

	private final Vector3f _center = new Vector3f();

	private float _radius;

	public Sphere(Vector3f center, float radius) {
		this._center.set(center);
		this._radius = radius;
	}

	public float getRadius() {
		return _radius;
	}

	public Sphere setRadius(float r) {
		this._radius = r;
		return this;
	}

	public Vector3f getCenter() {
		return _center;
	}

	public Sphere set(Sphere src) {
		this._center.set(src._center);
		this._radius = src._radius;
		return this;
	}

	public Sphere copyFrom(Sphere src) {
		return set(src);
	}

	public void fromAABB(AABB box) {
		_center.x = (box.minX + box.maxX) * 0.5f;
		_center.y = (box.minY + box.maxY) * 0.5f;
		_center.z = (box.minZ + box.maxZ) * 0.5f;
		_radius = _center.distance(box.max());
	}

	public void fromPoints(Vector3f[] points) {
		if (points == null || points.length == 0) {
			throw new LSysException("points must be array and length must > 0");
		}
		int len = points.length;
		Vector3f center = new Vector3f();
		center.x = center.y = center.z = 0;
		for (int i = 0; i < len; ++i) {
			center.addSelf(points[i]);
		}
		center.scaleSelf(1 / len);
		float radius = 0f;
		for (int i = 0; i < len; ++i) {
			float distance = center.distanceSquared(points[i]);
			if (distance > radius) {
				radius = distance;
			}
		}
		this._radius = MathUtils.sqrt(radius);
	}

	public boolean intersectsSphere(Sphere s) {
		float r2 = this._radius + s._radius;
		return _center.distanceSquared(s._center) < r2 * r2;
	}

	public boolean intersectsAABB(AABB aabb) {
		Vector3f max = aabb.max();
		Vector3f min = aabb.min();
		Vector3f closestPoint = new Vector3f();
		closestPoint.set(MathUtils.max(min.x, MathUtils.min(_center.x, max.x)),
				MathUtils.max(min.y, MathUtils.min(_center.y, max.y)),
				MathUtils.max(min.z, MathUtils.min(_center.z, max.z)));
		float distance = _center.distanceSquared(closestPoint);
		return distance <= this._radius * this._radius;
	}
}
