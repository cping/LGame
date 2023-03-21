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

public class TaskBind {

	private int _srcId;

	private int _dstId;

	public TaskBind(int sid, int did) {
		this._srcId = sid;
		this._dstId = did;
	}

	public int getSourceId() {
		return _srcId;
	}

	public TaskBind setSourceId(int id) {
		this._srcId = id;
		return this;
	}

	public int getDestinationId() {
		return _dstId;
	}

	public TaskBind setDestinationId(int id) {
		this._dstId = id;
		return this;
	}
}
