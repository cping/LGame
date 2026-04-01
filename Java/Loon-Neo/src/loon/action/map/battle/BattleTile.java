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
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ISOUtils;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.ISOUtils.IsoResult;

public class BattleTile {

	public interface EffectService {
		void applyEffect(BattleTile tile, BattleTileType newType, float duration);
	}

	public interface SkillService {
		void checkAndSet(BattleTile tile, BattleMapObject unit);

		boolean trigger(BattleTile tile, BattleMapObject target);
	}

	public int gridX, gridY;

	public int cellWidth, cellHeight;

	private final Vector2f tempResult = new Vector2f();

	private BattleTerrainEffect terrainEffect;
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

	// 瓦片可操作标记
	public boolean isInteractable = false;
	// 瓦片已破坏
	public boolean isDestroyed = false;
	// 耐久度
	public int durability = 100;
	// 斜视参数基本设置状态
	private final IsoConfig isoCofing;

	public BattleTile(int x, int y, int w, int h, IsoConfig config) {
		this(x, y, w, h, config, null, null);
	}

	public BattleTile(int x, int y, int w, int h, IsoConfig config, EffectService effectService,
			SkillService skillService) {
		this(x, y, w, h, config, BattleTileType.PLAIN, BattleTileType.PLAIN, effectService, skillService, 1f, 1f, 100);
	}

	public BattleTile(int x, int y, int w, int h, IsoConfig config, BattleTileType t, EffectService effectService,
			SkillService skillService) {
		this(x, y, w, h, config, t, t, effectService, skillService, 1f, 1f, 100);
	}

	public BattleTile(int x, int y, int w, int h, IsoConfig config, BattleTileType t, BattleTileType o,
			EffectService effectService, SkillService skillService, float pathCost, float brightness, int durability) {
		this.gridX = x;
		this.gridY = y;
		this.cellWidth = w + t.widthOffset;
		this.cellHeight = h + t.heightOffset;
		this.isoCofing = config;
		this.tiletype = t;
		this.originalType = o;
		this.hasUnit = false;
		this.hasSkill = false;
		this.skillDuration = 0;
		this.effectService = effectService;
		this.skillService = skillService;
		this.pathCost = pathCost;
		this.brightness = brightness;
		this.isInteractable = false;
		this.isDestroyed = false;
		this.durability = durability;
		if (pathCost <= 0) {
			this.pathCost = calculatePathCost();
		}
	}

	public BattleTile cpy() {
		BattleTile copy = new BattleTile(gridX, gridY, cellWidth, cellHeight, isoCofing);
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
		copy.pathCost = this.pathCost;
		copy.brightness = this.brightness;
		copy.isInteractable = this.isInteractable;
		copy.isDestroyed = this.isDestroyed;
		copy.durability = this.durability;
		copy.bgAnim = this.bgAnim.cpy();
		copy.effectAnim = this.effectAnim.cpy();
		copy.groundAnim = this.groundAnim.cpy();
		return copy;
	}

	private float calculatePathCost() {
		float baseCost = 1f / tiletype.moveSpeedMultiplier;
		baseCost *= (1.0f + (cellHeight * 0.2f));
		if (terrainEffect != null) {
			if (terrainEffect == BattleTerrainEffect.SLOW) {
				baseCost *= 1.5f;
			} else if (terrainEffect == BattleTerrainEffect.POISON) {
				baseCost *= 2.0f;
			}
		}
		if (tiletype != null) {
			if (tiletype.defaultMoveState == MoveState.DIFFICULT) {
				baseCost *= 2.0f;
			} else if (tiletype.defaultMoveState == MoveState.CLIMB) {
				baseCost *= 1.5f;
			}
		}
		if (isDestroyed) {
			baseCost *= 1.8f;
		}
		return baseCost;
	}

	public Vector2f getScreenPosition(Vector2f result, IsoResult iso) {
		return ISOUtils.isoTransform(gridX, gridY, cellWidth, cellHeight, isoCofing, result, iso).screenPos;
	}

	public void activateSpecialEffect(BattleTileType newType, float duration) {
		this.originalType = this.tiletype;
		this.tiletype = newType;
		this.specialEffectDuration = duration;
		if (effectService != null) {
			effectService.applyEffect(this, newType, duration);
		}
	}

	/**
	 * 更新瓦片亮度
	 */
	public void updateBrightness() {
		ISOUtils.IsoResult result = ISOUtils.isoTransform(gridX, gridY, cellWidth, cellHeight, isoCofing);
		this.brightness = result.brightness;
	}

	/**
	 * 检测点击
	 * 
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	public boolean isClicked(int screenX, int screenY) {
		return ISOUtils.isTileClicked(gridX, gridY, cellWidth, cellHeight, screenX, screenY, isoCofing, tempResult);
	}

	public void adaptToTileSize(int width, int height) {
		this.cellWidth = width;
		this.cellHeight = height;
		pathCost = calculatePathCost();
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
		if (bgAnim != null) {
			bgAnim.update(deltaTime);
		}
		if (groundAnim != null) {
			groundAnim.update(deltaTime);
		}
		if (effectAnim != null) {
			effectAnim.update(deltaTime);
		}
	}

	public void paint(GLEx g, float drawX, float drawY, float tileWidth, float tileHeight, LColor color) {

		// 绘制背景层
		if (bgAnim != null) {
			g.draw(bgAnim.getSpriteImage(), drawX, drawY, tileWidth, tileHeight, color);
		}
		// 绘制地表层
		if (groundAnim != null) {
			g.draw(groundAnim.getSpriteImage(), drawX, drawY, tileWidth, tileHeight, color);
		}
		// 绘制特效层
		if (effectAnim != null) {
			g.draw(effectAnim.getSpriteImage(), drawX, drawY, tileWidth, tileHeight, color);
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
		return tiletype.getDefaultMoveState();
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

	public IsoConfig getIsoCofing() {
		return isoCofing;
	}

	public BattleTerrainEffect getTerrainEffect() {
		return terrainEffect;
	}

	public void setTerrainEffect(BattleTerrainEffect terrainEffect) {
		this.terrainEffect = terrainEffect;
	}

	public boolean isPassable() {
		return tiletype.isPassable();
	}
}
