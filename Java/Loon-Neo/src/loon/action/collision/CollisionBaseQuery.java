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

import loon.geom.RectBox;
import loon.geom.Vector2f;

public class CollisionBaseQuery implements CollisionQuery {

	private String _flag;

	private CollisionObject _compareObject;

	private Vector2f _offsetLocation;

	private RectBox _collisionRect = new RectBox(0, 0, 0, 0);

	public void init(String flag, CollisionObject actor, Vector2f offset) {
		this._flag = flag;
		this._compareObject = actor;
		this._offsetLocation = offset;
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

	public boolean checkOnlyCollision(CollisionObject other) {
		if (!_offsetLocation.isZero()) {
			_collisionRect.setBounds(offsetX(this._compareObject.getX()), offsetY(this._compareObject.getY()),
					this._compareObject.getWidth(), this._compareObject.getHeight());
			return (this._compareObject == null ? true : other.contains(_collisionRect));
		}
		return (this._compareObject == null ? true : other.contains(this._compareObject));
	}

	@Override
	public boolean checkCollision(CollisionObject other) {
		if (!_offsetLocation.isZero()) {
			_collisionRect.setBounds(offsetX(this._compareObject.getX()), offsetY(this._compareObject.getY()),
					this._compareObject.getWidth(), this._compareObject.getHeight());
			return this._flag != null && !_flag.equals(other.getObjectFlag()) ? false
					: (this._compareObject == null ? true : other.contains(_collisionRect));
		}
		return this._flag != null && !_flag.equals(other.getObjectFlag()) ? false
				: (this._compareObject == null ? true : other.contains(this._compareObject));
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
