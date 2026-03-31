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

import loon.action.map.battle.BattleType.RangeType;
import loon.action.map.battle.BattleType.SkillType;
import loon.action.map.battle.BattleType.UnitType;
import loon.action.map.battle.BattleType.WeatherType;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.SortedList;
import loon.utils.TArray;

public class BattleSkill {
	// 唯一标识
	String id;
	// 名称
	String name;
	// 描述
	String description;
	// 类型
	SkillType skilltype;
	// 限定兵种
	UnitType[] limitUnits;
	// 限定地形
	BattleTileType[] limitTiles;
	// 限定天气
	WeatherType[] limitWeathers;
	// 最低使用等级
	int minLevel = 1;
	// 是否需要城池
	boolean needCity = false;
	// 优先级(1-5)
	int priority = 3;
	// 基础成功率
	float baseSuccessRate = 0.7f;
	// 气力消耗
	int moraleCost = 20;
	// 冷却时间(秒/回合)
	int cooldown = 5;
	// 上次使用时间
	long lastUseTime = 0;
	// 魔力消耗
	int mpCost = 0;
	// 行动点消耗
	int actionPointCost = 2;
	// 攻击范围
	RangeType rangeType = RangeType.SINGLE;
	// 攻击距离
	int rangeDistance = 1;
	// 范围半径
	int rangeRadius = 1;

