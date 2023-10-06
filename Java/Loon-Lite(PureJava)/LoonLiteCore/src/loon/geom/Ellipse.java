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
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Ellipse extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static SetXY getRandom(Ellipse e, SetXY out) {
		if (out == null) {
			out = new PointF();
		}

		float p = MathUtils.random() * MathUtils.TWO_PI;
		float s = MathUtils.sqrt(MathUtils.random());

		out.setX(e.getRealX() + ((s * MathUtils.cos(p)) * e.getRadius1() / 2));
		out.setY(e.getRealY() + ((s * MathUtils.sin(p)) * e.getRadius2() / 2));

		return out;
	}

	public static Ellipse at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new Ellipse();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 3) {
			try {
				float cx = Float.parseFloat(result[0].trim());
				float cy = Float.parseFloat(result[1].trim());
				float r1 = Float.parseFloat(result[2].trim());
				float r2 = Float.parseFloat(result[2].trim());
				return new Ellipse(cx, cy, r1, r2);
			} catch (Exception ex) {
			}
		}
		return new Ellipse();
	}

	/**
	 * 构建一个椭圆形,以中心圆点x与坐标y开始构建,radius1与radius2为半径
	 * 
	 * @param centerX
	 * @param centerY
	 * @param radius1
	 * @param radius2
	 * @return
	 */
	public static Ellipse oval(float centerX, float centerY, float radius1, float radius2) {
		return new Ellipse(centerX, centerY, radius1, radius2);
	}

	/**
	 * 构建一个椭圆形,以矩形坐标为基础构建,以x为左上角起始点,以y为右上角起始点,width与height为最大直径
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Ellipse rect(float x, float y, float width, float height) {
		final float r1 = width / 2f;
		final float r2 = height / 2f;
		return new Ellipse(x + r1, y + r2, r1, r2);
	}

	protected static final int DEFAULT_SEGMENT_MAX_COUNT = 50;

	private int segmentCount;

	private float radius1;

	private float radius2;

	private float _start = 0;

	private float _end = 359;

	/**
	 * 构建一个椭圆
	 */
	private Ellipse() {
		this(0f, 0f, 0f, 0f);
	}

	/**
	 * loon的ellipse默认使用中心原点与半径模式构建,以矩形为基础构建请使用ellipse.rect函数
	 * 
	 * @param centerPointX
	 * @param centerPointY
	 * @param radius1
	 * @param radius2
	 */
	public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2) {
		this.set(centerPointX, centerPointY, radius1, radius2);
	}

	/**
	 * loon的ellipse默认使用中心原点与半径模式构建,以矩形为基础构建请使用ellipse.rect函数
	 * 
	 * @param centerPointX
	 * @param centerPointY
	 * @param radius1
	 * @param radius2
	 * @param segmentCount
	 */
	public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount) {
		this.set(centerPointX, centerPointY, radius1, radius2, segmentCount);
	}

	/**
	 * loon的ellipse默认使用中心原点与半径模式构建,以矩形为基础构建请使用ellipse.rect函数
	 * 
	 * @param centerPointX
	 * @param centerPointY
	 * @param radius1
	 * @param radius2
	 * @param start
	 * @param end
	 * @param segmentCount
	 */
	public Ellipse(float centerPointX, float centerPointY, float radius1, float radius2, float start, float end,
			int segmentCount) {
		_start = start;
		_end = end;
		set(centerPointX, centerPointY, radius1, radius2, segmentCount);
	}

	public Ellipse set(float centerPointX, float centerPointY, float radius1, float radius2) {
		return set(centerPointX, centerPointY, radius1, radius2, DEFAULT_SEGMENT_MAX_COUNT);
	}

	public Ellipse set(float centerPointX, float centerPointY, float radius1, float radius2, int segmentCount) {
		this.x = centerPointX - radius1;
		this.y = centerPointY - radius2;
		this.radius1 = radius1;
		this.radius2 = radius2;
		this.segmentCount = segmentCount;
		checkPoints();
		return this;
	}

	public boolean inEllipse(Ellipse ellipse) {
		if (ellipse == null) {
			return false;
		}
		return CollisionHelper.checkEllipsevsEllipse(getRealX(), getRealY(), getRadius1(), getRadius2(),
				ellipse.getRealX(), ellipse.getRealY(), ellipse.getRadius1(), ellipse.getRadius2());
	}

	public boolean inRect(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkEllipsevsAABB(getRealX(), getRealY(), getRadius1(), getRadius2(), rect.x, rect.y,
				rect.width, rect.height);
	}

	public boolean inCircle(Circle circle) {
		if (circle == null) {
			return false;
		}
		return CollisionHelper.checkEllipsevsCircle(getRealX(), getRealY(), getRadius1(), getRadius2(),
				circle.getRealX(), circle.getRealY(), circle.getRadius());
	}

	public boolean inLine(Line line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkLinevsEllipse(line.getX1(), line.getY1(), line.getX2(), line.getY2(), getRealX(),
				getRealY(), getDiameter1(), getDiameter2());
	}

	public boolean inPoint(XY pos) {
		if (pos == null) {
			return false;
		}
		return CollisionHelper.checkPointvsEllipse(pos.getX(), pos.getY(), getRealX(), getRealY(), getRadius1(),
				getRadius2());
	}

	public boolean intersects(Line other) {
		return inLine(other);
	}

	public boolean intersects(Circle other) {
		return inCircle(other);
	}

	public boolean intersects(RectBox other) {
		return inRect(other);
	}

	public boolean intersects(Ellipse other) {
		return inEllipse(other);
	}

	/**
	 * 检查当前圆形与指定形状是否相交
	 */
	@Override
	public boolean intersects(Shape shape) {
		if (shape instanceof Circle) {
			return inCircle((Circle) shape);
		} else if (shape instanceof RectBox) {
			return inRect((RectBox) shape);
		} else if (shape instanceof Line) {
			return inLine((Line) shape);
		} else if (shape instanceof Ellipse) {
			return inEllipse((Ellipse) shape);
		} else {
			return super.intersects(shape);
		}
	}

	@Override
	public boolean contains(XY xy) {
		if (xy == null) {
			return false;
		}
		return contains(xy.getX(), xy.getY());
	}

	@Override
	public boolean contains(float x, float y) {
		return CollisionHelper.checkPointvsEllipse(x, y, getRealX(), getRealY(), getDiameter1(), getDiameter2());
	}

	@Override
	public float getLeft() {
		return this.x;
	}

	@Override
	public float getRight() {
		return this.x + this.getDiameter1();
	}

	@Override
	public float getTop() {
		return this.y;
	}

	@Override
	public float getBottom() {
		return this.y + this.getDiameter2();
	}

	/**
	 * 返回当前圆形的中心X点
	 * 
	 */
	@Override
	public float getCenterX() {
		return getRealX();
	}

	/**
	 * 返回当前圆形的中心Y点
	 */
	@Override
	public float getCenterY() {
		return getRealY();
	}

	public float getRealX() {
		return this.x + radius1;
	}

	public float getRealY() {
		return this.y + radius2;
	}

	/**
	 * 设定当前椭圆形半径
	 * 
	 * @param radius1
	 * @param radius2
	 */
	public Ellipse setRadii(float radius1, float radius2) {
		setRadius1(radius1);
		setRadius2(radius2);
		return this;
	}

	public float getDiameter1() {
		return radius1 * 2f;
	}

	public float getRadius1() {
		return radius1;
	}

	public Ellipse setRadius1(float radius1) {
		if (radius1 != this.radius1) {
			this.radius1 = radius1;
			pointsDirty = true;
		}
		return this;
	}

	public float getDiameter2() {
		return radius2 * 2f;
	}

	public float getRadius2() {
		return radius2;
	}

	public Ellipse setRadius2(float radius2) {
		if (radius2 != this.radius2) {
			this.radius2 = radius2;
			pointsDirty = true;
		}
		return this;
	}

	@Override
	protected void createPoints() {
		TArray<Float> tempPoints = new TArray<Float>();

		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;

		float start = _start;
		float end = _end;

		float cx = x + radius1;
		float cy = y + radius2;

		int step = 360 / segmentCount;

		for (float a = start; a <= end + step; a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}

			float newX = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * radius1));
			float newY = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * radius2));

			if (newX > maxX) {
				maxX = newX;
			}
			if (newY > maxY) {
				maxY = newY;
			}
			if (newX < minX) {
				minX = newX;
			}
			if (newY < minY) {
				minY = newY;
			}

			tempPoints.add(newX);
			tempPoints.add(newY);
		}
		points = new float[tempPoints.size];
		for (int i = 0; i < points.length; i++) {
			points[i] = tempPoints.get(i);
		}
	}

	@Override
	protected void findCenter() {
		center = new float[2];
		center[0] = x + radius1;
		center[1] = y + radius2;
	}

	@Override
	protected void calculateRadius() {
		boundingCircleRadius = MathUtils.max(radius1, radius2);
	}

	public float getStart() {
		return _start;
	}

	public Ellipse setStart(float start) {
		this._start = start;
		return this;
	}

	public float getEnd() {
		return _end;
	}

	public Ellipse setEnd(float end) {
		this._end = end;
		return this;
	}

	public boolean equals(Ellipse e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.x == this.x && e.y == this.y && e.radius1 == this.radius1 && e.radius2 == this.radius2
				&& this.boundingCircleRadius == e.boundingCircleRadius) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Ellipse) {
			return equals((Ellipse) obj);
		}
		return false;
	}

	public Ellipse copy(Ellipse e) {
		if (e == null) {
			return this;
		}
		if (equals(e)) {
			return this;
		}
		this.x = e.x;
		this.y = e.y;
		this.rotation = e.rotation;
		this.radius1 = e.radius1;
		this.radius2 = e.radius2;
		this.segmentCount = e.segmentCount;
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
	public Ellipse copy(Shape e) {
		if (e instanceof Ellipse) {
			copy((Ellipse) e);
		} else {
			super.copy(e);
		}
		return this;
	}

	@Override
	public Ellipse cpy() {
		return new Ellipse(getCenterX(), getCenterY(), this.radius1, this.radius2, this.segmentCount);
	}

	public float getMinorRadius() {
		return MathUtils.min(this.radius1, this.radius2) / 2f;
	}

	public float getMajorRadius() {
		return MathUtils.max(this.radius1, this.radius2) / 2f;
	}

	@Override
	public Shape transform(Matrix3 transform) {
		checkPoints();

		Polygon resultPolygon = new Polygon();

		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultPolygon.points = result;
		resultPolygon.checkPoints();

		return resultPolygon;
	}

	@Override
	public int hashCode() {
		final int prime = 53;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, x);
		hashCode = prime * LSystem.unite(hashCode, y);
		hashCode = prime * LSystem.unite(hashCode, boundingCircleRadius);
		hashCode = prime * LSystem.unite(hashCode, radius1);
		hashCode = prime * LSystem.unite(hashCode, radius2);
		return hashCode;
	}
}
