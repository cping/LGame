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
using Loon.Utils;
namespace Loon.Physics {
	
	public class PConcavePolygonShape : PShape {
	
		internal PConvexPolygonShape[] convexes;
		internal Vector2f[] localVers;
		internal int numConvexes;
		internal int numVertices;
		internal PPolygonizer poly;
		internal PFigure fig;
		internal PTriangulator tri;
		internal Vector2f[] vers;
	
		public PConcavePolygonShape(float[] xvers, float[] yvers, float density) {
			fig = new PFigure();
			tri = new PTriangulator();
			poly = new PPolygonizer();
			numVertices = xvers.Length;
			localVers = new Vector2f[numVertices];
			vers = new Vector2f[numVertices];
			_dens = density;
			for (int i = 0; i < numVertices; i++) {
				localVers[i] = new Vector2f(xvers[i], yvers[i]);
				vers[i] = new Vector2f(xvers[i], yvers[i]);
			}
	
			fig.Figure(localVers, numVertices);
			numVertices = fig.numVertices;
			localVers = new Vector2f[numVertices];
			vers = new Vector2f[numVertices];
			for (int i_0 = 0; i_0 < numVertices; i_0++) {
				localVers[i_0] = new Vector2f(fig.done[i_0].x, fig.done[i_0].y);
				vers[i_0] = new Vector2f(fig.done[i_0].x, fig.done[i_0].y);
			}
			tri.Triangulate(fig.done, fig.numVertices);
			poly.Polygonize(tri.triangles, tri.numTriangles);
			convexes = new PConvexPolygonShape[1024];
			for (int i_1 = 0; i_1 < poly.numPolygons; i_1++) {
				convexes[i_1] = new PConvexPolygonShape(poly.polygons[i_1].xs,
						poly.polygons[i_1].ys, _dens);
			}
			numConvexes = poly.numPolygons;
			CalcMassData();
			_type = PShapeType.CONCAVE_SHAPE;
		}
	
		internal override void CalcAABB() {
			for (int i = 0; i < numConvexes; i++) {
				PConvexPolygonShape c = convexes[i];
				c.CalcAABB();
				c._sapAABB.Update();
				if (i == 0) {
					_aabb.Set(c._aabb.minX, c._aabb.minY, c._aabb.maxX,
							c._aabb.maxY);
				} else {
					_aabb.Set(MathUtils.Min(_aabb.minX, c._aabb.minX),
							MathUtils.Min(_aabb.minY, c._aabb.minY),
							MathUtils.Max(_aabb.maxX, c._aabb.maxX),
							MathUtils.Max(_aabb.maxY, c._aabb.maxY));
				}
			}
	
		}
	
		private void CalcMassData() {
			CorrectCenterOfGravity();
			mm = ii = 0.0F;
			for (int j = 0; j < numConvexes; j++) {
				mm += convexes[j].mm * convexes[j]._dens;
				ii += convexes[j].ii * convexes[j]._dens;
				ii += (convexes[j]._localPos.x * convexes[j]._localPos.x + convexes[j]._localPos.y
						* convexes[j]._localPos.y)
						* convexes[j].mm * convexes[j]._dens;
			}
	
		}
	
		private void CorrectCenterOfGravity() {
			float cy;
			float cx = cy = 0.0F;
			float total = 0.0F;
			for (int j = 0; j < numConvexes; j++) {
				total += convexes[j].mm * convexes[j]._dens;
				cx += convexes[j]._localPos.x * convexes[j].mm * convexes[j]._dens;
				cy += convexes[j]._localPos.y * convexes[j].mm * convexes[j]._dens;
			}
	
			if (numConvexes > 0) {
				total = 1.0F / total;
				cx *= total;
				cy *= total;
			}
			_localPos.x += cx;
			_localPos.y += cy;
			for (int i = 0; i < numVertices; i++) {
				localVers[i].x -= cx;
				localVers[i].y -= cy;
			}
			for (int j_0 = 0; j_0 < numConvexes; j_0++) {
				convexes[j_0]._localPos.x -= cx;
				convexes[j_0]._localPos.y -= cy;
			}
	
		}
	
		public PConvexPolygonShape[] GetConvexes() {
			return (PConvexPolygonShape[]) CollectionUtils.CopyOf(convexes,
					numConvexes);
		}
	
		public Vector2f[] GetVertices() {
			Vector2f[] vertices = new Vector2f[numVertices];
            System.Array.Copy(vers, 0, vertices, 0, numVertices);
			return vertices;
		}
	
		internal override void Update() {
			float twoPI = MathUtils.TWO_PI;
			for (int i = 0; i < numConvexes; i++) {
				PConvexPolygonShape c = convexes[i];
				c._pos.Set(c._localPos.x, c._localPos.y);
				_mAng.MulEqual(c._pos);
				c._pos.AddLocal(_pos);
				c._localAng = (c._localAng + twoPI) % twoPI;
				c._ang = _ang + c._localAng;
				c._mAng.SetRotate(c._ang);
				c.Update();
			}
			for (int i_0 = 0; i_0 < numVertices; i_0++) {
				vers[i_0].Set(localVers[i_0].x, localVers[i_0].y);
				_mAng.MulEqual(vers[i_0]);
				vers[i_0].AddLocal(_pos);
			}
		}
	
	}
}
