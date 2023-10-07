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

import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class Circle extends Ellipse {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构建一个圆形,以中心圆点x与坐标y开始构建,radius为半径
	 * 
	 * @param centerX
	 * @param centerY
	 * @param radius1
	 * @param radius2
	 * @return
	 */
	public static Circle oval(float centerX, float centerY, float radius) {
		return new Circle(centerX, centerY, radius);
	}

	/**
	 * 构建一个圆形,以矩形坐标为基础构建,以x为左上角起始点,以y为右上角起始点,width与height为最大直径
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Circle rect(float x, float y, float width, float height) {
		final float radius = MathUtils.min(width / 2f, height / 2f);
		return new Circle(x + radius, y + radius, radius);
	}

	public static SetXY getRandom(Circle c, SetXY out) {
		if (out == null) {
			out = new PointF();
		}

		float t = MathUtils.random() * MathUtils.TWO_PI;
		float u = MathUtils.random() + MathUtils.random();
		float r = (u > 1) ? 2 - u : u;
		float x = r * MathUtils.cos(t);
		float y = r * MathUtils.sin(t);

		out.setX(c.getRealX() + (x * c.getRadius()));
		out.setY(c.getRealY() + (y * c.getRadius()));

		return out;
	}

	public static Circle at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Circle();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
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
		float radius = MathUtils.min(w, h);
		return new Circle(centerPointX, centerPointY, radius / 2f);
	}

	/**
	 * 构建一个圆形
	 */
	private Circle() {
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
		this.boundingCircleRadius = boundingCircleRadius;
		this.setLocation(x, y);
		this.checkPoints();
	}

	/**
	 * 设定当前圆形半径
	 * 
	 * @param boundingCircleRadius
	 */
	public Circle setRadius(float boundingCircleRadius) {
		if (boundingCircleRadius != this.boundingCircleRadius) {
			pointsDirty = true;
			this.boundingCircleRadius = boundingCircleRadius;
			setRadii(boundingCircleRadius, boundingCircleRadius);
		}
		return this;
	}

	/**
	 * 返回当前圆形半径
	 * 
	 * @return
	 */
	public float getRadius() {
		return boundingCircleRadius;
	}

	public boolean intersects(RectBox other) {
		return inRect(other);
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

	public boolean containCircle(Circle c) {
		float dx = x - c.x;
		float dy = y - c.y;
		return dx * dx + dy * dy < (boundingCircleRadius + c.boundingCircleRadius)
				* (boundingCircleRadius + c.boundingCircleRadius);
	}

	public boolean containBounds(RectBox size) {
		float radiusDouble = boundingCircleRadius * boundingCircleRadius;
		if (x < size.getX() - boundingCircleRadius) {
			return false;
		}
		if (x > size.getBottom() + boundingCircleRadius) {
			return false;
		}
		if (y < size.getY() - boundingCircleRadius) {
			return false;
		}
		if (y > size.getBottom() + boundingCircleRadius) {
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

	@Override
	public boolean inEllipse(Ellipse e) {
		return CollisionHelper.checkEllipsevsCircle(e.getRealX(), e.getRealY(), e.getRadius1(), e.getRadius2(),
				this.getRealX(), this.getRealY(), this.getDiameter());
	}

	@Override
	public boolean inLine(Line other) {
		return CollisionHelper.checkLinevsCircle(other.getX1(), other.getY1(), other.getX2(), other.getY2(),
				this.getRealX(), this.getRealY(), this.getDiameter());

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

	@Override
	public boolean contains(float x, float y) {
		if (this.boundingCircleRadius <= 0f) {
			return false;
		}
		final float r2 = this.boundingCircleRadius * this.boundingCircleRadius;
		float dx = (this.x - x);
		float dy = (this.y - y);
		dx *= dx;
		dy *= dy;
		return (dx + dy <= r2);
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

	public boolean overlaps(Circle c) {
		return contains(c);
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
	public boolean contains(Shape other) {
		if (other instanceof Circle) {
			return contains((Circle) other);
		}
		return super.contains(other);
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

	@Override
	public void setScale(float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(float sx, float sy) {
		if (scaleX != sx || scaleY != sy) {
			this.scaleX = sx;
			this.scaleY = sy;
			this.boundingCircleRadius = MathUtils.max(sx, sy) * boundingCircleRadius;
			this.setRadius1(boundingCircleRadius);
			this.setRadius2(boundingCircleRadius);
			this.pointsDirty = true;
		}
	}

	public float circumferenceFloat() {
		return MathUtils.TWO_PI * this.boundingCircleRadius;
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

	public Vector2f getPointAround(float angle) {
		final float nx = (MathUtils.cos(angle) * boundingCircleRadius) + this.x;
		final float ny = (MathUtils.sin(angle) * boundingCircleRadius) + this.y;
		return new Vector2f(nx, ny);
	}

	public boolean equals(Circle other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (this.x == other.x && this.y == other.y && this.boundingCircleRadius == other.boundingCircleRadius) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Circle) {
			return equals((Circle) obj);
		}
		return false;
	}

	public Circle copy(Circle e) {
		if (e == null) {
			return this;
		}
		if (equals(e)) {
			return this;
		}
		this.x = e.x;
		this.y = e.y;
		this.rotation = e.rotation;
		this.boundingCircleRadius = e.boundingCircleRadius;
		this.minX = e.minX;
		this.minY = e.minY;
		this.maxX = e.maxX;
		this.maxY = e.maxY;
		this.scaleX = e.scaleX;
		this.scaleY = e.scaleY;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	@Override
	public Circle copy(Shape e) {
		if (e instanceof Circle) {
			copy((Circle) e);
		} else {
			super.copy(e);
		}
		return this;
	}

	@Override
	public Circle cpy() {
		return new Circle(getCenterX(), getCenterY(), this.boundingCircleRadius);
	}

	@Override
	public int hashCode() {
		final int prime = 41;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, boundingCircleRadius);
		return hashCode;
	}

}
