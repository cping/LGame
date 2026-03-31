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
package loon.action.map.battle.script;

import loon.LSystem;
import loon.action.map.battle.BattleMap;
import loon.action.map.battle.BattleMapObject;
import loon.utils.TArray;

public class BattleScriptContext {

	protected int turn;

	protected BattleMapObject unit;

	protected BattleMap map;

	protected String weather;

	protected TArray<BattleMapObject> allUnits;

	protected BattleMapObject commander;

	protected boolean victory;

	protected boolean defeat;

	public BattleScriptContext(int turn, BattleMapObject unit, BattleMap map, String weather,
			TArray<BattleMapObject> allUnits, BattleMapObject commander) {
		this.turn = turn;
		this.unit = unit;
		this.map = map;
		this.weather = weather;
		this.allUnits = allUnits;
		this.commander = commander;
		this.victory = false;
		this.defeat = false;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public BattleMapObject getUnit() {
		return unit;
	}

	public void setUnit(BattleMapObject unit) {
		this.unit = unit;
	}

	public BattleMap getMap() {
		return map;
	}

	public void setMap(BattleMap map) {
		this.map = map;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public TArray<BattleMapObject> getAllUnits() {
		return allUnits;
	}

	public void setAllUnits(TArray<BattleMapObject> allUnits) {
		this.allUnits = allUnits;
	}

	public BattleMapObject getCommander() {
		return commander;
	}

	public void setCommander(BattleMapObject commander) {
		this.commander = commander;
	}

	public boolean isVictory() {
		return victory;
	}

	public void setVictory(boolean victory) {
		this.victory = victory;
	}

	public boolean isDefeat() {
		return defeat;
	}

	public void setDefeat(boolean defeat) {
		this.defeat = defeat;
	}

	public boolean isUnitDead(BattleMapObject u) {
		return u.getHealth() <= 0;
	}

	public boolean isCommanderDefeated() {
		return commander != null && commander.getHealth() <= 0;
	}

	public boolean isLowHealth(BattleMapObject u, float threshold) {
		return u.getMaxHealth() < threshold;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BattleScriptContext))
			return false;
		BattleScriptContext other = (BattleScriptContext) obj;
		return turn == other.turn && unit.equals(other.unit) && map.equals(other.map) && weather.equals(other.weather);
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, turn);
		hashCode = LSystem.unite(hashCode, unit);
		hashCode = LSystem.unite(hashCode, map);
		hashCode = LSystem.unite(hashCode, weather);
		return hashCode;
	}
}
