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

import loon.LSystem;
import loon.action.collision.CollisionHelper;

public class Curve extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2f _p1;

	private Vector2f _c1;

	private Vector2f _c2;

	private Vector2f _p2;

	private int _segments;

	public Curve(XY start, Bezier b) {
		this(start, b.controlPoint1, b.controlPoint2, b.endPosition);
	}

	public Curve(XY p1, XY c1, XY c2, XY p2) {
		this(p1, c1, c2, p2, 20);
	}

	public Curve(XY p1, XY c1, XY c2, XY p2, int segments) {
		this._p1 = new Vector2f(p1);
		this._c1 = new Vector2f(c1);
		this._c2 = new Vector2f(c2);
		this._p2 = new Vector2f(p2);
		this._segments = segments;
		pointsDirty = true;
	}

	public boolean intersects(Line line) {
		return CollisionHelper.checkIntersectCubicBezierCurveAndLine(_p1, _c1, _c2, _p2, line.getStart(),
				line.getEnd());
	}

	public boolean intersects(XY xy) {
		return CollisionHelper.checkIntersectCubicBezierCurveAndLine(_p1, _c1, _c2, _p2, Vector2f.at(xy),
				Vector2f.at(x + 1f, y + 1f));
	}

	public Vector2f pointAt(float t) {
		float a = 1 - t;
		float b = t;

		float f1 = a * a * a;
		float f2 = 3 * a * a * b;
		float f3 = 3 * a * b * b;
		float f4 = b * b * b;

		float nx = (_p1.x * f1) + (_c1.x * f2) + (_c2.x * f3) + (_p2.x * f4);
		float ny = (_p1.y * f1) + (_c1.y * f2) + (_c2.y * f3) + (_p2.y * f4);

		return new Vector2f(nx, ny);
	}

	@Override
	protected void createPoints() {
		float step = 1.0f / _segments;
		points = new float[(_segments + 1) * 2];
		for (int i = 0; i < _segments + 1; i++) {
			float t = i * step;
			Vector2f p = pointAt(t);
			points[i * 2] = p.x;
			points[(i * 2) + 1] = p.y;
		}
	}

	@Override
	public Shape transform(Matrix3 transform) {
		float[] pts = new float[8];
		float[] dest = new float[8];
		pts[0] = _p1.x;
		pts[1] = _p1.y;
		pts[2] = _c1.x;
		pts[3] = _c1.y;
		pts[4] = _c2.x;
		pts[5] = _c2.y;
		pts[6] = _p2.x;
		pts[7] = _p2.y;
		transform.transform(pts, 0, dest, 0, 4);

		return new Curve(new Vector2f(dest[0], dest[1]), new Vector2f(dest[2], dest[3]), new Vector2f(dest[4], dest[5]),
				new Vector2f(dest[6], dest[7]));
	}

	@Override
	public boolean closed() {
		return false;
	}

	public boolean equals(Curve e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (_p1.equals(e._p1) && _p2.equals(e._p2) && _c1.equals(e._c1) && _c2.equals(e._c2) && _segments == e._segments
				&& equalsRotateScale(this.rotation, this.scaleX, this.scaleY)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Curve) {
			return equals((Curve) obj);
		}
		return false;
	}

	public Curve copy(Curve e) {
		if (e == null) {
			return this;
		}
		if (equals(e)) {
			return this;
		}
		this._p1.set(e._p1);
		this._c1.set(e._c1);
		this._c2.set(e._c2);
		this._p2.set(e._p2);
		this._segments = e._segments;
		this.x = e.x;
		this.y = e.y;
		this.rotation = e.rotation;
		this.boundingCircleRadius = e.boundingCircleRadius;
		this.minX = e.minX;
		this.minY = e.minY;
		this.maxX = e.maxX;
		this.maxY = e.maxY;
		this.scaleX = e.scaleX;
		this.scaleY = e.scaleY;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	@Override
	public Curve copy(Shape e) {
		if (e instanceof Curve) {
			copy((Curve) e);
		} else {
			super.copy(e);
		}
		return this;
	}

	@Override
	public Curve cpy() {
		return new Curve(this._p1, this._c1, this._c2, this._p2, this._segments);
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, _p1.getX());
		hashCode = prime * LSystem.unite(hashCode, _p1.getY());
		hashCode = prime * LSystem.unite(hashCode, _c1.getX());
		hashCode = prime * LSystem.unite(hashCode, _c1.getY());
		hashCode = prime * LSystem.unite(hashCode, _p2.getX());
		hashCode = prime * LSystem.unite(hashCode, _p2.getY());
		hashCode = prime * LSystem.unite(hashCode, _c2.getX());
		hashCode = prime * LSystem.unite(hashCode, _c2.getY());
		hashCode = prime * LSystem.unite(hashCode, _segments);
		return hashCode;
	}

}
