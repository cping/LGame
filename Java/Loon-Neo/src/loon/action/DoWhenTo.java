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

public class DoWhenTo extends ActionEvent {

	private final ActionCondition[] _conditions;

	private final BooleanValue _refValue;

	public DoWhenTo(ActionCondition... conds) {
		_conditions = conds;
		_refValue = new BooleanValue();
	}

	@Override
	public void update(long elapsedTime) {
		if (_conditions != null) {
			for (int i = 0; i < _conditions.length; i++) {
				ActionCondition cond = _conditions[i];
				if (cond != null) {
					cond.update(_refValue);
					if (_refValue.get()) {
						_isCompleted = true;
						return;
					}
				}
			}
		}
	}

	public BooleanValue result() {
		return _refValue;
	}

	@Override
	public DoWhenTo reset() {
		super.reset();
		_refValue.set(false);
		return this;
	}

	@Override
	public void onLoad() {
		_refValue.set(false);
	}

	@Override
	public ActionEvent cpy() {
		DoWhenTo result = new DoWhenTo(_conditions);
		result.set(this);
		return result;
	}

	@Override
	public ActionEvent reverse() {
		return cpy();
	}

	@Override
	public String getName() {
		return "when";
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue(getName());
		if (_refValue != null) {
			builder.kv("Boolean", _refValue.result());
		}
		return builder.toString();
	}
}
