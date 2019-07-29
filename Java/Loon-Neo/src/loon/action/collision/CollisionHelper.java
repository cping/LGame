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
import loon.geom.Line;
import loon.geom.Point;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.ShapeUtils;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.geom.XY;
import loon.utils.MathUtils;

/**
 * 碰撞事件检测与处理工具类,内部是一系列碰撞检测与处理方法的集合
 */
public final class CollisionHelper extends ShapeUtils {

	private CollisionHelper() {
	}

	private static final RectBox rectTemp1 = new RectBox();

	private static final RectBox rectTemp2 = new RectBox();

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

	/**
	 * 获得两个矩形间初始XY位置的距离
	 * 
	 * @param box1
	 * @param box2
	 * @return
	 */
	public static float getDistance(final BoxSize box1, final BoxSize box2) {
		if (box1 == null || box2 == null) {
			return 0f;
		}
		final float xdiff = box1.getX() - box2.getX();
		final float ydiff = box1.getY() - box2.getY();
		return MathUtils.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	/**
	 * 获得多个点间距离
	 * 
	 * @param target
	 * @param beforePlace
	 * @param afterPlace
	 * @param distance
	 * @return
	 */
	public static final float getDistance(XY target, XY beforePlace, XY afterPlace, float distance) {
		return getDistance(target, beforePlace, afterPlace, distance, false);
	}

	/**
	 * 获得多个点间距离
	 * 
	 * @param target
	 * @param beforePlace
	 * @param afterPlace
	 * @param distance
	 * @param limit
	 * @return
	 */
	public static final float getDistance(XY target, XY beforePlace, XY afterPlace, float distance, boolean limit) {
		float before = MathUtils.abs(target.getX() - beforePlace.getX())
				+ MathUtils.abs(target.getY() - beforePlace.getY());
		float after = MathUtils.abs(target.getX() - afterPlace.getX())
				+ MathUtils.abs(target.getY() - afterPlace.getY());
		if (limit && before > distance) {
			return 0;
		}
		return 1f * (before - after) / after;
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
		return intersects(rect1.getX(), rect1.getY(), rect1.getWidth(), rect1.getHeight(), rect2.getX(), rect2.getY(),
				rect2.getWidth(), rect2.getHeight());
	}

	/**
	 * 判断两个圆形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isCircToCirc(BoxSize rect1, BoxSize rect2) {
		Point middle1 = getMiddlePoint(rect1);
		Point middle2 = getMiddlePoint(rect2);
		float distance = middle1.distanceTo(middle2);
		float radius1 = rect1.getWidth() / 2;
		float radius2 = rect2.getWidth() / 2;
		return (distance - radius2) < radius1;
	}

	/**
	 * 检查矩形与圆形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isRectToCirc(BoxSize rect1, BoxSize rect2) {
		float radius = rect2.getWidth() / 2;
		float minX = rect1.getX();
		float minY = rect1.getY();
		float maxX = rect1.getX() + rect1.getWidth();
		float maxY = rect1.getY() + rect1.getHeight();
		Point middle = getMiddlePoint(rect2);
		Point upperLeft = new Point(minX, minY);
		Point upperRight = new Point(maxX, minY);
		Point downLeft = new Point(minX, maxY);
		Point downRight = new Point(maxX, maxY);
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
	 * 换算点线距离
	 * 
	 * @param point1
	 * @param point2
	 * @param middle
	 * @param radius
	 * @return
	 */
	private static boolean isPointToLine(XY point1, XY point2, XY middle, float radius) {
		Line line = new Line(point1, point2);
		float distance = line.ptLineDist(middle);
		return distance < radius;
	}

	/**
	 * 返回中间距离的Point2D形式
	 * 
	 * @param rectangle
	 * @return
	 */
	private static Point getMiddlePoint(BoxSize rectangle) {
		return new Point(rectangle.getCenterX(), rectangle.getCenterY());
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

		float width1 = x1 + src.width() - 1, height1 = y1 + src.height() - 1, width2 = x2 + dest.width() - 1,
				height2 = y2 + dest.height() - 1;

		int xstart = (int) MathUtils.max(x1, x2), ystart = (int) MathUtils.max(y1, y2),
				xend = (int) MathUtils.min(width1, width2), yend = (int) MathUtils.min(height1, height2);

		int toty = MathUtils.abs(yend - ystart);
		int totx = MathUtils.abs(xend - xstart);

		for (int y = 1; y < toty - 1; y++) {
			int ny = MathUtils.abs(ystart - (int) y1) + y;
			int ny1 = MathUtils.abs(ystart - (int) y2) + y;

			for (int x = 1; x < totx - 1; x++) {
				int nx = MathUtils.abs(xstart - (int) x1) + x;
				int nx1 = MathUtils.abs(xstart - (int) x2) + x;

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

	public static boolean isPointInRect(float rectX, float rectY, float rectW, float rectH, float x, float y) {
		if (x >= rectX && x <= rectX + rectW) {
			if (y >= rectY && y <= rectY + rectH) {
				return true;
			}
		}
		return false;
	}

	public static final boolean intersect(RectBox rect, float x, float y) {
		if (rect != null) {
			if (rect.Left() <= x && x < rect.Right() && rect.Top() <= y && y < rect.Bottom())
				return true;
		}
		return false;
	}

	public static final boolean intersect(float sx, float sy, float width, float height, float x, float y) {
		return (x >= sx) && ((x - sx) < width) && (y >= sy) && ((y - sy) < height);
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
	public static boolean intersect(RectBox rectA, int[] dataA, RectBox rectB, int[] dataB) {
		int top = (int) MathUtils.max(rectA.getY(), rectB.getY());
		int bottom = (int) MathUtils.min(rectA.getBottom(), rectB.getBottom());
		int left = (int) MathUtils.max(rectA.getX(), rectB.getX());
		int right = (int) MathUtils.min(rectA.getRight(), rectB.getRight());

		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {

				int colorA = dataA[(int) ((x - rectA.x) + (y - rectA.y) * rectA.width)];
				int colorB = dataB[(int) ((x - rectB.x) + (y - rectB.y) * rectB.width)];
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

	public static final int[] intersect(RectBox rect1, RectBox rect2) {
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
		return intersects(x, y, width, height, dx, dy, dw, dh, false);
	}

	public static final boolean intersects(float x, float y, float width, float height, float dx, float dy, float dw,
			float dh, boolean touchingIsIn) {
		rectTemp1.setBounds(x, y, width, height).normalize();
		rectTemp2.setBounds(dx, dy, dw, dh).normalize();
		if (touchingIsIn) {
			if (rectTemp1.x + rectTemp1.width == rectTemp2.x) {
				return true;
			}
			if (rectTemp1.x == rectTemp2.x + rectTemp2.width) {
				return true;
			}
			if (rectTemp1.y + rectTemp1.height == rectTemp2.y) {
				return true;
			}
			if (rectTemp1.y == rectTemp2.y + rectTemp2.height) {
				return true;
			}
		}
		return rectTemp1.overlaps(rectTemp2);
	}

	/**
	 * 计算并返回两个正方形之间的碰撞间距值
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static float squareRects(BoxSize rect1, BoxSize rect2) {
		if (rect1 == null || rect2 == null) {
			return 0f;
		}
		return squareRects(rect1.getX(), rect1.getY(), rect1.getWidth(), rect1.getHeight(), rect2.getX(), rect2.getY(),
				rect2.getWidth(), rect2.getHeight());
	}

	/**
	 * 计算并返回两个正方形之间的碰撞间距值
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param x2
	 * @param y2
	 * @param w2
	 * @param h2
	 * @return
	 */
	public static float squareRects(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
		if (x1 < x2 + w2 && x2 < x1 + w1) {
			if (y1 < y2 + h2 && y2 < y1 + h1) {
				return 0f;
			}
			if (y1 > y2) {
				return (y1 - (y2 + h2)) * (y1 - (y2 + h2));
			}
			return (y2 - (y1 + h1)) * (y2 - (y1 + h1));
		}
		if (y1 < y2 + h2 && y2 < y1 + h1) {
			if (x1 > x2) {
				return (x1 - (x2 + w2)) * (x1 - (x2 + w2));
			}
			return (x2 - (x1 + w1)) * (x2 - (x1 + w1));
		}
		if (x1 > x2) {
			if (y1 > y2) {
				return MathUtils.distSquared((x2 + w2), (y2 + h2), x1, y1);
			}
			return MathUtils.distSquared(x2 + w2, y2, x1, y1 + h1);
		}
		if (y1 > y2) {
			return MathUtils.distSquared(x2, y2 + h2, x1 + w1, y1);
		}
		return MathUtils.distSquared(x2, y2, x1 + w1, y1 + h1);
	}

	/**
	 * 计算并返回指定位置与指定正方形之间的碰撞间距值
	 * 
	 * @param xy
	 * @param box
	 * @return
	 */
	public static float squarePointRect(XY xy, BoxSize box) {
		if (xy == null || box == null) {
			return 0f;
		}
		return squarePointRect(xy.getX(), xy.getY(), box.getX(), box.getY(), box.getWidth(), box.getHeight());
	}

	/**
	 * 计算并返回指定位置与指定正方形之间的碰撞间距值
	 * 
	 * @param px
	 * @param py
	 * @param rx
	 * @param ry
	 * @param rw
	 * @param rh
	 * @return
	 */
	public static float squarePointRect(float px, float py, float rx, float ry, float rw, float rh) {
		if (px >= rx && px <= rx + rw) {
			if (py >= ry && py <= ry + rh) {
				return 0f;
			}
			if (py > ry) {
				return (py - (ry + rh)) * (py - (ry + rh));
			}
			return (ry - py) * (ry - py);
		}
		if (py >= ry && py <= ry + rh) {
			if (px > rx) {
				return (px - (rx + rw)) * (px - (rx + rw));
			}
			return (rx - px) * (rx - px);
		}
		if (px > rx) {
			if (py > ry) {
				return MathUtils.distSquared(rx + rw, ry + rh, px, py);
			}
			return MathUtils.distSquared(rx + rw, ry, px, py);
		}
		if (py > ry) {
			return MathUtils.distSquared(rx, ry + rh, px, py);
		}
		return MathUtils.distSquared(rx, ry, px, py);
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

	public static final void confine(RectBox rect, RectBox field) {
		int x = rect.Right() > field.Right() ? field.Right() - (int) rect.getWidth() : rect.Left();
		if (x < field.Left()) {
			x = field.Left();
		}
		int y = (int) (rect.Bottom() > field.Bottom() ? field.Bottom() - rect.getHeight() : rect.Top());
		if (y < field.Top()) {
			y = field.Top();
		}
		rect.offset(x, y);
	}

	public static final Line getLine(Shape shape, int s, int e) {
		float[] start = shape.getPoint(s);
		float[] end = shape.getPoint(e);
		Line line = new Line(start[0], start[1], end[0], end[1]);
		return line;
	}

	public static final Line getLine(Shape shape, float sx, float sy, int e) {
		float[] end = shape.getPoint(e);
		Line line = new Line(sx, sy, end[0], end[1]);
		return line;
	}

	public static final boolean checkAABBvsAABB(XY p1, float w1, float h1, XY p2, float w2, float h2) {
		return checkAABBvsAABB(p1.getX(), p1.getY(), w1, h1, p2.getX(), p2.getY(), w2, h2);
	}

	public static final boolean checkAABBvsAABB(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		return !(x1 > x2 + w2 || x1 + w1 < x2) && !(y1 > y2 + h2 || y1 + h1 < y2);
	}

	public static final boolean checkAABBvsAABB(XY p1Min, XY p1Max, XY p2Min, XY p2Max) {
		return checkAABBvsAABB(p1Min.getX(), p1Min.getY(), p1Max.getX() - p1Min.getX(), p1Max.getY() - p1Min.getY(),
				p2Min.getX(), p2Min.getY(), p2Max.getX() - p2Min.getX(), p2Max.getY() - p2Min.getY());
	}

	public static final boolean checkAABBvsAABB(Vector3f p1, float w1, float h1, float t1, Vector3f p2, float w2,
			float h2, float t2) {
		return checkAABBvsAABB(p1.x, p1.y, p1.z, w1, h1, t1, p2.x, p2.y, p2.z, w2, h2, t2);
	}

	public static final boolean checkAABBvsAABB(float x1, float y1, float z1, float w1, float h1, float t1, float x2,
			float y2, float z2, float w2, float h2, float t2) {
		return !(x1 > x2 + w2 || x1 + w1 < x2) && !(y1 > y2 + h2 || y1 + h1 < y2) && !(z1 > z2 + t2 || z1 + t1 < z2);
	}

	public static final boolean checkAABBvsAABB(Vector3f p1Min, Vector3f p1Max, Vector3f p2Min, Vector3f p2Max) {
		return checkAABBvsAABB(p1Min.x, p1Min.y, p1Min.z, p1Max.x - p1Min.x, p1Max.y - p1Min.y, p1Max.z - p1Min.z,
				p2Min.x, p2Min.y, p1Min.z, p2Max.x - p2Min.x, p2Max.y - p2Min.y, p2Max.z - p2Min.z);
	}

	public static final boolean checkCircleCircle(XY p1, float r1, XY p2, float r2) {
		return checkCircleCircle(p1.getX(), p1.getY(), r1, p2.getX(), p2.getY(), r2);
	}

	public static final boolean checkCircleCircle(float x1, float y1, float r1, float x2, float y2, float r2) {
		float distance = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		float radiusSumSq = (r1 + r2) * (r1 + r2);

		return distance <= radiusSumSq;
	}

	public static final boolean checkSphereSphere(Vector3f p1, float r1, Vector3f p2, float r2) {
		return checkSphereSphere(p1.x, p1.y, p1.z, r1, p2.x, p2.y, p2.z, r2);
	}

	public static final boolean checkSphereSphere(float x1, float y1, float z1, float r1, float x2, float y2, float z2,
			float r2) {
		float distance = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1);
		float radiusSumSq = (r1 + r2) * (r1 + r2);

		return distance <= radiusSumSq;
	}

	public static final float getJumpVelocity(float gravity, float distance) {
		return MathUtils.sqrt(2 * distance * gravity);
	}

	public final static boolean checkAngle(float angle, float actual) {
		return actual > angle - 22.5f && actual < angle + 22.5f;
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

	public static final Vector2f nearestToLine(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f n) {
		int ax = (int) (p2.x - p1.x), ay = (int) (p2.y - p1.y);
		float u = (p3.x - p1.x) * ax + (p3.y - p1.y) * ay;
		u /= (ax * ax + ay * ay);
		n.x = p1.x + MathUtils.round(ax * u);
		n.y = p1.y + MathUtils.round(ay * u);
		return n;
	}

	public static final boolean lineIntersection(XY p1, XY p2, boolean seg1, XY p3, XY p4, boolean seg2,
			Vector2f result) {
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
		result.setLocation(x, y);
		return true;
	}

	public static final int whichSide(XY p1, float theta, XY p2) {
		theta += MathUtils.PI / 2;
		float x = (int) (p1.getX() + MathUtils.round(1000 * MathUtils.cos(theta)));
		float y = (int) (p1.getY() + MathUtils.round(1000 * MathUtils.sin(theta)));
		return MathUtils.iceil(dotf(p1.getX(), p1.getY(), p2.getX(), p2.getY(), x, y));
	}

	public static final void shiftToContain(RectBox tainer, RectBox tained) {
		if (tained.x < tainer.x) {
			tainer.x = tained.x;
		}
		if (tained.y < tainer.y) {
			tainer.y = tained.y;
		}
		if (tained.x + tained.width > tainer.x + tainer.width) {
			tainer.x = tained.x - (tainer.width - tained.width);
		}
		if (tained.y + tained.height > tainer.y + tainer.height) {
			tainer.y = tained.y - (tainer.height - tained.height);
		}
	}

	/**
	 * 将目标矩形添加到原始矩形的边界。
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static final RectBox add(RectBox source, RectBox target) {
		if (target == null) {
			return new RectBox(source);
		} else if (source == null) {
			source = new RectBox(target);
		} else {
			source.add(target);
		}
		return source;
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

}
