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
package loon.utils.cache;

import loon.events.Created;

public class DefaultPool<T> extends Pool<T> {

	private Created<T> _created;

	public DefaultPool() {
		this(null);
	}

	public DefaultPool(Created<T> c) {
		setCreateMethod(c);
	}

	public DefaultPool<T> setCreateMethod(final Created<T> c) {
		_created = c;
		return this;
	}

	public Created<T> getCreateMethod() {
		return _created;
	}

	@Override
	protected T newObject() {
		if (_created == null) {
			return null;
		}
		return _created.make();
	}

	@Override
	protected T filterObtain(T o) {
		return o;
	}

	@Override
	public boolean isLimit(T src, T dst) {
		return false;
	}

}
