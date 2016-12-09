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

import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class RotateTo extends ActionEvent {

	private float speed = 2f;
	private float diffAngle = 1f;
	private float startRotation;
	private float dstAngle;
	private float currentRotation;
	private EaseTimer easeTimer;

	public RotateTo(float dstAngle) {
		this(dstAngle, 2f);
	}

	public RotateTo(float dstAngle, float speed, EasingMode easing) {
		this(0, dstAngle, 1f, speed, 1f / 60f, easing);
	}

	public RotateTo(float dstAngle, float speed) {
		this(0, dstAngle, 1f, speed, 1f / 60f, EasingMode.Linear);
	}

	public RotateTo(float dstAngle, float diffAngle, float speed,
			EasingMode easing) {
		this(0, dstAngle, diffAngle, 2f, 1f / 60f, easing);
	}

	public RotateTo(float dstAngle, float diffAngle, float speed) {
		this(0, dstAngle, diffAngle, 2f, 1f / 60f, EasingMode.Linear);
	}

	public RotateTo(float startRotation, float dstAngle, float diffAngle,
			float duration, float delay, EasingMode easing) {
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
		startRotation = original.getRotation();
		if ((dstAngle - startRotation) == 0) {
			_isCompleted = true;
		}
	}

	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		if (easeTimer.isCompleted()) {
			_isCompleted = true;
			original.setRotation(dstAngle);
			return;
		}
		if (startRotation >= dstAngle) {
			if (currentRotation <= dstAngle && currentRotation != 0) {
				currentRotation = dstAngle;
				_isCompleted = true;
			}
		}
		original.setRotation(currentRotation = (startRotation + (dstAngle - startRotation)
				* easeTimer.getProgress() * diffAngle)
				+ speed);
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
		RotateTo r = new RotateTo(startRotation, dstAngle, diffAngle,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		RotateTo r = new RotateTo(dstAngle, startRotation, diffAngle,
				easeTimer.getDuration(), easeTimer.getDelay(),
				easeTimer.getEasingMode());
		r.set(this);
		return r;
	}

	@Override
	public String getName() {
		return "rotate";
	}

}
