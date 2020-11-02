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

public class PPolygonPolygonCollider implements PCollider {
	private class PWContactedVertex {

		@Override
		public PWContactedVertex clone() {
			PWContactedVertex cv = new PWContactedVertex();
			cv.v = v.clone();
			cv.data = data.clone();
			return cv;
		}

		PContactData data;
		Vector2f v;

		public PWContactedVertex() {

			v = new Vector2f();
			data = new PContactData();
		}
	}

	private class PWDistanceData {

		float dist;
		int edge;

		PWDistanceData() {

			dist = 0.0F;
			edge = -1;
		}
	}

	public PPolygonPolygonCollider() {
	}

	private PWContactedVertex[] clipEdge(PWContactedVertex clips[],
			Vector2f normal, float dist) {
		PWContactedVertex line[] = new PWContactedVertex[2];
		int numClips = 0;
		float dist0 = normal.dot(clips[0].v) - dist;
		float dist1 = normal.dot(clips[1].v) - dist;
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
		line[1] = new PWContactedVertex();
		line[1].v = clips[1].v.sub(clips[0].v).clone();
		line[1].v.mulLocal(d);
		line[1].v.addLocal(clips[0].v);
		line[1].data = clips[c].data;
		return line;
	}

	@Override
	public int collide(PShape s1, PShape s2, PContact cs[]) {
		if (s1._type != PShapeType.CONVEX_SHAPE
				&& s1._type != PShapeType.BOX_SHAPE
				|| s2._type != PShapeType.CONVEX_SHAPE
				&& s2._type != PShapeType.BOX_SHAPE) {
			return 0;
		}
		PConvexPolygonShape p1 = (PConvexPolygonShape) s1;
		PConvexPolygonShape p2 = (PConvexPolygonShape) s2;
		PWDistanceData dis1 = getDistance(p1, p2);
		if (dis1.dist > 0.0F)
			return 0;
		PWDistanceData dis2 = getDistance(p2, p1);
		if (dis2.dist > 0.0F)
			return 0;
		float error = 0.008F;
		int edgeA;
		PConvexPolygonShape pa;
		PConvexPolygonShape pb;
		boolean flip;
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
		Vector2f paVers[] = pa.vers;
		PWContactedVertex cv[] = getEdgeOfPotentialCollision(pa, pb, edgeA,
				flip);
		cv = clipEdge(cv, tangent.negate(), -tangent.dot(paVers[edgeA]));
		if (cv == null)
			return 0;
		cv = clipEdge(cv, tangent,
				tangent.dot(paVers[(edgeA + 1) % pa.numVertices]));
		if (cv == null)
			return 0;
		Vector2f contactNormal = flip ? normal : normal.negate();
		int numContacts = 0;
		for (int i = 0; i < 2; i++) {
			float dist = normal.dot(cv[i].v) - normal.dot(paVers[edgeA]);
			if (dist < 0.0F) {
				PContact c = new PContact();
				c.normal.set(contactNormal.x, contactNormal.y);
				c.pos.set(cv[i].v.x, cv[i].v.y);
				c.overlap = dist;
				c.data = cv[i].data;
				c.data.flip = flip;
				cs[numContacts] = c;
				numContacts++;
			}
		}

		return numContacts;
	}

	private PWDistanceData getDistance(PConvexPolygonShape p1,
			PConvexPolygonShape p2) {
		PWDistanceData distance = new PWDistanceData();
		Vector2f firstScan = p2._pos.sub(p1._pos);
		float dist = 1.0F;
		int edgeNumber = -1;
		for (int i = 0; i < p1.numVertices; i++) {
			float dot = p1.nors[i].dot(firstScan);
			if (dot > dist || dist == 1.0F) {
				dist = dot;
				edgeNumber = i;
			}
		}

		float edgeDist = getEdgeDistance(p1, p2, edgeNumber);
		if (edgeDist > 0.0F) {
			distance.dist = edgeDist;
			distance.edge = -1;
			return distance;
		}
		float nextEdgeDist = getEdgeDistance(p1, p2, (edgeNumber + 1)
				% p1.numVertices);
		if (nextEdgeDist > 0.0F) {
			distance.dist = nextEdgeDist;
			distance.edge = -1;
			return distance;
		}
		float prevEdgeDist = getEdgeDistance(p1, p2,
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
			nextEdgeDist = getEdgeDistance(p1, p2, edgeNumber);
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

	private float getEdgeDistance(PConvexPolygonShape p1,
			PConvexPolygonShape p2, int edge) {
		Vector2f normal = p1.nors[edge];
		Vector2f p1vers[] = p1.vers;
		Vector2f p2vers[] = p2.vers;
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

	private PWContactedVertex[] getEdgeOfPotentialCollision(
			PConvexPolygonShape p1, PConvexPolygonShape p2, int r1edge,
			boolean flip) {
		PWContactedVertex line[] = new PWContactedVertex[2];
		Vector2f normal = p1.nors[r1edge];
		float dist = 1.0F;
		int ver = -1;
		int nextVer = -1;
		for (int i = 0; i < p2.numVertices; i++) {
			float dot = normal.dot(p2.nors[i]);
			if (dot < dist || dist == 1.0F) {
				dist = dot;
				ver = i;
				nextVer = (i + 1) % p2.numVertices;
			}
		}

		line[0] = new PWContactedVertex();
		line[0].v.set(p2.vers[ver].x, p2.vers[ver].y);
		line[0].data.set(r1edge + ver * 2 + ver * 4, false);
		line[1] = new PWContactedVertex();
		line[1].v.set(p2.vers[nextVer].x, p2.vers[nextVer].y);
		line[1].data.set(r1edge + ver * 2 + nextVer * 4, false);
		return line;
	}
}
