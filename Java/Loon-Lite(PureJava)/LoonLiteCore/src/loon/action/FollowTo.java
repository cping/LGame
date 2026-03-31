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
package loon.action;

import loon.action.map.Field2D;
import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.MathUtils;

public class FollowTo extends ActionEvent {

	private ActionBind _actorToFollow;

	private boolean _started;
	private boolean _isDirUpdate;

	private Vector2f _currentPos;
	private Vector2f _end;
	private Vector2f _dir;

	private int _direction = -1;

	public float _newX = -1f;
	public float _newY = -1f;

	private float _velocityX = 1f;
	private float _velocityY = 1f;

	private float _followDistance;

	private float _speed = 1f;

	private Field2D _field2d;

	public FollowTo(ActionBind actorToFollow) {
		this(null, actorToFollow, -1f, 1f);
	}

	public FollowTo(Field2D field2d, ActionBind actorToFollow) {
		this(field2d, actorToFollow, -1f, 1f);
	}

	public FollowTo(ActionBind actorToFollow, float speed) {
		this(null, actorToFollow, -1f, speed);
	}

	public FollowTo(Field2D field2d, ActionBind actorToFollow, float speed) {
		this(field2d, actorToFollow, -1f, speed);
	}

	public FollowTo(ActionBind actorToFollow, float follow, float speed) {
		this(null, actorToFollow, follow, speed);
	}

	public FollowTo(Field2D field2d, ActionBind actorToFollow, float follow, float speed) {
		this(field2d, actorToFollow, 5f, 5f, follow, speed);
	}

	public FollowTo(ActionBind actorToFollow, float vx, float vy, float follow, float speed) {
		this(null, actorToFollow, vx, vy, follow, speed);
	}

	public FollowTo(Field2D field2d, ActionBind actorToFollow, float vx, float vy, float follow, float speed) {
		this._actorToFollow = actorToFollow;
		this._velocityX = vx;
		this._velocityY = vy;
		this._followDistance = follow;
		this._speed = speed;
		this._field2d = field2d;
	}

	@Override
	public void update(long elapsedTime) {
		if (!this._started) {
			this._started = true;
			this._dir = this._end.sub(this._currentPos).normalizeNew();
		}

		float actorToFollowSpeed = MathUtils
				.sqrt(MathUtils.pow(this._velocityX, 2) + MathUtils.pow(this._velocityY, 2));
		if (actorToFollowSpeed != 0f) {
			this._speed = actorToFollowSpeed;
		}

		this._currentPos.x = this.original.getX();
		this._currentPos.y = this.original.getY();

		this._end.x = this._actorToFollow.getX();
		this._end.y = this._actorToFollow.getY();
		this._dir = this._end.sub(this._currentPos).normalizeNew();

		Vector2f v = this._dir.scale(this._speed);

		_newX = (_currentPos.x + v.x);
		_newY = (_currentPos.y + v.y);

		updateDirection(MathUtils.ifloor(_newX - this._currentPos.x), MathUtils.ifloor(_newY - this._currentPos.y));
		if (original.getRectBox().intersects(_actorToFollow.getRectBox())
				|| original.getRectBox().contains(_actorToFollow.getRectBox())) {
			_isCompleted = true;
		} else {
			if (_field2d != null) {
				if (!checkTileCollision(_field2d, original, _newX, _newY)) {
					movePos(_newX + offsetX, _newY + offsetY);
				}
			} else {
				movePos(_newX + offsetX, _newY + offsetY);
			}
		}
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this._currentPos = new Vector2f(this.original.getX(), this.original.getY());
			this._end = new Vector2f(_actorToFollow.getX(), _actorToFollow.getY());
			if (_newX == -1) {
				_newX = original.getX();
			}
			if (_newY == -1) {
				_newY = original.getY();
			}
		}
	}

	protected final boolean checkTileCollision(Field2D field2d, ActionBind bind, float newX, float newY) {
		if (field2d == null) {
			return false;
		}
		return field2d.checkTileCollision(bind.getX() - offsetX, bind.getY() - offsetY, bind.getWidth(),
				bind.getHeight(), newX, newY);
	}

	public int getDirection() {
		return _direction;
	}

	public boolean isDirectionUpdate() {
		return _isDirUpdate;
	}

	public void updateDirection(int x, int y) {
		int oldDir = _direction;
		_direction = Field2D.getDirection(x, y, oldDir);
		_isDirUpdate = (oldDir != _direction);
	}

	public Field2D getField2d() {
		return _field2d;
	}

	public FollowTo setField2d(Field2D f) {
		this._field2d = f;
		return this;
	}

	public float getVelocityX() {
		return _velocityX;
	}

	public FollowTo setVelocityX(float vx) {
		this._velocityX = vx;
		return this;
	}

	public float getVelocityY() {
		return _velocityY;
	}

	public FollowTo setVelocityY(float vy) {
		this._velocityY = vy;
		return this;
	}

	public ActionBind getFollow() {
		return _actorToFollow;
	}

	public boolean isStarted() {
		return _started;
	}

	public float getNewX() {
		return _newX;
	}

	public float getNewY() {
		return _newY;
	}

	public float getFollowDistance() {
		return _followDistance;
	}

	public float getSpeed() {
		return _speed;
	}

	@Override
	public ActionEvent cpy() {
		FollowTo move = new FollowTo(_field2d, _actorToFollow, _velocityX, _velocityY, _followDistance, _speed);
		move.set(this);
		return move;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "follow";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("speed", _speed).comma().kv("currentX", _newX).comma().kv("currentY", _newY).comma()
				.kv("endX", _end.x).comma().kv("endY", _end.y);
		return builder.toString();
	}

}
