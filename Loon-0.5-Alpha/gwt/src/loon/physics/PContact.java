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

public class PContact {

	float corI;
	PContactData data;
	Vector2f localRel1;
	Vector2f localRel2;
	float massN;
	float massT;
	float norI;
	Vector2f normal;
	float overlap;
	Vector2f pos;
	Vector2f rel1;
	Vector2f rel2;
	Vector2f relPosVel;
	Vector2f relVel;
	Vector2f tangent;
	float tanI;
	float targetVelocity;

	public PContact() {
		rel1 = new Vector2f();
		rel2 = new Vector2f();
		localRel1 = new Vector2f();
		localRel2 = new Vector2f();
		pos = new Vector2f();
		normal = new Vector2f();
		tangent = new Vector2f();
		relVel = new Vector2f();
		relPosVel = new Vector2f();
		data = new PContactData();
	}

	public Vector2f getNormal() {
		return normal.clone();
	}

	public float getOverlap() {
		return overlap;
	}

	public Vector2f getPosition() {
		return pos.clone();
	}

	public Vector2f getRelativeVelocity() {
		return relVel.clone();
	}

	public Vector2f getTangent() {
		return tangent.clone();
	}

}
