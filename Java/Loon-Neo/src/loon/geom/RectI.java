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
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;

/*最简化的整型体积处理类,以减少对象大小*/
public class RectI implements XY {

	public static class Range implements XY {

		public int left;

		public int top;

		public int right;

		public int bottom;

		public Range() {
		}

		public Range(RectI rect) {
			this(rect.left(), rect.top(), rect.right(), rect.bottom());
		}

		public Range(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		public Range(Range r) {
			left = r.left;
			top = r.top;
			right = r.right;
			bottom = r.bottom;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + NumberUtils.floatToIntBits(left);
			result = prime * result + NumberUtils.floatToIntBits(top);
			result = prime * result + NumberUtils.floatToIntBits(right);
			result = prime * result + NumberUtils.floatToIntBits(bottom);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			Range r = (Range) obj;
			if (r != null) {
				return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
			}
			return false;
		}

		public final boolean isEmpty() {
			return left >= right || top >= bottom;
		}

		public final int x() {
			return left;
		}

		public final int y() {
			return top;
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

		public void set(Range src) {
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
			return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
		}

		public boolean contains(Circle circle) {
			float xmin = circle.x - circle.boundingCircleRadius;
			float xmax = xmin + 2f * circle.boundingCircleRadius;

			float ymin = circle.y - circle.boundingCircleRadius;
			float ymax = ymin + 2f * circle.boundingCircleRadius;

			return ((xmin > getX() && xmin < getX() + width()) && (xmax > getX() && xmax < getX() + width()))
					&& ((ymin > getY() && ymin < getY() + height()) && (ymax > getY() && ymax < getY() + height()));
		}

		public boolean contains(int left, int top, int right, int bottom) {
			return this.left < this.right && this.top < this.bottom && this.left <= left && this.top <= top
					&& this.right >= right && this.bottom >= bottom;
		}

		public boolean contains(Range r) {
			return this.left < this.right && this.top < this.bottom && left <= r.left && top <= r.top
					&& right >= r.right && bottom >= r.bottom;
		}

		public boolean intersect(int left, int top, int right, int bottom) {
			if (this.left < right && left < this.right && this.top < bottom && top < this.bottom) {
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

		public boolean intersect(Range r) {
			return intersect(r.left, r.top, r.right, r.bottom);
		}

		public boolean setIntersect(Range a, Range b) {
			if (a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom) {
				left = MathUtils.max(a.left, b.left);
				top = MathUtils.max(a.top, b.top);
				right = MathUtils.min(a.right, b.right);
				bottom = MathUtils.min(a.bottom, b.bottom);
				return true;
			}
			return false;
		}

		public boolean intersects(int left, int top, int right, int bottom) {
			return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
		}

		public boolean intersects(Range a, Range b) {
			return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
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

		public void union(Range r) {
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

		public RectI getRect() {
			return new RectI(this);
		}

		@Override
		public float getX() {
			return x();
		}

		@Override
		public float getY() {
			return y();
		}

	}

	public int width = 0;
	public int height = 0;
	public int x = 0;
	public int y = 0;

	public RectI() {
		this(0, 0, 0, 0);
	}

	public RectI(RectI rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public RectI(Range range) {
		this(range.x(), range.y(), range.width(), range.height());
	}

	public RectI(int w, int h) {
		this(0, 0, w, h);
	}

	public RectI(int x1, int y1, int w1, int h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
	}

	public RectI set(RectI r) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		return this;
	}

	public RectI set(int x1, int y1, int w1, int h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
		return this;
	}

	public boolean inside(int x, int y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	public int getRight() {
		return this.x + this.width;
	}

	public int getBottom() {
		return this.y + this.height;
	}

	public RectI getIntersection(RectI rect) {
		int x1 = MathUtils.max(x, rect.x);
		int x2 = MathUtils.min(x + width, rect.x + rect.width);
		int y1 = MathUtils.max(y, rect.y);
		int y2 = MathUtils.min(y + height, rect.y + rect.height);
		return new RectI(x1, y1, x2 - x1, y2 - y1);
	}

	public static RectI getIntersection(RectI a, RectI b) {
		int a_x = a.x;
		int a_r = a.getRight();
		int a_y = a.y;
		int a_t = a.getBottom();
		int b_x = b.x;
		int b_r = b.getRight();
		int b_y = b.y;
		int b_t = b.getBottom();
		int i_x = MathUtils.max(a_x, b_x);
		int i_r = MathUtils.min(a_r, b_r);
		int i_y = MathUtils.max(a_y, b_y);
		int i_t = MathUtils.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectI(i_x, i_y, i_r - i_x, i_t - i_y) : null;
	}

	public static RectI getIntersection(RectI a, RectI b, RectI result) {
		int a_x = a.x;
		int a_r = a.getRight();
		int a_y = a.y;
		int a_t = a.getBottom();
		int b_x = b.x;
		int b_r = b.getRight();
		int b_y = b.y;
		int b_t = b.getBottom();
		int i_x = MathUtils.max(a_x, b_x);
		int i_r = MathUtils.min(a_r, b_r);
		int i_y = MathUtils.max(a_y, b_y);
		int i_t = MathUtils.min(a_t, b_t);
		if (i_x < i_r && i_y < i_t) {
			result.set(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return result;
	}

	public int left() {
		return this.x;
	}

	public int right() {
		return this.x + this.width;
	}

	public int top() {
		return this.y;
	}

	public int bottom() {
		return this.y + this.height;
	}

	public int middleX() {
		return this.x + this.width / 2;
	}

	public int middleY() {
		return this.y + this.height / 2;
	}

	public int centerX() {
		return x + width / 2;
	}

	public int centerY() {
		return y + height / 2;
	}

	public Range getRange() {
		return new Range(this);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isEmpty() {
		return width <= 0 && height <= 0;
	}

	public static void getNearestCorner(int x, int y, int w, int h, int px, int py, PointI result) {
		result.set((int) MathUtils.nearest(px, x, x + w), (int) MathUtils.nearest(y, y, y + h));
	}

	public static boolean getSegmentIntersectionIndices(int x, int y, int w, int h, int x1, int y1, int x2, int y2,
			int ti1, int ti2, PointI ti, PointI n1, PointI n2) {
		int dx = x2 - x1;
		int dy = y2 - y1;

		int nx = 0, ny = 0;
		int nx1 = 0, ny1 = 0, nx2 = 0, ny2 = 0;
		int p, q, r;

		for (int side = 1; side <= 4; side++) {
			switch (side) {
			case 1:
				nx = -1;
				ny = 0;
				p = -dx;
				q = x1 - x;
				break;
			case 2:
				nx = 1;
				ny = 0;
				p = dx;
				q = x + w - x1;
				break;
			case 3:
				nx = 0;
				ny = -1;
				p = -dy;
				q = y1 - y;
				break;
			default:
				nx = 0;
				ny = -1;
				p = dy;
				q = y + h - y1;
				break;
			}

			if (p == 0) {
				if (q <= 0) {
					return false;
				}
			} else {
				r = q / p;
				if (p < 0) {
					if (r > ti2) {
						return false;
					} else if (r > ti1) {
						ti1 = r;
						nx1 = nx;
						ny1 = ny;
					}
				} else {
					if (r < ti1) {
						return false;
					} else if (r < ti2) {
						ti2 = r;
						nx2 = nx;
						ny2 = ny;
					}
				}
			}
		}
		ti.set(ti1, ti2);
		n1.set(nx1, ny1);
		n2.set(nx2, ny2);
		return true;
	}

	public static void getDiff(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2, RectI result) {
		result.set(x2 - x1 - w1, y2 - y1 - h1, w1 + w2, h1 + h2);
	}

	public static boolean containsPoint(int x, int y, int w, int h, int px, int py, int delta) {
		return px - x > delta && py - y > delta && x + w - px > delta && y + h - py > delta;
	}

	public static boolean isIntersecting(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		return x1 < x2 + w2 && x2 < x1 + w1 && y1 < y2 + h2 && y2 < y1 + h1;
	}

	public static int getSquareDistance(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		int dx = x1 - x2 + (w1 - w2) / 2;
		int dy = y1 - y2 + (h1 - h2) / 2;
		return dx * dx + dy * dy;
	}

	public RectI random() {
		this.x = MathUtils.random(0, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0, LSystem.viewSize.getHeight());
		this.width = MathUtils.random(0, LSystem.viewSize.getWidth());
		this.height = MathUtils.random(0, LSystem.viewSize.getHeight());
		return this;
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
		StringKeyValue builder = new StringKeyValue("RectI");
		builder.kv("x", x).comma().kv("y", y).comma().kv("width", width).comma().kv("height", height);
		return builder.toString();
	}
}