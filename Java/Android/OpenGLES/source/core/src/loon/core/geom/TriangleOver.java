package loon.core.geom;

/**
 * 
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
public class TriangleOver implements Triangle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float[][] triangles;

	public TriangleOver(Triangle t) {
		triangles = new float[t.getTriangleCount() * 6 * 3][2];

		int tcount = 0;
		for (int i = 0; i < t.getTriangleCount(); i++) {
			float cx = 0;
			float cy = 0;
			for (int p = 0; p < 3; p++) {
				float[] pt = t.getTrianglePoint(i, p);
				cx += pt[0];
				cy += pt[1];
			}

			cx /= 3;
			cy /= 3;

			for (int p = 0; p < 3; p++) {
				int n = p + 1;
				if (n > 2) {
					n = 0;
				}

				float[] pt1 = t.getTrianglePoint(i, p);
				float[] pt2 = t.getTrianglePoint(i, n);

				pt1[0] = (pt1[0] + pt2[0]) / 2;
				pt1[1] = (pt1[1] + pt2[1]) / 2;

				triangles[(tcount * 3) + 0][0] = cx;
				triangles[(tcount * 3) + 0][1] = cy;
				triangles[(tcount * 3) + 1][0] = pt1[0];
				triangles[(tcount * 3) + 1][1] = pt1[1];
				triangles[(tcount * 3) + 2][0] = pt2[0];
				triangles[(tcount * 3) + 2][1] = pt2[1];
				tcount++;
			}

			for (int p = 0; p < 3; p++) {
				int n = p + 1;
				if (n > 2) {
					n = 0;
				}

				float[] pt1 = t.getTrianglePoint(i, p);
				float[] pt2 = t.getTrianglePoint(i, n);

				pt2[0] = (pt1[0] + pt2[0]) / 2;
				pt2[1] = (pt1[1] + pt2[1]) / 2;

				triangles[(tcount * 3) + 0][0] = cx;
				triangles[(tcount * 3) + 0][1] = cy;
				triangles[(tcount * 3) + 1][0] = pt1[0];
				triangles[(tcount * 3) + 1][1] = pt1[1];
				triangles[(tcount * 3) + 2][0] = pt2[0];
				triangles[(tcount * 3) + 2][1] = pt2[1];
				tcount++;
			}
		}
	}

	@Override
	public void addPolyPoint(float x, float y) {
	}

	@Override
	public int getTriangleCount() {
		return triangles.length / 3;
	}

	@Override
	public float[] getTrianglePoint(int tri, int i) {
		float[] pt = triangles[(tri * 3) + i];

		return new float[] { pt[0], pt[1] };
	}

	@Override
	public void startHole() {
	}

	@Override
	public boolean triangulate() {
		return true;
	}

}
