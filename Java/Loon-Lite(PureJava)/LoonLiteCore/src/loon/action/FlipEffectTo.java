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

import loon.utils.StringKeyValue;
import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.timer.EaseTimer;

public class FlipEffectTo extends ActionEvent {

	protected boolean _flipAllow;

	protected boolean _effectRunning;

	protected boolean _flipIn;

	protected float _duration;

	protected Vector2f _initScale = new Vector2f();

	protected FlipType _flipType;

	public FlipEffectTo(FlipType ft, boolean flip) {
		this(EasingMode.Linear, ft, 1f, flip);
	}

	public FlipEffectTo(FlipType ft, float d, boolean flip) {
		this(EasingMode.Linear, ft, d, flip);
	}

	public FlipEffectTo(EasingMode e, FlipType ft, float d, boolean flip) {
		this(e, ft, d, flip, true);
	}

	public FlipEffectTo(EasingMode e, FlipType ft, float d, boolean flip, boolean effect) {
		this._duration = MathUtils.max(d, LSystem.DEFAULT_EASE_DELAY) / 2f;
		this._easeTimer = new EaseTimer(this._duration, e);
		this._flipType = (ft == null ? FlipType.FlipX : ft);
		this._flipAllow = flip;
		this._effectRunning = effect;
	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		if (_effectRunning) {
			float flipScaleX = 1f;
			float flipScaleY = 1f;
			_easeTimer.update(elapsedTime);
			float alpha = _easeTimer.getProgress();
			if (!_flipIn) {
				if (_easeTimer.isCompleted()) {
					_flipIn = true;
					_easeTimer.reset();
					if (original != null) {
						Flip<?> flip = (Flip<?>) original;
						switch (_flipType) {
						case FlipX:
							flip.setFlipX(_flipAllow);
							break;
						case FlipY:
							flip.setFlipY(_flipAllow);
							break;
						}
					}
				} else {
					float v = MathUtils.clamp(1f - alpha, 0f, 1f);
					if (original != null) {
						switch (_flipType) {
						case FlipX:
							flipScaleX = MathUtils.clamp(original.getScaleX() * v, 0f, 1f);
							flipScaleY = original.getScaleY();
							break;
						case FlipY:
							flipScaleX = original.getScaleX();
							flipScaleY = MathUtils.clamp(original.getScaleY() * v, 0f, 1f);
							break;
						}
						original.setScale(flipScaleX, flipScaleY);
					}
				}
			} else if (_easeTimer.isCompleted()) {
				this._isCompleted = true;
				if (original != null) {
					Flip<?> flip = (Flip<?>) original;
					switch (_flipType) {
					case FlipX:
						flip.setFlipX(_flipAllow);
						break;
					case FlipY:
						flip.setFlipY(_flipAllow);
						break;
					}
					original.setScale(_initScale.x, _initScale.y);
				}
			} else {
				float v = MathUtils.clamp(alpha, 0f, 1f);
				if (original != null) {
					switch (_flipType) {
					case FlipX:
						flipScaleX = MathUtils.clamp(_initScale.x * v, 0f, 1f);
						flipScaleY = original.getScaleY();
						break;
					case FlipY:
						flipScaleX = original.getScaleX();
						flipScaleY = MathUtils.clamp(_initScale.y * v, 0f, 1f);
						break;
					}
					original.setScale(flipScaleX, flipScaleY);
				}
			}
		} else if (original != null && original instanceof Flip<?>) {
			Flip<?> flip = (Flip<?>) original;
			switch (_flipType) {
			case FlipX:
				flip.setFlipX(_flipAllow);
				break;
			case FlipY:
				flip.setFlipY(_flipAllow);
				break;
			}
			this._isCompleted = true;
		}
	}

	public boolean isEffectRunning() {
		return _effectRunning;
	}

	public boolean isFlipAllow() {
		return _flipAllow;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			_initScale.set(original.getScaleX(), original.getScaleY());
		} else {
			_initScale.set(1f, 1f);
		}
	}

	@Override
	public ActionEvent cpy() {
		FlipEffectTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipEffectTo(_easeTimer.getEasingMode(), _flipType, _easeTimer.getDuration(), this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipEffectTo(EasingMode.Linear, _flipType, 1f, this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public ActionEvent reverse() {
		FlipEffectTo flip = null;
		if (_easeTimer != null) {
			flip = new FlipEffectTo(_easeTimer.getEasingMode(), _flipType, _easeTimer.getDuration(), !this._flipAllow,
					this._effectRunning);
		} else {
			flip = new FlipEffectTo(EasingMode.Linear, _flipType, 1f, !this._flipAllow, this._effectRunning);
		}
		flip.set(this);
		return flip;
	}

	@Override
	public String getName() {
		return "flip";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("flipType", _flipType).comma().kv("flip", _flipAllow).comma().kv("effectRunning", _effectRunning);
		return builder.toString();
	}

}
