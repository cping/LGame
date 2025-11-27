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
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.TArray;

public final class RaycastHelper {

	public static RaycastHit raycastLine(Vector2f origin, Vector2f direction, Line line) {
		Vector2f a = origin;
		Vector2f c = line.getStart();
		Vector2f d = line.getEnd();
		Vector2f ab = direction;
		Vector2f cd = d.sub(c);
		float abxcd = ab.cross(cd);
		if (MathUtils.abs(abxcd) < MathUtils.EPSILON) {
			return null;
		}
		Vector2f ac = c.sub(a);
		float s = ac.cross(cd) / abxcd;
		if (s <= 0 || s >= 1) {
			return null;
		}
		float t = ac.cross(ab) / abxcd;
		if (t <= 0 || t >= 1) {
			return null;
		}
		Vector2f normal = cd.nor().unit();
		if (direction.dot(normal) > 0) {
			normal.x *= -1;
			normal.y *= -1;
		}
		RaycastHit raycastHit = new RaycastHit();
		raycastHit.setPoint(a.add(ab.scale(s)));
		raycastHit.setNormal(normal);
		raycastHit.setFraction(s);
		return raycastHit;
	}

	public static RaycastHit raycastRect(Vector2f origin, Vector2f direction, RectBox rect) {
		float tmin = Float.MIN_VALUE, tmax = Float.MAX_VALUE;
		Vector2f normal = null;

		if (origin.x != 0.0) {
			float tx1 = (rect.x - origin.x) / direction.x;
			float tx2 = (rect.x + rect.width - origin.x) / direction.x;

			normal = Vector2f.at(-MathUtils.sign(direction.x), 0);

			tmin = MathUtils.max(tmin, MathUtils.min(tx1, tx2));
			tmax = MathUtils.min(tmax, MathUtils.max(tx1, tx2));
		}

		if (origin.y != 0.0) {
			float ty1 = (rect.y - origin.y) / direction.y;
			float ty2 = (rect.y + rect.height - origin.y) / direction.y;

			if (MathUtils.min(ty1, ty2) > tmin) {
				normal = Vector2f.at(0, -MathUtils.sign(direction.y));
			}

			tmin = MathUtils.max(tmin, MathUtils.min(ty1, ty2));
			tmax = MathUtils.min(tmax, MathUtils.max(ty1, ty2));
		}

		if (tmax >= tmin && tmin >= 0 && tmin <= 1) {
			RaycastHit raycastHit = new RaycastHit();
			raycastHit.setPoint(origin.add(direction.scale(tmin)));
			raycastHit.setNormal(normal);
			raycastHit.setFraction(tmin);
			return raycastHit;
		} else {
			return null;
		}
	}

	public static RaycastHit raycastCircle(Vector2f origin, Vector2f direction, Circle circle) {
		Vector2f a = origin;
		Vector2f c = circle.getCenterPos();
		Vector2f ab = direction;
		float A = ab.dot(ab);
		Vector2f centerToOrigin = a.sub(c);
		float B = 2 * ab.dot(centerToOrigin);
		float C = centerToOrigin.dot(centerToOrigin) - circle.getDoubleRadius();
		float disc = B * B - 4 * A * C;
		if ((A <= MathUtils.EPSILON) || (disc < 0)) {
			return null;
		} else if (disc == 0) {
			float t = -B / (2 * A);
			if (t >= 0 && t <= 1) {
				Vector2f point = a.add(ab.scale(t));
				RaycastHit raycastHit = new RaycastHit();
				raycastHit.setPoint(point);
				raycastHit.setNormal(point.sub(c));
				raycastHit.setFraction(t);
				return raycastHit;
			}
		} else {
			float t1 = (-B + MathUtils.sqrt(disc)) / (2 * A);
			float t2 = (-B - MathUtils.sqrt(disc)) / (2 * A);
			float t = -1;
			if (t1 >= 0 && t1 <= 1) {
				t = t1;
			}
			if (t2 >= 0 && t2 <= 1) {
				t = MathUtils.min(t2, t == -1 ? t2 : t);
			}
			if (t != -1) {
				Vector2f point = a.add(ab.scale(t));
				RaycastHit raycastHit = new RaycastHit();
				raycastHit.setPoint(point);
				raycastHit.setNormal(point.sub(c).unit());
				raycastHit.setFraction(t);
			}
		}
		return null;
	}

