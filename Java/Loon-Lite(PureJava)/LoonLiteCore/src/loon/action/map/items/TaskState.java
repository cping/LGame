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

import loon.LSystem;
import loon.utils.StrMap;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class TaskState {

	private final int _id;

	private final String _name;

	private String _description;

	private boolean _completed;

	private TaskType _taskType;

	private StrMap _properties;

	private TArray<TaskBind> _frontBinds;

	public TaskState(int id, TaskType task, String name) {
		this._properties = new StrMap();
		this._taskType = task;
		this._name = name;
		this._id = id;
	}

	public TArray<TaskBind> getFronts() {
		return _frontBinds;
	}

	public TaskState bind(TaskState state) {
		if (state == null) {
			return this;
		}
		return bind(state._id);
	}

	public TaskState bind(int id) {
		if (_frontBinds == null) {
			_frontBinds = new TArray<TaskBind>();
		}
		for (TaskBind bind : _frontBinds) {
			if (bind != null) {
				if (bind.getDestinationId() == id) {
					return this;
				}
			}
		}
		_frontBinds.add(new TaskBind(this._id, id));
		return this;
	}

	public int getID() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getDescription() {
		return _description;
	}

	public TaskState setDescription(String d) {
		this._description = d;
		return this;
	}

	public TaskState setPropertyValue(String key, String v) {
		_properties.put(key, v);
		return this;
	}

	public boolean getPropertyBooleanValue(String key) {
		return _properties.getBool(key);
	}

	public float getPropertyFloatValue(String key) {
		return _properties.getFloat(key);
	}

	public int getPropertyIntValue(String key) {
		return _properties.getInt(key);
	}

	public String getPropertyValue(String key) {
		String v = _properties.get(key);
		if (StringUtils.isEmpty(v)) {
			return LSystem.EMPTY;
		}
		return v;
	}

	public TaskType getTaskType() {
		return _taskType;
	}

	public TaskState setTaskType(TaskType taskType) {
		this._taskType = taskType;
		return this;
	}

	public TaskState setComplete(boolean c) {
		this._completed = c;
		return this;
	}

	public boolean isCompleted() {
		return this._completed;
	}

	@Override
	public String toString() {
		final StringKeyValue kv = new StringKeyValue(_name);
		kv.addValue(_name).comma().addValue(_taskType.toString()).comma().addValue(_completed);
		return kv.toString();
	}

}