	public BattleSkill(String id, String name, String description, SkillType skilltype, UnitType[] limitUnits,
			BattleTileType[] limitTiles, WeatherType[] limitWeathers, int minLevel, boolean needCity, int priority,
			float baseSuccessRate, int moraleCost, int cooldown, long lastUseTime, int mpCost, int actionPointCost,
			RangeType rangeType, int rangeDistance, int rangeRadius) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.skilltype = skilltype;
		this.limitUnits = limitUnits;
		this.limitTiles = limitTiles;
		this.limitWeathers = limitWeathers;
		this.minLevel = minLevel;
		this.needCity = needCity;
		this.priority = priority;
		this.baseSuccessRate = baseSuccessRate;
		this.moraleCost = moraleCost;
		this.cooldown = cooldown;
		this.lastUseTime = lastUseTime;
		this.mpCost = mpCost;
		this.actionPointCost = actionPointCost;
		this.rangeType = rangeType;
		this.rangeDistance = rangeDistance;
		this.rangeRadius = rangeRadius;
	}

	public SortedList<Vector2f> getSkillRange(BattleMapObject caster, BattleTile[][] map, int mapWidth, int mapHeight,
			RangeType rangeType, int rangeRadius, int rangeDistance) {
		return getSkillRange(caster, map, mapWidth, mapHeight, rangeType, rangeRadius, rangeDistance, true, null);
	}

	public SortedList<Vector2f> getSkillRange(BattleMapObject caster, BattleTile[][] map, int mapWidth, int mapHeight,
			RangeType rangeType, int rangeRadius, int rangeDistance, boolean allDir, TArray<Vector2f> pathList) {
		SortedList<Vector2f> range = new SortedList<Vector2f>();
		final int[][] dirs4 = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		final int[][] dirs8 = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
		final int[][] dirs = allDir ? dirs8 : dirs4;
		switch (rangeType) {
		case SINGLE:
		case SELF:
			range.add(new Vector2f(caster.gridX, caster.gridY));
			break;
		case ADJACENT:
			for (int[] d : dirs) {
				int nx = caster.gridX + d[0], ny = caster.gridY + d[1];
				if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
					range.add(new Vector2f(nx, ny));
				}
			}
			break;
		case CROSS:
			for (int i = 1; i <= rangeDistance; i++) {
				for (int[] d : dirs) {
					int nx = caster.gridX + d[0] * i;
					int ny = caster.gridY + d[1] * i;
					if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
						range.add(new Vector2f(nx, ny));
					}
				}
			}
			break;
		case DIAGONAL:
			for (int i = 1; i <= rangeDistance; i++) {
				int[][] diagDirs = { { i, i }, { i, -i }, { -i, i }, { -i, -i } };
				for (int[] d : diagDirs) {
					int nx = caster.gridX + d[0], ny = caster.gridY + d[1];
					if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
						range.add(new Vector2f(nx, ny));
					}
				}
			}
			break;
		case CIRCLE:
			for (int dx = -rangeRadius; dx <= rangeRadius; dx++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					if (dx * dx + dy * dy <= rangeDistance * rangeDistance) {
						int nx = caster.gridX + dx, ny = caster.gridY + dy;
						if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
							range.add(new Vector2f(nx, ny));
						}
					}
				}
			}
			break;
		case AOE:
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					range.add(new Vector2f(x, y));
				}
			}
			break;
		case SQUARE:
		case AREA:
			for (int dx = -rangeRadius; dx <= rangeRadius; dx++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					int nx = caster.gridX + dx, ny = caster.gridY + dy;
					if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
						range.add(new Vector2f(nx, ny));
					}
				}
			}
			break;

		case LINE:
			for (int i = 1; i <= rangeDistance; i++) {
				int nx = caster.gridX + i, ny = caster.gridY;
				if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
					range.add(new Vector2f(nx, ny));
				}
			}
			break;
		case LINE_AOE:
			for (int i = 1; i <= rangeDistance; i++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					int nx = caster.gridX + i, ny = caster.gridY + dy;
					if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
						range.add(new Vector2f(nx, ny));
					}
				}
			}
			break;
		case GLOBAL:
			for (int x = 0; x < mapWidth; x++) {
				for (int y = 0; y < mapHeight; y++) {
					range.add(new Vector2f(x, y));
				}
			}
			break;
		case ROW:
			for (int x = 0; x < mapWidth; x++) {
				range.add(new Vector2f(x, caster.gridY));
			}
			break;

		case COLUMN:
			for (int y = 0; y < mapHeight; y++) {
				range.add(new Vector2f(caster.gridX, y));
			}
			break;
		case RING:
			for (int dx = -rangeRadius; dx <= rangeRadius; dx++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					int dist2 = dx * dx + dy * dy;
					if (dist2 <= rangeRadius * rangeRadius && dist2 >= (rangeRadius - 1) * (rangeRadius - 1)) {
						int nx = caster.gridX + dx, ny = caster.gridY + dy;
						if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
							range.add(new Vector2f(nx, ny));
						}
					}
				}
			}
			break;
		case SECTOR:
			for (int dx = 0; dx <= rangeDistance; dx++) {
				for (int dy = -dx; dy <= dx; dy++) {
					int nx = caster.gridX + dx, ny = caster.gridY + dy;
					if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
						range.add(new Vector2f(nx, ny));
					}
				}
			}
			break;
		case DIAMOND:
			for (int dx = -rangeRadius; dx <= rangeRadius; dx++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					if (Math.abs(dx) + Math.abs(dy) <= rangeRadius) {
						int nx = caster.gridX + dx, ny = caster.gridY + dy;
						if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
							range.add(new Vector2f(nx, ny));
						}
					}
				}
			}
			break;
		case PLUS:
			for (int i = -rangeRadius; i <= rangeRadius; i++) {
				int nx1 = caster.gridX + i, ny1 = caster.gridY;
				int nx2 = caster.gridX, ny2 = caster.gridY + i;
				if (nx1 >= 0 && nx1 < mapWidth && ny1 >= 0 && ny1 < mapHeight) {
					range.add(new Vector2f(nx1, ny1));
				}
				if (nx2 >= 0 && nx2 < mapWidth && ny2 >= 0 && ny2 < mapHeight) {
					range.add(new Vector2f(nx2, ny2));
				}
			}
			break;
		case CHECKER:
			for (int dx = -rangeRadius; dx <= rangeRadius; dx++) {
				for (int dy = -rangeRadius; dy <= rangeRadius; dy++) {
					if ((dx + dy) % 2 == 0) {
						int nx = caster.gridX + dx, ny = caster.gridY + dy;
						if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
							range.add(new Vector2f(nx, ny));
						}
					}
				}
			}
			break;
		case RANDOM:
			for (int i = 0; i < rangeRadius; i++) {
				int nx = caster.gridX + MathUtils.nextInt(rangeRadius * 2 + 1) - rangeRadius;
				int ny = caster.gridY + MathUtils.nextInt(rangeRadius * 2 + 1) - rangeRadius;
				if (nx >= 0 && nx < mapWidth && ny >= 0 && ny < mapHeight) {
					range.add(new Vector2f(nx, ny));
				}
			}
			break;
		case PATH:
			if (pathList != null) {
				for (Vector2f p : pathList) {
					if (p.x >= 0 && p.x < mapWidth && p.y >= 0 && p.y < mapHeight) {
						range.add(p);
					}
				}
			}
			break;
		default:
			range.add(new Vector2f(caster.gridX, caster.gridY));
			break;
		}
		return range;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SkillType getSkilltype() {
		return skilltype;
	}

	public void setSkilltype(SkillType skilltype) {
		this.skilltype = skilltype;
	}

	public UnitType[] getLimitUnits() {
		return limitUnits;
	}

	public void setLimitUnits(UnitType[] limitUnits) {
		this.limitUnits = limitUnits;
	}

	public BattleTileType[] getLimitTiles() {
		return limitTiles;
	}

	public void setLimitTiles(BattleTileType[] limitTiles) {
		this.limitTiles = limitTiles;
	}

	public WeatherType[] getLimitWeathers() {
		return limitWeathers;
	}

	public void setLimitWeathers(WeatherType[] limitWeathers) {
		this.limitWeathers = limitWeathers;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public boolean isNeedCity() {
		return needCity;
	}

	public void setNeedCity(boolean needCity) {
		this.needCity = needCity;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getBaseSuccessRate() {
		return baseSuccessRate;
	}

	public void setBaseSuccessRate(float baseSuccessRate) {
		this.baseSuccessRate = baseSuccessRate;
	}

	public int getMoraleCost() {
		return moraleCost;
	}

	public void setMoraleCost(int moraleCost) {
		this.moraleCost = moraleCost;
	}

	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public long getLastUseTime() {
		return lastUseTime;
	}

	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}

	public int getMpCost() {
		return mpCost;
	}

	public void setMpCost(int mpCost) {
		this.mpCost = mpCost;
	}

	public int getActionPointCost() {
		return actionPointCost;
	}

	public void setActionPointCost(int actionPointCost) {
		this.actionPointCost = actionPointCost;
	}

	public RangeType getRangeType() {
		return rangeType;
	}

	public void setRangeType(RangeType rangeType) {
		this.rangeType = rangeType;
	}

	public int getRangeDistance() {
		return rangeDistance;
	}

	public void setRangeDistance(int rangeDistance) {
		this.rangeDistance = rangeDistance;
	}

	public int getRangeRadius() {
		return rangeRadius;
	}

	public void setRangeRadius(int rangeRadius) {
		this.rangeRadius = rangeRadius;
	}

}
