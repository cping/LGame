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

import loon.geom.Plane.PlaneIntersection;
import loon.utils.MathUtils;
import loon.utils.TempVars;

public class Frustum {

	public static enum Containment {
		Disjoint, Contains, Intersects
	}

	public static enum FrustumFace {
		Near, Far, Left, Right, Bottom, Top
	}

	public final Plane[] _planes = new Plane[6];

	private Plane _near;

	private Plane _far;

	private Plane _left;

	private Plane _right;

	private Plane _bottom;

	private Plane _top;

	public Frustum() {
		this._near = new Plane();
		this._far = new Plane();
		this._left = new Plane();
		this._right = new Plane();
		this._top = new Plane();
		this._bottom = new Plane();
		this._planes[0] = this._near;
		this._planes[1] = this._far;
		this._planes[2] = this._left;
		this._planes[3] = this._right;
		this._planes[4] = this._bottom;
		this._planes[5] = this._top;
	}

	public Plane getPlane(int idx) {
		switch (idx) {
		case 0:
			return getPlane(FrustumFace.Near);
		case 1:
			return getPlane(FrustumFace.Far);
		case 2:
			return getPlane(FrustumFace.Left);
		case 3:
			return getPlane(FrustumFace.Right);
		case 4:
			return getPlane(FrustumFace.Bottom);
		case 5:
			return getPlane(FrustumFace.Top);
		default:
			return null;
		}
	}

	public Plane getPlane(FrustumFace face) {
		if (face == null) {
			return null;
		}
		switch (face) {
		case Near:
			return this._near;
		case Far:
			return this._far;
		case Left:
			return this._left;
		case Right:
			return this._right;
		case Bottom:
			return this._bottom;
		case Top:
			return this._top;
		default:
			return null;
		}
	}

	public Frustum set(Frustum src) {
		this._near.set(src._near);
		this._far.set(src._far);
		this._left.set(src._left);
		this._right.set(src._right);
		this._bottom.set(src._bottom);
		this._top.set(src._top);
		return this;
	}

	public Plane getNear() {
		return _near;
	}

	public Plane getFar() {
		return _far;
	}

	public Plane getLeft() {
		return _left;
	}

	public Plane getRight() {
		return _right;
	}

	public Plane getBottom() {
		return _bottom;
	}

	public Plane getTop() {
		return _top;
	}

	public boolean intersectsAABB(AABB aabb) {
		Vector3f min = aabb.min();
		Vector3f max = aabb.max();
		Vector3f p = TempVars.get().vec3f1.setEmpty();
		for (int i = 0; i < 6; i++) {
			Plane plane = getPlane(i);
			Vector3f normal = plane.getNormal();
			p.set(normal.x >= 0 ? max.x : min.x, normal.y >= 0 ? max.y : min.y, normal.z >= 0 ? max.z : min.z);
			if (normal.dot(p) < -plane.getDistance()) {
				return false;
			}
		}
		return true;
	}

	public Containment frustumContainsPoint(Vector3f point) {
		float distance = _near.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		distance = _far.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		distance = _left.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		distance = _right.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		distance = _top.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		distance = _bottom.distancePoint(point);
		if (MathUtils.abs(distance) < MathUtils.ZEROTOLERANCE) {
			return Containment.Intersects;
		} else if (distance < 0) {
			return Containment.Disjoint;
		}
		return Containment.Contains;
	}

	public Containment containsAABB(AABB aabb) {
		Vector3f min = aabb.min();
		Vector3f max = aabb.max();
		TempVars vars = TempVars.getClean3f();
		Vector3f p = vars.vec3f1;
		Vector3f n = vars.vec3f2;
		Containment result = Containment.Contains;
		for (int i = 0; i < 6; i++) {
			Plane plane = getPlane(i);
			Vector3f normal = plane.getNormal();
			if (normal.x >= 0) {
				p.x = max.x;
				n.x = min.x;
			} else {
				p.x = min.x;
				n.x = max.x;
			}
			if (normal.y >= 0) {
				p.y = max.y;
				n.y = min.y;
			} else {
				p.y = min.y;
				n.y = max.y;
			}
			if (normal.z >= 0) {
				p.z = max.z;
				n.z = min.z;
			} else {
				p.z = min.z;
				n.z = max.z;
			}
			if (plane.intersectsPoint(p) == PlaneIntersection.Back) {
				return Containment.Disjoint;
			}
			if (plane.intersectsPoint(n) == PlaneIntersection.Back) {
				result = Containment.Intersects;
			}
		}
		return result;
	}

	public Containment containsSphere(Sphere s) {
		Containment result = Containment.Contains;
		for (int i = 0; i < 6; i++) {
			Plane plane = getPlane(i);
			PlaneIntersection intersection = plane.intersectsSphere(s);
			if (intersection == PlaneIntersection.Back) {
				return Containment.Disjoint;
			} else if (intersection == PlaneIntersection.Intersecting) {
				result = Containment.Intersects;
				break;
			}
		}
		return result;
	}

	public boolean containsPoint(XYZ pos) {
		return containsPoint(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean containsPoint(float x, float y, float z) {
		for (int i = 0; i < 6; i++) {
			Plane plane = getPlane(i);
			if (plane.testPoint(x, y, z) == PlaneIntersection.Back) {
				return false;
			}
		}
		return true;
	}

	public boolean containsSphere(XYZ pos, float radius) {
		return containsSphere(pos.getX(), pos.getY(), pos.getZ(), radius);
	}

	public boolean containsSphere(float x, float y, float z, float radius) {
		for (int i = 0; i < 6; i++) {
			if ((_planes[i].getNormal().x * x + _planes[i].getNormal().y * y + _planes[i].getNormal().z * z) < (-radius
					- _planes[i].getDistance())) {
				return false;
			}
		}
		return true;
	}

	public boolean containsSphereWithoutNearFar(float x, float y, float z, float radius) {
		for (int i = 2; i < 6; i++) {
			if ((_planes[i].getNormal().x * x + _planes[i].getNormal().y * y + _planes[i].getNormal().z * z) < (-radius
					- _planes[i].getDistance())) {
				return false;
			}
		}
		return true;
	}
}
