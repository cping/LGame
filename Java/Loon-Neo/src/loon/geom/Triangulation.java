/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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

import loon.utils.TArray;

public class Triangulation {

	private final TArray<Vector2f> points;
	private final TArray<Vector2f> nonconvexPoints;

	private boolean isCw;

	public Triangulation(TArray<XY> points) {
		this.points = new TArray<Vector2f>();
		for (int i = 0; i < points.size(); i++) {
			this.points.add(new Vector2f(points.get(i)));
		}
		this.nonconvexPoints = new TArray<Vector2f>();
		calcPolyOrientation();
		calcNonConvexPoints();
	}

	private void calcNonConvexPoints() {

		if (points.size() <= 3) {
			return;
		}

		Vector2f p;
		Vector2f v;
		Vector2f u;

		float res = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			p = points.get(i);
			Vector2f tmp = points.get(i + 1);
			v = new Vector2f();
			v.x = tmp.x - p.x;
			v.y = tmp.y - p.y;

			if (i == points.size() - 2) {
				u = points.get(0);
			} else {
				u = points.get(i + 2);
			}
			res = u.x * v.y - u.y * v.x + v.x * p.y - v.y * p.x;
			if ((res > 0 && isCw) || (res <= 0 && !isCw)) {
				nonconvexPoints.add(tmp);
			}

		}
	}

	private void calcPolyOrientation() {
		if (points.size() < 3) {
			return;
		}

		int index = 0;
		Vector2f pointOfIndex = points.get(0);
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).x < pointOfIndex.x) {
				pointOfIndex = points.get(i);
				index = i;
			} else if (points.get(i).x == pointOfIndex.x && points.get(i).y > pointOfIndex.y) {
				pointOfIndex = points.get(i);
				index = i;
			}
		}

		Vector2f prevPointOfIndex;
		if (index == 0) {
			prevPointOfIndex = points.get(points.size() - 1);
		} else {
			prevPointOfIndex = points.get(index - 1);
		}
		Vector2f v1 = new Vector2f(pointOfIndex.x - prevPointOfIndex.x, pointOfIndex.y - prevPointOfIndex.y);

		Vector2f succPointOfIndex;
		if (index == points.size() - 1) {
			succPointOfIndex = points.get(0);
		} else {
			succPointOfIndex = points.get(index + 1);
		}

		float res = succPointOfIndex.x * v1.y - succPointOfIndex.y * v1.x + v1.x * prevPointOfIndex.y
				- v1.y * prevPointOfIndex.x;

		isCw = (res <= 0);

	}

	private boolean isEar(Vector2f p1, Vector2f p2, Vector2f p3) {
		if (!(isConvex(p1, p2, p3))) {
			return false;
		}
		for (int i = 0; i < nonconvexPoints.size(); i++) {
			if (Triangle2f.isInside(p1, p2, p3, nonconvexPoints.get(i)))
				return false;
		}
		return true;
	}

	private boolean isConvex(Vector2f p1, Vector2f p2, Vector2f p3) {
		Vector2f v = new Vector2f(p2.x - p1.x, p2.y - p1.y);
		float res = p3.x * v.y - p3.y * v.x + v.x * p1.y - v.y * p1.x;
		return !((res > 0 && isCw) || (res <= 0 && !isCw));
	}

	private int getIndex(int index, int offset) {
		int newindex;

		if (index + offset >= points.size()) {
			newindex = points.size() - (index + offset);
		} else {
			if (index + offset < 0) {
				newindex = points.size() + (index + offset);
			} else {
				newindex = index + offset;
			}
		}
		return newindex;
	}

	public TArray<Triangle2f> createTriangulates() {
		TArray<Triangle2f> triangles = new TArray<Triangle2f>();

		if (points.size() <= 3) {
			return triangles;
		}

		int index = 1;

		for (;points.size() > 3;) {
			if (isEar(points.get(getIndex(index, -1)), points.get(index), points.get(getIndex(index, 1)))) {
				triangles.add(new Triangle2f(points.get(getIndex(index, -1)), points.get(index),
						points.get(getIndex(index, 1))));
				points.remove(points.get(index));
				index = getIndex(index, -1);
			} else {
				index = getIndex(index, 1);
			}
		}

		triangles.add(new Triangle2f(points.get(0), points.get(1), points.get(2)));

		return triangles;
	}
}
