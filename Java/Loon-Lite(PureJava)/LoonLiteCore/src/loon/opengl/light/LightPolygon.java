/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.opengl.light;

import loon.geom.Polygon;
import loon.geom.Vector2f;
import loon.utils.reply.TValue;

public class LightPolygon extends Polygon implements LightShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TValue<Object> _values;

	private Vector2f[] _verticies;

	public LightPolygon(float[] verticies) {
		super(verticies);
	}

	@Override
	public Vector2f[] getVertices(Vector2f light) {
		final float[] verts = getPoints();
		if (_verticies == null || _verticies.length != verts.length / 2) {
			_verticies = new Vector2f[verts.length / 2];
		}
		for (int i = 0; i < _verticies.length; i++) {
			if (_verticies[i] == null) {
				_verticies[i] = new Vector2f();
			}
			_verticies[i].set(verts[i * 2], verts[i * 2 + 1]);
		}
		return _verticies;
	}

	@Override
	public Vector2f getIntersection(Vector2f start, Vector2f dir, Vector2f ignore) {
		getVertices(null);
		Vector2f closest = null;
		for (int i = 0; i < _verticies.length; i++) {
			Vector2f p0 = _verticies[i];
			Vector2f p1 = _verticies[(i + 1) % _verticies.length];

			if (p0 == ignore || p1 == ignore) {
				continue;
			}

			Vector2f intersect = LightRect.intersect(start, dir, p0, p1);
			if (intersect == null) {
				continue;
			}

			if (closest == null || intersect.distance(start) < closest.distance(start)) {
				closest = intersect;
			}
		}
		return closest;
	}

	@Override
	public boolean contains(Vector2f pos) {
		return contains(pos.x, pos.y);
	}

	@Override
	public TValue<Object> getTag() {
		return _values;
	}

	@Override
	public void setTag(TValue<Object> t) {
		this._values = t;
	}
}
