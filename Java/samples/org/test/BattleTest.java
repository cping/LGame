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
package org.test;

import loon.LTexture;
import loon.Stage;
import loon.action.map.Direction;
import loon.action.map.Field2D;
import loon.action.map.TileIsoHighlighter;
import loon.action.map.TileIsoHighlighter.EffectType;
import loon.action.map.battle.BattleMap;
import loon.action.map.battle.BattleMapGenerator;
import loon.action.map.battle.BattleMapObject;
import loon.action.map.battle.BattlePathFinder.PathResult;
import loon.action.map.battle.BattleTileMake;
import loon.action.map.battle.BattleTileMake.TileAnimation;
import loon.action.map.battle.BattleTileType;
import loon.action.map.battle.BattleMovementManager.CollisionResponse;
import loon.action.map.battle.BattleMovementManager.MovementListener;
import loon.action.map.battle.BattleMovementManager.MovementMode;
import loon.action.map.battle.BattleMovementManager.MovementState;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.map.battle.BattleType.RangeType;
import loon.action.sprite.Animation;
import loon.action.sprite.AnimationManager;
import loon.action.sprite.AnimationRenderer;
import loon.canvas.LColor;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.TextureUtils;
import loon.utils.Easing;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.TArray;

/**
 * 战斗用地图测试用类
 */
public class BattleTest extends Stage {

