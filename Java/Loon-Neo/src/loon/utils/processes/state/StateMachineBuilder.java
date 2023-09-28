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

public class StateMachineBuilder<T> {

	private final State<T> _rootState;

	private final Counter _counter;

	public final static <T> StateMachineBuilder<T> build() {
		return build(null);
	}

	public final static <T> StateMachineBuilder<T> build(T obj) {
		return new StateMachineBuilder<T>(obj);
	}

	private StateMachineBuilder(T obj) {
		_counter = new Counter();
		_rootState = State.create(_counter, "Root", obj);
	}

	public IStateBuilder<State<T>, StateMachineBuilder<T>> state(String stateName) {
		return new StateBuilder<State<T>, StateMachineBuilder<T>>(this, _rootState, stateName,
				State.create(_counter, stateName, _rootState.getTagret()));
	}

	public IStateBuilder<State<T>, StateMachineBuilder<T>> state(String stateName, State<T> state) {
		return new StateBuilder<State<T>, StateMachineBuilder<T>>(this, _rootState, stateName, state);
	}

	public IStateBuilder<State<T>, StateMachineBuilder<T>> state(State<T> state) {
		return new StateBuilder<State<T>, StateMachineBuilder<T>>(this, _rootState, state.getName(), state);
	}

	public State<T> create() {
		return _rootState;
	}
}
