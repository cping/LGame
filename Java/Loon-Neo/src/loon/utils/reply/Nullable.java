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

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.events.EventActionT;
import loon.events.QueryEvent;
import loon.geom.IV;

public class Nullable<T> implements IV<T>, LRelease {

	protected T _value;

	public Nullable() {
		this(null);
	}

	public Nullable(T v) {
		this._value = v;
	}

	public boolean isPresent() {
		return this._value != null;
	}

	public boolean isEmpty() {
		return this._value == null;
	}

	public Nullable<T> reset() {
		this._value = null;
		return this;
	}

	public T orElse(T v) {
		if (this._value != null) {
			return this._value;
		}
		return v;
	}

	public boolean orElse(QueryEvent<T> q) {
		return q == null ? false : q.hit(_value);
	}

	public T orElseGet(IV<? extends T> v) {
		return _value != null ? _value : v.get();
	}

	public <X extends Throwable> T orElseThrow(IV<? extends X> ex) throws X {
		if (_value != null) {
			return _value;
		} else {
			if (ex != null) {
				throw ex.get();
			} else {
				throw new LSysException();
			}
		}
	}

	public boolean isPresent(EventActionT<T> q) {
		boolean result = this._value != null;
		if (result) {
			q.update(_value);
		}
		return result;
	}

	public GoFuture<T> success() {
		return GoFuture.success(_value);
	}

	public <X extends Throwable> GoFuture<T> failure(IV<? extends X> ex) {
		return failure(ex == null ? new LSysException() : ex.get());
	}

	public GoFuture<T> failure(Throwable cause) {
		return GoFuture.failure(cause);
	}

	@Override
	public String toString() {
		return String.valueOf(this._value);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = LSystem.unite(hash, _value);
		hash = LSystem.unite(hash, this);
		return hash;
	}

	@Override
	public boolean equals(Object v) {
		if (v == null) {
			return false;
		}
		if (v == this) {
			return true;
		}
		if (getClass() != v.getClass()) {
			return false;
		}
		if (v instanceof Nullable) {
			@SuppressWarnings("unchecked")
			Nullable<T> o = ((Nullable<T>) v);
			if (o._value != null && o._value.equals(_value)) {
				return true;
			}
			return o._value == _value;
		}
		return false;
	}

	@Override
	public T get() {
		if (_value == null) {
			throw new LSysException("No value present");
		}
		return _value;
	}

	@Override
	public void close() {
		if (_value != null) {
			if (_value instanceof LRelease) {
				((LRelease) _value).close();
			}
			_value = null;
		}
	}
}
