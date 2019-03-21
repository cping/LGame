/**
 * 
 * Copyright 2008 - 2015
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

/*最简化的浮点体积处理类,以减少对象大小*/
public class RectF implements XY {

	public static class Range implements XY {

		public float left;

		public float top;

		public float right;

		public float bottom;

		public Range() {
		}

		public Range(RectF rect) {
			this(rect.left(), rect.top(), rect.right(), rect.bottom());
		}

		public Range(float left, float top, float right, float bottom) {
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

		public final float x() {
			return left;
		}

		public final float y() {
			return top;
		}

		public final float width() {
			return right - left;
		}

		public final float height() {
			return bottom - top;
		}

		public final float centerX() {
			return ((int) (left + right)) >> 1;
		}

		public final float centerY() {
			return ((int) (top + bottom)) >> 1;
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

		public void set(float left, float top, float right, float bottom) {
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

		public void offset(float dx, float dy) {
			left += dx;
			top += dy;
			right += dx;
			bottom += dy;
		}

		public void offsetTo(float newLeft, float newTop) {
			right += newLeft - left;
			bottom += newTop - top;
			left = newLeft;
			top = newTop;
		}

		public void inset(float dx, float dy) {
			left += dx;
			top += dy;
			right -= dx;
			bottom -= dy;
		}

		public boolean contains(float x, float y) {
			return left < right && top < bottom && x >= left && x < right && y >= top && y < bottom;
		}

		public boolean contains(Circle circle) {
			float xmin = circle.x - circle.radius;
			float xmax = xmin + 2f * circle.radius;

			float ymin = circle.y - circle.radius;
			float ymax = ymin + 2f * circle.radius;

			return ((xmin > getX() && xmin < getX() + width()) && (xmax > getX() && xmax < getX() + width()))
					&& ((ymin > getY() && ymin < getY() + height()) && (ymax > getY() && ymax < getY() + height()));
		}

		public boolean contains(float left, float top, float right, float bottom) {
			return this.left < this.right && this.top < this.bottom && this.left <= left && this.top <= top
					&& this.right >= right && this.bottom >= bottom;
		}

		public boolean contains(Range r) {
			return this.left < this.right && this.top < this.bottom && left <= r.left && top <= r.top
					&& right >= r.right && bottom >= r.bottom;
		}

		public boolean intersect(float left, float top, float right, float bottom) {
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

		public boolean intersects(float left, float top, float right, float bottom) {
			return this.left < right && left < this.right && this.top < bottom && top < this.bottom;
		}

		public boolean intersects(Range a, Range b) {
			return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;
		}

		public void union(float left, float top, float right, float bottom) {
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

		public void union(float x, float y) {
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
				float temp = left;
				left = right;
				right = temp;
			}
			if (top > bottom) {
				float temp = top;
				top = bottom;
				bottom = temp;
			}
		}

		public void scale(float scale) {
			if (scale != 1.0f) {
				left = (float) (left * scale + 0.5f);
				top = (float) (top * scale + 0.5f);
				right = (float) (right * scale + 0.5f);
				bottom = (float) (bottom * scale + 0.5f);
			}
		}

		public RectF getRect() {
			return new RectF(this);
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

	public float width = 0f;
	public float height = 0f;
	public float x = 0f;
	public float y = 0f;

	public RectF() {

	}

	public RectF(RectF rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public RectF(Range range) {
		this(range.x(), range.y(), range.width(), range.height());
	}

	public RectF set(RectF r) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		return this;
	}

	public RectF set(float x1, float y1, float w1, float h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
		return this;
	}

	public RectF(float x1, float y1, float w1, float h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
	}

	public boolean inside(float x, float y) {
		return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y - this.y) < this.height);
	}

	public float getRight() {
		return this.x + this.width;
	}

	public float getBottom() {
		return this.y + this.height;
	}

	public RectF getIntersection(RectF rect) {
		float x1 = MathUtils.max(x, rect.x);
		float x2 = MathUtils.min(x + width, rect.x + rect.width);
		float y1 = MathUtils.max(y, rect.y);
		float y2 = MathUtils.min(y + height, rect.y + rect.height);
		return new RectF(x1, y1, x2 - x1, y2 - y1);
	}

	public static RectF getIntersection(RectF a, RectF b) {
		float a_x = a.x;
		float a_r = a.getRight();
		float a_y = a.y;
		float a_t = a.getBottom();
		float b_x = b.x;
		float b_r = b.getRight();
		float b_y = b.y;
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		return i_x < i_r && i_y < i_t ? new RectF(i_x, i_y, i_r - i_x, i_t - i_y) : new RectF();
	}

	public static RectF getIntersection(RectF a, RectF b, RectF result) {
		float a_x = a.x;
		float a_r = a.getRight();
		float a_y = a.y;
		float a_t = a.getBottom();
		float b_x = b.x;
		float b_r = b.getRight();
		float b_y = b.y;
		float b_t = b.getBottom();
		float i_x = MathUtils.max(a_x, b_x);
		float i_r = MathUtils.min(a_r, b_r);
		float i_y = MathUtils.max(a_y, b_y);
		float i_t = MathUtils.min(a_t, b_t);
		if (i_x < i_r && i_y < i_t) {
			result.set(i_x, i_y, i_r - i_x, i_t - i_y);
			return result;
		}
		return result;
	}

	public float left() {
		return this.x;
	}

	public float right() {
		return this.x + this.width;
	}

	public float top() {
		return this.y;
	}

	public float bottom() {
		return this.y + this.height;
	}

	public float middleX() {
		return this.x + this.width / 2f;
	}

	public float middleY() {
		return this.y + this.height / 2f;
	}

	public float centerX() {
		return x + width / 2;
	}

	public float centerY() {
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

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}