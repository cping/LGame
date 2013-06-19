package loon.core.geom;

import loon.action.collision.CollisionHelper;

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
public class Circle extends Ellipse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public float radius;

	/**
	 * 构建一个圆形
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 */
	public Circle(float x, float y, float radius) {
		this(x, y, radius, DEFAULT_SEGMENT_MAX_COUNT);
	}

	/**
	 * 构建一个圆形
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @param segment
	 */
	public Circle(float x, float y, float radius, int segment) {
		super(x, y, radius, radius, segment);
		this.x = x - radius;
		this.y = y - radius;
		this.radius = radius;
		this.boundingCircleRadius = radius;
	}

	/**
	 * 返回当前圆形的中心X点
	 * 
	 */
	@Override
	public float getCenterX() {
		return getX() + radius;
	}

	/**
	 * 返回当前圆形的中心Y点
	 */
	@Override
	public float getCenterY() {
		return getY() + radius;
	}

	/**
	 * 设定当前圆形半径
	 * 
	 * @param radius
	 */
	public void setRadius(float radius) {
		if (radius != this.radius) {
			pointsDirty = true;
			this.radius = radius;
			setRadii(radius, radius);
		}
	}

	/**
	 * 返回当前圆形半径
	 * 
	 * @return
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * 检查当前圆形与指定形状是否相交
	 */
	@Override
	public boolean intersects(Shape shape) {
		if (shape instanceof Circle) {
			Circle other = (Circle) shape;
			float totalRad2 = getRadius() + other.getRadius();

			if (Math.abs(other.getCenterX() - getCenterX()) > totalRad2) {
				return false;
			}
			if (Math.abs(other.getCenterY() - getCenterY()) > totalRad2) {
				return false;
			}

			totalRad2 *= totalRad2;

			float dx = Math.abs(other.getCenterX() - getCenterX());
			float dy = Math.abs(other.getCenterY() - getCenterY());

			return totalRad2 >= ((dx * dx) + (dy * dy));
		} else if (shape instanceof RectBox) {
			return intersects((RectBox) shape);
		} else {
			return super.intersects(shape);
		}
	}

	/**
	 * 检查当前圆形是否包含指定直线
	 * 
	 * @param line
	 * @return
	 */
	public boolean contains(Line line) {
		return contains(line.getX1(), line.getY1())
				&& contains(line.getX2(), line.getY2());
	}

	@Override
	protected void findCenter() {
		center = new float[2];
		center[0] = x + radius;
		center[1] = y + radius;
	}

	@Override
	protected void calculateRadius() {
		boundingCircleRadius = radius;
	}

	@Override
	public boolean contains(float x0, float y0) {
		return CollisionHelper.isCollision(x + radius, y + radius, radius, x0,
				y0, 0);
	}

	private boolean intersects(RectBox other) {
		RectBox box = other;
		Circle circle = this;

		if (box.contains(x + radius, y + radius)) {
			return true;
		}

		float x1 = box.getX();
		float y1 = box.getY();
		float x2 = box.getX() + box.getWidth();
		float y2 = box.getY() + box.getHeight();

		Line[] lines = new Line[4];
		lines[0] = new Line(x1, y1, x2, y1);
		lines[1] = new Line(x2, y1, x2, y2);
		lines[2] = new Line(x2, y2, x1, y2);
		lines[3] = new Line(x1, y2, x1, y1);

		float r2 = circle.getRadius() * circle.getRadius();

		Vector2f pos = new Vector2f(circle.getCenterX(), circle.getCenterY());

		for (int i = 0; i < 4; i++) {
			float dis = lines[i].distanceSquared(pos);
			if (dis < r2) {
				return true;
			}
		}

		return false;
	}

	public boolean intersects(Line other) {
		Vector2f lineSegmentStart = new Vector2f(other.getX1(), other.getY1());
		Vector2f lineSegmentEnd = new Vector2f(other.getX2(), other.getY2());
		Vector2f circleCenter = new Vector2f(getCenterX(), getCenterY());
		Vector2f closest;
		Vector2f segv = lineSegmentEnd.sub(lineSegmentStart);
		Vector2f ptv = circleCenter.sub(lineSegmentStart);
		float segvLength = segv.len();
		float projvl = ptv.dot(segv) / segvLength;
		if (projvl < 0) {
			closest = lineSegmentStart;
		} else if (projvl > segvLength) {
			closest = lineSegmentEnd;
		} else {
			Vector2f projv = segv.mul(projvl / segvLength);
			closest = lineSegmentStart.add(projv);
		}
		boolean intersects = circleCenter.sub(closest).lengthSquared() <= getRadius()
				* getRadius();
		return intersects;
	}
}
