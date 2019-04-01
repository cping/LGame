/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.event.Updateable;
import loon.utils.StringKeyValue;

/**
 * 缓动事件,单纯执行一次Updateable的action中内容
 */
public class UpdateTo extends ActionEvent {

	private Updateable updateable;

	public UpdateTo(Updateable u) {
		this.updateable = u;
	}

	@Override
	public void update(long elapsedTime) {
		if (updateable != null) {
			updateable.action(original);
		}
		this._isCompleted = true;
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
		UpdateTo update = new UpdateTo(updateable);
		update.set(this);
		return update;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		if (updateable != null) {
			builder.kv("Update", updateable);
		}
		return builder.toString();
	}
}
