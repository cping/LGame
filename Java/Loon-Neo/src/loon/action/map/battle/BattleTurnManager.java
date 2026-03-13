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

import loon.action.map.items.Team;
import loon.utils.SortedList;
import loon.utils.TArray;

public class BattleTurnManager {

	private SortedList<BattleTurnable> _turnQueue = new SortedList<BattleTurnable>();
	private TArray<BattleTurnListener> _listeners = new TArray<BattleTurnListener>();
	private int _roundCount = 0;
	boolean _playerAlive = false;
	boolean _allyAlive = false;
	boolean _enemyAlive = false;
	boolean _neutralAlive = false;

	public void registerTurnable(BattleTurnable obj) {
		_turnQueue.add(obj);
	}

	public void removeTurnable(BattleTurnable obj) {
		_turnQueue.remove(obj);
		for (BattleTurnListener l : _listeners) {
			l.onUnitDefeated(obj);
		}
	}

	public void addTurnListener(BattleTurnListener listener) {
		_listeners.add(listener);
	}

	public void startNewRound() {
		_roundCount++;
		for (BattleTurnListener l : _listeners) {
			l.onRoundStart(_roundCount);
		}
	}

	public void nextTurn() {
		if (_turnQueue.isEmpty()) {
			endRound();
			return;
		}
		TArray<BattleTurnable> simultaneous = new TArray<BattleTurnable>();
		BattleTurnable first = _turnQueue.poll();
		simultaneous.add(first);
		while (!_turnQueue.isEmpty() && _turnQueue.peek().getSpeed() == first.getSpeed()) {
			simultaneous.add(_turnQueue.poll());
		}
		for (BattleTurnable unit : simultaneous) {
			unit.startTurn();
			for (BattleTurnListener l : _listeners) {
				l.onTurnStart(unit);
			}
			unit.takeAction();
			for (BattleTurnListener l : _listeners) {
				l.onActionTaken(unit, "Action");
			}
			unit.endTurn();
			for (BattleTurnListener l : _listeners) {
				l.onTurnEnd(unit);
			}
			_turnQueue.add(unit);
		}
	}

	private void endRound() {
		for (BattleTurnListener l : _listeners) {
			l.onRoundEnd(_roundCount);
		}
		checkVictoryCondition();
	}

	private void checkVictoryCondition() {
		for (BattleTurnable u : _turnQueue) {
			if (u.getTeam() == Team.Player) {
				_playerAlive = true;
			}
			if (u.getTeam() == Team.Ally) {
				_allyAlive = true;
			}
			if (u.getTeam() == Team.Enemy) {
				_enemyAlive = true;
			}
			if (u.getTeam() == Team.Npc) {
				_neutralAlive = true;
			}
		}
	}

	public int getRoundCount() {
		return _roundCount;
	}

	public SortedList<BattleTurnable> getAllUnits() {
		return new SortedList<BattleTurnable>(_turnQueue);
	}

	public SortedList<BattleTurnable> getTurnQueue() {
		return _turnQueue;
	}

	public void setTurnQueue(SortedList<BattleTurnable> turnQueue) {
		this._turnQueue = turnQueue;
	}

	public TArray<BattleTurnListener> getListeners() {
		return _listeners;
	}

	public void setListeners(TArray<BattleTurnListener> listeners) {
		this._listeners = listeners;
	}

	public boolean isPlayerAlive() {
		return _playerAlive;
	}

	public void setPlayerAlive(boolean playerAlive) {
		this._playerAlive = playerAlive;
	}

	public boolean isAllyAlive() {
		return _allyAlive;
	}

	public void setAllyAlive(boolean allyAlive) {
		this._allyAlive = allyAlive;
	}

	public boolean isEnemyAlive() {
		return _enemyAlive;
	}

	public void setEnemyAlive(boolean enemyAlive) {
		this._enemyAlive = enemyAlive;
	}

	public boolean isNeutralAlive() {
		return _neutralAlive;
	}

	public void setNeutralAlive(boolean neutralAlive) {
		this._neutralAlive = neutralAlive;
	}

	public void setRoundCount(int roundCount) {
		this._roundCount = roundCount;
	}
}
