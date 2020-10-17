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
package loon.action;

import loon.events.ActionUpdate;
import loon.geom.BooleanValue;
import loon.utils.StringKeyValue;

/**
 * 缓动事件,暂停一系列缓动动画,等待ActionUpdate的completed返回true或BooleanValue的result为true才会继续
 */
public class WaitTo extends ActionEvent {

	private ActionUpdate actionUpdate;

	private BooleanValue boolValue;

	public WaitTo(ActionUpdate au) {
		this.actionUpdate = au;
	}

	public WaitTo(BooleanValue bv) {
		this.boolValue = bv;
	}

	@Override
	public void update(long elapsedTime) {
		if (boolValue != null && boolValue.result()) {
			this._isCompleted = true;
		} else if (actionUpdate != null) {
			actionUpdate.action(original);
			if (actionUpdate.completed()) {
				this._isCompleted = true;
			}
		}
	}

	public BooleanValue waiting() {
		return boolValue;
	}
	
	@Override
	public void onLoad() {

	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		WaitTo waitEvent = null;
		if (actionUpdate != null) {
			waitEvent = new WaitTo(actionUpdate);
		} else if (boolValue != null) {
			waitEvent = new WaitTo(boolValue);
		}
		waitEvent.set(this);
		return waitEvent;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "wait";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		if (actionUpdate != null) {
			builder.kv("ActionUpdate", actionUpdate.completed());
		}
		if (boolValue != null) {
			builder.kv("Boolean", boolValue.result());
		}
		return builder.toString();
	}

}
