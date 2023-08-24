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

import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Line extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static SetXY getRandom(Line line, SetXY out) {
		if (out == null) {
			out = new PointF();
		}

		float r = MathUtils.random();

		out.setX(line.getX1() + r * (line.getX2() - line.getX1()));
		out.setY(line.getY1() + r * (line.getY2() - line.getY1()));

		return out;
	}

	public static Line at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Line();
		}
		String[] result = StringUtils.split(v, ',');
		int len = result.length;
		if (len > 3) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				float x1 = Float.parseFloat(result[2].trim());
				float y1 = Float.parseFloat(result[3].trim());
				return new Line(x, y, x1, y1);
			} catch (Exception ex) {
			}
		}
		return new Line();
	}

	public final static Line at(float x1, float y1, float x2, float y2) {
		return new Line(x1, y1, x2, y2);
	}

	private Vector2f start;

	private Vector2f end;

	private Vector2f vec;

	private final Vector2f loc = new Vector2f(0, 0);

	private final Vector2f closest = new Vector2f(0, 0);

	public Line() {
		this(0f, 0f);
	}

	public Line(float x, float y, boolean inner, boolean outer) {
		this(0, 0, x, y);
	}

	public Line(float x, float y) {
		this(x, y, true, true);
	}

	public Line(XY p1, XY p2) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public Line(float x1, float y1, float x2, float y2) {
		this(new Vector2f(x1, y1), new Vector2f(x2, y2));
	}

	public Line(float x1, float y1, float dx, float dy, boolean dummy) {
		this(new Vector2f(x1, y1), new Vector2f(x1 + dx, y1 + dy));
	}

	public Line(float[] start, float[] end) {
		super();
		set(start, end);
	}

	public Line(Vector2f start, Vector2f end) {
		super();
		set(start, end);
	}

	public void set(float[] start, float[] end) {
		set(start[0], start[1], end[0], end[1]);
	}

	public Vector2f getStart() {
		return start.cpy();
	}

	public Vector2f getEnd() {
		return end.cpy();
	}

	public Vector2f getDirectionValue() {
		return Field2D.getDirection(getDirection());
	}

	public int getDirection() {
		return Field2D.getDirection(end.x() - start.x(), end.y() - start.y(), Config.EMPTY);
	}

	@Override
	public float length() {
		return MathUtils.sqrt((getX2() - getX1()) * (getX2() - getX1()) + (getY2() - getY1()) * (getY2() - getY1()));
	}

	public float lengthSquared() {
		return vec.lengthSquared();
	}

	public TArray<PointF> getPoints(float quantity, float stepRate) {
		return getPoints(new TArray<PointF>(), quantity, stepRate);
	}

	public TArray<PointF> getPoints(TArray<PointF> points, float quantity, float stepRate) {
		if (stepRate > 0) {
			quantity = length() / stepRate;
		}
		float x1 = getX1();
		float y1 = getY1();

		float x2 = getX2();
		float y2 = getY2();

		for (int i = 0; i < quantity; i++) {
			float position = i / quantity;
			float x = x1 + (x2 - x1) * position;
			float y = y1 + (y2 - y1) * position;
			points.add(new PointF(x, y));
		}
		return points;
	}

	public void set(Vector2f start, Vector2f end) {
		super.pointsDirty = true;
		if (this.start == null) {
			this.start = new Vector2f();
		}
		this.start.set(start);

		if (this.end == null) {
			this.end = new Vector2f();
		}
		this.end.set(end);

		vec = new Vector2f(end);
		vec.sub(start);

		this.setLocation(start.x, start.y);
	}

	public void set(float sx, float sy, float ex, float ey) {
		super.pointsDirty = true;
		start.set(sx, sy);
		end.set(ex, ey);
		float dx = (ex - sx);
		float dy = (ey - sy);
		vec.set(dx, dy);
	}

	public float getDX() {
		return end.getX() - start.getX();
	}

	public float getDY() {
		return end.getY() - start.getY();
	}

	@Override
	public float getX() {
		return getX1();
	}

	@Override
	public float getY() {
		return getY1();
	}

	public float getX1() {
		return start.getX();
	}

	public float getY1() {
		return start.getY();
	}

	public float getX2() {
		return end.getX();
	}

	public float getY2() {
		return end.getY();
	}

	public Line setX1(float x) {
		start.setX(x);
		return this;
	}

	public Line setY1(float y) {
		start.setY(y);
		return this;
	}

	public Line setX2(float x) {
		end.setX(x);
		return this;
	}

	public Line setY2(float y) {
		end.setY(y);
		return this;
	}

	public TArray<Point> getBresenhamPoints(int stepRate) {
		return getBresenhamPoints(stepRate, null);
	}

	public TArray<Point> getBresenhamPoints(int stepRate, TArray<Point> points) {

		stepRate = MathUtils.max(1, stepRate);
		if (points == null) {
			points = new TArray<Point>();
		}

		int x1 = MathUtils.round(getX1());
		int y1 = MathUtils.round(getY1());
		int x2 = MathUtils.round(getX2());
		int y2 = MathUtils.round(getY2());

		int dx = MathUtils.abs(x2 - x1);
		int dy = MathUtils.abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = (dx - dy);

		points.add(Point.at(x1, y1));

		float count = 1;

		for (; !((x1 == x2) && (y1 == y2));) {
			int e2 = err * 2;
			if (e2 > -dy) {
				err -= dy;
				x1 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y1 += sy;
			}
			if (count % stepRate == 0) {
				points.add(Point.at(x1, y1));
			}
			count++;
		}
		return points;
	}

	public float distance(Vector2f point) {
		return MathUtils.sqrt(distanceSquared(point));
	}

	public boolean on(Vector2f point) {
		getClosestPoint(point, closest);
		return point.equals(closest);
	}

	public float distanceSquared(Vector2f point) {
		getClosestPoint(point, closest);
		closest.sub(point);
		float result = closest.lengthSquared();
		return result;
	}

	public Line getClosestPoint(Vector2f point, Vector2f result) {
		loc.set(point);
		loc.sub(start);

		float projDistance = vec.dot(loc);

		projDistance /= vec.lengthSquared();

		if (projDistance < 0) {
			result.set(start);
			return this;
		}
		if (projDistance > 1) {
			result.set(end);
			return this;
		}

		result.x = start.getX() + projDistance * vec.getX();
		result.y = start.getY() + projDistance * vec.getY();
		return this;
	}

	public boolean intersects(Line other) {
		return intersects(other, null);
	}

	public boolean intersects(Line other, Vector2f result) {
		if (other == null) {
			return false;
		}

		float x1 = getX1();
		float y1 = getY1();
		float x2 = getX2();
		float y2 = getY2();

		float x3 = other.getX1();
		float y3 = other.getY1();
		float x4 = other.getX2();
		float y4 = other.getY2();

		float numA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
		float numB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
		float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

		if (denom == 0) {
			return false;
		}

		float uA = numA / denom;
		float uB = numB / denom;

		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
			if (result != null) {
				result.x = x1 + (uA * (x2 - x1));
				result.y = y1 + (uA * (y2 - y1));
			}
			return true;
		}

		return false;
	}

	public boolean intersects(RectBox rect) {
		if (rect == null) {
			return false;
		}

		float x1 = start.getX();
		float y1 = start.getY();

		float x2 = end.getX() + start.getX();
		float y2 = end.getY() + start.getY();

		float bx1 = rect.x;
		float by1 = rect.y;
		float bx2 = rect.getRight();
		float by2 = rect.getBottom();

		float t = 0;
		if ((x1 >= bx1 && x1 <= bx2 && y1 >= by1 && y1 <= by2) || (x2 >= bx1 && x2 <= bx2 && y2 >= by1 && y2 <= by2)) {
			return true;
		}

		if (x1 < bx1 && x2 >= bx1) {
			t = y1 + (y2 - y1) * (bx1 - x1) / (x2 - x1);
			if (t > by1 && t <= by2) {
				return rect.intersects(this);
			}
		} else if (x1 > bx2 && x2 <= bx2) {
			t = y1 + (y2 - y1) * (bx2 - x1) / (x2 - x1);
			if (t >= by1 && t <= by2) {
				return rect.intersects(this);
			}
		}

		if (y1 < by1 && y2 >= by1) {
			t = x1 + (x2 - x1) * (by1 - y1) / (y2 - y1);
			if (t >= bx1 && t <= bx2) {
				return rect.intersects(this);
			}

		} else if (y1 > by2 && y2 <= by2) {
			t = x1 + (x2 - x1) * (by2 - y1) / (y2 - y1);
			if (t >= bx1 && t <= bx2) {
				return rect.intersects(this);
			}
		}

		return false;
	}

	public Line increaseDistance(float offset) {
		final float dx = this.getX2() - this.getX1();
		final float dy = this.getY2() - this.getY1();
		final float currentDistance = MathUtils.sqrt(dx * dx + dy * dy);

		final float unitVectorX = dx / currentDistance;
		final float unitVectorY = dy / currentDistance;

		final float newDistance = currentDistance + offset;

		this.setX1(this.getX1() - unitVectorX * (offset / 2f));
		this.setY1(this.getY1() - unitVectorY * (offset / 2f));
		this.setX2(this.getX1() + unitVectorX * newDistance);
		this.setY2(this.getY1() + unitVectorY * newDistance);
		return this;
	}

	public float getLineAngle(boolean degrees) {
		final Vector2f point1 = start;
		final Vector2f point2 = end;

		final float radians = MathUtils.atan2(point2.y - point1.y, point2.x - point1.x);
		if (!degrees) {
			return radians;
		}

		return MathUtils.toDegrees(radians);
	}

	public float side(XY v) {
		if (v == null) {
			return 0f;
		}
		return side(v.getX(), v.getY());
	}

	public float side(float x, float y) {
		return (end.x - start.x) * (y - start.y) - (end.y - start.y) * (x - start.x);
	}

	public Vector2f getMidPoint() {
		Vector2f out = new Vector2f();
		out.x = (getX1() + getX2()) / 2f;
		out.y = (getY1() + getY2()) / 2f;
		return out;
	}

	public Vector2f project(Vector2f v) {
		if (v == null) {
			return Vector2f.ZERO();
		}
		return project(v.x, v.y);
	}

	public Vector2f project(float x, float y) {
		float dx = end.x - start.x;
		float dy = end.y - start.y;
		float k = ((x - start.x) * dx + (y - start.y) * dy) / (dx * dx + dy * dy);
		return new Vector2f(dx * k + start.x, dy * k + start.y);
	}

	@Override
	protected void createPoints() {
		points = new float[4];
		points[0] = getX1();
		points[1] = getY1();
		points[2] = getX2();
		points[3] = getY2();
	}

	public float ptSegDistSq(XY pt) {
		return ShapeUtils.ptSegDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt.getY());
	}

	public float ptSegDistSq(float px, float py) {
		return ShapeUtils.ptSegDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
	}

	public float ptLineDist(XY pt) {
		return ShapeUtils.ptLineDist(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt.getY());
	}

	public float ptLineDistSq(float px, float py) {
		return ShapeUtils.ptLineDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
	}

	public float ptLineDistSq(XY pt) {
		return ShapeUtils.ptLineDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(), pt.getY());
	}

	public float slope() {
		return (this.getY2() - this.getY1()) / (this.getX2() - this.getX1());
	}

	public float perpSlope() {
		return -((this.getX2() - this.getX1()) / (this.getY2() - this.getY1()));
	}

	public float angle() {
		return MathUtils.atan2(this.getY2() - this.getY1(), this.getX2() - this.getX1());
	}

	public boolean isPointOnLine(float x, float y) {
		return (x - this.getX1()) * (this.getY2() - this.getY1()) == (this.getX2() - this.getX1()) * (y - this.getY1());
	}

	public boolean isPointOnLineSegment(float x, float y) {
		float xMin = MathUtils.min(this.getX1(), this.getX2());
		float xMax = MathUtils.max(this.getX1(), this.getX2());
		float yMin = MathUtils.min(this.getY1(), this.getY2());
		float yMax = MathUtils.max(this.getY1(), this.getY2());
		return this.isPointOnLine(x, y) && (x >= xMin && x <= xMax) && (y >= yMin && y <= yMax);
	}

	public boolean isPointOnRay(float x, float y) {
		if ((x - this.getX1()) * (this.getY2() - this.getY1()) == (this.getX2() - this.getX1()) * (y - this.getY1())) {
			if (MathUtils.atan2(y - this.getY1(), x - this.getX1()) == MathUtils.atan2(this.getY2() - this.getY1(),
					this.getX2() - this.getX1())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Shape transform(Matrix3 transform) {
		float[] temp = new float[4];
		createPoints();
		transform.transform(points, 0, temp, 0, 2);
		return new Line(temp[0], temp[1], temp[2], temp[3]);
	}

	@Override
	public boolean closed() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(start.hashCode());
		result = prime * result + NumberUtils.floatToIntBits(end.hashCode());
		return result;
	}

	@Override
	public final String toString() {
		return "(" + getX1() + "," + getY1() + "," + getX2() + "," + getY2() + ")";
	}
}
