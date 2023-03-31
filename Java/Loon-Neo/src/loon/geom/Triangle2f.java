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

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;

public class Triangle2f extends Shape implements Triangle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static SetXY getRandom(Triangle2f triangle, SetXY out) {
		if (out == null) {
			out = new PointF();
		}

		float ux = triangle.getX2() - triangle.getX1();
		float uy = triangle.getY2() - triangle.getY1();

		float vx = triangle.getX3() - triangle.getX1();
		float vy = triangle.getY3() - triangle.getY1();

		float r = MathUtils.random();
		float s = MathUtils.random();

		if (r + s >= 1) {
			r = 1 - r;
			s = 1 - s;
		}

		out.setX(triangle.getX1() + ((ux * r) + (vx * s)));
		out.setY(triangle.getY1() + ((uy * r) + (vy * s)));

		return out;
	}

	public final static Triangle2f at(float x, float y, float w, float h) {
		return new Triangle2f(x + w / 2, y + h / 2, w, h);
	}

	public final static Triangle2f left(float x, float y, float w, float h) {
		final float x1 = x + w;
		final float y1 = y - h;
		final float x2 = x + w;
		final float y2 = y;
		final float x3 = x;
		final float y3 = y;
		return new Triangle2f(x1, y1, x2, y2, x3, y3);
	}

	public final static Triangle2f right(float x, float y, float w, float h) {
		final float x1 = x;
		final float y1 = y;
		final float x2 = x;
		final float y2 = y - h;
		final float x3 = x + w;
		final float y3 = y;
		return new Triangle2f(x1, y1, x2, y2, x3, y3);
	}

	public float[] xpoints;

	public float[] ypoints;

	public Triangle2f() {
		xpoints = new float[3];
		ypoints = new float[3];
	}

	public Triangle2f(float w, float h) {
		this();
		set(w, h);
	}

	public Triangle2f(float x, float y, float w, float h) {
		this();
		set(x, y, w, h);
	}

	public Triangle2f(Vector2f t1, Vector2f t2, Vector2f t3) {
		this(t1.x, t1.y, t2.x, t2.y, t3.x, t3.y);
	}

	public Triangle2f(float x1, float y1, float x2, float y2, float x3, float y3) {
		this();
		float dx1 = x2 - x1;
		float dx2 = x3 - x1;
		float dy1 = y2 - y1;
		float dy2 = y3 - y1;
		float cross = dx1 * dy2 - dx2 * dy1;
		boolean ccw = (cross > 0);
		if (ccw) {
			xpoints[0] = x1;
			xpoints[1] = x2;
			xpoints[2] = x3;
			ypoints[0] = y1;
			ypoints[1] = y2;
			ypoints[2] = y3;
		} else {
			xpoints[0] = x1;
			xpoints[1] = x3;
			xpoints[2] = x2;
			ypoints[0] = y1;
			ypoints[1] = y3;
			ypoints[2] = y2;
		}
	}

	public float getX1() {
		return xpoints[0];
	}

	public float getX2() {
		return xpoints[1];
	}

	public float getX3() {
		return xpoints[2];
	}

	public float getY1() {
		return ypoints[0];
	}

	public float getY2() {
		return ypoints[1];
	}

	public float getY3() {
		return ypoints[2];
	}

	protected void convertPoints(float[] points) {
		int size = points.length / 2;
		for (int i = 0, j = 0; i < size; i += 2, j++) {
			xpoints[j] = points[i];
			ypoints[j] = points[i + 1];
		}
	}

	public float[] getVertexs() {
		int vertice_size = xpoints.length * 2;
		float[] verts = new float[vertice_size];
		for (int i = 0, j = 0; i < vertice_size; i += 2, j++) {
			verts[i] = xpoints[j];
			verts[i + 1] = ypoints[j];
		}
		return verts;
	}

	public void set(float w, float h) {
		set(w / 2 - 1, h / 2 - 1, w - 1, h - 1);
	}

	public void set(float x, float y, float w, float h) {
		float halfWidth = w / 2;
		float halfHeight = h / 2;
		float top = -halfWidth;
		float bottom = halfHeight;
		float left = -halfHeight;
		float center = 0;
		float right = halfWidth;

		xpoints[0] = x + center;
		xpoints[1] = x + right;
		xpoints[2] = x + left;
		ypoints[0] = y + top;
		ypoints[1] = y + bottom;
		ypoints[2] = y + bottom;

		updateTriangle(6);
	}

	public void set(Triangle2f t) {
		xpoints[0] = t.xpoints[0];
		xpoints[1] = t.xpoints[1];
		xpoints[2] = t.xpoints[2];
		ypoints[0] = t.ypoints[0];
		ypoints[1] = t.ypoints[1];
		ypoints[2] = t.ypoints[2];
		updateTriangle(6);
	}

	protected void updateTriangle(int length) {

		if (points == null || points.length != length) {
			this.points = new float[length];
		}

		for (int i = 0, j = 0; i < length; i += 2, j++) {
			points[i] = xpoints[j];
			points[i + 1] = ypoints[j];
		}

		for (int i = 0; i < length; i++) {
			this.points[i] = points[i];
			if (i % 2 == 0) {
				if (points[i] > maxX) {
					maxX = points[i];
				}
				if (points[i] < minX) {
					minX = points[i];
				}
				if (points[i] < x) {
					x = points[i];
				}
			} else {
				if (points[i] > maxY) {
					maxY = points[i];
				}
				if (points[i] < minY) {
					minY = points[i];
				}
				if (points[i] < y) {
					y = points[i];
				}
			}
		}

		findCenter();
		calculateRadius();
		pointsDirty = true;
	}

	public boolean isInside(XY p) {
		return isInside(new Vector2f(getX1(), getY1()), new Vector2f(getX2(), getY2()), new Vector2f(getX3(), getY3()),
				p);
	}

	public static boolean isInside(XY x, XY y, XY z, XY p) {
		Vector2f v1 = new Vector2f(y.getX() - x.getX(), y.getY() - x.getY());
		Vector2f v2 = new Vector2f(z.getX() - x.getX(), z.getY() - x.getY());

		float det = v1.x * v2.y - v2.x * v1.y;
		Vector2f tmp = new Vector2f(p.getX() - x.getX(), p.getY() - x.getY());
		float lambda = (tmp.x * v2.y - v2.x * tmp.y) / det;
		float mue = (v1.x * tmp.y - tmp.x * v1.y) / det;

		return (lambda > 0 && mue > 0 && (lambda + mue) < 1);
	}

	public boolean isInside(float nx, float ny) {
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u > 0) && (v > 0) && (u + v < 1));
	}

	public boolean containsPoint(Vector2f v) {
		if (v == null) {
			return false;
		}
		return containsPoint(v.x, v.y);
	}

	public boolean containsPoint(float nx, float ny) {
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u >= 0) && (v >= 0) && (u + v <= 1));
	}

	public PointF getPointCenter() {
		return new PointF((xpoints[0] + xpoints[1] + xpoints[2]) / 3, (ypoints[0] + ypoints[1] + ypoints[2]) / 3);
	}

	@Override
	protected void createPoints() {
	}

	@Override
	public boolean triangulate() {
		return true;
	}

	@Override
	public int getTriangleCount() {
		return 1;
	}

	@Override
	public float[] getTrianglePoint(int t, int i) {
		return null;
	}

	@Override
	public void addPolyPoint(float x, float y) {
	}

	@Override
	public void startHole() {
	}

	@Override
	public Shape transform(Matrix3 transform) {
		checkPoints();
		Triangle2f resultTriangle = new Triangle2f();
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultTriangle.points = result;
		resultTriangle.findCenter();
		resultTriangle.convertPoints(points);
		return resultTriangle;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Triangle");
		builder.kv("xpoints", "[" + StringUtils.join(',', xpoints) + "]").comma()
				.kv("ypoints", "[" + StringUtils.join(',', ypoints) + "]").comma()
				.kv("center", "[" + StringUtils.join(',', center) + "]").comma().kv("rotation", rotation).comma()
				.kv("minX", minX).comma().kv("minY", minY).comma().kv("maxX", maxX).comma().kv("maxY", maxY);
		return builder.toString();
	}

}
