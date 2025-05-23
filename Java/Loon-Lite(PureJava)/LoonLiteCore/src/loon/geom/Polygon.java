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
import loon.events.QueryEvent;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Polygon extends Shape implements BoxSize {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7491444927273846690L;

	public final static Polygon rect(float x, float y, float w, float h) {
		return rect(Vector2f.at(x + w / 2f, y + h / 2f), Vector2f.at(w / 2f, h / 2f));
	}

	public final static Polygon rect(XY center, Vector2f size) {
		if (center == null) {
			center = Vector2f.ZERO();
		}
		if (size == null) {
			size = Vector2f.ZERO();
		}
		return new Polygon(new Vector2f(center.getX() - size.x, center.getY() - size.y),
				new Vector2f(center.getX() + size.x, center.getY() - size.y),
				new Vector2f(center.getX() + size.x, center.getY() + size.y),
				new Vector2f(center.getX() - size.x, center.getY() + size.y));
	}

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

	public final static boolean isConvexTriangle(float ax, float ay, float bx, float by, float cx, float cy) {
		return (ay - by) * (cx - bx) + (bx - ax) * (cy - by) >= 0f;
	}

	public final static boolean isVectorsIntersecting(float ax, float ay, float bx, float by, float cx, float cy,
			float dx, float dy) {
		if ((ax == bx && ay == by) || (cx == dx && cy == dy)) {
			return false;
		}
		float abx = bx - ax;
		float aby = by - ay;
		float cdx = dx - cx;
		float cdy = dy - cy;
		float tDen = cdy * abx - cdx * aby;
		if (tDen == 0f) {
			return false;
		}
		float t = (aby * (cx - ax) - abx * (cy - ay)) / tDen;
		if (t < 0f || t > 1f) {
			return false;
		}
		float result = aby != 0f ? (cy - ay + t * cdy) / aby : (cx - ax + t * cdx) / abx;
		return result >= 0f && result <= 1f;
	}

	public final static float getPolygonSignedArea(Vector2f[] points) {
		if (points.length < 3) {
			return 0;
		}
		float sum = 0;
		for (int i = 0; i < points.length; i++) {
			Vector2f p1 = points[i];
			Vector2f p2 = i != points.length - 1 ? points[i + 1] : points[0];
			sum += (p1.x * p2.y) - (p1.y * p2.x);
		}
		return 0.5f * sum;
	}

	public final static float getPolygonArea(Vector2f[] points) {
		return MathUtils.abs(getPolygonSignedArea(points));
	}

	public final static boolean isPolygonCCW(Vector2f[] points) {
		return getPolygonSignedArea(points) > 0;
	}

	public final static Vector2f getCenter(TArray<Vector2f> vertices) {
		Vector2f center = new Vector2f();
		for (int i = 0; i < vertices.size; i++) {
			center.addSelf(vertices.get(i));
		}
		center.mulSelf(1f / (float) vertices.size());
		return center;
	}

	public final static TArray<Vector2f> getCenterVertices(TArray<Vector2f> vertices, Vector2f vertex) {
		TArray<Vector2f> newVertices = new TArray<Vector2f>();
		for (int i = 0; i < vertices.size; i++) {
			newVertices.add(vertices.get(i).sub(vertex));
		}
		return newVertices;
	}

	public final static TArray<Vector2f> getCenterVerticesReverse(TArray<Vector2f> vertices, Vector2f vertex) {
		return getCenterVertices(vertices, vertex.mul(-1f));
	}

	public final static Vector2f getClosestNode(float x, float y, TArray<Vector2f> nodes) {
		Vector2f closest = null;
		float closestDistance = Float.MAX_VALUE;
		for (int i = 0; i < nodes.size; i++) {
			Vector2f vertex = nodes.get(i);
			float distance = nodes.get(i).dst2(x, y);
			if (closest == null || closestDistance > distance) {
				closest = vertex;
				closestDistance = distance;
			}
		}
		return closest;
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

	private final TArray<Float> _tempPoints = new TArray<Float>();

	private boolean _allowDups = false;

	private boolean _closed = true;

	public Polygon() {
		this.initPoints();
	}

	public Polygon(Vector2f... vs) {
		this(new TArray<Vector2f>(vs));
	}

	public Polygon(TArray<Vector2f> vectors) {
		if (vectors == null) {
			throw new LSysException("points is null !");
		}
		if (vectors.size < 1) {
			throw new LSysException("points < 1");
		}
		this.initPoints(vectors.size);
		this.setPolygon(vectors);
	}

	public Polygon(float[] points) {
		if (points == null) {
			throw new LSysException("points is null !");
		}
		if (points.length < 1) {
			throw new LSysException("points < 1");
		}
		this.initPoints(points.length);
		this.setPolygon(points, points.length);
	}

	public Polygon(float[] xpoints, float[] ypoints, int npoints) {
		this.initPoints(npoints * 2);
		this.setPolygon(xpoints, ypoints, npoints);
	}

	public Polygon(int[] xpoints, int[] ypoints, int npoints) {
		this.initPoints(npoints * 2);
		this.setPolygon(xpoints, ypoints, npoints);
	}

	protected void initPoints() {
		this.initPoints(0);
	}

	protected void initPoints(int size) {
		points = new float[size];
		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
	}

	public Polygon setPolygon(TArray<Vector2f> vectors) {
		return setPolygon(vectors, false);
	}

	public Polygon setPolygon(TArray<Vector2f> vectors, boolean dirty) {
		this.setPolygon(syncPoints(vectors, dirty), vectors.size);
		return this;
	}

	public Polygon setPolygon(int[] xpoints, int[] ypoints, int npoints) {
		if (xpoints == null || ypoints == null) {
			throw new LSysException("points is null !");
		}
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new LSysException("npoints < 0");
		}
		int size = xpoints.length + ypoints.length;
		if (this.points == null) {
			this.points = new float[size];
		}
		int length = points.length;
		if (size != length) {
			points = new float[size];
			length = points.length;
		}
		this.maxX = -Float.MIN_VALUE;
		this.maxY = -Float.MIN_VALUE;
		this.minX = Float.MAX_VALUE;
		this.minY = Float.MAX_VALUE;
		this.x = Float.MAX_VALUE;
		this.y = Float.MAX_VALUE;
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (i % 2 == 0) {
				final int newX = xpoints[count];
				if (newX > maxX) {
					maxX = newX;
				}
				if (newX < minX) {
					minX = newX;
				}
				if (newX < x) {
					x = newX;
				}
				this.points[i] = newX;
			} else {
				final int newY = ypoints[count];
				if (newY > maxY) {
					maxY = newY;
				}
				if (newY < minY) {
					minY = newY;
				}
				if (newY < y) {
					y = newY;
				}
				this.points[i] = newY;
				count++;
			}
		}
		findCenter();
		calculateRadius();
		pointsDirty = true;
		return this;
	}

	public Polygon setPolygon(float[] xpoints, float[] ypoints, int npoints) {
		if (xpoints == null || ypoints == null) {
			throw new LSysException("points is null !");
		}
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new LSysException("npoints > xpoints.length || " + "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new LSysException("npoints < 0");
		}
		int size = xpoints.length + ypoints.length;
		if (this.points == null) {
			this.points = new float[size];
		}
		int length = points.length;
		if (size != length) {
			points = new float[size];
			length = points.length;
		}
		this.maxX = -Float.MIN_VALUE;
		this.maxY = -Float.MIN_VALUE;
		this.minX = Float.MAX_VALUE;
		this.minY = Float.MAX_VALUE;
		this.x = Float.MAX_VALUE;
		this.y = Float.MAX_VALUE;
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (i % 2 == 0) {
				final float newX = xpoints[count];
				if (newX > maxX) {
					maxX = newX;
				}
				if (newX < minX) {
					minX = newX;
				}
				if (newX < x) {
					x = newX;
				}
				this.points[i] = newX;
			} else {
				final float newY = ypoints[count];
				if (newY > maxY) {
					maxY = newY;
				}
				if (newY < minY) {
					minY = newY;
				}
				if (newY < y) {
					y = newY;
				}
				this.points[i] = newY;
				count++;
			}
		}
		findCenter();
		calculateRadius();
		pointsDirty = true;
		return this;
	}

	public Polygon setPolygon(float[] ps, int npoints) {
		if (ps == null) {
			throw new LSysException("points is null !");
		}
		if (npoints > ps.length) {
			throw new LSysException("npoints > points.length");
		}
		if (ps.length == 0) {
			throw new LSysException("points.length == 0");
		}
		if (this.points == null) {
			this.points = ps;
		}
		int size = ps.length;
		int length = this.points.length;
		if (size != length) {
			this.points = new float[size];
			length = ps.length;
		}
		this.maxX = -Float.MIN_VALUE;
		this.maxY = -Float.MIN_VALUE;
		this.minX = Float.MAX_VALUE;
		this.minY = Float.MAX_VALUE;
		this.x = Float.MAX_VALUE;
		this.y = Float.MAX_VALUE;
		for (int i = 0; i < length; i++) {
			if (i % 2 == 0) {
				float newX = ps[i];
				if (newX > maxX) {
					maxX = newX;
				}
				if (newX < minX) {
					minX = newX;
				}
				if (newX < x) {
					x = newX;
				}
				this.points[i] = newX;
			} else {
				float newY = ps[i];
				if (newY > maxY) {
					maxY = newY;
				}
				if (newY < minY) {
					minY = newY;
				}
				if (newY < y) {
					y = newY;
				}
				this.points[i] = newY;
			}
		}
		findCenter();
		calculateRadius();
		pointsDirty = true;
		return this;
	}

	public Polygon setAllowDuplicatePoints(boolean allowDups) {
		this._allowDups = allowDups;
		return this;
	}

	public Polygon addPoint(float x, float y) {
		if (hasVertex(x, y) && (!_allowDups)) {
			return this;
		}
		_tempPoints.clear();
		final int size = points.length;
		for (int i = 0; i < size; i++) {
			_tempPoints.add(points[i]);
		}
		_tempPoints.add(x + getX());
		_tempPoints.add(y + getY());
		int length = _tempPoints.size;
		this.points = new float[length];
		for (int i = 0; i < length; i++) {
			points[i] = _tempPoints.get(i);
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

	public float[] syncPoints(TArray<Vector2f> list) {
		return syncPoints(list, true);
	}

	public float[] syncPoints(TArray<Vector2f> list, boolean dirty) {
		final int size = list.size * 2;
		if (dirty || this.points == null || this.points.length != size) {
			this.points = new float[size];
		}
		for (int i = 0, j = 0; i < points.length; i += 2, j++) {
			Vector2f v = list.get(j);
			points[i] = v.x;
			points[i + 1] = v.y;
		}
		if (dirty) {
			this.pointsDirty = true;
			checkPoints();
		}
		return this.points;
	}

	public boolean query(QueryEvent<Vector2f> query) {
		final TArray<Vector2f> result = getVertices();
		final int len = result.size;
		for (int i = 0; i < len; i++) {
			Vector2f v = result.get(i);
			if (query.hit(v)) {
				return true;
			}
		}
		return false;
	}

	public Polygon update(QueryEvent<Vector2f> query) {
		final TArray<Vector2f> result = getVertices();
		final int len = result.size;
		int updated = 0;
		for (int i = 0; i < len; i++) {
			Vector2f v = result.get(i);
			if (query.hit(v)) {
				updated++;
			}
		}
		if (updated > 0) {
			syncPoints(result);
		}
		return this;
	}

	public Polygon rotate(float cx, float cy, float angle) {
		if (!MathUtils.equal(rotation, angle)) {
			this.rotation = angle;
			final TArray<Vector2f> result = getVertices();
			final int len = result.size;
			for (int i = 0; i < len; i++) {
				result.get(i).rotateSelf(cx, cy, angle);
			}
			syncPoints(result);
			this.pointsDirty = true;
		}
		return this;
	}

	public Polygon mul(float v) {
		final TArray<Vector2f> result = getVertices();
		final int len = result.size;
		for (int i = 0; i < len; i++) {
			result.get(i).mulSelf(v);
		}
		syncPoints(result);
		this.pointsDirty = true;
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
		resultPolygon._closed = _closed;

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

	@Override
	protected void createPoints() {

	}

	@Override
	public boolean closed() {
		return _closed;
	}

	public Polygon setClosed(boolean closed) {
		this._closed = closed;
		return this;
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

	public boolean isPolygonConvex() {
		checkPoints();
		int numCoords = points.length;
		if (numCoords < 6) {
			return true;
		} else {
			for (int i = 0; i < numCoords; i += 2) {
				if (!isConvexTriangle(points[i], points[i + 1], points[(i + 2) % numCoords],
						points[(i + 3) % numCoords], points[(i + 4) % numCoords], points[(i + 5) % numCoords])) {
					return false;
				}
				i += 2;
			}
		}
		return true;
	}

	public boolean isPolygonNotSelfIntersecting() {
		checkPoints();
		int numCoords = points.length;
		if (numCoords <= 6) {
			return true;
		}
		for (int i = 0; i < numCoords; i += 2) {
			float ax = points[i];
			float ay = points[i + 1];
			float bx = points[(i + 2) % numCoords];
			float by = points[(i + 3) % numCoords];
			float endJ = i + numCoords - 2;
			for (int j = i + 4; j < endJ; j += 2) {
				float cx = points[j % numCoords];
				float cy = points[(j + 1) % numCoords];
				float dx = points[(j + 2) % numCoords];
				float dy = points[(j + 3) % numCoords];
				if (isVectorsIntersecting(ax, ay, bx, by, cx, cy, dx, dy)) {
					return false;
				}
			}
		}
		return true;
	}

	public float getArea() {
		checkPoints();
		float area = 0f;
		int numCoords = points.length;
		if (numCoords >= 6) {
			for (int i = 0; i < numCoords; i += 2) {
				area += points[i] * points[(i + 3) % numCoords];
				area -= points[i + 1] * points[(i + 2) % numCoords];

			}
		}
		return area / 2f;
	}

	public Polygon getOffsetPolygon(float offset) {
		return new Polygon(offsetPolygon(getPoints(), offset));
	}

	public Vector2f distance(Vector2f point) {
		return distance(point, true);
	}

	public Vector2f distance(Vector2f point, boolean inside) {
		boolean outside = false;
		float minV = Float.MAX_VALUE;
		Vector2f minN = Vector2f.ZERO();
		final TArray<Vector2f> vertices = getVertices();
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			final Vector2f src = vertices.get(current);
			final Vector2f dst = vertices.get(next);
			final Vector2f edge = dst.sub(src);
			float len = edge.length();
			Vector2f normal = edge.left();
			edge.normalizeSelf();
			normal.normalizeSelf();
			Vector2f apos = point.sub(src);
			float dist = normal.dot(apos);
			float edist = edge.dot(apos);
			edge.normalizeSelf();
			if (MathUtils.abs(dist) < MathUtils.abs(minV) && edist > 0 && edist < len) {
				minV = dist;
				minN = normal;
				minN.mulSelf(dist);
			}
			if (dist > 0) {
				outside = true;
			}
		}
		for (int i = 0; i < vertices.size; i++) {
			Vector2f pos = vertices.get(i);
			float dist = point.dist(pos);
			if (outside && dist < MathUtils.abs(minV)) {
				minV = dist;
				minN = point.sub(pos);
				minN.normalizeSelf();
				minN.mulSelf(dist);
			}
		}
		if (inside) {
			if (!outside || minV < 0) {
				return minN;
			}
			return Vector2f.ZERO();
		} else if (!outside || minV < 0) {
			return Vector2f.ZERO();
		}
		return minN;
	}

	public float intersection(Vector2f point) {
		float minDist = Float.MAX_VALUE;
		final TArray<Vector2f> vertices = getVertices();
		for (int i = 1; i < vertices.size; i++) {
			Vector2f normal = vertices.get(i).sub(vertices.get(i - 1)).left();
			normal.normalizeSelf();
			Vector2f relPoint = point.sub(vertices.get(i - 1));
			float dist = normal.dot(relPoint);
			if (dist > 0) {
				return 1f;
			}
			if (dist < minDist) {
				minDist = dist;
			}
		}
		final Vector2f normal = vertices.get(0).sub(vertices.get(vertices.size - 1)).left();
		final Vector2f relPoint = point.sub(vertices.get(vertices.size - 1));
		float dist = normal.dot(relPoint);
		if (dist > 0) {
			return 1f;
		}
		if (dist < minDist) {
			minDist = dist;
		}
		return minDist;
	}

	public Vector2f intersection(Line line) {
		final Vector2f dir = line.getSubDirection();
		Vector2f point = null;
		Line edge = new Line();
		final TArray<Vector2f> vertices = getVertices();
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			final Vector2f src = vertices.get(current);
			final Vector2f dst = vertices.get(next);
			float min_dot = Float.POSITIVE_INFINITY;
			edge.set(src, dst);
			Intersection ins = edge.intersection(line);
			if (ins.intersected) {
				final float dot = ins.point.sub(line.getStart()).dot(dir);
				if (dot < min_dot) {
					min_dot = dot;
					point = ins.point;
				}
			}
		}
		return point;
	}

	@Override
	public float[] getCenter() {
		final TArray<Vector2f> v = getVertices();
		final int len = v.size;

		float cx = 0f;
		float cy = 0f;
		float ar = 0f;

		for (int i = 0; i < len; i++) {
			Vector2f p1 = v.get(i);
			Vector2f p2 = (i == len - 1) ? v.get(0) : v.get(i + 1);
			final float a = p1.x * p2.y - p2.x * p1.y;
			cx += (p1.x + p2.x) * a;
			cy += (p1.y + p2.y) * a;
			ar += a;
		}
		ar = ar * 3f;
		cx = cx / ar;
		cy = cy / ar;

		return new float[] { cx, cy };
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
