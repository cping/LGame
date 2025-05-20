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

public class History<T> {
	
	private RingBuffer<T> _re;
	
	private RingBuffer<T> _un;

	public History(int len) {
		_re = new RingBuffer<T>(len);
		_un = new RingBuffer<T>(len);
	}

	public T redo() {
		T r = _re.pop();
		if (r != null) {
			_un.push(r);
		}
		return r;
	}

	public T undo() {
		T u = _un.pop();
		if (u != null) {
			_re.push(u);
		}
		return u;
	}

	public void add(T v) {
		_un.push(v);
		_re.reset();
	}
}
