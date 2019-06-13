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

import loon.geom.Affine2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * State管理器
 */
public class StateManager implements LRelease {

	private final TArray<State> states;

	private int index;

	public StateManager() {
		this.index = -1;
		this.states = new TArray<State>();
	}

	public void add(String name, State state) {
		if (state != null) {
			if (!states.contains(state)) {
				states.add(state);
				state.setName(name);
				state.setStateManager(this);
			}
			index = 0;
		}
	}

	public void play(int idx) {
		if (idx != this.index) {
			State state = states.get(idx);
			if (state != null) {
				state.close();
				state.isLoaded = false;
			}
		}
		this.index = MathUtils.clamp(idx, 0, states.size - 1);
	}

	public void play(String name) {
		int idx = -1;
		for (int i = states.size - 1; i > -1; i--) {
			State state = states.get(i);
			if (state != null && name.equals(state.stateName)) {
				idx = i;
				break;
			}
		}
		play(idx);
	}

	public void remove(int idx) {
		if (idx > -1) {
			states.removeIndex(idx);
		} else {
			idx = 0;
		}
		while (idx == index) {
			index--;
		}
		if (index < 0) {
			index = -1;
		}
	}

	public void remove(String name) {
		int idx = -1;
		for (int i = states.size - 1; i > -1; i--) {
			State state = states.get(i);
			if (state != null && name.equals(state.stateName)) {
				idx = i;
				return;
			}
		}
		remove(idx);
	}

	public State peek() {
		if (index > -1) {
			return states.get(index);
		}
		return null;
	}

	public void pop() {
		State s = states.pop();
		if (s != null) {
			s.close();
			s.isLoaded = false;
		}
		index = (states.size - 1);
	}

	public void load() {
		for (int i = states.size - 1; i > -1; i--) {
			State state = states.get(i);
				state.load();
				state.isLoaded = true;
		}
	}

	public void update(float delta) {
		if (index > -1) {
			State state = states.get(index);
			if (!state.isLoaded) {
				state.load();
				state.isLoaded = true;
			}
			state.update(delta);
		}
	}

	public void paint(GLEx g) {
		if (index > -1) {
			State state = states.get(index);
			if (!state.isLoaded) {
				state.load();
				state.isLoaded = true;
			}
			try {
				if (state.syncCamera) {
					g.saveTx();
					Affine2f tx = g.tx();
					if (tx != null) {
						tx.concat(state.camera);
					}
				}
				state.paint(g);
			} finally {
				if (state.syncCamera) {
					g.restoreTx();
				}
			}
		}
	}

	@Override
	public void close() {
		for (int i = states.size - 1; i > -1; i--) {
			State state = states.get(i);
			if (state != null) {
				state.close();
				state.isLoaded = false;
			}
		}
		states.clear();
		index = -1;
	}

}
