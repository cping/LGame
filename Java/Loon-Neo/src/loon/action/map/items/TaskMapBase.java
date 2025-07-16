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
package loon.action.map.items;

import loon.events.EventActionT;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

public class TaskMapBase extends RealtimeProcess {

	private TaskType _taskType;

	private float _costTime;

	private EventActionT<TaskMapBase> _taskEvent;

	private boolean _looping;

	public TaskMapBase(TaskType taskType, float costTimeSecond) {
		this(taskType, costTimeSecond, false);
	}

	public TaskMapBase(TaskType taskType, float costTimeSecond, boolean loop) {
		this(taskType, costTimeSecond, loop, null);
	}

	public TaskMapBase(TaskType taskType, float costTimeSecond, EventActionT<TaskMapBase> eve) {
		this(taskType, costTimeSecond, false, eve);
	}

	public TaskMapBase(TaskType taskType, float costTimeSecond, boolean loop, EventActionT<TaskMapBase> eve) {
		this._taskType = taskType;
		this._costTime = costTimeSecond;
		this._looping = loop;
		this._taskEvent = eve;
		this.setProcessType(GameProcessType.LimitedTimeTask);
		this.setDelayS(costTimeSecond);
	}

	@Override
	public void run(LTimerContext time) {
		if (_taskEvent != null) {
			_taskEvent.update(this);
		}
		if (!_looping) {
			kill();
		}
	}

	public boolean isLooping() {
		return this._looping;
	}

	public TaskType getTaskType() {
		return this._taskType;
	}

	public float getCostTime() {
		return this._costTime;
	}

	public boolean isCompleted() {
		return this.isDead();
	}

}
