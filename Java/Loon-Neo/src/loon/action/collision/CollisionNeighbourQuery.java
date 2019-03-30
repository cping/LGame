/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action.collision;

import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class CollisionNeighbourQuery implements CollisionQuery {

	private float x;

	private float y;

	private float distance;

	private boolean diag;

	private String flag;

	private Vector2f offsetLocation;

	public CollisionNeighbourQuery init(float x, float y, float distance, boolean diag, String flag, Vector2f offset) {
		this.x = offsetX(x);
		this.y = offsetY(y);
		this.distance = distance;
		this.diag = diag;
		this.flag = flag;
		this.offsetLocation = offset;
		return this;
	}

	private float offsetX(float x) {
		if (offsetLocation == null) {
			return x;
		}
		return x + offsetLocation.x;
	}

	private float offsetY(float y) {
		if (offsetLocation == null) {
			return y;
		}
		return y + offsetLocation.y;
	}
	
	@Override
	public boolean checkCollision(CollisionObject actor) {
		if (this.flag != null && !flag.equals(actor.getObjectFlag())) {
			return false;
		} else {
			float actorX = actor.getX();
			float actorY = actor.getY();
			if (actorX == this.x && actorY == this.y) {
				return false;
			} else {
				float ax = actor.getX();
				float ay = actor.getY();
				float dx;
				float dy;
				if (!this.diag) {
					dx = MathUtils.abs(ax - this.x);
					dy = MathUtils.abs(ay - this.y);
					return dx + dy <= this.distance;
				} else {
					dx = this.x - this.distance;
					dy = this.y - this.distance;
					float x2 = this.x + this.distance;
					float y2 = this.y + this.distance;
					return ax >= dx && ay >= dy && ax <= x2 && ay <= y2;
				}
			}
		}
	}

	@Override
	public void setOffsetPos(Vector2f offset) {
		offsetLocation = offset;
	}

	@Override
	public Vector2f getOffsetPos() {
		return offsetLocation;
	}
}
