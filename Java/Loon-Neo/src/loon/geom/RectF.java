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

import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.StringKeyValue;
import loon.utils.TArray;

/*最简化的浮点体积处理类,以减少对象大小*/
public class RectF implements XYZW, SetXY {

	public static class Range implements XY, SetXY {

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
		public int hashCode() {
			final int prime = 31;
			int hashCode = 1;
			hashCode = prime * LSystem.unite(hashCode, left);
			hashCode = prime * LSystem.unite(hashCode, top);
			hashCode = prime * LSystem.unite(hashCode, right);
			hashCode = prime * LSystem.unite(hashCode, bottom);
			return hashCode;
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
			float xmin = circle.x - circle.boundingCircleRadius;
			float xmax = xmin + 2f * circle.boundingCircleRadius;

			float ymin = circle.y - circle.boundingCircleRadius;
			float ymax = ymin + 2f * circle.boundingCircleRadius;

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

		@Override
		public void setX(float x) {
			this.left = x;
		}

		@Override
		public void setY(float y) {
			this.right = y;
		}
	}

	public float width = 0f;
	public float height = 0f;
	public float x = 0f;
	public float y = 0f;

	public RectF() {
		this(0, 0, 0, 0);
	}

	public RectF(float w, float h) {
		this(0, 0, w, h);
	}

