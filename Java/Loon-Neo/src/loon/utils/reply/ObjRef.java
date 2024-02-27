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
package loon.utils.reply;

import loon.LSystem;
import loon.geom.SetIV;
import loon.utils.StringUtils;

/**
 * 模拟C#中ref以返回注入对象的修改结果，也就是引用传递
 * 
 * @param <T>
 */
public class ObjRef<T> extends Nullable<T> implements ObjT<T>, SetIV<T> {

	public static <T> void swap(ObjRef<T> ref1, ObjRef<T> ref2) {
		T t3 = ref1.get();
		ref1.set(ref2.get());
		ref2.set(t3);
	}

	public static <T> ObjRef<T> empty() {
		return of(null);
	}

	public static final <T> ObjRef<T> of(T v) {
		return getValue(v);
	}

	public static final <T> ObjRef<T> getValue(T v) {
		return new ObjRef<T>(v);
	}

	private Callback<T> _closed;

	public ObjRef(T refarg) {
		super(refarg);
	}

	public boolean hasValue() {
		return _value != null;
	}

	public Callback<T> getClosed() {
		return _closed;
	}

	public void setClosed(Callback<T> closed) {
		this._closed = closed;
	}

	@Override
	public void set(T v) {
		this._value = v;
	}

	@Override
	public String toString() {
		return StringUtils.toString(_value, LSystem.NULL);
	}

	@Override
	public void close() {
		if (_value != null && _closed != null) {
			_closed.onSuccess(_value);
		}
	}

}
