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
package loon.geom;

import loon.LSysException;
import loon.utils.TArray;

public class TriangleBasic implements Triangle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final float EPSILON = 0.0000000001f;

	private final TArray<PointF> poly = new TArray<PointF>();

	private final TArray<PointF> tris = new TArray<PointF>();

	private boolean tried;

	public TriangleBasic() {
	}
	
	@Override
	public void addPolyPoint(float x, float y) {
		PointF p = new PointF(x, y);
		if (!poly.contains(p)) {
			poly.add(p);
		}
	}

	public int getPolyPointCount() {
		return poly.size();
	}

	public float[] getPolyPoint(int index) {
		return new float[] { poly.get(index).x, poly.get(index).y };
	}

	@Override
	public boolean triangulate() {
		tried = true;

		boolean worked = process(poly, tris);
		return worked;
	}
	
	@Override
	public int getTriangleCount() {
		if (!tried) {
			throw new LSysException("this not Triangle !");
		}
		return tris.size() / 3;
	}
	
	@Override
	public float[] getTrianglePoint(int t, int i) {
		if (!tried) {
			throw new LSysException("this not Triangle !");
		}

		return tris.get((t * 3) + i).toArray();
	}

	private float area(TArray<PointF> contour) {
		int n = contour.size();

		float sA = 0.0f;

		for (int p = n - 1, q = 0; q < n; p = q++) {
			PointF contourP = contour.get(p);
			PointF contourQ = contour.get(q);

			sA += contourP.getX() * contourQ.getY() - contourQ.getX()
					* contourP.getY();
		}
		return sA * 0.5f;
	}

	private boolean insideTriangle(float Ax, float Ay, float Bx, float By,
			float Cx, float Cy, float Px, float Py) {
		float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
		float cCROSSap, bCROSScp, aCROSSbp;

		ax = Cx - Bx;
		ay = Cy - By;
		bx = Ax - Cx;
		by = Ay - Cy;
		cx = Bx - Ax;
		cy = By - Ay;
		apx = Px - Ax;
		apy = Py - Ay;
		bpx = Px - Bx;
		bpy = Py - By;
		cpx = Px - Cx;
		cpy = Py - Cy;

		aCROSSbp = ax * bpy - ay * bpx;
		cCROSSap = cx * apy - cy * apx;
		bCROSScp = bx * cpy - by * cpx;

		return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
	}

	private boolean snip(TArray<PointF> contour, int u, int v, int w, int n, int[] V) {
		int p;
		float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

		Ax = contour.get(V[u]).getX();
		Ay = contour.get(V[u]).getY();

		Bx = contour.get(V[v]).getX();
		By = contour.get(V[v]).getY();

		Cx = contour.get(V[w]).getX();
		Cy = contour.get(V[w]).getY();

		if (EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax)))) {
			return false;
		}

		for (p = 0; p < n; p++) {
			if ((p == u) || (p == v) || (p == w)) {
				continue;
			}

			Px = contour.get(V[p]).getX();
			Py = contour.get(V[p]).getY();

			if (insideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py)) {
				return false;
			}
		}

		return true;
	}

	private boolean process(TArray<PointF> contour, TArray<PointF> result) {
		result.clear();

		int n = contour.size();
		if (n < 3) {
			return false;
		}
		int[] sV = new int[n];

		if (0.0f < area(contour)) {
			for (int v = 0; v < n; v++) {
				sV[v] = v;
			}
		} else {
			for (int v = 0; v < n; v++) {
				sV[v] = (n - 1) - v;
			}
		}

		int nv = n;

		int count = 2 * nv;

		for (int v = nv - 1; nv > 2;) {

			if (0 >= (count--)) {
				return false;
			}

			int u = v;
			if (nv <= u) {
				u = 0;
			}
			v = u + 1;
			if (nv <= v) {
				v = 0;
			}
			int w = v + 1;
			if (nv <= w) {
				w = 0;
			}
			if (snip(contour, u, v, w, nv, sV)) {
				int a, b, c, s, t;

				a = sV[u];
				b = sV[v];
				c = sV[w];

				result.add(contour.get(a));
				result.add(contour.get(b));
				result.add(contour.get(c));

				for (s = v, t = v + 1; t < nv; s++, t++) {
					sV[s] = sV[t];
				}
				nv--;

				count = 2 * nv;
			}
		}

		return true;
	}


	@Override
	public void startHole() {

	}
}
