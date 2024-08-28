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

import loon.LRelease;
import loon.LSystem;
import loon.utils.ArrayByte;
import loon.utils.reply.Nullable;

public class BytesValue implements LRelease {

	private ArrayByte _value;

	public BytesValue() {
		this(new ArrayByte(512));
	}

	public BytesValue(ArrayByte v) {
		this.set(v);
	}

	public boolean update(ArrayByte v) {
		set(v);
		return v != null;
	}

	public BytesValue set(ArrayByte v) {
		this._value = v;
		return this;
	}

	public ArrayByte result() {
		return _value;
	}

	public boolean isZero() {
		return _value == null;
	}

	@Override
	public int hashCode() {
		int result = 59;
		result = LSystem.unite(result, _value.hashCode());
		result = LSystem.unite(result, super.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof BytesValue) {
			if (((BytesValue) o)._value.equals(this._value)) {
				return true;
			}
		}
		return false;
	}

	public ArrayByte cpy() {
		return _value != null ? _value.cpy() : null;
	}

	public Nullable<ArrayByte> toNullable() {
		return new Nullable<ArrayByte>(_value);
	}

	@Override
	public String toString() {
		return _value.toString();
	}

	@Override
	public void close() {
		_value = null;
	}
}
