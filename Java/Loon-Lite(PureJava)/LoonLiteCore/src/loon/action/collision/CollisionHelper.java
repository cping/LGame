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
package loon.action.collision;

import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.BoxSize;
import loon.geom.PointF;
import loon.geom.RangeF;
import loon.geom.RectBox;
import loon.geom.RectF;
import loon.geom.RectI;
import loon.geom.SetXY;
import loon.geom.SetXYZW;
import loon.geom.Shape;
import loon.geom.ShapeUtils;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.geom.XYZ;
import loon.geom.XYZW;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 碰撞事件检测与处理工具类,内部是一系列碰撞检测与处理方法的集合
 */
public final class CollisionHelper extends ShapeUtils {

	final private static int MAX_ITERATIONS = 12;

	private static class CollisionCoef {

		final static float[] innerPolygonCoef = new float[MAX_ITERATIONS + 1];
		final static float[] outerPolygonCoef = new float[MAX_ITERATIONS + 1];

		static {
			for (int t = 0; t <= MAX_ITERATIONS; t++) {
				int numNodes = 4 << t;
				innerPolygonCoef[t] = 0.5f / MathUtils.cos(4f * MathUtils.acos(0f) / numNodes);
				outerPolygonCoef[t] = 0.5f / (MathUtils.cos(2f * MathUtils.acos(0f) / numNodes)
						* MathUtils.cos(2f * MathUtils.acos(0f) / numNodes));
			}
		}
	}

	private static final RectBox rectTemp1 = new RectBox();

	private static final RectBox rectTemp2 = new RectBox();

	private CollisionHelper() {
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
		if (result == null) {
			return;
		}
		result.set(x2 - x1 - w1, y2 - y1 - h1, w1 + w2, h1 + h2);
	}

	public static void getDiff(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2,
			SetXYZW result) {
		if (result == null) {
			return;
		}
		result.setX(x2 - x1 - w1);
		result.setY(y2 - y1 - h1);
		result.setZ(w1 + w2);
		result.setW(h1 + h2);
	}

	public static boolean containsPoint(float x, float y, float w, float h, float px, float py, float delta) {
		return px - x > delta && py - y > delta && x + w - px > delta && y + h - py > delta;
	}

	public static float getSquareDistance(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		float dx = x1 - x2 + (w1 - w2) / 2;
		float dy = y1 - y2 + (h1 - h2) / 2;
		return dx * dx + dy * dy;
	}

	public static boolean isPointInRect(float rectX, float rectY, float rectW, float rectH, float x, float y) {
		return checkPointvsAABB(x, y, rectX, rectY, rectW, rectH);
	}

