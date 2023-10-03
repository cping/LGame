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
import loon.utils.NumberUtils;
import loon.utils.TArray;

/*最简化的整型坐标处理类,以减少对象大小*/
public class PointI implements XY, SetXY {

	public static boolean pointEquals(int x1, int y1, int x2, int y2, int tolerance) {
		int dx = x2 - x1;
		int dy = y2 - y1;
		return dx * dx + dy * dy < tolerance * tolerance;
	}

	public int x = 0;
	public int y = 0;

	public PointI() {
		this(0, 0);
	}

	public PointI(int size) {
		set(size, size);
	}

	public PointI(int x1, int y1) {
		set(x1, y1);
	}

	public PointI(PointI p) {
		this.x = p.x;
		this.y = p.y;
	}

	public PointI set(int v) {
		return set(v, v);
	}

	public PointI set(int x1, int y1) {
		this.x = x1;
		this.y = y1;
		return this;
	}

	public PointF getF() {
		return new PointF(this.x, this.y);
	}

	public PointI toRoundPoint() {
		return new PointI(MathUtils.floor(this.x), MathUtils.floor(this.y));
	}

	public PointI empty() {
		return this.set(0, 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointI other = (PointI) obj;
		return equals(other);
	}

	public final boolean equals(PointI point) {
		return equals(point.x, point.y);
	}

	public final boolean equals(int x, int y) {
		return MathUtils.equal(x, this.x) && MathUtils.equal(y, this.y);
	}

	public final int length() {
		return (int) MathUtils.sqrt(MathUtils.mul(x, x) + MathUtils.mul(y, y));
	}

	public final PointI negate() {
		x = -x;
		y = -y;
		return this;
	}

	public final PointI offset(int x, int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public final PointI set(PointI p) {
		this.x = p.x;
		this.y = p.y;
		return this;
	}

	public final int distanceTo(PointI p) {
		final int tx = this.x - p.x;
		final int ty = this.y - p.y;
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(int x, int y) {
		final int tx = this.x - x;
		final int ty = this.y - y;
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(PointI p1, PointI p2) {
		final int tx = p2.x - p1.x;
		final int ty = p2.y - p1.y;
		final int u = MathUtils.div(MathUtils.mul(x - p1.x, tx) + MathUtils.mul(y - p1.y, ty),
				MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
		final int ix = p1.x + MathUtils.mul(u, tx);
		final int iy = p1.y + MathUtils.mul(u, ty);
		final int dx = ix - x;
		final int dy = iy - y;
		return (int) MathUtils.sqrt(MathUtils.mul(dx, dx) + MathUtils.mul(dy, dy));
	}

	public PointI cpy(PointI p) {
		return new PointI(p.x, p.y);
	}

	public PointI cpy() {
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
		this.x = MathUtils.floor(x);
	}

	@Override
	public void setY(float y) {
		this.y = MathUtils.floor(y);
	}

	public PointI random() {
		this.x = MathUtils.random(0, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0, LSystem.viewSize.getHeight());
		return this;
	}

	public float[] toArray() {
		return new float[] { x, y };
	}

	public String toCSS() {
		return this.x + "px " + this.y + "px";
	}

	public ObservableXY<PointI> observable(XYChange<PointI> v) {
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
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
