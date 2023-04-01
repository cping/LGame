/**
 * Copyright 2008 - 2016
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
package loon.utils.timer;

import loon.LSystem;
import loon.utils.Easing;
import loon.utils.StringKeyValue;
import loon.utils.Easing.EasingMode;

/**
 * 缓动动画使用的计时器(绑定缓动方法的具体实现)
 */
public class EaseTimer extends BasicTimer {

	private float _ease_value_max = 1f;
	private float _ease_value_min = 0f;

	private EasingMode _mode;

	public EaseTimer(float duration) {
		this(duration, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public EaseTimer(EasingMode mode) {
		this(1f, mode);
	}

	public EaseTimer(float duration, EasingMode mode) {
		this(duration, LSystem.DEFAULT_EASE_DELAY, mode);
	}

	public EaseTimer(float duration, float delay) {
		this(duration, delay, EasingMode.Linear);
	}

	public EaseTimer(float duration, float delay, EasingMode mode) {
		this(duration, LSystem.DEFAULT_EASE_DELAY, mode, 0);
	}

	public EaseTimer(float duration, float delay, EasingMode mode, int loop) {
		super(0, duration, loop);
		this._mode = mode;
		this._delay = delay;
		this._ease_value_max = 1f;
		this._ease_value_min = 0f;
	}

	public EaseTimer(EaseTimer timer) {
		super(timer);
		this._mode = timer._mode;
		this._ease_value_max = timer._ease_value_max;
		this._ease_value_min = timer._ease_value_min;
	}

	@Override
	public float process() {
		switch (this._mode) {
		case InQuad:
			this._progress = Easing.inQuad(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutQuad:
			this._progress = Easing.outQuad(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutQuad:
			this._progress = Easing.inOutQuad(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InCubic:
			this._progress = Easing.inCubic(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutCubic:
			this._progress = Easing.outCubic(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InQuart:
			this._progress = Easing.inQuart(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutQuart:
			this._progress = Easing.outQuart(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutQuart:
			this._progress = Easing.inOutQuart(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InQuint:
			this._progress = Easing.inQuint(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutQuint:
			this._progress = Easing.outQuint(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutQuint:
			this._progress = Easing.inOutQuint(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InSine:
			this._progress = Easing.inSine(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutSine:
			this._progress = Easing.outSine(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutSine:
			this._progress = Easing.inOutSine(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InExp:
			this._progress = Easing.inExp(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutExp:
			this._progress = Easing.outExp(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutExp:
			this._progress = Easing.inOutExp(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InCirc:
			this._progress = Easing.inCirc(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutCirc:
			this._progress = Easing.outCirc(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutCirc:
			this._progress = Easing.inOutCirc(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InBack:
			this._progress = Easing.inBack(this._timer, this._duration, this._ease_value_max, this._ease_value_min,
					0.1f);
			break;
		case OutBack:
			this._progress = Easing.outBack(this._timer, this._duration, this._ease_value_max, this._ease_value_min,
					0.1f);
			break;
		case InOutBack:
			this._progress = Easing.inOutBack(this._timer, this._duration, this._ease_value_max, this._ease_value_min,
					0.1f);
			break;
		case OutBounce:
			this._progress = Easing.outBounce(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InBounce:
			this._progress = Easing.inBounce(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutBounce:
			this._progress = Easing.inOutBounce(this._timer, this._duration, this._ease_value_max,
					this._ease_value_min);
		case InElastic:
			this._progress = Easing.inElastic(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case OutElastic:
			this._progress = Easing.outElastic(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		case InOutElastic:
			this._progress = Easing.inOutElastic(this._timer, this._duration, this._ease_value_max,
					this._ease_value_min);
			break;
		case Linear:
		default:
			this._progress = Easing.linear(this._timer, this._duration, this._ease_value_max, this._ease_value_min);
			break;
		}
		return this._progress;
	}

	@Override
	public EaseTimer reset(float delay) {
		super.reset(delay);
		this._ease_value_max = 1f;
		this._ease_value_min = 0f;
		return this;
	}

	public EaseTimer setEasingMode(EasingMode ease) {
		this._mode = ease;
		return this;
	}

	public EasingMode getEasingMode() {
		return this._mode;
	}

	public float getTweenValue() {
		return Easing.getTween(this._mode, this._timer);
	}

	public float getEaseValueMax() {
		return _ease_value_max;
	}

	public EaseTimer setEaseValueMax(float max) {
		this._ease_value_max = max;
		return this;
	}

	public float getEaseValueMin() {
		return _ease_value_min;
	}

	public EaseTimer setEaseValueMin(float min) {
		this._ease_value_min = min;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("EaseTimer");
		builder.kv("timer", _timer).comma().kv("duration", _duration).comma().kv("delta", _delta).comma()
				.kv("progress", _progress).comma().kv("timeInAfter", _timeInAfter).comma().kv("easing", _mode).comma()
				.kv("easemax", _ease_value_max).comma().kv("easemin", _ease_value_min).comma()
				.kv("finished", _finished);
		return builder.toString();
	}

}
