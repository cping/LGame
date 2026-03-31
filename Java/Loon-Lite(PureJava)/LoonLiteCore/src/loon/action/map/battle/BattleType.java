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

public class BattleType {

	public static enum GameMode {
		TURN_BASED, REAL_TIME
	}

	public static enum SkillType {
		BATTLE_SKILL, LEADER_SKILL, TALENT, STRATEGY, PASSIVE
	}

	public static enum EffectType {
		ATTACK, SKILL, EXPLOSION, FIRE, THUNDER, DAMAGE, HEAL, MORALE_CHANGE, FATIGUE_CHANGE, ATTACK_UP, DEFENSE_UP,
		SPEED_UP, RESIST_UP, CONFUSION, STUN, FREEZE, BURN, POISON, SILENCE, PANIC, AMBUSH, PUSH_BACK, TAUNT,
		ASSIST_ATTACK
	}

	public static enum ObjectState {
		IDLE, MOVING, ATTACKING, CASTING, CASTING_SKILL, DEAD, FLEEING, PANICKED, FAKE_RETREAT, CONFUSED, STUNNED,
		FROZEN, BURNING, POISONED, SILENCED, FATIGUED, RESTING, HIDING, DEFENDING, WAITING, PATROLLING, AUTO_ATTACK,
		GARRISONED, BLOCKED, SKILL, PREPARE_ATTACK, PREPARE_SKILL, LEFT, RIGHT, UP, DOWN
	}

	public static enum UnitType {
		INFANTRY, HEAVY_INFANTRY, CAVALRY, LIGHT_CAVALRY, ARCHER, ELITE_ARCHER, CATAPULT, SIEGE, NAVAL
	}

	public static enum RangeType {
		SINGLE, CIRCLE, LINE, CROSS, AREA, SELF, ADJACENT, GLOBAL, DIAGONAL, SQUARE, LINE_AOE, AOE, ROW, COLUMN, RING,
		SECTOR, DIAMOND, PLUS, CHECKER, RANDOM, PATH
	}

	public static enum WeatherType {
		CLEAR, RAINY, WINDY, FOGGY, SNOWY
	}

	public static enum MoveState {
		NORMAL, LIGHTNESS, DIFFICULT, SWIM, CLIMB, FLY
	}
}
