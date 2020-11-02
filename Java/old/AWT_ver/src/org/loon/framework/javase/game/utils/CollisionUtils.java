package org.loon.framework.javase.game.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.loon.framework.javase.game.action.sprite.Collidable;
import org.loon.framework.javase.game.action.sprite.Sprite;
import org.loon.framework.javase.game.core.geom.RectBox;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.3
 */
public class CollisionUtils {
	public static boolean checkCollision(Collidable other1, Collidable other2) {
		return other1.getMask().collidesWith(other2.getMask());
	}

	public static boolean checkCollision(Collidable other, int x, int y) {
		return other.getMask().collidesWith(x, y);
	}

	public static boolean checkBoundingBoxCollision(Collidable other1,
			Collidable other2) {
		return other1.getMask().checkBoundingBoxCollision(other2.getMask());
	}

	public static boolean checkBoundingBoxCollision(Collidable other, int x,
			int y) {
		return other.getMask().checkBoundingBoxCollision(x, y);
	}

	public static boolean isPixelHit(Sprite src, Sprite dest) {
		return checkCollision(src, dest);
	}

	/**
	 * 获得两个矩形间距离
	 * 
	 * @param box1
	 * @param box2
	 * @return
	 */
	public static double getDistance(final RectBox box1, final RectBox box2) {
		final double xdiff = box1.x - box2.x;
		final double ydiff = box1.y - box2.y;
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
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
		Point2D middle1 = CollisionUtils.getMiddlePoint(rect1);
		Point2D middle2 = CollisionUtils.getMiddlePoint(rect2);
		double distance = middle1.distance(middle2);
		double radius1 = rect1.getWidth() / 2;
		double radius2 = rect2.getWidth() / 2;
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
		double radius = rect2.getWidth() / 2;
		Point2D middle = CollisionUtils.getMiddlePoint(rect2);
		Point2D upperLeft = new Point2D.Double(rect1.getMinX(), rect1.getMinY());
		Point2D upperRight = new Point2D.Double(rect1.getMaxX(), rect1
				.getMinY());
		Point2D downLeft = new Point2D.Double(rect1.getMinX(), rect1.getMaxY());
		Point2D downRight = new Point2D.Double(rect1.getMaxX(), rect1.getMaxY());
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
	private static boolean isPointToLine(Point2D point1, Point2D point2,
			Point2D middle, double radius) {
		Line2D line = new Line2D.Double(point1, point2);
		double distance = line.ptLineDist(middle);
		return distance < radius;
	}

	/**
	 * 返回中间距离的Point2D形式
	 * 
	 * @param rectangle
	 * @return
	 */
	private static Point2D getMiddlePoint(RectBox rectangle) {
		return new Point2D.Double(rectangle.getCenterX(), rectangle
				.getCenterY());
	}

}
