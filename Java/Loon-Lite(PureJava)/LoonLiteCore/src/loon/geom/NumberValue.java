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
package loon.geom;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.reply.TValue;

public class NumberValue extends TValue<Number> {

	public NumberValue(Number v) {
		super(v);
	}

	public boolean update(Number v) {
		set(v);
		return v == null ? false : (v.floatValue() != 0);
	}

	public float scaled(float length) {
		return _value == null ? 0 : (_value.floatValue() * length);
	}

	public int scaledCeil(float length) {
		return MathUtils.iceil(scaled(length));
	}

	public int scaledFloor(float length) {
		return MathUtils.ifloor(scaled(length));
	}

	public boolean isZero() {
		return MathUtils.equal(_value.doubleValue(), 0f);
	}

	@Override
	public int hashCode() {
		int result = 59;
		result = LSystem.unite(result, _value);
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof NumberValue) {
			NumberValue num = (NumberValue) o;
			if (num != null && (num._value).equals(_value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NumberValue cpy() {
		return new NumberValue(_value);
	}

	@Override
	public void close() {
		super.close();
		this._value = Float.valueOf(0f);
	}
}
