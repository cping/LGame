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

import loon.utils.IntStack;

public class IntPool {

	public final static IntPool create() {
		return new IntPool();
	}

	private IntStack _stack = new IntStack();

	private int _lastId = 0;

	public int alloc() {
		if (_stack.getLength() == 0) {
			_stack.push(_lastId++);
		}
		return _stack.pop();
	}

	public IntPool free(int value) {
		_stack.push(value);
		return this;
	}
}