/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
import loon.utils.reply.TValue;

public class StrValue extends TValue<String> {

	public StrValue() {
		this(LSystem.EMPTY);
	}

	public StrValue(String v) {
		super(v);
	}

	public boolean update(String v) {
		set(v);
		return v != null;
	}

	public boolean isZero() {
		return _value == null || LSystem.UNKNOWN.equals(_value) || LSystem.EMPTY.equals(_value);
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
		if (o instanceof StrValue) {
			StrValue str = (StrValue) o;
			if (str != null && str._value.equals(_value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	@Override
	public void close() {
		super.close();
		this._value = LSystem.EMPTY;
	}

}
