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

	private float _x;

	private float _y;

	private float _distance;

	private boolean _diag;

	private String _flag;

	private Vector2f _offsetLocation;

	public CollisionNeighbourQuery init(float x, float y, float distance, boolean diag, String flag, Vector2f offset) {
		this._x = offsetX(x);
		this._y = offsetY(y);
		this._distance = distance;
		this._diag = diag;
		this._flag = flag;
		this._offsetLocation = offset;
		return this;
	}

	private float offsetX(float x) {
		if (_offsetLocation == null) {
			return x;
		}
		return x + _offsetLocation.x;
	}

	private float offsetY(float y) {
		if (_offsetLocation == null) {
			return y;
		}
		return y + _offsetLocation.y;
	}

	@Override
	public boolean checkCollision(CollisionObject actor) {
		if (this._flag != null && !_flag.equals(actor.getObjectFlag())) {
			return false;
		} else {
			float actorX = actor.getX();
			float actorY = actor.getY();
			if (actorX == this._x && actorY == this._y) {
				return false;
			} else {
				float ax = actor.getX();
				float ay = actor.getY();
				float dx;
				float dy;
				if (!this._diag) {
					dx = MathUtils.abs(ax - this._x);
					dy = MathUtils.abs(ay - this._y);
					return dx + dy <= this._distance;
				} else {
					dx = this._x - this._distance;
					dy = this._y - this._distance;
					float x2 = this._x + this._distance;
					float y2 = this._y + this._distance;
					return ax >= dx && ay >= dy && ax <= x2 && ay <= y2;
				}
			}
		}
	}

	@Override
	public void setOffsetPos(Vector2f offset) {
		_offsetLocation = offset;
	}

	@Override
	public Vector2f getOffsetPos() {
		return _offsetLocation;
	}
}
