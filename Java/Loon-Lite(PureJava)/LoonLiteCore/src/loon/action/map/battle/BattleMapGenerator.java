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

import loon.LRelease;
import loon.action.collision.CollisionHelper;
import loon.geom.PerlinNoise;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.TArray;

/**
 * 战斗用随机地图生成用类
 * (本质上这个类只是用于生成与BattileTile索引id绑定的二维数组,也可用于其它地图,可以将其视作单纯的随机地图生成器，并非只能用于战斗地图)
 */
public class BattleMapGenerator implements LRelease {

	public static class MapGenerationConfig {
		// 基本参数
		public int width;
		public int height;

		// 噪声参数
		public int seed;
		public int octaves;
		public float persistence;
		public float frequency;
		public float amplitude;

		// 权重分布
		public IntMap<Float> tileWeights = new IntMap<Float>();

		// 相邻规则开关
		public boolean useAdjacencyRules = true;

		// 嵌套区域参数
		public int nestedRegionCount = 0;

		public ObjectMap<String, Float> shapeRatio = new ObjectMap<String, Float>();

		public IntMap<Float> nestedTileWeights = new IntMap<Float>();

		public MapGenerationConfig(int width, int height) {
			this.width = width;
			this.height = height;
		}
	}

	private IntArray tileIds = new IntArray();

	private float[] cumulativeWeights;

	private float totalWeight = 0f;

	private int width;

	private int height;

	private int[][] mapGrid;

	// 可用地形集合及权重
	private IntMap<Float> activeTileWeights = new IntMap<Float>();

	// 是否启用默认相邻规则
	private boolean useAdjacencyRules = true;

	// 默认相邻规则表
	private static final IntMap<int[]> ADJACENCY_RULES = new IntMap<int[]>();

	public BattleMapGenerator(int width, int height) {
		this.width = width;
		this.height = height;
		this.mapGrid = new int[height][width];
		// 默认启用所有地形，权重为1
		int[] tileIds = BattleTileType.getIds();
		for (int i = 0; i < tileIds.length; i++) {
			activeTileWeights.put(tileIds[i], Float.valueOf(1.0f));
		}
		// 初始化邻接规则
		generateAdjacencyRules();
		// 检查并修复邻接规则一致性
		fixAdjacencyConsistency();
	}

	/**
	 * 自动生成邻接规则
	 */
	private void generateAdjacencyRules() {
		// 自然地形组
		int[] natural = { BattleTileType.PLAIN.getId(), BattleTileType.GRASSLAND.getId(), BattleTileType.DESERT.getId(),
				BattleTileType.FOREST.getId(), BattleTileType.DENSE_FOREST.getId(), BattleTileType.MOUNTAIN.getId(),
				BattleTileType.HILL.getId(), BattleTileType.SNOW.getId(), BattleTileType.GLACIER.getId(),
				BattleTileType.WASTELAND.getId(), BattleTileType.OASIS.getId(), BattleTileType.VOLCANO.getId(),
				BattleTileType.CLIFF.getId(), BattleTileType.RUINS.getId() };

		// 水系地形组
		int[] water = { BattleTileType.RIVER.getId(), BattleTileType.FORD.getId(), BattleTileType.SHOAL.getId(),
				BattleTileType.SEA.getId(), BattleTileType.COAST.getId(), BattleTileType.SWAMP.getId(),
				BattleTileType.MARSH.getId(), BattleTileType.FERRY.getId(), BattleTileType.PORT.getId(),
				BattleTileType.DAM.getId() };

		// 建筑地形组
		int[] buildings = { BattleTileType.ROAD.getId(), BattleTileType.BRIDGE.getId(), BattleTileType.FORT.getId(),
				BattleTileType.CASTLE.getId(), BattleTileType.CITY.getId(), BattleTileType.WALL.getId(),
				BattleTileType.GATE.getId(), BattleTileType.TOWER.getId(), BattleTileType.PALISADE.getId(),
				BattleTileType.MINE.getId(), BattleTileType.FARM.getId(), BattleTileType.MARKET.getId() };

		// 特殊地形组
		int[] special = { BattleTileType.SKY.getId(), BattleTileType.LAVA_FIELD.getId() };

		// 自动规则生成：同组互相邻接
		for (int tile : natural) {
			ADJACENCY_RULES.put(tile, natural);
		}
		for (int tile : water) {
			ADJACENCY_RULES.put(tile, water);
		}
		for (int tile : buildings) {
			ADJACENCY_RULES.put(tile, buildings);
		}
		for (int tile : special) {
			ADJACENCY_RULES.put(tile, special);
		}

		// 跨组规则示例
		ADJACENCY_RULES.put(BattleTileType.COAST.getId(), new int[] { BattleTileType.SEA.getId(),
				BattleTileType.PLAIN.getId(), BattleTileType.GRASSLAND.getId(), BattleTileType.PORT.getId() });

		ADJACENCY_RULES.put(BattleTileType.RIVER.getId(), new int[] { BattleTileType.GRASSLAND.getId(),
				BattleTileType.FORD.getId(), BattleTileType.BRIDGE.getId(), BattleTileType.DAM.getId() });

		ADJACENCY_RULES.put(BattleTileType.CITY.getId(), new int[] { BattleTileType.ROAD.getId(),
				BattleTileType.MARKET.getId(), BattleTileType.FARM.getId(), BattleTileType.CASTLE.getId() });
	}