	public static final boolean intersects(RectBox rect, float x, float y) {
		if (rect == null) {
			return false;
		}
		return checkPointvsAABB(x, y, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	public static final boolean intersects(float rx, float ry, float rw, float rh, float x, float y) {
		return checkPointvsAABB(x, y, rx, ry, rw, rh);
	}

	/**
	 * 检查两个矩形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isRectToRect(BoxSize rect1, BoxSize rect2) {
		if (rect1 == null || rect2 == null) {
			return false;
		}
		return checkAABBvsAABB(rect1.getX(), rect1.getY(), rect1.getWidth(), rect1.getHeight(), rect2.getX(),
				rect2.getY(), rect2.getWidth(), rect2.getHeight());
	}

	/**
	 * 判断两个圆形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isCircToCirc(BoxSize rect1, BoxSize rect2) {
		if (rect1 == null || rect2 == null) {
			return false;
		}
		final PointF middle1 = getMiddlePoint(rect1);
		final PointF middle2 = getMiddlePoint(rect2);
		final float distance = middle1.distanceTo(middle2);
		final float radius1 = rect1.getWidth() / 2;
		final float radius2 = rect2.getWidth() / 2;
		return (distance - radius2) < radius1;
	}

	private static PointF getMiddlePoint(BoxSize rectangle) {
		return new PointF(rectangle.getCenterX(), rectangle.getCenterY());
	}

	private static boolean isPointToLine(XY point1, XY point2, XY middle, float radius) {
		return isPointToLine(point1.getX(), point1.getY(), point2.getX(), point2.getY(), middle.getX(), middle.getY(),
				radius);
	}

	private static boolean isPointToLine(float px1, float py1, float px2, float py2, float middleX, float middleY,
			float radius) {
		float distance = ptLineDist(px1, py1, px2, py2, middleX, middleY);
		return distance < radius;
	}

	/**
	 * 检查矩形与圆形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isRectToCirc(BoxSize rect1, BoxSize rect2) {
		if (rect1 == null || rect2 == null) {
			return false;
		}
		final float radius = rect2.getWidth() / 2;
		final float minX = rect1.getX();
		final float minY = rect1.getY();
		final float maxX = rect1.getX() + rect1.getWidth();
		final float maxY = rect1.getY() + rect1.getHeight();
		final PointF middle = getMiddlePoint(rect2);
		final PointF upperLeft = new PointF(minX, minY);
		final PointF upperRight = new PointF(maxX, minY);
		final PointF downLeft = new PointF(minX, maxY);
		final PointF downRight = new PointF(maxX, maxY);
		boolean collided = true;
		if (!isPointToLine(upperLeft, upperRight, middle, radius)) {
			if (!isPointToLine(upperRight, downRight, middle, radius)) {
				if (!isPointToLine(upperLeft, downLeft, middle, radius)) {
					if (!isPointToLine(downLeft, downRight, middle, radius)) {
						collided = false;
					}
				}
			}
		}
		return collided;
	}

	/**
	 * 判断指定大小的两组像素是否相交
	 * 
	 * @param rectA
	 * @param dataA
	 * @param rectB
	 * @param dataB
	 * @return
	 */
	public static boolean intersects(RectBox rectA, int[] dataA, RectBox rectB, int[] dataB) {
		if (rectA == null || rectB == null) {
			return false;
		}
		int top = MathUtils.ifloor(MathUtils.max(rectA.getY(), rectB.getY()));
		int bottom = MathUtils.ifloor(MathUtils.min(rectA.getBottom(), rectB.getBottom()));
		int left = MathUtils.ifloor(MathUtils.max(rectA.getX(), rectB.getX()));
		int right = MathUtils.ifloor(MathUtils.min(rectA.getRight(), rectB.getRight()));

		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {

				int colorA = dataA[MathUtils.ifloor((x - rectA.x) + (y - rectA.y) * rectA.width)];
				int colorB = dataB[MathUtils.ifloor((x - rectB.x) + (y - rectB.y) * rectB.width)];
				if (colorA >>> 24 != 0 && colorB >>> 24 != 0) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 判断两个Shape是否相交
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static final boolean intersects(Shape s1, Shape s2) {
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.intersects(s2);
	}

	public static final int[] intersects(RectBox rect1, RectBox rect2) {
		if (rect1 == null || rect2 == null) {
			return null;
		}
		if (rect1.Left() < rect2.Right() && rect2.Left() < rect1.Right() && rect1.Top() < rect2.Bottom()
				&& rect2.Top() < rect1.Bottom()) {
			return new int[] { rect1.Left() < rect2.Left() ? rect2.Left() - rect1.Left() : 0,
					rect1.Top() < rect2.Top() ? rect2.Top() - rect1.Top() : 0,
					rect1.Right() > rect2.Right() ? rect1.Right() - rect2.Right() : 0,
					rect1.Bottom() > rect2.Bottom() ? rect1.Bottom() - rect2.Bottom() : 0 };
		}
		return null;
	}

	public static final boolean intersects(float x, float y, float width, float height, float dx, float dy, float dw,
			float dh) {
		return checkAABBvsAABB(x, y, width, height, dx, dy, dw, dh);
	}

	public static final boolean intersects(float x, float y, float width, float height, float dx, float dy, float dw,
			float dh, boolean touchingIsIn) {
		rectTemp1.setBounds(x, y, width, height).normalize();
		rectTemp2.setBounds(dx, dy, dw, dh).normalize();
		if (touchingIsIn) {
			if (MathUtils.equal(rectTemp1.x + rectTemp1.width, rectTemp2.x)) {
				return true;
			}
			if (MathUtils.equal(rectTemp1.x, rectTemp2.x + rectTemp2.width)) {
				return true;
			}
			if (MathUtils.equal(rectTemp1.y + rectTemp1.height, rectTemp2.y)) {
				return true;
			}
			if (MathUtils.equal(rectTemp1.y, rectTemp2.y + rectTemp2.height)) {
				return true;
			}
		}
		return rectTemp1.intersects(rectTemp2);
	}

	/**
	 * 判定指定的两张图片之间是否产生了碰撞
	 * 
	 * @param src
	 * @param x1
	 * @param y1
	 * @param dest
	 * @param x2
	 * @param y2
	 * @return
	 */
	public boolean isPixelCollide(Image src, float x1, float y1, Image dest, float x2, float y2) {
		final float width1 = x1 + src.width() - 1, height1 = y1 + src.height() - 1, width2 = x2 + dest.width() - 1,
				height2 = y2 + dest.height() - 1;

		final int xstart = MathUtils.ifloor(MathUtils.max(x1, x2)), ystart = MathUtils.ifloor(MathUtils.max(y1, y2)),
				xend = MathUtils.ifloor(MathUtils.min(width1, width2)),
				yend = MathUtils.ifloor(MathUtils.min(height1, height2));

		final int toty = MathUtils.abs(yend - ystart);
		final int totx = MathUtils.abs(xend - xstart);

		for (int y = 1; y < toty - 1; y++) {
			int ny = MathUtils.abs(ystart - MathUtils.ifloor(y1) + y);
			int ny1 = MathUtils.abs(ystart - MathUtils.ifloor(y2) + y);

			for (int x = 1; x < totx - 1; x++) {
				int nx = MathUtils.abs(xstart - MathUtils.ifloor(x1) + x);
				int nx1 = MathUtils.abs(xstart - MathUtils.ifloor(x2) + x);

				try {
					if (((src.getPixel(nx, ny) & LColor.TRANSPARENT) != 0x00)
							&& ((dest.getPixel(nx1, ny1) & LColor.TRANSPARENT) != 0x00)) {
						return true;
					} else if (getPixelData(src, nx, ny)[0] != 0 && getPixelData(dest, nx1, ny1)[0] != 0) {
						return true;
					}
				} catch (Throwable e) {

				}
			}
		}
		return false;
	}

	private static final int[] getPixelData(Image image, int x, int y) {
		return LColor.getRGBs(image.getPixel(x, y));
	}

	/**
	 * 判断两个Shape是否存在包含关系
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static final boolean contains(Shape s1, Shape s2) {
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.contains(s2);
	}

	public static final boolean contains(float sx, float sy, float sw, float sh, float dx, float dy, float dw,
			float dh) {
		return (dx >= sx && dy >= sy && ((dx + dw) <= (sx + sw)) && ((dy + dh) <= (sy + sh)));
	}

	public static final boolean contains(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
		return (dx >= sx && dy >= sy && ((dx + dw) <= (sx + sw)) && ((dy + dh) <= (sy + sh)));
	}

	public static final boolean containsIsometric(int x, int y, int w, int h, int px, int py) {
		float mx = w / 2;
		float my = h / 2;
		float ix = px - x;
		float iy = py - y;
		if (iy > my) {
			iy = my - (iy - my);
		}
		if ((ix > mx + 1 + (2 * iy)) || (ix < mx - 1 - (2 * iy))) {
			return false;
		}
		return true;
	}

	public static final boolean containsHexagon(int x, int y, int w, int h, int px, int py) {
		float mx = w / 4;
		float my = h / 2;
		float hx = px - x;
		float hy = py - y;
		if (hx > mx * 3) {
			hx = mx - (hx - mx * 3);
		} else if (hx > mx) {
			return py >= y && py <= y + h;
		}
		if ((hy > my + 1 + (2 * hx)) || (hy < my - 1 - (2 * hx))) {
			return false;
		}
		return true;
	}

	public static final RectBox constructRect(Vector2f topLeft, Vector2f bottomRight) {
		if (topLeft == null || bottomRight == null) {
			return null;
		}
		return new RectBox(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
	}

	public static final RectBox constructRect(Vector2f pos, Vector2f size, Vector2f alignement) {
		if (pos == null || size == null || alignement == null) {
			return null;
		}
		Vector2f offset = size.mul(alignement);
		Vector2f topLeft = pos.sub(offset);
		return new RectBox(topLeft.x, topLeft.y, size.x, size.y);
	}

	public static final Object[] collideField(RectBox rect, Vector2f pos, float radius) {
		if (rect == null || pos == null) {
			return null;
		}
		boolean collided = false;
		Vector2f hitPoint = pos;
		Vector2f result = new Vector2f();
		Vector2f newPos = pos;
		if (pos.x + radius > rect.x + rect.width) {
			hitPoint = new Vector2f(rect.x + rect.width, pos.y);
			newPos.x = hitPoint.x - radius;
			result = new Vector2f(-1f, 0f);
			collided = true;
		} else if (pos.x - radius < rect.x) {
			hitPoint = new Vector2f(rect.x, pos.y);
			newPos.x = hitPoint.x + radius;
			result = new Vector2f(1f, 0f);
			collided = true;
		}

		if (pos.y + radius > rect.y + rect.height) {
			hitPoint = new Vector2f(pos.x, rect.y + rect.height);
			newPos.y = hitPoint.y - radius;
			result = new Vector2f(0f, -1f);
			collided = true;
		} else if (pos.y - radius < rect.y) {
			hitPoint = new Vector2f(pos.x, rect.y);
			newPos.y = hitPoint.y + radius;
			result = new Vector2f(0f, 1f);
			collided = true;
		}

		return new Object[] { collided, hitPoint, result, newPos };
	}

	public static final Object[] collideAroundField(RectBox rect, Vector2f pos, float radius) {
		if (rect == null || pos == null) {
			return null;
		}
		boolean outOfBounds = false;
		Vector2f newPos = pos;
		if (pos.x + radius > rect.x + rect.width) {
			newPos = new Vector2f(rect.x, pos.y);
			outOfBounds = true;
		} else if (pos.x - radius < rect.x) {
			newPos = new Vector2f(rect.x + rect.width, pos.y);
			outOfBounds = true;
		}
		if (pos.y + radius > rect.y + rect.height) {
			newPos = new Vector2f(pos.x, rect.y);
			outOfBounds = true;
		} else if (pos.y - radius < rect.y) {
			newPos = new Vector2f(pos.x, rect.y + rect.height);
			outOfBounds = true;
		}
		return new Object[] { outOfBounds, newPos };
	}

	public static final boolean checkOverlappingRange(float minA, float maxA, float minB, float maxB) {
		if (maxA < minA) {
			float temp = minA;
			minA = maxA;
			maxA = temp;
		}
		if (maxB < minB) {
			float temp = minB;
			minB = maxB;
			maxB = temp;
		}
		return minB <= maxA && minA <= maxB;
	}

	public static final boolean checkOverlappingRange(RangeF a, RangeF b) {
		return checkOverlappingRange(a.getMin(), a.getMax(), b.getMin(), b.getMax());
	}

	public static final boolean checkAABBvsAABB(XY p1, float w1, float h1, XY p2, float w2, float h2) {
		return checkAABBvsAABB(p1.getX(), p1.getY(), w1, h1, p2.getX(), p2.getY(), w2, h2);
	}

	public static final boolean checkAABBvsAABB(XYZW rect1, XYZW rect2) {
		return checkAABBvsAABB(rect1.getX(), rect1.getY(), rect1.getZ(), rect1.getW(), rect2.getX(), rect2.getY(),
				rect2.getZ(), rect2.getW());
	}

	public static final boolean checkAABBvsAABB(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
	}

	public static final boolean checkAABBvsAABB(XY p1Min, XY p1Max, XY p2Min, XY p2Max) {
		return checkAABBvsAABB(p1Min.getX(), p1Min.getY(), p1Max.getX() - p1Min.getX(), p1Max.getY() - p1Min.getY(),
				p2Min.getX(), p2Min.getY(), p2Max.getX() - p2Min.getX(), p2Max.getY() - p2Min.getY());
	}

	public static final boolean checkAABBvsAABB(XYZ p1, float w1, float h1, float t1, XYZ p2, float w2, float h2,
			float t2) {
		return checkAABBvsAABB(p1.getX(), p1.getY(), p1.getZ(), w1, h1, t1, p2.getX(), p2.getY(), p2.getZ(), w2, h2,
				t2);
	}

	public static final boolean checkAABBvsAABB(float x1, float y1, float z1, float w1, float h1, float t1, float x2,
			float y2, float z2, float w2, float h2, float t2) {
		return !(x1 > x2 + w2 || x1 + w1 < x2) && !(y1 > y2 + h2 || y1 + h1 < y2) && !(z1 > z2 + t2 || z1 + t1 < z2);
	}

	public static final boolean checkAABBvsAABB(XYZ p1Min, XYZ p1Max, XYZ p2Min, XYZ p2Max) {
		return checkAABBvsAABB(p1Min.getX(), p1Min.getY(), p1Min.getZ(), p1Max.getX() - p1Min.getX(),
				p1Max.getY() - p1Min.getY(), p1Max.getZ() - p1Min.getZ(), p2Min.getX(), p2Min.getY(), p1Min.getZ(),
				p2Max.getX() - p2Min.getX(), p2Max.getY() - p2Min.getY(), p2Max.getZ() - p2Min.getZ());
	}

	public static final boolean checkAABBvsCircle(XYZW rect, XY pos, float diameter) {
		return checkAABBvsCircle(rect, pos.getX(), pos.getY(), diameter);
	}

	public static final boolean checkAABBvsCircle(XYZW rect, float cx, float cy, float diameter) {
		return checkAABBvsCircle(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), cx, cy, diameter);
	}

	public static final boolean checkAABBvsCircle(XYZW rect, XYZ c) {
		return checkAABBvsCircle(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), c.getX(), c.getY(), c.getZ());
	}

	public static final boolean checkAABBvsCircle(float rx, float ry, float rw, float rh, float cx, float cy,
			float diameter) {
		float newX = cx;
		float newY = cy;
		if (cx < rx) {
			newX = rx;
		} else if (cx > rx + rw) {
			newX = rx + rw;
		}
		if (cy < ry) {
			newY = ry;
		} else if (cy > ry + rh) {
			newY = ry + rh;
		}
		float distance = MathUtils.dist(cx, cy, newX, newY);
		if (distance <= diameter / 2) {
			return true;
		}
		return false;
	}

	public static final <T extends XY> boolean checkAABBvsPolygon(XYZW rect, TArray<T> vertices) {
		return checkAABBvsPolygon(rect.getX(), rect.getY(), rect.getZ(), rect.getW(), vertices, true);
	}

	public static final <T extends XY> boolean checkAABBvsPolygon(float rx, float ry, float rw, float rh,
			TArray<T> vertices) {
		return checkAABBvsPolygon(rx, ry, rw, rh, vertices, true);
	}

	public static final <T extends XY> boolean checkAABBvsPolygon(float rx, float ry, float rw, float rh,
			TArray<T> vertices, boolean inside) {
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			T vc = vertices.get(current);
			T vn = vertices.get(next);
			final boolean collision = checkLinevsAABB(vc.getX(), vc.getY(), vn.getX(), vn.getY(), rx, ry, rw, rh);
			if (collision) {
				return true;
			}
			if (inside) {
				boolean checkCollide = checkPointvsPolygon(rx, ry, vertices);
				if (checkCollide) {
					return true;
				}
			}
		}
		return false;
	}

	public static final boolean checkCirclevsCircle(XY p1, float r1, XY p2, float r2) {
		return checkCirclevsCircle(p1.getX(), p1.getY(), r1, p2.getX(), p2.getY(), r2);
	}

	public static final boolean checkCirclevsCircle(XYZ circle1, XYZ circle2) {
		return checkCirclevsCircle(circle1.getX(), circle1.getY(), circle1.getZ(), circle2.getX(), circle2.getY(),
				circle2.getZ());
	}

	public static final boolean checkCirclevsCircle(float x1, float y1, float r1, float x2, float y2, float r2) {
		float distance = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		float radiusSumSq = (r1 + r2) * (r1 + r2);
		return distance <= radiusSumSq;
	}

	public static final <T extends XY> boolean checkCirclevsPolygon(XYZ cir, TArray<T> vertices) {
		return checkCirclevsPolygon(cir.getX(), cir.getY(), cir.getZ(), vertices, false);
	}

	public static final <T extends XY> boolean checkCirclevsPolygon(float cx, float cy, float diameter,
			TArray<T> vertices) {
		return checkCirclevsPolygon(cx, cy, diameter, vertices, false);
	}

	public static final <T extends XY> boolean checkCirclevsPolygon(float cx, float cy, float diameter,
			TArray<T> vertices, boolean inside) {
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			T vc = vertices.get(current);
			T vn = vertices.get(next);
			final boolean collision = checkLinevsCircle(vc.getX(), vc.getY(), vn.getX(), vn.getY(), cx, cy, diameter);
			if (collision) {
				return true;
			}
		}
		if (inside) {
			boolean centerInside = checkPointvsPolygon(cx, cy, vertices);
			if (centerInside) {
				return true;
			}
		}
		return false;
	}

	public static final boolean checkEllipsevsLine(XYZW ellipse, XYZW line) {
		return checkLinevsEllipse(line.getX(), line.getY(), line.getZ(), line.getW(), ellipse.getX(), ellipse.getY(),
				ellipse.getZ(), ellipse.getW());
	}

	public static boolean checkEllipsevsLine(float x1, float y1, float x2, float y2, float cx, float cy, float dx,
			float dy) {
		return checkLinevsEllipse(x1, y1, x2, y2, cx, cy, dx, dy);
	}

	public static final boolean checkEllipsevsAABB(XYZW ellipse, XYZW rect) {
		return checkEllipsevsAABB(ellipse.getX(), ellipse.getY(), ellipse.getZ(), ellipse.getW(), rect.getX(),
				rect.getY(), rect.getZ(), rect.getW());
	}

	public static final boolean checkEllipsevsAABB(XYZW ellipse, XY p1, XY p2) {
		return checkEllipsevsAABB(ellipse.getX(), ellipse.getY(), ellipse.getZ(), ellipse.getW(), p1.getX(), p1.getY(),
				p2.getX(), p2.getY());
	}

	public static final boolean checkEllipsevsAABB(float cx, float cy, float dx, float dy, float ax, float ay, float aw,
			float ah) {
		final boolean pointed = checkPointvsAABB(cx, cy, ax, ay, aw, ah);
		final boolean right = checkLinevsEllipse(ax, ay, ax + aw, ay, cx, cy, dx, dy);
		final boolean bottom = checkLinevsEllipse(ax, ay + ah, ax + aw, ay + ah, cx, cy, dx, dy);
		final boolean left = checkLinevsEllipse(ax, ay, ax, ay + ah, cx, cy, dx, dy);
		final boolean top = checkLinevsEllipse(ax + aw, ay, ax + aw, ay + ah, cx, cy, dx, dy);
		return pointed || right || bottom || left || top;
	}

	public static final boolean checkEllipsevsPoint(float cx, float cy, float dx, float dy, float px, float py) {
		return checkPointvsEllipse(px, py, cx, cy, dx, dy);
	}

	private static boolean iterate(float x, float y, float c0x, float c0y, float c2x, float c2y, float rr) {
		for (int t = 1; t <= MAX_ITERATIONS; t++) {
			float c1x = (c0x + c2x) * CollisionCoef.innerPolygonCoef[t];
			float c1y = (c0y + c2y) * CollisionCoef.innerPolygonCoef[t];
			float tx = x - c1x;
			float ty = y - c1y;
			if (tx * tx + ty * ty <= rr) {
				return true;
			}
			float t2x = c2x - c1x;
			float t2y = c2y - c1y;
			if (tx * t2x + ty * t2y >= 0 && tx * t2x + ty * t2y <= t2x * t2x + t2y * t2y && (ty * t2x - tx * t2y >= 0
					|| rr * (t2x * t2x + t2y * t2y) >= (ty * t2x - tx * t2y) * (ty * t2x - tx * t2y))) {
				return true;
			}
			float t0x = c0x - c1x;
			float t0y = c0y - c1y;
			if (tx * t0x + ty * t0y >= 0 && tx * t0x + ty * t0y <= t0x * t0x + t0y * t0y && (ty * t0x - tx * t0y <= 0
					|| rr * (t0x * t0x + t0y * t0y) >= (ty * t0x - tx * t0y) * (ty * t0x - tx * t0y))) {
				return true;
			}
			float c3x = (c0x + c1x) * CollisionCoef.outerPolygonCoef[t];
			float c3y = (c0y + c1y) * CollisionCoef.outerPolygonCoef[t];
			if ((c3x - x) * (c3x - x) + (c3y - y) * (c3y - y) < rr) {
				c2x = c1x;
				c2y = c1y;
				continue;
			}
			float c4x = c1x - c3x + c1x;
			float c4y = c1y - c3y + c1y;
			if ((c4x - x) * (c4x - x) + (c4y - y) * (c4y - y) < rr) {
				c0x = c1x;
				c0y = c1y;
				continue;
			}
			float t3x = c3x - c1x;
			float t3y = c3y - c1y;
			if (ty * t3x - tx * t3y <= 0
					|| rr * (t3x * t3x + t3y * t3y) > (ty * t3x - tx * t3y) * (ty * t3x - tx * t3y)) {
				if (tx * t3x + ty * t3y > 0) {
					if (MathUtils.abs(tx * t3x + ty * t3y) <= t3x * t3x + t3y * t3y
							|| (x - c3x) * (c0x - c3x) + (y - c3y) * (c0y - c3y) >= 0) {
						c2x = c1x;
						c2y = c1y;
						continue;
					}
				} else if (-(tx * t3x + ty * t3y) <= t3x * t3x + t3y * t3y
						|| (x - c4x) * (c2x - c4x) + (y - c4y) * (c2y - c4y) >= 0) {
					c0x = c1x;
					c0y = c1y;
					continue;
				}
			}
			return false;
		}
		return false;
	}

	public static final boolean checkEllipsevsEllipse(XYZW e1, XYZW e2) {
		return checkEllipsevsEllipse(e1.getX(), e1.getY(), e1.getZ(), e1.getW(), e2.getX(), e2.getY(), e2.getZ(),
				e2.getW());
	}

	public static final boolean checkEllipsevsEllipse(float x0, float y0, float w0, float h0, float x1, float y1,
			float w1, float h1) {
		final float x = MathUtils.abs(x1 - x0) * h1;
		final float y = MathUtils.abs(y1 - y0) * w1;
		w0 *= h1;
		h0 *= w1;
		final float r = w1 * h1;
		if (x * x + (h0 - y) * (h0 - y) <= r * r || (w0 - x) * (w0 - x) + y * y <= r * r || x * h0 + y * w0 <= w0 * h0
				|| ((x * h0 + y * w0 - w0 * h0) * (x * h0 + y * w0 - w0 * h0) <= r * r * (w0 * w0 + h0 * h0)
						&& x * w0 - y * h0 >= -h0 * h0 && x * w0 - y * h0 <= w0 * w0)) {
			return true;
		} else {
			if ((x - w0) * (x - w0) + (y - h0) * (y - h0) <= r * r || (x <= w0 && y - r <= h0)
					|| (y <= h0 && x - r <= w0)) {
				return iterate(x, y, w0, 0, 0, h0, r * r);
			}
			return false;
		}
	}

	public static final boolean checkEllipsevsCircle(XYZW e, XY pos, float r) {
		return checkEllipsevsCircle(e, pos.getX(), pos.getY(), r);
	}

	public static final boolean checkEllipsevsCircle(XYZW e, float x1, float y1, float r) {
		return checkEllipsevsCircle(e.getX(), e.getY(), e.getZ(), e.getW(), x1, y1, r);
	}

	public static final boolean checkEllipsevsCircle(float x0, float y0, float w, float h, float rx, float ry,
			float r) {
		final float x = MathUtils.abs(rx - x0);
		final float y = MathUtils.abs(ry - y0);
		if (x * x + (h - y) * (h - y) <= r * r || (w - x) * (w - x) + y * y <= r * r || x * h + y * w <= w * h
				|| ((x * h + y * w - w * h) * (x * h + y * w - w * h) <= r * r * (w * w + h * h)
						&& x * w - y * h >= -h * h && x * w - y * h <= w * w)) {
			return true;
		} else {
			if ((x - w) * (x - w) + (y - h) * (y - h) <= r * r || (x <= w && y - r <= h) || (y <= h && x - r <= w)) {
				return iterate(x, y, w, 0, 0, h, r * r);
			}
			return false;
		}
	}

	public static final boolean checkSpherevsSphere(XYZ p1, float r1, XYZ p2, float r2) {
		return checkSpherevsSphere(p1.getX(), p1.getY(), p1.getZ(), r1, p2.getX(), p2.getY(), p2.getZ(), r2);
	}

	public static final boolean checkSpherevsSphere(float x1, float y1, float z1, float r1, float x2, float y2,
			float z2, float r2) {
		float distance = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1);
		float radiusSumSq = (r1 + r2) * (r1 + r2);
		return distance <= radiusSumSq;
	}

	public static final boolean checkPointvsCircle(XY pos, XYZ cir) {
		return checkPointvsCircle(pos.getX(), pos.getY(), cir.getX(), cir.getY(), cir.getZ());
	}

	public static final boolean checkPointvsCircle(float x, float y, XYZ cir) {
		return checkPointvsCircle(x, y, cir.getX(), cir.getY(), cir.getZ());
	}

	public static final boolean checkPointvsCircle(XY pos, XY cir, float d) {
		return checkPointvsCircle(pos.getX(), pos.getY(), cir.getX(), cir.getY(), d);
	}

	public static final boolean checkPointvsCircle(float x, float y, float cx, float cy, float d) {
		return MathUtils.dist(x, y, cx, cy) <= d / 2f;
	}

	public static final boolean checkPointvsEllipse(XY xy, XYZW e) {
		return checkPointvsEllipse(xy.getX(), xy.getY(), e.getX(), e.getY(), e.getZ(), e.getW());
	}

	public static final boolean checkPointvsEllipse(float x, float y, XYZW e) {
		return checkPointvsEllipse(x, y, e.getX(), e.getY(), e.getZ(), e.getW());
	}

	public static final boolean checkPointvsEllipse(float x, float y, float cx, float cy, float dx, float dy) {
		final float rx = dx / 2f;
		final float ry = dy / 2f;
		if (x > cx + rx || x < cx - rx || y > cy + ry || y < cy - ry) {
			return false;
		}
		final float newX = x - cx;
		final float newY = y - cy;
		final float over = ry * MathUtils.sqrt(MathUtils.abs(rx * rx - newX * newX)) / rx;
		return newY <= over && newY >= -over;
	}

	public static final boolean checkPointvsAABB(float pointX, float pointY, XYZW rect) {
		return checkPointvsAABB(pointX, pointY, rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public static final boolean checkPointvsAABB(XY pos, XYZW rect) {
		return checkPointvsAABB(pos.getX(), pos.getY(), rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public static final boolean checkPointvsAABB(float pointX, float pointY, float x, float y, float z, float w) {
		return pointX >= x && pointX <= x + z && pointY >= y && pointY <= y + w;
	}

	public static final boolean checkPointvsLine(XY pos, XY p1, XY p2, float offset) {
		return checkPointvsLine(pos.getX(), pos.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), offset);
	}

	public static final boolean checkPointvsLine(XY pos, XY p1, XY p2) {
		return checkPointvsLine(pos, p1, p2, 0.1f);
	}

	public static final boolean checkPointvsLine(XY pos, XYZW p, float offset) {
		return checkPointvsLine(pos.getX(), pos.getY(), p.getX(), p.getY(), p.getZ(), p.getW(), offset);
	}

	public static final boolean checkPointvsLine(XY pos, XYZW p) {
		return checkPointvsLine(pos, p, 1f);
	}

	public static final boolean checkPointvsLine(float px, float py, float x1, float y1, float x2, float y2) {
		return checkPointvsLine(px, py, x1, y1, x2, y2, 1f);
	}

	public static final boolean checkPointvsLine(float px, float py, float x1, float y1, float x2, float y2,
			float size) {
		final float mpx = px - x1;
		final float mpy = py - y1;
		final float d1 = MathUtils.dist(mpx, mpy, x1, y1);
		final float d2 = MathUtils.dist(mpx, mpy, x2, y2);
		final float lineLen = MathUtils.dist(x1, y1, x2, y2);
		return d1 + d2 >= lineLen - size && d1 + d2 <= lineLen + size;
	}

	public static final boolean checkPointvsTriangle(XY pos, XY p1, XY p2, XY p3) {
		return checkPointvsTriangle(pos.getX(), pos.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(),
				p3.getY());
	}

	public static final boolean checkPointvsTriangle(float px, float py, float x1, float y1, float x2, float y2,
			float x3, float y3) {
		float areaOrig = MathUtils.abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
		float area1 = MathUtils.abs((x1 - px) * (y2 - py) - (x2 - px) * (y1 - py));
		float area2 = MathUtils.abs((x2 - px) * (y3 - py) - (x3 - px) * (y2 - py));
		float area3 = MathUtils.abs((x3 - px) * (y1 - py) - (x1 - px) * (y3 - py));
		return MathUtils.equal(area1 + area2 + area3, areaOrig);
	}

	public static final <T extends XY> boolean checkPointvsPolygon(XY pos, TArray<T> vertices) {
		return checkPointvsPolygon(pos.getX(), pos.getY(), vertices);
	}

	public static final <T extends XY> boolean checkPointvsPolygon(float px, float py, TArray<T> vertices) {
		boolean collision = false;
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			T vc = vertices.get(current);
			T vn = vertices.get(next);
			if (((vc.getY() >= py && vn.getY() < py) || (vc.getY() < py && vn.getY() >= py))
					&& (px < (vn.getX() - vc.getX()) * (py - vc.getY()) / (vn.getY() - vc.getY()) + vc.getX())) {
				collision = !collision;
			}
		}
		return collision;
	}

	public static final boolean checkPointvsPolygon(XY pos, float[] vertices) {
		return checkPointvsPolygon(pos, vertices, 1f);
	}

	public static final boolean checkPointvsPolygon(XY pos, float[] vertices, float size) {
		return checkPointvsPolygon(pos.getX(), pos.getY(), vertices, size);
	}

	public static final boolean checkPointvsPolygon(float px, float py, float[] vertices, float size) {
		final int length = vertices.length;
		boolean collision = false;
		int i, j;
		for (i = 0, j = length - 2; i < length; i += 2) {
			if (((vertices[i + 1] > py) != (vertices[j + 1] > py))
					&& (px < (vertices[j] - vertices[i]) * (py - vertices[i + 1]) / (vertices[j + 1] - vertices[i + 1])
							+ vertices[i])) {
				collision = !collision;
			}
			j = i;
		}
		if (collision) {
			return true;
		}
		for (i = 0; i < length; i += 2) {
			float p1x = vertices[i];
			float p1y = vertices[i + 1];
			float p2x, p2y;
			if (i == length - 2) {
				p2x = vertices[0];
				p2y = vertices[1];
			} else {
				p2x = vertices[i + 2];
				p2y = vertices[i + 3];
			}
			if (checkPointvsLine(px, py, p1x, p1y, p2x, p2y, size)) {
				return true;
			}
		}
		return false;
	}

	public static final boolean checkPointvsArc(float px, float py, float ax, float ay, float arcRadius,
			float arcHeading, float arcAngle) {
		return checkPointvsArc(px, py, ax, ay, arcRadius, arcHeading, arcAngle, 0f);
	}

	public static final boolean checkPointvsArc(float px, float py, float ax, float ay, float arcRadius,
			float arcHeading, float arcAngle, float offset) {
		final Vector2f point = Vector2f.at(px, py);
		final Vector2f arcPos = Vector2f.at(ax, ay);
		final Vector2f radius = Vector2f.at(arcRadius, 0).rotate(arcHeading);
		final Vector2f pointToArc = point.sub(arcPos);
		if (point.distance(arcPos) <= (arcRadius + offset)) {
			final float dot = radius.dot(pointToArc);
			final float angle = radius.angleRad(pointToArc);
			if (dot > 0 && angle <= arcAngle / 2f && angle >= -arcAngle / 2f) {
				return true;
			}
		}
		return false;
	}

	public static final boolean checkLinevsCircle(XYZW xyzw, float cx, float cy, float diameter) {
		return checkLinevsCircle(xyzw.getX(), xyzw.getY(), xyzw.getZ(), xyzw.getW(), cx, cy, diameter);
	}

	public static final boolean checkLinevsCircle(XY p1, XY p2, XY p3, float diameter) {
		return checkLinevsCircle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), diameter);
	}

	public static final boolean checkLinevsCircle(XY p1, XY p2, float cx, float cy, float diameter) {
		return checkLinevsCircle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), cx, cy, diameter);
	}

	public static final boolean checkLinevsCircle(XYZW xyzw, XYZ cir) {
		return checkLinevsCircle(xyzw, cir.getX(), cir.getY(), cir.getZ());
	}

	public static final boolean checkLinevsCircle(float x1, float y1, float x2, float y2, float cx, float cy,
			float diameter) {
		final boolean result1 = checkPointvsCircle(x1, y1, cx, cy, diameter);
		final boolean result2 = checkPointvsCircle(x2, y2, cx, cy, diameter);
		if (result1 || result2) {
			return true;
		}
		final Vector2f distX = Vector2f.at(cx - x1, cy - y1);
		final Vector2f distY = Vector2f.at(x2 - x1, y2 - y1);
		final float dota = distY.dot(distY);
		final float dotb = distX.dot(distY);
		float t = dotb / dota;
		t = (t < 0) ? 0 : t;
		t = (t > 1) ? 1 : t;
		final Vector2f dist = Vector2f.at((distY.x * t + x1) - cx, (distY.y * t + y1) - cy);
		final float len = dist.dot(dist);
		final float r = diameter / 2f;
		return len <= r * r;
	}

	public static boolean checkLinevsEllipse(XYZW line, XYZW ellipse) {
		return checkLinevsEllipse(line.getX(), line.getY(), line.getZ(), line.getW(), ellipse.getX(), ellipse.getY(),
				ellipse.getZ(), ellipse.getW());
	}

	public static boolean checkLinevsEllipse(float x1, float y1, float x2, float y2, XYZW ellipse) {
		return checkLinevsEllipse(x1, y1, x2, y2, ellipse.getX(), ellipse.getY(), ellipse.getZ(), ellipse.getW());
	}

	public static boolean checkLinevsEllipse(float x1, float y1, float x2, float y2, float cx, float cy, float d1,
			float d2) {
		final float dx = d1 / 2f;
		final float dy = d1 / 2f;
		final float nx1 = x1 - cx;
		final float nx2 = x2 - cx;
		final float ny1 = y1 - cy;
		final float ny2 = y2 - cy;
		final float len = MathUtils.pow(nx2 - nx1, 2f) / dx / dx + MathUtils.pow(ny2 - ny1, 2f) / dy / dy;
		final float uB = nx1 * 2f * (nx2 - nx1) / dx / dx + 2f * ny1 * (ny2 - ny1) / dy / dy;
		final float uC = nx1 * nx1 / dx / dx + ny1 * ny1 / dy / dy - 1f;
		final float distance = uB * uB - 4f * len * uC;
		if (distance == 0f) {
			final float t = -uB / 2f / len;
			return t >= 0 && t <= 1;
		} else if (distance > 0f) {
			final float sqrt = MathUtils.sqrt(distance);
			final float t1 = (-uB + sqrt) / 2f / len;
			final float t2 = (-uB - sqrt) / 2f / len;
			return (t1 >= 0 && t1 <= 1f) || (t2 >= 0 && t2 <= 1f);
		} else {
			return false;
		}
	}

	public static final boolean checkLinevsLine(XYZW l1, XYZW l2) {
		return checkLinevsLine(l1.getX(), l1.getY(), l1.getZ(), l1.getW(), l2.getX(), l2.getY(), l2.getZ(), l2.getW());
	}

	public static final boolean checkLinevsLine(XY p1, XY p2, XY p3, XY p4) {
		return checkLinevsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
	}

	public static final boolean checkLinevsLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4,
			float y4) {
		final float uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))
				/ ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		final float uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))
				/ ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
		return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
	}

	public static final boolean checkLinevsAABB(XY l1, XY l2, XYZW rect) {
		return checkLinevsAABB(l1.getX(), l1.getY(), l2.getX(), l2.getY(), rect);
	}

	public static final boolean checkLinevsAABB(float x1, float y1, float x2, float y2, XYZW rect) {
		return checkLinevsAABB(x1, y1, x2, y2, rect.getX(), rect.getY(), rect.getZ(), rect.getW());
	}

	public static final boolean checkLinevsAABB(float x1, float y1, float x2, float y2, float rx, float ry, float rw,
			float rh) {
		final boolean onLeft = checkLinevsLine(x1, y1, x2, y2, rx, ry, rx, ry + rh);
		final boolean onRight = checkLinevsLine(x1, y1, x2, y2, rx + rw, ry, rx + rw, ry + rh);
		final boolean onTop = checkLinevsLine(x1, y1, x2, y2, rx, ry, rx + rw, ry);
		final boolean onBottom = checkLinevsLine(x1, y1, x2, y2, rx, ry + rh, rx + rw, ry + rh);
		return onLeft || onRight || onTop || onBottom;
	}

	public static final <T extends XY> boolean checkLinevsPolygon(XY p1, XY p2, TArray<T> vertices) {
		return checkLinevsPolygon(p1.getX(), p1.getY(), p2.getX(), p2.getY(), vertices);
	}

	public static final <T extends XY> boolean checkLinevsPolygon(XYZW line, TArray<T> vertices) {
		return checkLinevsPolygon(line.getX(), line.getY(), line.getZ(), line.getW(), vertices);
	}

	public static final <T extends XY> boolean checkLinevsPolygon(float x1, float y1, float x2, float y2,
			TArray<T> vertices) {
		int next = 0;
		for (int current = 0; current < vertices.size; current++) {
			next = current + 1;
			if (next == vertices.size) {
				next = 0;
			}
			final float x3 = vertices.get(current).getX();
			final float y3 = vertices.get(current).getY();
			final float x4 = vertices.get(next).getX();
			final float y4 = vertices.get(next).getY();
			boolean collision = checkLinevsLine(x1, y1, x2, y2, x3, y3, x4, y4);
			if (collision) {
				return true;
			}
		}
		return false;
	}

	public static final <T extends XY> boolean checkPolygonvsPolygon(TArray<T> p1, TArray<T> p2) {
		return checkPolygonvsPolygon(p1, p2, true);
	}

	public static final <T extends XY> boolean checkPolygonvsPolygon(TArray<T> p1, TArray<T> p2, boolean inside) {
		int next = 0;
		for (int current = 0; current < p1.size; current++) {
			next = current + 1;
			if (next == p1.size) {
				next = 0;
			}
			T vc = p1.get(current);
			T vn = p1.get(next);
			boolean collision = checkLinevsPolygon(vc.getX(), vc.getY(), vn.getX(), vn.getY(), p2);
			if (collision) {
				return true;
			}
			if (inside) {
				collision = checkPointvsPolygon(p2.get(0).getX(), p2.get(0).getY(), p1);
				if (collision) {
					return true;
				}
				collision = checkPointvsPolygon(p1.get(0).getX(), p1.get(0).getY(), p2);
				if (collision)
					return true;
			}
		}
		return false;
	}

	public static final boolean checkPolygonvsPolygon(final float[] points1, final float[] points2) {
		final Vector2f normal = Vector2f.ZERO();
		final float[] poly1 = CollectionUtils.copyOf(points1);
		final float[] poly2 = CollectionUtils.copyOf(points2);
		final float[][] polygons = { poly1, poly2 };
		float minA, maxA, projected, minB, maxB;
		int j;
		for (int i = 0; i < polygons.length; i++) {
			float[] polygon = polygons[i];
			for (int i1 = 0; i1 < polygon.length; i1 += 2) {
				int i2 = (i1 + 2) % polygon.length;
				normal.set(polygon[i2 + 1] - polygon[i1 + 1], polygon[i1] - polygon[i2]);
				minA = maxA = -1;
				for (j = 0; j < poly1.length; j += 2) {
					projected = normal.x * poly1[j] + normal.y * poly1[j + 1];
					if (minA == -1 || projected < minA) {
						minA = projected;
					}
					if (maxA == -1 || projected > maxA) {
						maxA = projected;
					}
				}
				minB = maxB = -1;
				for (j = 0; j < poly2.length; j += 2) {
					projected = normal.x * poly2[j] + normal.y * poly2[j + 1];
					if (minB == -1 || projected < minB) {
						minB = projected;
					}
					if (maxB == -1 || projected > maxB) {
						maxB = projected;
					}
				}
				if (maxA < minB || maxB < minA) {
					return false;
				}
			}
		}
		return true;
	}

	public final static boolean checkAngle(float angle, float actual, float offset) {
		return actual > angle - offset && actual < angle + offset;
	}

	public final static boolean checkAngle(float angle, float actual) {
		return checkAngle(angle, actual, 22.5f);
	}

	public static final boolean checkIntersectTwoRectangles(final XY p1Min, final XY p1Max, final XY p2Min,
			final XY p2Max) {
		final boolean par1 = p1Min.getX() > p2Max.getX();
		final boolean par2 = p2Min.getX() > p1Max.getX();
		final boolean par3 = p1Min.getY() > p2Max.getY();
		final boolean par4 = p2Min.getY() > p1Max.getY();
		return !(par1 || par2 || par3 || par4);
	}

	public static final boolean checkSegmentOnOneSide(Vector2f axisPos, Vector2f axisDir, Vector2f segmentPos,
			Vector2f segmentEnd) {
		Vector2f d1 = segmentPos.sub(axisPos);
		Vector2f d2 = segmentEnd.sub(axisPos);
		Vector2f n = Vector2f.rotationLeft(axisDir);
		return n.dot(d1) * n.dot(d2) > 0f;
	}

	public static final boolean checkSeperateAxisRect(Vector2f axisStart, Vector2f axisEnd, Vector2f rectPos,
			Vector2f rectSize, Vector2f rectAlignement) {
		Vector2f result = axisStart.sub(axisEnd);
		Vector2f edgeAStart = getRectCorner(rectPos, rectSize, rectAlignement, 0);
		Vector2f edgeAEnd = getRectCorner(rectPos, rectSize, rectAlignement, 1);
		Vector2f edgeBStart = getRectCorner(rectPos, rectSize, rectAlignement, 2);
		Vector2f edgeBEnd = getRectCorner(rectPos, rectSize, rectAlignement, 3);

		RangeF edgeARange = getProjectSegment(edgeAStart, edgeAEnd, result);
		RangeF edgeBRange = getProjectSegment(edgeBStart, edgeBEnd, result);
		RangeF projection = getRangeHull(edgeARange, edgeBRange);

		RangeF axisRange = getProjectSegment(axisStart, axisEnd, result);
		return !checkOverlappingRange(axisRange, projection);
	}

	public static boolean checkIntersectCubicBezierCurveAndLine(XY bezierStartPoint, XY bezierPoint1, XY bezierPoint2,
			XY bezierEndPoint, XY lineStartPoint, XY lineEndPoint) {

		final float A = lineEndPoint.getY() - lineStartPoint.getY();
		final float B = lineStartPoint.getX() - lineEndPoint.getX();
		final float C = -lineStartPoint.getX() * A - lineStartPoint.getY() * B;

		final Vector2f[] coeffs = getBezierCoeffs(bezierStartPoint, bezierPoint1, bezierPoint2, bezierEndPoint);

		final float[] P = new float[4];

		P[0] = A * coeffs[0].getX() + B * coeffs[0].getY();
		P[1] = A * coeffs[1].getX() + B * coeffs[1].getY();
		P[2] = A * coeffs[2].getX() + B * coeffs[2].getY();
		P[3] = A * coeffs[3].getX() + B * coeffs[3].getY() + C;

		final float[] r = cubicRoots(P[0], P[1], P[2], P[3]);

		final TArray<Vector2f> list = new TArray<Vector2f>();
		float t;
		Vector2f p;
		float s;
		float tMt;
		float tMtMt;
		for (int i = 0; i < 3; i++) {
			t = r[i];

			tMt = t * t;
			tMtMt = tMt * t;

			p = new Vector2f(
					coeffs[0].getX() * tMtMt + coeffs[1].getX() * tMt + coeffs[2].getX() * t + coeffs[3].getX(),
					coeffs[0].getY() * tMtMt + coeffs[1].getY() * tMt + coeffs[2].getY() * t + coeffs[3].getY());

			if ((lineEndPoint.getX() - lineStartPoint.getX()) != 0) {
				s = (p.getX() - lineStartPoint.getX()) / (lineEndPoint.getX() - lineStartPoint.getX());
			} else {
				s = (p.getY() - lineStartPoint.getY()) / (lineEndPoint.getY() - lineStartPoint.getY());
			}
			if (t < 0 || t > 1f || s < 0 || s > 1f) {
				continue;
			}

			list.add(p);
		}
		return list.size > 0;
	}

	public static final RangeF getProjectSegment(Vector2f pos, Vector2f end, Vector2f onto) {
		Vector2f unitOnto = onto.nor();
		return new RangeF(unitOnto.dot(pos), unitOnto.dot(end));
	}

	public static final float getJumpVelocity(float gravity, float distance) {
		return MathUtils.sqrt(2 * distance * gravity);
	}

	private static float[] cubicRoots(float a, float b, float c, float d) {
		float A = b / a;
		float B = c / a;
		float C = d / a;

		float Q, R, D, S, T, Im;

		Q = (3f * B - MathUtils.pow(A, 2f)) / 9f;
		R = (9f * A * B - 27f * C - 2f * MathUtils.pow(A, 3f)) / 54f;
		D = MathUtils.pow(Q, 3f) + MathUtils.pow(R, 2f);

		float[] t = new float[3];

		if (D >= 0) {

			float sqrtD = MathUtils.sqrt(D);
			float TAD3 = -A / 3f;
			float RAsqrtD = R + sqrtD;
			float RSsqrtD = R - sqrtD;
			float D13 = (1f / 3f);

			S = MathUtils.sign(RAsqrtD) * MathUtils.pow(MathUtils.abs(RAsqrtD), D13);
			T = MathUtils.sign(RSsqrtD) * MathUtils.pow(MathUtils.abs(RSsqrtD), D13);

			float SST = (S + T);

			t[0] = TAD3 + SST;
			t[1] = TAD3 - SST / 2f;
			t[2] = t[1];
			Im = MathUtils.abs(MathUtils.sqrt(3f) * (S - T) / 2f);

			if (Im != 0) {
				t[1] = -1;
				t[2] = -1;
			}
		} else {

			float th = MathUtils.acos(R / MathUtils.sqrt(-MathUtils.pow(Q, 3f)));
			float sqrt_QM2 = 2f * MathUtils.sqrt(-Q);
			float AD3 = A / 3f;

			float thD3 = (th / 3f);
			float PIM2D3 = MathUtils.TWO_PI / 3f;

			t[0] = sqrt_QM2 * MathUtils.cos(thD3) - AD3;
			t[1] = sqrt_QM2 * MathUtils.cos(thD3 + PIM2D3) - AD3;
			t[2] = sqrt_QM2 * MathUtils.cos(thD3 + 2f * PIM2D3) - AD3;
		}

		for (int i = 0; i < 3; i++) {
			if (t[i] < 0 || t[i] > 1f) {
				t[i] = -1;
			}
		}

		return sortCubicRoots(t);
	}

	private static float[] sortCubicRoots(float[] array) {
		boolean flip;
		float temp;
		do {
			flip = false;
			for (int i = 0; i < array.length - 1; i++) {
				if ((array[i + 1] >= 0 && array[i] > array[i + 1]) || (array[i] < 0 && array[i + 1] >= 0)) {
					flip = true;
					temp = array[i];
					array[i] = array[i + 1];
					array[i + 1] = temp;

				}
			}
		} while (flip);
		return array;
	}

	/**
	 * 判断两点坐标是否存在移动
	 * 
	 * @param distance
	 * @param startPoints
	 * @param endPoint
	 * @return
	 */
	public static final boolean isMoved(float distance, XY startPoints, XY endPoint) {
		return isMoved(distance, startPoints.getX(), startPoints.getY(), endPoint.getX(), endPoint.getY());
	}

	/**
	 * 判断两点坐标是否存在移动
	 * 
	 * @param distance
	 * @param sx
	 * @param sy
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static final boolean isMoved(float distance, float sx, float sy, float dx, float dy) {
		float xDistance = dx - sx;
		float yDistance = dy - sy;
		if (MathUtils.abs(xDistance) < distance && MathUtils.abs(yDistance) < distance) {
			return false;
		}
		return true;
	}

	/**
	 * 检查两个坐标值是否在指定的碰撞半径内
	 * 
	 * @param x1
	 * @param y1
	 * @param r1
	 * @param x2
	 * @param y2
	 * @param r2
	 * @return
	 */
	public static boolean isCollision(float x1, float y1, float r1, float x2, float y2, float r2) {
		float a = r1 + r2;
		float dx = x1 - x2;
		float dy = y1 - y2;
		return a * a > dx * dx + dy * dy;
	}

	public static final boolean lineIntersection(XY p1, XY p2, boolean seg1, XY p3, XY p4, boolean seg2, SetXY result) {
		float y43 = p4.getY() - p3.getY();
		float x21 = p2.getX() - p1.getX();
		float x43 = p4.getX() - p3.getX();
		float y21 = p2.getY() - p1.getY();
		float denom = y43 * x21 - x43 * y21;
		if (denom == 0) {
			return false;
		}

		float y13 = p1.getY() - p3.getY();
		float x13 = p1.getX() - p3.getX();
		float ua = (x43 * y13 - y43 * x13) / denom;
		if (seg1 && ((ua < 0) || (ua > 1))) {
			return false;
		}

		if (seg2) {
			float ub = (x21 * y13 - y21 * x13) / denom;
			if ((ub < 0) || (ub > 1)) {
				return false;
			}
		}

		float x = p1.getX() + ua * x21;
		float y = p1.getY() + ua * y21;
		result.setX(x);
		result.setY(y);
		return true;
	}

	public static final boolean lineIntersection(XY p1, XY p2, XY p3, XY p4, SetXY ptIntersection) {
		float num1 = ((p4.getY() - p3.getY()) * (p2.getX() - p1.getX()))
				- ((p4.getX() - p3.getX()) * (p2.getY() - p1.getY()));
		float num2 = ((p4.getX() - p3.getX()) * (p1.getY() - p3.getY()))
				- ((p4.getY() - p3.getY()) * (p1.getX() - p3.getX()));
		float num3 = ((p2.getX() - p1.getX()) * (p1.getY() - p3.getY()))
				- ((p2.getY() - p1.getY()) * (p1.getX() - p3.getX()));
		if (num1 != 0f) {
			float num4 = num2 / num1;
			float num5 = num3 / num1;
			if (((num4 >= 0f) && (num4 <= 1f)) && ((num5 >= 0f) && (num5 <= 1f))) {
				ptIntersection.setX((p1.getX() + (num4 * (p2.getX() - p1.getX()))));
				ptIntersection.setY((p1.getY() + (num4 * (p2.getY() - p1.getY()))));
				return true;
			}
		}
		return false;
	}

	public static boolean pointIntersection(Vector2f min1Values, Vector2f max1Values, Vector2f min2Values,
			Vector2f max2Values, Vector2f overlap) {
		overlap.setZero();
		final float overlapX1 = max1Values.getX() - min2Values.getX();
		final float overlapX2 = max2Values.getX() - min1Values.getX();
		if (overlapX1 < 0 || overlapX2 < 0) {
			return false;
		}
		final float overlapY1 = max1Values.getY() - min2Values.getY();
		final float overlapY2 = max2Values.getY() - min1Values.getY();
		if (overlapY1 < 0 || overlapY2 < 0) {
			return false;
		}
		overlap.setX(MathUtils.min(overlapX1, overlapX2));
		overlap.setY(MathUtils.min(overlapY1, overlapY2));
		return true;
	}

	/**
	 * 填充指定瓦片的边界。瓦片从左到右，从上到下。
	 * 
	 * @param width
	 * @param height
	 * @param tileWidth
	 * @param tileHeight
	 * @param tileIndex
	 * @return
	 */
	public static final RectBox getTile(int width, int height, int tileWidth, int tileHeight, int tileIndex) {
		return getTile(width, height, tileWidth, tileHeight, tileIndex, null);
	}

	/**
	 * 填充指定瓦片的边界。瓦片从左到右，从上到下。
	 * 
	 * @param width
	 * @param height
	 * @param tileWidth
	 * @param tileHeight
	 * @param tileIndex
	 * @param result
	 */
	public static final RectBox getTile(int width, int height, int tileWidth, int tileHeight, int tileIndex,
			RectBox result) {
		if (result == null) {
			result = new RectBox();
		}
		int tilesPerRow = width / tileWidth;
		if (tilesPerRow == 0) {
			result.setBounds(0, 0, width, height);
		} else {
			int row = tileIndex / tilesPerRow;
			int col = tileIndex % tilesPerRow;
			result.setBounds(tileWidth * col, tileHeight * row, tileWidth, tileHeight);
		}
		return result;
	}

	/**
	 * 返回指定矩形间的对应碰撞点集合
	 * 
	 * @param src
	 * @param dst
	 * @return
	 */
	public static TArray<RectBox> getNineTiles(final RectBox src, final RectBox dst) {
		TArray<RectBox> tiles = new TArray<RectBox>(9);

		// topLeft
		Vector2f tl0 = new Vector2f(dst.x, dst.y);
		Vector2f br0 = new Vector2f(src.x, src.y);

		// topCenter
		Vector2f tl1 = new Vector2f(src.x, dst.y);
		Vector2f br1 = new Vector2f(src.x + src.width, src.y);

		// topRight
		Vector2f tl2 = new Vector2f(src.x + src.width, dst.y);
		Vector2f br2 = new Vector2f(dst.x + dst.width, src.y);

		// rightCenter
		Vector2f tl3 = br1;
		Vector2f br3 = new Vector2f(dst.x + dst.width, src.y + src.height);

		// bottomRight
		Vector2f tl4 = new Vector2f(src.x + src.width, src.y + src.height);
		Vector2f br4 = new Vector2f(dst.x + dst.width, dst.y + dst.height);

		// bottomCenter
		Vector2f tl5 = new Vector2f(src.x, src.y + src.height);
		Vector2f br5 = new Vector2f(src.x + src.width, dst.y + dst.height);

		// bottomLeft
		Vector2f tl6 = new Vector2f(dst.x, src.y + src.height);
		Vector2f br6 = new Vector2f(src.x, dst.y + dst.height);

		// leftCenter
		Vector2f tl7 = new Vector2f(dst.x, src.y);
		Vector2f br7 = tl5;

		tiles.add(constructRect(tl0, br0));
		tiles.add(constructRect(tl1, br1));
		tiles.add(constructRect(tl2, br2));
		tiles.add(constructRect(tl7, br7));
		tiles.add(src);
		tiles.add(constructRect(tl3, br3));
		tiles.add(constructRect(tl6, br6));
		tiles.add(constructRect(tl5, br5));
		tiles.add(constructRect(tl4, br4));

		return tiles;
	}

	private static final RangeF getRangeHull(RangeF a, RangeF b) {
		return new RangeF(a.getMin() < b.getMin() ? a.getMin() : b.getMin(),
				a.getMax() > b.getMax() ? a.getMax() : b.getMax());
	}

	public static final Vector2f getRectCorner(Vector2f rectPos, Vector2f rectSize, Vector2f rectAlignement,
			int corner) {
		return getRectCorner(constructRect(rectPos, rectSize, rectAlignement), corner);
	}

	public static final Vector2f getRectCorner(RectBox rect, int corner) {
		return getRectCornersList(rect)[corner % 4];
	}

	public static final Vector2f[] getRectCornersList(RectBox rect) {
		Vector2f tl = new Vector2f(rect.x, rect.y);
		Vector2f tr = new Vector2f(rect.x + rect.width, rect.y);
		Vector2f bl = new Vector2f(rect.x, rect.y + rect.height);
		Vector2f br = new Vector2f(rect.x + rect.width, rect.y + rect.height);
		return new Vector2f[] { tl, tr, br, bl };
	}

	public static final Vector2f getStartPointDiagonal(final XY p1, final XY p2) {
		return new Vector2f(MathUtils.min(p1.getX(), p2.getX()), MathUtils.min(p1.getY(), p2.getY()));
	}

	public static final Vector2f getEndPointDiagonal(final XY p1, final XY p2) {
		return new Vector2f(MathUtils.max(p1.getX(), p2.getX()), MathUtils.max(p1.getY(), p2.getY()));
	}

	private static final Vector2f[] getBezierCoeffs(XY bezierStartPoint, XY bezierPoint1, XY bezierPoint2,
			XY bezierEndPoint) {
		final Vector2f[] coeffs = new Vector2f[4];
		final float bezierStartPointX_M_3 = bezierStartPoint.getX() * 3f;
		final float bezierPoint1X_M_3 = bezierPoint1.getX() * 3f;
		final float bezierPoint2X_M_3 = bezierPoint2.getX() * 3f;

		coeffs[0].setX(-bezierStartPoint.getX() + bezierPoint1X_M_3 - bezierPoint2X_M_3 + bezierEndPoint.getX());
		coeffs[1].setX(bezierStartPointX_M_3 - 6f * bezierPoint1.getX() + bezierPoint2X_M_3);
		coeffs[2].setX(-bezierStartPointX_M_3 + bezierPoint1X_M_3);
		coeffs[3].setX(bezierStartPoint.getX());

		final float bezierStartPointY_M_3 = bezierStartPoint.getY() * 3f;
		final float bezierPoint1Y_M_3 = bezierPoint1.getY() * 3f;
		final float bezierPoint2Y_M_3 = bezierPoint2.getY() * 3f;

		coeffs[0].setY(-bezierStartPoint.getY() + bezierPoint1Y_M_3 - bezierPoint2Y_M_3 + bezierEndPoint.getY());
		coeffs[1].setY(bezierStartPointY_M_3 - 6f * bezierPoint1.getY() + bezierPoint2Y_M_3);
		coeffs[2].setY(-bezierStartPointY_M_3 + bezierPoint1Y_M_3);
		coeffs[3].setY(bezierStartPoint.getY());

		return coeffs;
	}

}
