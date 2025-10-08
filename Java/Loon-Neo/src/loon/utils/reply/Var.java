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

public class Var<T> extends AbstractValue<T> {

	protected T _value;

	public static <T> Var<T> create(final T value) {
		return new Var<T>(value);
	}

	public Var(final T value) {
		_value = value;
	}

	public T update(final T value) {
		return updateAndNotifyIf(value);
	}

	public T updateForce(final T value) {
		return updateAndNotify(value);
	}

	public Port<T> port() {
		return new Port<T>() {
			@Override
			public void onEmit(final T value) {
				update(value);
			}
		};
	}

	@Override
	public T get() {
		return _value;
	}

	@Override
	protected T updateLocal(final T value) {
		T oldValue = _value;
		_value = value;
		return oldValue;
	}

}
