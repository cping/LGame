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

public class PVertexLoop {

	public boolean crossPoint;
	public Vector2f epsilon;
	public PVertexLoop next;
	public PVertexLoop pair;
	public PVertexLoop prev;
	public Vector2f v;

	public PVertexLoop(float x, float y) {
		this.v = new Vector2f(x, y);
	}

	public PVertexLoop(Vector2f v1) {
		this.v = v1;
	}

	PVertexLoop() {
		this.v = new Vector2f();
	}
}
