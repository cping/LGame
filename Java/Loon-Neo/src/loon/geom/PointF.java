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
import loon.utils.MathUtils;
import loon.utils.TArray;

/*最简化的浮点坐标处理类,以减少对象大小*/
public class PointF implements XY, SetXY {

	public static boolean pointEquals(float x1, float y1, float x2, float y2, float tolerance) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy < tolerance * tolerance;
	}

	public float x = 0;
	public float y = 0;

	public PointF() {
		this(0, 0);
	}

	public PointF(float size) {
		set(size, size);
	}

	public PointF(float x1, float y1) {
		set(x1, y1);
	}

	public PointF(XY p) {
		this(p.getX(), p.getY());
	}

	public PointF(PointF p) {
		this(p.getX(), p.getY());
	}

	public PointF setEmpty() {
		return set(0f, 0f);
	}

	public PointF set(float v) {
		return set(v, v);
	}

	public PointF set(float x1, float y1) {
		this.x = x1;
		this.y = y1;
		return this;
	}

	public PointI getI() {
		return new PointI((int) this.x, (int) this.y);
	}

	public PointF toRoundPoint() {
		return new PointF(MathUtils.floor(this.x), MathUtils.floor(this.y));
	}

	public PointF empty() {
		return set(0f, 0f);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointF other = (PointF) obj;
		return equals(other);
	}

	public final boolean equals(PointF point) {
		return equals(point.x, point.y);
	}

	public final boolean equals(float x, float y) {
		return MathUtils.equal(x, this.x) && MathUtils.equal(y, this.y);
	}

	public final float length() {
		return MathUtils.sqrt(MathUtils.mul(x, x) + MathUtils.mul(y, y));
	}

	public final PointF negate() {
		x = -x;
		y = -y;
		return this;
	}

	public final PointF offset(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public final PointF set(PointF p) {
		this.x = p.x;
		this.y = p.y;
		return this;
	}

	public final float distanceTo(PointF p) {
		final float tx = this.x - p.x;
		final float ty = this.y - p.y;
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final float distanceTo(float x, float y) {
		final float tx = this.x - x;
		final float ty = this.y - y;
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final float distanceTo(PointF p1, PointF p2) {
		final float tx = p2.x - p1.x;
		final float ty = p2.y - p1.y;
		final float u = MathUtils.div(MathUtils.mul(x - p1.x, tx) + MathUtils.mul(y - p1.y, ty),
				MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
		final float ix = p1.x + MathUtils.mul(u, tx);
		final float iy = p1.y + MathUtils.mul(u, ty);
		final float dx = ix - x;
		final float dy = iy - y;
		return MathUtils.sqrt(MathUtils.mul(dx, dx) + MathUtils.mul(dy, dy));
	}

	public PointF cpy(PointF p) {
		return new PointF(p.x, p.y);
	}

	public PointF cpy() {
		return cpy(this);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	public PointF random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		return this;
	}

	public float[] toArray() {
		return new float[] { x, y };
	}

	public String toCSS() {
		return this.x + "px " + this.y + "px";
	}

	public ObservableXY<PointF> observable(XYChange<PointF> v) {
		return ObservableXY.at(v, this, this);
	}

	public boolean inCircle(XYZ cir) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, cir);
	}

	public boolean inCircle(Circle c) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, c.getRealX(), c.getRealY(), c.getDiameter());
	}

	public boolean inCircle(float cx, float cy, float d) {
		return CollisionHelper.checkPointvsCircle(this.x, this.y, cx, cy, d);
	}

	public boolean inEllipse(float ex, float ey, float ew, float eh) {
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, ex, ey, ew, eh);
	}

	public boolean inEllipse(Ellipse e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, e.getRealX(), e.getRealY(), e.getDiameter1(),
				e.getDiameter2());
	}

	public boolean inEllipse(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsEllipse(this.x, this.y, rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public boolean inArc(float ax, float ay, float arcRadius, float arcHeading, float arcAngle) {
		return CollisionHelper.checkPointvsArc(this.x, this.y, ax, ay, arcRadius, arcHeading, arcAngle);
	}

	public boolean inRect(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rect);
	}

	public boolean inRect(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rect.getX(), rect.getY(), rect.getWidth(),
				rect.getHeight());
	}

	public boolean inRect(float rx, float ry, float rw, float rh) {
		return CollisionHelper.checkPointvsAABB(this.x, this.y, rx, ry, rw, rh);
	}

	public boolean inLine(XYZW line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX(), line.getY(), line.getZ(), line.getW());
	}

	public boolean inLine(Line line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX1(), line.getY1(), line.getX2(), line.getY2());
	}

	public boolean inLine(Line line, float size) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkPointvsLine(this.x, this.y, line.getX1(), line.getY1(), line.getX2(), line.getY2(),
				size);
	}

	public boolean inLine(float x1, float y1, float x2, float y2, float size) {
		return CollisionHelper.checkPointvsLine(this.x, this.y, x1, y1, x2, y2, size);
	}

	public boolean inTriangle(Triangle2f t) {
		if (t == null) {
			return false;
		}
		return CollisionHelper.checkPointvsTriangle(this.x, this.y, t.getX1(), t.getY1(), t.getX2(), t.getY2(),
				t.getX3(), t.getY3());
	}

	public boolean inTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		return CollisionHelper.checkPointvsTriangle(this.x, this.y, x1, y1, x2, y2, x3, y3);
	}

	public boolean inPolygon(Polygon poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, poly.getVertices());
	}

	public <T extends XY> boolean inPolygon(TArray<T> poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, poly);
	}

	public boolean collided(Shape shape) {
		if (shape instanceof Polygon) {
			return inPolygon((Polygon) shape);
		} else if (shape instanceof Line) {
			return inLine((Line) shape);
		} else if (shape instanceof RectBox) {
			return inRect((RectBox) shape);
		} else if (shape instanceof Triangle2f) {
			return inTriangle((Triangle2f) shape);
		} else if (shape instanceof Circle) {
			return inCircle((Circle) shape);
		} else if (shape instanceof Ellipse) {
			return inEllipse((Ellipse) shape);
		}
		return CollisionHelper.checkPointvsPolygon(this.x, this.y, shape.getPoints(), 1f);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		return hashCode;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
