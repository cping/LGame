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

import loon.action.map.battle.BattleType.UnitType;
import loon.utils.IntMap;

/**
 * 主要供战棋或回合制游戏使用的地形参数类
 */
public class BattleTileType {

	public static BattleTileType getById(int id) {
		return TILE_MAP.get(id);
	}

	// 基础地形
	// 平原
	public static final BattleTileType PLAIN = new BattleTileType(1, 101, "PLAIN", true, false, 1, 1.0f, 0.0f);
	// 草地
	public static final BattleTileType GRASSLAND = new BattleTileType(2, 102, "GRASSLAND", true, false, 1, 1.0f, 0.0f);
	// 沙漠
	public static final BattleTileType DESERT = new BattleTileType(3, 103, "DESERT", true, false, 2, 1.1f, 0.1f);
	// 森林
	public static final BattleTileType FOREST = new BattleTileType(4, 104, "FOREST", true, true, 2, 0.9f, 0.2f);
	// 密林
	public static final BattleTileType DENSE_FOREST = new BattleTileType(5, 105, "DENSE_FOREST", true, true, 3, 0.8f,
			0.3f);
	// 山地
	public static final BattleTileType MOUNTAIN = new BattleTileType(6, 106, "MOUNTAIN", false, true, 3, 0.7f, 0.5f);
	// 丘陵
	public static final BattleTileType HILL = new BattleTileType(7, 107, "HILL", true, true, 2, 0.9f, 0.25f);
	// 河流
	public static final BattleTileType RIVER = new BattleTileType(8, 108, "RIVER", true, false, 2, 1.0f, 0.0f);
	// 浅滩
	public static final BattleTileType FORD = new BattleTileType(9, 109, "FORD", true, false, 3, 1.0f, 0.0f);
	// 沙洲
	public static final BattleTileType SHOAL = new BattleTileType(10, 110, "SHOAL", true, false, 1, 1.0f, 0.0f);
	// 海洋
	public static final BattleTileType SEA = new BattleTileType(11, 111, "SEA", true, false, 2, 1.0f, 0.0f);
	// 海岸
	public static final BattleTileType COAST = new BattleTileType(12, 112, "COAST", true, false, 1, 1.0f, 0.0f);
	// 沼泽
	public static final BattleTileType SWAMP = new BattleTileType(13, 113, "SWAMP", true, false, 3, 0.8f, 0.1f);
	// 湿地
	public static final BattleTileType MARSH = new BattleTileType(14, 114, "MARSH", true, false, 4, 0.7f, 0.15f);
	// 火山
	public static final BattleTileType VOLCANO = new BattleTileType(15, 301, "VOLCANO", false, true, 5, 0.0f, 0.0f);
	// 断崖
	public static final BattleTileType CLIFF = new BattleTileType(16, 302, "CLIFF", false, true, 4, 0.0f, 0.0f);

