/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.LSystem;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.utils.Easing.EasingMode;
import loon.utils.StringKeyValue;
import loon.utils.timer.EaseTimer;

public class MoveBy extends ActionEvent {

	private float _speed = 1;

	private float _startX = -1, _startY = -1, _endX, _endY;

	private int _direction = -1;

	private boolean isDirUpdate = false;

	private EaseTimer easeTimer;

	public MoveBy(float endX, float endY, float duration, float delay, EasingMode easing) {
		this(-1, -1, endX, endY, 0, duration, delay, easing, 0, 0);
	}

	public MoveBy(float endX, float endY, EasingMode easing) {
		this(-1, -1, endX, endY, 0, 1f, LSystem.DEFAULT_EASE_DELAY, easing, 0, 0);
	}

	public MoveBy(float endX, float endY, float duration, EasingMode easing) {
		this(-1, -1, endX, endY, 0, duration, LSystem.DEFAULT_EASE_DELAY, easing, 0, 0);
	}

	public MoveBy(float endX, float endY, float speed) {
		this(-1, -1, endX, endY, speed, 1f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear, 0, 0);
	}

	public MoveBy(float endX, float endY, float speed, EasingMode easing, float sx, float sy) {
		this(-1, -1, endX, endY, speed, 1f, LSystem.DEFAULT_EASE_DELAY, easing, sx, sy);
	}

	public MoveBy(float startX, float startY, float endX, float endY, float speed, float duration, float delay,
			EasingMode easing, float sx, float sy) {
		this._startX = startX;
		this._startY = startY;
		this._endX = endX;
		this._endY = endY;
		this._speed = speed;
		this._direction = Config.EMPTY;
		this.offsetX = sx;
		this.offsetY = sy;
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this.setDelay(0);
	}

	@Override
	public void update(long elapsedTime) {
		synchronized (original) {
			if (_speed == 0) {
				easeTimer.update(elapsedTime);
				if (easeTimer.isCompleted()) {
					_isCompleted = true;
					return;
				}
				float dirX = (_endX - _startX);
				float dirY = (_endY - _startY);
				float newX = _startX + dirX * easeTimer.getProgress() + offsetX;
				float newY = _startY + dirY * easeTimer.getProgress() + offsetY;
				float lastX = original.getX();
				float lastY = original.getY();
				updateDirection((int) (newX - lastX), (int) (newY - lastY));
				movePos(newX, newY);
			} else {
				float x = original.getX();
				float y = original.getY();
				int dirX = (int) (_endX - _startX);
				int dirY = (int) (_endY - _startY);
				int count = 0;
				if (dirX > 0) {
					if (x >= _endX) {
						count++;
					} else {
						x += _speed;
					}
				} else if (dirX < 0) {
					if (x <= _endX) {
						count++;
					} else {
						x -= _speed;
					}
				} else {
					count++;
				}
				if (dirY > 0) {
					if (y >= _endY) {
						count++;
					} else {
						y += _speed;
					}
				} else if (dirY < 0) {
					if (y <= _endY) {
						count++;
					} else {
						y -= _speed;
					}
				} else {
					count++;
				}
				float lastX = original.getX();
				float lastY = original.getY();
				float newX = x + offsetX;
				float newY = y + offsetY;
				updateDirection((int) (newX - lastX), (int) (newY - lastY));
				movePos(newX, newY);
				_isCompleted = (count == 2);
			}
		}
	}

	public int getDirection() {
		return _direction;
	}

	public boolean isDirectionUpdate() {
		return isDirUpdate;
	}

	public void updateDirection(int x, int y) {
		int oldDir = _direction;
		_direction = Field2D.getDirection(x, y, oldDir);
		isDirUpdate = (oldDir != _direction);
	}

	@Override
	public void onLoad() {
		if (original != null) {
			if (_startX == -1) {
				_startX = original.getX();
			}
			if (_startY == -1) {
				_startY = original.getY();
			}
		}
	}

	public float getSpeed() {
		return _speed;
	}

	public MoveBy setSpeed(float speed) {
		this._speed = speed;
		return this;
	}

	public float getStartX() {
		return _startX;
	}

	public float getStartY() {
		return _startY;
	}

	public float getEndX() {
		return _endX;
	}

	public float getEndY() {
		return _endY;
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		MoveBy move = new MoveBy(_startX, _startY, _endX, _endY, _speed, easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode(), offsetX, offsetY);
		move.set(this);
		return move;
	}

	@Override
	public ActionEvent reverse() {
		MoveBy move = new MoveBy(_endX, _endY, _startX, _startY, _speed, easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode(), offsetX, offsetY);
		move.set(this);
		return move;
	}

	@Override
	public String getName() {
		return "moveby";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("speed", _speed).comma().kv("startX", _startX).comma().kv("startY", _startY).comma()
				.kv("endX", _endX).comma().kv("endY", _endY).comma().kv("EaseTimer", easeTimer);
		return builder.toString();
	}

}
