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
package loon;

import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * State管理器(简单的FSM状态机实现)
 */
public class StateManager implements LRelease {

	public State _currentState;

	public State _previousState;

	private final TArray<State> _states;

	private int _currentIndex;

	public StateManager() {
		this._currentIndex = -1;
		this._states = new TArray<State>();
	}

	public StateManager add(String name, State state) {
		if (state != null) {
			if (!_states.contains(state)) {
				_states.add(state);
				state.setName(name);
				state.setStateManager(this);
			}
			_currentIndex = 0;
		}
		return this;
	}

	public StateManager playNext() {
		if (this._currentIndex < _states.size - 1) {
			play(this._currentIndex + 1);
		}
		return this;
	}

	public StateManager playBack() {
		if (this._currentIndex > 0) {
			play(this._currentIndex - 1);
		}
		return this;
	}

	public StateManager play(int idx) {
		if (idx == -1) {
			return this;
		}
		if (this._currentIndex == idx && this._currentState != null) {
			return this;
		}
		int newidx = MathUtils.clamp(idx, 0, _states.size - 1);
		this.changeState(this._currentIndex, newidx);
		return this;
	}

	public StateManager play(String name) {
		int idx = -1;
		for (int i = _states.size - 1; i > -1; i--) {
			State state = _states.get(i);
			if (state != null && name.equals(state._stateName)) {
				idx = i;
				break;
			}
		}
		play(idx);
		return this;
	}

	public StateManager remove(int idx) {
		if (idx == -1) {
			return this;
		}
		if (idx < _states.size) {
			State oldState = _states.removeIndex(idx);
			int oldidx = _currentIndex;
			int newidx = _currentIndex;
			while (idx == newidx) {
				newidx--;
			}
			if (newidx < 0) {
				newidx = -1;
			}
			if (newidx != -1 && newidx != this._currentIndex) {
				this.changeState(oldidx, newidx);
			} else if (oldState != null) {
				oldState.end();
				oldState.close();
				oldState._isLoaded = false;
			}
			this._currentIndex = newidx;
		}
		return this;
	}

	public StateManager remove(String name) {
		int idx = -1;
		for (int i = _states.size - 1; i > -1; i--) {
			State state = _states.get(i);
			if (state != null && name.equals(state._stateName)) {
				idx = i;
				break;
			}
		}
		remove(idx);
		return this;
	}

	public State getCurrentState() {
		return this._currentState;
	}

	public State getPreviousState() {
		return this._previousState;
	}

	private State changeState(int oldidx, int newidx) {
		if (this._currentIndex == newidx && oldidx != this._currentIndex) {
			if (oldidx < _states.size) {
				this._currentState = _states.get(oldidx);
				if (_currentState != null) {
					_currentState.end();
					_currentState.close();
					_currentState._isLoaded = false;
				}
				this._previousState = _currentState;
			}
		} else {
			if (oldidx != newidx) {
				if (oldidx < _states.size) {
					this._currentState = _states.get(oldidx);
					if (_currentState != null) {
						_currentState.end();
						_currentState.close();
						_currentState._isLoaded = false;
					}
					this._previousState = _currentState;
				}
				this._currentIndex = newidx;
				if (newidx < _states.size) {
					this._currentState = _states.get(newidx);
					if (_currentState != null) {
						_currentState.begin();
					}
				}
			} else {
				this._currentIndex = newidx;
				this._currentState = _states.get(_currentIndex);
				this._previousState = _currentState;
				if (_currentState != null) {
					_currentState.begin();
				}
			}
		}
		return _currentState;
	}

	public State getState() {
		return peek();
	}

	public State peek() {
		if (_currentIndex > -1) {
			return _states.get(_currentIndex);
		}
		return null;
	}

	public StateManager pop() {
		State oldState = _states.pop();
		if (oldState != null) {
			oldState.end();
			oldState.close();
			oldState._isLoaded = false;
			_previousState = oldState;
		}
		if (_currentIndex >= _states.size) {
			_currentIndex = (_states.size - 1);
			if (_currentIndex > -1) {
				this._currentState = _states.get(_currentIndex);
				if (_currentState != null) {
					_currentState.begin();
				}
			}
		}
		return this;
	}

	public StateManager load() {
		for (int i = _states.size - 1; i > -1; i--) {
			State state = _states.get(i);
			state.load();
			state._isLoaded = true;
		}
		return this;
	}

	public void update(float delta) {
		if (_currentIndex > -1) {
			State state = _states.get(_currentIndex);
			if (!state._isLoaded) {
				state.load();
				state._isLoaded = true;
			}
			state.update(delta);
		}
	}

	public void paint(GLEx g) {
		if (_currentIndex > -1) {
			State state = _states.get(_currentIndex);
			if (!state._isLoaded) {
				state.load();
				state._isLoaded = true;
			}
			try {
				if (state._syncCamera) {
					g.concat(state._camera);
				}
				state.paint(g);
			} finally {
				if (state._syncCamera) {
					g.restoreTx();
				}
			}
		}
	}

	@Override
	public void close() {
		for (int i = _states.size - 1; i > -1; i--) {
			State state = _states.get(i);
			if (state != null) {
				state.end();
				state.close();
				state._isLoaded = false;
			}
		}
		_states.clear();
		_currentIndex = -1;
	}

}
