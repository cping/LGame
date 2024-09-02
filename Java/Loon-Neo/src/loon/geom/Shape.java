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

import java.io.Serializable;

import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.action.sprite.ShapeEntity;
import loon.canvas.LColor;
import loon.utils.CollectionUtils;
import loon.utils.IArray;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public abstract class Shape implements Serializable, IArray, XY, SetXY {

	public class HitResult {

		public Line line;

		public int p1;

		public int p2;

		public Vector2f pt;
	}

	public final static int MAX_POINTS = 10000;

	public final static float EDGE_SCALE = 1f;

	public final static float EPSILON = 1.0E-4f;

	/**
	 * SAT凸多边形碰撞检测(Separating Axis Theorem)
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static Vector2f collideSAT(final Shape s1, final Shape s2) {
		if (s1 == null || s2 == null) {
			return null;
		}
		s1.checkPoints();
		s2.checkPoints();
		final float max = Float.MAX_VALUE;
		float overlap = max;
		Vector2f displacement = Vector2f.ZERO();
		final TArray<Shape> list = new TArray<Shape>(s1, s2);
		final TArray<Vector2f> s1vertices = s1.getVertices();
		final TArray<Vector2f> s2vertices = s2.getVertices();
		for (int n = 0; n < list.size; n++) {
			final TArray<Vector2f> vertices = list.get(n).getVertices();
			for (int i = 0; i < vertices.size; i++) {
				Vector2f a = vertices.get(i);
				Vector2f b = vertices.get((i + 1) % vertices.size);
				Vector2f axisProj = b.sub(a).nor().unit();
				float min1 = max;
				float max1 = -max;
				for (int j = 0; j < s1vertices.size; j++) {
					final float q = s1vertices.get(j).dot(axisProj);
					min1 = MathUtils.min(min1, q);
					max1 = MathUtils.max(max1, q);
				}
				float min2 = max;
				float max2 = -max;
				for (int j = 0; j < s2vertices.size; j++) {
					final float q = s2vertices.get(j).dot(axisProj);
					min2 = MathUtils.min(min2, q);
					max2 = MathUtils.max(max2, q);
				}
				final float o = MathUtils.min(max1, max2) - MathUtils.max(min1, min2);
				if (o < 0) {
					return null;
				}
				if (o < MathUtils.abs(overlap)) {
					float o1 = max2 - min1;
					float o2 = min2 - max1;
					overlap = MathUtils.abs(o1) < MathUtils.abs(o2) ? o1 : o2;
					displacement = axisProj.scale(overlap);
				}
			}
		}
		return displacement;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TArray<Vector2f> _vertices;

	public float x;

	public float y;

	protected float rotation;

	protected float[] points;

	protected float[] center;

	protected float scaleX, scaleY;

	protected float maxX, maxY;

	protected float minX, minY;

	protected float boundingCircleRadius;

	protected boolean pointsDirty;

	protected transient Triangle triangle;

	protected boolean trianglesDirty;

	protected AABB aabb;

	protected RectBox rect;

	protected ShapeEntity entity;

	public Shape() {
		pointsDirty = true;
		scaleX = scaleY = 1f;
	}

	public Shape setLocation(XY pos) {
		if (pos == null) {
			return this;
		}
		return setLocation(pos.getX(), pos.getY());
	}

	public Shape setLocation(float x, float y) {
		if (this.x != x || this.y != y) {
			this.pointsDirty = true;
		}
		setX(x);
		setY(y);
		return this;
	}

	public abstract Shape transform(Matrix3 transform);

	protected abstract void createPoints();

	public Shape translate(float deltaX, float deltaY) {
		this.pointsDirty = true;
		setX(x + deltaX);
		setY(y + deltaY);
		return this;
	}

	public int vertexCount() {
		checkPoints();
		return points.length / 2;
	}

	public float getDoubleRadius() {
		return boundingCircleRadius * boundingCircleRadius;
	}

	public Vector2f getPosition() {
		return new Vector2f(getX(), getY());
	}

	public Vector2f getCenterPos() {
		return new Vector2f(getCenterX(), getCenterY());
	}

	public boolean inPoint(XY pos) {
		return inPoint(pos.getX(), pos.getY(), 1f);
	}

	public boolean inPoint(XY pos, float size) {
		return inPoint(pos.getX(), pos.getY(), size);
	}

	public boolean inPoint(float px, float py, float size) {
		return CollisionHelper.checkPointvsPolygon(px, py, this.getPoints(), size);
	}

	public boolean inCircle(Circle c) {
		if (c == null) {
			return false;
		}
		return CollisionHelper.checkCirclevsPolygon(c.getRealX(), c.getRealY(), c.getDiameter(), this.getVertices());
	}

	public boolean inCircle(float cx, float cy, float diameter) {
		return CollisionHelper.checkCirclevsPolygon(cx, cy, diameter, this.getVertices());
	}

	public boolean inRect(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsPolygon(rect, this.getVertices());
	}

	public boolean inRect(float x, float y, float w, float h) {
		return CollisionHelper.checkAABBvsPolygon(x, y, w, h, this.getVertices());
	}

	public boolean inShape(float[] points) {
		if (points == null) {
			return false;
		}
		return CollisionHelper.checkPolygonvsPolygon(this.getPoints(), points);
	}

	public boolean inShape(Shape shape) {
		if (shape == null) {
			return false;
		}
		return CollisionHelper.checkPolygonvsPolygon(this.getPoints(), shape.getPoints());
	}

	@Override
	public float getX() {
		checkPoints();
		return x;
	}

	@Override
	public void setX(float x) {
		if (x != this.x || x == 0) {
			this.pointsDirty = true;
			float dx = x - this.x;
			this.x = x;
			if ((points == null) || (center == null)) {
				checkPoints();
			}
			for (int i = 0; i < points.length / 2; i++) {
				points[i * 2] += dx;
			}
			center[0] += dx;
			maxX += dx;
			minX += dx;
			trianglesDirty = true;
		}
	}

	@Override
	public void setY(float y) {
		if (y != this.y || y == 0) {
			this.pointsDirty = true;
			float dy = y - this.y;
			this.y = y;
			if ((points == null) || (center == null)) {
				checkPoints();
			}
			for (int i = 0; i < points.length / 2; i++) {
				points[(i * 2) + 1] += dy;
			}
			center[1] += dy;
			maxY += dy;
			minY += dy;
			trianglesDirty = true;
		}
	}

	@Override
	public float getY() {
		checkPoints();
		return y;
	}

	public float length() {
		checkPoints();
		return MathUtils.sqrt(x * x + y * y);
	}

	public float getCenterX() {
		checkPoints();
		return center[0];
	}

	public Shape setCenterX(float centerX) {
		if ((points == null) || (center == null)) {
			checkPoints();
		}

		float xDiff = centerX - getCenterX();
		setX(x + xDiff);
		return this;
	}

	public float getCenterY() {
		checkPoints();

		return center[1];
	}

	public Shape setCenterY(float centerY) {
		if ((points == null) || (center == null)) {
			checkPoints();
		}

		float yDiff = centerY - getCenterY();
		setY(y + yDiff);
		return this;
	}

	public Shape setCenter(Vector2f pos) {
		setCenterX(pos.x);
		setCenterY(pos.y);
		return this;
	}

	public float getMaxX() {
		checkPoints();
		return maxX;
	}

	public float getMaxY() {
		checkPoints();
		return maxY;
	}

	public float getMinX() {
		checkPoints();
		return minX;
	}

	public float getMinY() {
		checkPoints();
		return minY;
	}

	public float getLeft() {
		return this.getMinX();
	}

	public float getRight() {
		return getMaxX();
	}

	public float getTop() {
		return getMinY();
	}

	public float getBottom() {
		return getMaxY();
	}

	public float getBoundingCircleRadius() {
		checkPoints();
		return boundingCircleRadius;
	}

	public float getDiameter() {
		return getBoundingCircleRadius() * 2f;
	}

	public float perimeter() {
		final TArray<PointF> result = new TArray<PointF>();
		final float[] points = getPoints();
		final int size = points.length;
		float perimeter = 0;
		for (int i = 0; i < size; i += 2) {
			result.add(new PointF(points[i], points[i + 1]));
		}
		for (int i = 0; i < result.size; i++) {
			PointF pointA = result.get(i);
			PointF pointB = result.get((i + 1) % result.size);
			Line line = new Line(pointA.x, pointA.y, pointB.x, pointB.y);
			perimeter += line.length();
		}
		return perimeter;
	}

	public float[] getCenter() {
		checkPoints();
		return center;
	}

	public float[] getPoints() {
		checkPoints();
		return points;
	}

	public TArray<Vector2f> getVertices() {
		if (_vertices == null) {
			_vertices = new TArray<Vector2f>();
		}
		if (pointsDirty) {
			checkPoints();
			_vertices.clear();
			int size = points.length;
			for (int i = 0; i < size; i += 2) {
				_vertices.add(new Vector2f(points[i], points[i + 1]));
			}
		}
		if (_vertices.size == 0) {
			checkPoints();
			int size = points.length;
			for (int i = 0; i < size; i += 2) {
				_vertices.add(new Vector2f(points[i], points[i + 1]));
			}
		}
		return _vertices;
	}

	public int getPointCount() {
		checkPoints();
		return points.length / 2;
	}

	public float[] getPoint(int index) {
		checkPoints();

		float result[] = new float[2];

		result[0] = points[index * 2];
		result[1] = points[index * 2 + 1];

		return result;
	}

	public float[] getNormal(int index) {
		float[] current = getPoint(index);
		float[] prev = getPoint(index - 1 < 0 ? getPointCount() - 1 : index - 1);
		float[] next = getPoint(index + 1 >= getPointCount() ? 0 : index + 1);

		float[] t1 = getNormal(prev, current);
		float[] t2 = getNormal(current, next);

		if ((index == 0) && (!closed())) {
			return t2;
		}
		if ((index == getPointCount() - 1) && (!closed())) {
			return t1;
		}

		float tx = (t1[0] + t2[0]) / 2;
		float ty = (t1[1] + t2[1]) / 2;
		float len = MathUtils.sqrt((tx * tx) + (ty * ty));
		return new float[] { tx / len, ty / len };
	}

	private float[] getNormal(float[] start, float[] end) {
		float dx = start[0] - end[0];
		float dy = start[1] - end[1];
		float len = MathUtils.sqrt((dx * dx) + (dy * dy));
		dx /= len;
		dy /= len;
		return new float[] { -dy, dx };
	}

	public Vector2f collideSAT(Shape s) {
		return collideSAT(this, s);
	}

	public boolean contains(XY xy) {
		return xy == null ? false : contains(xy.getX(), xy.getY());
	}

	public boolean contains(Shape other) {
		for (int i = 0; i < other.getPointCount(); i++) {
			float[] pt = other.getPoint(i);
			if (!contains(pt[0], pt[1])) {
				return false;
			}
		}
		return true;
	}

	public boolean includes(float x, float y) {
		if (points.length == 0) {
			return false;
		}

		checkPoints();

		Line testLine = new Line(0, 0, 0, 0);
		Vector2f pt = new Vector2f(x, y);

		for (int i = 0; i < points.length; i += 2) {
			int n = i + 2;
			if (n >= points.length) {
				n = 0;
			}
			testLine.set(points[i], points[i + 1], points[n], points[n + 1]);

			if (testLine.on(pt)) {
				return true;
			}
		}

		return false;
	}

	public int indexOf(float x, float y) {
		for (int i = 0; i < points.length; i += 2) {
			if ((points[i] == x) && (points[i + 1] == y)) {
				return i / 2;
			}
		}

		return -1;
	}

	public boolean contains(float x, float y, float size) {
		return inPoint(x, y, size);
	}

	public boolean contains(float x, float y) {
		checkPoints();
		if (points.length == 0) {
			return false;
		}
		boolean result = false;
		float xnew, ynew;
		float xold, yold;
		float x1, y1;
		float x2, y2;
		final int npoints = points.length;
		xold = points[npoints - 2];
		yold = points[npoints - 1];
		for (int i = 0; i < npoints; i += 2) {
			xnew = points[i];
			ynew = points[i + 1];
			if (xnew > xold) {
				x1 = xold;
				x2 = xnew;
				y1 = yold;
				y2 = ynew;
			} else {
				x1 = xnew;
				x2 = xold;
				y1 = ynew;
				y2 = yold;
			}
			if ((xnew < x) == (x <= xold) && (y - y1) * (x2 - x1) < (y2 - y1) * (x - x1)) {
				result = !result;
			}
			xold = xnew;
			yold = ynew;
		}
		return result;
	}

	public boolean intersects(XY pos) {
		if (pos == null) {
			return false;
		}
		checkPoints();
		return CollisionHelper.checkPointvsPolygon(pos, getPoints());
	}

	public boolean intersects(Shape shape) {
		if (shape == null) {
			return false;
		}
		checkPoints();
		boolean result = false;
		final float points[] = getPoints();
		final float thatPoints[] = shape.getPoints();
		int length = points.length;
		int thatLength = thatPoints.length;
		float unknownA;
		float unknownB;
		if (!closed()) {
			length -= 2;
		}
		if (!shape.closed()) {
			thatLength -= 2;
		}
		for (int i = 0; i < length; i += 2) {
			int iNext = i + 2;
			if (iNext >= points.length) {
				iNext = 0;
			}
			for (int j = 0; j < thatLength; j += 2) {
				int jNext = j + 2;
				if (jNext >= thatPoints.length) {
					jNext = 0;
				}
				unknownA = (((points[iNext] - points[i]) * (thatPoints[j + 1] - points[i + 1]))
						- ((points[iNext + 1] - points[i + 1]) * (thatPoints[j] - points[i])))
						/ (((points[iNext + 1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j]))
								- ((points[iNext] - points[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
				unknownB = (((thatPoints[jNext] - thatPoints[j]) * (thatPoints[j + 1] - points[i + 1]))
						- ((thatPoints[jNext + 1] - thatPoints[j + 1]) * (thatPoints[j] - points[i])))
						/ (((points[iNext + 1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j]))
								- ((points[iNext] - points[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
				if (unknownA >= 0 && unknownA <= 1 && unknownB >= 0 && unknownB <= 1) {
					result = true;
					break;
				}
			}
			if (result) {
				break;
			}
		}

		return result;
	}

	public boolean collided(Shape shape) {
		if (shape == null) {
			return false;
		}
		boolean result = contains(shape);
		if (!result) {
			result = intersects(shape);
		}
		return result;
	}

	public boolean hasVertex(float x, float y) {
		if (points.length == 0) {
			return false;
		}

		checkPoints();

		for (int i = 0; i < points.length; i += 2) {
			if ((points[i] == x) && (points[i + 1] == y)) {
				return true;
			}
		}

		return false;
	}

	protected void findCenter() {
		center = new float[] { 0, 0 };
		int length = points.length;
		for (int i = 0; i < length; i += 2) {
			center[0] += points[i];
			center[1] += points[i + 1];
		}
		center[0] /= (length / 2);
		center[1] /= (length / 2);
	}

	protected void calculateRadius() {
		boundingCircleRadius = 0;

		for (int i = 0; i < points.length; i += 2) {
			float temp = ((points[i] - center[0]) * (points[i] - center[0]))
					+ ((points[i + 1] - center[1]) * (points[i + 1] - center[1]));
			boundingCircleRadius = (boundingCircleRadius > temp) ? boundingCircleRadius : temp;
		}
		boundingCircleRadius = MathUtils.sqrt(boundingCircleRadius);
	}

	protected void calculateTriangles() {
		if ((!trianglesDirty) && (triangle != null)) {
			return;
		}
		if (points.length >= 6) {
			triangle = new TriangleNeat();
			for (int i = 0; i < points.length; i += 2) {
				triangle.addPolyPoint(points[i], points[i + 1]);
			}
			triangle.triangulate();
		}

		trianglesDirty = false;
	}

	private void callTransform(Matrix3 m) {
		if (points != null) {
			float[] result = new float[points.length];
			m.transform(points, 0, result, 0, points.length / 2);
			this.points = result;
			this.pointsDirty = true;
			this.checkPoints();
		}
	}

	public void setScale(float s) {
		this.setScale(s, s);
	}

	public void setScale(float sx, float sy) {
		if (scaleX != sx || scaleY != sy) {
			Matrix3 m = new Matrix3();
			m.scale(scaleX = sx, scaleY = sy);
			this.callTransform(m);
		}
	}

	public float getScaleX() {
		return scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public boolean isScaled() {
		return scaleX != 1f || scaleY != 1f;
	}

	public float getRotation() {
		return rotation;
	}

	public boolean isRotated() {
		return rotation != 0f;
	}

	public Shape setRotation(float r) {
		checkPoints();
		return this.setRotation(r, this.center[0], this.center[1]);
	}

	public Shape setRotation(float r, float x, float y) {
		if (!MathUtils.equal(rotation, r)) {
			this.callTransform(Matrix3.createRotateTransform(rotation = (r * MathUtils.RAD_TO_DEG), x, y));
			this.updatePoints();
		}
		return this;
	}

	public void increaseTriangulation() {
		checkPoints();
		calculateTriangles();

		triangle = new TriangleOver(triangle);
	}

	public Triangle getTriangles() {
		checkPoints();
		calculateTriangles();
		return triangle;
	}

	protected final void updatePoints() {
		this.pointsDirty = true;
		final int size = points.length;
		if (size > 0) {
			maxX = points[0];
			maxY = points[1];
			minX = points[0];
			minY = points[1];
			for (int i = 0; i < size / 2; i++) {
				int idx = i * 2;
				maxX = MathUtils.max(points[idx], maxX);
				maxY = MathUtils.max(points[idx + 1], maxY);
				minX = MathUtils.min(points[idx], minX);
				minY = MathUtils.min(points[idx + 1], minY);
			}
		}
	}

	protected final void checkPoints() {
		if (pointsDirty) {
			createPoints();
			findCenter();
			calculateRadius();
			if (points == null) {
				return;
			}
			updatePoints();
			pointsDirty = false;
			trianglesDirty = true;
		}
	}

	public void preCache() {
		checkPoints();
		getTriangles();
	}

	public boolean closed() {
		return true;
	}

	public Polygon prune() {
		Polygon result = new Polygon();

		for (int i = 0; i < getPointCount(); i++) {
			int next = i + 1 >= getPointCount() ? 0 : i + 1;
			int prev = i - 1 < 0 ? getPointCount() - 1 : i - 1;

			float dx1 = getPoint(i)[0] - getPoint(prev)[0];
			float dy1 = getPoint(i)[1] - getPoint(prev)[1];
			float dx2 = getPoint(next)[0] - getPoint(i)[0];
			float dy2 = getPoint(next)[1] - getPoint(i)[1];

			float len1 = MathUtils.sqrt((dx1 * dx1) + (dy1 * dy1));
			float len2 = MathUtils.sqrt((dx2 * dx2) + (dy2 * dy2));
			dx1 /= len1;
			dy1 /= len1;
			dx2 /= len2;
			dy2 /= len2;

			if ((dx1 != dx2) || (dy1 != dy2)) {
				result.addPoint(getPoint(i)[0], getPoint(i)[1]);
			}
		}

		return result;
	}

	public float getWidth() {
		checkPoints();
		return maxX - minX;
	}

	public float getHeight() {
		checkPoints();
		return maxY - minY;
	}

	public RectBox getRect() {
		checkPoints();
		if (rect == null) {
			rect = new RectBox(x, y, getWidth(), getHeight());
		} else {
			rect.setBounds(x, y, getWidth(), getHeight());
		}
		return rect;
	}

	public AABB getAABB() {
		checkPoints();
		if (aabb == null) {
			aabb = new AABB(minX, minY, maxX, maxY);
		} else {
			aabb.set(minX, minY, maxX, maxY);
		}
		return aabb;
	}

	public Shape[] subtract(Shape source) {
		return this.subtract(this, source);
	}

	public Shape[] subtract(Shape target, Shape source) {
		target = target.cpy();
		source = source.cpy();
		int count = 0;
		for (int i = 0; i < target.getPointCount(); i++) {
			if (source.contains(target.getPoint(i)[0], target.getPoint(i)[1])) {
				count++;
			}
		}
		if (count == target.getPointCount()) {
			return new Shape[0];
		}
		if (!target.intersects(source)) {
			return new Shape[] { target };
		}
		int found = 0;
		int j;
		for (j = 0; j < source.getPointCount(); j++) {
			if (target.contains(source.getPoint(j)[0], source.getPoint(j)[1])
					&& !onPath(target, source.getPoint(j)[0], source.getPoint(j)[1])) {
				found++;
			}
		}
		for (j = 0; j < target.getPointCount(); j++) {
			if (source.contains(target.getPoint(j)[0], target.getPoint(j)[1])
					&& !onPath(source, target.getPoint(j)[0], target.getPoint(j)[1])) {
				found++;
			}
		}
		if (found < 1) {
			return new Shape[] { target };
		}
		return combine(target, source, true);
	}

	private Shape[] combine(Shape target, Shape other, boolean subtract) {
		if (subtract) {
			TArray<Shape> shapes = new TArray<Shape>();
			TArray<Vector2f> used = new TArray<Vector2f>();
			int j;
			for (j = 0; j < target.getPointCount(); j++) {
				float[] point = target.getPoint(j);
				if (other.contains(point[0], point[1])) {
					used.add(new Vector2f(point[0], point[1]));
				}
			}
			for (j = 0; j < target.getPointCount(); j++) {
				float[] point = target.getPoint(j);
				Vector2f pt = new Vector2f(point[0], point[1]);
				if (!used.contains(pt)) {
					Shape result = combineSingle(target, other, true, j);
					shapes.add(result);
					for (int k = 0; k < result.getPointCount(); k++) {
						float[] kpoint = result.getPoint(k);
						Vector2f kpt = new Vector2f(kpoint[0], kpoint[1]);
						used.add(kpt);
					}
				}
			}
			Shape[] list = new Shape[shapes.size];
			for (int i = 0; i < shapes.size; i++) {
				list[i] = shapes.get(i);
			}
			return list;
		}
		for (int i = 0; i < target.getPointCount(); i++) {
			if (!other.contains(target.getPoint(i)[0], target.getPoint(i)[1])
					&& !other.hasVertex(target.getPoint(i)[0], target.getPoint(i)[1])) {
				Shape shape = combineSingle(target, other, false, i);
				return new Shape[] { shape };
			}
		}
		return new Shape[] { other };
	}

	public static int rationalPoint(Shape shape, int p) {
		while (p < 0) {
			p += shape.getPointCount();
		}
		while (p >= shape.getPointCount()) {
			p -= shape.getPointCount();
		}
		return p;
	}

	public Shape[] union(Shape target, Shape other) {
		target = target.cpy();
		other = other.cpy();
		if (!target.intersects(other)) {
			return new Shape[] { target, other };
		}
		boolean touches = false;
		int buttCount = 0;
		int i;
		for (i = 0; i < target.getPointCount(); i++) {
			if (other.contains(target.getPoint(i)[0], target.getPoint(i)[1])
					&& !other.hasVertex(target.getPoint(i)[0], target.getPoint(i)[1])) {
				touches = true;
				break;
			}
			if (other.hasVertex(target.getPoint(i)[0], target.getPoint(i)[1])) {
				buttCount++;
			}
		}
		for (i = 0; i < other.getPointCount(); i++) {
			if (target.contains(other.getPoint(i)[0], other.getPoint(i)[1])
					&& !target.hasVertex(other.getPoint(i)[0], other.getPoint(i)[1])) {
				touches = true;
				break;
			}
		}
		if (!touches && buttCount < 2) {
			return new Shape[] { target, other };
		}
		return combine(target, other, false);
	}

	public Line getLine(Shape shape, int s, int e) {
		float[] start = shape.getPoint(s);
		float[] end = shape.getPoint(e);
		Line line = new Line(start[0], start[1], end[0], end[1]);
		return line;
	}

	public Line getLine(Shape shape, float sx, float sy, int e) {
		float[] end = shape.getPoint(e);
		Line line = new Line(sx, sy, end[0], end[1]);
		return line;
	}

	public HitResult intersect(Shape shape, Line line) {
		float distance = Float.MAX_VALUE;
		HitResult hit = null;
		for (int i = 0; i < shape.getPointCount(); i++) {
			int next = rationalPoint(shape, i + 1);
			Line local = getLine(shape, i, next);
			Vector2f pt = line.intersect(local, true);
			if (pt != null) {
				float newDis = pt.distance(line.getStart());
				if (newDis < distance && newDis > EPSILON) {
					hit = new HitResult();
					hit.pt = pt;
					hit.line = local;
					hit.p1 = i;
					hit.p2 = next;
					distance = newDis;
				}
			}
		}
		return hit;
	}

	private Shape combineSingle(Shape target, Shape missing, boolean subtract, int start) {
		Shape current = target;
		Shape other = missing;
		int point = start;
		int dir = 1;
		Polygon poly = new Polygon();
		boolean first = true;
		int loop = 0;
		float px = current.getPoint(point)[0];
		float py = current.getPoint(point)[1];
		while (!poly.hasVertex(px, py) || first || current != target) {
			first = false;
			loop++;
			if (loop > MAX_POINTS) {
				break;
			}
			poly.addPoint(px, py);
			Line line = getLine(current, px, py, rationalPoint(current, point + dir));
			HitResult hit = intersect(other, line);
			if (hit != null) {
				Line hitLine = hit.line;
				Vector2f pt = hit.pt;
				px = pt.x;
				py = pt.y;
				if (other.hasVertex(px, py)) {
					point = other.indexOf(pt.x, pt.y);
					dir = 1;
					px = pt.x;
					py = pt.y;
					Shape shape = current;
					current = other;
					other = shape;
					continue;
				}
				float dx = hitLine.getDX() / hitLine.length();
				float dy = hitLine.getDY() / hitLine.length();
				dx *= EDGE_SCALE;
				dy *= EDGE_SCALE;
				if (current.contains(pt.x + dx, pt.y + dy)) {
					if (subtract) {
						if (current == missing) {
							point = hit.p2;
							dir = -1;
						} else {
							point = hit.p1;
							dir = 1;
						}
					} else if (current == target) {
						point = hit.p2;
						dir = -1;
					} else {
						point = hit.p2;
						dir = -1;
					}
					Shape shape = current;
					current = other;
					other = shape;
					continue;
				}
				if (current.contains(pt.x - dx, pt.y - dy)) {
					if (subtract) {
						if (current == target) {
							point = hit.p2;
							dir = -1;
						} else {
							point = hit.p1;
							dir = 1;
						}
					} else if (current == missing) {
						point = hit.p1;
						dir = 1;
					} else {
						point = hit.p1;
						dir = 1;
					}
					Shape shape = current;
					current = other;
					other = shape;
					continue;
				}
				if (subtract) {
					break;
				}
				point = hit.p1;
				dir = 1;
				Shape temp = current;
				current = other;
				other = temp;
				point = rationalPoint(current, point + dir);
				px = current.getPoint(point)[0];
				py = current.getPoint(point)[1];
				continue;
			}
			point = rationalPoint(current, point + dir);
			px = current.getPoint(point)[0];
			py = current.getPoint(point)[1];
		}
		poly.addPoint(px, py);
		return poly;
	}

	private boolean onPath(Shape path, float x, float y) {
		for (int i = 0; i < path.getPointCount() + 1; i++) {
			int n = rationalPoint(path, i + 1);
			Line line = getLine(path, rationalPoint(path, i), n);
			if (line.distance(new Vector2f(x, y)) < EPSILON * 100f) {
				return true;
			}
		}
		return false;
	}

	public ShapeEntity getEntity() {
		return getEntity(LColor.white, true);
	}

	public ShapeEntity getEntity(LColor c, boolean fill) {
		if (entity == null) {
			entity = new ShapeEntity(this, c, fill);
		} else {
			entity.setShape(this);
		}
		return entity;
	}

	public boolean equalsRotateScale(float rotate, float sx, float sy) {
		return MathUtils.equal(rotate, this.rotation) && MathUtils.equal(sx, this.scaleX)
				&& MathUtils.equal(sy, this.scaleY);
	}

	public boolean equals(Shape shape) {
		if (shape == null) {
			return false;
		}
		if (shape == this) {
			return true;
		}
		boolean eq = MathUtils.equal(shape.x, this.x) && MathUtils.equal(shape.y, this.y)
				&& MathUtils.equal(shape.rotation, this.rotation) && MathUtils.equal(shape.minX, this.minX)
				&& MathUtils.equal(shape.minY, this.minY) && MathUtils.equal(shape.maxX, this.maxX)
				&& MathUtils.equal(shape.maxY, this.maxY)
				&& MathUtils.equal(shape.boundingCircleRadius, this.boundingCircleRadius);
		if (eq) {
			checkPoints();
			if (eq && shape.points != null) {
				eq = CollectionUtils.equals(this.points, shape.points);
			}
			if (eq && shape.center != null) {
				eq = CollectionUtils.equals(this.center, shape.center);
			}
		}
		return eq;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof Shape) {
			return equals((Shape) obj);
		}
		return super.equals(obj);
	}

	public Shape cpy() {
		float[] copyPoints = new float[points.length];
		System.arraycopy(points, 0, copyPoints, 0, copyPoints.length);
		return new Polygon(copyPoints);
	}

	public Shape copy(Shape shape) {
		if (shape == null) {
			return this;
		}
		if (shape == this) {
			return this;
		}
		this.x = shape.x;
		this.y = shape.y;
		this.rotation = shape.rotation;
		if (shape.points != null) {
			this.points = CollectionUtils.copyOf(shape.points);
		}
		if (shape.center != null) {
			this.center = CollectionUtils.copyOf(shape.center);
		}
		this.scaleX = shape.scaleX;
		this.scaleY = shape.scaleY;
		this.minX = shape.minX;
		this.minY = shape.minY;
		this.maxX = shape.maxX;
		this.maxY = shape.maxY;
		this.boundingCircleRadius = shape.boundingCircleRadius;
		this.pointsDirty = shape.pointsDirty;
		this.triangle = shape.triangle;
		this.trianglesDirty = shape.trianglesDirty;
		if (shape.aabb != null) {
			this.aabb = shape.aabb.cpy();
		}
		if (shape.rect != null) {
			this.rect = shape.rect.cpy();
		}
		if (shape.entity != null) {
			this.entity = shape.entity;
		}
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	@Override
	public int size() {
		return points == null ? 0 : points.length;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public void clear() {
		points = new float[0];
		center = new float[0];
		x = 0;
		y = 0;
		rotation = 0;
		scaleX = 1f;
		scaleY = 1f;
		maxX = maxY = 0;
		minX = minY = 0;
		pointsDirty = true;
	}

	@Override
	public int hashCode() {
		final int prime = 67;
		int hashCode = 17;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, minX);
		hashCode = prime * LSystem.unite(hashCode, minY);
		hashCode = prime * LSystem.unite(hashCode, maxX);
		hashCode = prime * LSystem.unite(hashCode, maxY);
		hashCode = prime * LSystem.unite(hashCode, scaleX);
		hashCode = prime * LSystem.unite(hashCode, scaleY);
		for (int j = 0; j < points.length; j++) {
			final long val = NumberUtils.floatToIntBits(this.points[j]);
			hashCode += prime * hashCode + (int) (val ^ (val >>> 32));
		}
		if (center != null) {
			for (int i = 0; i < center.length; i++) {
				hashCode = prime * LSystem.unite(hashCode, center[i]);
			}
		}
		return hashCode;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Shape");
		builder.kv("pos", x + "," + y).comma().kv("size", getWidth() + "," + getHeight()).comma()
				.kv("scale", scaleX + "," + scaleY).comma().kv("points", "[" + StringUtils.join(',', points) + "]")
				.comma().kv("center", "[" + StringUtils.join(',', center) + "]").comma()
				.kv("circleRadius", boundingCircleRadius).comma().kv("rotation", rotation).comma().kv("minX", minX)
				.comma().kv("minY", minY).comma().kv("maxX", maxX).comma().kv("maxY", maxY);
		return builder.toString();
	}
}