	// 建筑地形
	// 道路
	public static final BattleTileType ROAD = new BattleTileType(17, 201, "ROAD", true, false, 1, 1.0f, 0.0f);
	// 桥梁
	public static final BattleTileType BRIDGE = new BattleTileType(18, 202, "BRIDGE", true, false, 1, 1.0f, 0.0f);
	// 砦
	public static final BattleTileType FORT = new BattleTileType(19, 203, "FORT", true, true, 1, 0.8f, 0.4f);
	// 城塞
	public static final BattleTileType CASTLE = new BattleTileType(20, 204, "CASTLE", true, true, 1, 0.7f, 0.6f);
	// 城池
	public static final BattleTileType CITY = new BattleTileType(21, 205, "CITY", true, true, 1, 0.6f, 0.8f);
	// 城墙
	public static final BattleTileType WALL = new BattleTileType(22, 206, "WALL", true, true, 2, 0.7f, 0.7f);
	// 城门
	public static final BattleTileType GATE = new BattleTileType(23, 207, "GATE", true, true, 1, 0.7f, 0.7f);
	// 岗哨
	public static final BattleTileType TOWER = new BattleTileType(24, 208, "TOWER", true, true, 1, 0.8f, 0.5f);
	// 木栅
	public static final BattleTileType PALISADE = new BattleTileType(25, 209, "PALISADE", true, true, 1, 0.85f, 0.3f);
	// 渡口
	public static final BattleTileType FERRY = new BattleTileType(26, 303, "FERRY", true, false, 2, 1.0f, 0.0f);
	// 港口
	public static final BattleTileType PORT = new BattleTileType(27, 304, "PORT", true, false, 1, 1.0f, 0.0f);
	// 堤坝
	public static final BattleTileType DAM = new BattleTileType(28, 305, "DAM", true, false, 2, 1.0f, 0.0f);
	// 矿坑
	public static final BattleTileType MINE = new BattleTileType(29, 306, "MINE", true, false, 2, 1.0f, 0.0f);
	// 农田
	public static final BattleTileType FARM = new BattleTileType(30, 307, "FARM", true, false, 1, 1.0f, 0.0f);
	// 市场
	public static final BattleTileType MARKET = new BattleTileType(31, 308, "MARKET", true, false, 1, 1.0f, 0.0f);
	// 唯一标识（地形类型）
	private final int id;
	// 用于和其它物体绑定
	private final int bindingId;
	// 地形名称
	private final String name;
	// 是否可通行
	private final boolean passable;
	// 是否有防御加成
	private final boolean defensive;
	// 基础行动点消耗
	private final int baseActionCost; 
	// 攻击倍率
	private final float attackMultiplier;
	// 防御加成
	private final float defenseBonus; 

	private static final IntMap<BattleTileType> TILE_MAP = new IntMap<BattleTileType>();

	private BattleTileType(int id, int bindingId, String name, boolean passable, boolean defensive, int baseActionCost,
			float attackMultiplier, float defenseBonus) {
		this.id = id;
		this.bindingId = bindingId;
		this.name = name;
		this.passable = passable;
		this.defensive = defensive;
		this.baseActionCost = baseActionCost;
		this.attackMultiplier = attackMultiplier;
		this.defenseBonus = defenseBonus;

		TILE_MAP.put(id, this);
	}

	public int getId() {
		return id;
	}

	public int getBindingId() {
		return bindingId;
	}

	public String getName() {
		return name;
	}

	public boolean isPassable() {
		return passable;
	}

	public boolean isDefensive() {
		return defensive;
	}

	public int getBaseActionCost() {
		return baseActionCost;
	}

	public float getAttackMultiplier() {
		return attackMultiplier;
	}

	public float getDefenseBonus() {
		return defenseBonus;
	}

	public int getActionPointCost(UnitType unitType) {
		int cost = baseActionCost;
		switch (unitType) {
		case CAVALRY:
		case LIGHT_CAVALRY:
			if (this == MOUNTAIN || this == SWAMP || this == MARSH || this == DENSE_FOREST) {
				cost *= 2;
			}
			if (this == ROAD || this == PLAIN) {
				cost = Math.max(1, cost / 2);
			}
			break;

		case HEAVY_INFANTRY:
			if (this == WALL || this == CASTLE || this == FORT) {
				cost = Math.max(1, cost / 2);
			}
			if (this == RIVER || this == SEA) {
				cost *= 2;
			}
			break;

		case ARCHER:
		case ELITE_ARCHER:
			if (this == HILL || this == MOUNTAIN) {
				cost = Math.max(1, cost);
			}
			break;

		case NAVAL:
			if (this == RIVER || this == SEA || this == FORD || this == FERRY || this == PORT) {
				cost = Math.max(1, cost / 2);
			}
			if (!(this == SEA || this == RIVER || this == FORD || this == FERRY || this == PORT)) {
				cost *= 2;
			}
			break;

		case CATAPULT:
		case SIEGE:
			if (this == FORT || this == CASTLE || this == CITY || this == WALL) {
				cost *= 2;
			}
			break;

		default:
			break;
		}
		return cost;
	}

	public float getTalentBonus(String talentId) {
		return BettleTalentTileRegistry.getTalentBonus(talentId, this);
	}
}
