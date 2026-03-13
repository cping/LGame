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
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public abstract class BattleTurnable implements Comparable<BattleTurnable> {

	private boolean _hasActed = false;
	private int _actionPoints = 1;
	private int _speed = 10;
	private int _team = Team.Unknown;
	private ObjectMap<String, Integer> _cooldowns = new ObjectMap<String, Integer>();
	private TArray<String> _buffs = new TArray<String>();

	public BattleTurnable(int team, int speed) {
		this._team = team;
		this._speed = speed;
	}

	public void startTurn() {
		_hasActed = false;
		_actionPoints = getBaseActionPoints();
		applyStartOfTurnEffects();
		onTurnStart();
	}

	public void endTurn() {
		applyEndOfTurnEffects();
		onTurnEnd();
	}

	public abstract void takeAction();

	public boolean canAct() {
		return !_hasActed && _actionPoints > 0;
	}

	protected void consumeActionPoint() {
		if (_actionPoints > 0) {
			_actionPoints--;
			if (_actionPoints == 0) {
				_hasActed = true;
			}
		}
	}

	protected void applyStartOfTurnEffects() {
	}

	protected void applyEndOfTurnEffects() {
		for (String skill : _cooldowns.keys()) {
			int turns = _cooldowns.get(skill);
			_cooldowns.put(skill, MathUtils.max(0, turns - 1));
		}
	}

	protected abstract void onTurnStart();

	protected abstract void onTurnEnd();

	protected int getBaseActionPoints() {
		return 1;
	}

	public int getTeam() {
		return _team;
	}

	public void setTeam(int newTeam) {
		this._team = newTeam;
	}

	public int getSpeed() {
		return _speed;
	}

	public void setCooldown(String skill, int turns) {
		_cooldowns.put(skill, turns);
	}

	public boolean isSkillAvailable(String skill) {
		Integer v = _cooldowns.get(skill);
		return v == null || v == 0;
	}

	public void addBuff(String buff) {
		_buffs.add(buff);
	}

	public int compareTo(BattleTurnable other) {
		return MathUtils.compare(other._speed, this._speed);
	}

}
