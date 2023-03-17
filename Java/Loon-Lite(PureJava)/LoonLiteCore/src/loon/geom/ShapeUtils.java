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
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.TArray;

public class ShapeUtils {

	public static final Vector2f calculateVector(float angle, float magnitude) {
		Vector2f v = new Vector2f();
		v.x = MathUtils.sin(MathUtils.toRadians(angle));
		v.x *= magnitude;
		v.y = -MathUtils.cos(MathUtils.toRadians(angle));
		v.y *= magnitude;
		return v;
	}

	public static final float calculateAngle(float x, float y, float x1, float y1) {
		float angle = MathUtils.atan2(y - y1, x - x1);
		return (MathUtils.toDegrees(angle) - 90);
	}

	public static final float updateAngle(float currentAngle, float targetAngle, float step) {
		float pi = MathUtils.PI;

		currentAngle = (currentAngle + pi * 2) % (pi * 2);
		targetAngle = (targetAngle + pi * 2) % (pi * 2);

		if (MathUtils.abs(currentAngle - targetAngle) < step) {
			return targetAngle;
		}

		if (2 * pi - currentAngle + targetAngle < pi || 2 * pi - targetAngle + currentAngle < pi) {
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

	public static final float updateLine(float value, float target, float step) {
		if (MathUtils.abs(value - target) < step)
			return target;
		if (value > target) {
			return value - step;
		}
		return value + step;
	}

	public static final float getAngleDiff(float currentAngle, float targetAngle) {
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

	public static final Vector2f rotateVector(Vector2f v, Vector2f center, float angle) {
		Vector2f result = new Vector2f();
		float x = v.x - center.x;
		float y = v.y - center.y;
		result.x = MathUtils.cos(angle) * x - MathUtils.sin(angle) * y + center.x;
		result.y = MathUtils.sin(angle) * x + MathUtils.cos(angle) * y + center.y;
		return result;
	}

	public static final Triangle triangulate(Triangle triangulator, Vector2f[] vertices) {
		int size = vertices.length;
		for (int i = 0; i < size; i++) {
			triangulator.addPolyPoint(vertices[i].x, vertices[i].y);
		}
		triangulator.triangulate();
		return triangulator;
	}

	public static final void calculateCenter(Vector2f[] vertices, Vector2f center) {
		center.x = 0f;
		center.y = 0f;
		for (Vector2f element : vertices) {
			center.x += element.x;
			center.y += element.y;
		}
		center.x /= vertices.length;
		center.y /= vertices.length;
	}

	public static final void translateVertices(Vector2f[] vertices, Vector2f tx) {
		for (Vector2f element : vertices) {
			element.addSelf(tx.x, tx.y);
		}
	}

	public static final void calculateBounds(Vector2f[] vertices, RectBox bounds) {
		bounds.x = Integer.MAX_VALUE;
		bounds.y = Integer.MAX_VALUE;

		bounds.width = -Integer.MAX_VALUE;
		bounds.height = -Integer.MAX_VALUE;

		for (Vector2f v : vertices) {
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
		for (Vector2f element : vertices) {
			element.rotateSelf(angle);
		}
	}

	public static final void calculateConvexHull(TArray<Vector2f> points, TArray<Vector2f> convexHullPoints) {
		if (points.size <= 1) {
			return;
		}
		Vector2f p;
		Vector2f bot = points.get(0);
		for (int i = 1; i < points.size; i++) {
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

			for (i = i + 1; i < points.size; i++) {
				Vector2f point = points.get(i);
				if (point != p && area(p, cand, point) > 0)
					cand = points.get(i);
			}
			convexHullPoints.add(cand);
			p = cand;
		} while (p != bot);
	}

	public static final float area(Vector2f a, Vector2f b, Vector2f c) {
		return area(a.x, a.y, b.x, b.y, c.x, c.y);
	}

	public static final float area(float x0, float y0, float x1, float y1, float x2, float y2) {
		return x1 * y2 - y1 * x2 + x2 * y0 - y2 * x0 + x0 * y1 - y0 * x1;
	}

	public static final float getScaleFactor(float srcSize, float dstSize) {
		float dScale = 1;
		if (srcSize > dstSize) {
			dScale = dstSize / srcSize;
		} else {
			dScale = dstSize / srcSize;
		}
		return dScale;
	}

	public static final float getScaleFactorToFit(float ox, float oy, float nx, float ny) {
		float dScaleWidth = getScaleFactor(ox, nx);
		float dScaleHeight = getScaleFactor(oy, ny);
		return MathUtils.min(dScaleHeight, dScaleWidth);
	}

	public static final float snapToNearest(float number, float interval) {
		interval = MathUtils.abs(interval);
		if (interval == 0) {
			return number;
		}
		return MathUtils.round(number / interval) * interval;
	}

	public static final float lockAtIntervals(float number, float interval) {
		interval = MathUtils.abs(interval);
		if (interval == 0) {
			return number;
		}
		return ((int) (number / interval)) * interval;
	}

	public static final float calcRotationAngleInDegrees(float x, float y, float tx, float ty) {
		float theta = MathUtils.atan2(tx - x, ty - y);
		float angle = theta * MathUtils.RAD_TO_DEG;
		if (angle < 0) {
			angle += 360;
		}
		angle += 180;
		return angle;
	}

	public static final float calcRotationAngleInRadians(float x, float y, float tx, float ty) {
		return calcRotationAngleInDegrees(x, y, tx, ty) * MathUtils.DEG_TO_RAD;
	}

	public static final float calcRadiansDiff(float x, float y, float tx, float ty) {
		float d = calcRotationAngleInDegrees(x, y, tx, ty);
		d -= 90;
		d %= 360;
		return MathUtils.toRadians(d);
	}

	public static final int dot(Vector2f v1s, Vector2f v1e, Vector2f v2s, Vector2f v2e) {
		return (int) ((v1e.x - v1s.x) * (v2e.x - v2s.x) + (v1e.y - v1s.y) * (v2e.y - v2s.y));
	}

	public static final int dot(int v1sx, int v1sy, int v1ex, int v1ey, int v2sx, int v2sy, int v2ex, int v2ey) {
		return ((v1ex - v1sx) * (v2ex - v2sx) + (v1ey - v1sy) * (v2ey - v2sy));
	}

	public static final int dot(Vector2f vs, Vector2f v1e, Vector2f v2e) {
		return (int) ((v1e.x - vs.x) * (v2e.x - vs.x) + (v1e.y - vs.y) * (v2e.y - vs.y));
	}

	public static final int dot(int vsx, int vsy, int v1ex, int v1ey, int v2ex, int v2ey) {
		return ((v1ex - vsx) * (v2ex - vsx) + (v1ey - vsy) * (v2ey - vsy));
	}

	public static final float dotf(float vsx, float vsy, float v1ex, float v1ey, float v2ex, float v2ey) {
		return ((v1ex - vsx) * (v2ex - vsx) + (v1ey - vsy) * (v2ey - vsy));
	}

	public static final void transPointList(float[] points, float x, float y) {
		int i = 0, len = points.length;
		for (i = 0; i < len; i += 2) {
			points[i] += x;
			points[i + 1] += y;
		}
	}

	public static final void transPointList(int[] points, int x, int y) {
		int i = 0, len = points.length;
		for (i = 0; i < len; i += 2) {
			points[i] += x;
			points[i + 1] += y;
		}
	}

	public static final float ptSegDist(float x1, float y1, float x2, float y2, float px, float py) {
		return MathUtils.sqrt(ptSegDistSq(x1, y1, x2, y2, px, py));
	}

	public static final float ptSegDistSq(float x1, float y1, float x2, float y2, float px, float py) {
		x2 -= x1;
		y2 -= y1;
		px -= x1;
		py -= y1;
		float dotprod = px * x2 + py * y2;
		float projlenSq;
		if (dotprod <= 0.0) {
			projlenSq = 0.0f;
		} else {
			px = x2 - px;
			py = y2 - py;
			dotprod = px * x2 + py * y2;
			if (dotprod <= 0.0) {
				projlenSq = 0.0f;
			} else {
				projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
			}
		}
		float lenSq = px * px + py * py - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

	public static final float ptLineDist(float x1, float y1, float x2, float y2, float px, float py) {
		return MathUtils.sqrt(ptLineDistSq(x1, y1, x2, y2, px, py));
	}

	public static final float ptLineDistSq(float x1, float y1, float x2, float y2, float px, float py) {
		x2 -= x1;
		y2 -= y1;
		px -= x1;
		py -= y1;
		float dotprod = px * x2 + py * y2;
		float projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
		float lenSq = px * px + py * py - projlenSq;
		if (lenSq < 0) {
			lenSq = 0;
		}
		return lenSq;
	}

}
