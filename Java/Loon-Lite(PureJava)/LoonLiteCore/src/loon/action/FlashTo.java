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

/**
 * 让指定对象产生闪烁效果
 */
public class FlashTo extends ActionEvent {

	private boolean _initVisible;

	private float interval = 0f;

	public FlashTo() {
		this(1f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public FlashTo(float duration) {
		this(duration, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public FlashTo(float duration, float delay) {
		this(duration, delay, EasingMode.Linear);
	}

	public FlashTo(float duration, EasingMode easing) {
		this(duration, LSystem.DEFAULT_EASE_DELAY, easing);
	}

	public FlashTo(float duration, float delay, EasingMode easing) {
		this._easeTimer = new EaseTimer(duration, delay, easing);
		this.interval = delay;
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		if (_isCompleted = _easeTimer.isCompleted()) {
			original.setVisible(_initVisible);
			return;
		}
		interval -= LSystem.toScaleFPS(_easeTimer.getProgress());
		if (this.interval <= 0) {
			this.original.setVisible(!this.original.isVisible());
			this.interval = _easeTimer.getDelay();
		}
	}

	@Override
	public FlashTo reset() {
		super.reset();
		interval = _easeTimer.getDelay();
		return this;
	}

	public float getInterval() {
		return interval;
	}

	@Override
	public void onLoad() {
		if (original != null) {
			_initVisible = original.isVisible();
		}
	}

	@Override
	public boolean isComplete() {
		if (_isCompleted) {
			this.original.setVisible(_initVisible);
		}
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		FlashTo flash = new FlashTo(_easeTimer.getDuration(), _easeTimer.getDelay(), _easeTimer.getEasingMode());
		flash.set(this);
		return flash;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "flash";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		if (original != null) {
			builder.kv("visible", original.isVisible()).comma();
		}
		builder.kv("interval", interval).comma().kv("EaseTimer", _easeTimer);
		return builder.toString();
	}
}
