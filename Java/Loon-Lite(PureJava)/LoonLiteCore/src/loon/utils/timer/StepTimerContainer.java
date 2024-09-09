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

public class StepTimerContainer {

	private StepTimer _step;

	private float _timer;

	private float _maxTimer;

	public StepTimerContainer(StepTimer step, float time) {
		this._timer = time;
		this._maxTimer = time;
		this._step = step;
	}

	public boolean update(float dt) {
		if (_step != null && (this._timer > 0f)) {
			boolean result = _step.step(dt, this._timer / this._maxTimer);
			this._timer -= dt;
			return result;
		}
		return true;
	}

	public boolean contains(StepTimer step) {
		return step == this._step;
	}
}