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
package loon.action.behaviors;

public class AbortTypes {

	public final static AbortTypes None = new AbortTypes(0);

	public final static AbortTypes LowerPriority = new AbortTypes(1);

	public final static AbortTypes Self = new AbortTypes(2);

	public final static AbortTypes Both = new AbortTypes(1 | 2);

	protected final int _code;

	public AbortTypes(int code) {
		this._code = code;
	}

	public int getAbort() {
		return _code;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof AbortTypes) {
			AbortTypes types = (AbortTypes) o;
			return types._code == _code;
		}
		return false;
	}

	public boolean has(AbortTypes check) {
		return (this._code & check._code) == check._code;
	}
}
