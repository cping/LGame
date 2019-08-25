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
package loon.action.map.battle;

import loon.utils.TArray;

public class BattleStateMachine {

	protected TArray<BattleTransition> _transitions;

	protected BattleState _current;

	public BattleStateMachine(BattleState start, TArray<BattleTransition> transitions) {
		this(start, transitions, true);
	}

	public BattleStateMachine(BattleState start, TArray<BattleTransition> transitions, boolean enter) {
		this._current = start;
		this._transitions = transitions;
		if (enter) {
			this._current.onEnter();
		}
	}

	public void addTransition(BattleTransition tran) {
		_transitions.add(tran);
	}

	public void update(long elapsedTime) {
		_current.update(elapsedTime);
		BattleState nextState = getNextState();
		if (nextState != null) {
			_current.onExit();
			nextState.onEnter();
			_current = nextState;
		}
	}

	public BattleTransition remove(int idx) {
		if (idx > -1 && idx < _transitions.size) {
			return _transitions.removeIndex(idx);
		}
		return null;
	}

	public BattleState getNextState() {
		for (BattleTransition transition : _transitions) {
			if (transition.from != null && !transition.from.equals(_current)) {
				continue;
			}
			if (transition.condition.isTrue()) {
				return transition.to;
			}
		}
		return null;
	}
}