	/**
	 * 获取某个地形的邻接规则
	 */
	public static int[] getAdjacency(int tileId) {
		int[] result = ADJACENCY_RULES.get(tileId);
		return result != null ? result : new int[0];
	}

	/**
	 * 邻接规则修复器 自动补全单向邻接关系
	 */
	private void fixAdjacencyConsistency() {
		IntMap.Entry<int[]>[] entrys = ADJACENCY_RULES.getEntrys();
		for (IntMap.Entry<int[]> entry : entrys) {
			int tileId = entry.getKeyInt();
			int[] neighbors = entry.getValue();
			for (int neighborId : neighbors) {
				int[] reverseNeighbors = ADJACENCY_RULES.get(neighborId);
				if (reverseNeighbors == null) {
					// 如果邻接表不存在，创建并补全
					ADJACENCY_RULES.put(neighborId, new int[] { tileId });
				} else {
					boolean found = false;
					for (int n : reverseNeighbors) {
						if (n == tileId) {
							found = true;
							break;
						}
					}
					if (!found) {
						int[] newNeighbors = new int[reverseNeighbors.length + 1];
						System.arraycopy(reverseNeighbors, 0, newNeighbors, 0, reverseNeighbors.length);
						newNeighbors[reverseNeighbors.length] = tileId;
						ADJACENCY_RULES.put(neighborId, newNeighbors);
					}
				}
			}
		}
	}

	public void generateWithConfig(MapGenerationConfig config) {
		this.width = config.width;
		this.height = config.height;
		this.mapGrid = new int[height][width];
		this.useAdjacencyRules = config.useAdjacencyRules;
		// 设置权重
		activeTileWeights.clear();
		activeTileWeights.putAll(config.tileWeights);
		rebuildWeightCache();
		// 使用噪声生成
		generate(config.seed, config.octaves, config.persistence, config.frequency, config.amplitude);
		// 如果需要嵌套区域
		if (config.nestedRegionCount > 0) {
			generateParameterizedNestedRegions(config.nestedRegionCount, config.shapeRatio, config.nestedTileWeights);
		}
	}

	/**
	 * 设置启用的地形类型（若此项设置，则地形生成将只生成包含此处设定的索引id的地形)
	 * 
	 * @param tileIds
	 */
	public void setActiveTileTypes(int... tileIds) {
		setActiveTileTypes(1f, tileIds);
	}

	/**
	 * 设置启用的地形类型（若此项设置，则地形生成将只生成包含索引id的地形)
	 * 
	 * @param weight
	 * @param tileIds
	 */
	public void setActiveTileTypes(float weight, int... tileIds) {
		activeTileWeights.clear();
		for (int id : tileIds) {
			activeTileWeights.put(id, Float.valueOf(weight));
		}
		rebuildWeightCache();
	}

	/**
	 * 设置某个地形的权重（也就是出现概率，权重越高，随机出现概率越高）
	 * 
	 * @param id
	 * @param weight
	 */
	public void setTileWeight(int id, float weight) {
		if (weight > 0) {
			activeTileWeights.put(id, Float.valueOf(weight));
			rebuildWeightCache();
		}
	}

	/**
	 * 此项为false,则不用PerlinNoise,而按照固定规则生成地图
	 * 
	 * @param useRules
	 */
	public void setUseAdjacencyRules(boolean useRules) {
		this.useAdjacencyRules = useRules;
	}

