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
import loon.action.map.Field2D;
import loon.action.map.battle.BattleMap;
import loon.action.map.battle.BattleMapGenerator;
import loon.action.map.battle.BattleMapObject;
import loon.action.map.battle.BattlePathFinder.PathResult;
import loon.action.map.battle.BattleTileMake;
import loon.action.map.battle.BattleTileMake.TileAnimation;
import loon.action.map.battle.BattleTileType;
import loon.action.sprite.Animation;
import loon.canvas.LColor;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.opengl.TextureUtils;
import loon.utils.ISOUtils.IsoConfig;

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
		
		// 拖拽地图
		drag((x, y) -> {
			newMap.scroll(x, y);
		});
		// 获得点中瓦片坐标
		up((x, y) -> {
			System.out.println(newMap.findTileXY(x, y));
		});

		add(newMap);
	}

}