	@Override
	public void create() {
		setBackground(LColor.red);
		// 构建斜视地图的基础设置
		IsoConfig config = IsoConfig.defaultConfig();
		// 瓦片大小32x32
		config.setTileSize(32, 32);
		// 显示时按照缩放2倍处理
		// config.setScale(2f);
		// 构建一个随机地图，瓦片数量32x24
		BattleMapGenerator generator = new BattleMapGenerator(32, 24);

		// 只启用平原、草地、森林、山地、绿洲
		generator.setActiveTileTypes(BattleTileType.PLAIN.getId(), BattleTileType.GRASSLAND.getId(),
				BattleTileType.FOREST.getId(), BattleTileType.MOUNTAIN.getId(), BattleTileType.OASIS.getId());

		// 设置权重：平原和草地多一些，森林和山地少一些
		generator.setTileWeight(BattleTileType.PLAIN.getId(), 2.0f);
		generator.setTileWeight(BattleTileType.GRASSLAND.getId(), 2.0f);
		generator.setTileWeight(BattleTileType.FOREST.getId(), 1.5f);
		generator.setTileWeight(BattleTileType.MOUNTAIN.getId(), 1.5f);
		generator.setTileWeight(BattleTileType.OASIS.getId(), 1.5f);

		// 使用PerlinNoise生成地图
		generator.generate(12345, 4, 0.5f, 0.1f, 3.0f);

		// 获得随机地图与索引id关系的二维数组
		int[][] maps = generator.getMapGrid();

		GameEventBus<GameEventType> bus = new GameEventBus<>();
		// int[][] maps = TileMapConfig.loadAthwartArray("battle.txt");

		Field2D map = new Field2D(maps, 32, 32);

		// 构建一个简易的 BattleTileMake 类，用于绑定斜视瓦片与地图的映射关系(非必须，只是为了省事避免自己做地图时有用……)
		BattleTileMake make = new BattleTileMake();
		// 拆分图片为纹理数组
		LTexture[] splits = TextureUtils.getSplitTextures("spritesheet.png", 0, 0, 32, 32, 0, 1);

		// 构建一个动画用于平原瓦片
		Animation plain = new Animation();
		plain.addFrame(splits[22], 1000);
		plain.addFrame(splits[23], 1000);
		plain.addFrame(splits[24], 1000);
		TileAnimation plainAni = new TileAnimation(plain);
		make.putTile(BattleTileType.PLAIN.getId(), plainAni);

		Animation grassland = new Animation();
		grassland.addFrame(splits[40], 1000);
		TileAnimation grasslandAni = new TileAnimation(grassland);
		make.putTile(BattleTileType.GRASSLAND.getId(), grasslandAni);

		TileAnimation forestAni = new TileAnimation(splits[36]);
		make.putTile(BattleTileType.FOREST.getId(), forestAni);

		TileAnimation mountainAni = new TileAnimation(splits[11]);
		make.putTile(BattleTileType.MOUNTAIN.getId(), mountainAni);

		TileAnimation oasisAni = new TileAnimation(splits[51]);
		make.putTile(BattleTileType.OASIS.getId(), oasisAni);

		// 构建一个战斗用地图精灵(本身是一个基于数组构建的斜视地图，但是绑定了现成的战斗系统，作用是一些沙盒游戏或者策略游戏的直接构建,传参可用)
		BattleMap newMap = new BattleMap(make, map, this, bus, config);
		// 实际构建地图(因为构建方式可能不同，所以不会自动构建。比如目前演示为随机地图，但也允许直接注入BattleTile数组制作固定地图,精确设定每块瓦片的图片样式参数等)
		newMap.createMap(new GameEventBus<PathResult>(), new GameEventBus<BattleMapObject>());
		// 获得瓦片高亮特效控制器
		// TileIsoHighlighter highlighter= newMap.getTileHighlighter();

		// newMap.setOtherGrid(true);
		// newMap.setDrawGrid(true);

		// 加载json动画配置资源到纹理动画管理器，不绑定任何对象(若绑定具体战斗对象或事件，则在执行对应命令时会回调触发)
		AnimationManager mang = new AnimationManager("battleRole0.json", null, null);

		// 插入图层IDLE标识动画，同时添加动作标识LEFT和RIGHT(任何状态标识都是json中设置了才能执行，没有对应名称则无效，另外此处使用字符串或者枚举类型的效果一样，只是用枚举方便用我自定义的模板，若自定义字符串则只能自行定义具体触发的事件)
		mang.addLayerDirs(ObjectState.IDLE);

		mang.setState("IDLE", Direction.DOWN_LEFT);

		// 构建并获得一个战斗地图对象，瓦片坐标位置1,4，大小64x64，使用AnimationManager控制动画，监控所有移动相关动作
		BattleMapObject obj = newMap.addMapObject(1, 4, 64, 64, "role0", mang, new MovementListener() {

			@Override
			public void onTileEntered(BattleMapObject o, int mapX, int mapY) {

			}

			@Override
			public void onTerrainEffectApplied(BattleMapObject o, String terrain, BattleTileType effect) {

			}

			@Override
			public void onTerrainCostDeducted(BattleMapObject o, int cost, int remainingPoints) {

			}

			@Override
			public void onStepReached(BattleMapObject o, int mapX, int mapY) {

			}

			@Override
			public void onStateExpired(BattleMapObject o, MovementState state) {

			}

			@Override
			public void onStateCooldown(BattleMapObject o, MovementState state) {

			}

			@Override
			public void onSpeedChanged(BattleMapObject o, float newSpeed) {

			}

			@Override
			public void onPathUpdated(BattleMapObject o, TArray<PointI> newPath) {

			}

			@Override
			public void onPathResumed(BattleMapObject o) {

			}

			@Override
			public void onPathInterrupted(BattleMapObject o) {

			}

			@Override
			public void onPathCompleted(BattleMapObject o) {
				// 移动完毕后，清空瓦片高亮设置
				newMap.clearHighlighterEffect();
			}

			@Override
			public void onMovementPointChanged(BattleMapObject o, int remainingPoints) {

			}

			@Override
			public void onMovementModeChanged(BattleMapObject o, MovementMode oldMode, MovementMode newMode) {

			}

			@Override
			public void onEasingChanged(BattleMapObject o, Easing newEasing) {

			}

			@Override
			public void onDirectionChanged(BattleMapObject o, Direction newDirection) {
				// 改变当前动画角色方向，为移动检测的方向(PS:loon中的Direction为类，而不是枚举，所以允许自定义，但若自定了特殊方向名称对应的动画，此处需要自行匹配动画切换)
				mang.setState("IDLE", Direction.toDisplayDirection(newDirection));
			}

			@Override
			public void onCollision(BattleMapObject self, BattleMapObject other, CollisionResponse response) {

			}

			@Override
			public void onAnimationStateChanged(BattleMapObject o, String state) {

			}
		});

		// 拖拽地图
		drag((x, y) -> {
			newMap.scroll(x, y);
		});
		// 获得点中瓦片坐标
		up((x, y) -> {
			// 以像素坐标,获得实际瓦片坐标
			// Vector2f pos = newMap.findTileXY(x, y);
			// 请求指定地图对象到指定像素坐标的寻径，并返回瓦片坐标的寻径结果
			TArray<PointI> result = newMap.findObjectMovePathToTile(obj, x, y);
			if (result != null && result.size > 0) {
				// 将移动路径以默认的move色彩高亮显示在地图上
				newMap.highlighterRangePathToEffect(result, EffectType.MOVE);

				// 改变缓动动画效果
				// obj.setEasing(Easing.BACK_IN_OUT);
				// 刷新状态，重置移动点数(loon的战斗地图引擎内置有移动点限制设定，移动点耗尽强制不可移动,所以设置移动前必须刷新)
				obj.resetPathState();
				// 设置移动路径
				obj.setPath(result);

				// 上两步合一用此函数，为说明运行逻辑故此不调用
				// obj.setResetPath(result);
			}
			// 产生一个圆形，范围大小为3，颜色象征攻击状态的高亮区域
			// newMap.highlighterRange(pos.x(), pos.y(), RangeType.CIRCLE, 3,
			// EffectType.ATTACK);
		});

		add(newMap);
	}

}
