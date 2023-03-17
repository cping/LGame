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

import loon.utils.StringKeyValue;

/**
 * 单纯删除指定动作对象的所有缓动动画事件
 */
public class RemoveActionsTo extends ActionEvent {

	private ActionBind _removeBind;

	public RemoveActionsTo() {
		this(null);
	}

	public RemoveActionsTo(ActionBind bind) {
		this._removeBind = bind;
	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		if (_removeBind != null) {
			ActionControl.get().removeAllActions(_removeBind);
		}
		this._isCompleted = true;
	}

	public ActionBind getRemoveBind() {
		return _removeBind;
	}

	@Override
	public void onLoad() {
		if (_removeBind == null) {
			_removeBind = original;
		}
	}

	@Override
	public boolean isComplete() {
		return _isCompleted;
	}

	@Override
	public ActionEvent cpy() {
		RemoveActionsTo update = new RemoveActionsTo(_removeBind);
		update.set(this);
		return update;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("removeBind", _removeBind);
		return builder.toString();
	}
}
