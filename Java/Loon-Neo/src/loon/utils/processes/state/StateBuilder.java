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
import loon.events.EventActionT;
import loon.events.EventActionTN;
import loon.geom.BooleanValue;

public class StateBuilder<T extends StateBase, TParent> extends StateBase implements IStateBuilder<T, TParent> {

	private final TParent _parentBuilder;

	private final Counter _counter;

	private T _state;

	public StateBuilder(TParent parentBuilder, StateBase parentState, T state) {
		this._parentBuilder = parentBuilder;
		this._counter = new Counter();
		this._state = state;
		parentState.addChild(state);
	}

	public StateBuilder(TParent parentBuilder, StateBase parentState, String stateName, T state) {
		this._parentBuilder = parentBuilder;
		this._counter = new Counter();
		this._state = state;
		parentState.addChild(state, stateName);
	}

	@Override
	public IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(String stateName) {
		return new StateBuilder<State<T>, IStateBuilder<T, TParent>>(this, this._state, stateName,
				State.create(_counter, stateName));
	}

	@Override
	public IStateBuilder<State<T>, IStateBuilder<T, TParent>> state() {
		return new StateBuilder<State<T>, IStateBuilder<T, TParent>>(this, this._state, State.create(_counter));
	}

	@Override
	public IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(State<T> state) {
		return new StateBuilder<State<T>, IStateBuilder<T, TParent>>(this, this._state, state);
	}

	@Override
	public IStateBuilder<State<T>, IStateBuilder<T, TParent>> state(String stateName, State<T> state) {
		return new StateBuilder<State<T>, IStateBuilder<T, TParent>>(this, this._state, stateName, state);
	}

	@Override
	public IStateBuilder<T, TParent> enter(EventActionT<T> onEnter) {
		_state.setEnter(onEnter);
		return this;
	}

	@Override
	public IStateBuilder<T, TParent> exit(EventActionT<T> onExit) {
		_state.setExit(onExit);
		return this;
	}

	@Override
	public IStateBuilder<T, TParent> update(EventActionTN<T, Long> onUpdate) {
		_state.setUpdate(onUpdate);
		return this;
	}

	@Override
	public IStateBuilder<T, TParent> condition(EventActionTN<T, BooleanValue> action, boolean v) {
		_state.setCondition(new BooleanValue(v), action);
		return this;
	}

	@Override
	public IStateBuilder<T, TParent> condition(EventActionTN<T, BooleanValue> action) {
		return condition(action, true);
	}

	@Override
	public IStateBuilder<T, TParent> event(String identifier, EventActionT<T> action) {
		_state.addEvent(identifier, action);
		return this;
	}

	@Override
	public TParent end() {
		return _parentBuilder;
	}

	@Override
	public void close() {
		super.close();
		if (_state != null) {
			_state.close();
		}
	}
}
