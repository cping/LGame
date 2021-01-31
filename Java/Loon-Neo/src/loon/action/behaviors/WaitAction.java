/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.behaviors;

import loon.LSystem;
import loon.utils.TimeUtils;

public class WaitAction<T> extends Behavior<T> {

	public float waitTime;

	protected float _startTime;

	public WaitAction() {
		this(0f);
	}

	public WaitAction(float time) {
		this.waitTime = time * LSystem.SECOND;
	}

	@Override
	public void onStart() {
		_startTime = 0f;
	}

	@Override
	public void onEnd() {
		_startTime = TimeUtils.currentMillis();
	}

	@Override
	public TaskStatus update(T context) {
		if (_startTime == 0) {
			_startTime = TimeUtils.currentMillis();
		}
		if (TimeUtils.currentMillis() - _startTime >= waitTime) {
			return TaskStatus.Success;
		}
		return TaskStatus.Running;
	}

}