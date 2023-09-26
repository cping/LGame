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

	private int step;
	private boolean isIterationStep;
	private boolean isBackward;

	protected int repeatSize;
	protected float delay;
	protected float duration;
	private float repeatDelay;
	private float currentTime;
	private float deltaTime;
	private boolean isStarted;
	private boolean isInitialized;
	private boolean isFinished;
	private boolean isKilled;
	private boolean isPaused;

	private ActionCallback callback;
	private int callbackTriggers;

	boolean _isAutoRemoveEnabled;
	boolean _isAutoStartEnabled;

	protected void reset() {
		step = -2;
		repeatSize = 0;
		isIterationStep = isBackward = false;
		delay = duration = repeatDelay = currentTime = deltaTime = 0;
		isStarted = isInitialized = isFinished = isKilled = isPaused = false;
		callback = null;
		callbackTriggers = ActionMode.COMPLETE;
		_isAutoRemoveEnabled = _isAutoStartEnabled = true;
	}

	public TweenTo<T> start() {
		build();
		currentTime = 0;
		isStarted = true;
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
		isKilled = true;
		return (T) this;
	}

	public void free() {
		_dispose = null;
	}

	public T pause() {
		isPaused = true;
		return (T) this;
	}

	public T resume() {
		isPaused = false;
		return (T) this;
	}

	public T repeat(int count, float delay) {
		if (isStarted) {
			return (T) this;
		}
		repeatSize = count;
		repeatDelay = delay >= 0 ? delay : 0;
		isBackward = false;
		return (T) this;
	}

	public T repeatBackward(int count, float delay) {
		if (isStarted) {
			return (T) this;
		}
		repeatSize = count;
		repeatDelay = delay >= 0 ? delay : 0;
		isBackward = true;
		return (T) this;
	}

	public T setCallback(ActionCallback callback) {
		this.callback = callback;
		return (T) this;
	}

	public T setCallbackTriggers(int flags) {
		this.callbackTriggers = flags;
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
		return repeatDelay;
	}

	public float getFullDuration() {
		if (repeatSize < 0) {
			return -1;
		}
		return delay + duration + (repeatDelay + duration) * repeatSize;
	}

	public int getStep() {
		return step;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public boolean isFinished() {
		return isFinished || isKilled;
	}

	public boolean isBackward() {
		return isBackward;
	}

	public boolean isPaused() {
		return isPaused;
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
		currentTime = -delay;
		step = -1;
		isIterationStep = false;
		if (isReverse(0)) {
			forceEndValues();
		} else {
			forceStartValues();
		}
	}

	protected void forceToEnd(float time) {
		currentTime = time - getFullDuration();
		step = repeatSize * 2 + 1;
		isIterationStep = false;
		if (isReverse(repeatSize * 2)) {
			forceStartValues();
		} else {
			forceEndValues();
		}
	}

	protected void callCallback(int type) {
		if (callback != null && (callbackTriggers & type) > 0)
			callback.onEvent(type, this);
	}

	protected boolean isReverse(int step) {
		return isBackward && MathUtils.abs(step % 4) == 2;
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
		isFinished = repeatSize >= 0 && (step > repeatSize * 2 || step < 0);
	}

	protected boolean actionEventOver() {
		return true;
	}

	public void update(float delta) {
		if (!isStarted || isPaused || isKilled) {
			return;
		}

		deltaTime = delta;

		if (!isInitialized) {
			initialize();
		}

		if (isInitialized) {
			checkRelaunch();
			updateStep();
			checkCompletion();
		}

		currentTime += deltaTime;
		deltaTime = 0;
	}

	private void initialize() {
		if (currentTime + deltaTime >= delay) {
			initializeOverride();
			isInitialized = true;
			isIterationStep = true;
			step = 0;
			deltaTime -= delay - currentTime;
			currentTime = 0;
			callCallback(ActionMode.BEGIN);
			callCallback(ActionMode.START);
		}
	}

	private void checkRelaunch() {
		if (!isIterationStep && repeatSize >= 0 && step < 0 && currentTime + deltaTime >= 0) {
			isIterationStep = true;
			step = 0;
			float delta = 0 - currentTime;
			deltaTime -= delta;
			currentTime = 0;
			callCallback(ActionMode.BEGIN);
			callCallback(ActionMode.START);
			update(step, step - 1, isIterationStep, delta);

		} else if (!isIterationStep && repeatSize >= 0 && step > repeatSize * 2 && currentTime + deltaTime < 0) {
			isIterationStep = true;
			step = repeatSize * 2;
			float delta = 0 - currentTime;
			deltaTime -= delta;
			currentTime = duration;
			callCallback(ActionMode.BACK_BEGIN);
			callCallback(ActionMode.BACK_START);
			update(step, step + 1, isIterationStep, delta);
		}
	}

	private void updateStep() {
		for (; isValid(step);) {
			if (!isIterationStep && currentTime + deltaTime <= 0) {
				isIterationStep = true;
				step -= 1;
				float delta = -currentTime;
				deltaTime -= delta;
				currentTime = duration;
				if (isReverse(step)) {
					forceStartValues();
				} else {
					forceEndValues();
				}
				callCallback(ActionMode.BACK_START);
				update(step, step + 1, isIterationStep, delta);

			} else if (!isIterationStep && currentTime + deltaTime >= repeatDelay) {
				isIterationStep = true;
				step += 1;

				float delta = repeatDelay - currentTime;
				deltaTime -= delta;
				currentTime = 0;

				if (isReverse(step)) {
					forceEndValues();
				} else {
					forceStartValues();
				}

				callCallback(ActionMode.START);
				update(step, step - 1, isIterationStep, delta);

			} else if (isIterationStep && currentTime + deltaTime < 0) {
				isIterationStep = false;
				step -= 1;
				float delta = -currentTime;
				deltaTime -= delta;
				currentTime = 0;
				update(step, step + 1, isIterationStep, delta);
				callCallback(ActionMode.BACK_END);
				if (step < 0 && repeatSize >= 0) {
					callCallback(ActionMode.BACK_COMPLETE);
				} else {
					currentTime = repeatDelay;
				}
			} else if (isIterationStep && currentTime + deltaTime > duration) {
				isIterationStep = false;
				step += 1;
				float delta = duration - currentTime;
				deltaTime -= delta;
				currentTime = duration;
				update(step, step - 1, isIterationStep, delta);
				callCallback(ActionMode.END);
				if (step > repeatSize * 2 && repeatSize >= 0) {
					callCallback(ActionMode.COMPLETE);
				}
				currentTime = 0;
			} else if (isIterationStep) {
				float delta = deltaTime;
				deltaTime -= delta;
				currentTime += delta;
				update(step, step, isIterationStep, delta);
				break;
			} else {
				float delta = deltaTime;
				deltaTime -= delta;
				currentTime += delta;
				break;
			}
		}
	}

}
