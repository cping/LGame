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

import loon.LRelease;
import loon.utils.MathUtils;

@SuppressWarnings("unchecked")
public abstract class ActionTweenBase<T> {

	protected ActionBind _target;

	private TweenTo<T> _actionTween;
	private LRelease _dispose;

	private int _step;
	private boolean _isIterationStep;
	private boolean _isBackward;

	protected int repeatSize;
	protected float delay;
	protected float duration;
	private float _repeatDelay;
	private float _currentTime;
	private float _deltaTime;
	private boolean _isStarted;
	private boolean _isInitialized;
	private boolean _isFinished;
	private boolean _isKilled;
	private boolean _isPaused;

	private ActionCallback _callback;
	private int _callbackTriggers;

	boolean _isAutoRemoveEnabled;
	boolean _isAutoStartEnabled;

	protected void reset() {
		_step = -2;
		repeatSize = 0;
		_isIterationStep = _isBackward = false;
		delay = duration = _repeatDelay = _currentTime = _deltaTime = 0;
		_isStarted = _isInitialized = _isFinished = _isKilled = _isPaused = false;
		_callback = null;
		_callbackTriggers = ActionMode.COMPLETE;
		_isAutoRemoveEnabled = _isAutoStartEnabled = true;
	}

	public TweenTo<T> start() {
		build();
		_currentTime = 0;
		_isStarted = true;
		_actionTween = new TweenTo<T>(this);
		if (this._dispose != null) {
			_actionTween.dispose(this._dispose);
		}
		ActionControl.get().addAction(_actionTween, _target);
		return _actionTween;
	}

	public T dispose(LRelease dispose) {
		this._dispose = dispose;
		if (_actionTween != null) {
			_actionTween.dispose(this._dispose);
		}
		return (T) this;
	}

	public T build() {
		return (T) this;
	}

	public T delay(float delay) {
		this.delay += delay;
		return (T) this;
	}

	public T kill() {
		_isKilled = true;
		return (T) this;
	}

	public void free() {
		_dispose = null;
	}

	public T pause() {
		_isPaused = true;
		return (T) this;
	}

	public T resume() {
		_isPaused = false;
		return (T) this;
	}

	public T repeat(int count, float delay) {
		if (_isStarted) {
			return (T) this;
		}
		repeatSize = count;
		_repeatDelay = delay >= 0 ? delay : 0;
		_isBackward = false;
		return (T) this;
	}

	public T repeatBackward(int count, float delay) {
		if (_isStarted) {
			return (T) this;
		}
		repeatSize = count;
		_repeatDelay = delay >= 0 ? delay : 0;
		_isBackward = true;
		return (T) this;
	}

	public T setCallback(ActionCallback callback) {
		this._callback = callback;
		return (T) this;
	}

	public T setCallbackTriggers(int flags) {
		this._callbackTriggers = flags;
		return (T) this;
	}

	public float getDelay() {
		return delay;
	}

	public float getDuration() {
		return duration;
	}

	public int getRepeatCount() {
		return repeatSize;
	}

	public float getRepeatDelay() {
		return _repeatDelay;
	}

	public float getFullDuration() {
		if (repeatSize < 0) {
			return -1;
		}
		return delay + duration + (_repeatDelay + duration) * repeatSize;
	}

	public int getStep() {
		return _step;
	}

	public float getCurrentTime() {
		return _currentTime;
	}

	public boolean isStarted() {
		return _isStarted;
	}

	public boolean isInitialized() {
		return _isInitialized;
	}

	public boolean isFinished() {
		return _isFinished || _isKilled;
	}

	public boolean isBackward() {
		return _isBackward;
	}

	public boolean isPaused() {
		return _isPaused;
	}

	protected abstract void forceStartValues();

	protected abstract void forceEndValues();

	protected abstract boolean containsTarget(ActionBind target);

	protected abstract boolean containsTarget(ActionBind target, int tweenType);

	protected void initializeOverride() {
	}

	protected void update(int step, int lastStep, boolean isIterationStep, float delta) {
	}

	protected void forceToStart() {
		_currentTime = -delay;
		_step = -1;
		_isIterationStep = false;
		if (isReverse(0)) {
			forceEndValues();
		} else {
			forceStartValues();
		}
	}

	protected void forceToEnd(float time) {
		_currentTime = time - getFullDuration();
		_step = repeatSize * 2 + 1;
		_isIterationStep = false;
		if (isReverse(repeatSize * 2)) {
			forceStartValues();
		} else {
			forceEndValues();
		}
	}

	protected void callCallback(int type) {
		if (_callback != null && (_callbackTriggers & type) > 0)
			_callback.onEvent(type, this);
	}

