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

public class ObjLazy<T> implements ObjT<T> {

	private final ObjT<T> _obj;

	private T _value;

	public ObjLazy(final ObjT<T> o) {
		this._obj = o;
	}
	
	public ObjLazy<T> reset(){
		_value = null;
		return this;
	}

	@Override
	public T get() {
		if (_value == null) {
			_value = _obj.get();
		}
		return _value;
	}

}
