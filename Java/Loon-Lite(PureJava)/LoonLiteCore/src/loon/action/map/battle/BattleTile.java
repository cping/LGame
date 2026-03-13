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

import loon.action.map.battle.BattleType.MoveState;
import loon.action.sprite.Animation;

public class BattleTile implements Cloneable {

	public interface EffectService {
		void applyEffect(BattleTile tile, BattleTileType newType, float duration);
	}

	public interface SkillService {
		void checkAndSet(BattleTile tile, BattleMapObject unit);

		boolean trigger(BattleTile tile, BattleMapObject target);
	}

	public int gridX, gridY;

	public int cellWidth, cellHeight;

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

	public Animation bgAnim, groundAnim, effectAnim;

	public boolean isHighlighted = false;

	public boolean isVisible = true;
	// 瓦片移动成本
	public float pathCost = 1.0f;
	// 光照亮度
	public float brightness = 1.0f;
	// 地形移动状态
	public MoveState moveState;
	// 瓦片可操作标记
	public boolean isInteractable = false;
	// 瓦片已破坏
	public boolean isDestroyed = false;
	// 耐久度
	public int durability = 100;

	public BattleTile(int x, int y, int w, int h) {
		this(x, y, w, h, BattleTileType.PLAIN, BattleTileType.PLAIN, null, null);
	}

	public BattleTile(int x, int y, int w, int h, BattleTileType t, EffectService effectService,
			SkillService skillService) {
		this(x, y, w, h, t, t, effectService, skillService);
	}

	public BattleTile(int x, int y, int w, int h, BattleTileType t, BattleTileType o, EffectService effectService,
			SkillService skillService) {
		this.gridX = x;
		this.gridY = y;
		this.cellWidth = w + t.widthOffset;
		this.cellHeight = h + t.heightOffset;
		this.tiletype = t;
		this.originalType = o;
		this.hasUnit = false;
		this.hasSkill = false;
		this.skillDuration = 0;
		this.effectService = effectService;
		this.skillService = skillService;
	}

	public BattleTile cpy() {
		BattleTile copy = new BattleTile(gridX, gridY, cellWidth, cellHeight);
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
		return gridX;
	}

	public int getY() {
		return gridY;
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

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public BattleTileType getTiletype() {
		return tiletype;
	}

	public void setTiletype(BattleTileType tiletype) {
		this.tiletype = tiletype;
	}

	public boolean isHasUnit() {
		return hasUnit;
	}

	public void setHasUnit(boolean hasUnit) {
		this.hasUnit = hasUnit;
	}

	public BattleTileType getOriginalType() {
		return originalType;
	}

	public void setOriginalType(BattleTileType originalType) {
		this.originalType = originalType;
	}

	public float getSpecialEffectDuration() {
		return specialEffectDuration;
	}

	public void setSpecialEffectDuration(float specialEffectDuration) {
		this.specialEffectDuration = specialEffectDuration;
	}

	public boolean isHasSkill() {
		return hasSkill;
	}

	public void setHasSkill(boolean hasSkill) {
		this.hasSkill = hasSkill;
	}

	public EffectService getEffectService() {
		return effectService;
	}

	public void setEffectService(EffectService effectService) {
		this.effectService = effectService;
	}

	public SkillService getSkillService() {
		return skillService;
	}

	public void setSkillService(SkillService skillService) {
		this.skillService = skillService;
	}

	public Animation getBgAnim() {
		return bgAnim;
	}

	public void setBgAnim(Animation bgAnim) {
		this.bgAnim = bgAnim;
	}

	public Animation getGroundAnim() {
		return groundAnim;
	}

	public void setGroundAnim(Animation groundAnim) {
		this.groundAnim = groundAnim;
	}

	public Animation getEffectAnim() {
		return effectAnim;
	}

	public void setEffectAnim(Animation effectAnim) {
		this.effectAnim = effectAnim;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public float getPathCost() {
		return pathCost;
	}

	public void setPathCost(float pathCost) {
		this.pathCost = pathCost;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	public MoveState getMoveState() {
		return moveState;
	}

	public void setMoveState(MoveState moveState) {
		this.moveState = moveState;
	}

	public boolean isInteractable() {
		return isInteractable;
	}

	public void setInteractable(boolean isInteractable) {
		this.isInteractable = isInteractable;
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}

	public void setDestroyed(boolean isDestroyed) {
		this.isDestroyed = isDestroyed;
	}

	public int getDurability() {
		return durability;
	}

	public void setDurability(int durability) {
		this.durability = durability;
	}

	public void setSkillDuration(float skillDuration) {
		this.skillDuration = skillDuration;
	}
}
