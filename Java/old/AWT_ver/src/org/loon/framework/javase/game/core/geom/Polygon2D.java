package org.loon.framework.javase.game.core.geom;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Polygon2D {
	
	public static class Point2D {
		
		public float x;

		public float y;

		public int type;

		public static final int POINT_CONVEX = 1;

		public static final int POINT_CONCAVE = 2;

		public Point2D(float x, float y) {

			this.x = x;
			this.y = y;
		}

		public boolean equals(Object obj) {

			Point2D p = (Point2D) obj;
			if (p.x == this.x && p.y == this.y && p.type == this.type) {

				return true;
			} else {
				return false;
			}

		}

		public String toString() {

			return "(" + this.x + ", " + this.y + ")";
		}

	}

	private LinkedList<Point2D> points = new LinkedList<Point2D>();

	public Polygon2D(Point2D[] pts) {
		if (polygonClock(pts)) {
			for (int i = 0; i < pts.length; i++) {
				this.points.add(pts[i]);
			}
		} else {
			for (int i = pts.length - 1; i >= 0; i--) {
				this.points.add(pts[i]);
			}
		}
	}

	public Triangle2D[] getTriangles() {

		float x1;
		float y1;
		float x2;
		float y2;
		float x3;
		float y3;

		LinkedList<Triangle2D> triangles = new LinkedList<Triangle2D>();

		boolean finish = false;

		if (this.points.size() == 3) {

			x1 = points.get(0).x;
			y1 = points.get(0).y;
			x2 = points.get(1).x;
			y2 = points.get(1).y;
			x3 = points.get(2).x;
			y3 = points.get(2).y;

			triangles.add(new Triangle2D(x1, y1, x2, y2, x3, y3));

			finish = true;
		}
		for (; !finish;) {

			int pointIndex = 0;
			boolean earFound = false;
			this.matchPoints();

			while (pointIndex < this.points.size()) {
				if (isEar(this.points.get(pointIndex), pointIndex)) {
					earFound = true;
					break;
				}
				pointIndex++;
			}
			if (!earFound) {
				throw new RuntimeException("Cannot triangle polygon !");
			}

			Point2D p0 = this.getPreviousPoint(pointIndex);
			Point2D p1 = this.points.get(pointIndex);
			Point2D p2 = this.getNextPoint(pointIndex);

			x1 = p0.x;
			y1 = p0.y;
			x2 = p1.x;
			y2 = p1.y;
			x3 = p2.x;
			y3 = p2.y;

			triangles.add(new Triangle2D(x1, y1, x2, y2, x3, y3));

			this.points.remove(pointIndex);

			if (this.points.size() == 3) {

				x1 = points.get(0).x;
				y1 = points.get(0).y;
				x2 = points.get(1).x;
				y2 = points.get(1).y;
				x3 = points.get(2).x;
				y3 = points.get(2).y;

				triangles.add(new Triangle2D(x1, y1, x2, y2, x3, y3));

				finish = true;
			}
		}
		return triangles.toArray(new Triangle2D[0]);
	}

	private boolean isEar(Point2D point, int index) {

		if (point.type == Point2D.POINT_CONCAVE) {
			return false;
		}

		Point2D p0 = this.getPreviousPoint(index);
		Point2D p1 = this.points.get(index);
		Point2D p2 = this.getNextPoint(index);

		int numPoints = this.points.size();
		for (int i = 0; i < numPoints; i++) {

			Point2D currPoint = this.points.get(i);

			if (currPoint.equals(p0) || currPoint.equals(p1)
					|| currPoint.equals(p2)) {
				continue;
			}

			if (currPoint.type == Point2D.POINT_CONCAVE) {
				if (this.isSide(currPoint, p0, p1, p2)
						&& this.isSide(currPoint, p1, p0, p2)
						&& this.isSide(currPoint, p2, p0, p1)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isSide(Point2D p1, Point2D p2, Point2D a, Point2D b) {

		double dotProduct = 0.0;
		double crossProduct1 = 0.0;
		double crossProduct2 = 0.0;
		double a1, a2, b1, b2;

		a1 = (b.x - a.x);
		a2 = (b.y - a.y);
		b1 = (p1.x - a.x);
		b2 = (p1.y - a.y);
		crossProduct1 = a1 * b2 - a2 * b1;

		a1 = (b.x - a.x);
		a2 = (b.y - a.y);
		b1 = (p2.x - a.x);
		b2 = (p2.y - a.y);
		crossProduct2 = a1 * b2 - a2 * b1;

		dotProduct = crossProduct1 * crossProduct2;

		if (dotProduct >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public void matchPoints() {

		int numPoints = this.points.size();
		for (int i = 0; i < numPoints; i++) {

			Point2D p1 = this.getPreviousPoint(i);
			Point2D p2 = this.points.get(i);
			Point2D p3 = this.getNextPoint(i);

			if (!convex(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)) {
				this.points.get(i).type = Point2D.POINT_CONVEX;
			} else {
				this.points.get(i).type = Point2D.POINT_CONCAVE;
			}
		}
	}

	private boolean convex(double x1, double y1, double x2, double y2,
			double x3, double y3) {
		if (triangleArea(x1, y1, x2, y2, x3, y3) < 0) {
			return true;
		} else {
			return false;
		}
	}

	private double triangleArea(double x1, double y1, double x2, double y2,
			double x3, double y3) {
		double areaSum = 0;
		areaSum += x1 * (y3 - y2);
		areaSum += x2 * (y1 - y3);
		areaSum += x3 * (y2 - y1);
		return areaSum / 2;
	}

	private boolean polygonClock(Point2D[] pts) {

		int numPoints = pts.length;

		if (numPoints < 3) {
			throw new RuntimeException("Less than three points !");
		}
		Point2D minPoint = pts[0];
		int minIndex = 0;

		for (int i = 1; i < numPoints; i++) {

			Point2D curr = pts[i];

			if (curr.x > minPoint.x) {

				minPoint = curr;
				minIndex = i;
			} else if (curr.x == minPoint.x && curr.y < minPoint.y) {

				minPoint = curr;
				minIndex = i;
			}
		}

		Point2D p1 = null, p2 = null, p3 = null;

		if (minIndex == 0) {
			p1 = pts[numPoints - 1];
			p2 = pts[0];
			p3 = pts[1];
		} else if (minIndex == numPoints - 1) {
			p1 = pts[numPoints - 2];
			p2 = pts[numPoints - 1];
			p3 = pts[0];

		} else if (minIndex > 0 && minIndex < numPoints - 1) {
			p1 = pts[minIndex - 1];
			p2 = pts[minIndex];
			p3 = pts[minIndex + 1];
		}

		double crossProduct = (p2.x - p1.x) * (p3.y - p2.y);
		crossProduct = crossProduct - ((p2.y - p1.y) * (p3.x - p2.x));

		if (crossProduct < 0) {
			return true;
		} else {
			return false;
		}
	}

	private Point2D getNextPoint(int index) {
		if (index != this.points.size() - 1) {
			return this.points.get(index + 1);
		} else {
			return this.points.get(0);
		}
	}

	private Point2D getPreviousPoint(int index) {
		if (index != 0) {
			return this.points.get(index - 1);
		} else {
			return this.points.get(this.points.size() - 1);
		}
	}

	public Point2D findCentroid() {
		float cx = 0.0f, cy = 0.0f, f = 0;
		Iterator<Point2D> it = this.points.iterator();
		while (it.hasNext()) {
			Point2D p1 = it.next();
			if (!it.hasNext()) {
				break;
			}
			Point2D p2 = it.next();
			f = p1.x * p2.y - p2.x * p1.y;
			cx += (p1.x + p2.y) * f;
			cy += (p1.y + p2.y) * f;
		}
		cx = 1 / (6 * Math.abs(signedArea())) * cx;
		cy = 1 / (6 * Math.abs(signedArea())) * cy;
		return new Point2D(cx, cy);
	}

	private float signedArea() {
		float sum = 0.0f;
		Iterator<Point2D> it = this.points.iterator();
		while (it.hasNext()) {
			Point2D p1 = it.next();
			if (!it.hasNext()) {
				break;
			}
			Point2D p2 = it.next();
			sum += (p1.x * p2.y - p2.x * p1.y);
		}
		return 0.5f * sum;
	}

}
