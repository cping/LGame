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
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;

public class PConcavePolygonShape extends PShape {

	PConvexPolygonShape[] convexes;
	Vector2f[] localVers;
	int numConvexes;
	int numVertices;
	PPolygonizer poly;
	PFigure fig;
	PTriangulator tri;
	Vector2f[] vers;

	public PConcavePolygonShape(float[] xvers, float[] yvers, float density) {
		fig = new PFigure();
		tri = new PTriangulator();
		poly = new PPolygonizer();
		numVertices = xvers.length;
		localVers = new Vector2f[numVertices];
		vers = new Vector2f[numVertices];
		_dens = density;
		for (int i = 0; i < numVertices; i++) {
			localVers[i] = new Vector2f(xvers[i], yvers[i]);
			vers[i] = new Vector2f(xvers[i], yvers[i]);
		}

		fig.figure(localVers, numVertices);
		numVertices = fig.numVertices;
		localVers = new Vector2f[numVertices];
		vers = new Vector2f[numVertices];
		for (int i = 0; i < numVertices; i++) {
			localVers[i] = new Vector2f(fig.done[i].x, fig.done[i].y);
			vers[i] = new Vector2f(fig.done[i].x, fig.done[i].y);
		}
		tri.triangulate(fig.done, fig.numVertices);
		poly.polygonize(tri.triangles, tri.numTriangles);
		convexes = new PConvexPolygonShape[1024];
		for (int i = 0; i < poly.numPolygons; i++){
			convexes[i] = new PConvexPolygonShape(poly.polygons[i].xs,
					poly.polygons[i].ys, _dens);
		}
		numConvexes = poly.numPolygons;
		calcMassData();
		_type = PShapeType.CONCAVE_SHAPE;
	}

	@Override
	void calcAABB() {
		for (int i = 0; i < numConvexes; i++) {
			PConvexPolygonShape c = convexes[i];
			c.calcAABB();
			c._sapAABB.update();
			if (i == 0) {
				_aabb.set(c._aabb.minX, c._aabb.minY, c._aabb.maxX,
						c._aabb.maxY);
			} else {
				_aabb.set(MathUtils.min(_aabb.minX, c._aabb.minX),
						MathUtils.min(_aabb.minY, c._aabb.minY),
						MathUtils.max(_aabb.maxX, c._aabb.maxX),
						MathUtils.max(_aabb.maxY, c._aabb.maxY));
			}
		}

	}

	private void calcMassData() {
		correctCenterOfGravity();
		mm = ii = 0.0F;
		for (int j = 0; j < numConvexes; j++) {
			mm += convexes[j].mm * convexes[j]._dens;
			ii += convexes[j].ii * convexes[j]._dens;
			ii += (convexes[j]._localPos.x * convexes[j]._localPos.x + convexes[j]._localPos.y
					* convexes[j]._localPos.y)
					* convexes[j].mm * convexes[j]._dens;
		}

	}

	private void correctCenterOfGravity() {
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
		for (int j = 0; j < numConvexes; j++) {
			convexes[j]._localPos.x -= cx;
			convexes[j]._localPos.y -= cy;
		}

	}

	public PConvexPolygonShape[] getConvexes() {
		return (PConvexPolygonShape[]) CollectionUtils.copyOf(convexes, numConvexes);
	}

	public Vector2f[] getVertices() {
		Vector2f[] vertices = new Vector2f[numVertices];
		System.arraycopy(vers, 0, vertices, 0, numVertices);
		return vertices;
	}

	@Override
	void update() {
		float twoPI = MathUtils.TWO_PI;
		for (int i = 0; i < numConvexes; i++) {
			PConvexPolygonShape c = convexes[i];
			c._pos.set(c._localPos.x, c._localPos.y);
			_mAng.mulEqual(c._pos);
			c._pos.addLocal(_pos);
			c._localAng = (c._localAng + twoPI) % twoPI;
			c._ang = _ang + c._localAng;
			c._mAng.setRotate(c._ang);
			c.update();
		}
		for (int i = 0; i < numVertices; i++) {
			vers[i].set(localVers[i].x, localVers[i].y);
			_mAng.mulEqual(vers[i]);
			vers[i].addLocal(_pos);
		}
	}

}
