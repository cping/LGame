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

	private Plane _near;

	private Plane _far;

	private Plane _left;

	private Plane _right;

	private Plane _bottom;

	private Plane _top;

	public Frustum(Matrix4 m) {
		this._near = new Plane();
		this._far = new Plane();
		this._left = new Plane();
		this._right = new Plane();
		this._top = new Plane();
		this._bottom = new Plane();
		if (m != null) {
			setMatrix(m);
		}
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

	public void setMatrix(Matrix4 matrix) {
		final float[] me = matrix.val;
		final float m11 = me[0];
		final float m12 = me[1];
		final float m13 = me[2];
		final float m14 = me[3];
		final float m21 = me[4];
		final float m22 = me[5];
		final float m23 = me[6];
		final float m24 = me[7];
		final float m31 = me[8];
		final float m32 = me[9];
		final float m33 = me[10];
		final float m34 = me[11];
		final float m41 = me[12];
		final float m42 = me[13];
		final float m43 = me[14];
		final float m44 = me[15];

		final Vector3f nearNormal = this._near.getNormal();
		nearNormal.set(m14 + m13, m24 + m23, m34 + m33);
		this._near.setDistance(m44 + m43);
		this._near.normalize();

		final Vector3f farNormal = this._far.getNormal();
		farNormal.set(m14 - m13, m24 - m23, m34 - m33);
		this._far.setDistance(m44 - m43);
		this._far.normalize();

		final Vector3f leftNormal = this._left.getNormal();
		leftNormal.set(m14 + m11, m24 + m21, m34 + m31);
		this._left.setDistance(m44 + m41);
		this._left.normalize();

		final Vector3f rightNormal = this._right.getNormal();
		rightNormal.set(m14 - m11, m24 - m21, m34 - m31);
		this._right.setDistance(m44 - m41);
		this._right.normalize();

		final Vector3f bottomNormal = this._bottom.getNormal();
		bottomNormal.set(m14 + m12, m24 + m22, m34 + m32);
		this._bottom.setDistance(m44 + m42);
		this._bottom.normalize();

		final Vector3f topNormal = this._top.getNormal();
		topNormal.set(m14 - m12, m24 - m22, m34 - m32);
		this._top.setDistance(m44 - m42);
		this._top.normalize();
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
		for (int i = 0; i < 6; ++i) {
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
		for (int i = 0; i < 6; ++i) {
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
		for (int i = 0; i < 6; ++i) {
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
}
