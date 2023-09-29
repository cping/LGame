/**
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

import loon.LObject;
import loon.LSystem;
import loon.action.ActionBind;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class RectBox extends Shape implements BoxSize, SetXYZW, XYZW {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static RectBox toPixels(final RectBox rect, final XY point) {
		return new RectBox(rect.getX() * point.getX(), rect.getY() * point.getY(), rect.getWidth() * point.getX(),
				rect.getHeight() * point.getY());
	}

	public static RectBox toPixels(final RectBox rect, int tileWidth, int tileHeight) {
		return new RectBox(rect.x * tileWidth, rect.y * tileHeight, rect.width * tileWidth, rect.height * tileHeight);
	}

	public static RectBox toTitle(final RectBox rect, final XY point) {
		return new RectBox(rect.getX() / point.getX(), rect.getY() / point.getY(), rect.getWidth() / point.getX(),
				rect.getHeight() / point.getY());
	}

	public static RectBox toTitle(final RectBox rect, int tileWidth, int tileHeight) {
		return new RectBox(rect.x / tileWidth, rect.y / tileHeight, rect.width / tileWidth, rect.height / tileHeight);
	}

	public final static SetXY getRandom(RectBox rect, SetXY out) {
		if (out == null) {
			out = new PointF();
		}
		out.setX(rect.x + (MathUtils.random() * rect.width));
		out.setY(rect.y + (MathUtils.random() * rect.height));
		return out;
	}

	public final static RectBox at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new RectBox();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 3) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				float width = Float.parseFloat(result[2].trim());
				float height = Float.parseFloat(result[3].trim());
				return new RectBox(x, y, width, height);
			} catch (Exception ex) {
			}
		}
		return new RectBox();
	}

	public final static RectBox at(int x, int y, int w, int h) {
		return new RectBox(x, y, w, h);
	}

	public final static RectBox at(float x, float y, float w, float h) {
		return new RectBox(x, y, w, h);
	}

	public final static RectBox fromActor(ActionBind bind) {
		return new RectBox(bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight());
	}

	public final static RectBox inflate(RectBox src, int xScale, int yScale) {
		float destWidth = src.width + xScale;
		float destHeight = src.height + yScale;
		float destX = src.x - xScale / 2;
		float destY = src.y - yScale / 2;
		return new RectBox(destX, destY, destWidth, destHeight);
	}

	public final static RectBox intersect(RectBox src1, RectBox src2, RectBox dest) {
		if (dest == null) {
			dest = new RectBox();
		}
		float x1 = MathUtils.max(src1.getMinX(), src2.getMinX());
		float y1 = MathUtils.max(src1.getMinY(), src2.getMinY());
		float x2 = MathUtils.min(src1.getMaxX(), src2.getMaxX());
		float y2 = MathUtils.min(src1.getMaxY(), src2.getMaxY());
		dest.setBounds(x1, y1, x2 - x1, y2 - y1);
		return dest;
	}

	public final static RectBox getIntersection(RectBox a, RectBox b) {
		float a_x = a.getX();
		float a_r = a.getRight();
		float a_y = a.getY();
		float a_t = a.getBottom();
		float b_x = b.getX();
		float b_r = b.getRight();
		float b_y = b.getY();
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectBox(i_x, i_y, i_r - i_x, i_t - i_y) : null;
	}

	public final static RectBox getIntersection(RectBox a, RectBox b, RectBox result) {
		float a_x = a.getX();
		float a_r = a.getRight();
		float a_y = a.getY();
		float a_t = a.getBottom();
		float b_x = b.getX();
		float b_r = b.getRight();
		float b_y = b.getY();
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		if (i_x < i_r && i_y < i_t) {
			result.setBounds(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return null;
	}

	public final static PointF[] getRectCorners(float x, float y, float width, float height) {
		PointF a = new PointF(x, y);
		PointF b = new PointF(x + height, y);
		PointF c = new PointF(x, y + width);
		PointF d = new PointF(x + height, y + width);
		return new PointF[] { a, b, c, d };
	}

	public final static PointF[] getRectCorners(XYZW rect) {
		PointF a = new PointF(rect.getX(), rect.getY());
		PointF b = new PointF(rect.getX() + rect.getW(), rect.getY());
		PointF c = new PointF(rect.getX(), rect.getY() + rect.getZ());
		PointF d = new PointF(rect.getX() + rect.getW(), rect.getY() + rect.getZ());
		return new PointF[] { a, b, c, d };
	}

	public final static TArray<PointF> getRectCornersList(XYZW rect) {
		PointF a = new PointF(rect.getX(), rect.getY());
		PointF b = new PointF(rect.getX() + rect.getW(), rect.getY());
		PointF c = new PointF(rect.getX(), rect.getY() + rect.getZ());
		PointF d = new PointF(rect.getX() + rect.getW(), rect.getY() + rect.getZ());
		TArray<PointF> result = new TArray<PointF>();
		result.add(a);
		result.add(b);
		result.add(c);
		result.add(d);
		return result;
	}

	public final static PointF[] getRectSegments(float x, float y, float w, float h) {
		PointF[] c = getRectCorners(x, y, w, h);
		return getRectSegments(c[0], c[1], c[2], c[3]);
	}

	public final static PointF[] getRectSegments(XYZW rect) {
		PointF[] c = getRectCorners(rect);
		return getRectSegments(c[0], c[1], c[2], c[3]);
	}

	public final static PointF[] getRectSegments(PointF left, PointF right, PointF top, PointF bottom) {
		return new PointF[] { left, right, top, bottom, left, top, bottom, right };
	}

	public int width;

	public int height;

	private Matrix4 _rectMatrix;

	public RectBox() {
		setBounds(0, 0, 0, 0);
	}

	public RectBox(int width, int height) {
		setBounds(0, 0, width, height);
	}

	public RectBox(int x, int y, int width, int height) {
		setBounds(x, y, width, height);
	}

	public RectBox(float x, float y, float width, float height) {
		setBounds(x, y, width, height);
	}

	public RectBox(double x, double y, double width, double height) {
		setBounds(x, y, width, height);
	}

	public RectBox(RectBox rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public RectBox offset(Vector2f offset) {
		this.x += offset.x;
		this.y += offset.y;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	public RectBox offset(int offsetX, int offsetY) {
		this.x += offsetX;
		this.y += offsetY;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	public RectBox setBoundsFromCenter(float centerX, float centerY, float cornerX, float cornerY) {
		float halfW = MathUtils.abs(cornerX - centerX);
		float halfH = MathUtils.abs(cornerY - centerY);
		setBounds(centerX - halfW, centerY - halfH, halfW * 2.0, halfH * 2.0);
		return this;
	}

	public RectBox setBounds(RectBox rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
		return this;
	}

	public RectBox setBounds(double x, double y, double width, double height) {
		setBounds((float) x, (float) y, (float) width, (float) height);
		return this;
	}

	public RectBox setBounds(float x, float y, float width, float height) {
		if (this.x == x && this.y == y && this.width == width && this.height == height) {
			return this;
		}
		this.x = x;
		this.y = y;
		this.width = (int) width;
		this.height = (int) height;
		this.minX = x;
		this.minY = y;
		this.maxX = x + width;
		this.maxY = y + height;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	public RectBox set(BoxSize size) {
		if (size == null) {
			return this;
		}
		return setBounds(size.getX(), size.getY(), size.getWidth(), size.getHeight());
	}

	public RectBox set(float x, float y, float width, float height) {
		return setBounds(x, y, width, height);
	}

	@Override
	public void setZ(float z) {
		this.setWidth(z);
	}

	@Override
	public void setW(float w) {
		this.setHeight(w);
	}

	public Polygon getPolygon() {
		this.checkPoints();
		Polygon poly = new Polygon(this.points);
		return poly;
	}

	public RectBox inflate(int horizontalValue, int verticalValue) {
		this.x -= horizontalValue;
		this.y -= verticalValue;
		this.width += horizontalValue * 2;
		this.height += verticalValue * 2;
		this.minX = x;
		this.minY = y;
		this.maxX = x + width;
		this.maxY = y + height;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	public RectBox setLocation(BoxSize r) {
		if (r == null) {
			return this;
		}
		return setLocation(r.getX(), r.getY());
	}

	public RectBox setLocation(XY r) {
		if (r == null) {
			return this;
		}
		return setLocation(r.getX(), r.getY());
	}

	public RectBox setLocation(Point r) {
		if (r == null) {
			return this;
		}
		return setLocation(r.x, r.y);
	}

	@Override
	public RectBox setLocation(float x, float y) {
		if (this.x == x && this.y == y) {
			return this;
		}
		super.setLocation(x, y);
		return this;
	}

	public RectBox setLocation(int x, int y) {
		if (this.x == x && this.y == y) {
			return this;
		}
		super.setLocation(x, y);
		return this;
	}

	public RectBox grow(float h, float v) {
		return setBounds(getX() - h, getY() - v, getWidth() + (h * 2), getHeight() + (v * 2));
	}

	public RectBox scaleGrow(float h, float v) {
		grow(getWidth() * (h - 1), getHeight() * (v - 1));
		return this;
	}

	@Override
	public void setScale(float sx, float sy) {
		if (scaleX != sx || scaleY != sy) {
			setSize(width * (scaleX = sx), height * (scaleY * sy));
		}
	}

	public RectBox setRotate(float r) {
		if (r != this.rotation) {
			this.rotation = r;
			int[] rect = MathUtils.getLimit(x, y, width, height, rotation);
			return setBounds(rect[0], rect[1], rect[2], rect[3]);
		}
		return this;
	}

	@Override
	public Shape setRotation(float r, float x, float y) {
		if (r != this.rotation) {
			super.setRotation(r, x, y);
			setBounds(minX, minY, (maxX - minX), (maxY - minY));
		}
		return this;
	}

	public RectBox setSize(float width, float height) {
		if (this.width == width && this.height == height) {
			return this;
		}
		return setBounds(this.x, this.y, width, height);
	}

	public boolean overlaps(RectBox rectangle) {
		return !(x > rectangle.x + rectangle.width || x + width < rectangle.x || y > rectangle.y + rectangle.height
				|| y + height < rectangle.y);
	}

	public Matrix4 getMatrix() {
		if (_rectMatrix == null) {
			_rectMatrix = new Matrix4();
		}
		return _rectMatrix.setToOrtho2D(this.x, this.y, this.width, this.height);
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public float getZ() {
		return getWidth();
	}

	@Override
	public float getW() {
		return getHeight();
	}

	public RectBox clip(XYZW rect) {
		float right = this.x + this.width;
		float bottom = this.y + this.height;
		float newX = MathUtils.max(rect.getX(), this.x);
		float newWidth = MathUtils.ifloor(MathUtils.min(rect.getX() + rect.getZ(), right) - this.x);
		float newY = MathUtils.max(rect.getY(), this.y);
		float newHeight = MathUtils.ifloor(MathUtils.min(rect.getY() + rect.getW(), bottom) - this.y);
		return setBounds(newX, newY, newWidth, newHeight);
	}

	public RectBox copy(RectBox other) {
		return setBounds(other);
	}

	public RectBox ceil() {
		return ceil(1f, MathUtils.EPSILON);
	}

	public RectBox ceil(float resolution, float eps) {
		final float x2 = MathUtils.ceil((this.x + this.width - eps) * resolution) / resolution;
		final float y2 = MathUtils.ceil((this.y + this.height - eps) * resolution) / resolution;
		float newX = MathUtils.floor((this.x + eps) * resolution) / resolution;
		float newY = MathUtils.floor((this.y + eps) * resolution) / resolution;
		float newWidth = (x2 - this.x);
		float newHeight = (y2 - this.y);
		return setBounds(newX, newY, newWidth, newHeight);
	}

	@Override
	public float getMinX() {
		return getX();
	}

	@Override
	public float getMinY() {
		return getY();
	}

	@Override
	public float getMaxX() {
		return this.x + this.width;
	}

	@Override
	public float getMaxY() {
		return this.y + this.height;
	}

	public float getMiddleX() {
		return getCenterX();
	}

	public float getMiddleY() {
		return getCenterY();
	}

	@Override
	public float getCenterX() {
		return x + width / 2f;
	}

	@Override
	public float getCenterY() {
		return y + height / 2f;
	}

	public float getLeft() {
		return this.getMinX();
	}

	public RectBox setLeft(float value) {
		this.width += this.x - value;
		this.x = value;
		return this;
	}

	public float getRight() {
		return getMaxX();
	}

	public RectBox setRight(float v) {
		this.width = (int) (v - this.x);
		return this;
	}

	public float getTop() {
		return getMinY();
	}

	public RectBox setTop(float value) {
		this.height += this.y - value;
		this.y = value;
		return this;
	}

	public float getBottom() {
		return getMaxY();
	}

	public RectBox setBottom(float v) {
		this.height = (int) (v - this.y);
		return this;
	}

	public Vector2f getHalfSize() {
		return new Vector2f(this.width / 2f, this.height / 2f);
	}

	public int Left() {
		return this.x();
	}

	public int Right() {
		return (int) getMaxX();
	}

	public int Top() {
		return this.y();
	}

	public int Bottom() {
		return (int) getMaxY();
	}

	public Vector2f topLeft() {
		return new Vector2f(this.getLeft(), this.getTop());
	}

	public Vector2f bottomRight() {
		return new Vector2f(this.getRight(), this.getBottom());
	}

	public RectBox normalize() {
		return normalize(this);
	}

	public RectBox normalize(RectBox r) {
		if (r.width < 0) {
			r.width = MathUtils.abs(r.width);
			r.x -= r.width;
		}
		if (r.height < 0) {
			r.height = MathUtils.abs(r.height);
			r.y -= r.height;
		}
		return this;
	}

	public float[] toFloat() {
		return new float[] { x, y, width, height };
	}

	@Override
	public RectBox getRect() {
		return this;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void setHeight(float height) {
		this.height = (int) height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void setWidth(float width) {
		this.width = (int) width;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RectBox) {
			RectBox rect = (RectBox) obj;
			return equals(rect.x, rect.y, rect.width, rect.height);
		} else {
			return false;
		}
	}

	public boolean equals(float x, float y, float width, float height) {
		return (this.x == x && this.y == y && this.width == width && this.height == height);
	}

	public RectBox enlarge(RectBox rect) {
		final float x1 = MathUtils.min(this.x, rect.x);
		final float x2 = MathUtils.max(this.x + this.width, rect.x + rect.width);
		final float y1 = MathUtils.min(this.y, rect.y);
		final float y2 = MathUtils.max(this.y + this.height, rect.y + rect.height);

		this.x = x1;
		this.width = (int) (x2 - x1);
		this.y = y1;
		this.height = (int) (y2 - y1);

		return this;
	}

	public int getArea() {
		return width * height;
	}

	public Line[] getLines() {
		final Line[] lines = new Line[4];
		lines[0] = new Line(getMinX(), getMinY(), getMinX(), getMaxY());
		lines[1] = new Line(getMinX(), getMaxY(), getMaxX(), getMaxY());
		lines[2] = new Line(getMaxX(), getMaxY(), getMaxX(), getMinY());
		lines[3] = new Line(getMaxX(), getMinY(), getMinX(), getMinY());
		return lines;
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	@Override
	public boolean contains(float x, float y) {
		return contains(x, y, 1f, 1f);
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean contains(float x, float y, float width, float height) {
		return (x >= this.x && y >= this.y && ((x + width) <= (this.x + this.width))
				&& ((y + height) <= (this.y + this.height)));
	}

	/**
	 * 检查是否包含指定坐标
	 * 
	 * @param rect
	 * @return
	 */
	public boolean contains(RectBox rect) {
		return contains(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean contains(LObject<?> rect) {
		return contains(rect.getCollisionArea());
	}

	public boolean contains(Circle circle) {
		float xmin = circle.x - circle.boundingCircleRadius;
		float xmax = xmin + 2f * circle.boundingCircleRadius;
		float ymin = circle.y - circle.boundingCircleRadius;
		float ymax = ymin + 2f * circle.boundingCircleRadius;
		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
				&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
	}

	public boolean contains(Vector2f v) {
		return contains(v.x, v.y);
	}

	public boolean contains(Vector3f v) {
		return contains(v.x, v.y);
	}

	public boolean contains(Vector4f v) {
		return contains(v.x, v.y);
	}

	public boolean contains(Point point) {
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y) {
			return true;
		}
		return false;
	}

	public boolean contains(PointF point) {
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y) {
			return true;
		}
		return false;
	}

	public boolean contains(PointI point) {
		if (this.x < point.x && this.x + this.width > point.x && this.y < point.y && this.y + this.height > point.y) {
			return true;
		}
		return false;
	}

	/**
	 * 判定矩形选框交集
	 * 
	 * @param rect
	 * @return
	 */
	public boolean intersects(RectBox rect) {
		return intersects(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 判定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean intersects(float x, float y) {
		return intersects(x, y, 1f, 1f);
	}

	/**
	 * 判定矩形选框交集
	 * 
	 * @param xy
	 * @return
	 */
	public boolean intersects(XY xy) {
		if (xy == null) {
			return false;
		}
		return intersects(xy.getX(), xy.getY());
	}

	/**
	 * 判定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean intersects(float x, float y, float width, float height) {
		return x + width > this.x && x < this.x + this.width && y + height > this.y && y < this.y + this.height;
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param rect
	 */
	public RectBox intersection(RectBox rect) {
		return intersection(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public RectBox intersection(float x, float y, float width, float height) {
		int x1 = (int) MathUtils.max(this.x, x);
		int y1 = (int) MathUtils.max(this.y, y);
		int x2 = (int) MathUtils.min(this.x + this.width - 1, x + width - 1);
		int y2 = (int) MathUtils.min(this.y + this.height - 1, y + height - 1);
		return setBounds(x1, y1, MathUtils.max(0, x2 - x1 + 1), MathUtils.max(0, y2 - y1 + 1));
	}

	/**
	 * 判定指定坐标是否位于当前RectBox内部
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inside(int x, int y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	public boolean intersects(RectBox other, Affine2f transform) {
		if (transform == null) {
			final float x0 = this.x < other.x ? other.x : this.x;
			final float x1 = this.getRight() > other.getRight() ? other.getRight() : this.getRight();

			if (x1 <= x0) {
				return false;
			}

			final float y0 = this.y < other.y ? other.y : this.y;
			final float y1 = this.getBottom() > other.getBottom() ? other.getBottom() : this.getBottom();

			return y1 > y0;
		}

		final float x0 = this.getLeft();
		final float x1 = this.getRight();
		final float y0 = this.getTop();
		final float y1 = this.getBottom();

		if (x1 <= x0 || y1 <= y0) {
			return false;
		}

		final Vector2f lt = new Vector2f(other.getLeft(), other.getTop());
		final Vector2f lb = new Vector2f(other.getLeft(), other.getBottom());
		final Vector2f rt = new Vector2f(other.getRight(), other.getTop());
		final Vector2f rb = new Vector2f(other.getRight(), other.getBottom());

		if (rt.x <= lt.x || lb.y <= lt.y) {
			return false;
		}

		final float s = MathUtils.sign((transform.m00 * transform.m01) - (transform.m10 * transform.m11));

		if (s == 0) {
			return false;
		}

		transform.apply(lt, lt);
		transform.apply(lb, lb);
		transform.apply(rt, rt);
		transform.apply(rb, rb);

		if (MathUtils.max(lt.x, lb.x, rt.x, rb.x) <= x0 || MathUtils.min(lt.x, lb.x, rt.x, rb.x) >= x1
				|| MathUtils.max(lt.y, lb.y, rt.y, rb.y) <= y0 || MathUtils.min(lt.y, lb.y, rt.y, rb.y) >= y1) {
			return false;
		}

		final float nx = s * (lb.y - lt.y);
		final float ny = s * (lt.x - lb.x);
		final float n00 = (nx * x0) + (ny * y0);
		final float n10 = (nx * x1) + (ny * y0);
		final float n01 = (nx * x0) + (ny * y1);
		final float n11 = (nx * x1) + (ny * y1);

		if (MathUtils.max(n00, n10, n01, n11) <= (nx * lt.x) + (ny * lt.y)
				|| MathUtils.min(n00, n10, n01, n11) >= (nx * rb.x) + (ny * rb.y)) {
			return false;
		}

		final float mx = s * (lt.y - rt.y);
		final float my = s * (rt.x - lt.x);
		final float m00 = (mx * x0) + (my * y0);
		final float m10 = (mx * x1) + (my * y0);
		final float m01 = (mx * x0) + (my * y1);
		final float m11 = (mx * x1) + (my * y1);

		if (MathUtils.max(m00, m10, m01, m11) <= (mx * lt.x) + (my * lt.y)
				|| MathUtils.min(m00, m10, m01, m11) >= (mx * rb.x) + (my * rb.y)) {
			return false;
		}

		return true;
	}

	/**
	 * 返回当前的矩形选框交集
	 * 
	 * @param rect
	 * @return
	 */
	public RectBox getIntersection(RectBox rect) {
		int x1 = (int) MathUtils.max(x, rect.x);
		int x2 = (int) MathUtils.min(x + width, rect.x + rect.width);
		int y1 = (int) MathUtils.max(y, rect.y);
		int y2 = (int) MathUtils.min(y + height, rect.y + rect.height);
		return new RectBox(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * 合并矩形选框
	 * 
	 * @param rect
	 */
	public RectBox union(RectBox rect) {
		return union(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 合并矩形选框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public RectBox union(float x, float y, float width, float height) {
		int x1 = (int) MathUtils.min(this.x, x);
		int y1 = (int) MathUtils.min(this.y, y);
		int x2 = (int) MathUtils.max(this.x + this.width - 1, x + width - 1);
		int y2 = (int) MathUtils.max(this.y + this.height - 1, y + height - 1);
		setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		return this;
	}

	@Override
	protected void createPoints() {

		float useWidth = width;
		float useHeight = height;
		points = new float[8];

		points[0] = x;
		points[1] = y;

		points[2] = x + useWidth;
		points[3] = y;

		points[4] = x + useWidth;
		points[5] = y + useHeight;

		points[6] = x;
		points[7] = y + useHeight;

		maxX = points[2];
		maxY = points[5];
		minX = points[0];
		minY = points[1];
		findCenter();
		calculateRadius();
	}

	@Override
	public Shape transform(Matrix3 transform) {
		checkPoints();
		Polygon resultPolygon = new Polygon();
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultPolygon.points = result;
		resultPolygon.findCenter();
		resultPolygon.checkPoints();
		return resultPolygon;
	}

	public RectBox fit(RectBox rect) {
		final float x1 = MathUtils.max(this.x, rect.x);
		final float x2 = MathUtils.min(this.x + this.width, rect.x + rect.width);
		final float y1 = MathUtils.max(this.y, rect.y);
		final float y2 = MathUtils.min(this.y + this.height, rect.y + rect.height);
		this.x = x1;
		this.width = (int) MathUtils.max(x2 - x1, 0f);
		this.y = y1;
		this.height = (int) MathUtils.max(y2 - y1, 0f);
		return this;
	}

	/**
	 * 水平移动X坐标执行长度
	 * 
	 * @param xMod
	 */
	public final RectBox modX(float xMod) {
		x += xMod;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	/**
	 * 水平移动Y坐标指定长度
	 * 
	 * @param yMod
	 */
	public final RectBox modY(float yMod) {
		y += yMod;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	/**
	 * 水平移动Width指定长度
	 * 
	 * @param w
	 */
	public RectBox modWidth(float w) {
		this.width += w;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	/**
	 * 水平移动Height指定长度
	 * 
	 * @param h
	 */
	public RectBox modHeight(float h) {
		this.height += h;
		this.pointsDirty = true;
		this.checkPoints();
		return this;
	}

	/**
	 * 判断指定坐标是否在一条直线上
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public final boolean intersectsLine(final float x1, final float y1, final float x2, final float y2) {
		return contains(x1, y1) || contains(x2, y2);
	}

	public final boolean intersects(final Line line) {
		return contains(line.getX1(), line.getY1()) || contains(line.getX2(), line.getY2());
	}

	/**
	 * 判定指定坐标是否位于当前RectBox内部
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inside(float x, float y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	@Override
	public RectBox cpy() {
		return new RectBox(this.x, this.y, this.width, this.height);
	}

	public RectBox createIntersection(RectBox rectBox) {
		RectBox dest = new RectBox();
		dest.intersection(rectBox);
		intersect(this, rectBox, dest);
		return dest;
	}

	public float maxX() {
		return x() + width();
	}

	public float maxY() {
		return y() + height();
	}

	public RectBox mergeXY(RectBox rect, XY pos) {

		float minX = MathUtils.min(rect.x, x);
		float maxX = MathUtils.max(rect.getRight(), x);

		rect.x = minX;
		rect.width = MathUtils.floor(maxX - minX);

		float minY = MathUtils.min(rect.y, y);
		float maxY = MathUtils.max(rect.getBottom(), y);

		rect.y = minY;
		rect.height = MathUtils.floor(maxY - minY);

		return rect;
	}

	@Override
	public boolean isEmpty() {
		return getWidth() <= 0 || height() <= 0;
	}

	public RectBox setEmpty() {
		return this.setBounds(0f, 0f, 0f, 0f);
	}

	public RectBox offset(Point point) {
		x += point.x;
		y += point.y;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	public RectBox offset(PointF point) {
		x += point.x;
		y += point.y;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	public RectBox offset(PointI point) {
		x += point.x;
		y += point.y;
		this.pointsDirty = true;
		checkPoints();
		return this;
	}

	public RectBox inc(RectBox view) {
		if (view == null) {
			return cpy();
		}
		return new RectBox(x + view.x, y + view.y, width + view.width, height + view.height);
	}

	public RectBox sub(RectBox view) {
		if (view == null) {
			return cpy();
		}
		return new RectBox(x - view.x, y - view.y, width - view.width, height - view.height);
	}

	public RectBox mul(RectBox view) {
		if (view == null) {
			return cpy();
		}
		return new RectBox(x * view.x, y * view.y, width * view.width, height * view.height);
	}

	public RectBox div(RectBox view) {
		if (view == null) {
			return cpy();
		}
		return new RectBox(x / view.x, y / view.y, width / view.width, height / view.height);
	}

	public RectBox inc(float v) {
		return new RectBox(x + v, y + v, width + v, height + v);
	}

	public RectBox sub(float v) {
		return new RectBox(x - v, y - v, width - v, height - v);
	}

	public RectBox mul(float v) {
		return new RectBox(x * v, y * v, width * v, height * v);
	}

	public RectBox div(float v) {
		return new RectBox(x / v, y / v, width / v, height / v);
	}

	public RectBox add(float px, float py) {
		float x1 = MathUtils.min(x, px);
		float x2 = MathUtils.max(x + width, px);
		float y1 = MathUtils.min(y, py);
		float y2 = MathUtils.max(y + height, py);
		setBounds(x1, y1, x2 - x1, y2 - y1);
		return this;
	}

	public RectBox add(XY pos) {
		if (pos == null) {
			return this;
		}
		return add(pos.getX(), pos.getY());
	}

	public RectBox add(RectBox r) {
		int tx2 = this.width;
		int ty2 = this.height;
		if ((tx2 | ty2) < 0) {
			setBounds(r.x, r.y, r.width, r.height);
		}
		int rx2 = r.width;
		int ry2 = r.height;
		if ((rx2 | ry2) < 0) {
			return this;
		}
		float tx1 = this.x;
		float ty1 = this.y;
		tx2 += tx1;
		ty2 += ty1;
		float rx1 = r.x;
		float ry1 = r.y;
		rx2 += rx1;
		ry2 += ry1;
		if (tx1 > rx1) {
			tx1 = rx1;
		}
		if (ty1 > ry1) {
			ty1 = ry1;
		}
		if (tx2 < rx2) {
			tx2 = rx2;
		}
		if (ty2 < ry2) {
			ty2 = ry2;
		}
		tx2 -= tx1;
		ty2 -= ty1;
		if (tx2 > Integer.MAX_VALUE) {
			tx2 = Integer.MAX_VALUE;
		}
		if (ty2 > Integer.MAX_VALUE) {
			ty2 = Integer.MAX_VALUE;
		}
		setBounds(tx1, ty1, tx2, ty2);
		return this;
	}

	public DirtyRect getDirtyRect() {
		return new DirtyRect(this);
	}

	public float getAspectRatio() {
		return (height == 0) ? MathUtils.NaN : (float) width / (float) height;
	}

	public float area() {
		return this.width * this.height;
	}

	@Override
	public float perimeter() {
		return 2f * (this.width + this.height);
	}

	public TArray<PointF> getMarchingAnts(float step, float quantity) {
		if (step == -1f) {
			step = perimeter() / quantity;
		} else {
			quantity = MathUtils.round(perimeter() / step);
		}
		final TArray<PointF> result = new TArray<PointF>();
		float x = getX();
		float y = getY();
		int face = 0;
		for (int i = 0; i < quantity; i++) {
			result.add(new PointF(x, y));
			switch (face) {
			case 0:
				x += step;
				if (x >= getRight()) {
					face = 1;
					y += (x - getRight());
					x = getRight();
				}
				break;
			case 1:
				y += step;
				if (y >= getBottom()) {
					face = 2;
					x -= (y - getBottom());
					y = getBottom();
				}
				break;
			case 2:
				x -= step;
				if (x <= getLeft()) {
					face = 3;
					y -= (getLeft() - x);
					x = getLeft();
				}
				break;

			case 3:
				y -= step;
				if (y <= getTop()) {
					face = 0;
					y = getTop();
				}
				break;
			}
		}
		return result;
	}

	public RectBox random() {
		final int w = LSystem.viewSize.getWidth();
		final int h = LSystem.viewSize.getHeight();
		float newX = MathUtils.random(0f, w);
		float newY = MathUtils.random(0f, h);
		float newWidth = MathUtils.random(0, w);
		float newHeight = MathUtils.random(0, h);
		return setBounds(newX, newY, newWidth, newHeight);
	}

	public RectBox toPixels(XY point) {
		return toPixels(this, point);
	}

	public RectBox toPixels(int tileWidth, int tileHeight) {
		return toPixels(this, tileWidth, tileHeight);
	}

	public RectBox toTitle(final XY point) {
		return toTitle(this, point);
	}

	public RectBox toTitle(int tileWidth, int tileHeight) {
		return toTitle(this, tileWidth, tileHeight);
	}

	public RectBox pad(float padding) {
		return pad(padding, padding);
	}

	public RectBox pad(float paddingX, float paddingY) {
		float newX = x - paddingX;
		float newY = y - paddingY;
		float newWidth = width + paddingX * 2;
		float newHeight = height + paddingY * 2;
		return setBounds(newX, newY, newWidth, newHeight);
	}

	public ObservableXYZW<RectBox> observable(XYChange<RectBox> v) {
		return ObservableXYZW.at(v, this, this);
	}

	public boolean isValid() {
		return this.width > 0 && this.height > 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + NumberUtils.floatToIntBits(x);
		result = prime * result + NumberUtils.floatToIntBits(y);
		result = prime * result + NumberUtils.floatToIntBits(width);
		result = prime * result + NumberUtils.floatToIntBits(height);
		return result;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("RectBox");
		builder.kv("x", x).comma().kv("y", y).comma().kv("width", width).comma().kv("height", height).comma()
				.kv("left", Left()).comma().kv("right", Right()).comma().kv("top", Top()).comma()
				.kv("bottom", Bottom());
		return builder.toString();
	}

}
