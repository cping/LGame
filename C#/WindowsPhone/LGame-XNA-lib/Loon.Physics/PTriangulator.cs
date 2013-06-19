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
	
	public class PTriangulator {
	
		internal PPolygon local;
		internal int numTriangles;
		private int numVertices;
		internal PPolygon[] triangles;
		private PVertexLoop vers;
	
		public PTriangulator() {
		}
	
		private float CalcArea(Vector2f v1, Vector2f v2, Vector2f v3) {
			return (v1.Cross(v2) + v2.Cross(v3) + v3.Cross(v1)) * 0.5F;
		}
	
		private float GetCross(PVertexLoop v) {
			return (v.v.x - v.next.v.x) * (v.prev.v.y - v.v.y)
					- (v.v.y - v.next.v.y) * (v.prev.v.x - v.v.x);
		}
	
		private PVertexLoop GetEar(PVertexLoop v) {
			for (int i = 0; i < numVertices; i++) {
				if (GetCross(v) <= 0.0F && !PointInPolygon(v)) {
					return v;
				}
				v = v.next;
			}
	
			return null;
		}

        private void MakeVertexList(Vector2f[] vertices)
        {
			numVertices = vertices.Length;
			vers = new PVertexLoop(vertices[0].x, vertices[0].y);
			PVertexLoop list = vers;
			for (int i = 1; i < numVertices; i++) {
				PVertexLoop next = new PVertexLoop(vertices[i].x, vertices[i].y);
				list.next = next;
				next.prev = list;
				list = next;
			}
	
			vers.prev = list;
			list.next = vers;
		}
	
		private bool PointInPolygon(PVertexLoop v) {
			PVertexLoop prev = v.prev;
			PVertexLoop next = v.next;
			float nor = prev.v.Sub(v.v).Cross(prev.v.Sub(next.v));
			PVertexLoop list = vers;
			for (int i = 0; i < numVertices; i++) {
				bool hit = true;
				if (list == prev || list == v || list == next) {
					hit = false;
				}
				Vector2f t1 = prev.v;
				Vector2f t2 = v.v;
				if (list.v.Sub(t1).Cross(list.v.Sub(t2)) * nor < 0.0F) {
					hit = false;
				} else {
					t1 = v.v;
					t2 = next.v;
					if (list.v.Sub(t1).Cross(list.v.Sub(t2)) * nor < 0.0F) {
						hit = false;
					} else {
						t1 = next.v;
						t2 = prev.v;
						if (list.v.Sub(t1).Cross(list.v.Sub(t2)) * nor < 0.0F) {
							hit = false;
						}
					}
				}
				if (hit) {
					return true;
				}
				list = list.next;
			}
	
			return false;
		}
	
		private void RemoveVertex(PVertexLoop v) {
			v.prev.next = v.next;
			v.next.prev = v.prev;
			numVertices--;
		}
	
		private void Triangulate() {
			if (numVertices < 3) {
				return;
			}
			PVertexLoop list = vers;
			for (int i = 0; i < numVertices; i++) {
				if (vers.v.x > list.v.x) {
					vers = list;
				}
				list = list.next;
			}
	
			vers = GetEar(vers);
			if (vers == null) {
				return;
			}
			if (CalcArea(vers.prev.v, vers.v, vers.next.v) > 1E-008F) {
				triangles[numTriangles++] = new PPolygon(new Vector2f[] {
						vers.prev.v, vers.v, vers.next.v });
			}
			RemoveVertex(vers);
			vers = vers.next;
			Triangulate();
		}
	
		public void Triangulate(Vector2f[] vertices, int numVers) {
			triangles = new PPolygon[numVers - 2];
			local = new PPolygon(vertices);
			if (!local.IsClockwise()) {
				Vector2f[] newVertices = new Vector2f[numVers];
				int num = 0;
				for (int i = numVers - 1; i >= 0; i--) {
					newVertices[num++] = vertices[i];
				}
				MakeVertexList(newVertices);
			} else {
				MakeVertexList(vertices);
			}
			numVertices = numVers;
			Triangulate();
		}
	
	}
}