	public static RaycastHit raycastEllipse(Vector2f origin, Vector2f direction, Ellipse ellipse) {
		Affine2f T = ellipse.toAffine2f();
		Affine2f TI = T.invert();
		Vector2f Torigin = TI.transform(origin.sub(ellipse.getCenterPos()), new Vector2f());
		Vector2f Tdirection = TI.transform(direction, new Vector2f());
		RaycastHit result = raycastCircle(Torigin, Tdirection, new Circle(0, 0, 360));
		if (result != null) {
			Affine2f R = Affine2f.ofRotate(MathUtils.toRadians(-ellipse.getRotation()));
			Affine2f S = Affine2f.ofScale(ellipse.getRadius1(), ellipse.getRadius2());
			Vector2f p = S.transformPoint(result.getPoint());
			Vector2f point = T.transformPoint(result.getPoint()).add(ellipse.getCenterPos());
			float fraction = point.dist(origin) / direction.len();
			RaycastHit raycastHit = new RaycastHit();
			raycastHit.setPoint(point);
			raycastHit.setNormal(
					R.transformPoint(Vector2f.at(ellipse.getRadius2() * 2 * p.x, ellipse.getRadius1() * 2 * p.y))
							.unit());
			raycastHit.setFraction(fraction);
			return raycastHit;
		}
		return result;
	}

	public static RaycastHit raycastPolygon(Vector2f origin, Vector2f direction, Polygon polygon) {
		final TArray<Vector2f> points = polygon.getVertices();
		RaycastHit minHit = null;
		Vector2f prev = points.get(points.size - 1);
		for (int i = 0; i < points.size; i++) {
			Vector2f cur = points.get(i);
			RaycastHit hit = raycastLine(origin, direction, new Line(prev, cur));
			if (hit != null) {
				if (minHit == null || minHit.getFraction() > hit.getFraction()) {
					minHit = hit;
				}
			}
			prev = cur;
		}
		return minHit;
	}

	public static RaycastHit raycastGrid(Vector2f origin, Vector2f direction, float maxDistance) {
		Vector2f pos = origin;
		float len = direction.len();
		Vector2f dir = direction.scale(1 / len);
		float t = 0;
		Vector2f gridPos = Vector2f.at(MathUtils.floor(origin.x), MathUtils.floor(origin.y));
		Vector2f step = Vector2f.at(dir.x > 0 ? 1 : -1, dir.y > 0 ? 1 : -1);
		Vector2f tDelta = Vector2f.at(MathUtils.abs(1 / dir.x), MathUtils.abs(1 / dir.y));
		Vector2f dist = Vector2f.at((step.x > 0) ? (gridPos.x + 1 - origin.x) : (origin.x - gridPos.x),
				(step.y > 0) ? (gridPos.y + 1 - origin.y) : (origin.y - gridPos.y));
		Vector2f tMax = Vector2f.at((tDelta.x < Float.MAX_VALUE) ? tDelta.x * dist.x : Float.MAX_VALUE,
				(tDelta.y < Float.MAX_VALUE) ? tDelta.y * dist.y : Float.MAX_VALUE);
		float steppedIndex = -1;
		while (t <= maxDistance) {
			Vector2f hit = gridPos;
			if (!hit.isEmpty()) {
				RaycastHit raycastHit = new RaycastHit();
				raycastHit.setPoint(pos.add(dir.scale(t)));
				raycastHit.setNormal(Vector2f.at(steppedIndex == 0 ? -step.x : 0, steppedIndex == 1 ? -step.y : 0));
				raycastHit.setFraction(t / len);
				raycastHit.setGridPos(gridPos);
			}
			if (tMax.x < tMax.y) {
				gridPos.x += step.x;
				t = tMax.x;
				tMax.x += tDelta.x;
				steppedIndex = 0;
			} else {
				gridPos.y += step.y;
				t = tMax.y;
				tMax.y += tDelta.y;
				steppedIndex = 1;
			}
		}
		return null;
	}

}
