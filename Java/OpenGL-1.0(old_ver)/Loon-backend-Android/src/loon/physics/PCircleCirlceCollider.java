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

public class PCircleCirlceCollider implements PCollider {

	public PCircleCirlceCollider() {
	}

	@Override
	public int collide(PShape s1, PShape s2, PContact[] cs) {
		if (s1._type != PShapeType.CIRCLE_SHAPE
				|| s2._type != PShapeType.CIRCLE_SHAPE) {
			return 0;
		}
		PCircleShape c1 = (PCircleShape) s1;
		PCircleShape c2 = (PCircleShape) s2;
		Vector2f normal = c2._pos.sub(c1._pos);
		float rad = c1.rad + c2.rad;
		float length = normal.length();
		if (length < rad) {
			PContact c = new PContact();
			c.overlap = length - rad;
			normal.normalize();
			c.pos.set(c1._pos.x + normal.x * c1.rad, c1._pos.y + normal.y
					* c1.rad);
			c.normal.set(-normal.x, -normal.y);
			cs[0] = c;
			return 1;
		} else {
			return 0;
		}
	}
}
