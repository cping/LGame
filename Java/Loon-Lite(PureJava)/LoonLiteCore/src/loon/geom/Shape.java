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

import loon.action.sprite.ShapeEntity;
import loon.canvas.LColor;
import loon.utils.IArray;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public abstract class Shape implements Serializable, IArray, XY, SetXY {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public Shape setLocation(float x, float y) {
		setX(x);
		setY(y);
		return this;
	}

	public abstract Shape transform(Matrix3 transform);

	protected abstract void createPoints();

	public void translate(int deltaX, int deltaY) {
		setX(x + deltaX);
		setY(y + deltaY);
	}

	public int vertexCount() {
		return points.length / 2;
	}

	public Vector2f getPosition() {
		return new Vector2f(getX(), getY());
	}

	public Vector2f getCenterPos() {
		return new Vector2f(getCenterX(), getCenterY());
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		if (x != this.x || x == 0) {
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
		return y;
	}

	public float length() {
		return MathUtils.sqrt(x * x + y * y);
	}

	public void setLocation(Vector2f loc) {
		setX(loc.x);
		setY(loc.y);
	}

	public float getCenterX() {
		checkPoints();
		return center[0];
	}

	public void setCenterX(float centerX) {
		if ((points == null) || (center == null)) {
			checkPoints();
		}

		float xDiff = centerX - getCenterX();
		setX(x + xDiff);
	}

	public float getCenterY() {
		checkPoints();

		return center[1];
	}

	public void setCenterY(float centerY) {
		if ((points == null) || (center == null)) {
			checkPoints();
		}

		float yDiff = centerY - getCenterY();
		setY(y + yDiff);
	}

	public void setCenter(Vector2f pos) {
		setCenterX(pos.x);
		setCenterY(pos.y);
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

	public float getBoundingCircleRadius() {
		checkPoints();
		return boundingCircleRadius;
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

	private float[] getNormal(float[] start, float[] end) {
		float dx = start[0] - end[0];
		float dy = start[1] - end[1];
		float len = MathUtils.sqrt((dx * dx) + (dy * dy));
		dx /= len;
		dy /= len;
		return new float[] { -dy, dx };
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
		int npoints = points.length;

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

	public boolean intersects(Shape shape) {
		if (shape == null) {
			return false;
		}

		checkPoints();

		boolean result = false;
		float points[] = getPoints();
		float thatPoints[] = shape.getPoints();
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

				unknownA = (((points[iNext] - points[i]) * (float) (thatPoints[j + 1] - points[i + 1]))
						- ((points[iNext + 1] - points[i + 1]) * (thatPoints[j] - points[i])))
						/ (((points[iNext + 1] - points[i + 1]) * (thatPoints[jNext] - thatPoints[j]))
								- ((points[iNext] - points[i]) * (thatPoints[jNext + 1] - thatPoints[j + 1])));
				unknownB = (((thatPoints[jNext] - thatPoints[j]) * (float) (thatPoints[j + 1] - points[i + 1]))
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

	public float getRotation() {
		return rotation;
	}

	public Shape setRotation(float r) {
		return this.setRotation(r, this.center[0], this.center[1]);
	}

	public Shape setRotation(float r, float x, float y) {
		if (rotation != r) {
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
		return maxX - minX;
	}

	public float getHeight() {
		return maxY - minY;
	}

	public RectBox getRect() {
		if (rect == null) {
			rect = new RectBox(x, y, getWidth(), getHeight());
		} else {
			rect.setBounds(x, y, getWidth(), getHeight());
		}
		return rect;
	}

	public AABB getAABB() {
		if (aabb == null) {
			aabb = new AABB(minX, minY, maxX, maxY);
		} else {
			aabb.set(minX, minY, maxX, maxY);
		}
		return aabb;
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

	@Override
	public int size() {
		return points == null ? 0 : points.length;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
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
		final int prime = 31;
		int result = 17;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		result = prime * result + NumberUtils.floatToIntBits(scaleX);
		result = prime * result + NumberUtils.floatToIntBits(scaleY);
		for (int j = 0; j < points.length; j++) {
			final long val = NumberUtils.floatToIntBits(this.points[j]);
			result += 31 * result + (int) (val ^ (val >>> 32));
		}
		return result;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Shape");
		builder.kv("pos", x + "," + y).comma().kv("scale", scaleX + "," + scaleY).comma()
				.kv("points", "[" + StringUtils.join(',', points) + "]").comma()
				.kv("center", "[" + StringUtils.join(',', center) + "]").comma().kv("rotation", rotation).comma()
				.kv("minX", minX).comma().kv("minY", minY).comma().kv("maxX", maxX).comma().kv("maxY", maxY);
		return builder.toString();
	}
}
