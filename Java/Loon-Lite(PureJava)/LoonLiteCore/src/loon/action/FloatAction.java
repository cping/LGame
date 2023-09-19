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

import loon.LSystem;
import loon.geom.FloatValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class FloatAction extends ActionEvent {

	private float _start, _end;

	private final FloatValue _value;

	public FloatAction() {
		this(EasingMode.Linear);
	}

	public FloatAction(EasingMode easing) {
		this(0f, 1f, 1f, 0f, easing);
	}

	public FloatAction(float start, float end) {
		this(start, end, 1f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public FloatAction(float duration, float delay, EasingMode easing) {
		this(0f, 1f, duration, delay, easing);
	}

	public FloatAction(float start, float end, float duration, float delay, EasingMode easing) {
		_easeTimer = new EaseTimer(duration, delay, easing);
		this._start = start;
		this._end = end;
		this._value = new FloatValue(_start);
	}

	@Override
	public void update(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		_value.set((_start + (_end - _start) * _easeTimer.getProgress()));
	}

	@Override
	public void onLoad() {
		_value.set(_start);
	}

	public FloatAction set(float v) {
		this._value.set(v);
		return this;
	}

	public FloatValue get() {
		return _value;
	}

	public float getStart() {
		return _start;
	}

	public FloatAction setStart(float s) {
		this._start = s;
		return this;
	}

	public float getEnd() {
		return _end;
	}

	public FloatAction setEnd(float e) {
		this._end = e;
		return this;
	}

	@Override
	public boolean isComplete() {
		return _easeTimer.isCompleted();
	}

	@Override
	public ActionEvent cpy() {
		return new FloatAction(_start, _end, _easeTimer.getDuration(), _easeTimer.getDelay(), _easeTimer.getEasingMode());
	}

	@Override
	public ActionEvent reverse() {
		return new FloatAction(_end, _start, _easeTimer.getDuration(), _easeTimer.getDelay(), _easeTimer.getEasingMode());
	}

	@Override
	public String getName() {
		return "float";
	}

}
