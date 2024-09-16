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

import loon.geom.Circle;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.reply.TValue;

public class LightCircle extends Circle implements LightShape {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TValue<Object> _values;

	private Vector2f[] _verticies = new Vector2f[2];

	public LightCircle(float x, float y, float radius) {
		super(x, y, radius);
	}

	@Override
	public Vector2f[] getVertices(Vector2f light) {
		Vector2f toLight = light.cpy();
		float x = getCenterX(), y = getCenterY();
		toLight.x -= x;
		toLight.y -= y;
		toLight.add(90);
		toLight.norSelf();
		toLight.scaleSelf(this.getCircle());
		_verticies[0] = toLight.cpy();
		_verticies[0].x += x;
		_verticies[0].y += y;
		_verticies[1] = toLight.scale(-1);
		_verticies[1].x += x;
		_verticies[1].y += y;
		return _verticies;
	}

	public float getCircle() {
		return this.getRadius() * 2f;
	}

	@Override
	public Vector2f getIntersection(Vector2f start, Vector2f dir, Vector2f ignore) {
		for (int i = 0; i < 2; i++) {
			if (_verticies[i] == ignore) {
				return null;
			}
		}
		float x = getCenterX(), y = getCenterY();
		float ox = start.x - x, oy = start.y - y;
		float a = dir.x * dir.x + dir.y * dir.y;
		float b = 2 * (ox * dir.x + oy * dir.y);
		float c = ox * ox + oy * oy - this.getCircle() * this.getCircle();
		float disc = b * b - 4 * a * c;
		if (disc < 0) {
			return null;
		}
		float t0 = (-b - MathUtils.sqrt(disc)) / (2 * a);
		float t1 = (-b + MathUtils.sqrt(disc)) / (2 * a);
		float t;
		if (t0 < 0) {
			if (t1 < 0)
				return null;
			t = t1;
		} else {
			t = t0;
		}
		return dir.scale(t).addSelf(start);
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