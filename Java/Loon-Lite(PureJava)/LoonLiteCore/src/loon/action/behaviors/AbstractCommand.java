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
package loon.action.behaviors;

import loon.events.ActionUpdate;

public abstract class AbstractCommand implements ActionUpdate {

	protected int _typeCode;

	protected TaskStatus _taskStatus;

	protected BehaviorAction _action;

	public AbstractCommand() {
		this._taskStatus = TaskStatus.Invalid;
	}

	public AbstractCommand setBehaviorAction(BehaviorAction a) {
		this._action = a;
		return this;
	}

	public BehaviorAction getBehaviorAction() {
		return this._action;
	}

	@Override
	public void action(Object a) {
		execute();
	}

	public abstract void execute();

	public int getTypeCode() {
		return _typeCode;
	}

	public AbstractCommand setTypeCode(int t) {
		this._typeCode = t;
		return this;
	}

	public TaskStatus getTaskStatus() {
		return _taskStatus;
	}

	public AbstractCommand setTaskStatus(TaskStatus t) {
		this._taskStatus = t;
		return this;
	}
}
