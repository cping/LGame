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
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Line extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Vector2f intersect(Line other) {
		return intersect(other, false);
	}

	public Vector2f intersect(Line other, boolean limit) {
		Vector2f temp = new Vector2f();
		if (!intersect(other, limit, temp))
			return null;
		return temp;
	}

	public boolean intersect(Line other, boolean limit, Vector2f result) {
		float dx1 = this._currentEnd.getX() - this._currentStart.getX();
		float dx2 = other._currentEnd.getX() - other._currentStart.getX();
		float dy1 = this._currentEnd.getY() - this._currentStart.getY();
		float dy2 = other._currentEnd.getY() - other._currentStart.getY();
		float denom = dy2 * dx1 - dx2 * dy1;
		if (denom == 0f) {
			return false;
		}
		float ua = dx2 * (this._currentStart.getY() - other._currentStart.getY())
				- dy2 * (this._currentStart.getX() - other._currentStart.getX());
		ua /= denom;
		float ub = dx1 * (this._currentStart.getY() - other._currentStart.getY())
				- dy1 * (this._currentStart.getX() - other._currentStart.getX());
		ub /= denom;
		if (limit && (ua < 0f || ua > 1f || ub < 0f || ub > 1f)) {
			return false;
		}
		float u = ua;
		float ix = this._currentStart.getX() + u * (this._currentEnd.getX() - this._currentStart.getX());
		float iy = this._currentStart.getY() + u * (this._currentEnd.getY() - this._currentStart.getY());
		result.set(ix, iy);
		return true;
	}

	public static final Vector2f getIntersects(final Line lineA, final Line lineB) {
		return getIntersects(lineA, lineB, 0.001f);
	}

	public static final Vector2f getIntersects(final Line lineA, final Line lineB, float tolerance) {
		final float x1 = lineA.getX1(), y1 = lineA.getY1();
		final float x2 = lineA.getX2(), y2 = lineA.getY2();
		final float x3 = lineB.getX1(), y3 = lineB.getY1();
		final float x4 = lineB.getX2(), y4 = lineB.getY2();
		if (MathUtils.abs(x1 - x2) < tolerance && MathUtils.abs(x3 - x4) < tolerance
				&& MathUtils.abs(x1 - x3) < tolerance) {
			return null;
		}
		if (MathUtils.abs(y1 - y2) < tolerance && MathUtils.abs(y3 - y4) < tolerance
				&& MathUtils.abs(y1 - y3) < tolerance) {
			return null;
		}
		if (MathUtils.abs(x1 - x2) < tolerance && MathUtils.abs(x3 - x4) < tolerance) {
			return null;
		}
		if (MathUtils.abs(y1 - y2) < tolerance && MathUtils.abs(y3 - y4) < tolerance) {
			return null;
		}
		float x, y;
		if (MathUtils.abs(x1 - x2) < tolerance) {
			final float m2 = (y4 - y3) / (x4 - x3);
			final float c2 = -m2 * x3 + y3;
			x = x1;
			y = c2 + m2 * x1;
		}

		else if (MathUtils.abs(x3 - x4) < tolerance) {
			final float m1 = (y2 - y1) / (x2 - x1);
			final float c1 = -m1 * x1 + y1;
			x = x3;
			y = c1 + m1 * x3;
		} else {
			final float m1 = (y2 - y1) / (x2 - x1);
			final float c1 = -m1 * x1 + y1;
			final float m2 = (y4 - y3) / (x4 - x3);
			final float c2 = -m2 * x3 + y3;

			x = (c1 - c2) / (m2 - m1);
			y = c2 + m2 * x;

			if (!(MathUtils.abs(-m1 * x + y - c1) < tolerance && MathUtils.abs(-m2 * x + y - c2) < tolerance)) {
				return null;
			}
		}
		return new Vector2f(x, y);
	}

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
		String[] result = StringUtils.split(v, LSystem.COMMA);
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

	public final static Line rect(float x, float y, float w, float h) {
		return new Line(x, y, w + x, h + y);
	}

	private final Vector2f _currentStart = Vector2f.ZERO();

	private final Vector2f _currentEnd = Vector2f.ZERO();

	private final Vector2f _vec = Vector2f.ZERO();

	private final Vector2f _loc = Vector2f.ZERO();

	private final Vector2f _closest = Vector2f.ZERO();

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

	public Line(float x1, float y1, float dx, float dy, boolean dummy) {
		this(new Vector2f(x1, y1), dummy ? new Vector2f(x1 + dx, y1 + dy) : new Vector2f(dx, dy));
	}

	public Line(float[] start, float[] end) {
		super();
		set(start, end);
	}

	public Line(Line line) {
		super();
		set(line.getStart(), line.getEnd());
	}

	public Line(Vector2f start, Vector2f end) {
		super();
		set(start, end);
	}

	public Line(float x1, float y1, float x2, float y2) {
		super();
		set(x1, y1, x2, y2);
	}

	@Override
	public Line setLocation(float x, float y) {
		return set(x, y, _currentEnd.x, _currentEnd.y);
	}

	public Line set(float[] start, float[] end) {
		set(start[0], start[1], end[0], end[1]);
		return this;
	}

	public Vector2f getStart() {
		return _currentStart.cpy();
	}

	public Vector2f getEnd() {
		return _currentEnd.cpy();
	}

	public float getTheta() {
		return MathUtils.atan2(_currentEnd.y - _currentStart.y, _currentEnd.x - _currentStart.x);
	}

	public float getRho() {
		return getSubDirection().length();
	}

	public Vector2f getDirectionValue() {
		return Field2D.getDirection(getDirection());
	}

	public int getDirection() {
		return Field2D.getDirection(_currentEnd.x() - _currentStart.x(), _currentEnd.y() - _currentStart.y(),
				Config.EMPTY);
	}

	public Vector2f getSubDirection() {
		return _currentEnd.sub(_currentStart);
	}

	public Vector2f getSubDirectionNormalized() {
		return getSubDirection().normalizeNew();
	}

	@Override
	public float length() {
		return MathUtils.sqrt((getX2() - getX1()) * (getX2() - getX1()) + (getY2() - getY1()) * (getY2() - getY1()));
	}

	public float lengthSquared() {
		return _vec.lengthSquared();
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

	public Line set(XY start, XY end) {
		return set(start.getX(), start.getY(), end.getX(), end.getY());
	}

	public Line set(float sx, float sy, float ex, float ey) {
		if (ex < sx) {
			float temp = sx;
			sx = ex;
			ex = temp;
			temp = sy;
			sy = ey;
			ey = temp;
		}
		super.pointsDirty = true;
		this._currentStart.set(sx, sy);
		this._currentEnd.set(ex, ey);
		float dx = (ex - sx);
		float dy = (ey - sy);
		_vec.set(dx, dy);
		setX(this._currentStart.x);
		setY(this._currentStart.y);
		return this;
	}

	public float getDX() {
		return _currentEnd.getX() - _currentStart.getX();
	}

	public float getDY() {
		return _currentEnd.getY() - _currentStart.getY();
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
		return _currentStart.getX();
	}

	public float getY1() {
		return _currentStart.getY();
	}

	public float getX2() {
		return _currentEnd.getX();
	}

	public float getY2() {
		return _currentEnd.getY();
	}

	public Line setX1(float x) {
		_currentStart.setX(x);
		return this;
	}

	public Line setY1(float y) {
		_currentStart.setY(y);
		return this;
	}

	public Line setX2(float x) {
		_currentEnd.setX(x);
		return this;
	}

	public Line setY2(float y) {
		_currentEnd.setY(y);
		return this;
	}

	public Line setEmpty() {
		_currentStart.setEmpty();
		_currentEnd.setEmpty();
		return this;
	}

	@Override
	public void clear() {
		super.clear();
		setEmpty();
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

	@Override
	public boolean contains(XY xy) {
		if (xy == null) {
			return false;
		}
		return contains(xy.getX(), xy.getY());
	}

	@Override
	public boolean contains(float x, float y) {
		return CollisionHelper.checkPointvsLine(x, y, this.getX1(), this.getY1(), this.getX2(), this.getY2());
	}

	@Override
	public boolean inPoint(XY pos) {
		if (pos == null) {
			return false;
		}
		return inPoint(pos.getX(), pos.getY());
	}

	public boolean inPoint(float x, float y) {
		return CollisionHelper.checkPointvsLine(x, y, getX1(), getY1(), getX2(), getY2());
	}

	public boolean inRect(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return inRect(rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public boolean inRect(float x, float y, float w, float h) {
		return CollisionHelper.checkLinevsAABB(getX1(), getY1(), getX2(), getY2(), x, y, w, h);
	}

	public boolean inLine(Line e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkLinevsLine(getX1(), getY1(), getX2(), getY2(), e.getX1(), e.getY1(), e.getX2(),
				e.getY2());
	}

	public boolean inCircle(Circle e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkLinevsCircle(getX1(), getY1(), getX2(), getY2(), e.getRealX(), e.getRealY(),
				e.getDiameter());
	}

	public boolean inEllipse(Ellipse e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkLinevsEllipse(getX1(), getY1(), getX2(), getY2(), e.getRealX(), e.getRealY(),
				e.getDiameter1(), e.getDiameter2());
	}

	public float distance(Vector2f point) {
		return MathUtils.sqrt(distanceSquared(point));
	}

	public boolean on(Vector2f point) {
		getClosestPoint(point, _closest);
		return point.equals(_closest);
	}

	public float distanceSquared(Vector2f point) {
		getClosestPoint(point, _closest);
		_closest.sub(point);
		float result = _closest.lengthSquared();
		return result;
	}

	public Line getClosestPoint(Vector2f point, Vector2f result) {
		_loc.set(point);
		_loc.sub(_currentStart);

		float projDistance = _vec.dot(_loc);

		projDistance /= _vec.lengthSquared();

		if (projDistance < 0) {
			result.set(_currentStart);
			return this;
		}
		if (projDistance > 1) {
			result.set(_currentEnd);
			return this;
		}

		result.x = _currentStart.getX() + projDistance * _vec.getX();
		result.y = _currentStart.getY() + projDistance * _vec.getY();
		return this;
	}

	@Override
	public boolean intersects(XY pos) {
		return inPoint(pos);
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

		float x1 = _currentStart.getX();
		float y1 = _currentStart.getY();

		float x2 = _currentEnd.getX() + _currentStart.getX();
		float y2 = _currentEnd.getY() + _currentStart.getY();

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

	public Vector2f getIntersects(Line l) {
		return getIntersects(this, l);
	}

	public float getLineAngle(boolean degrees) {
		final Vector2f point1 = _currentStart;
		final Vector2f point2 = _currentEnd;

		final float radians = MathUtils.atan2(point2.y - point1.y, point2.x - point1.x);
		if (!degrees) {
			return radians;
		}

		return MathUtils.toDegrees(radians);
	}

	@Override
	public Line setRotation(float degrees) {
		checkPoints();
		return this.setRotation(degrees, center[0], center[1]);
	}

	@Override
	public Line setRotation(float degrees, float cx, float cy) {
		if (!MathUtils.equal(rotation, degrees)) {
			this.rotation = degrees;
			_currentStart.rotateSelf(cx, cy, degrees);
			_currentEnd.rotateSelf(cx, cy, degrees);
			pointsDirty = true;
		}
		return this;
	}

	@Override
	public void setScale(float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(float sx, float sy) {
		if (scaleX != sx || scaleY != sy) {
			this.scaleX = sx;
			this.scaleY = sy;
			_currentStart.scaleSelf(sx, sy);
			_currentEnd.scaleSelf(sx, sy);
			pointsDirty = true;
		}
	}

	public RectBox bounds() {
		return bounds(null);
	}

	public RectBox bounds(RectBox rect) {
		float min_x = 0f;
		float min_y = 0f;
		float max_x = 0f;
		float max_y = 0f;
		if (_currentStart.x < _currentEnd.x) {
			min_x = _currentStart.x;
			max_x = _currentEnd.x;
		} else {
			min_x = _currentEnd.x;
			max_x = _currentStart.x;
		}
		if (_currentStart.y < _currentEnd.y) {
			min_y = _currentStart.y;
			max_y = _currentEnd.y;
		} else {
			min_y = _currentEnd.y;
			max_y = _currentStart.y;
		}
		if (min_x - max_x == 0) {
			max_x += 1;
		}
		if (min_y + max_y == 0) {
			max_y += 1;
		}
		return RectBox.fromMinMax(min_x, min_y, max_x, max_y, rect);
	}

	public float len() {
		return _currentStart.distance(_currentEnd);
	}

	public float setRadians(float r) {
		float len = len();
		_currentEnd.x = _currentStart.x + MathUtils.cos(r) * len;
		_currentEnd.y = _currentStart.y + MathUtils.sin(r) * len;
		pointsDirty = true;
		return r;
	}

	public float getRadians() {
		return MathUtils.atan2(_currentEnd.y - _currentStart.y, _currentEnd.x - _currentStart.x);
	}

	public float setDegrees(float d) {
		return setRadians(MathUtils.toRadians(d));
	}

	public Line clip(Vector2f side, float length, boolean normalize) {
		Vector2f dir = side;
		if (normalize) {
			dir = dir.nor();
		}
		float near = dir.dot(this._currentStart) - length;
		float far = dir.dot(this._currentEnd) - length;
		TArray<Vector2f> results = new TArray<Vector2f>();
		if (near <= 0) {
			results.add(this._currentStart);
		}
		if (far <= 0) {
			results.add(this._currentEnd);
		}
		if (near * far < 0) {
			float clipTime = near / (near - far);
			results.add(this._currentStart
					.add(this._currentEnd.cpy().subtractSelf(this._currentStart).scaleSelf(clipTime)));
		}
		if (results.size != 2) {
			return null;
		}
		if (results.size > 1) {
			return new Line(results.get(0), results.get(1));
		}
		return null;
	}

	public float side(XY v) {
		if (v == null) {
			return 0f;
		}
		return side(v.getX(), v.getY());
	}

	public float side(float x, float y) {
		return (_currentEnd.x - _currentStart.x) * (y - _currentStart.y)
				- (_currentEnd.y - _currentStart.y) * (x - _currentStart.x);
	}

	public boolean below(Vector2f pos) {
		float above2 = (this._currentEnd.x - this._currentStart.x) * (pos.y - this._currentStart.y)
				- (this._currentEnd.y - this._currentStart.y) * (pos.x - this._currentStart.x);
		return above2 >= 0;
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
		float dx = _currentEnd.x - _currentStart.x;
		float dy = _currentEnd.y - _currentStart.y;
		float k = ((x - _currentStart.x) * dx + (y - _currentStart.y) * dy) / (dx * dx + dy * dy);
		return new Vector2f(dx * k + _currentStart.x, dy * k + _currentStart.y);
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

	public Vector2f getSlope() {
		Vector2f begin = this._currentStart;
		Vector2f end = this._currentEnd;
		float distance = begin.distance(end);
		return end.sub(begin).scale(1f / distance);
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

	public Intersection intersection(Line other) {

		final Vector2f pos = Vector2f.ZERO();

		final Vector2f start = other.getStart();
		final Vector2f end = other.getEnd();

		final float bxax = (_currentEnd.x - _currentStart.x);
		final float byay = (_currentEnd.y - _currentStart.y);
		final float dycy = (end.y - start.y);
		final float dxcx = (end.x - start.x);

		final float denom = dycy * bxax - dxcx * byay;

		if (denom == 0) {
			return new Intersection(this, other, null, false);
		}
		final float axcx = (_currentStart.x - start.x);
		final float aycy = (_currentStart.y - start.y);

		float ua = dxcx * aycy - dycy * axcx;
		ua /= denom;
		float ub = bxax * aycy - byay * axcx;
		ub /= denom;

		pos.x = _currentStart.x + ua * bxax;
		pos.y = _currentStart.y + ua * byay;

		if (ua >= 0 && ua < 1 && ub >= 0 && ub < 1) {
			return new Intersection(this, other, pos, true);
		}
		return new Intersection(this, other, null, false);
	}

	public Line mutate() {
		return mutate(16);
	}

	public Line mutate(int v) {
		int r = MathUtils.random(1);
		switch (r) {
		case 0:
			_currentStart.x = MathUtils.clamp(_currentStart.x + MathUtils.random(-v, v), 0, _currentStart.x);
			_currentStart.y = MathUtils.clamp(_currentStart.y + MathUtils.random(-v, v), 0, _currentStart.y);
		case 1:
			_currentEnd.x = MathUtils.clamp(_currentEnd.x + MathUtils.random(-v, v), 0, _currentEnd.x);
			_currentEnd.y = MathUtils.clamp(_currentEnd.y + MathUtils.random(-v, v), 0, _currentEnd.y);
		}
		checkPoints();
		return this;
	}

	public Line flip() {
		return new Line(this._currentEnd, this._currentStart);
	}

	public boolean equals(Line e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (_currentStart.equals(e._currentStart) && _currentEnd.equals(e._currentEnd)
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
		if (obj instanceof Line) {
			return equals((Line) obj);
		}
		return super.equals(obj);
	}

	public Line copy(Line e) {
		if (e == null) {
			return this;
		}
		if (equals(e)) {
			return this;
		}
		this.set(_currentStart, _currentEnd);
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
	public Line copy(Shape e) {
		if (e instanceof Line) {
			copy((Line) e);
		} else {
			super.copy(e);
		}
		return this;
	}

	@Override
	public Line cpy() {
		return new Line(_currentStart, _currentEnd);
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
		final int prime = 39;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, getX1());
		hashCode = prime * LSystem.unite(hashCode, getY1());
		hashCode = prime * LSystem.unite(hashCode, getX2());
		hashCode = prime * LSystem.unite(hashCode, getY2());
		return hashCode;
	}

	@Override
	public final String toString() {
		return "(" + getX1() + "," + getY1() + "," + getX2() + "," + getY2() + ")";
	}
}
