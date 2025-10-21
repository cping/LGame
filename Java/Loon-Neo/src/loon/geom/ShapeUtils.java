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
			source.addSelf(target);
		}
		return source;
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

	public static final Vector2f nearestToLine(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f n) {
		int ax = (int) (p2.x - p1.x), ay = (int) (p2.y - p1.y);
		float u = (p3.x - p1.x) * ax + (p3.y - p1.y) * ay;
		u /= (ax * ax + ay * ay);
		n.x = p1.x + MathUtils.round(ax * u);
		n.y = p1.y + MathUtils.round(ay * u);
		return n;
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

	public static final Triangle triangulate(Vector2f[] vertices) {
		return triangulate(new TriangleBasic(), vertices);
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
	 * 获得指定线经过的点
	 * 
	 * @param line
	 * @param stepRate
	 * @return
	 */
	public static final TArray<Vector2f> getBresenhamPoints(Line line, float stepRate) {
		if (stepRate < 1f) {
			stepRate = 1f;
		}
		TArray<Vector2f> results = new TArray<Vector2f>();

		float x1 = MathUtils.round(line.getX1());
		float y1 = MathUtils.round(line.getY1());
		float x2 = MathUtils.round(line.getX2());
		float y2 = MathUtils.round(line.getY2());

		float dx = MathUtils.abs(x2 - x1);
		float dy = MathUtils.abs(y2 - y1);
		float sx = (x1 < x2) ? 1 : -1;
		float sy = (y1 < y2) ? 1 : -1;
		int err = MathUtils.ceil(dx) - MathUtils.ceil(dy);

		results.add(new Vector2f(x1, y1));

		int i = 1;

		while (!((x1 == x2) && (y1 == y2))) {
			int e2 = err << 1;

			if (e2 > -dy) {
				err -= dy;
				x1 += sx;
			}

			if (e2 < dx) {
				err += dx;
				y1 += sy;
			}

			if (i % stepRate == 0) {
				results.add(new Vector2f(x1, y1));
			}

			i++;
		}

		return results;
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

	/**
	 * 获得两个三维体间初始XYZ位置的距离
	 * 
	 * @param target
	 * @param beforePlace
	 * @param distance
	 * @return
	 */
	public static Vector3f getDistantPoint(XYZ target, XYZ source, float distance) {

		float deltaX = target.getX() - source.getX();
		float deltaY = target.getY() - source.getY();
		float deltaZ = target.getZ() - source.getZ();

		float dist = MathUtils.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

		deltaX /= dist;
		deltaY /= dist;
		deltaZ /= dist;

		return new Vector3f(target.getX() - distance * deltaX, target.getY() - distance * deltaY,
				target.getZ() - distance * deltaZ);
	}

	/**
	 * 获得两个三维体间初始XYZ位置的距离
	 * 
	 * @param target
	 * @param source
	 * @param distance
	 * @return
	 */
	public static Vector2f distantPoint(XY target, XY source, float distance) {

		float deltaX = target.getX() - source.getX();
		float deltaY = target.getY() - source.getY();

		float dist = MathUtils.sqrt(deltaX * deltaX + deltaY * deltaY);

		deltaX /= dist;
		deltaY /= dist;

		return new Vector2f(target.getX() - distance * deltaX, target.getY() - distance * deltaY);
	}

	/**
	 * 获得两个矩形间初始XY位置的距离
	 * 
	 * @param target
	 * @param beforePlace
	 * @return
	 */
	public static float getDistance(final BoxSize target, final BoxSize beforePlace) {
		if (target == null || beforePlace == null) {
			return 0f;
		}
		final float xdiff = target.getX() - beforePlace.getX();
		final float ydiff = target.getY() - beforePlace.getY();
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

	/**
	 * 将点投射到直线位置上并返回结果
	 * 
	 * @param slopeOfLine
	 * @param interceptOfLineY
	 * @param point
	 * @return
	 */
	public static final Vector2f projectPointToLine(float slopeOfLine, float interceptOfLineY, XY point) {
		float perpendicularSlope = -1 / slopeOfLine;
		float boundSize = point.getY() - (perpendicularSlope * point.getX());
		float x = (boundSize - interceptOfLineY) / (slopeOfLine - perpendicularSlope);
		float y = (slopeOfLine * x) + interceptOfLineY;
		float v = MathUtils.abs(slopeOfLine);
		if (v == 0) {
			x = point.getX();
			y = interceptOfLineY;
		}
		return new Vector2f(x, y);
	}

	public static final float distanceSegmentrect(Segment segment, RectBox rect) {
		float dist = distancerectPoint(rect, segment._start);
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distancerectPoint(rect, segment._end));
		if (dist == 0f) {
			return 0f;
		}
		Vector3f a = segment._start;
		Vector3f b = segment._end;
		float dirX = b.x - a.x;
		float dirY = b.y - a.y;
		float dirZ = b.z - a.z;
		float originToCenterX = rect.getCenterX() - a.x;
		float originToCenterY = rect.getCenterY() - a.y;
		float originToCenterZ = a.z;
		float ab_x = b.x - a.x;
		float ab_y = b.y - a.y;
		float ab_z = b.z - a.z;
		float ab_dot_ab = (MathUtils.pow(ab_x, 2f) + MathUtils.pow(ab_y, 2f) + MathUtils.pow(ab_z, 2f));
		float projectionScalar = Vector3f.dot(originToCenterX, originToCenterY, originToCenterZ, ab_x, ab_y, ab_z)
				/ ab_dot_ab;
		projectionScalar = MathUtils.clamp(projectionScalar, 0f, 1f);
		float projectedCenterX = a.x + projectionScalar * dirX;
		float projectedCenterY = a.y + projectionScalar * dirY;
		float projectedCenterZ = a.z + projectionScalar * dirZ;
		dist = MathUtils.min(dist, distancerectPoint(rect, projectedCenterX, projectedCenterY, projectedCenterZ));
		if (dist == 0f) {
			return 0f;
		}
		Vector2f min = rect.getStart();
		Vector2f max = rect.getEnd();
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, min.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, min.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, min.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, min.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, max.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, max.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, max.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceSegmentPoint(segment, max.x, max.y, 0));
		return dist;
	}

	public static final float distanceRayrect(Ray ray, RectBox rect) {
		Vector3f rayOrigin = ray.getOrigin();
		Vector3f rayDir = ray.getDirection();
		float dist = distancerectPoint(rect, rayOrigin);
		if (dist == 0f) {
			return 0f;
		}
		float originToCenterX = rect.getCenterX() - rayOrigin.x;
		float originToCenterY = rect.getCenterY() - rayOrigin.y;
		float originToCenterZ = rayOrigin.z;
		float projectionScalar = rayDir.dot(originToCenterX, originToCenterY, originToCenterZ);
		projectionScalar = MathUtils.max(0.0F, projectionScalar);
		float projectedCenterX = rayOrigin.x + projectionScalar * rayDir.x;
		float projectedCenterY = rayOrigin.y + projectionScalar * rayDir.y;
		float projectedCenterZ = rayOrigin.z + projectionScalar * rayDir.z;
		dist = MathUtils.min(dist, distancerectPoint(rect, projectedCenterX, projectedCenterY, projectedCenterZ));
		if (dist == 0f) {
			return 0f;
		}
		Vector2f min = rect.getStart();
		Vector2f max = rect.getEnd();
		dist = MathUtils.min(dist, distanceRayPoint(ray, min.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, min.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, min.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, min.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, max.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, max.x, min.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, max.x, max.y, 0));
		if (dist == 0f) {
			return 0f;
		}
		dist = MathUtils.min(dist, distanceRayPoint(ray, max.x, max.y, 0));
		return dist;
	}

	public static final float distancerectPoint(RectBox rect, Vector3f point) {
		Vector2f min = rect.getStart();
		Vector2f max = rect.getEnd();
		float clampedX = MathUtils.clamp(point.x, min.x, max.x);
		float clampedY = MathUtils.clamp(point.y, min.y, max.y);
		float clampedZ = MathUtils.clamp(point.z, 0, 0);
		return point.dst(clampedX, clampedY, clampedZ);
	}

	public static final float distancerectPoint(RectBox rect, float pointX, float pointY, float pointZ) {
		Vector2f min = rect.getStart();
		Vector2f max = rect.getEnd();
		float clampedX = MathUtils.clamp(pointX, min.x, max.x);
		float clampedY = MathUtils.clamp(pointY, min.y, max.y);
		float clampedZ = MathUtils.clamp(pointZ, 0, 0);
		return Vector3f.dst(pointX, pointY, pointZ, clampedX, clampedY, clampedZ);
	}

	public static final float distanceRayPoint(Ray ray, XYZ point) {
		return distanceRayPoint(ray, point.getX(), point.getY(), point.getZ());
	}

	public static final float distanceRayPoint(Ray ray, float pointX, float pointY, float pointZ) {
		Vector3f rayOrigin = ray.getOrigin();
		Vector3f rayDir = ray.getDirection();
		float originToPointX = pointX - rayOrigin.x;
		float originToPointY = pointY - rayOrigin.y;
		float originToPointZ = pointZ - rayOrigin.z;
		float projectionScalar = rayDir.dot(originToPointX, originToPointY, originToPointZ);
		projectionScalar = MathUtils.max(0.0F, projectionScalar);
		float projectedX = rayOrigin.x + projectionScalar * rayDir.x;
		float projectedY = rayOrigin.y + projectionScalar * rayDir.y;
		float projectedZ = rayOrigin.z + projectionScalar * rayDir.z;
		return Vector3f.dst(pointX, pointY, pointZ, projectedX, projectedY, projectedZ);
	}

	public static final float distanceSegmentPoint(Segment segment, XYZ point) {
		return distanceSegmentPoint(segment, point.getX(), point.getY(), point.getZ());
	}

	public static final Vector3f alignAngleVectorTowardTarget(Vector3f source, Vector3f target, float maxAngleDeg) {
		float angleAx = (source.x % 360f + 360f) % 360f;
		float angleAy = (source.y % 360f + 360f) % 360f;
		float angleAz = (source.z % 360f + 360f) % 360f;
		float angleTx = (target.x % 360f + 360f) % 360f;
		float angleTy = (target.y % 360f + 360f) % 360f;
		float angleTz = (target.z % 360f + 360f) % 360f;
		float dx = MathUtils.abs(angleAx - angleTx);
		float dy = MathUtils.abs(angleAy - angleTy);
		float dz = MathUtils.abs(angleAz - angleTz);
		if (dx > 180f)
			dx = 360f - dx;
		if (dy > 180f)
			dy = 360f - dy;
		if (dz > 180f)
			dz = 360f - dz;
		if (dx > maxAngleDeg)
			if (angleAx < angleTx) {
				angleAx = angleTx - maxAngleDeg;
			} else {
				angleAx = angleTx + maxAngleDeg;
			}
		if (dy > maxAngleDeg)
			if (angleAy < angleTy) {
				angleAy = angleTy - maxAngleDeg;
			} else {
				angleAy = angleTy + maxAngleDeg;
			}
		if (dz > maxAngleDeg)
			if (angleAz < angleTz) {
				angleAz = angleTz - maxAngleDeg;
			} else {
				angleAz = angleTz + maxAngleDeg;
			}
		source.x = angleAx;
		source.y = angleAy;
		source.z = angleAz;
		return source;
	}

	public static final float distanceSegmentPoint(Segment segment, float pointX, float pointY, float pointZ) {
		Vector3f a = segment._start;
		Vector3f b = segment._end;
		float originToPointX = pointX - a.x;
		float originToPointY = pointY - a.y;
		float originToPointZ = pointZ - a.z;
		float dirX = b.x - a.x;
		float dirY = b.y - a.y;
		float dirZ = b.z - a.z;
		float ab_x = b.x - a.x;
		float ab_y = b.y - a.y;
		float ab_z = b.z - a.z;
		float ab_dot_ab = (MathUtils.pow(ab_x, 2f) + MathUtils.pow(ab_y, 2f) + MathUtils.pow(ab_z, 2f));
		float projectionScalar = Vector3f.dot(originToPointX, originToPointY, originToPointZ, ab_x, ab_y, ab_z)
				/ ab_dot_ab;
		projectionScalar = MathUtils.clamp(projectionScalar, 0f, 1f);
		float projectedX = a.x + projectionScalar * dirX;
		float projectedY = a.y + projectionScalar * dirY;
		float projectedZ = a.z + projectionScalar * dirZ;
		return Vector3f.dst(pointX, pointY, pointZ, projectedX, projectedY, projectedZ);
	}

	public static final float norDot(Vector3f a, Vector3f b) {
		return a.dot(b) / a.len() * b.len();
	}

	public static final void alignVectorTowardTarget(Vector3f source, Vector3f target, float dotThreshold) {
		if (source.isZero()) {
			source.set(target);
			return;
		}
		if (target.isZero()) {
			return;
		}
		float al = source.len();
		float tl = target.len();
		float dot = source.dot(target) / al * tl;
		if (dot < dotThreshold) {
			float epsilon = dotThreshold - dot;
			source.nor().addSelf(target.x * epsilon / tl, target.y * epsilon / tl, target.z * epsilon / tl).norSelf()
					.scaleSelf(al);
		}
	}

	public static final float bilinear(float x1y1, float x2y1, float x1y2, float x2y2, float x1, float x2, float y1,
			float y2, float x, float y) {
		float interpY1 = (x2 - x) / (x2 - x1) * x1y1 + (x - x1) / (x2 - x1) * x2y1;
		float interpY2 = (x2 - x) / (x2 - x1) * x1y2 + (x - x1) / (x2 - x1) * x2y2;
		return (y2 - y) / (y2 - y1) * interpY1 + (y - y1) / (y2 - y1) * interpY2;
	}

	public static final float bilinearNormalized(float x1y1, float x2y1, float x1y2, float x2y2, float u, float v) {
		return bilinear(x1y1, x2y1, x1y2, x2y2, 0f, 1f, 0f, 1f, u, v);
	}

	public static final float cosineLerp(float a, float b, float t) {
		float t2 = (1f - MathUtils.cos(t * MathUtils.PI)) * 0.5f;
		return a * (1f - t2) + b * t2;
	}

	public static final float cosineBilinear(float x1y1, float x2y1, float x1y2, float x2y2, float x1, float x2,
			float y1, float y2, float x, float y) {
		float tx = (x - x1) / (x2 - x1);
		float ty = (y - y1) / (y2 - y1);
		float interpY1 = cosineLerp(x1y1, x2y1, tx);
		float interpY2 = cosineLerp(x1y2, x2y2, tx);
		return cosineLerp(interpY1, interpY2, ty);
	}

	public static final float cosineBilinearNormalized(float x1y1, float x2y1, float x1y2, float x2y2, float u,
			float v) {
		return cosineBilinear(x1y1, x2y1, x1y2, x2y2, 0f, 1f, 0f, 1f, u, v);
	}
}
