package loon.core.geom;

import loon.utils.MathUtils;

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
public class RectBox extends Shape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Rect2i {

		public int left;

		public int top;

		public int right;

		public int bottom;

		public Rect2i() {
		}

		public Rect2i(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public Rect2i(Rect2i r) {
			left = r.left;
			top = r.top;
			right = r.right;
			bottom = r.bottom;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			Rect2i r = (Rect2i) obj;
			if (r != null) {
				return left == r.left && top == r.top && right == r.right
						&& bottom == r.bottom;
			}
			return false;
		}

		public final boolean isEmpty() {
			return left >= right || top >= bottom;
		}

		public final int width() {
			return right - left;
		}

		public final int height() {
			return bottom - top;
		}

		public final int centerX() {
			return (left + right) >> 1;
		}

		public final int centerY() {
			return (top + bottom) >> 1;
		}

		public final float exactCenterX() {
			return (left + right) * 0.5f;
		}

		public final float exactCenterY() {
			return (top + bottom) * 0.5f;
		}

		public void setEmpty() {
			left = right = top = bottom = 0;
		}

		public void set(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public void set(Rect2i src) {
			this.left = src.left;
			this.top = src.top;
			this.right = src.right;
			this.bottom = src.bottom;
		}

		public void offset(int dx, int dy) {
			left += dx;
			top += dy;
			right += dx;
			bottom += dy;
		}

		public void offsetTo(int newLeft, int newTop) {
			right += newLeft - left;
			bottom += newTop - top;
			left = newLeft;
			top = newTop;
		}

		public void inset(int dx, int dy) {
			left += dx;
			top += dy;
			right -= dx;
			bottom -= dy;
		}

		public boolean contains(int x, int y) {
			return left < right && top < bottom && x >= left && x < right
					&& y >= top && y < bottom;
		}

		public boolean contains(int left, int top, int right, int bottom) {
			return this.left < this.right && this.top < this.bottom
					&& this.left <= left && this.top <= top
					&& this.right >= right && this.bottom >= bottom;
		}

		public boolean contains(Rect2i r) {
			return this.left < this.right && this.top < this.bottom
					&& left <= r.left && top <= r.top && right >= r.right
					&& bottom >= r.bottom;
		}

		public boolean intersect(int left, int top, int right, int bottom) {
			if (this.left < right && left < this.right && this.top < bottom
					&& top < this.bottom) {
				if (this.left < left) {
					this.left = left;
				}
				if (this.top < top) {
					this.top = top;
				}
				if (this.right > right) {
					this.right = right;
				}
				if (this.bottom > bottom) {
					this.bottom = bottom;
				}
				return true;
			}
			return false;
		}

		public boolean intersect(Rect2i r) {
			return intersect(r.left, r.top, r.right, r.bottom);
		}

		public boolean setIntersect(Rect2i a, Rect2i b) {
			if (a.left < b.right && b.left < a.right && a.top < b.bottom
					&& b.top < a.bottom) {
				left = Math.max(a.left, b.left);
				top = Math.max(a.top, b.top);
				right = Math.min(a.right, b.right);
				bottom = Math.min(a.bottom, b.bottom);
				return true;
			}
			return false;
		}

		public boolean intersects(int left, int top, int right, int bottom) {
			return this.left < right && left < this.right && this.top < bottom
					&& top < this.bottom;
		}

		public static boolean intersects(Rect2i a, Rect2i b) {
			return a.left < b.right && b.left < a.right && a.top < b.bottom
					&& b.top < a.bottom;
		}

		public void union(int left, int top, int right, int bottom) {
			if ((left < right) && (top < bottom)) {
				if ((this.left < this.right) && (this.top < this.bottom)) {
					if (this.left > left)
						this.left = left;
					if (this.top > top)
						this.top = top;
					if (this.right < right)
						this.right = right;
					if (this.bottom < bottom)
						this.bottom = bottom;
				} else {
					this.left = left;
					this.top = top;
					this.right = right;
					this.bottom = bottom;
				}
			}
		}

		public void union(Rect2i r) {
			union(r.left, r.top, r.right, r.bottom);
		}

		public void union(int x, int y) {
			if (x < left) {
				left = x;
			} else if (x > right) {
				right = x;
			}
			if (y < top) {
				top = y;
			} else if (y > bottom) {
				bottom = y;
			}
		}

		public void sort() {
			if (left > right) {
				int temp = left;
				left = right;
				right = temp;
			}
			if (top > bottom) {
				int temp = top;
				top = bottom;
				bottom = temp;
			}
		}

		public void scale(float scale) {
			if (scale != 1.0f) {
				left = (int) (left * scale + 0.5f);
				top = (int) (top * scale + 0.5f);
				right = (int) (right * scale + 0.5f);
				bottom = (int) (bottom * scale + 0.5f);
			}
		}

	}

	public int width;

	public int height;

	public void offset(Vector2f offset) {
		x += offset.x;
		y += offset.y;
	}

	public void offset(int offsetX, int offsetY) {
		x += offsetX;
		y += offsetY;
	}

	public int Left() {
		return this.x();
	}

	public int Right() {
		return (int) (this.x + this.width);
	}

	public int Top() {
		return this.y();
	}

	public int Bottom() {
		return (int) (this.y + this.height);
	}

	public RectBox() {
		setBounds(0, 0, 0, 0);
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

	public void setBoundsFromCenter(float centerX, float centerY,
			float cornerX, float cornerY) {
		float halfW = MathUtils.abs(cornerX - centerX);
		float halfH = MathUtils.abs(cornerY - centerY);
		setBounds(centerX - halfW, centerY - halfH, halfW * 2.0, halfH * 2.0);
	}

	public void setBounds(RectBox rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	public void setBounds(double x, double y, double width, double height) {
		setBounds((float) x, (float) y, (float) width, (float) height);
	}

	public void setBounds(float x, float y, float width, float height) {
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
	}

	public void inflate(int horizontalValue, int verticalValue) {
		this.x -= horizontalValue;
		this.y -= verticalValue;
		this.width += horizontalValue * 2;
		this.height += verticalValue * 2;
	}

	public void setLocation(RectBox r) {
		this.x = r.x;
		this.y = r.y;
	}

	public void setLocation(Point r) {
		this.x = r.x;
		this.y = r.y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void grow(float h, float v) {
		setX(getX() - h);
		setY(getY() - v);
		setWidth(getWidth() + (h * 2));
		setHeight(getHeight() + (v * 2));
	}

	public void scaleGrow(float h, float v) {
		grow(getWidth() * (h - 1), getHeight() * (v - 1));
	}

	@Override
	public void setScale(float sx, float sy) {
		if (scaleX != sx || scaleY != sy) {
			setSize(width * (scaleX = sx), height * (scaleY * sy));
		}
	}

	public void setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
	}

	public boolean overlaps(RectBox rectangle) {
		return !(x > rectangle.x + rectangle.width || x + width < rectangle.x
				|| y > rectangle.y + rectangle.height || y + height < rectangle.y);
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
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

	public void copy(RectBox other) {
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
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

	public float getRight() {
		return getMaxX();
	}

	public float getBottom() {
		return getMaxY();
	}

	public float getMiddleX() {
		return this.x + this.width / 2;
	}

	public float getMiddleY() {
		return this.y + this.height / 2;
	}

	@Override
	public float getCenterX() {
		return x + width / 2f;
	}

	@Override
	public float getCenterY() {
		return y + height / 2f;
	}

	public static RectBox getIntersection(RectBox a, RectBox b) {
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
		return i_x < i_r && i_y < i_t ? new RectBox(i_x, i_y, i_r - i_x, i_t
				- i_y) : null;
	}

	public static RectBox getIntersection(RectBox a, RectBox b, RectBox result) {
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

	public void setHeight(float height) {
		this.height = (int) height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = (int) width;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
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

	public int getArea() {
		return width * height;
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
		return contains(x, y, 0, 0);
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
		return (x >= this.x && y >= this.y
				&& ((x + width) <= (this.x + this.width)) && ((y + height) <= (this.y + this.height)));
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

	/**
	 * 设定矩形选框交集
	 * 
	 * @param rect
	 * @return
	 */

	public boolean intersects(RectBox rect) {
		return intersects(rect.x, rect.y, rect.width, rect.height);
	}

	public boolean intersects(int x, int y) {
		return intersects(0, 0, width, height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean intersects(float x, float y, float width, float height) {
		return x + width > this.x && x < this.x + this.width
				&& y + height > this.y && y < this.y + this.height;
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param rect
	 */
	public void intersection(RectBox rect) {
		intersection(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 设定矩形选框交集
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void intersection(float x, float y, float width, float height) {
		int x1 = (int) MathUtils.max(this.x, x);
		int y1 = (int) MathUtils.max(this.y, y);
		int x2 = (int) MathUtils.min(this.x + this.width - 1, x + width - 1);
		int y2 = (int) MathUtils.min(this.y + this.height - 1, y + height - 1);
		setBounds(x1, y1, Math.max(0, x2 - x1 + 1), Math.max(0, y2 - y1 + 1));
	}

	/**
	 * 判定指定坐标是否位于当前RectBox内部
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inside(int x, int y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y)
				&& ((y - this.y) < this.height);
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
	public void union(RectBox rect) {
		union(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * 合并矩形选框
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void union(float x, float y, float width, float height) {
		int x1 = (int) MathUtils.min(this.x, x);
		int y1 = (int) MathUtils.min(this.y, y);
		int x2 = (int) MathUtils.max(this.x + this.width - 1, x + width - 1);
		int y2 = (int) MathUtils.max(this.y + this.height - 1, y + height - 1);
		setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
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
	public Shape transform(Matrix transform) {
		checkPoints();
		Polygon resultPolygon = new Polygon();
		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultPolygon.points = result;
		resultPolygon.findCenter();
		resultPolygon.checkPoints();
		return resultPolygon;
	}

	/**
	 * 水平移动X坐标执行长度
	 * 
	 * @param xMod
	 */
	public final void modX(float xMod) {
		x += xMod;
	}

	/**
	 * 水平移动Y坐标指定长度
	 * 
	 * @param yMod
	 */
	public final void modY(float yMod) {
		y += yMod;
	}

	/**
	 * 水平移动Width指定长度
	 * 
	 * @param w
	 */
	public void modWidth(float w) {
		this.width += w;
	}

	/**
	 * 水平移动Height指定长度
	 * 
	 * @param h
	 */
	public void modHeight(float h) {
		this.height += h;
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
	public final boolean intersectsLine(final float x1, final float y1,
			final float x2, final float y2) {
		return contains(x1, y1) || contains(x2, y2);
	}

	/**
	 * 判定指定坐标是否位于当前RectBox内部
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean inside(float x, float y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y)
				&& ((y - this.y) < this.height);
	}

}
