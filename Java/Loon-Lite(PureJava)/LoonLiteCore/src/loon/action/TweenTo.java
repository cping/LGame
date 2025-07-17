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

import loon.LRelease;
import loon.utils.StringKeyValue;

/**
 * 缓动事件,多个缓动事件组合嵌套
 * 
 * @param <T>
 */
public class TweenTo<T> extends ActionEvent {

	private ActionTweenBase<T> _base;

	private LRelease _dispose;

	public TweenTo(ActionTweenBase<T> b) {
		this._base = b;
	}

	public ActionTweenBase<T> get() {
		return _base;
	}

	@Override
	public TweenTo<T> reset() {
		super.reset();
		_dispose = null;
		return this;
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void update(long elapsedTime) {
		if (_isCompleted) {
			return;
		}
		_base.update(elapsedTime);
		if (_base.isFinished()) {
			_isCompleted = _base.actionEventOver();
			if (_isCompleted && _dispose != null) {
				_dispose.close();
			}
		}
	}

	public TweenTo<T> dispose(LRelease dispose) {
		this._dispose = dispose;
		return this;
	}

	@Override
	public ActionEvent cpy() {
		TweenTo<T> result = new TweenTo<T>(_base);
		result.dispose(_dispose);
		result.set(this);
		return result;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "tween";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		if (_base != null) {
			builder.kv("ActionTweenBase", _base);
		}
		return builder.toString();
	}

}
