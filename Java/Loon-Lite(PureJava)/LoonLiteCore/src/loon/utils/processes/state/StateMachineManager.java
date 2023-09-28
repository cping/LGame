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

import loon.LRelease;
import loon.LSysException;
import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.processes.RealtimeProcessManager;

@SuppressWarnings("unchecked")
public class StateMachineManager implements LRelease {

	private final ArrayMap _stateProcess;

	public StateMachineManager() {
		_stateProcess = new ArrayMap();
	}

	public <T> StateMachineManager addStateMachine(String name, StateMachineProcess<T> process) {
		_stateProcess.put(name, process);
		return this;
	}

	public <T> StateMachineProcess<T> getStateMachine(String name) {
		StateMachineProcess<T> result = (StateMachineProcess<T>) _stateProcess.getValue(name);
		if (result == null) {
			throw new LSysException("The StateMachine with the specified name \"" + name + "\" does not exist .");
		}
		return result;
	}

	public <T> StateMachineProcess<T> removeStateMachine(String name) {
		return (StateMachineProcess<T>) _stateProcess.remove(name);
	}

	public <T> StateMachineProcess<T> removeStateMachine(StateMachineProcess<T> process) {
		for (int i = _stateProcess.size() - 1; i > -1; i--) {
			Entry entry = _stateProcess.getEntry(i);
			if (entry != null) {
				if (entry.getValue().equals(process)) {
					return (StateMachineProcess<T>) _stateProcess.remove(i);
				}
			}
		}
		return null;
	}

	public StateMachineManager build() {
		RealtimeProcessManager manager = RealtimeProcessManager.get();
		for (int i = 0; i < _stateProcess.size(); i++) {
			StateMachineProcess<?> state = (StateMachineProcess<?>) _stateProcess.get(i);
			if (state != null && !manager.containsProcess(state)) {
				manager.addProcess(state);
			}
		}
		return this;
	}

	public StateMachineManager resumeAll() {
		for (int i = 0; i < _stateProcess.size(); i++) {
			StateMachineProcess<?> state = (StateMachineProcess<?>) _stateProcess.get(i);
			if (state != null) {
				state.unpause();
			}
		}
		return this;
	}

	public StateMachineManager pauseAll() {
		for (int i = 0; i < _stateProcess.size(); i++) {
			StateMachineProcess<?> state = (StateMachineProcess<?>) _stateProcess.get(i);
			if (state != null) {
				state.pause();
			}
		}
		return this;
	}

	public StateMachineManager closeAll() {
		for (int i = 0; i < _stateProcess.size(); i++) {
			StateMachineProcess<?> state = (StateMachineProcess<?>) _stateProcess.get(i);
			if (state != null) {
				state.close();
			}
		}
		return this;
	}

	public StateMachineManager clear() {
		_stateProcess.clear();
		return this;
	}

	@Override
	public void close() {
		closeAll();
		clear();
	}

}
