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
using Loon.Core.Geom;
namespace Loon.Physics {

	
	public class PPolygon {
	
		internal int numVertices;
		internal bool polygonized;
		internal float[] xs;
		internal float[] ys;
	
		public PPolygon(float[] xs_0, float[] ys_1) {
			this.numVertices = xs_0.Length;
			this.xs = (float[]) xs_0.Clone();
			this.ys = (float[]) ys_1.Clone();
		}
	
		public PPolygon(float[] points):this(points, 1f) {
			
		}
	
		public PPolygon(float[] points, float scale) {
			this.numVertices = points.Length;
			int half = numVertices / 2;
			this.xs = new float[half];
			this.ys = new float[half];
			for (int i = 0, j = 0; i < numVertices; i += 2, j++) {
				this.xs[j] = points[i] / scale;
				this.ys[j] = points[i + 1] / scale;
			}
		}
	
		public PPolygon(Vector2f[] vertices) {
			numVertices = vertices.Length;
			xs = new float[numVertices];
			ys = new float[numVertices];
			for (int i = 0; i < numVertices; i++) {
				xs[i] = vertices[i].x;
				ys[i] = vertices[i].y;
			}
		}
	
		public bool IsClockwise() {
			float area = 0.0F;
			for (int i = 0; i < numVertices; i++) {
				int next = (i + 1) % numVertices;
				float x1 = xs[i];
				float y1 = ys[i];
				float x2 = xs[next];
				float y2 = ys[next];
				area += x1 * y2 - y1 * x2;
			}
			return area > 0.0F;
		}
	
		public bool IsConvex() {
			int cross = 0;
			for (int i = 0; i < numVertices; i++) {
				int prev = ((i + numVertices) - 1) % numVertices;
				int next = (i + 1) % numVertices;
				int c = ((xs[i] - xs[prev]) * (ys[next] - ys[i])
						- (xs[next] - xs[i]) * (ys[i] - ys[prev]) <= 0.0F) ? -1 : 1;
				if (cross == 0) {
					cross = c;
				} else {
					if (cross != c) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
