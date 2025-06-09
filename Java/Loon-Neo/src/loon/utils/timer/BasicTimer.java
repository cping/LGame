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
package loon.utils.timer;

import loon.LSystem;
import loon.utils.MathUtils;

/**
 * 缓动动画用基础时间类(方便用户自行扩展)
 */
public class BasicTimer {

	protected float _timer = 0f;
	protected float _elapsed = 0f;
	protected float _duration = 1f;

	protected float _timeInAfter = 0f;
	protected float _progress = 0f;
	protected float _delay = 0f;
	protected float _delta = 0f;

	protected boolean _finished = false;
	protected boolean _paused = false;

	private float _initTimer;

	private float _initDuration;

	private int _maxLoop;

	private int _loopCount;

	public BasicTimer() {
		this(0);
	}

	public BasicTimer(int loop) {
		this(0f, 1f, loop);
	}

	public BasicTimer(float time, float duration, int loop) {
		this._timer = time;
		this._duration = duration;
		this._initTimer = time;
		this._initDuration = duration;
		this._maxLoop = loop;
	}

	public BasicTimer(BasicTimer timer) {
		this._timer = timer._timer;
		this._elapsed = timer._elapsed;
		this._duration = timer._duration;
		this._timeInAfter = timer._timeInAfter;
		this._progress = timer._progress;
		this._delay = timer._delay;
		this._delta = timer._delta;
		this._finished = timer._finished;
		this._paused = timer._paused;
		this._initTimer = timer._initTimer;
		this._initDuration = timer._initDuration;
		this._maxLoop = timer._maxLoop;
		this._loopCount = timer._loopCount;
	}

	public boolean action(LTimerContext context) {
		return action(context.timeSinceLastUpdate);
	}

	public boolean action(long elapsedTime) {
		update(elapsedTime);
		return isCompleted();
	}

	public void update(long elapsedTime) {
		update(MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(float dt) {

		if (_finished) {
			return;
		}

		if (_paused) {
			return;
		}
		if (isLoop() && !isRunning()) {
			reset(dt);
			_loopCount++;
		}

		this._delta = dt;
		this._timer += dt;
		this._elapsed = _duration - _timer;
		if (this._timer >= _delay) {
			_timeInAfter += _delta / _duration;
		}
		if (this._timer >= this._duration) {
			this._timer = this._duration;
			if (!isLoop()) {
				this._finished = true;
			}
		}
		this._progress = process();
	}

	public boolean isLoop() {
		return (_maxLoop == -1) || (_maxLoop != 0 && _loopCount != _maxLoop);
	}

	public BasicTimer setLoop(boolean loop) {
		if (loop) {
			setLoop(-1);
		} else {
			setLoop(0);
		}
		return this;
	}

	public BasicTimer setLoop(int loop) {
		this._maxLoop = loop;
		this._loopCount = 0;
		return this;
	}

	public int getLoopCount() {
		return this._loopCount;
	}

	public BasicTimer stopLoop() {
		this._maxLoop = 0;
		this._loopCount = 0;
		return this;
	}

	protected float process() {
		return _timer / _duration;
	}

	public BasicTimer reset() {
		return reset(LSystem.DEFAULT_EASE_DELAY);
	}

	public BasicTimer reset(float delay) {
		this._timer = _initTimer;
		this._duration = _initDuration;
		this._elapsed = 0f;
		this._finished = false;
		this._paused = false;
		this._progress = 0f;
		this._delta = 0;
		this._delay = delay;
		this._timeInAfter = 0;
		return this;
	}

	public BasicTimer setDuration(float d) {
		this._duration = d;
		return this;
	}

	public boolean isRunning() {
		return _timer < _duration;
	}

	public boolean isPaused() {
		return _paused;
	}

	public boolean isFinished() {
		return wasStarted() && !isRunning();
	}

	public boolean isCompleted() {
		return _finished;
	}

	public boolean wasStarted() {
		return _timer > 0f;
	}

	public float getValue() {
		return this._progress + this._timeInAfter;
	}

	public float getProgress() {
		return _progress;
	}

	public float getDuration() {
		return _duration;
	}

	public float getElapsed() {
		return _elapsed;
	}

	public float getRemaining() {
		return _timer;
	}

	public BasicTimer start() {
		return start(_duration);
	}

	public BasicTimer start(float duration) {
		if (duration <= 0f) {
			return this;
		}
		this._paused = false;
		this._timer = _initTimer;
		this._duration = duration;
		this._elapsed = 0f;
		this._progress = 0f;
		return this;
	}

	public BasicTimer add(float amount) {
		if (amount <= 0f) {
			return this;
		}
		this._paused = false;
		this._timer += amount;
		this._elapsed = _duration - _timer;
		this._progress = process();
		return this;
	}

	public float getTimeInAfter() {
		return this._timeInAfter;
	}

	public float getDelta() {
		return this._delta;
	}

	public float getDelay() {
		return this._delay;
	}

	public float getTimer() {
		return this._timer;
	}

	public BasicTimer setInitTimer(float t) {
		this._initTimer = t;
		return this;
	}

	public BasicTimer setInitDuration(float d) {
		this._initDuration = d;
		return this;
	}

	public BasicTimer pause() {
		if (_paused) {
			return this;
		}
		_paused = true;
		return this;
	}

	public BasicTimer resume() {
		if (!_paused) {
			return this;
		}
		_paused = false;
		return this;
	}

	public BasicTimer stop() {
		_paused = false;
		_timer = 0f;
		_progress = 0f;
		return this;
	}

	public BasicTimer cancel() {
		reset();
		return this;
	}

	public BasicTimer restart() {
		if (_duration <= 0f) {
			return this;
		}
		reset();
		return this;
	}
}
