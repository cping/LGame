package loon.action.collision;

import loon.core.LSystem;
import loon.core.geom.Line;
import loon.core.geom.Point;
import loon.core.geom.RectBox;
import loon.core.geom.Shape;
import loon.core.graphics.LColor;
import loon.core.graphics.LImage;
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
public final class CollisionHelper {

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
	public static boolean isCollision(float x1, float y1, float r1, float x2,
			float y2, float r2) {
		float a = r1 + r2;
		float dx = x1 - x2;
		float dy = y1 - y2;
		return a * a > dx * dx + dy * dy;
	}

	/**
	 * 获得两个矩形间距离
	 * 
	 * @param box1
	 * @param box2
	 * @return
	 */
	public static float getDistance(final RectBox box1, final RectBox box2) {
		final float xdiff = box1.x - box2.x;
		final float ydiff = box1.y - box2.y;
		return MathUtils.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	/**
	 * 检查两个矩形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isRectToRect(RectBox rect1, RectBox rect2) {
		return rect1.intersects(rect2);
	}

	/**
	 * 判断两个圆形是否发生了碰撞
	 * 
	 * @param rect1
	 * @param rect2
	 * @return
	 */
	public static boolean isCircToCirc(RectBox rect1, RectBox rect2) {
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
	public static boolean isRectToCirc(RectBox rect1, RectBox rect2) {
		float radius = rect2.getWidth() / 2;
		Point middle = getMiddlePoint(rect2);
		Point upperLeft = new Point(rect1.getMinX(), rect1.getMinY());
		Point upperRight = new Point(rect1.getMaxX(), rect1.getMinY());
		Point downLeft = new Point(rect1.getMinX(), rect1.getMaxY());
		Point downRight = new Point(rect1.getMaxX(), rect1.getMaxY());
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
	private static boolean isPointToLine(Point point1, Point point2,
			Point middle, float radius) {
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
	private static Point getMiddlePoint(RectBox rectangle) {
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
	public boolean isPixelCollide(LImage src, float x1, float y1, LImage dest,
			float x2, float y2) {

		float width1 = x1 + src.getWidth() - 1, height1 = y1 + src.getHeight()
				- 1, width2 = x2 + dest.getWidth() - 1, height2 = y2
				+ dest.getHeight() - 1;

		int xstart = (int) MathUtils.max(x1, x2), ystart = (int) MathUtils.max(
				y1, y2), xend = (int) MathUtils.min(width1, width2), yend = (int) MathUtils
				.min(height1, height2);

		int toty = MathUtils.abs(yend - ystart);
		int totx = MathUtils.abs(xend - xstart);

		for (int y = 1; y < toty - 1; y++) {
			int ny = MathUtils.abs(ystart - (int) y1) + y;
			int ny1 = MathUtils.abs(ystart - (int) y2) + y;

			for (int x = 1; x < totx - 1; x++) {
				int nx = MathUtils.abs(xstart - (int) x1) + x;
				int nx1 = MathUtils.abs(xstart - (int) x2) + x;

				try {
					if (((src.getPixel(nx, ny) & LSystem.TRANSPARENT) != 0x00)
							&& ((dest.getPixel(nx1, ny1) & LSystem.TRANSPARENT) != 0x00)) {
						return true;
					} else if (getPixelData(src, nx, ny)[0] != 0
							&& getPixelData(dest, nx1, ny1)[0] != 0) {
						return true;
					}
				} catch (Exception e) {

				}
			}
		}
		return false;
	}

	private static int[] getPixelData(LImage image, int x, int y) {
		return LColor.getRGBs(image.getPixel(x, y));
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
	public static boolean intersect(RectBox rectA, int[] dataA, RectBox rectB,
			int[] dataB) {
		int top = (int) MathUtils.max(rectA.getY(), rectB.getY());
		int bottom = (int) MathUtils.min(rectA.getBottom(), rectB.getBottom());
		int left = (int) MathUtils.max(rectA.getX(), rectB.getX());
		int right = (int) MathUtils.min(rectA.getRight(), rectB.getRight());

		for (int y = top; y < bottom; y++) {
			for (int x = left; x < right; x++) {

				int colorA = dataA[(int) ((x - rectA.x) + (y - rectA.y)
						* rectA.width)];
				int colorB = dataB[(int) ((x - rectB.x) + (y - rectB.y)
						* rectB.width)];
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
	public final static boolean intersects(Shape s1, Shape s2) {
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.intersects(s2);
	}

	/**
	 * 判断两个Shape是否存在包含关系
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public final static boolean contains(Shape s1, Shape s2) {
		if (s1 == null || s2 == null) {
			return false;
		}
		return s1.contains(s2);
	}

	public final static Line getLine(Shape shape, int s, int e) {
		float[] start = shape.getPoint(s);
		float[] end = shape.getPoint(e);
		Line line = new Line(start[0], start[1], end[0], end[1]);
		return line;
	}

	public final static Line getLine(Shape shape, float sx, float sy, int e) {
		float[] end = shape.getPoint(e);
		Line line = new Line(sx, sy, end[0], end[1]);
		return line;
	}
}
