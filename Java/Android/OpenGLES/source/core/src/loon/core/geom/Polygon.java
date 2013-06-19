package loon.core.geom;

import java.util.ArrayList;

import loon.physics.PPolygon;
import loon.utils.CollectionUtils;

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
public class Polygon extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Polygon2i {

		public int npoints;

		public int[] xpoints;

		public int[] ypoints;

		private static final int MIN_LENGTH = 4;

		public Polygon2i() {
			xpoints = new int[MIN_LENGTH];
			ypoints = new int[MIN_LENGTH];
		}

		public Polygon2i(int xpoints[], int ypoints[], int npoints) {
			if (npoints > xpoints.length || npoints > ypoints.length) {
				throw new IndexOutOfBoundsException(
						"npoints > xpoints.length || "
								+ "npoints > ypoints.length");
			}
			if (npoints < 0) {
				throw new NegativeArraySizeException("npoints < 0");
			}
			this.npoints = npoints;
			this.xpoints = CollectionUtils.copyOf(xpoints, npoints);
			this.ypoints = CollectionUtils.copyOf(ypoints, npoints);
		}

		public void addPoint(int x, int y) {
			if (npoints >= xpoints.length || npoints >= ypoints.length) {
				int newLength = (npoints * 2);
				if (newLength < MIN_LENGTH) {
					newLength = MIN_LENGTH;
				} else if ((newLength & (newLength - 1)) != 0) {
					newLength = Integer.highestOneBit(newLength);
				}
				xpoints = CollectionUtils.copyOf(xpoints, newLength);
				ypoints = CollectionUtils.copyOf(ypoints, newLength);
			}
			xpoints[npoints] = x;
			ypoints[npoints] = y;
			npoints++;
		}

		public int[] getVertices() {
			int vertice_size = xpoints.length * 2;
			int[] verts = new int[vertice_size];
			for (int i = 0, j = 0; i < vertice_size; i += 2, j++) {
				verts[i] = xpoints[j];
				verts[i + 1] = ypoints[j];
			}
			return verts;
		}

		public void reset() {
			npoints = 0;
			xpoints = new int[MIN_LENGTH];
			ypoints = new int[MIN_LENGTH];
		}
	}

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
			throw new IndexOutOfBoundsException("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new NegativeArraySizeException("npoints < 0");
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
			throw new IndexOutOfBoundsException("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}
		if (npoints < 0) {
			throw new NegativeArraySizeException("npoints < 0");
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

	public PPolygon getPPolygon(float scale) {
		return new PPolygon(points,scale);
	}

	public void setAllowDuplicatePoints(boolean allowDups) {
		this.allowDups = allowDups;
	}

	public void addPoint(float x, float y) {
		if (hasVertex(x, y) && (!allowDups)) {
			return;
		}
		ArrayList<Float> tempPoints = new ArrayList<Float>();
		for (int i = 0; i < points.length; i++) {
			tempPoints.add(points[i]);
		}
		tempPoints.add(x);
		tempPoints.add(y);
		int length = tempPoints.size();
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

	@Override
	public Shape transform(Matrix transform) {
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

	public Polygon copy() {
		float[] copyPoints = new float[points.length];
		System.arraycopy(points, 0, copyPoints, 0, copyPoints.length);
		return new Polygon(copyPoints);
	}
}
