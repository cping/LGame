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

public class Choice<T> {

	public static <T> Choice<T> of(final T a, final T b) {
		return new Choice<T>(a, b);
	}

	private boolean _toggled;

	private T _first;

	private T _second;

	public Choice(final T first, final T second) {
		this._first = first;
		this._second = second;
	}

	public Choice<T> setActive(final T v) {
		if (_toggled) {
			_second = v;
		} else {
			_first = v;
		}
		return this;
	}

	public T getSelected() {
		return _toggled ? _second : _first;
	}

	public T getNotSelected() {
		return _toggled ? _first : _second;
	}

	public boolean toggle() {
		return (_toggled = !_toggled);
	}

}
