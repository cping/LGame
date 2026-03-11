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

public class BattleTile implements Cloneable {

	public interface EffectService {
		void applyEffect(BattleTile tile, BattleTileType newType, float duration);
	}

	public interface SkillService {
		void checkAndSet(BattleTile tile, BattleMapObject unit);

		boolean trigger(BattleTile tile, BattleMapObject target);
	}

	private int x;
	private int y;
	private BattleTileType tiletype;
	private boolean hasUnit;
	private BattleTileType originalType;
	private float specialEffectDuration;
	private BattleMapObject unit;
	private boolean hasSkill;
	private BattleMapObject skillUnit;
	private float skillDuration;

	private EffectService effectService;
	private SkillService skillService;

	public BattleTile() {
		this(0, 0, BattleTileType.PLAIN, BattleTileType.PLAIN, null, null);
	}

	public BattleTile(int x, int y, BattleTileType t, EffectService effectService, SkillService skillService) {
		this(x, y, t, t, effectService, skillService);
	}

	public BattleTile(int x, int y, BattleTileType t, BattleTileType o, EffectService effectService,
			SkillService skillService) {
		this.x = x;
		this.y = y;
		this.tiletype = t;
		this.originalType = o;
		this.hasUnit = false;
		this.hasSkill = false;
		this.skillDuration = 0;
		this.effectService = effectService;
		this.skillService = skillService;
	}

	public BattleTile cpy() {
		BattleTile copy = new BattleTile();
		copy.x = this.x;
		copy.y = this.y;
		copy.tiletype = this.tiletype;
		copy.originalType = this.originalType;
		copy.hasUnit = this.hasUnit;
		copy.specialEffectDuration = this.specialEffectDuration;
		copy.hasSkill = this.hasSkill;
		copy.skillDuration = this.skillDuration;
		copy.unit = this.unit;
		copy.skillUnit = this.skillUnit;
		copy.effectService = this.effectService;
		copy.skillService = this.skillService;
		return copy;
	}

	public void activateSpecialEffect(BattleTileType newType, float duration) {
		this.originalType = this.tiletype;
		this.tiletype = newType;
		this.specialEffectDuration = duration;
		if (effectService != null) {
			effectService.applyEffect(this, newType, duration);
		}
	}

	public void update(float deltaTime) {
		if (specialEffectDuration > 0) {
			specialEffectDuration -= deltaTime;
			if (specialEffectDuration <= 0) {
				this.tiletype = originalType;
			}
		}
		if (hasSkill && skillDuration > 0) {
			skillDuration -= deltaTime;
			if (skillDuration <= 0) {
				hasSkill = false;
				skillUnit = null;
			}
		}
	}

	public void setUnit(BattleMapObject unit) {
		this.unit = unit;
		this.hasUnit = (unit != null);
		if (skillService != null) {
			skillService.checkAndSet(this, unit);
		}
	}

	public boolean trigger(BattleMapObject target) {
		if (skillService != null) {
			return skillService.trigger(this, target);
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public BattleTileType getTileType() {
		return tiletype;
	}

	public void setTileType(BattleTileType t) {
		this.tiletype = t;
	}

	public boolean hasUnit() {
		return hasUnit;
	}

	public BattleMapObject getUnit() {
		return unit;
	}

	public boolean hasSkill() {
		return hasSkill;
	}

	public BattleMapObject getSkillUnit() {
		return skillUnit;
	}

	public float getSkillDuration() {
		return skillDuration;
	}

	public void setSkillUnit(BattleMapObject skillUnit) {
		this.skillUnit = skillUnit;
	}
}
