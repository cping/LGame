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

import loon.utils.MathUtils;

public class Point extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int clazz;

	public static final int POINT_CONVEX = 1;

	public static final int POINT_CONCAVE = 2;

	public Point(float x, float y) {
		this.checkPoints();
		this.setLocation(x, y);
	}

	public Point(Point p) {
		this.checkPoints();
		this.setLocation(p);
	}

	public Shape transform(Matrix3 transform) {
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		return new Point(points[0], points[1]);
	}

	protected void createPoints() {
		if (points == null) {
			points = new float[2];
		}
		points[0] = getX();
		points[1] = getY();

		maxX = x;
		maxY = y;
		minX = x;
		minY = y;

		findCenter();
		calculateRadius();
	}

	protected void findCenter() {
		if (center == null) {
			center = new float[2];
		}
		center[0] = points[0];
		center[1] = points[1];
	}

	protected void calculateRadius() {
		boundingCircleRadius = 0;
	}

	public final void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setLocation(Point p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	public void translate(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

	public void translate(Point p) {
		this.x += p.x;
		this.y += p.y;
	}

	public void untranslate(Point p) {
		this.x -= p.x;
		this.y -= p.y;
	}

	public final int distanceTo(Point p) {
		final int tx = (int) (this.x - p.x);
		final int ty = (int) (this.y - p.y);
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(int x, int y) {
		final int tx = (int) (this.x - x);
		final int ty = (int) (this.y - y);
		return MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public void getLocation(Point dest) {
		dest.setLocation(this.x, this.y);
	}

	public int hashCode() {
		return super.hashCode();
	}

	public boolean equals(Object obj) {
		Point p = (Point) obj;
		return p.x == this.x && p.y == this.y && p.clazz == this.clazz;
	}
}
