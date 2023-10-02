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
package loon.action.collision;

import loon.geom.Vector2f;
import loon.utils.cache.Pool.Poolable;

public class GravityResult implements Poolable {

	protected Vector2f normal = new Vector2f();
	protected Vector2f position = new Vector2f();
	protected boolean isCollided = false;
	protected int steps = 0;

	public GravityResult() {
	}

	@Override
	public void reset() {
		normal.setZero();
		position.setZero();
		isCollided = false;
		steps = 0;
	}

	public Vector2f getNormal() {
		return normal;
	}

	public GravityResult setNormal(Vector2f n) {
		this.normal = n;
		return this;
	}

	public Vector2f getPosition() {
		return position;
	}

	public GravityResult setPosition(Vector2f p) {
		this.position = p;
		return this;
	}

	public boolean isCollide() {
		return isCollided;
	}

	public GravityResult setCollide(boolean c) {
		this.isCollided = c;
		return this;
	}

	public int getSteps() {
		return steps;
	}

	public GravityResult setSteps(int s) {
		this.steps = s;
		return this;
	}

	@Override
	public boolean equals(Object g) {
		if (g == null) {
			return false;
		}
		if (g == this) {
			return true;
		}
		if (g instanceof GravityResult) {
			return equals((GravityResult) g);
		}
		return false;
	}

	public boolean equals(GravityResult g) {
		if (g == null) {
			return false;
		}
		if (g == this) {
			return true;
		}
		if (g.normal.equals(this.normal) && g.position.equals(this.position) && g.isCollided == this.isCollided
				&& g.steps == this.steps) {
			return true;
		}
		return true;
	}
}
