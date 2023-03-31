/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action;

import loon.action.sprite.IEntity;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.geom.Circle;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.Point;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.geom.Triangle2f;
import loon.geom.XY;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class PlaceActions {

	public static void rotateAroundDistance(Sprites spr, XY point, float angle, float distance) {
		final ISprite[] sprites = spr.getSprites();
		final int size = sprites.length;
		float x = point.getX();
		float y = point.getY();

		for (int i = 0; i < size; i++) {
			ISprite s = sprites[i];
			MathUtils.rotateAroundDistance(point, s, x, y, angle, distance);
		}
	}

	public static void rotateAroundDistance(IEntity e, XY point, float angle, float distance) {
		float x = point.getX();
		float y = point.getY();
		for (int i = 0; i < e.getChildCount(); i++) {
			IEntity ec = e.getChildByIndex(i);
			MathUtils.rotateAroundDistance(point, ec, x, y, angle, distance);
		}
	}

	public static void rotateAround(Sprites spr, XY point, float angle) {
		final ISprite[] sprites = spr.getSprites();
		final int size = sprites.length;
		float x = point.getX();
		float y = point.getY();

		for (int i = 0; i < size; i++) {
			ISprite s = sprites[i];
			float distance = MathUtils.max(1f, MathUtils.distanceBetween(s.getX(), s.getY(), x, y));
			MathUtils.rotateAroundDistance(point, s, x, y, angle, distance);
		}
	}

	public static void rotateAround(IEntity e, XY point, float angle) {

		float x = point.getX();
		float y = point.getY();

		for (int i = 0; i < e.getChildCount(); i++) {
			IEntity ec = e.getChildByIndex(i);
			float distance = MathUtils.max(1f, MathUtils.distanceBetween(ec.getX(), ec.getY(), x, y));
			MathUtils.rotateAroundDistance(point, ec, x, y, angle, distance);
		}

	}

	public static void triangle(Sprites spr, Triangle2f triangle, int stepRate) {
		final ISprite[] sprites = spr.getSprites();
		final int size = sprites.length;
		TArray<Point> p1 = new Line(triangle.getX1(), triangle.getY1(), triangle.getX2(), triangle.getY2())
				.getBresenhamPoints(stepRate);
		TArray<Point> p2 = new Line(triangle.getX2(), triangle.getY2(), triangle.getX3(), triangle.getY3())
				.getBresenhamPoints(stepRate);
		TArray<Point> p3 = new Line(triangle.getX3(), triangle.getY3(), triangle.getX1(), triangle.getY1())
				.getBresenhamPoints(stepRate);
		p1.pop();
		p2.pop();
		p3.pop();
		p1 = p1.concat(p2).concat(p3);
		int step = p1.size / size;
		int p = 0;
		for (int i = 0; i < size; i++) {
			ISprite sprite = sprites[i];
			Point point = p1.get(MathUtils.floor(p));
			sprite.setX(point.x);
			sprite.setY(point.y);
			p += step;
		}
	}

	public static void triangle(IEntity e, Triangle2f triangle, int stepRate) {
		TArray<Point> p1 = new Line(triangle.getX1(), triangle.getY1(), triangle.getX2(), triangle.getY2())
				.getBresenhamPoints(stepRate);
		TArray<Point> p2 = new Line(triangle.getX2(), triangle.getY2(), triangle.getX3(), triangle.getY3())
				.getBresenhamPoints(stepRate);
		TArray<Point> p3 = new Line(triangle.getX3(), triangle.getY3(), triangle.getX1(), triangle.getY1())
				.getBresenhamPoints(stepRate);
		p1.pop();
		p2.pop();
		p3.pop();
		p1 = p1.concat(p2).concat(p3);
		int step = p1.size / e.getChildCount();
		int p = 0;
		for (int i = 0; i < e.getChildCount(); i++) {
			IEntity sprite = e.getChildByIndex(i);
			Point point = p1.get(MathUtils.floor(p));
			sprite.setX(point.x);
			sprite.setY(point.y);
			p += step;
		}
	}

	public static void rect(Sprites spr, RectBox rect, int shift) {
		final ISprite[] sprites = spr.getSprites();
		final int size = sprites.length;
		final TArray<PointF> points = rect.getMarchingAnts(-1, size);
		if (shift > 0) {
			points.rotateLeft(shift);
		} else if (shift < 0) {
			points.rotateRight(MathUtils.abs(shift));
		}
		for (int i = 0; i < size; i++) {
			PointF point = points.get(i);
			ISprite sprite = sprites[i];
			sprite.setX(point.x);
			sprite.setY(point.y);
		}
	}

	public static void rect(IEntity e, RectBox rect, int shift) {
		final int size = e.getChildCount();
		final TArray<PointF> points = rect.getMarchingAnts(-1, size);
		if (shift > 0) {
			points.rotateLeft(shift);
		} else if (shift < 0) {
			points.rotateRight(MathUtils.abs(shift));
		}
		for (int i = 0; i < size; i++) {
			PointF point = points.get(i);
			IEntity sprite = e.getChildByIndex(i);
			sprite.setX(point.x);
			sprite.setY(point.y);
		}
	}

	public static void line(Sprites spr, Line line) {
		final ISprite[] sprs = spr.getSprites();
		final int size = sprs.length;
		TArray<PointF> points = line.getPoints(size, 0);
		for (int i = 0; i < size; i++) {
			ISprite s = sprs[i];
			PointF point = points.get(i);
			s.setX(point.x);
			s.setY(point.y);
		}
	}

	public static void line(IEntity e, Line line) {
		final int size = e.getChildCount();
		TArray<PointF> points = line.getPoints(0, 0);
		for (int i = 0; i < size; i++) {
			IEntity s = e.getChildByIndex(i);
			PointF point = points.get(i);
			s.setX(point.x);
			s.setY(point.y);
		}
	}

	public static void ellipse(Sprites spr, Ellipse ellipse, float startAngle, float endAngle) {
		if (startAngle == -1f) {
			startAngle = 0f;
		} else {
			startAngle = MathUtils.toRadians(startAngle);
		}
		if (endAngle == -1f) {
			endAngle = 6.28f;
		} else {
			endAngle = MathUtils.toRadians(endAngle);
		}
		float angle = startAngle;

		final ISprite[] sprs = spr.getSprites();

		final int size = sprs.length;
		float angleStep = (endAngle - startAngle) / size;

		float nrw = ellipse.getRadius1() / 2;
		float nrh = ellipse.getRadius2() / 2;

		for (int i = 0; i < size; i++) {
			ISprite sprite = sprs[i];
			float x = ellipse.getRealX() + nrw * MathUtils.cos(angle);
			float y = ellipse.getRealY() + nrh * MathUtils.sin(angle);
			sprite.setX(x);
			sprite.setY(y);
			angle += angleStep;
		}
	}

	public static void ellipse(IEntity e, Ellipse ellipse, float startAngle, float endAngle) {
		if (startAngle == -1f) {
			startAngle = 0f;
		} else {
			startAngle = MathUtils.toRadians(startAngle);
		}
		if (endAngle == -1f) {
			endAngle = 6.28f;
		} else {
			endAngle = MathUtils.toRadians(endAngle);
		}
		float angle = startAngle;

		final int size = e.getChildCount();
		float angleStep = (endAngle - startAngle) / size;

		float nrw = ellipse.getRadius1() / 2;
		float nrh = ellipse.getRadius2() / 2;

		for (int i = 0; i < size; i++) {
			IEntity sprite = e.getChildByIndex(i);
			float x = ellipse.getRealX() + nrw * MathUtils.cos(angle);
			float y = ellipse.getRealY() + nrh * MathUtils.sin(angle);
			sprite.setX(x);
			sprite.setY(y);
			angle += angleStep;
		}
	}

	public static void circle(Sprites spr, Circle circle, float startAngle, float endAngle) {
		if (startAngle == -1f) {
			startAngle = 0f;
		} else {
			startAngle = MathUtils.toRadians(startAngle);
		}
		if (endAngle == -1f) {
			endAngle = 6.28f;
		} else {
			endAngle = MathUtils.toRadians(endAngle);
		}

		final ISprite[] sprs = spr.getSprites();

		final int size = sprs.length;

		float angle = startAngle;

		final float angleStep = (endAngle - startAngle) / size;

		final float cx = circle.getX();
		final float cy = circle.getY();

		final float radius = circle.getRadius();

		for (int i = 0; i < size; i++) {
			ISprite sprite = sprs[i];
			float x = cx + (radius * MathUtils.cos(angle));
			float y = cy + (radius * MathUtils.sin(angle));
			sprite.setX(x);
			sprite.setY(y);
			angle += angleStep;
		}
	}

	public static void circle(IEntity e, Circle circle, float startAngle, float endAngle) {
		if (startAngle == -1f) {
			startAngle = 0f;
		} else {
			startAngle = MathUtils.toRadians(startAngle);
		}
		if (endAngle == -1f) {
			endAngle = 6.28f;
		} else {
			endAngle = MathUtils.toRadians(endAngle);
		}

		final int size = e.getChildCount();

		float angle = startAngle;

		final float angleStep = (endAngle - startAngle) / size;

		final float cx = circle.getX();
		final float cy = circle.getY();

		final float radius = circle.getRadius();

		for (int i = 0; i < size; i++) {
			IEntity sprite = e.getChildByIndex(i);
			float x = cx + (radius * MathUtils.cos(angle));
			float y = cy + (radius * MathUtils.sin(angle));
			sprite.setX(x);
			sprite.setY(y);
			angle += angleStep;
		}
	}

}