	public RectF(XYZW rect) {
		this(rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public RectF(RectF rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public RectF(Range range) {
		this(range.x(), range.y(), range.width(), range.height());
	}

	public RectF(float x1, float y1, float w1, float h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
	}

	public RectF rotate(float rotate) {
		final int[] rect = MathUtils.getLimit(x, y, width, height, rotate);
		return set(rect[0], rect[1], rect[2], rect[3]);
	}

	public RectF set(RectF r) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
		return this;
	}

	public RectF setEmpty() {
		return set(0f);
	}

	public RectF set(float size) {
		return set(size, size);
	}

	public RectF set(float w1, float h1) {
		return set(this.x, this.y, w1, h1);
	}

	public RectF set(float x1, float y1, float w1, float h1) {
		this.x = x1;
		this.y = y1;
		this.width = w1;
		this.height = h1;
		return this;
	}

	public boolean intersects(float x, float y) {
		return intersects(x, y, 1f, 1f);
	}

	public boolean intersects(XY xy) {
		if (xy == null) {
			return false;
		}
		return intersects(xy.getX(), xy.getY());
	}

	public boolean intersects(float x, float y, float width, float height) {
		return (x >= this.x && y >= this.y && ((x + width) <= (this.x + this.width))
				&& ((y + height) <= (this.y + this.height)));
	}

	public boolean intersects(RectF rect) {
		if (rect == null) {
			return false;
		}
		return intersects(rect.x, rect.y, rect.width, rect.height);
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

	public boolean contains(float xp, float yp) {
		return (xp >= this.getX()) && (yp >= this.getY()) && (xp < this.right()) && (yp < this.bottom());
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

	public RectF cpy() {
		return new RectF(this.x, this.y, this.width, this.height);
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

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	@Override
	public float getZ() {
		return getWidth();
	}

	@Override
	public float getW() {
		return getHeight();
	}

	public boolean isEmpty() {
		return width <= 0 && height <= 0;
	}

	public static void getNearestCorner(float x, float y, float w, float h, float px, float py, PointF result) {
		result.set(MathUtils.nearest(px, x, x + w), MathUtils.nearest(y, y, y + h));
	}

	public static boolean getSegmentIntersectionIndices(float x, float y, float w, float h, float x1, float y1,
			float x2, float y2, float ti1, float ti2, PointF ti, PointF n1, PointF n2) {
		float dx = x2 - x1;
		float dy = y2 - y1;

		float nx = 0, ny = 0;
		float nx1 = 0, ny1 = 0, nx2 = 0, ny2 = 0;
		float p, q, r;

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

	public static void getDiff(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2,
			RectF result) {
		result.set(x2 - x1 - w1, y2 - y1 - h1, w1 + w2, h1 + h2);
	}

	public static boolean containsPoint(float x, float y, float w, float h, float px, float py, float delta) {
		return px - x > delta && py - y > delta && x + w - px > delta && y + h - py > delta;
	}

	public static boolean isIntersecting(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		return x1 < x2 + w2 && x2 < x1 + w1 && y1 < y2 + h2 && y2 < y1 + h1;
	}

	public static float getSquareDistance(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		float dx = x1 - x2 + (w1 - w2) / 2;
		float dy = y1 - y2 + (h1 - h2) / 2;
		return dx * dx + dy * dy;
	}

	public RectF random() {
		this.x = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.y = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.width = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.height = MathUtils.random(0f, LSystem.viewSize.getHeight());
		return this;
	}

	public boolean inPoint(XY pos) {
		if (pos == null) {
			return false;
		}
		return CollisionHelper.checkPointvsAABB(pos.getX(), pos.getY(), this.x, this.y, this.width, this.height);
	}

	public boolean inPoint(float x, float y) {
		return CollisionHelper.checkPointvsAABB(x, y, this.x, this.y, this.width, this.height);
	}

	public boolean inCircle(XYZ cir) {
		if (cir == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsCircle(this.x, this.y, this.width, this.height, cir.getX(), cir.getY(),
				cir.getZ());
	}

	public boolean inCircle(Circle c) {
		if (c == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsCircle(this.x, this.y, this.width, this.height, c.getRealX(), c.getRealY(),
				c.getDiameter());
	}

	public boolean inCircle(float cx, float cy, float d) {
		return CollisionHelper.checkAABBvsCircle(this.x, this.y, this.width, this.height, cx, cy, d);
	}

	public boolean inEllipse(float cx, float cy, float dx, float dy) {
		return CollisionHelper.checkEllipsevsAABB(cx, cy, dx, dy, this.x, this.y, this.width, this.height);
	}

	public boolean inEllipse(Ellipse e) {
		if (e == null) {
			return false;
		}
		return CollisionHelper.checkEllipsevsAABB(e.getRealX(), e.getRealY(), e.getRadius1(), e.getRadius2(), this.x,
				this.y, this.width, this.height);
	}

	public boolean inRect(XYZW rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsAABB(this.x, this.y, this.width, this.height, rect.getX(), rect.getY(),
				rect.getZ(), rect.getW());
	}

	public boolean inRect(RectBox rect) {
		if (rect == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsAABB(this.x, this.y, this.width, this.height, rect.getX(), rect.getY(),
				rect.getWidth(), rect.getHeight());
	}

	public boolean inRect(float rx, float ry, float rw, float rh) {
		return CollisionHelper.checkAABBvsAABB(this.x, this.y, this.width, this.height, rx, ry, rw, rh);
	}

	public boolean inLine(XYZW line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkLinevsAABB(line.getX(), line.getY(), line.getZ(), line.getW(), this.x, this.y,
				this.width, this.height);
	}

	public boolean inLine(Line line) {
		if (line == null) {
			return false;
		}
		return CollisionHelper.checkLinevsAABB(line.getX1(), line.getY1(), line.getX2(), line.getY2(), this.x, this.y,
				this.width, this.height);
	}

	public boolean inLine(float x1, float y1, float x2, float y2) {
		return CollisionHelper.checkLinevsAABB(x1, y1, x2, y2, this.x, this.y, this.width, this.height);
	}

	public boolean inPolygon(Polygon poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsPolygon(this.x, this.y, this.width, this.height, poly.getVertices(), true);
	}

	public <T extends XY> boolean inPolygon(TArray<T> poly) {
		if (poly == null) {
			return false;
		}
		return CollisionHelper.checkAABBvsPolygon(this.x, this.y, this.width, this.height, poly, true);
	}

	public boolean collided(Shape shape) {
		if (shape instanceof Polygon) {
			return inPolygon((Polygon) shape);
		} else if (shape instanceof Line) {
			return inLine((Line) shape);
		} else if (shape instanceof RectBox) {
			return inRect((RectBox) shape);
		} else if (shape instanceof Point) {
			return inPoint((Point) shape);
		} else if (shape instanceof Circle) {
			return inCircle((Circle) shape);
		} else if (shape instanceof Ellipse) {
			return inEllipse((Ellipse) shape);
		}
		return CollisionHelper.checkAABBvsPolygon(x, y, width, height, shape.getVertices(), true);
	}

	public TArray<Vector2f> getAllPoints() {
		TArray<Vector2f> points = new TArray<Vector2f>();
		for (int i = MathUtils.ifloor(x); i <= MathUtils.ifloor(width); i++) {
			for (int j = MathUtils.ifloor(y); j <= MathUtils.ifloor(height); j++) {
				points.add(new Vector2f(i, j));
			}
		}
		return points;
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
		StringKeyValue builder = new StringKeyValue("RectF");
		builder.kv("x", x).comma().kv("y", y).comma().kv("width", width).comma().kv("height", height);
		return builder.toString();
	}

}