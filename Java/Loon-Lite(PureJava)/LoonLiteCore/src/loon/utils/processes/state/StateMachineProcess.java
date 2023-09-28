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

import loon.utils.processes.RealtimeProcess;
import loon.utils.timer.LTimerContext;

/**
 * 状态机进程用类,用于将对应状态机注入游戏系统
 */
public class StateMachineProcess<T> extends RealtimeProcess {

	private final StateMachineBuilder<T> _stateMachineBuilder;

	private State<T> _state;

	public StateMachineProcess(T obj) {
		this(obj, 0);
	}

	public StateMachineProcess(T obj, long delay) {
		super(delay);
		_stateMachineBuilder = StateMachineBuilder.build(obj);
	}

	public StateMachineBuilder<T> create() {
		return _stateMachineBuilder;
	}

	public StateMachineProcess<T> create(String stateName, StateCreated<T> created) {
		IStateBuilder<State<T>, StateMachineBuilder<T>> build = _stateMachineBuilder.state(stateName);
		if (created != null) {
			created.run(build);
			_state = build.end().create();
		}
		return this;
	}

	public StateMachineProcess<T> create(String stateName, State<T> state, StateCreated<T> created) {
		IStateBuilder<State<T>, StateMachineBuilder<T>> build = _stateMachineBuilder.state(stateName, state);
		if (created != null) {
			_state = build.end().create();
		}
		return this;
	}

	public StateMachineProcess<T> create(State<T> state, StateCreated<T> created) {
		IStateBuilder<State<T>, StateMachineBuilder<T>> build = _stateMachineBuilder.state(state);
		if (created != null) {
			_state = build.end().create();
		}
		return this;
	}

	public State<T> getState() {
		return _state;
	}

	public IState play(String stateName) {
		if (_state != null) {
			return _state.changeState(stateName);
		}
		return (IState) this;
	}

	@Override
	public void run(LTimerContext time) {
		if (_state != null) {
			_state.update(time.timeSinceLastUpdate);
		}
	}

	@Override
	public void close() {
		super.close();
		if (_state != null) {
			_state.close();
		}
	}

}
