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

import loon.events.QueryEvent;
import loon.utils.TArray;

public class TaskTeam {

	protected class CleanQuery implements QueryEvent<TaskState> {

		@Override
		public boolean hit(TaskState c) {
			return c == null || c.isCompleted();
		}

	}

	private TArray<TaskState> _tasks;

	public TaskTeam() {
		this._tasks = new TArray<TaskState>();
	}

	public TArray<TaskState> list() {
		return this._tasks;
	}

	public TaskTeam addTask(TaskState task) {
		_tasks.add(task);
		return this;
	}

	public TaskTeam removeTask(TaskState task) {
		_tasks.remove(task);
		return this;
	}

	public TArray<TaskState> where(final QueryEvent<TaskState> query) {
		return _tasks.save(query);
	}
	
	public TArray<TaskState> cleanOver() {
		return _tasks.clean(new CleanQuery());
	}
	
	public boolean isAllTasksCompleted() {
		for (TaskState task : _tasks) {
			if (!task.isCompleted()) {
				return false;
			}
		}
		return true;
	}

	public TaskTeam clean() {
		_tasks.clear();
		return this;
	}
}
