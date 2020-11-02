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

public class PConvexPolygonShape extends PShape {

	Vector2f[] localNors;
	Vector2f[] localVers;
	Vector2f[] nors;
	int numVertices;
	Vector2f[] vers;

	public PConvexPolygonShape(float xvers[], float yvers[], float density) {
		float xs[] = xvers.clone();
		float ys[] = yvers.clone();
		numVertices = xs.length;
		_dens = density;
		localVers = new Vector2f[numVertices];
		nors = new Vector2f[numVertices];
		float fakeCenterY;
		float fakeCenterX = fakeCenterY = 0.0F;
		for (int i = 0; i < numVertices; i++) {
			fakeCenterX += xs[i];
			fakeCenterY += ys[i];
		}

		fakeCenterX /= numVertices;
		fakeCenterY /= numVertices;
		for (int i = 0; i < numVertices; i++) {
			localVers[i] = new Vector2f(xs[i] - fakeCenterX, ys[i]
					- fakeCenterY);
			nors[i] = new Vector2f();
		}

		vers = new Vector2f[numVertices];
		for (int j = 0; j < localVers.length; j++) {
			mm += localVers[j].cross(localVers[(j + 1) % numVertices]) * 0.5F;
		}

		float cy;
		float cx = cy = 0.0F;
		float invThree = 0.3333333F;
		for (int j = 0; j < localVers.length; j++) {
			Vector2f ver = localVers[j];
			Vector2f nextVer = localVers[(j + 1) % numVertices];
			float triArea = ver.cross(nextVer) * 0.5F;
			cx += triArea * (ver.x + nextVer.x) * invThree;
			cy += triArea * (ver.y + nextVer.y) * invThree;
		}

		float invM = 1.0F / mm;
		cx *= invM;
		cy *= invM;
		cx += fakeCenterX;
		cy += fakeCenterY;
		for (int i = 0; i < numVertices; i++) {
			localVers[i].x += fakeCenterX;
			localVers[i].y += fakeCenterY;
		}

		_localPos.set(cx, cy);
		for (int i = 0; i < numVertices; i++) {
			vers[i] = new Vector2f(localVers[i].x, localVers[i].y);
		}
		for (int i = 0; i < numVertices; i++) {
			localVers[i].subLocal(_localPos);
		}

		float invSix = 0.1666667F;
		for (int j = 0; j < localVers.length; j++) {
			Vector2f ver = localVers[j];
			Vector2f nextVer = localVers[(j + 1) % numVertices];
			float triArea = ver.cross(nextVer) * 0.5F;
			this.ii += triArea
					* invSix
					* (ver.x * ver.x + ver.y * ver.y + ver.x * nextVer.x
							+ ver.y * nextVer.y + nextVer.x * nextVer.x + nextVer.y
							* nextVer.y);
		}

		localNors = new Vector2f[numVertices];
		for (int i = 0; i < numVertices; i++) {
			Vector2f ver = localVers[i];
			Vector2f nextVer = localVers[(i + 1) % localVers.length];
			localNors[i] = new Vector2f(nextVer.y - ver.y, -nextVer.x + ver.x);
			localNors[i].normalize();
		}

		_type = PShapeType.CONVEX_SHAPE;
		setDensity(_dens);
		calcAABB();
	}

	@Override
	void calcAABB() {
		float miny;
		float maxx;
		float maxy;
		float minx = miny = maxx = maxy = 0.0F;
		for (int i = 0; i < numVertices; i++) {
			Vector2f vertex = vers[i];
			if (i == 0) {
				minx = vertex.x;
				miny = vertex.y;
				maxx = vertex.x;
				maxy = vertex.y;
			} else {
				minx = minx <= vertex.x ? minx : vertex.x;
				miny = miny <= vertex.y ? miny : vertex.y;
				maxx = maxx >= vertex.x ? maxx : vertex.x;
				maxy = maxy >= vertex.y ? maxy : vertex.y;
			}
		}

		_aabb.set(minx, miny, maxx, maxy);
	}

	public Vector2f[] getVertices() {
		Vector2f[] vertices = new Vector2f[numVertices];
		System.arraycopy(vers, 0, vertices, 0, numVertices);
		return vertices;
	}

	@Override
	void update() {
		for (int i = 0; i < numVertices; i++) {
			vers[i].set(localVers[i].x, localVers[i].y);
			_mAng.mulEqual(vers[i]);
			vers[i].addLocal(_pos);
			nors[i].set(localNors[i].x, localNors[i].y);
			_mAng.mulEqual(nors[i]);
		}

	}

}
