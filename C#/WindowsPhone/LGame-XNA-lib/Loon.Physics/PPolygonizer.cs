/// <summary>
/// Copyright 2013 The Loon Authors
/// Licensed under the Apache License, Version 2.0 (the "License"); you may not
/// use this file except in compliance with the License. You may obtain a copy of
/// the License at
/// http://www.apache.org/licenses/LICENSE-2.0
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
/// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
/// License for the specific language governing permissions and limitations under
/// the License.
/// </summary>
///
namespace Loon.Physics {

	public class PPolygonizer {
	
		internal int numPolygons;
		internal int numTriangles;
		internal PPolygon[] polygons;
		internal PPolygon[] triangles;
	
		public PPolygonizer() {
		}
	
		private PPolygon AddTriangleToPolygon(PPolygon bs, PPolygon triangle) {
			if (triangle.numVertices != 3)
				return null;
			int connectIndex = -1;
			int triangle2;
			int triangle1 = triangle2 = -1;
			for (int i = 0; i < bs.numVertices; i++) {
				for (int j = 0; j < 3; j++) {
					if (bs.xs[i] != triangle.xs[j]
							|| bs.ys[i] != triangle.ys[j]) {
						continue;
					}
					if (connectIndex == -1 || connectIndex == 0
							&& i == bs.numVertices - 1)
						connectIndex = i;
					if (triangle1 == -1) {
						triangle1 = j;
					} else {
						triangle2 = j;
					}
					break;
				}
	
			}
	
			if (triangle2 == -1) {
				return null;
			}
			int shouldAddIndex = 0;
			if (triangle1 == shouldAddIndex || triangle2 == shouldAddIndex) {
				shouldAddIndex = 1;
			}
			if (triangle1 == shouldAddIndex || triangle2 == shouldAddIndex) {
				shouldAddIndex = 2;
			}
			float[] xs = new float[bs.numVertices + 1];
			float[] ys = new float[bs.numVertices + 1];
			int count = 0;
			for (int i_0 = 0; i_0 < bs.numVertices; i_0++) {
				xs[count] = bs.xs[i_0];
				ys[count] = bs.ys[i_0];
				count++;
				if (i_0 == connectIndex) {
					xs[count] = triangle.xs[shouldAddIndex];
					ys[count] = triangle.ys[shouldAddIndex];
					count++;
				}
			}
	
			return new PPolygon(xs, ys);
		}
	
		private void Polygonize() {
			do {
				PPolygon bs = null;
				for (int i = 0; i < numTriangles; i++) {
					if (triangles[i].polygonized) {
						continue;
					}
					bs = triangles[i];
					bs.polygonized = true;
					break;
				}
	
				if (bs != null) {
					for (int i_0 = 0; i_0 < numTriangles; i_0++)
						if (!triangles[i_0].polygonized) {
							PPolygon next = AddTriangleToPolygon(bs, triangles[i_0]);
							if (next != null && next.IsConvex()) {
								triangles[i_0].polygonized = true;
								bs = next;
							}
						}
	
					polygons[numPolygons++] = bs;
				} else {
					return;
				}
			} while (true);
		}
	
		public void Polygonize(PPolygon[] t, int n) {
			this.triangles = t;
			this.numTriangles = n;
			polygons = new PPolygon[t.Length];
			Polygonize();
		}
	
	}
}
