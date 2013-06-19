package loon.core.geom;

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
public class Line extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Vector2f start;

	private Vector2f end;

	private Vector2f vec;

	private Vector2f loc = new Vector2f(0, 0);

	private Vector2f closest = new Vector2f(0, 0);

	public Line(float x, float y, boolean inner, boolean outer) {
		this(0, 0, x, y);
	}

	public Line(float x, float y) {
		this(x, y, true, true);
	}

	public Line(Point p1, Point p2) {
		this(p1.x, p1.y, p2.x, p2.y);
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
		return start;
	}

	public Vector2f getEnd() {
		return end;
	}

	@Override
	public float length() {
		return vec.len();
	}

	public float lengthSquared() {
		return vec.lengthSquared();
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

	public float distance(Vector2f point) {
		return (float) Math.sqrt(distanceSquared(point));
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

	public void getClosestPoint(Vector2f point, Vector2f result) {
		loc.set(point);
		loc.sub(start);

		float projDistance = vec.dot(loc);

		projDistance /= vec.lengthSquared();

		if (projDistance < 0) {
			result.set(start);
			return;
		}
		if (projDistance > 1) {
			result.set(end);
			return;
		}

		result.x = start.getX() + projDistance * vec.getX();
		result.y = start.getY() + projDistance * vec.getY();
	}

	public Vector2f intersect(Line other) {
		return intersect(other, false);
	}

	public Vector2f intersect(Line other, boolean limit) {
		Vector2f temp = new Vector2f();

		if (!intersect(other, limit, temp)) {
			return null;
		}

		return temp;
	}

	public boolean intersect(Line other, boolean limit, Vector2f result) {
		float dx1 = end.getX() - start.getX();
		float dx2 = other.end.getX() - other.start.getX();
		float dy1 = end.getY() - start.getY();
		float dy2 = other.end.getY() - other.start.getY();
		float denom = (dy2 * dx1) - (dx2 * dy1);

		if (denom == 0) {
			return false;
		}

		float ua = (dx2 * (start.getY() - other.start.getY()))
				- (dy2 * (start.getX() - other.start.getX()));
		ua /= denom;
		float ub = (dx1 * (start.getY() - other.start.getY()))
				- (dy1 * (start.getX() - other.start.getX()));
		ub /= denom;

		if ((limit) && ((ua < 0) || (ua > 1) || (ub < 0) || (ub > 1))) {
			return false;
		}

		float u = ua;

		float ix = start.getX() + (u * (end.getX() - start.getX()));
		float iy = start.getY() + (u * (end.getY() - start.getY()));

		result.set(ix, iy);
		return true;
	}

	@Override
	protected void createPoints() {
		points = new float[4];
		points[0] = getX1();
		points[1] = getY1();
		points[2] = getX2();
		points[3] = getY2();
	}

	public float ptSegDistSq(Point pt) {
		return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(),
				pt.getY());
	}

	public float ptSegDistSq(float px, float py) {
		return ptSegDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
	}

	public static float ptSegDist(float x1, float y1, float x2, float y2,
			float px, float py) {
		return (float) Math.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
	}

	public static float ptSegDistSq(float x1, float y1, float x2, float y2,
			float px, float py) {
		x2 -= x1;
		y2 -= y1;
		px -= x1;
		py -= y1;
		float dotprod = px * x2 + py * y2;
		float projlenSq;
		if (dotprod <= 0.0) {
			projlenSq = 0.0f;
		} else {
			px = x2 - px;
			py = y2 - py;
			dotprod = px * x2 + py * y2;
			if (dotprod <= 0.0) {
				projlenSq = 0.0f;
			} else {
				projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			}
		}
		float lenSq = px * px + py * py - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	public static float ptLineDist(float x1, float y1, float x2, float y2,
			float px, float py) {
		return (float) Math.sqrt(ptLineDistSq(x1, y1, x2, y2, px, py));
	}

	public float ptLineDist(Point pt) {
		return ptLineDist(getX1(), getY1(), getX2(), getY2(), pt.getX(),
				pt.getY());
	}

	public float ptLineDistSq(float px, float py) {
		return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), px, py);
	}

	public float ptLineDistSq(Point pt) {
		return ptLineDistSq(getX1(), getY1(), getX2(), getY2(), pt.getX(),
				pt.getY());
	}

	public static float ptLineDistSq(float x1, float y1, float x2, float y2,
			float px, float py) {
		x2 -= x1;
		y2 -= y1;
		px -= x1;
		py -= y1;
		float dotprod = px * x2 + py * y2;
		float projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
		float lenSq = px * px + py * py - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	@Override
	public Shape transform(Matrix transform) {
		float[] temp = new float[4];
		createPoints();
		transform.transform(points, 0, temp, 0, 2);
		return new Line(temp[0], temp[1], temp[2], temp[3]);
	}

	@Override
	public boolean closed() {
		return false;
	}

}
