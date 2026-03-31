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

import loon.action.sprite.ISprite;
import loon.component.LComponent;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.Duration;
import loon.utils.timer.EaseTimer;

public class FlashScaleTo extends ActionEvent {

	private boolean _zooming;

	private float _scaleValue;

	private float _deltaScale;

	private float _startScale;

	private float _endScale;

	private float _initScaleX, _initScaleY;

	private float _delta;

	private float _speed;

	public FlashScaleTo(float endScale) {
		this(1f, endScale);
	}

	public FlashScaleTo(float startScale, float endScale) {
		this(startScale, endScale, 1.5f);
	}

	public FlashScaleTo(float startScale, float endScale, float speed) {
		this(startScale, endScale, speed, 1f, EasingMode.Linear);
	}

	public FlashScaleTo(float startScale, float endScale, float speed, float duration, EasingMode mode) {
		this._easeTimer = new EaseTimer(duration, mode);
		this._startScale = startScale;
		this._endScale = endScale;
		this._speed = speed;
	}

	public FlashScaleTo setSpeed(float s) {
		this._speed = s;
		return this;
	}

	public float getSpeed() {
		return _speed;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this._initScaleX = original.getScaleX();
			this._initScaleY = original.getScaleY();
			this._startScale = MathUtils.max(_initScaleX, _initScaleY);
			_deltaScale = _endScale - _startScale;
		}
	}

	@Override
	public void update(long elapsedTime) {
		if (original != null) {
			_easeTimer.update(elapsedTime);
			if (original != null) {
				if (!_zooming && _startScale <= _endScale) {
					_delta += (MathUtils.max(Duration.toS(elapsedTime), _speed) * _easeTimer.getProgress());
					_scaleValue = _startScale + (_deltaScale * _delta);
					original.setScale(_scaleValue, _scaleValue);
					setAnchor(_scaleValue);
					final float oldScaleValue = MathUtils.max(original.getScaleX(), original.getScaleY());
					_zooming = (_deltaScale > 0 ? (oldScaleValue >= _endScale) : (oldScaleValue <= _endScale));
					if (_zooming) {
						_easeTimer.restart();
						_delta = 0;
						_deltaScale = _endScale - _startScale;
					}
				} else if (_zooming && _scaleValue >= _startScale) {
					_delta += (MathUtils.max(Duration.toS(elapsedTime), _speed) * _easeTimer.getProgress());
					_scaleValue = _endScale - (_deltaScale * _delta);
					original.setScale(_scaleValue, _scaleValue);
					setAnchor(_scaleValue);
					final float oldScaleValue = MathUtils.max(original.getScaleX(), original.getScaleY());
					_isCompleted = (_deltaScale > 0 ? (oldScaleValue <= _startScale) : (oldScaleValue >= _startScale));
				}
			}
		} else {
			_isCompleted = true;
		}
		if (_isCompleted && original != null) {
			original.setScale(_initScaleX, _initScaleY);
			resetAnchor();
		}
	}

	private void setAnchor(float scale) {
		if (original == null) {
			return;
		}
		if (original instanceof ISprite) {
			((ISprite) original).setAnchor(scale, scale);
		} else if (original instanceof LComponent) {
			((LComponent) original).setAnchor(scale, scale);
		}
	}

	private void resetAnchor() {
		if (original == null) {
			return;
		}
		if (original instanceof ISprite) {
			((ISprite) original).resetAnchor();
		} else if (original instanceof LComponent) {
			((LComponent) original).resetAnchor();
		}
	}

	public float getDeltaScale() {
		return _deltaScale;
	}

	public float getStartScale() {
		return _startScale;
	}

	public float getEndScale() {
		return _endScale;
	}

	@Override
	public ActionEvent cpy() {
		FlashScaleTo scale = new FlashScaleTo(_startScale, _endScale, _speed, _easeTimer.getDuration(),
				_easeTimer.getEasingMode());
		scale.set(this);
		return scale;
	}

	@Override
	public ActionEvent reverse() {
		FlashScaleTo scale = new FlashScaleTo(_endScale, _startScale, _speed, _easeTimer.getDuration(),
				_easeTimer.getEasingMode());
		scale.set(this);
		return scale;
	}

	@Override
	public String getName() {
		return "flashscale";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startScale", _startScale).comma().kv("deltaScale", _deltaScale).comma().kv("endScale", _endScale)
				.kv("speed", _speed).comma().kv("delta", _delta);
		return builder.toString();
	}

}
