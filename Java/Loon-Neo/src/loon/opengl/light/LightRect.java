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

import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.reply.TValue;

public class LightRect extends RectBox implements LightShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TValue<Object> _values;

	private final Vector2f[] _verticies = new Vector2f[4];

	public LightRect(float x, float y, float width, float height) {
		super(x, y, width, height);
		for (int i = 0; i < _verticies.length; i++) {
			_verticies[i] = new Vector2f();
		}
	}

	public static Vector2f intersect(Vector2f start, Vector2f dir, Vector2f p0, Vector2f p1) {
		Vector2f p = start, r = dir, q = p0, s = p1.sub(p0);
		float cross = cross(r, s);
		if (cross == 0) {
			return null;
		}
		Vector2f qmp = q.sub(p);
		float u = cross(qmp, r) / cross;
		if (u < 0 || u > 1) {
			return null;
		}
		float t = cross(qmp, s) / cross;
		if (t < 0) {
			return null;
		}
		return s.scale(u).addSelf(q);
	}

	private static float cross(Vector2f a, Vector2f b) {
		return a.x * b.y - a.y * b.x;
	}

	@Override
	public Vector2f[] getVertices(Vector2f light) {
		_verticies[0].set(getMaxX(), getMaxY());
		_verticies[1].set(getMinX(), getMaxY());
		_verticies[2].set(getMinX(), getMinY());
		_verticies[3].set(getMaxX(), getMinY());
		return _verticies;
	}

	@Override
	public Vector2f getIntersection(Vector2f start, Vector2f dir, Vector2f ignore) {
		getVertices(null);
		Vector2f closest = null;
		for (int i = 0; i < 4; i++) {
			Vector2f p0 = _verticies[i];
			Vector2f p1 = _verticies[(i + 1) % 4];

			if (p0 == ignore || p1 == ignore) {
				continue;
			}

			Vector2f intersect = intersect(start, dir, p0, p1);
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