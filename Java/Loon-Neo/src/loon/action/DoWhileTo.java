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

import loon.geom.BooleanValue;
import loon.utils.StringKeyValue;

/**
 * 循环用缓动类,当BooleanValue不成立前,会一直循环ActionCondition的update函数
 * 
 * example:
 * 
 * Counter count = new Counter(); e.selfAction().sizeTo(80, 80).doWhile(ref -> {
 * if (ref.update(count.getValue() <= 100)) { label.setText("testing:" +
 * count.getValue()); count.increment(); } }).sizeTo(32, 32).start();
 */
public class DoWhileTo extends ActionEvent {

	private final ActionCondition _actionCondition;

	private final BooleanValue _refValue;

	public DoWhileTo(ActionCondition condition) {
		this._refValue = new BooleanValue(true);
		this._actionCondition = condition;
	}

	@Override
	public void update(long elapsedTime) {
		if (_actionCondition != null) {
			_actionCondition.update(_refValue);
			if (!_refValue.get()) {
				_isCompleted = true;
			}
		}
	}

	public BooleanValue result() {
		return _refValue;
	}

	@Override
	public DoWhileTo reset() {
		super.reset();
		_refValue.set(true);
		return this;
	}

	@Override
	public void onLoad() {
		_refValue.set(true);
	}

	@Override
	public ActionEvent cpy() {
		DoWhileTo result = new DoWhileTo(_actionCondition);
		result.set(this);
		return this;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "while";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		if (_refValue != null) {
			builder.kv("Boolean", _refValue.result());
		}
		return builder.toString();
	}
}
