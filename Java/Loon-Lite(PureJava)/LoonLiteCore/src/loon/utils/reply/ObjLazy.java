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

public class ObjLazy<T> implements ObjT<T> {

	public static <T> ObjLazy<T> empty() {
		return new ObjLazy<T>(new ObjRef<T>(null));
	}

	public static <T> ObjLazy<T> of(final T value) {
		return new ObjLazy<T>(new ObjRef<T>(value));
	}

	public static <T> ObjLazy<T> of(ObjT<T> value) {
		return new ObjLazy<T>(value);
	}

	private ObjT<T> _obj;

	private T _value;

	public ObjLazy(final ObjT<T> o) {
		this._obj = o;
	}

	public ObjLazy<T> set(final ObjT<T> o) {
		this._obj = o;
		this._value = null;
		return this;
	}

	public boolean isPresent() {
		return _value != null;
	}

	public boolean isEmpty() {
		return _value == null;
	}

	public ObjT<T> refObj() {
		return _obj;
	}

	@Override
	public T get() {
		_value = _obj.get();
		if (_value == null) {
			throw new LSysException("No value present");
		}
		return _value;
	}

}
