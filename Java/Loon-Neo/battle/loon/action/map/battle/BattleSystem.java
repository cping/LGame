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

import loon.utils.StringUtils;
import loon.utils.TArray;

public abstract class BattleSystem implements BattleDraw {

	protected BattleState _state;

	protected TArray<BattleAction> _actionsQueue = new TArray<BattleAction>(32);

	protected BattleAction _running;

	public BattleSystem(BattleState state) {
		_state = state;
	}
	
	public abstract void update(long elapsedTime);
	
	public void addPlayerAction(BattleAction action) {
		_actionsQueue.add(action);
	}

	public BattleState getState() {
		return _state;
	}

	public BattleAction getRunning() {
		return _running;
	}

	public BattleAction remove(int idx) {
		if (idx > -1 && idx < _actionsQueue.size) {
			return _actionsQueue.removeIndex(idx);
		}
		return null;
	}

	public BattleAction remove(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		BattleAction removed = null;
		for (BattleAction n : _actionsQueue) {
			if (n != null && name.equals(n.getActionName())) {
				removed = n;
				break;
			}
		}
		return _actionsQueue.removeValue(removed) ? removed : null;
	}

	public BattleSystem setRunning(String name) {
		if (StringUtils.isEmpty(name)) {
			return this;
		}
		for (BattleAction n : _actionsQueue) {
			if (n != null && name.equals(n.getActionName())) {
				this._running = n;
				break;
			}
		}
		return this;
	}

	public BattleSystem setRunning(int idx) {
		if (idx > -1 && idx < _actionsQueue.size) {
			this._running = _actionsQueue.get(idx);
		}
		return this;
	}

}
