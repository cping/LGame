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
import loon.utils.Easing.EasingMode;
import loon.utils.StringKeyValue;
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
	private EaseTimer easeTimer;

	private boolean angleLoop = false;

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
		this.easeTimer = new EaseTimer(duration, delay, easing);
		this._isCompleted = (startRotation - dstAngle == 0);
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
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

	public RotateTo loop(boolean l) {
		if (this.angleLoop == l) {
			return this;
		}
		this.angleLoop = l;
		if (easeTimer != null) {
			easeTimer.reset(easeTimer.getDelay());
		}
		return this;
	}

	public boolean isLoop() {
		return this.angleLoop;
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			if (!angleLoop) {
				_isCompleted = true;
			} else {
				easeTimer.reset(easeTimer.getDelay());
			}
			original.setRotation(dstAngle);
			return;
		}
		if (startRotation >= dstAngle) {
			if (currentRotation <= dstAngle && currentRotation != 0f) {
				currentRotation = dstAngle;
				if (!angleLoop) {
					_isCompleted = true;
				}
			}
		}
		original.setRotation(currentRotation = (startRotation
				+ ((angleLoop ? 360f : dstAngle) - startRotation) * easeTimer.getProgress() * diffAngle) + speed);
	}

	public float getDiffAngle() {
		return this.diffAngle;
	}

	public void setDiffAngle(float diff) {
		this.diffAngle = diff;
	}

	public float getRotation() {
		return currentRotation;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public ActionEvent cpy() {
		RotateTo r = new RotateTo(startRotation, dstAngle, diffAngle, easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		RotateTo r = new RotateTo(dstAngle, startRotation, diffAngle, easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
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
				.kv("EaseTimer", easeTimer);
		return builder.toString();
	}
}
