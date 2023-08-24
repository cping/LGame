/**
 * 
 * Copyright 2008 - 2023
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

import loon.LSysException;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Polygon extends Shape implements BoxSize {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7491444927273846690L;

	public final static TArray<Vector2f> offsetPolygon(float[] points, float offset) {
		final TArray<Vector2f> offsetPoints = new TArray<Vector2f>();
		final int length = points.length;

		offset = isPolygonClockwise(points) ? offset : -1f * offset;

		for (int j = 0; j < length; j += 2) {
			int i = (j - 2);

			if (i < 0) {
				i += length;
			}

			final int k = (j + 2) % length;

			float v1x = points[j] - points[i];
			float v1y = points[j + 1] - points[i + 1];
			float len = MathUtils.sqrt((v1x * v1x) + (v1y * v1y));

			v1x /= len;
			v1y /= len;
			v1x *= offset;
			v1y *= offset;

			float norm1x = -v1y;
			float norm1y = v1x;

			final float[] pij1 = new float[] { points[i] + norm1x, points[i + 1] + norm1y };
			final float[] pij2 = new float[] { points[j] + norm1x, points[j + 1] + norm1y };

			float v2x = points[k] - points[j];
			float v2y = points[k + 1] - points[j + 1];

			len = MathUtils.sqrt((v2x * v2x) + (v2y * v2y));

			v2x /= len;
			v2y /= len;
			v2x *= offset;
			v2y *= offset;

			final float norm2x = -v2y;
			final float norm2y = v2x;

			final float[] pjk1 = new float[] { points[j] + norm2x, points[j + 1] + norm2y };
			final float[] pjk2 = new float[] { points[k] + norm2x, points[k + 1] + norm2y };

			final Vector2f intersectPoint = findIntersection(pij1[0], pij1[1], pij2[0], pij2[1], pjk1[0], pjk1[1],
					pjk2[0], pjk2[1]);

			if (intersectPoint != null) {
				offsetPoints.add(intersectPoint);
			}
		}
		return offsetPoints;
	}

	public final static boolean isPolygonClockwise(float[] points) {
		int sum = 0;
		for (int i = 0, j = points.length - 2; i < points.length; j = i, i += 2) {
			sum += (points[i] - points[j]) * (points[i + 1] + points[j + 1]);
		}
		return sum > 0;
	}

	public final static Vector2f findIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4,
			float y4) {
		float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
		float a = ((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3));
		float b = ((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3));
		if (d == 0f) {
			if (a == 0 && b == 0f) {
				return new Vector2f((x1 + x2) / 2f, (y1 + y2) / 2f);
			}
			return null;
		}
		float uA = a / d;
		return new Vector2f(x1 + (uA * (x2 - x1)), y1 + (uA * (y2 - y1)));
	}

	private boolean allowDups = false;

	private boolean closed = true;

	private TArray<Vector2f> _vertices;

	public Polygon(float[] points) {
		int length = points.length;

		this.points = new float[length];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		x = Float.MAX_VALUE;
		y = Float.MAX_VALUE;

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

	public Polygon() {
		points = new float[0];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
	}

	public Polygon(float[] xpoints, float[] ypoints, int npoints) {
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new LSysException("npoints < 0");
		}
		points = new float[0];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		for (int i = 0; i < npoints; i++) {
			addPoint(xpoints[i], ypoints[i]);
		}
	}

	public Polygon(int[] xpoints, int[] ypoints, int npoints) {
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new LSysException("npoints < 0");
		}
		points = new float[0];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		for (int i = 0; i < npoints; i++) {
			addPoint(xpoints[i], ypoints[i]);
		}
	}

	public Polygon(TArray<Vector2f> vectors) {
		if (vectors == null || vectors.size < 0) {
			throw new LSysException("points < 0");
		}
		points = new float[0];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		for (int i = 0; i < vectors.size; i++) {
			Vector2f pos = vectors.get(i);
			addPoint(pos.x, pos.y);
		}
	}

	public Polygon setAllowDuplicatePoints(boolean allowDups) {
		this.allowDups = allowDups;
		return this;
	}

	public Polygon addPoint(float x, float y) {
		if (hasVertex(x, y) && (!allowDups)) {
			return this;
		}
		int size = points.length;
		TArray<Float> tempPoints = new TArray<Float>();
		for (int i = 0; i < size; i++) {
			tempPoints.add(points[i]);
		}
		tempPoints.add(x);
		tempPoints.add(y);
		int length = tempPoints.size;
		this.points = new float[length];
		for (int i = 0; i < length; i++) {
			points[i] = tempPoints.get(i);
		}
		if (x > maxX) {
			maxX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (x < minX) {
			minX = x;
		}
		if (y < minY) {
			minY = y;
		}
		findCenter();
		calculateRadius();

		pointsDirty = true;
		return this;
	}

	@Override
	public Shape transform(Matrix3 transform) {
		checkPoints();

		Polygon resultPolygon = new Polygon();

		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultPolygon.points = result;
		resultPolygon.findCenter();
		resultPolygon.closed = closed;

		return resultPolygon;
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		pointsDirty = false;
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		pointsDirty = false;
	}

	public Polygon addVertex(float x, float y) {
		return addPoint(x, y);
	}

	public Polygon addVertex(Vector2f v) {
		return addVertex(v.x, v.y);
	}

	public TArray<Vector2f> getVertices() {
		if (_vertices == null) {
			_vertices = new TArray<Vector2f>();
		}
		if (pointsDirty) {
			_vertices.clear();
			int size = points.length;
			for (int i = 0; i < size; i += 2) {
				_vertices.add(new Vector2f(points[i], points[i + 1]));
			}
			pointsDirty = false;
		}
		return _vertices;
	}

	@Override
	protected void createPoints() {

	}

	@Override
	public boolean closed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	@Override
	public boolean contains(XY pos) {
		if (pos == null) {
			return false;
		}
		return contains(pos.getX(), pos.getY());
	}

	@Override
	public boolean contains(float x, float y) {
		boolean inside = false;
		final float[] result = getPoints();
		final int length = result.length / 2;

		for (int i = 0, j = length - 1; i < length; j = i++) {
			final float xi = result[i * 2];
			final float yi = result[(i * 2) + 1];
			final float xj = result[j * 2];
			final float yj = result[(j * 2) + 1];
			final boolean intersect = ((yi > y) != (yj > y)) && (x < ((xj - xi) * ((y - yi) / (yj - yi))) + xi);
			if (intersect) {
				inside = !inside;
			}
		}
		return inside;
	}

	public Polygon cpy() {
		float[] copyPoints = new float[points.length];
		System.arraycopy(points, 0, copyPoints, 0, copyPoints.length);
		return new Polygon(copyPoints);
	}

	@Override
	public void setWidth(float w) {
		this.maxX = w;
		this.pointsDirty = true;
	}

	@Override
	public void setHeight(float h) {
		this.maxY = h;
		this.pointsDirty = true;
	}

	public boolean isPolygonClockwise() {
		return isPolygonClockwise(getPoints());
	}

	public Polygon getOffsetPolygon(float offset) {
		return new Polygon(offsetPolygon(getPoints(), offset));
	}

	public RectBox getBox() {
		TArray<Vector2f> v = getVertices();
		float miX = this.minX;
		float miY = this.minY;
		float maX = this.maxX;
		float maY = this.maxY;
		for (int i = 0; i < v.size; i++) {
			Vector2f p = v.get(i);
			miX = MathUtils.min(miX, p.x);
			miY = MathUtils.min(miY, p.y);
			maX = MathUtils.max(maX, p.x);
			maY = MathUtils.max(maY, p.y);
		}
		return new RectBox(miX, miY, maX - miX, maY - miY);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Polygon");
		builder.kv("points", "[" + StringUtils.join(',', points) + "]").comma()
				.kv("center", "[" + StringUtils.join(',', center) + "]").comma().kv("rotation", rotation).comma()
				.kv("minX", minX).comma().kv("minY", minY).comma().kv("maxX", maxX).comma().kv("maxY", maxY);
		return builder.toString();
	}
}
