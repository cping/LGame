/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.geom;

import java.util.ArrayList;

import loon.utils.MathUtils;


public class ShapeUtils {

	public static Vector2f calculateVector(float angle, float magnitude) {
		Vector2f v = new Vector2f();
		v.x = MathUtils.sin(MathUtils.toRadians(angle));
		v.x *= magnitude;
		v.y = -MathUtils.cos(MathUtils.toRadians(angle));
		v.y *= magnitude;
		return v;
	}

	public static float calculateAngle(float x, float y, float x1, float y1) {
		float angle = MathUtils.atan2(y - y1, x - x1);
		return (MathUtils.toDegrees(angle) - 90);
	}

	public static float updateAngle(float currentAngle, float targetAngle,
			float step) {
		float pi = MathUtils.PI;

		currentAngle = (currentAngle + pi * 2) % (pi * 2);
		targetAngle = (targetAngle + pi * 2) % (pi * 2);

		if (MathUtils.abs(currentAngle - targetAngle) < step) {
			return targetAngle;
		}

		if (2 * pi - currentAngle + targetAngle < pi
				|| 2 * pi - targetAngle + currentAngle < pi) {
			if (currentAngle < targetAngle) {
				currentAngle -= step;
			} else {
				currentAngle += step;
			}
		} else {
			if (currentAngle < targetAngle) {
				currentAngle += step;
			} else {
				currentAngle -= step;
			}
		}
		return (2 * pi + currentAngle) % (2 * pi);
	}

	public static float updateLine(float value, float target, float step) {
		if (MathUtils.abs(value - target) < step)
			return target;
		if (value > target) {
			return value - step;
		}
		return value + step;
	}

	public static float getAngleDiff(float currentAngle, float targetAngle) {
		float pi = MathUtils.PI;
		currentAngle = (currentAngle + pi * 2) % (pi * 2);
		targetAngle = (targetAngle + pi * 2) % (pi * 2);

		float diff = MathUtils.abs(currentAngle - targetAngle);
		float v = MathUtils.abs(2 * pi - currentAngle + targetAngle);
		if (v < diff) {
			diff = v;
		}
		v = MathUtils.abs(2 * pi - targetAngle + currentAngle);
		if (v < diff) {
			diff = v;
		}
		return diff;
	}

	public static Vector2f rotateVector(Vector2f v, Vector2f center, float angle) {
		Vector2f result = new Vector2f();
		float x = v.x - center.x;
		float y = v.y - center.y;
		result.x = MathUtils.cos(angle) * x - MathUtils.sin(angle) * y
				+ center.x;
		result.y = MathUtils.sin(angle) * x + MathUtils.cos(angle) * y
				+ center.y;
		return result;
	}

	public static Triangle triangulate(Vector2f[] vertices) {
		return triangulate(new TriangleBasic(), vertices);
	}

	public static Triangle triangulate(Triangle triangulator,
			Vector2f[] vertices) {
		int size = vertices.length;
		for (int i = 0; i < size; i++) {
			triangulator.addPolyPoint(vertices[i].x, vertices[i].y);
		}
		triangulator.triangulate();
		return triangulator;
	}

	public static void calculateCenter(Vector2f[] vertices, Vector2f center) {
		center.x = 0f;
		center.y = 0f;
		for (int i = 0; i < vertices.length; i++) {
			center.x += vertices[i].x;
			center.y += vertices[i].y;
		}
		center.x /= vertices.length;
		center.y /= vertices.length;
	}

	public static void translateVertices(Vector2f[] vertices, Vector2f tx) {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].add(tx.x, tx.y);
		}
	}

	public static void calculateBounds(Vector2f[] vertices, RectBox bounds) {
		bounds.x = Float.MAX_VALUE;
		bounds.y = Float.MAX_VALUE;

		bounds.width = (int) -Float.MAX_VALUE;
		bounds.height = (int) -Float.MAX_VALUE;

		for (int i = 0; i < vertices.length; i++) {
			Vector2f v = vertices[i];

			if (v.x < bounds.x)
				bounds.x = v.x;

			if (v.y < bounds.y)
				bounds.y = v.y;

			if (v.x > bounds.x + bounds.width) {
				bounds.width = (int) (v.x - bounds.x);
			}

			if (v.y > bounds.y + bounds.height) {
				bounds.height = (int) (v.y - bounds.y);
			}
		}
	}

	public void rotate(Vector2f[] vertices, float angle) {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].rotate(angle);
		}
	}

	public static void calculateConvexHull(ArrayList<Vector2f> points,
			ArrayList<Vector2f> convexHullPoints) {
		if (points.size() <= 1) {
			return;
		}
		Vector2f p;
		Vector2f bot = points.get(0);
		for (int i = 1; i < points.size(); i++) {
			Vector2f point = points.get(i);
			if (point.y < bot.y)
				bot = point;
		}
		convexHullPoints.add(bot);
		p = bot;
		do {
			int i;
			i = points.get(0) == p ? 1 : 0;
			Vector2f cand = points.get(i);

			for (i = i + 1; i < points.size(); i++) {
				Vector2f point = points.get(i);
				if (point != p && area(p, cand, point) > 0)
					cand = points.get(i);
			}
			convexHullPoints.add(cand);
			p = cand;
		} while (p != bot);
	}

	public static float area(Vector2f a, Vector2f b, Vector2f c) {
		return area(a.x, a.y, b.x, b.y, c.x, c.y);
	}

	public static float area(float x0, float y0, float x1, float y1, float x2,
			float y2) {
		return x1 * y2 - y1 * x2 + x2 * y0 - y2 * x0 + x0 * y1 - y0 * x1;
	}
}
