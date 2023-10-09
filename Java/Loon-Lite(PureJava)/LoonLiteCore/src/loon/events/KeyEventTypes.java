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
package loon.events;

public class KeyEventTypes {

	public final static int KEY_NONE = 1;

	public final static int KEY_DOWN = 1;

	public final static int KEY_UP = 2;

	private int _keyType = -1;

	public KeyEventTypes(int t) {
		this._keyType = t;
	}

	public KeyEventTypes down(boolean down) {
		if (down) {
			_keyType = KEY_DOWN;
		} else {
			_keyType = KEY_UP;
		}
		return this;
	}

	public KeyEventTypes setTypeCode(int t) {
		this._keyType = t;
		return this;
	}

	public int getTypeCode() {
		return _keyType;
	}

}
