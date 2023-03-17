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

	public void setNormal(Vector2f normal) {
		this.normal = normal;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public boolean isCollide() {
		return isCollided;
	}

	public void setCollide(boolean isCollide) {
		this.isCollided = isCollide;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int stepsTaken) {
		this.steps = stepsTaken;
	}

}
