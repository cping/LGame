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
package loon.utils.timer;

import loon.LRelease;
import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 计步器集合用类，一个基于经过时间或帧数的持续循环的事件管理与触发用类
 */
public class StepList implements LRelease {

	private final TArray<StepBase> _stepBases;
	private final TArray<StepTimerContainer> _stepTimers;
	private final TArray<StepFrameContainer> _stepFrames;

	public StepList() {
		this._stepBases = new TArray<StepBase>(6);
		this._stepTimers = new TArray<StepTimerContainer>(6);
		this._stepFrames = new TArray<StepFrameContainer>(6);
	}

	public StepList add(StepBase step) {
		if (step == null) {
			return this;
		}
		_stepBases.add(step);
		return this;
	}

	public StepList add(StepTimer step, float timer) {
		if (step == null || timer <= 0f) {
			return this;
		}
		_stepTimers.add(new StepTimerContainer(step, timer));
		return this;
	}

	public StepList add(StepFrame step, int maxFrames) {
		if (step == null || maxFrames <= 0) {
			return this;
		}
		_stepFrames.add(new StepFrameContainer(step, maxFrames));
		return this;
	}

	public StepList remove(StepBase step) {
		if (step == null || _stepBases.size <= 0) {
			return this;
		}
		_stepBases.remove(step);
		return this;
	}

	public StepList remove(StepTimer step) {
		if (step == null || _stepTimers.size <= 0) {
			return this;
		}
		for (int i = _stepTimers.size - 1; i > -1; i--) {
			if (_stepTimers.get(i).contains(step)) {
				_stepTimers.removeIndex(i);
				return this;
			}
		}
		return this;
	}

	public StepList remove(StepFrame step) {
		if (step == null || _stepFrames.size <= 0) {
			return this;
		}
		for (int i = _stepFrames.size - 1; i > -1; i--) {
			if (_stepFrames.get(i).contains(step)) {
				_stepFrames.removeIndex(i);
				return this;
			}
		}
		return this;
	}

	public StepList clearStepBases() {
		_stepBases.clear();
		return this;
	}

	public StepList clearStepTimers() {
		_stepTimers.clear();
		return this;
	}

	public StepList clearStepFrames() {
		_stepFrames.clear();
		return this;
	}

	public StepList clear() {
		_stepBases.clear();
		_stepTimers.clear();
		_stepFrames.clear();
		return this;
	}

	public int getCount() {
		return _stepBases.size + _stepTimers.size + _stepFrames.size;
	}

	public void update(LTimerContext timer) {
		if (timer != null) {
			update(timer.dt());
		}
	}

	public void update(long elapsedTime) {
		update(MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED));
	}

	public void update(float dt) {
		for (int i = _stepBases.size - 1; i > -1; i--) {
			// 返回true完成并删除，返回false不断循环，以下同
			if (_stepBases.get(i).step(dt)) {
				_stepBases.removeIndex(i);
			}
		}
		for (int i = _stepTimers.size - 1; i > -1; i--) {
			if (_stepTimers.get(i).update(dt)) {
				_stepTimers.removeIndex(i);
			}
		}
		for (int i = _stepFrames.size - 1; i > -1; i--) {
			if (_stepFrames.get(i).update(dt)) {
				_stepFrames.removeIndex(i);
			}
		}
	}

	@Override
	public void close() {
		clear();
	}
}
