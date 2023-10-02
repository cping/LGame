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
package loon.utils.processes.state;

import loon.Counter;

public class State<T> extends StateBase {

	public static <T> State<T> create(Counter counter, String name, T obj) {
		return new State<T>(counter, name, obj);
	}

	public static <T> State<T> create(Counter counter, String name) {
		return new State<T>(counter, name, null);
	}

	public static <T> State<T> create(Counter counter) {
		return new State<T>(counter, null, null);
	}

	public static <T> State<T> create() {
		return new State<T>();
	}

	private Counter _counter;

	private T _tagret;

	private int _currentNodeId;

	public State() {
		this(null, null, null);
	}

	private State(Counter counter, String name, T obj) {
		super(name);
		this._counter = counter;
		if (this._counter != null) {
			this._currentNodeId = this._counter.increment();
		}
		this._tagret = obj;
	}

	@SuppressWarnings("unchecked")
	public T getTagret() {
		if (this._tagret == null && this.getParent() != null && this.getParent() instanceof State) {
			return (T) ((State<T>) this.getParent()).getTagret();
		}
		return this._tagret;
	}

	public int getId() {
		return _currentNodeId;
	}

}
