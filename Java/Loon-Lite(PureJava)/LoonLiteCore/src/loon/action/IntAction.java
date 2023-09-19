/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
import loon.geom.IntValue;
import loon.utils.Easing.EasingMode;
import loon.utils.timer.EaseTimer;

public class IntAction extends ActionEvent {

	private int _start, _end;

	private final IntValue _value;

	private final EaseTimer easeTimer;

	public IntAction() {
		this(EasingMode.Linear);
	}

	public IntAction(EasingMode easing) {
		this(0, 1, 1f, 0f, easing);
	}

	public IntAction(int start, int end) {
		this(start, end, 1f, LSystem.DEFAULT_EASE_DELAY, EasingMode.Linear);
	}

	public IntAction(float duration, float delay, EasingMode easing) {
		this(0, 1, duration, delay, easing);
	}

	public IntAction(int start, int end, float duration, float delay, EasingMode easing) {
		easeTimer = new EaseTimer(duration, delay, easing);
		this._start = start;
		this._end = end;
		this._value = new IntValue(_start);
	}

	@Override
	public void update(long elapsedTime) {
		easeTimer.update(elapsedTime);
		_value.set((int) (_start + (_end - _start) * easeTimer.getValue()));
	}

	public IntAction loop(int count) {
		easeTimer.setLoop(count);
		return this;
	}

	public IntAction loop(boolean l) {
		easeTimer.setLoop(l);
		return this;
	}

	public IntAction reset() {
		easeTimer.reset();
		return this;
	}

	public boolean isLoop() {
		return easeTimer.isLoop();
	}

	@Override
	public void onLoad() {
		_value.set(_start);
	}

	public IntAction set(int v) {
		this._value.set(v);
		return this;
	}

	public IntValue get() {
		return _value;
	}

	public int getStart() {
		return _start;
	}

	public IntAction setStart(int s) {
		this._start = s;
		return this;
	}

	public int getEnd() {
		return _end;
	}

	public IntAction setEnd(int e) {
		this._end = e;
		return this;
	}

	@Override
	public boolean isComplete() {
		return easeTimer.isCompleted();
	}

	@Override
	public ActionEvent cpy() {
		return new IntAction(_start, _end, easeTimer.getDuration(), easeTimer.getDelay(), easeTimer.getEasingMode());
	}

	@Override
	public ActionEvent reverse() {
		return new IntAction(_end, _start, easeTimer.getDuration(), easeTimer.getDelay(), easeTimer.getEasingMode());
	}

	@Override
	public String getName() {
		return "int";
	}

}
