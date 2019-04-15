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
import loon.physics.PPolygon;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Polygon extends Shape implements BoxSize {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Polygon2i {}

	private boolean allowDups = false;

	private boolean closed = true;

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
			throw LSystem.runThrow("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw LSystem.runThrow("npoints < 0");
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
			throw LSystem.runThrow("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw LSystem.runThrow("npoints < 0");
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

	public void setAllowDuplicatePoints(boolean allowDups) {
		this.allowDups = allowDups;
	}

	public void addPoint(float x, float y) {
		if (hasVertex(x, y) && (!allowDups)) {
			return;
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
	}

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

	public void addVertex(float x, float y) {
		addPoint(x, y);
	}

	public void addVertex(Vector2f v) {
		addVertex(v.x, v.y);
	}

	public TArray<Vector2f> getVertices() {
		int size = points.length;
		TArray<Vector2f> vertices = new TArray<Vector2f>();
		for (int i = 0; i < size; i += 2) {
			vertices.add(new Vector2f(points[i], points[i + 1]));
		}
		return vertices;
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

	public PPolygon getPPolygon(float scale) {
		return new PPolygon(points, scale);
	}

	public Polygon cpy() {
		float[] copyPoints = new float[points.length];
		System.arraycopy(points, 0, copyPoints, 0, copyPoints.length);
		return new Polygon(copyPoints);
	}

	@Override
	public void setWidth(float w) {
        this.maxX = w;
	}

	@Override
	public void setHeight(float h) {
	    this.maxY = h; 
	}
	
	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Polygon");
		builder.kv("points", "[" + StringUtils.join(',', points) + "]")
		.comma()
		.kv("center", "[" + StringUtils.join(',', center) + "]")
		.comma()
		.kv("rotation", rotation)
		.comma()
		.kv("minX", minX)
		.comma()
		.kv("minY", minY)
		.comma()
		.kv("maxX", maxX)
		.comma()
		.kv("maxY", maxY);
		return builder.toString();
	}
}
