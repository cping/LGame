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

import loon.utils.StringKeyValue;
import loon.LSystem;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class ShakeTo extends ActionEvent {

	private float shakeTimer;
	private float shakeX, oldShakeX;
	private float shakeY, oldShakeY;
	private float startOffsetX;
	private float startOffsetY;

	public ShakeTo(float shakeX, float shakeY) {
		this(shakeX, shakeY, 1f);
	}

	public ShakeTo(float shakeX, float shakeY, float duration) {
		this(shakeX, shakeY, duration, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public ShakeTo(float shakeX, float shakeY, float duration, float delay) {
		this(shakeX, shakeY, duration, delay, EasingMode.Linear);
	}

	public ShakeTo(float shakeX, float shakeY, float duration, float delay, EasingMode easing) {
		this._easeTimer = new EaseTimer(duration, delay, easing);
		this.shakeX = oldShakeX = shakeX;
		this.shakeY = oldShakeY = shakeY;
		this.offsetX = shakeX;
		this.offsetY = shakeY;
		this.shakeTimer = delay;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (_easeTimer.isCompleted()) {
			_isCompleted = true;
			original.setLocation(this.startOffsetX, this.startOffsetY);
			return;
		}
		final float v = _easeTimer.getTimeInAfter() * _easeTimer.getDelta();
		this.shakeX += v;
		this.shakeY += v;
		if (this.offsetX > 0.0f) {
			this.offsetX = (-this.shakeX);
			this.offsetY = (-this.shakeY);
		} else {
			this.offsetX = this.shakeX;
			this.offsetY = this.shakeY;
		}
		original.setLocation(this.startOffsetX + this.offsetX, this.startOffsetY + this.offsetY);
	}

	public float getShakeX() {
		return shakeX;
	}

	public float getShakeY() {
		return shakeY;
	}

	public float getOldShakeX() {
		return oldShakeX;
	}

	public float getOldShakeY() {
		return oldShakeY;
	}

	public float getStartOffsetX() {
		return startOffsetX;
	}

	public float getStartOffsetY() {
		return startOffsetY;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			this.startOffsetX = original.getX();
			this.startOffsetY = original.getY();
		}
	}

	@Override
	public ActionEvent cpy() {
		ShakeTo shake = new ShakeTo(_easeTimer.getDuration(), shakeTimer, oldShakeX, oldShakeY);
		shake.set(this);
		return shake;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "shake";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("shakeTimer", shakeTimer).comma().kv("shakeX", shakeX).comma().kv("shakeY", shakeY).comma()
				.kv("startOffsetX", startOffsetX).comma().kv("startOffsetY", startOffsetY).comma()
				.kv("EaseTimer", _easeTimer);
		return builder.toString();
	}

}
