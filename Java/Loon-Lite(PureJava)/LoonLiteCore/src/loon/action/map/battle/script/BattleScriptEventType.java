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

import loon.utils.TArray;

public class BattleScriptEventType {

	// 静态预定义事件
	public static final BattleScriptEventType UNIT_DEATH = new BattleScriptEventType("UNIT_DEATH", "Unit has died");

	public static final BattleScriptEventType UNIT_LOW_HEALTH = new BattleScriptEventType("UNIT_LOW_HEALTH",
			"Unit health below threshold");

	public static final BattleScriptEventType SKILL_USED = new BattleScriptEventType("SKILL_USED",
			"Skill has been used");

	public static final BattleScriptEventType REINFORCEMENT_ARRIVED = new BattleScriptEventType("REINFORCEMENT_ARRIVED",
			"Reinforcements arrived");

	public static final BattleScriptEventType MORALE_BROKEN = new BattleScriptEventType("MORALE_BROKEN",
			"Morale broken");

	public static final BattleScriptEventType TURN_CHANGED = new BattleScriptEventType("TURN_CHANGED", "Turn changed");

	public static final BattleScriptEventType WEATHER_CHANGED = new BattleScriptEventType("WEATHER_CHANGED",
			"Weather changed");

	public static final BattleScriptEventType BATTLE_VICTORY = new BattleScriptEventType("BATTLE_VICTORY",
			"Battle victory");

	public static final BattleScriptEventType BATTLE_DEFEAT = new BattleScriptEventType("BATTLE_DEFEAT",
			"Battle defeat");

	public static final BattleScriptEventType UNIT_LEVEL_UP = new BattleScriptEventType("UNIT_LEVEL_UP",
			"Unit leveled up");

	public static final BattleScriptEventType ITEM_OBTAINED = new BattleScriptEventType("ITEM_OBTAINED",
			"Item obtained");

	public static final BattleScriptEventType OBJECTIVE_COMPLETED = new BattleScriptEventType("OBJECTIVE_COMPLETED",
			"Objective completed");

	public static final BattleScriptEventType OBJECTIVE_FAILED = new BattleScriptEventType("OBJECTIVE_FAILED",
			"Objective failed");

	public static final BattleScriptEventType TERRAIN_CHANGED = new BattleScriptEventType("TERRAIN_CHANGED",
			"Terrain changed");

	public static final BattleScriptEventType COMMANDER_DEFEATED = new BattleScriptEventType("COMMANDER_DEFEATED",
			"Commander defeated");

	public static final BattleScriptEventType TIME_LIMIT_REACHED = new BattleScriptEventType("TIME_LIMIT_REACHED",
			"Time limit reached");

	public static final BattleScriptEventType SPECIAL_TRIGGER = new BattleScriptEventType("SPECIAL_TRIGGER",
			"Special scripted event triggered");

	public static interface EventCondition {
		boolean check(BattleScriptContext context, BattleScriptEventType event);
	}

	private final String name;
	private final String description;
	private final TArray<EventCondition> conditions = new TArray<EventCondition>();
	private Object parameter;

	public BattleScriptEventType(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public BattleScriptEventType addCondition(EventCondition condition) {
		conditions.add(condition);
		return this;
	}

	public BattleScriptEventType withParameter(Object parameter) {
		this.parameter = parameter;
		return this;
	}

	public boolean isTriggered(BattleScriptContext context) {
		for (EventCondition condition : conditions) {
			if (!condition.check(context, this)) {
				return false;
			}
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Object getParameter() {
		return parameter;
	}

}
