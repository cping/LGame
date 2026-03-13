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

/**
 * 用于为特定瓦片绑定特殊效果
 */
public class BattleTerrainEffect {
	// 无效果
	public static final BattleTerrainEffect NONE = new BattleTerrainEffect("NONE", 0, 0);
	// 毒：每回合掉5血
	public static final BattleTerrainEffect POISON = new BattleTerrainEffect("POISON", 5, 1);
	// 回血：每回合回3血
	public static final BattleTerrainEffect HEAL = new BattleTerrainEffect("HEAL", 3, 1);
	// 减速：移动速度降为0.5，持续2回合
	public static final BattleTerrainEffect SLOW = new BattleTerrainEffect("SLOW", 0.5f, 2);
	// 加速：移动速度1.5倍，持续2回合
	public static final BattleTerrainEffect BOOST = new BattleTerrainEffect("BOOST", 1.5f, 2);
	// 回气魔：每回合回5魔力
	public static final BattleTerrainEffect MP_RESTORE = new BattleTerrainEffect("MP_RESTORE", 5, 1);
	// 回气：每回合回5气力
	public static final BattleTerrainEffect STAMINA_RESTORE = new BattleTerrainEffect("STAMINA_RESTORE", 5, 1);
	// 沉默：无法释放技能，持续2回合
	public static final BattleTerrainEffect SILENCE = new BattleTerrainEffect("SILENCE", 0, 2);
	// 祝福：防御提升20%，持续3回合
	public static final BattleTerrainEffect BLESS = new BattleTerrainEffect("BLESS", 0, 3);
	// 诅咒：攻击降低20%，持续3回合
	public static final BattleTerrainEffect CURSE = new BattleTerrainEffect("CURSE", 0, 3);
	// 效果名称
	private final String name;
	// 效果值（伤害/治疗/速度倍率）
	public final float value;
	// 效果持续回合数
	public final int duration;
	// 是否生效
	public boolean isActive = false;
	// 剩余回合数
	public int remainingTurns = 0;

	public int bindingid = 0;

	private BattleTerrainEffect(String name, float value, int duration) {
		this.name = name;
		this.value = value;
		this.duration = duration;
		this.remainingTurns = duration;
	}

	public boolean update() {
		if (!isActive) {
			return false;
		}
		remainingTurns--;
		if (remainingTurns <= 0) {
			isActive = false;
			remainingTurns = duration;
			return true;
		}
		return false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getRemainingTurns() {
		return remainingTurns;
	}

	public void setRemainingTurns(int remainingTurns) {
		this.remainingTurns = remainingTurns;
	}

	public int getDuration() {
		return duration;
	}

	public int getBindingid() {
		return bindingid;
	}

	public void setBindingid(int bindingid) {
		this.bindingid = bindingid;
	}

	public void reset() {
		isActive = false;
		remainingTurns = duration;
	}

	@Override
	public String toString() {
		return name;
	}

}
