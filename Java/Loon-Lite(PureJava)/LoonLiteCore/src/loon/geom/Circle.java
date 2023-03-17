/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class Circle extends Ellipse {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static Circle at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Circle();
		}
		String[] result = StringUtils.split(v, ',');
		int len = result.length;
		if (len > 2) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				float r = Float.parseFloat(result[2].trim());
				return new Circle(x, y, r);
			} catch (Exception ex) {
			}
		}
		return new Circle();
	}

	public static Circle at(float centerPointX, float centerPointY, float r) {
		return new Circle(centerPointX, centerPointY, r);
	}

	public static Circle at(float centerPointX, float centerPointY, float w, float h) {
		float radius = MathUtils.max(w, h);
		return new Circle(centerPointX, centerPointY, radius);
	}

	/**
	 * 构建一个圆形
	 */
	public Circle() {
		this(0f, 0f, 0f);
	}

	/**
	 * 构建一个圆形
	 *
	 * @param x
	 * @param y
	 * @param boundingCircleRadius
	 */
	public Circle(float centerPointX, float centerPointY, float boundingCircleRadius) {
		this(centerPointX, centerPointY, boundingCircleRadius, DEFAULT_SEGMENT_MAX_COUNT);
	}

	/**
	 * 构建一个圆形
	 *
	 * @param x
	 * @param y
	 * @param boundingCircleRadius
	 * @param segment
	 */
	public Circle(float centerPointX, float centerPointY, float boundingCircleRadius, int segment) {
		super(centerPointX, centerPointY, boundingCircleRadius, boundingCircleRadius, segment);
		this.x = centerPointX;
		this.y = centerPointY;
		this.boundingCircleRadius = boundingCircleRadius;
		this.setLocation(x, y);
		this.checkPoints();
	}

	/**
	 * 返回当前圆形的中心X点
	 *
	 */
	@Override
	public float getCenterX() {
		return getX() + boundingCircleRadius;
	}

	/**
	 * 返回当前圆形的中心Y点
	 */
	@Override
	public float getCenterY() {
		return getY() + boundingCircleRadius;
	}

	/**
	 * 设定当前圆形半径
	 *
	 * @param boundingCircleRadius
	 */
	public void setRadius(float boundingCircleRadius) {
		if (boundingCircleRadius != this.boundingCircleRadius) {
			pointsDirty = true;
			this.boundingCircleRadius = boundingCircleRadius;
			setRadii(boundingCircleRadius, boundingCircleRadius);
		}
	}

	/**
	 * 返回当前圆形半径
	 *
	 * @return
	 */
	public float getRadius() {
		return boundingCircleRadius;
	}

	/**
	 * 检查当前圆形与指定形状是否相交
	 */
	@Override
	public boolean intersects(Shape shape) {
		if (shape instanceof Circle) {
			return collideCircle((Circle) shape);
		} else if (shape instanceof RectBox) {
			return intersects((RectBox) shape);
		} else {
			return super.intersects(shape);
		}
	}

	public boolean intersects(RectBox other) {
		RectBox box = other;
		if (box.contains(x + boundingCircleRadius, y + boundingCircleRadius)) {
			return true;
		}
		return collideBounds(other);
	}

	/**
	 * 检查当前圆形是否包含指定点
	 *
	 * @param xy
	 * @return
	 */
	@Override
	public boolean contains(XY xy) {
		if (xy == null) {
			return false;
		}
		return contains(xy.getX(), xy.getY());
	}

	/**
	 * 检查当前圆形是否包含指定直线
	 *
	 * @param line
	 * @return
	 */
	public boolean contains(Line line) {
		if (line == null) {
			return false;
		}
		return contains(line.getX1(), line.getY1()) && contains(line.getX2(), line.getY2());
	}

	@Override
	protected void findCenter() {
		center = new float[2];
		center[0] = x + boundingCircleRadius;
		center[1] = y + boundingCircleRadius;
	}

	public float side(Vector2f v) {
		if (v == null) {
			return 0f;
		}
		return side(v.x, v.y);
	}

	public float side(float px, float py) {
		float dx = px - x;
		float dy = py - y;
		return boundingCircleRadius * boundingCircleRadius - (dx * dx + dy * dy);
	}

	public boolean collideCircle(Circle c) {
		float dx = x - c.x;
		float dy = y - c.y;
		return dx * dx + dy * dy < (boundingCircleRadius + c.boundingCircleRadius)
				* (boundingCircleRadius + c.boundingCircleRadius);
	}

	public boolean collideBounds(RectBox size) {
		float radiusDouble = boundingCircleRadius * boundingCircleRadius;
		if ((x < size.getX() - boundingCircleRadius) || (x > size.getBottom() + boundingCircleRadius) || (y < size.getY() - boundingCircleRadius) || (y > size.getBottom() + boundingCircleRadius)) {
			return false;
		}
		if (x < size.getX() && y < size.getY() && MathUtils.distance(x - size.getX(), y - size.getY()) > radiusDouble) {
			return false;
		}
		if (x > size.getRight() && y < size.getY()
				&& MathUtils.distance(x - size.getRight(), y - size.getY()) > radiusDouble) {
			return false;
		}
		if (x < size.getX() && y > size.getBottom()
				&& MathUtils.distance(x - size.getX(), y - size.getBottom()) > radiusDouble) {
			return false;
		}
		if (x > size.getRight() && y > size.getBottom()
				&& MathUtils.distance(x - size.getRight(), y - size.getBottom()) > radiusDouble) {
			return false;
		}
		return true;
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
		boolean intersects = circleCenter.sub(closest).lengthSquared() <= getRadius() * getRadius();
		return intersects;
	}

	@Override
	public boolean contains(Shape other) {
		if (other instanceof Circle) {
			return contains((Circle) other);
		}
		return super.contains(other);
	}

	public float getLeft() {
		return this.x - this.boundingCircleRadius;
	}

	public float getRight() {
		return this.x + this.boundingCircleRadius;
	}

	public float getTop() {
		return this.y - this.boundingCircleRadius;
	}

	public float getBottom() {
		return this.y + this.boundingCircleRadius;
	}

	public boolean contains(Circle c) {
		final float radiusDiff = boundingCircleRadius - c.boundingCircleRadius;
		if (radiusDiff < 0f) {
			return false;
		}
		final float dx = x - c.x;
		final float dy = y - c.y;
		final float dst = dx * dx + dy * dy;
		final float radiusSum = boundingCircleRadius + c.boundingCircleRadius;
		return (!(radiusDiff * radiusDiff < dst) && (dst < radiusSum * radiusSum));
	}

	public float distanceTo(XY target) {
		return distanceTo(target, false);
	}

	public float distanceTo(XY target, boolean round) {
		if (target == null) {
			return 0f;
		}
		float dx = this.x - target.getX();
		float dy = this.y - target.getY();
		if (round) {
			return MathUtils.round(MathUtils.sqrt(dx * dx + dy * dy));
		} else {
			return MathUtils.sqrt(dx * dx + dy * dy);
		}
	}

	public float circumferenceFloat() {
		return 2f * (MathUtils.PI * this.boundingCircleRadius);
	}

	public Vector2f circumferencePoint(float angle, boolean asDegrees) {
		return circumferencePoint(angle, asDegrees, null);
	}

	public Vector2f circumferencePoint(float angle, boolean asDegrees, Vector2f output) {
		if (asDegrees) {
			angle = MathUtils.toDegrees(angle);
		}
		if (output == null) {
			output = new Vector2f();
		}
		output.x = this.x + this.boundingCircleRadius * MathUtils.cos(angle);
		output.y = this.y + this.boundingCircleRadius * MathUtils.sin(angle);
		return output;
	}

	public float area() {
		return (this.boundingCircleRadius > 0) ? MathUtils.PI * this.boundingCircleRadius * this.boundingCircleRadius
				: 0f;
	}

	public boolean equals(Circle other) {
		if (other == null) {
			return false;
		}
		if ((other == this) || (this.x == other.x && this.y == other.y && this.boundingCircleRadius == other.boundingCircleRadius)) {
			return true;
		}
		return false;
	}
}
