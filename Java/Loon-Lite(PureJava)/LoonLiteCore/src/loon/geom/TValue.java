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
import loon.Nullable;

public class TValue<T> implements SetIV<T>, IV<T>, LRelease {

	private T _value;

	public TValue(T v) {
		this._value = v;
	}

	@Override
	public T get() {
		return _value;
	}

	@Override
	public void set(T v) {
		this._value = v;
	}

	public T result() {
		return _value;
	}

	public TValue<T> cpy() {
		return new TValue<T>(_value);
	}

	public Nullable<T> toNullable(){
		return new Nullable<T>(_value);
	}
	
	public ObservableValue<T> observable(XYChange<T> v) {
		return ObservableValue.at(v, this, _value);
	}

	@Override
	public String toString() {
		return String.valueOf(_value);
	}

	@Override
	public void close() {
		this._value = null;
	}

}
