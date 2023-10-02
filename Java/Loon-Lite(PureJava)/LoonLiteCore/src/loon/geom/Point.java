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
import loon.action.collision.CollisionHelper;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;

public class Point extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Point at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Point();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 1) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				return new Point(x, y);
			} catch (Exception ex) {
			}
		}
		return new Point();
	}

	public final static Point at(float x, float y) {
		return new Point(x, y);
	}

	public static final int POINT_CONVEX = 1;

	public static final int POINT_CONCAVE = 2;

	public Point() {
		this(0f, 0f);
	}

	public Point(float x, float y) {
		this.checkPoints();
		this.setLocation(x, y);
	}

	public Point(Point p) {
		this.checkPoints();
		this.setLocation(p);
	}

	@Override
	public boolean contains(XY xy) {
		if (xy == null) {
			return false;
		}
		return contains(xy.getX(), xy.getY());
	}

	@Override
	public boolean contains(float px, float py) {
		return CollisionHelper.checkPointvsAABB(px, py, x, y, 1f, 1f);
	}

	@Override
	public Shape transform(Matrix3 transform) {
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		return new Point(points[0], points[1]);
	}

	@Override
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

	@Override
	protected void findCenter() {
		if (center == null) {
			center = new float[2];
		}
		center[0] = points[0];
		center[1] = points[1];
	}

	@Override
	protected void calculateRadius() {
		boundingCircleRadius = 0;
	}

	public Point set(float x, float y) {
		this.x = x;
		this.y = y;
		this.pointsDirty = true;
		return this;
	}

	@Override
	public Point setLocation(float x, float y) {
		this.x = x;
		this.y = y;
		this.pointsDirty = true;
		return this;
	}

	public Point setLocation(Point p) {
		this.x = p.getX();
		this.y = p.getY();
		this.pointsDirty = true;
		return this;
	}

	@Override
	public Point translate(float dx, float dy) {
		this.x += dx;
		this.y += dy;
		this.pointsDirty = true;
		return this;
	}

	public Point translate(Point p) {
		this.x += p.x;
		this.y += p.y;
		this.pointsDirty = true;
		return this;
	}

	public Point untranslate(Point p) {
		this.x -= p.x;
		this.y -= p.y;
		this.pointsDirty = true;
		return this;
	}

	public final int distanceTo(Point p) {
		final float tx = (this.x - p.x);
		final float ty = (this.y - p.y);
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public final int distanceTo(int x, int y) {
		final float tx = (int) (this.x - x);
		final float ty = (int) (this.y - y);
		return (int) MathUtils.sqrt(MathUtils.mul(tx, tx) + MathUtils.mul(ty, ty));
	}

	public void getLocation(Point dest) {
		dest.setLocation(this.x, this.y);
	}

	public Point random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.pointsDirty = true;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Point p = (Point) obj;
		return p.x == this.x && p.y == this.y;
	}

	@Override
	public final String toString() {
		return "(" + x + "," + y + ")";
	}

}
