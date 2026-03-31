/**
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
package loon.action;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.Duration;
import loon.utils.timer.EaseTimer;

public class ScaleTo extends ActionEvent {

	private float _delta;

	private float _deltaX, _deltaY;

	private float _startX = -1f, _startY = -1f;

	private float _endX, _endY;

	private float _speed;

	public ScaleTo(float s) {
		this(-1, -1, s, s, LSystem.MIN_SECONE_SPEED_FIXED, 1f, EasingMode.Linear);
	}

	public ScaleTo(float s, float duration, EasingMode mode) {
		this(s, s, duration, mode);
	}

	public ScaleTo(float sx, float sy) {
		this(-1, -1, sx, sy, LSystem.MIN_SECONE_SPEED_FIXED, 1f, EasingMode.Linear);
	}

	public ScaleTo(float sx, float sy, float duration, EasingMode mode) {
		this(-1, -1, sx, sy, LSystem.MIN_SECONE_SPEED_FIXED, duration, mode);
	}

	public ScaleTo(float sx, float sy, float sp, float duration, EasingMode mode) {
		this(-1, -1, sx, sy, sp, duration, mode);
	}

	public ScaleTo(float stx, float sty, float sx, float sy) {
		this(-1, -1, sx, sy, LSystem.MIN_SECONE_SPEED_FIXED, 1f, EasingMode.Linear);
	}

	public ScaleTo(float stx, float sty, float sx, float sy, float sp, float duration, EasingMode mode) {
		this._easeTimer = new EaseTimer(duration, mode);
		this._startX = stx;
		this._startY = sty;
		this._endX = sx;
		this._endY = sy;
		this._speed = sp;
		this._deltaX = _endX - _startX;
		this._deltaY = _endY - _startY;
	}

	public ScaleTo setSpeed(float s) {
		this._speed = s;
		return this;
	}

	public float getSpeed() {
		return _speed;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			if (_startX == -1) {
				_startX = original.getScaleX();
			}
			if (_startY == -1) {
				_startY = original.getScaleY();
			}
			_deltaX = _endX - _startX;
			_deltaY = _endY - _startY;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (original != null) {
			_easeTimer.update(elapsedTime);
			if (original != null) {
				_delta += (MathUtils.max(Duration.toS(elapsedTime), _speed) * _easeTimer.getProgress());
				original.setScale(_startX + (_deltaX * _delta), _startY + (_deltaY * _delta));
				_isCompleted = (_deltaX > 0 ? (original.getScaleX() >= _endX) : (original.getScaleX() <= _endX))
						&& (_deltaY > 0 ? (original.getScaleY() >= _endY) : (original.getScaleY() <= _endY));
			}
		} else {
			_isCompleted = true;
		}
		if (_isCompleted && original != null) {
			original.setScale(_endX, _endY);
		}
	}

	public float getDeltaX() {
		return _deltaX;
	}

	public float getDeltaY() {
		return _deltaY;
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
	public ActionEvent cpy() {
		ScaleTo scale = new ScaleTo(_startX, _startY, _endX, _endY, _speed, _easeTimer.getDuration(),
				_easeTimer.getEasingMode());
		scale.set(this);
		return scale;
	}

	@Override
	public ActionEvent reverse() {
		ScaleTo scale = new ScaleTo(_endX, _endY, _startX, _startY, _speed, _easeTimer.getDuration(),
				_easeTimer.getEasingMode());
		scale.set(this);
		return scale;
	}

	@Override
	public String getName() {
		return "scale";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startX", _startX).comma().kv("startY", _startY).comma().kv("deltaX", _deltaX).comma()
				.kv("deltaY", _deltaY).comma().kv("endX", _endX).comma().kv("endY", _endY).comma().kv("speed", _speed)
				.comma().kv("delta", _delta);
		return builder.toString();
	}

}
