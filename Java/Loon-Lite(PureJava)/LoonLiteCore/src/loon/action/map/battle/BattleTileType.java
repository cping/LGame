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
import loon.action.map.battle.BattleType.UnitType;
import loon.utils.IntMap;

/**
 * 主要供战棋或回合制游戏使用的地形参数类(具体地形的瓦片属性只是模板，具体多少可自行修正或建立新瓦片类型)
 */
public class BattleTileType {

	private static final IntMap<BattleTileType> TILE_MAP = new IntMap<BattleTileType>();

	public final static int[] getIds() {
		return TILE_MAP.keys();
	}

	public final static BattleTileType getById(int id) {
		return TILE_MAP.get(id);
	}

	public final static void putTileType(int id, BattleTileType tileType) {
		TILE_MAP.put(id, tileType);
	}

	// 基础地形
	// 未知
	public static final BattleTileType UNKNOWN = new BattleTileType(0, 0, MoveState.NORMAL, "UNKNOWN", 1, 0, 0, false,
			false, 0, 0f, 0.0f);
	// 平原
	public static final BattleTileType PLAIN = new BattleTileType(1, 1, MoveState.NORMAL, "PLAIN", 1, 0, 0, true, false,
			1, 1.0f, 0.0f);
	// 草地
	public static final BattleTileType GRASSLAND = new BattleTileType(2, 2, MoveState.NORMAL, "GRASSLAND", 1, 0, 0,
			true, false, 1, 1.0f, 0.0f);
	// 沙漠
	public static final BattleTileType DESERT = new BattleTileType(3, 3, MoveState.DIFFICULT, "DESERT", 0.8f, 0, 0,
			true, false, 2, 1.1f, 0.1f);
	// 森林
	public static final BattleTileType FOREST = new BattleTileType(4, 4, MoveState.NORMAL, "FOREST", 0.7f, 0, 0, true,
			true, 2, 0.9f, 0.2f);
	// 密林
	public static final BattleTileType DENSE_FOREST = new BattleTileType(5, 5, MoveState.DIFFICULT, "DENSE_FOREST",
			0.5f, 0, 0, true, true, 3, 0.8f, 0.3f);
	// 山地
	public static final BattleTileType MOUNTAIN = new BattleTileType(6, 6, MoveState.CLIMB, "MOUNTAIN", 0.5f, 0, 0,
			false, true, 3, 0.7f, 0.5f);
	// 丘陵
	public static final BattleTileType HILL = new BattleTileType(7, 7, MoveState.CLIMB, "HILL", 0.5f, 0, 0, true, true,
			2, 0.9f, 0.25f);
	// 河流
	public static final BattleTileType RIVER = new BattleTileType(8, 8, MoveState.SWIM, "RIVER", 0.7f, 0, 0, true,
			false, 2, 1.0f, 0.0f);
	// 浅滩
	public static final BattleTileType FORD = new BattleTileType(9, 9, MoveState.NORMAL, "FORD", 0.9f, 0, 0, true,
			false, 3, 1.0f, 0.0f);
	// 沙洲
	public static final BattleTileType SHOAL = new BattleTileType(10, 10, MoveState.DIFFICULT, "SHOAL", 0.85f, 0, 0,
			true, false, 1, 1.0f, 0.0f);
	// 海洋
	public static final BattleTileType SEA = new BattleTileType(11, 11, MoveState.SWIM, "SEA", 0.6f, 0, 0, true, false,
			2, 1.0f, 0.0f);
	// 海岸
	public static final BattleTileType COAST = new BattleTileType(12, 12, MoveState.NORMAL, "COAST", 0.9f, 0, 0, true,
			false, 1, 1.0f, 0.0f);
	// 沼泽
	public static final BattleTileType SWAMP = new BattleTileType(13, 13, MoveState.DIFFICULT, "SWAMP", 0.2f, 0, 0,
			true, false, 3, 0.8f, 0.1f);
	// 湿地
	public static final BattleTileType MARSH = new BattleTileType(14, 14, MoveState.DIFFICULT, "MARSH", 0.6f, 0, 0,
			true, false, 4, 0.7f, 0.15f);
	// 火山
	public static final BattleTileType VOLCANO = new BattleTileType(15, 15, MoveState.FLY, "VOLCANO", 0.3f, 0, 0, false,
			true, 5, 0.0f, 0.0f);
	// 断崖
	public static final BattleTileType CLIFF = new BattleTileType(16, 16, MoveState.CLIMB, "CLIFF", 0.1f, 0, 0, false,
			true, 4, 0.0f, 0.0f);
	// 天空
	public static final BattleTileType SKY = new BattleTileType(17, 17, MoveState.FLY, "SKY", 1f, 0, 0, false, true, 4,
			0.0f, 0.0f);
	// 雪地
	public static final BattleTileType SNOW = new BattleTileType(18, 18, MoveState.DIFFICULT, "SNOW", 0.7f, 0, 0, true,
			true, 2, 0.9f, 0.3f);

	// 冰川
	public static final BattleTileType GLACIER = new BattleTileType(19, 19, MoveState.DIFFICULT, "GLACIER", 0.3f, 0, 0,
			false, true, 4, 0.7f, 0.5f);

	// 荒原
	public static final BattleTileType WASTELAND = new BattleTileType(20, 20, MoveState.NORMAL, "WASTELAND", 1.0f, 0, 0,
			true, false, 1, 1.0f, 0.0f);

	// 熔岩地带
	public static final BattleTileType LAVA_FIELD = new BattleTileType(21, 21, MoveState.FLY, "LAVA_FIELD", 0.2f, 0, 0,
			false, false, 5, 0.5f, 0.0f);

