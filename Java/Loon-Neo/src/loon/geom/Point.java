/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.geom;

import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Point extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Point at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Point();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 1) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				return new Point(x, y);
			} catch (Exception ex) {
			}
		}
		return new Point();
	}

	public final static Point at(float x, float y) {
		return new Point(x, y);
	}

	public static final int POINT_CONVEX = 1;

	public static final int POINT_CONCAVE = 2;

	public Point() {
		this(0f, 0f);
	}

	public Point(float x, float y) {
		this.checkPoints();
		this.setLocation(x, y);
	}

	public Point(Point p) {
		this.checkPoints();
		this.setLocation(p);
	}

	@Override
	public boolean contains(XY xy) {
		if (xy == null) {
			return false;
		}
		return contains(xy.getX(), xy.getY());
	}

	@Override
	public boolean contains(float px, float py) {
		return CollisionHelper.checkPointvsAABB(px, py, x, y, 1f, 1f);
	}

	@Override
	public Shape transform(Matrix3 transform) {
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		return new Point(points[0], points[1]);
	}

	@Override
	protected void createPoints() {
		if (points == null) {
			points = new float[2];
		}
		points[0] = getX();
		points[1] = getY();

		maxX = x;
		maxY = y;
		minX = x;
		minY = y;

		findCenter();
		calculateRadius();
	}

	@Override
	protected void findCenter() {
		if (center == null) {
			center = new float[2];
		}
		center[0] = points[0];
		center[1] = points[1];
	}

	@Override
	protected void calculateRadius() {
		boundingCircleRadius = 0;
	}

	public Point set(float x, float y) {
		this.x = x;
		this.y = y;
		this.pointsDirty = true;
		return this;
	}

	@Override
	public Point setLocation(float x, float y) {
		this.x = x;
		this.y = y;
		this.pointsDirty = true;
		return this;
	}

	public Point setLocation(Point p) {
		this.x = p.getX();
		this.y = p.getY();
		this.pointsDirty = true;
		return this;
	}

	@Override
	public Point translate(float dx, float dy) {
		this.x += dx;
		this.y += dy;
		this.pointsDirty = true;
		return this;
	}

	public Point translate(Point p) {
		this.x += p.x;
		this.y += p.y;
		this.pointsDirty = true;
		return this;
	}

	public Point untranslate(Point p) {
		this.x -= p.x;
		this.y -= p.y;
		this.pointsDirty = true;
		return this;
	}

	public final int distanceTo(Point p) {
		final float tx = (this.x - p.x);
		final float ty = (this.y - p.y);
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(int x, int y) {
		final float tx = (int) (this.x - x);
		final float ty = (int) (this.y - y);
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public Point getLocation(Point dest) {
		dest.setLocation(this.x, this.y);
		return this;
	}

	public Point random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.pointsDirty = true;
		return this;
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

	@Override
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

	public boolean equals(float x, float y) {
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(this.x)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(this.y)) {
			return false;
		}
		if (NumberUtils.floatToIntBits(rotation) != NumberUtils.floatToIntBits(this.rotation)) {
			return false;
		}
		return true;
	}

	public boolean equals(XY pos) {
		if (pos == null) {
			return false;
		}
		return equals(pos.getX(), pos.getY());
	}

	public boolean equals(Point v) {
		if (v == null) {
			return false;
		}
		if (this == v) {
			return true;
		}
		return equals(v.getX(), v.getY());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof Point) {
			return equals((Point) obj);
		}
		return true;
	}

	public Point copy(Point e) {
		if (e == null) {
			return this;
		}
		if (equals(e)) {
			return this;
		}
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
	public Point copy(Shape e) {
		if (e instanceof Point) {
			copy((Point) e);
		} else {
			super.copy(e);
		}
		return this;
	}

	@Override
	public Point cpy() {
		return new Point(this.x, this.y);
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

}