	/**
	 * 使用PerlinNoise算法作为噪点基础，生成随机地图
	 * 
	 * @param seed
	 * @param octaves
	 * @param persistence
	 * @param frequency
	 * @param amplitude
	 */
	public void generate(int seed, int octaves, float persistence, float frequency, float amplitude) {
		rebuildWeightCache();
		PerlinNoise noise = new PerlinNoise(seed, persistence, frequency, amplitude, octaves);
		IntArray appearedTiles = new IntArray();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float value = noise.getHeight(x, y);
				int tile = pickTileByNoise(value);
				mapGrid[y][x] = tile;
				appearedTiles.add(tile);
			}
		}
		for (int i = 0; i < tileIds.length; i++) {
			int id = tileIds.get(i);
			if (!appearedTiles.contains(id)) {
				int count = MathUtils.random(1, 3);
				for (int j = 0; j < count; j++) {
					int rx = MathUtils.random(width - 1);
					int ry = MathUtils.random(height - 1);
					mapGrid[ry][rx] = id;
				}
			}
		}
		smoothMap();
	}

	/**
	 * 重置地图噪点权重缓存
	 */
	private void rebuildWeightCache() {
		tileIds.clear();
		cumulativeWeights = new float[activeTileWeights.size()];
		totalWeight = 0f;
		int i = 0;
		IntMap.Entry<Float>[] entrys = activeTileWeights.getEntrys();
		for (IntMap.Entry<Float> entry : entrys) {
			totalWeight += entry.getValue();
			tileIds.add(entry.getKeyInt());
			cumulativeWeights[i] = totalWeight;
			i++;
		}
	}

	/**
	 * 根据噪点值选择地形
	 * 
	 * @param value
	 * @return
	 */
	private int pickTileByNoise(float value) {
		if (activeTileWeights.isEmpty()) {
			return BattleTileType.UNKNOWN.getId();
		}
		// 噪声值范围大约在 -1 ~ 1，把它归一化到 0 ~ 1
		float normalized = (value + 1f) / 2f;
		normalized = MathUtils.clamp(normalized, 0f, 1f);
		// 映射到权重区间
		float target = normalized * totalWeight;
		int low = 0, high = cumulativeWeights.length - 1;
		while (low < high) {
			int mid = (low + high) / 2;
			if (target <= cumulativeWeights[mid]) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}
		return tileIds.get(low);
	}

	/**
	 * 按照基础规则平滑地图
	 */
	private void smoothMap() {
		// 如果关闭规则，直接跳过
		if (!useAdjacencyRules) {
			return;
		}
		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int current = mapGrid[y][x];
				final int[] neighbors = { mapGrid[y - 1][x], mapGrid[y + 1][x], mapGrid[y][x - 1], mapGrid[y][x + 1] };
				boolean valid = false;
				if (ADJACENCY_RULES.containsKey(current)) {
					for (int n : neighbors) {
						for (int allowed : ADJACENCY_RULES.get(current)) {
							if (n == allowed) {
								valid = true;
								break;
							}
						}
					}
				}
				if (!valid && ADJACENCY_RULES.containsKey(current)) {
					int[] allowed = ADJACENCY_RULES.get(current);
					mapGrid[y][x] = allowed[(int) (MathUtils.random() * allowed.length)];
				}
			}
		}
	}

	/**
	 * 填充指定矩形区域索引id(即在随机地图中，加入特定范围，特性形状的固定地形)
	 * 
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param tileId
	 */
	public void fillRegion(int startX, int startY, int endX, int endY, int tileId) {
		for (int y = startY; y <= endY && y < height; y++) {
			for (int x = startX; x <= endX && x < width; x++) {
				mapGrid[y][x] = tileId;
			}
		}
	}

	/**
	 * 填充指定圆形区域索引id(即在随机地图中，加入特定范围，特性形状的固定地形)
	 * 
	 * @param centerX
	 * @param centerY
	 * @param radius
	 * @param tileId
	 */
	public void fillCircle(float centerX, float centerY, float radius, int tileId) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int dx = (int) (x - centerX);
				int dy = (int) (y - centerY);
				float dist = MathUtils.sqrt(dx * dx + dy * dy);
				if (dist <= radius) {
					mapGrid[y][x] = tileId;
				}
			}
		}
	}

	/**
	 * 填充指定三角形区域索引id(即在随机地图中，加入特定范围，特性形状的固定地形)
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @param tileId
	 */
	public void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int tileId) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (CollisionHelper.checkPointvsTriangle(x, y, x1, y1, x2, y2, x3, y3)) {
					mapGrid[y][x] = tileId;
				}
			}
		}
	}

	/**
	 * 填充指定多边形区域索引id(即在随机地图中，加入特定范围，特性形状的固定地形)
	 * 
	 * @param xs
	 * @param ys
	 * @param tileId
	 */
	public void fillPolygon(float[] xs, float[] ys, int tileId) {
		if (xs.length != ys.length || xs.length < 3) {
			throw new IllegalArgumentException(
					"A polygon must have at least three vertices, and the x and y arrays must have the same length.");
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (CollisionHelper.checkPointvsPolygon(x, y, xs, ys)) {
					mapGrid[y][x] = tileId;
				}
			}
		}
	}

	/**
	 * 在一个区域内嵌套另一个区域
	 * 
	 * @param shape
	 * @param xsOuter
	 * @param ysOuter
	 * @param xsInner
	 * @param ysInner
	 * @param outerTileId
	 * @param innerTileId
	 */
	public void fillNestedRegion(String shape, float[] xsOuter, float[] ysOuter, float[] xsInner, float[] ysInner,
			int outerTileId, int innerTileId) {
		if ("polygon".equalsIgnoreCase(shape)) {
			fillPolygon(xsOuter, ysOuter, outerTileId);
			fillPolygon(xsInner, ysInner, innerTileId);
		} else if ("triangle".equalsIgnoreCase(shape)) {
			fillTriangle(xsOuter[0], ysOuter[0], xsOuter[1], ysOuter[1], xsOuter[2], ysOuter[2], outerTileId);
			fillTriangle(xsInner[0], ysInner[0], xsInner[1], ysInner[1], xsInner[2], ysInner[2], innerTileId);
		} else if ("circle".equalsIgnoreCase(shape)) {
			fillCircle(xsOuter[0], ysOuter[0], xsOuter[1], outerTileId);
			fillCircle(xsInner[0], ysInner[0], xsInner[1], innerTileId);
		}
	}

	/**
	 * 随机嵌套生成复杂地形区域(也就是随机生成大块的固定地形，不同样式的大块地形是固定的，但具体生成什么是随机的)
	 */
	public void generateRandomNestedRegion() {
		// 随机选择形状类型
		String[] shapes = { "circle", "triangle", "polygon" };
		String shape = shapes[MathUtils.random(0, shapes.length)];
		// 随机选择外层和内层地形
		IntArray ids = new IntArray(activeTileWeights.keys());
		if (ids.isEmpty()) {
			ids.add(BattleTileType.PLAIN.getId());
			ids.add(BattleTileType.SEA.getId());
			ids.add(BattleTileType.MOUNTAIN.getId());
		}
		int outerTileId = ids.get(MathUtils.nextInt(ids.size() - 1));
		int innerTileId = ids.get(MathUtils.nextInt(ids.size() - 1));
		// 随机生成坐标
		if ("circle".equals(shape)) {
			int cx = width / 2;
			int cy = height / 2;
			int outerRadius = MathUtils.min(width, height) / 4;
			int innerRadius = outerRadius / 2;
			fillCircle(cx, cy, outerRadius, outerTileId);
			fillCircle(cx, cy, innerRadius, innerTileId);
		} else if ("triangle".equals(shape)) {
			int x1 = MathUtils.nextInt(width / 2);
			int y1 = MathUtils.nextInt(height / 2);
			int x2 = x1 + MathUtils.nextInt(width / 3);
			int y2 = y1 + MathUtils.nextInt(height / 3);
			int x3 = x1 + MathUtils.nextInt(width / 3);
			int y3 = y1 + MathUtils.nextInt(height / 3);
			fillTriangle(x1, y1, x2, y2, x3, y3, outerTileId);
			fillTriangle(x1 + 5, y1 + 5, x2 - 5, y2 - 5, x3 - 5, y3 - 5, innerTileId);
		} else if ("polygon".equals(shape)) {
			float[] xsOuter = { 20, 40, 60, 50, 30 };
			float[] ysOuter = { 20, 10, 20, 40, 40 };
			float[] xsInner = { 30, 40, 35 };
			float[] ysInner = { 20, 20, 30 };
			fillPolygon(xsOuter, ysOuter, outerTileId);
			fillPolygon(xsInner, ysInner, innerTileId);
		}
	}

	/**
	 * 随机嵌套区域生成
	 * 
	 * @param count        生成区域数量
	 * @param shapeWeights 形状比例，例如 {"circle":0.5, "triangle":0.3, "polygon":0.2}
	 * @param tileWeights  地形比例，例如 {BattleTileType.PLAIN.getId():0.4,
	 *                     BattleTileType.SEA.getId():0.3,
	 *                     BattleTileType.MOUNTAIN.getId():0.3}
	 */
	public void generateParameterizedNestedRegions(int count, ObjectMap<String, Float> shapeWeights,
			IntMap<Float> tileWeights) {
		// 预计算形状权重
		TArray<String> shapes = new TArray<String>(shapeWeights.keys());
		float[] shapeCumulative = new float[shapes.size()];
		float totalShapeWeight = 0f;
		for (int i = 0; i < shapes.size(); i++) {
			totalShapeWeight += shapeWeights.get(shapes.get(i));
			shapeCumulative[i] = totalShapeWeight;
		}
		// 预计算地形权重
		IntArray tiles = new IntArray(tileWeights.keys());
		float[] tileCumulative = new float[tiles.size()];
		float totalTileWeight = 0f;
		for (int i = 0; i < tiles.size(); i++) {
			totalTileWeight += tileWeights.get(tiles.get(i));
			tileCumulative[i] = totalTileWeight;
		}
		// 批量生成
		for (int k = 0; k < count; k++) {
			// 随机选择形状
			float shapeTarget = MathUtils.random() * totalShapeWeight;
			String shape = shapes.get(binarySearch(shapeCumulative, shapeTarget));
			// 随机选择外层和内层地形
			float outerTarget = MathUtils.random() * totalTileWeight;
			int outerTileId = tiles.get(binarySearch(tileCumulative, outerTarget));
			float innerTarget = MathUtils.random() * totalTileWeight;
			int innerTileId = tiles.get(binarySearch(tileCumulative, innerTarget));
			// 随机位置
			int offsetX = MathUtils.nextInt(width - 1);
			int offsetY = MathUtils.nextInt(height - 1);
			if ("circle".equals(shape)) {
				int outerRadius = MathUtils.nextInt(MathUtils.min(width, height) / 5) + 5;
				int innerRadius = MathUtils.max(2, outerRadius / 2);
				fillCircle(offsetX, offsetY, outerRadius, outerTileId);
				fillCircle(offsetX, offsetY, innerRadius, innerTileId);
			} else if ("triangle".equals(shape)) {
				int x1 = offsetX;
				int y1 = offsetY;
				int x2 = x1 + MathUtils.nextInt(width / 5);
				int y2 = y1 + MathUtils.nextInt(height / 5);
				int x3 = x1 + MathUtils.nextInt(width / 5);
				int y3 = y1 + MathUtils.nextInt(height / 5);
				fillTriangle(x1, y1, x2, y2, x3, y3, outerTileId);
				fillTriangle(x1 + 2, y1 + 2, x2 - 2, y2 - 2, x3 - 2, y3 - 2, innerTileId);
			} else if ("polygon".equals(shape)) {
				float[] xsOuter = { offsetX, offsetX + MathUtils.nextInt(10), offsetX + MathUtils.nextInt(20),
						offsetX + MathUtils.nextInt(15), offsetX + MathUtils.nextInt(5) };
				float[] ysOuter = { offsetY, offsetY + MathUtils.nextInt(10), offsetY + MathUtils.nextInt(20),
						offsetY + MathUtils.nextInt(15), offsetY + MathUtils.nextInt(5) };
				float[] xsInner = { offsetX + 3, offsetX + 8, offsetX + 5 };
				float[] ysInner = { offsetY + 3, offsetY + 8, offsetY + 5 };
				fillPolygon(xsOuter, ysOuter, outerTileId);
				fillPolygon(xsInner, ysInner, innerTileId);
			}
		}
	}

	private int binarySearch(float[] cumulative, float target) {
		int low = 0, high = cumulative.length - 1;
		while (low < high) {
			int mid = (low + high) / 2;
			if (target <= cumulative[mid]) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}

	/** 高级随机生成：结合噪声、模板和嵌套区域 */
	public void generateAdvancedRandom(MapGenerationConfig config) {
		// 基础噪声生成
		generate(config.seed, config.octaves, config.persistence, config.frequency, config.amplitude);

		// 随机选择是否应用边界
		if (MathUtils.random() < 0.5) {
			generateBorderSea(3);
		} else {
			generateBorderMountains(3);
		}

		// 随机选择模板
		if (Math.random() < 0.3) {
			generateCityCenter();
		} else if (Math.random() < 0.3) {
			generateIslandMap();
		} else {
			generateRiverCrossing();
		}

		// 批量嵌套区域
		if (config.nestedRegionCount > 0) {
			generateParameterizedNestedRegions(config.nestedRegionCount, config.shapeRatio, config.nestedTileWeights);
		}
	}

	/**
	 * 生成一半陆地，一半海洋的地图
	 */
	public void generateHalfLandHalfSea() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x < width / 2) {
					mapGrid[y][x] = BattleTileType.PLAIN.getId();
				} else {
					mapGrid[y][x] = BattleTileType.SEA.getId();
				}
			}
		}
		smoothMap();
	}

	/**
	 * 生成中心城市，周围农田的地图
	 */
	public void generateCityCenter() {
		int centerX = width / 2;
		int centerY = height / 2;
		int radius = MathUtils.min(width, height) / 6;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int dx = x - centerX;
				int dy = y - centerY;
				float dist = MathUtils.sqrt(dx * dx + dy * dy);
				if (dist < radius) {
					mapGrid[y][x] = BattleTileType.CITY.getId();
				} else if (dist < radius * 2) {
					mapGrid[y][x] = BattleTileType.FARM.getId();
				} else {
					mapGrid[y][x] = BattleTileType.PLAIN.getId();
				}
			}
		}
		smoothMap();
	}

	/**
	 * 生成四角山地，中间平原的地图
	 */
	public void generateMountainCorners() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean corner = (x < width / 4 && y < height / 4) || (x > 3 * width / 4 && y < height / 4)
						|| (x < width / 4 && y > 3 * height / 4) || (x > 3 * width / 4 && y > 3 * height / 4);
				if (corner) {
					mapGrid[y][x] = BattleTileType.MOUNTAIN.getId();
				} else {
					mapGrid[y][x] = BattleTileType.PLAIN.getId();
				}
			}
		}
		smoothMap();
	}

	/**
	 * 生成半陆地半海洋中心有城市的地图
	 */
	public void generateCombinedTemplate() {
		generateHalfLandHalfSea();
		generateCityCenter();
	}

	/**
	 * 生成岛屿地图
	 */
	public void generateIslandMap() {
		int centerX = width / 2;
		int centerY = height / 2;
		int radius = MathUtils.min(width, height) / 3;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int dx = x - centerX;
				int dy = y - centerY;
				float dist = MathUtils.sqrt(dx * dx + dy * dy);
				if (dist < radius) {
					mapGrid[y][x] = BattleTileType.PLAIN.getId();
				} else {
					mapGrid[y][x] = BattleTileType.SEA.getId();
				}
			}
		}
		smoothMap();
	}

	/**
	 * 生成河流穿越地图
	 */
	public void generateRiverCrossing() {
		int riverX = width / 2;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (MathUtils.abs(x - riverX) < 2) {
					mapGrid[y][x] = BattleTileType.RIVER.getId();
				} else {
					mapGrid[y][x] = BattleTileType.GRASSLAND.getId();
				}
			}
		}
		smoothMap();
	}

	/**
	 * 随机选择一个预设模板
	 */
	public void generateRandomTemplate() {
		int choice = (int) (MathUtils.random() * 5);
		switch (choice) {
		case 0:
			generateHalfLandHalfSea();
			break;
		case 1:
			generateCityCenter();
			break;
		case 2:
			generateMountainCorners();
			break;
		case 3:
			generateIslandMap();
			break;
		case 4:
			generateRiverCrossing();
			break;
		}
	}

	public void printMap() {
		// 定义地形字符映射表
		IntMap<Character> tileCharMap = new IntMap<Character>();
		tileCharMap.put(BattleTileType.UNKNOWN.getId(), Character.valueOf('?'));
		tileCharMap.put(BattleTileType.PLAIN.getId(), Character.valueOf('P'));
		tileCharMap.put(BattleTileType.GRASSLAND.getId(), Character.valueOf('G'));
		tileCharMap.put(BattleTileType.DESERT.getId(), Character.valueOf('D'));
		tileCharMap.put(BattleTileType.FOREST.getId(), Character.valueOf('F'));
		tileCharMap.put(BattleTileType.DENSE_FOREST.getId(), Character.valueOf('f'));
		tileCharMap.put(BattleTileType.MOUNTAIN.getId(), Character.valueOf('M'));
		tileCharMap.put(BattleTileType.HILL.getId(), Character.valueOf('H'));
		tileCharMap.put(BattleTileType.RIVER.getId(), Character.valueOf('R'));
		tileCharMap.put(BattleTileType.FORD.getId(), Character.valueOf('r'));
		tileCharMap.put(BattleTileType.SHOAL.getId(), Character.valueOf('O'));
		tileCharMap.put(BattleTileType.SEA.getId(), Character.valueOf('S'));
		tileCharMap.put(BattleTileType.COAST.getId(), Character.valueOf('C'));
		tileCharMap.put(BattleTileType.SWAMP.getId(), Character.valueOf('W'));
		tileCharMap.put(BattleTileType.MARSH.getId(), Character.valueOf('A'));
		tileCharMap.put(BattleTileType.VOLCANO.getId(), Character.valueOf('V'));
		tileCharMap.put(BattleTileType.CLIFF.getId(), Character.valueOf('L'));
		tileCharMap.put(BattleTileType.SKY.getId(), Character.valueOf('Y'));

		// 建筑地形
		tileCharMap.put(BattleTileType.ROAD.getId(), Character.valueOf('='));
		tileCharMap.put(BattleTileType.BRIDGE.getId(), Character.valueOf('B'));
		tileCharMap.put(BattleTileType.FORT.getId(), Character.valueOf('T'));
		tileCharMap.put(BattleTileType.CASTLE.getId(), Character.valueOf('K'));
		tileCharMap.put(BattleTileType.CITY.getId(), Character.valueOf('Z'));
		tileCharMap.put(BattleTileType.WALL.getId(), Character.valueOf('W'));
		tileCharMap.put(BattleTileType.GATE.getId(), Character.valueOf('g'));
		tileCharMap.put(BattleTileType.TOWER.getId(), Character.valueOf('t'));
		tileCharMap.put(BattleTileType.PALISADE.getId(), Character.valueOf('p'));
		tileCharMap.put(BattleTileType.FERRY.getId(), Character.valueOf('f'));
		tileCharMap.put(BattleTileType.PORT.getId(), Character.valueOf('o'));
		tileCharMap.put(BattleTileType.DAM.getId(), Character.valueOf('d'));
		tileCharMap.put(BattleTileType.MINE.getId(), Character.valueOf('m'));
		tileCharMap.put(BattleTileType.FARM.getId(), Character.valueOf('a'));
		tileCharMap.put(BattleTileType.MARKET.getId(), Character.valueOf('k'));

		// 扩展地形
		tileCharMap.put(BattleTileType.SNOW.getId(), Character.valueOf('N'));
		tileCharMap.put(BattleTileType.GLACIER.getId(), Character.valueOf('I'));
		tileCharMap.put(BattleTileType.WASTELAND.getId(), Character.valueOf('X'));
		tileCharMap.put(BattleTileType.LAVA_FIELD.getId(), Character.valueOf('V'));
		tileCharMap.put(BattleTileType.OASIS.getId(), Character.valueOf('O'));
		tileCharMap.put(BattleTileType.RUINS.getId(), Character.valueOf('U'));

		// 打印地图
		for (int y = 0; y < height; y++) {
			StrBuilder sbr = new StrBuilder();
			for (int x = 0; x < width; x++) {
				int id = mapGrid[y][x];
				char c;
				if (tileCharMap.containsKey(id)) {
					c = tileCharMap.get(id);
				} else {
					c = '.';
				}
				sbr.append(c);
			}
			System.out.println(sbr.toString());
		}
	}

	/**
	 * 通用地图边界生成函数
	 * 
	 * @param tileId    地形索引
	 * @param thickness 地形厚度(即几层地形包围地图)
	 */
	public void generateBorder(int tileId, int thickness) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x < thickness || y < thickness || x >= width - thickness || y >= height - thickness) {
					mapGrid[y][x] = tileId;
				}
			}
		}
	}

	/**
	 * 让地图边界生成山脉
	 * 
	 * @param thickness
	 */
	public void generateBorderMountains(int thickness) {
		generateBorder(BattleTileType.MOUNTAIN.getId(), thickness);
	}

	/**
	 * 让地图边界生成海洋
	 * 
	 * @param thickness
	 */
	public void generateBorderSea(int thickness) {
		generateBorder(BattleTileType.SEA.getId(), thickness);
	}

	/**
	 * 让地图边界生成森林
	 * 
	 * @param thickness
	 */
	public void generateBorderForest(int thickness) {
		generateBorder(BattleTileType.FOREST.getId(), thickness);
	}

	/**
	 * 让地图边界生成城墙
	 * 
	 * @param thickness
	 */
	public void generateBorderWalls(int thickness) {
		generateBorder(BattleTileType.WALL.getId(), thickness);
	}

	/**
	 * 让地图边界生成沙漠
	 * 
	 * @param thickness
	 */
	public void generateBorderDesert(int thickness) {
		generateBorder(BattleTileType.DESERT.getId(), thickness);
	}

	/**
	 * 生成地图四边不同地形的组合边界
	 * 
	 * @param topTileId
	 * @param bottomTileId
	 * @param leftTileId
	 * @param rightTileId
	 * @param thickness
	 */
	public void generateBorderCombination(int topTileId, int bottomTileId, int leftTileId, int rightTileId,
			int thickness) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// 上边
				if (y < thickness) {
					mapGrid[y][x] = topTileId;
				}
				// 下边
				else if (y >= height - thickness) {
					mapGrid[y][x] = bottomTileId;
				}
				// 左边
				else if (x < thickness) {
					mapGrid[y][x] = leftTileId;
				}
				// 右边
				else if (x >= width - thickness) {
					mapGrid[y][x] = rightTileId;
				}
			}
		}
	}

	public void generateRandomBorderCombination() {
		int[] tileOptions = { BattleTileType.SEA.getId(), BattleTileType.MOUNTAIN.getId(),
				BattleTileType.FOREST.getId(), BattleTileType.WALL.getId(), BattleTileType.DESERT.getId(),
				BattleTileType.PLAIN.getId() };
		generateRandomBorderCombination(tileOptions);
	}

	/**
	 * 随机厚度组合边界
	 * 
	 * @param tiles
	 */
	public void generateRandomBorderCombination(int... tiles) {
		final int size = tiles.length - 1;
		// 随机选择四边地形
		int topTileId = tiles[MathUtils.nextInt(size)];
		int bottomTileId = tiles[MathUtils.nextInt(size)];
		int leftTileId = tiles[MathUtils.nextInt(size)];
		int rightTileId = tiles[MathUtils.nextInt(size)];
		// 随机厚度（1~3层）
		int thickness = MathUtils.nextInt(1, 3);
		// 调用组合边界生成
		generateBorderCombination(topTileId, bottomTileId, leftTileId, rightTileId, thickness);
	}

	/**
	 * 渐变边界生成
	 * 
	 * @param outerTileId 外层地形
	 * @param innerTileId 内层过渡地形
	 * @param thickness   厚度（层数）
	 */
	public void generateGradientBorder(int outerTileId, int innerTileId, int thickness) {
		for (int layer = 0; layer < thickness; layer++) {
			int currentTileId = (layer < thickness / 2) ? outerTileId : innerTileId;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (x == layer || y == layer || x == width - 1 - layer || y == height - 1 - layer) {
						mapGrid[y][x] = currentTileId;
					}
				}
			}
		}
	}

	/**
	 * 多层渐变边界（支持多种地形过渡）
	 * 
	 * @param tileIds   地形序列，例如 [SEA, FOREST, PLAIN]
	 * @param thickness 总厚度
	 */
	public void generateMultiGradientBorder(IntArray tileIds, int thickness) {
		int segment = thickness / tileIds.size();
		if (segment == 0) {
			segment = 1;
		}
		for (int i = 0; i < tileIds.size(); i++) {
			int startLayer = i * segment;
			int endLayer = MathUtils.min(thickness, (i + 1) * segment);
			for (int layer = startLayer; layer < endLayer; layer++) {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						if (x == layer || y == layer || x == width - 1 - layer || y == height - 1 - layer) {
							mapGrid[y][x] = tileIds.get(i);
						}
					}
				}
			}
		}
	}

	public void generateRandomGradientBorder() {
		int[] tileOptions = { BattleTileType.SEA.getId(), BattleTileType.MOUNTAIN.getId(),
				BattleTileType.FOREST.getId(), BattleTileType.DESERT.getId(), BattleTileType.PLAIN.getId() };
		generateRandomGradientBorder(tileOptions);
	}

	/**
	 * 随机渐变边界生成
	 * 
	 * @param tiles
	 */
	public void generateRandomGradientBorder(int... tiles) {
		// 随机选择渐变层数（2~4层）
		int layers = MathUtils.nextInt(2, 4);
		// 随机选择厚度（3~6层）
		int thickness = MathUtils.nextInt(3, 6);
		// 随机生成地形序列
		IntArray gradientTiles = new IntArray();
		for (int i = 0; i < layers; i++) {
			gradientTiles.add(tiles[MathUtils.nextInt(tiles.length - 1)]);
		}
		// 调用多层渐变边界生成
		generateMultiGradientBorder(gradientTiles, thickness);
	}

	/** 随机边界生成器 */
	public void generateRandomBorderMaster() {
		// 随机厚度（1~5层）
		int thickness = MathUtils.nextInt(1, 5);
		// 随机选择边界类型
		int choice = MathUtils.nextInt(7);
		switch (choice) {
		case 0:
			generateBorderSea(thickness);
			break;
		case 1:
			generateBorderMountains(thickness);
			break;
		case 2:
			generateBorderForest(thickness);
			break;
		case 3:
			generateBorderWalls(thickness);
			break;
		case 4:
			generateBorderDesert(thickness);
			break;
		case 5:
			// 四边组合：随机选择四种地形
			int[] tileOptions = { BattleTileType.SEA.getId(), BattleTileType.MOUNTAIN.getId(),
					BattleTileType.FOREST.getId(), BattleTileType.WALL.getId(), BattleTileType.DESERT.getId(),
					BattleTileType.PLAIN.getId() };
			int size = tileOptions.length - 1;
			int topTileId = tileOptions[MathUtils.nextInt(size)];
			int bottomTileId = tileOptions[MathUtils.nextInt(size)];
			int leftTileId = tileOptions[MathUtils.nextInt(size)];
			int rightTileId = tileOptions[MathUtils.nextInt(size)];
			generateBorderCombination(topTileId, bottomTileId, leftTileId, rightTileId, thickness);
			break;
		case 6:
			// 渐变边界
			int layers = MathUtils.nextInt(2, 4);
			IntArray gradientTiles = new IntArray();
			int[] gradientOptions = { BattleTileType.SEA.getId(), BattleTileType.MOUNTAIN.getId(),
					BattleTileType.FOREST.getId(), BattleTileType.DESERT.getId(), BattleTileType.PLAIN.getId() };
			for (int i = 0; i < layers; i++) {
				gradientTiles.add(gradientOptions[MathUtils.nextInt(gradientOptions.length - 1)]);
			}
			generateMultiGradientBorder(gradientTiles, thickness);
			break;
		}
	}

	public void generateRandomMap() {
		// 随机噪声参数
		int seed = MathUtils.nextInt(100000);
		int octaves = MathUtils.nextInt(3, 5); // 3~5
		float persistence = 0.3f + MathUtils.random() * 0.4f; // 0.3~0.7
		float frequency = 0.005f + MathUtils.random() * 0.02f; // 0.005~0.025
		float amplitude = 0.8f + MathUtils.random() * 0.4f; // 0.8~1.2
		// 使用噪声生成基础地图
		generate(seed, octaves, persistence, frequency, amplitude);
		// 随机边界风格
		generateRandomBorderMaster();
		// 随机嵌套区域数量（0~5）
		int nestedCount = MathUtils.nextInt(0, 5);
		if (nestedCount > 0) {
			// 随机形状比例
			ObjectMap<String, Float> shapeRatio = new ObjectMap<String, Float>();
			shapeRatio.put("circle", MathUtils.random());
			shapeRatio.put("triangle", MathUtils.random());
			shapeRatio.put("polygon", MathUtils.random());
			// 归一化比例（传统for循环）
			float totalShape = 0f;
			for (String key : shapeRatio.keys()) {
				totalShape += shapeRatio.get(key);
			}
			for (String key : shapeRatio.keys()) {
				shapeRatio.put(key, shapeRatio.get(key) / totalShape);
			}
			// 随机地形权重
			IntMap<Float> nestedTileWeights = new IntMap<Float>();
			nestedTileWeights.put(BattleTileType.CITY.getId(), Float.valueOf(MathUtils.random()));
			nestedTileWeights.put(BattleTileType.CASTLE.getId(), Float.valueOf(MathUtils.random()));
			nestedTileWeights.put(BattleTileType.FARM.getId(), Float.valueOf(MathUtils.random()));
			// 归一化权重
			float totalTiles = 0f;
			int[] keys = nestedTileWeights.keys();
			for (int key : keys) {
				totalTiles += nestedTileWeights.get(key);
			}
			for (int key : keys) {
				nestedTileWeights.put(key, Float.valueOf(nestedTileWeights.get(key) / totalTiles));
			}
			// 批量生成嵌套区域
			generateParameterizedNestedRegions(nestedCount, shapeRatio, nestedTileWeights);
		}
	}

	/**
	 * 按照噪声设置生成地图
	 * 
	 * @param seed
	 * @param octaves
	 * @param persistence
	 * @param frequency
	 * @param amplitude
	 */
	/**
	 * 使用分层噪声生成地图，支持更多地形
	 */
	public void generateLayeredNoise(int seed, int octaves, float persistence, float frequency, float amplitude) {
		PerlinNoise noise = new PerlinNoise(seed, persistence, frequency, amplitude, octaves);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float value = (noise.getHeight(x, y) + 1f) / 2f;
				if (value < 0.2f) {
					mapGrid[y][x] = BattleTileType.SEA.getId();
				} else if (value < 0.3f) {
					mapGrid[y][x] = BattleTileType.FORD.getId();
				} else if (value < 0.45f) {
					mapGrid[y][x] = BattleTileType.PLAIN.getId();
				} else if (value < 0.55f) {
					mapGrid[y][x] = BattleTileType.FARM.getId();
				} else if (value < 0.65f) {
					mapGrid[y][x] = BattleTileType.FOREST.getId();
				} else if (value < 0.75f) {
					mapGrid[y][x] = BattleTileType.HILL.getId();
				} else if (value < 0.85f) {
					mapGrid[y][x] = BattleTileType.MOUNTAIN.getId();
				} else if (value < 0.95f) {
					mapGrid[y][x] = BattleTileType.SNOW.getId();
				} else {
					mapGrid[y][x] = BattleTileType.VOLCANO.getId();
				}
			}
		}
		smoothMap();
	}

	public int[][] getMapGrid() {
		return mapGrid;
	}

	public BattleTileType getTile(int x, int y) {
		return BattleTileType.getById(mapGrid[y][x]);
	}

	@Override
	public void close() {

	}
}
