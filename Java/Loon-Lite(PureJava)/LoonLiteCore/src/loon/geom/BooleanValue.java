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
package loon.geom;

import loon.LRelease;
import loon.LSystem;
import loon.utils.reply.Nullable;

public class BooleanValue implements LRelease {

	private boolean _value = false;

	public BooleanValue() {
		this(false);
	}

	public BooleanValue(boolean v) {
		this.set(v);
	}

	public boolean update(boolean v) {
		set(v);
		return v;
	}

	public BooleanValue set(boolean res) {
		this._value = res;
		return this;
	}

	public BooleanValue start() {
		set(false);
		return this;
	}

	public BooleanValue stop() {
		set(true);
		return this;
	}

	public boolean neg() {
		return !_value;
	}

	public boolean get() {
		return _value;
	}

	public boolean result() {
		return _value;
	}

	public boolean isZero() {
		return !_value;
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
		if (o instanceof BooleanValue) {
			if (((BooleanValue) o)._value == this._value) {
				return true;
			}
		}
		return false;
	}

	public BooleanValue cpy() {
		return new BooleanValue(_value);
	}

	public Nullable<Boolean> toNullable() {
		return new Nullable<Boolean>(_value);
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	@Override
	public void close() {
		this._value = false;
	}
}
