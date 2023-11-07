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

import loon.action.map.Field2D;
import loon.action.map.Side;
import loon.geom.Vector2f;
import loon.utils.ObjectSet;
import loon.utils.StringKeyValue;
import loon.utils.cache.Pool.Poolable;

public class GravityResult implements Poolable {

	protected Vector2f normal = new Vector2f();
	protected Vector2f position = new Vector2f();
	protected Gravity source = null;
	protected ObjectSet<Gravity> targets = new ObjectSet<Gravity>();
	protected boolean collided = false;
	protected int steps = 0;

	public GravityResult() {
	}

	@Override
	public void reset() {
		normal.setZero();
		position.setZero();
		collided = false;
		steps = 0;
		source = null;
		targets.clear();
	}

	public Gravity getSource() {
		return source;
	}

	public ObjectSet<Gravity> getTargets() {
		return targets;
	}

	public int getTargetDirection() {
		return Field2D.getDirection(normal.x(), normal.y());
	}

	public String getTargetDirectionName() {
		return Side.getDirectionName(getTargetDirection());
	}

	public int getSourceDirection() {
		final int dir = Field2D.getDirection(normal.x(), normal.y());
		return Side.getOppositeSide(dir);
	}

	public String geSourceDirectionName() {
		return Side.getDirectionName(getSourceDirection());
	}

	public Vector2f getNormal() {
		return normal;
	}

	public Vector2f getPosition() {
		return position;
	}

	public boolean isCollide() {
		return collided;
	}

	public int getSteps() {
		return steps;
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
		if (g.normal.equals(this.normal) && g.position.equals(this.position) && g.collided == this.collided
				&& g.steps == this.steps) {
			return true;
		}
		return true;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("GravityResult").kv("normal", normal).comma()
				.kv("position", position).comma().kv("collided", collided);
		return builder.toString();
	}
}
