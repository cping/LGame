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

public abstract class ActionCommand extends AbstractCommand {

	@Override
	public boolean completed() {
		switch (_taskStatus) {
		case Invalid:
			onInvalid();
			break;
		case Success:
			onSuccess();
			break;
		case Running:
			onRunning();
			break;
		default:
		case Failure:
			onFailure();
			break;
		}
		return _taskStatus == TaskStatus.Success || _taskStatus == TaskStatus.Failure;
	}

	@Override
	public void execute() {
		try {
			_taskStatus = run();
		} catch (Exception e) {
			_taskStatus = TaskStatus.Failure;
		}
		if (_taskStatus == null) {
			_taskStatus = TaskStatus.Invalid;
		}
	}

	public abstract TaskStatus run();

	public void onInvalid() {

	}

	public void onSuccess() {

	}

	public void onRunning() {

	}

	public void onFailure() {

	}

}