	protected boolean isReverse(int step) {
		return _isBackward && MathUtils.abs(step % 4) == 2;
	}

	protected boolean isValid(int step) {
		return (step >= 0 && step <= repeatSize * 2) || repeatSize < 0;
	}

	protected void killTarget(ActionBind _target) {
		if (containsTarget(_target)) {
			kill();
		}
	}

	protected void killTarget(ActionBind _target, int tweenType) {
		if (containsTarget(_target, tweenType)) {
			kill();
		}
	}

	private void checkCompletion() {
		_isFinished = repeatSize >= 0 && (_step > repeatSize * 2 || _step < 0);
	}

	protected boolean actionEventOver() {
		return true;
	}

	public void update(float delta) {
		if (!_isStarted || _isPaused || _isKilled) {
			return;
		}

		_deltaTime = delta;

		if (!_isInitialized) {
			initialize();
		}

		if (_isInitialized) {
			checkRelaunch();
			updateStep();
			checkCompletion();
		}

		_currentTime += _deltaTime;
		_deltaTime = 0;
	}

	private void initialize() {
		if (_currentTime + _deltaTime >= delay) {
			initializeOverride();
			_isInitialized = true;
			_isIterationStep = true;
			_step = 0;
			_deltaTime -= delay - _currentTime;
			_currentTime = 0;
			callCallback(ActionMode.BEGIN);
			callCallback(ActionMode.START);
		}
	}

	private void checkRelaunch() {
		if (!_isIterationStep && repeatSize >= 0 && _step < 0 && _currentTime + _deltaTime >= 0) {
			_isIterationStep = true;
			_step = 0;
			float delta = 0 - _currentTime;
			_deltaTime -= delta;
			_currentTime = 0;
			callCallback(ActionMode.BEGIN);
			callCallback(ActionMode.START);
			update(_step, _step - 1, _isIterationStep, delta);

		} else if (!_isIterationStep && repeatSize >= 0 && _step > repeatSize * 2 && _currentTime + _deltaTime < 0) {
			_isIterationStep = true;
			_step = repeatSize * 2;
			float delta = 0 - _currentTime;
			_deltaTime -= delta;
			_currentTime = duration;
			callCallback(ActionMode.BACK_BEGIN);
			callCallback(ActionMode.BACK_START);
			update(_step, _step + 1, _isIterationStep, delta);
		}
	}

	private void updateStep() {
		for (; isValid(_step);) {
			if (!_isIterationStep && _currentTime + _deltaTime <= 0) {
				_isIterationStep = true;
				_step -= 1;
				float delta = -_currentTime;
				_deltaTime -= delta;
				_currentTime = duration;
				if (isReverse(_step)) {
					forceStartValues();
				} else {
					forceEndValues();
				}
				callCallback(ActionMode.BACK_START);
				update(_step, _step + 1, _isIterationStep, delta);

			} else if (!_isIterationStep && _currentTime + _deltaTime >= _repeatDelay) {
				_isIterationStep = true;
				_step += 1;

				float delta = _repeatDelay - _currentTime;
				_deltaTime -= delta;
				_currentTime = 0;

				if (isReverse(_step)) {
					forceEndValues();
				} else {
					forceStartValues();
				}

				callCallback(ActionMode.START);
				update(_step, _step - 1, _isIterationStep, delta);

			} else if (_isIterationStep && _currentTime + _deltaTime < 0) {
				_isIterationStep = false;
				_step -= 1;
				float delta = -_currentTime;
				_deltaTime -= delta;
				_currentTime = 0;
				update(_step, _step + 1, _isIterationStep, delta);
				callCallback(ActionMode.BACK_END);
				if (_step < 0 && repeatSize >= 0) {
					callCallback(ActionMode.BACK_COMPLETE);
				} else {
					_currentTime = _repeatDelay;
				}
			} else if (_isIterationStep && _currentTime + _deltaTime > duration) {
				_isIterationStep = false;
				_step += 1;
				float delta = duration - _currentTime;
				_deltaTime -= delta;
				_currentTime = duration;
				update(_step, _step - 1, _isIterationStep, delta);
				callCallback(ActionMode.END);
				if (_step > repeatSize * 2 && repeatSize >= 0) {
					callCallback(ActionMode.COMPLETE);
				}
				_currentTime = 0;
			} else if (_isIterationStep) {
				float delta = _deltaTime;
				_deltaTime -= delta;
				_currentTime += delta;
				update(_step, _step, _isIterationStep, delta);
				break;
			} else {
				float delta = _deltaTime;
				_deltaTime -= delta;
				_currentTime += delta;
				break;
			}
		}
	}

}
