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
package loon.utils;

import loon.LSystem;

public class IntStack {

	private int[] _buffer;

	private int _offset;

	public IntStack() {
		this(LSystem.DEFAULT_MAX_CACHE_SIZE);
	}

	public IntStack(int capacity) {
		this._buffer = new int[MathUtils.max(1, capacity)];
	}

	public int pop() {
		if (_offset <= 0) {
			return -1;
		}
		return this._buffer[--_offset];
	}

	public int getLength() {
		return this._offset;
	}

	private void ensure(int count) {
		int requiredSize = _offset + count;
		if (requiredSize > _buffer.length) {
			this._buffer = CollectionUtils.copyOf(this._buffer, Math.max(requiredSize, _buffer.length * 2));
		}
	}

	public IntStack push(int value) {
		this.ensure(1);
		this._buffer[_offset++] = value;
		return this;
	}

}
