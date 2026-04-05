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

import java.util.Comparator;

import loon.LRelease;
import loon.LSystem;
import loon.events.GameEvent;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.geom.PointI;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 战斗地图专属寻径工具类
 */
public class BattlePathFinder implements LRelease {

	// 封装坐标和成本
	public static class Node {
		public final int x, y, gCost, hCost, fCost;

		public Node(int x, int y, int gCost, int hCost) {
			this.x = x;
			this.y = y;
			this.gCost = gCost;
			this.hCost = hCost;
			this.fCost = gCost + hCost;
		}
	}

	// 路径结果类
	public static class PathResult {

		public final boolean success;

		public final String message;

		public TArray<PointI> path;

		public PathResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		public PathResult(boolean success, String message, TArray<PointI> path) {
			this.success = success;
			this.message = message;
			this.path = path;
		}
	}

	// 排序模式
	public static enum SortMode {
		// 只按x排序
		BY_X,
		// 只按y排序
		BY_Y,
		// 同时考虑x与y
		BY_XY
	}

	public static TArray<PointI> sortVectors(TArray<PointI> list) {
		return sortVectors(list, true, SortMode.BY_XY);
	}

	public static TArray<PointI> sortVectors(TArray<PointI> list, final boolean ascending, final SortMode mode) {
		list.sort(new Comparator<PointI>() {

			@Override
			public int compare(PointI o1, PointI o2) {
				int cmp = 0;
				switch (mode) {
				case BY_X:
					cmp = MathUtils.compare(o1.x, o2.x);
					break;
				case BY_Y:
					cmp = MathUtils.compare(o1.y, o2.y);
					break;
				case BY_XY:
					cmp = MathUtils.compare(o1.x, o2.x);
					if (cmp == 0) {
						cmp = MathUtils.compare(o1.y, o2.y);
					}
					break;
				}
				return ascending ? cmp : -cmp;

			}
		});
		return list;
	}

	private final static int PATH_MAX_ITERATIONS = 10000;
	// 斜向移动成本
	public static final float PATH_DIAGONAL_COST = 1.414f;
	// 路径缓存大小
	public static final int PATH_CACHE_SIZE = 50;
	private final BattleTile[][] map;
	private final int width, height;
	private final int[][] dirs = { { 1, 0, 10 }, { -1, 0, 10 }, { 0, 1, 10 }, { 0, -1, 10 }, { 1, 1, 14 },
			{ -1, 1, 14 }, { 1, -1, 14 }, { -1, -1, 14 } };
	private int[][] gCost, hCost, parentX, parentY;
	private boolean[][] closed;
	// 路径缓存
	private final IntMap<TArray<PointI>> pathCache = new IntMap<TArray<PointI>>();
	// 寻径启发函数权重
	private final float heuristicWeight = 1.0f;

	private final GameEventBus<PathResult> eventBus;

	private boolean allowSmoothPath = true;

	public BattlePathFinder(GameEventBus<PathResult> bus, BattleTile[][] map, int width, int height) {
		this(bus, map, width, height, true);
	}

	public BattlePathFinder(GameEventBus<PathResult> bus, BattleTile[][] map, int width, int height, boolean smooth) {
		this.eventBus = bus;
		this.map = map;
		this.width = width;
		this.height = height;
		this.allowSmoothPath = smooth;
		initCostArrays();
	}

	private void initCostArrays() {
		gCost = new int[width][height];
		hCost = new int[width][height];
		parentX = new int[width][height];
		parentY = new int[width][height];
		closed = new boolean[width][height];
	}

