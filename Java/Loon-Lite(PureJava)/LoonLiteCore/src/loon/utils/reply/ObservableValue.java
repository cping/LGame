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
package loon.utils.reply;

import loon.LSysException;
import loon.geom.IV;
import loon.geom.SetIV;

public class ObservableValue<T> implements SetIV<T>, IV<T> {

	public final static <T> ObservableValue<T> at(TChange<T> change, IV<T> v, T obj) {
		return new ObservableValue<T>(change, v, obj);
	}

	private TChange<T> _change;

	private IV<T> _value;

	private SetIV<T> _tempValue;

	private T _obj;

	public ObservableValue(TChange<T> c, T v) {
		this(c, new TValue<T>(v), null);
	}

	public ObservableValue(TChange<T> c, IV<T> v) {
		this(c, v, null);
	}

	public ObservableValue(TChange<T> c, IV<T> v, T obj) {
		if (v == null) {
			throw new LSysException("The Value Object cannot be null !");
		}
		this._change = c;
		this._value = v;
		this._obj = obj;
	}

	private boolean checkUpdate() {
		return _value != null;
	}

	@SuppressWarnings("unchecked")
	private SetIV<T> location() {
		if (_value instanceof SetIV) {
			return ((SetIV<T>) _value);
		}
		if (_tempValue == null) {
			_tempValue = new TValue<T>(_obj);
		}
		return _tempValue;
	}

	@Override
	public void set(T v) {
		if (checkUpdate() && !_value.get().equals(v)) {
			location().set(v);
			if (_change != null) {
				_change.onUpdate(_obj);
			}
		}
	}

	public T getObj() {
		return this._obj;
	}

	@Override
	public T get() {
		return this._obj;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		IV<T> v = ((IV<T>) o);
		return v.get() == this._value || v.get().equals(this._value);
	}

	@Override
	public final String toString() {
		return "(" + _value.getClass() + " = " + _value.get() + ")";
	}
}
