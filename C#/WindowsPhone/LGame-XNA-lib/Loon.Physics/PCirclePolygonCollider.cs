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
	
	public class PCirclePolygonCollider : PCollider {
	
		public PCirclePolygonCollider() {
		}
	
		public virtual int Collide(PShape s1, PShape s2, PContact[] cs) {
			if (s1._type != PShapeType.CIRCLE_SHAPE
					|| s2._type != PShapeType.CONVEX_SHAPE
					&& s2._type != PShapeType.BOX_SHAPE) {
				return 0;
			}
			PCircleShape c1 = (PCircleShape) s1;
			PConvexPolygonShape p1 = (PConvexPolygonShape) s2;
			float distance = -1F;
			int edgeNumber = -1;
			Vector2f[] vers = p1.vers;
			int numVers = p1.numVertices;
			Vector2f normal = new Vector2f();
			Vector2f edgeNormal = new Vector2f();
			Vector2f a = new Vector2f();
			Vector2f b = new Vector2f();
			int num = 0;
			for (int i = 0; i < numVers; i++) {
				a.Set(c1._pos.x - vers[i].x, c1._pos.y - vers[i].y);
				distance = a.Length();
				distance -= c1.rad;
				if (distance <= 0.0F) {
					PContact c = new PContact();
					c.overlap = distance;
					a.Normalize();
					c.normal.Set(a.x, a.y);
					c.pos.Set(vers[i].x, vers[i].y);
					cs[num] = c;
					if (++num == 2) {
						return num;
					}
				}
			}
	
			if (num > 0) {
				return num;
			}
			for (int i_0 = 0; i_0 < numVers; i_0++) {
				Vector2f ver = vers[i_0];
				Vector2f nextVer = vers[(i_0 + 1) % numVers];
				float edgeX = nextVer.x - ver.x;
				float edgeY = nextVer.y - ver.y;
				edgeNormal.Set(edgeY, -edgeX);
				edgeNormal.Normalize();
				a.Set(c1._pos.x - ver.x, c1._pos.y - ver.y);
				b.Set(c1._pos.x - nextVer.x, c1._pos.y - nextVer.y);
				if ((a.x * edgeX + a.y * edgeY) * (b.x * edgeX + b.y * edgeY) <= 0.0F) {
					float edgeLen = (float) System.Math.Sqrt(edgeX * edgeX + edgeY * edgeY);
					float distanceToEdge = System.Math.Abs(a.x * edgeY - a.y * edgeX)
							/ edgeLen;
					if (distanceToEdge <= c1.rad) {
						distanceToEdge -= c1.rad;
						if (distance > distanceToEdge || distance == -1F) {
							edgeNumber = i_0;
							distance = distanceToEdge;
							normal.Set(edgeNormal.x, edgeNormal.y);
						}
					}
				}
			}
	
			if (edgeNumber > -1) {
				PContact c_1 = new PContact();
				c_1.overlap = distance;
				c_1.normal = normal;
				c_1.pos = c1._pos.Sub(normal.Mul(c1.rad));
				cs[0] = c_1;
				return 1;
			}
			bool hit = true;
			for (int i_2 = 0; i_2 < numVers; i_2++) {
				Vector2f ver = vers[i_2];
				Vector2f nextVer = vers[(i_2 + 1) % numVers];
				float v1x = nextVer.x - ver.x;
				float v1y = nextVer.y - ver.y;
				float v2x = c1._pos.x - ver.x;
				float v2y = c1._pos.y - ver.y;
				if (v1x * v2y - v1y * v2x >= 0.0F) {
					continue;
				}
				hit = false;
				break;
			}
	
			if (hit) {
				distance = 1.0F;
				normal = new Vector2f();
				for (int i = 0; i < numVers; i++) {
					Vector2f ver = vers[i];
					Vector2f nextVer = vers[(i + 1) % numVers];
					a.Set(nextVer.x - ver.x, nextVer.y - ver.y);
					a.Normalize();
					float d = c1._pos.Sub(ver).Cross(a);
					if (d < 0.0F && (distance == 1.0F || distance < d)) {
						distance = d;
						normal.Set(a.y, -a.x);
					}
				}
	
				if (distance != 1.0F) {
					PContact c = new PContact();
					c.normal.Set(normal.x, normal.y);
					c.pos.Set(c1._pos.x, c1._pos.y);
					c.overlap = distance;
					cs[0] = c;
					return 1;
				}
			}
			return 0;
		}
	}
}
