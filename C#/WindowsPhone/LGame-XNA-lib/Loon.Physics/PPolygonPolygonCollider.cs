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
	
	public class PPolygonPolygonCollider : PCollider {
		private class PWContactedVertex {
	
			public virtual PPolygonPolygonCollider.PWContactedVertex  Clone() {
				PPolygonPolygonCollider.PWContactedVertex  cv = new PPolygonPolygonCollider.PWContactedVertex ();
				cv.v = v.Clone();
				cv.data = data.Clone();
				return cv;
			}
	
			internal PContactData data;
			internal Vector2f v;
	
			public PWContactedVertex() {
	
				v = new Vector2f();
				data = new PContactData();
			}
		}
	
		private class PWDistanceData {
	
			internal float dist;
			internal int edge;
	
			internal PWDistanceData() {
	
				dist = 0.0F;
				edge = -1;
			}
		}
	
		public PPolygonPolygonCollider() {
		}
	
		private PPolygonPolygonCollider.PWContactedVertex [] ClipEdge(PPolygonPolygonCollider.PWContactedVertex [] clips,
				Vector2f normal, float dist) {
			PPolygonPolygonCollider.PWContactedVertex [] line = new PPolygonPolygonCollider.PWContactedVertex [2];
			int numClips = 0;
			float dist0 = normal.Dot(clips[0].v) - dist;
			float dist1 = normal.Dot(clips[1].v) - dist;
			if (dist0 < 0.0F) {
				line[numClips] = clips[0];
				numClips++;
			}
			if (dist1 < 0.0F) {
				line[numClips] = clips[1];
				numClips++;
			}
			if (numClips == 0)
				return null;
			if (numClips == 2)
				return line;
			int c = 0;
			if (dist0 < 0.0F && dist1 > 0.0F)
				c = 1;
			float d = dist0 / (dist0 - dist1);
			line[1] = new PPolygonPolygonCollider.PWContactedVertex ();
			line[1].v = clips[1].v.Sub(clips[0].v).Clone();
			line[1].v.MulLocal(d);
			line[1].v.AddLocal(clips[0].v);
			line[1].data = clips[c].data;
			return line;
		}
	
		public virtual int Collide(PShape s1, PShape s2, PContact[] cs) {
			if (s1._type != PShapeType.CONVEX_SHAPE
					&& s1._type != PShapeType.BOX_SHAPE
					|| s2._type != PShapeType.CONVEX_SHAPE
					&& s2._type != PShapeType.BOX_SHAPE) {
				return 0;
			}
			PConvexPolygonShape p1 = (PConvexPolygonShape) s1;
			PConvexPolygonShape p2 = (PConvexPolygonShape) s2;
			PPolygonPolygonCollider.PWDistanceData  dis1 = GetDistance(p1, p2);
			if (dis1.dist > 0.0F)
				return 0;
			PPolygonPolygonCollider.PWDistanceData  dis2 = GetDistance(p2, p1);
			if (dis2.dist > 0.0F)
				return 0;
			float error = 0.008F;
			int edgeA;
			PConvexPolygonShape pa;
			PConvexPolygonShape pb;
			bool flip;
			if (dis1.dist > dis2.dist + error) {
				pa = p1;
				pb = p2;
				edgeA = dis1.edge;
				flip = false;
			} else {
				pa = p2;
				pb = p1;
				edgeA = dis2.edge;
				flip = true;
			}
			Vector2f normal = pa.nors[edgeA];
			Vector2f tangent = new Vector2f(-normal.y, normal.x);
			Vector2f[] paVers = pa.vers;
			PPolygonPolygonCollider.PWContactedVertex [] cv = GetEdgeOfPotentialCollision(pa, pb, edgeA,
					flip);
			cv = ClipEdge(cv, tangent.Negate(), -tangent.Dot(paVers[edgeA]));
			if (cv == null)
				return 0;
			cv = ClipEdge(cv, tangent,
					tangent.Dot(paVers[(edgeA + 1) % pa.numVertices]));
			if (cv == null)
				return 0;
			Vector2f contactNormal = (flip) ? normal : normal.Negate();
			int numContacts = 0;
			for (int i = 0; i < 2; i++) {
				float dist = normal.Dot(cv[i].v) - normal.Dot(paVers[edgeA]);
				if (dist < 0.0F) {
					PContact c = new PContact();
					c.normal.Set(contactNormal.x, contactNormal.y);
					c.pos.Set(cv[i].v.x, cv[i].v.y);
					c.overlap = dist;
					c.data = cv[i].data;
					c.data.flip = flip;
					cs[numContacts] = c;
					numContacts++;
				}
			}
	
			return numContacts;
		}
	
		private PPolygonPolygonCollider.PWDistanceData  GetDistance(PConvexPolygonShape p1,
				PConvexPolygonShape p2) {
			PPolygonPolygonCollider.PWDistanceData  distance = new PPolygonPolygonCollider.PWDistanceData ();
			Vector2f firstScan = p2._pos.Sub(p1._pos);
			float dist = 1.0F;
			int edgeNumber = -1;
			for (int i = 0; i < p1.numVertices; i++) {
				float dot = p1.nors[i].Dot(firstScan);
				if (dot > dist || dist == 1.0F) {
					dist = dot;
					edgeNumber = i;
				}
			}
	
			float edgeDist = GetEdgeDistance(p1, p2, edgeNumber);
			if (edgeDist > 0.0F) {
				distance.dist = edgeDist;
				distance.edge = -1;
				return distance;
			}
			float nextEdgeDist = GetEdgeDistance(p1, p2, (edgeNumber + 1)
					% p1.numVertices);
			if (nextEdgeDist > 0.0F) {
				distance.dist = nextEdgeDist;
				distance.edge = -1;
				return distance;
			}
			float prevEdgeDist = GetEdgeDistance(p1, p2,
					((edgeNumber + p1.numVertices) - 1) % p1.numVertices);
			if (prevEdgeDist > 0.0F) {
				distance.dist = prevEdgeDist;
				distance.edge = -1;
				return distance;
			}
			float mimimumDistance;
			int mimimumEdgeNumber;
			if (edgeDist > nextEdgeDist && edgeDist > prevEdgeDist) {
				mimimumDistance = edgeDist;
				mimimumEdgeNumber = edgeNumber;
				distance.dist = mimimumDistance;
				distance.edge = mimimumEdgeNumber;
				return distance;
			}
			int signal;
			if (nextEdgeDist > prevEdgeDist) {
				mimimumDistance = nextEdgeDist;
				mimimumEdgeNumber = (edgeNumber + 1) % p1.numVertices;
				signal = 1;
			} else {
				mimimumDistance = prevEdgeDist;
				mimimumEdgeNumber = ((edgeNumber + p1.numVertices) - 1)
						% p1.numVertices;
				signal = p1.numVertices - 1;
			}
			do {
				edgeNumber = (mimimumEdgeNumber + signal) % p1.numVertices;
				nextEdgeDist = GetEdgeDistance(p1, p2, edgeNumber);
				if (nextEdgeDist > 0.0F) {
					distance.dist = nextEdgeDist;
					distance.edge = -1;
					return distance;
				}
				if (nextEdgeDist > mimimumDistance) {
					mimimumEdgeNumber = edgeNumber;
					mimimumDistance = nextEdgeDist;
				} else {
					distance.dist = mimimumDistance;
					distance.edge = mimimumEdgeNumber;
					return distance;
				}
			} while (true);
		}
	
		private float GetEdgeDistance(PConvexPolygonShape p1,
				PConvexPolygonShape p2, int edge) {
			Vector2f normal = p1.nors[edge];
			Vector2f[] p1vers = p1.vers;
			Vector2f[] p2vers = p2.vers;
			int num = -1;
			float dist = 1.0F;
			for (int i = 0; i < p2.numVertices; i++) {
				float dot = normal.x * (p2vers[i].x - p2._pos.x) + normal.y
						* (p2vers[i].y - p2._pos.y);
				if (dist == 1.0F || dot < dist) {
					dist = dot;
					num = i;
				}
			}
	
			dist = normal.x * (p2vers[num].x - p1vers[edge].x) + normal.y
					* (p2vers[num].y - p1vers[edge].y);
			return dist;
		}
	
		private PPolygonPolygonCollider.PWContactedVertex [] GetEdgeOfPotentialCollision(
				PConvexPolygonShape p1, PConvexPolygonShape p2, int r1edge,
				bool flip) {
			PPolygonPolygonCollider.PWContactedVertex [] line = new PPolygonPolygonCollider.PWContactedVertex [2];
			Vector2f normal = p1.nors[r1edge];
			float dist = 1.0F;
			int ver = -1;
			int nextVer = -1;
			for (int i = 0; i < p2.numVertices; i++) {
				float dot = normal.Dot(p2.nors[i]);
				if (dot < dist || dist == 1.0F) {
					dist = dot;
					ver = i;
					nextVer = (i + 1) % p2.numVertices;
				}
			}
	
			line[0] = new PPolygonPolygonCollider.PWContactedVertex ();
			line[0].v.Set(p2.vers[ver].x, p2.vers[ver].y);
			line[0].data.Set(r1edge + ver * 2 + ver * 4, false);
			line[1] = new PPolygonPolygonCollider.PWContactedVertex ();
			line[1].v.Set(p2.vers[nextVer].x, p2.vers[nextVer].y);
			line[1].data.Set(r1edge + ver * 2 + nextVer * 4, false);
			return line;
		}
	}
}