	// 草原
	public static final BattleTileType STEPPE = new BattleTileType(22, 22, MoveState.NORMAL, "STEPPE", 1.2f, 0, 0, true,
			false, 1, 1.0f, 0.0f);
	// 绿洲
	public static final BattleTileType OASIS = new BattleTileType(23, 23, MoveState.NORMAL, "OASIS", 1.0f, 0, 0, true,
			true, 1, 1.0f, 0.4f);

	// 废墟
	public static final BattleTileType RUINS = new BattleTileType(24, 24, MoveState.NORMAL, "RUINS", 0.9f, 0, 0, true,
			true, 2, 0.8f, 0.4f);

	// 建筑地形
	// 道路
	public static final BattleTileType ROAD = new BattleTileType(25, 25, MoveState.NORMAL, "ROAD", 1, 0, 0, true, false,
			1, 1.0f, 0.0f);
	// 桥梁
	public static final BattleTileType BRIDGE = new BattleTileType(26, 26, MoveState.NORMAL, "BRIDGE", 1, 0, 0, true,
			false, 1, 1.0f, 0.0f);
	// 砦
	public static final BattleTileType FORT = new BattleTileType(27, 27, MoveState.NORMAL, "FORT", 1, 0, 0, true, true,
			1, 0.8f, 0.4f);
	// 城塞
	public static final BattleTileType CASTLE = new BattleTileType(28, 28, MoveState.NORMAL, "CASTLE", 1, 0, 0, true,
			true, 1, 0.7f, 0.6f);
	// 城池
	public static final BattleTileType CITY = new BattleTileType(29, 29, MoveState.NORMAL, "CITY", 1, 0, 0, true, true,
			1, 0.6f, 0.8f);
	// 城墙
	public static final BattleTileType WALL = new BattleTileType(30, 30, MoveState.NORMAL, "WALL", 1, 0, 0, true, true,
			2, 0.7f, 0.7f);
	// 城门
	public static final BattleTileType GATE = new BattleTileType(31, 31, MoveState.NORMAL, "GATE", 1, 0, 0, true, true,
			1, 0.7f, 0.7f);
	// 岗哨
	public static final BattleTileType TOWER = new BattleTileType(32, 32, MoveState.NORMAL, "TOWER", 1, 0, 0, true,
			true, 1, 0.8f, 0.5f);
	// 木栅
	public static final BattleTileType PALISADE = new BattleTileType(33, 33, MoveState.NORMAL, "PALISADE", 1, 0, 0,
			true, true, 1, 0.85f, 0.3f);
	// 渡口
	public static final BattleTileType FERRY = new BattleTileType(34, 34, MoveState.NORMAL, "FERRY", 1, 0, 0, true,
			false, 2, 1.0f, 0.0f);
	// 港口
	public static final BattleTileType PORT = new BattleTileType(35, 35, MoveState.NORMAL, "PORT", 1, 0, 0, true, false,
			1, 1.0f, 0.0f);
	// 堤坝
	public static final BattleTileType DAM = new BattleTileType(36, 36, MoveState.NORMAL, "DAM", 1, 0, 0, true, false,
			2, 1.0f, 0.0f);
	// 矿坑
	public static final BattleTileType MINE = new BattleTileType(37, 37, MoveState.NORMAL, "MINE", 1, 0, 0, true, false,
			2, 1.0f, 0.0f);
	// 农田
	public static final BattleTileType FARM = new BattleTileType(38, 38, MoveState.NORMAL, "FARM", 1, 0, 0, true, false,
			1, 1.0f, 0.0f);
	// 市场
	public static final BattleTileType MARKET = new BattleTileType(39, 39, MoveState.NORMAL, "MARKET", 1, 0, 0, true,
			false, 1, 1.0f, 0.0f);
	// 唯一标识（地形类型）
	private final int id;
	// 用于和其它物体绑定
	private int bindingId;
	// 地形名称
	private final String name;
	// 是否可通行
	private final boolean passable;
	// 是否有防御加成
	private final boolean defensive;
	// 基础行动点消耗
	private final int baseActionCost;
	// 攻击倍率
	private float attackMultiplier;
	// 防御加成
	private final float defenseBonus;
	// 移动速度倍率
	public float moveSpeedMultiplier;
	// 宽度偏移
	public int widthOffset;
	// 高度偏移
	public int heightOffset;
	// 该地形默认移动状态
	public MoveState defaultMoveState;

	private BattleTileType(int id, int bindingId, MoveState state, String name, float multiplier, int widthOffset,
			int heightOffset, boolean passable, boolean defensive, int baseActionCost, float attackMultiplier,
			float defenseBonus) {
		this.id = id;
		this.bindingId = bindingId;
		this.defaultMoveState = state;
		this.name = name;
		this.moveSpeedMultiplier = multiplier;
		this.widthOffset = widthOffset;
		this.heightOffset = heightOffset;
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

	public float getMoveSpeedMultiplier() {
		return moveSpeedMultiplier;
	}

	public int getWidthOffset() {
		return widthOffset;
	}

	public int getHeightOffset() {
		return heightOffset;
	}

	public MoveState getDefaultMoveState() {
		return defaultMoveState;
	}

	public void setBindingId(int bindingId) {
		this.bindingId = bindingId;
	}

	public void setAttackMultiplier(float attackMultiplier) {
		this.attackMultiplier = attackMultiplier;
	}

	public void setMoveSpeedMultiplier(float moveSpeedMultiplier) {
		this.moveSpeedMultiplier = moveSpeedMultiplier;
	}

	public void setWidthOffset(int widthOffset) {
		this.widthOffset = widthOffset;
	}

	public void setHeightOffset(int heightOffset) {
		this.heightOffset = heightOffset;
	}

	public void setDefaultMoveState(MoveState defaultMoveState) {
		this.defaultMoveState = defaultMoveState;
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
