/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.physics;

import loon.core.geom.Vector2f;

public class PCirclePolygonCollider implements PCollider {

	public PCirclePolygonCollider() {
	}

	@Override
	public int collide(PShape s1, PShape s2, PContact[] cs) {
		if (s1._type != PShapeType.CIRCLE_SHAPE
				|| s2._type != PShapeType.CONVEX_SHAPE
				&& s2._type != PShapeType.BOX_SHAPE){
			return 0;
		}
		PCircleShape c1 = (PCircleShape) s1;
		PConvexPolygonShape p1 = (PConvexPolygonShape) s2;
		float distance = -1F;
		int edgeNumber = -1;
		Vector2f vers[] = p1.vers;
		int numVers = p1.numVertices;
		Vector2f normal = new Vector2f();
		Vector2f edgeNormal = new Vector2f();
		Vector2f a = new Vector2f();
		Vector2f b = new Vector2f();
		int num = 0;
		for (int i = 0; i < numVers; i++) {
			a.set(c1._pos.x - vers[i].x, c1._pos.y - vers[i].y);
			distance = a.length();
			distance -= c1.rad;
			if (distance <= 0.0F) {
				PContact c = new PContact();
				c.overlap = distance;
				a.normalize();
				c.normal.set(a.x, a.y);
				c.pos.set(vers[i].x, vers[i].y);
				cs[num] = c;
				if (++num == 2){
					return num;
				}
			}
		}

		if (num > 0){
			return num;
		}
		for (int i = 0; i < numVers; i++) {
			Vector2f ver = vers[i];
			Vector2f nextVer = vers[(i + 1) % numVers];
			float edgeX = nextVer.x - ver.x;
			float edgeY = nextVer.y - ver.y;
			edgeNormal.set(edgeY, -edgeX);
			edgeNormal.normalize();
			a.set(c1._pos.x - ver.x, c1._pos.y - ver.y);
			b.set(c1._pos.x - nextVer.x, c1._pos.y - nextVer.y);
			if ((a.x * edgeX + a.y * edgeY) * (b.x * edgeX + b.y * edgeY) <= 0.0F) {
				float edgeLen = (float) Math
						.sqrt(edgeX * edgeX + edgeY * edgeY);
				float distanceToEdge = Math.abs(a.x * edgeY - a.y * edgeX)
						/ edgeLen;
				if (distanceToEdge <= c1.rad) {
					distanceToEdge -= c1.rad;
					if (distance > distanceToEdge || distance == -1F) {
						edgeNumber = i;
						distance = distanceToEdge;
						normal.set(edgeNormal.x, edgeNormal.y);
					}
				}
			}
		}

		if (edgeNumber > -1) {
			PContact c = new PContact();
			c.overlap = distance;
			c.normal = normal;
			c.pos = c1._pos.sub(normal.mul(c1.rad));
			cs[0] = c;
			return 1;
		}
		boolean hit = true;
		for (int i = 0; i < numVers; i++) {
			Vector2f ver = vers[i];
			Vector2f nextVer = vers[(i + 1) % numVers];
			float v1x = nextVer.x - ver.x;
			float v1y = nextVer.y - ver.y;
			float v2x = c1._pos.x - ver.x;
			float v2y = c1._pos.y - ver.y;
			if (v1x * v2y - v1y * v2x >= 0.0F){
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
				a.set(nextVer.x - ver.x, nextVer.y - ver.y);
				a.normalize();
				float d = c1._pos.sub(ver).cross(a);
				if (d < 0.0F && (distance == 1.0F || distance < d)) {
					distance = d;
					normal.set(a.y, -a.x);
				}
			}

			if (distance != 1.0F) {
				PContact c = new PContact();
				c.normal.set(normal.x, normal.y);
				c.pos.set(c1._pos.x, c1._pos.y);
				c.overlap = distance;
				cs[0] = c;
				return 1;
			}
		}
		return 0;
	}
}