	/**
	 * 使用二分查找插入，保持有序
	 *
	 * @param list
	 * @param node
	 */
	private void insertSorted(TArray<Node> list, Node node) {
		int low = 0, high = list.size - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Node midNode = list.get(mid);
			int cmp = MathUtils.compare(node.fCost, midNode.fCost);
			if (cmp < 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		list.add(node);
		for (int i = list.size - 1; i > low; i--) {
			list.set(i, list.get(i - 1));
		}
		list.set(low, node);
	}

	public boolean isAllowSmoothPath() {
		return allowSmoothPath;
	}

	public void setAllowSmoothPath(boolean s) {
		allowSmoothPath = s;
	}

	private final static int setKey(int x, int y) {
		int result = 1;
		result = LSystem.unite(result, x);
		result = LSystem.unite(result, y);
		return result;
	}

	/**
	 * 加入地形移动成本、高度惩罚、路径缓存、迭代限制
	 * 
	 * @param sx
	 * @param sy
	 * @param ex
	 * @param ey
	 * @return
	 */
	public TArray<PointI> findPath(int sx, int sy, int ex, int ey) {
		if (!isValid(sx, sy) || !isValid(ex, ey) || !map[ex][ey].isPassable()) {
			publishPathEvent(false, "Invalid or impassable start/end point", null);
			return new TArray<PointI>();
		}

		if (sx == ex && sy == ey) {
			TArray<PointI> path = new TArray<PointI>();
			path.add(new PointI(sx, sy));
			publishPathEvent(true, "Start and end are the same", path);
			return path;
		}

		int cacheKey = 1;
		cacheKey = LSystem.unite(cacheKey, sx);
		cacheKey = LSystem.unite(cacheKey, sy);
		cacheKey = LSystem.unite(cacheKey, ex);
		cacheKey = LSystem.unite(cacheKey, ey);

		if (pathCache.containsKey(cacheKey)) {
			TArray<PointI> cachedPath = new TArray<PointI>(pathCache.get(cacheKey));
			publishPathEvent(true, "Using cached path", cachedPath);
			return cachedPath;
		}

		resetCostArrays();

		TArray<Node> open = new TArray<Node>();
		IntMap<Node> openMap = new IntMap<Node>();
		gCost[sx][sy] = 0;
		hCost[sx][sy] = calculateHeuristic(sx, sy, ex, ey);
		Node startNode = new Node(sx, sy, gCost[sx][sy], hCost[sx][sy]);
		insertSorted(open, startNode);
		openMap.put(setKey(sx, sy), startNode);

		int iterations = 0;

		while (!open.isEmpty() && iterations < PATH_MAX_ITERATIONS) {
			iterations++;
			Node current = open.removeIndex(0);
			openMap.remove(setKey(current.x, current.y));
			int cx = current.x, cy = current.y;

			if (closed[cx][cy]) {
				continue;
			}
			closed[cx][cy] = true;

			if (cx == ex && cy == ey) {
				TArray<PointI> path = reconstructPath(ex, ey);
				if (allowSmoothPath) {
					TArray<PointI> smoothPath = smoothPath(path);
					cachePath(cacheKey, smoothPath);
					publishPathEvent(true, "Path found", smoothPath);
					return smoothPath;
				} else {
					publishPathEvent(true, "Path found", path);
					return path;
				}
			}

			for (int[] dir : dirs) {
				int nx = cx + dir[0], ny = cy + dir[1];
				int baseCost = dir[2];

				if (!isValid(nx, ny) || closed[nx][ny] || !map[nx][ny].isPassable()) {
					continue;
				}
				// 斜向移动合法性检查
				if (dir[0] != 0 && dir[1] != 0) {
					if (!map[cx + dir[0]][cy].isPassable() || !map[cx][cy + dir[1]].isPassable()) {
						continue;
					}
				}
				BattleTile tile = map[nx][ny];
				float terrainMultiplier = tile.pathCost;

				float dirCost = (dir[0] != 0 && dir[1] != 0) ? PATH_DIAGONAL_COST : 1.0f;
				int newG = gCost[cx][cy] + (int) (baseCost * terrainMultiplier * dirCost);

				if (newG < gCost[nx][ny]) {
					gCost[nx][ny] = newG;
					hCost[nx][ny] = calculateHeuristic(nx, ny, ex, ey);
					parentX[nx][ny] = cx;
					parentY[nx][ny] = cy;

					int nodeKey = setKey(nx, ny);
					Node newNode = new Node(nx, ny, gCost[nx][ny], hCost[nx][ny]);
					if (!openMap.containsKey(nodeKey)) {
						insertSorted(open, newNode);
						openMap.put(nodeKey, newNode);
					} else {
						Node oldNode = openMap.get(nodeKey);
						if (newNode.fCost < oldNode.fCost) {
							open.remove(oldNode);
							insertSorted(open, newNode);
							openMap.put(nodeKey, newNode);
						}
					}
				}
			}
		}

		String message = iterations >= PATH_MAX_ITERATIONS ? "Pathfinding iteration limit exceeded"
				: "No valid path found";
		publishPathEvent(false, message, null);
		return new TArray<PointI>();
	}

	/**
	 * 检测三点是否共线
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean isStraightLine(PointI p0, PointI p1, PointI p2) {
		return MathUtils.equal((p1.x - p0.x) * (p2.y - p0.y), (p2.x - p0.x) * (p1.y - p0.y));
	}

	private boolean isValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private int calculateHeuristic(int x, int y, int ex, int ey) {
		int dx = MathUtils.abs(x - ex);
		int dy = MathUtils.abs(y - ey);
		int manhattan = dx + dy;
		float euclidean = MathUtils.sqrt(dx * dx + dy * dy);
		return (int) ((manhattan + euclidean) * heuristicWeight);
	}

	/**
	 * 发布寻径事件
	 * 
	 * @param success
	 * @param message
	 * @param path
	 */
	private void publishPathEvent(boolean success, String message, TArray<PointI> path) {
		PathResult result = new PathResult(success, message, path);
		eventBus.publish(new GameEvent<PathResult>(success ? GameEventType.PATH_FOUND : GameEventType.PATH_FAILED, this,
				null, result));
	}

	/**
	 * 缓存路径
	 * 
	 * @param key
	 * @param path
	 */
	private void cachePath(int key, TArray<PointI> path) {
		if (pathCache.size() >= PATH_CACHE_SIZE) {
			// 移除最旧的缓存项
			if (pathCache.size > 0) {
				int oldestKey = pathCache.keys()[0];
				pathCache.remove(oldestKey);
			}
		}
		pathCache.put(key, new TArray<PointI>(path));
	}

	/**
	 * 路径平滑（使用拉格朗日插值+障碍物检测）
	 * 
	 * @param path
	 * @return
	 */
	private TArray<PointI> smoothPath(TArray<PointI> path) {
		if (path.size() < 3) {
			return new TArray<PointI>(path);
		}
		TArray<PointI> smooth = new TArray<PointI>();
		smooth.add(path.get(0));
		int lastIndex = 0;
		for (int i = 1; i < path.size(); i++) {
			PointI last = path.get(lastIndex);
			PointI curr = path.get(i);
			if (isPathClear(last, curr)) {
				continue;
			} else {
				smooth.add(path.get(i - 1));
				lastIndex = i - 1;
			}
		}
		smooth.add(path.get(path.size() - 1));
		TArray<PointI> finalSmooth = new TArray<PointI>();
		finalSmooth.add(smooth.get(0));
		for (int i = 1; i < smooth.size() - 1; i++) {
			PointI prev = smooth.get(i - 1);
			PointI curr = smooth.get(i);
			PointI next = smooth.get(i + 1);
			if (!isStraightLine(prev, curr, next)) {
				finalSmooth.add(curr);
			}
		}
		finalSmooth.add(smooth.get(smooth.size() - 1));
		int minPoints = MathUtils.max(3, path.size() / 3);
		if (finalSmooth.size() < minPoints) {
			return path;
		}
		return finalSmooth;
	}

	/**
	 * 检测两点之间路径是否畅通
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean isPathClear(PointI start, PointI end) {
		int sx = start.x;
		int sy = start.y;
		int ex = end.x;
		int ey = end.y;

		int dx = MathUtils.abs(ex - sx);
		int dy = MathUtils.abs(ey - sy);
		int x = sx;
		int y = sy;
		int err = dx - dy;

		int stepX = sx < ex ? 1 : -1;
		int stepY = sy < ey ? 1 : -1;

		for (;;) {
			if (x == ex && y == ey) {
				break;
			}
			if (!map[x][y].isPassable()) {
				return false;
			}
			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x += stepX;
			}
			if (e2 < dx) {
				err += dx;
				y += stepY;
			}
		}
		return true;
	}

	/**
	 * 重构路径
	 *
	 * @param ex 终点x
	 * @param ey 终点y
	 * @return 路径列表
	 */
	private TArray<PointI> reconstructPath(int ex, int ey) {
		TArray<PointI> path = new TArray<PointI>();
		int cx = ex, cy = ey;
		while (cx != -1 && cy != -1) {
			path.add(new PointI(cx, cy));
			int px = parentX[cx][cy];
			int py = parentY[cx][cy];
			cx = px;
			cy = py;
		}
		path.reverse();
		return path;
	}

	/**
	 * 重置成本数组
	 */
	private void resetCostArrays() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gCost[i][j] = Integer.MAX_VALUE;
				hCost[i][j] = 0;
				parentX[i][j] = -1;
				parentY[i][j] = -1;
				closed[i][j] = false;
			}
		}
	}

	/**
	 * 清理路径缓存
	 */
	public void clearCache() {
		pathCache.clear();
	}

	@Override
	public void close() {
		clearCache();
	}
}
