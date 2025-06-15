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

import loon.utils.StringKeyValue;
import loon.LSystem;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

/**
 * 旋转用缓动事件
 */
public class RotateTo extends ActionEvent {

	private float speed = 2f;
	private float diffAngle = 1f;
	private float startRotation = -1f;
	private float dstAngle = 0;
	private float currentRotation = 0;

	public RotateTo(float dstAngle) {
		this(dstAngle, 2f);
	}

	public RotateTo(float dstAngle, float speed, EasingMode easing) {
		this(-1f, dstAngle, 1f, speed, LSystem.DEFAULT_EASE_DELAY, easing);
	}

	public RotateTo(float dstAngle, float speed) {
		this(-1f, dstAngle, 1f, speed, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public RotateTo(float dstAngle, float diffAngle, float speed, EasingMode easing) {
		this(-1f, dstAngle, diffAngle, 2f, LSystem.DEFAULT_EASE_DELAY, easing);
	}

	public RotateTo(float dstAngle, float diffAngle, float speed) {
		this(-1f, dstAngle, diffAngle, 2f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public RotateTo(float startRotation, float dstAngle, float diffAngle, float duration, float delay,
			EasingMode easing) {
		this.startRotation = startRotation;
		this.dstAngle = dstAngle;
		this.diffAngle = diffAngle;
		this._easeTimer = new EaseTimer(duration, delay, easing);
		this._isCompleted = (startRotation - dstAngle == 0);
	}

	@Override
	public void onLoad() {
		if (startRotation == -1f) {
			startRotation = original.getRotation();
		}
		if ((dstAngle - startRotation) == 0) {
			_isCompleted = true;
		}
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (_easeTimer.isCompleted()) {
			_isCompleted = true;
			original.setRotation(dstAngle);
			return;
		}
		if (startRotation >= dstAngle) {
			if (currentRotation <= dstAngle && currentRotation != 0f) {
				currentRotation = dstAngle;
				_isCompleted = true;
			}
		}
		original.setRotation(
				currentRotation = (startRotation + (dstAngle - startRotation) * _easeTimer.getProgress() * diffAngle)
						+ LSystem.toScaleFPS(speed));
	}

	public float getDiffAngle() {
		return this.diffAngle;
	}

	public RotateTo setDiffAngle(float diff) {
		this.diffAngle = diff;
		return this;
	}

	public float getRotation() {
		return currentRotation;
	}

	public float getSpeed() {
		return speed;
	}

	public RotateTo setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	@Override
	public ActionEvent cpy() {
		RotateTo r = new RotateTo(startRotation, dstAngle, diffAngle, _easeTimer.getDuration(), _easeTimer.getDelay(),
				_easeTimer.getEasingMode());
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		RotateTo r = new RotateTo(dstAngle, startRotation, diffAngle, _easeTimer.getDuration(), _easeTimer.getDelay(),
				_easeTimer.getEasingMode());
		r.set(this);
		return r;
	}

	@Override
	public String getName() {
		return "rotate";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("speed", speed).comma().kv("diffAngle", diffAngle).comma().kv("startRotation", startRotation).comma()
				.kv("dstAngle", dstAngle).comma().kv("currentRotation", currentRotation).comma()
				.kv("EaseTimer", _easeTimer);
		return builder.toString();
	}
}
