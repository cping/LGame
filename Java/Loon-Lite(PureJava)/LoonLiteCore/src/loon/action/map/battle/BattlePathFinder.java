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

import loon.events.GameEvent;
import loon.events.GameEventBus;
import loon.events.GameEventType;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;
import loon.utils.TArray;

/**
 * 战斗地图专属寻径工具类
 */
public class BattlePathFinder {

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

		public TArray<Vector2f> path;

		public PathResult(boolean success, String message) {
			this.success = success;
			this.message = message;
		}

		public PathResult(boolean success, String message, TArray<Vector2f> path) {
			this.success = success;
			this.message = message;
			this.path = path;
		}
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
	private final ObjectMap<String, TArray<Vector2f>> pathCache = new ObjectMap<String, TArray<Vector2f>>();
	// 寻径启发函数权重
	private final float heuristicWeight = 1.0f;

	private final GameEventBus<PathResult> eventBus;

	public BattlePathFinder(GameEventBus<PathResult> bus, BattleTile[][] map, int width, int height) {
		this.eventBus = bus;
		this.map = map;
		this.width = width;
		this.height = height;
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
		int low = 0, high = list.size() - 1;
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
		// 插入到正确位置
		list.set(low, node);
	}

	private final static String setKey(int x, int y) {
		return x + "," + y;
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
	public TArray<Vector2f> findPath(int sx, int sy, int ex, int ey) {
		if (!isValid(sx, sy) || !isValid(ex, ey) || !map[ex][ey].isPassable()) {
			publishPathEvent(false, "Invalid or impassable start/end point", null);
			return new TArray<Vector2f>();
		}

		if (sx == ex && sy == ey) {
			TArray<Vector2f> path = new TArray<Vector2f>();
			path.add(new Vector2f(sx, sy));
			publishPathEvent(true, "Start and end are the same", path);
			return path;
		}

		String cacheKey = sx + "," + sy + "->" + ex + "," + ey;
		if (pathCache.containsKey(cacheKey)) {
			TArray<Vector2f> cachedPath = new TArray<Vector2f>(pathCache.get(cacheKey));
			publishPathEvent(true, "Using cached path", cachedPath);
			return cachedPath;
		}

		resetCostArrays();

		TArray<Node> open = new TArray<Node>();
		ObjectMap<String, Node> openMap = new ObjectMap<String, Node>();
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
				TArray<Vector2f> path = reconstructPath(ex, ey);
				TArray<Vector2f> smoothPath = smoothPath(path);
				cachePath(cacheKey, smoothPath);
				publishPathEvent(true, "Path found", smoothPath);
				return smoothPath;
			}

			for (int[] dir : dirs) {
				int nx = cx + dir[0], ny = cy + dir[1];
				int baseCost = dir[2];

				if (!isValid(nx, ny) || closed[nx][ny] || !map[nx][ny].isPassable()) {
					continue;
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

					String nodeKey = setKey(nx, ny);
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
		return new TArray<Vector2f>();
	}

	/**
	 * 发布寻径事件
	 * 
	 * @param success
	 * @param message
	 * @param path
	 */
	private void publishPathEvent(boolean success, String message, TArray<Vector2f> path) {
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
	private void cachePath(String key, TArray<Vector2f> path) {
		if (pathCache.size() >= PATH_CACHE_SIZE) {
			// 移除最旧的缓存项
			Keys<String> it = pathCache.keys();
			if (it.hasNext()) {
				it.next();
				it.remove();
			}
		}
		pathCache.put(key, new TArray<Vector2f>(path));
	}

	/**
	 * 路径平滑（使用拉格朗日插值+障碍物检测）
	 * 
	 * @param path
	 * @return
	 */
	private TArray<Vector2f> smoothPath(TArray<Vector2f> path) {
		if (path.size() < 3) {
			return new TArray<Vector2f>(path);
		}
		TArray<Vector2f> smooth = new TArray<Vector2f>();
		smooth.add(path.get(0));
		int lastIndex = 0;

		for (int i = 2; i < path.size(); i++) {
			Vector2f p0 = path.get(lastIndex);
			Vector2f p1 = path.get(i - 1);
			Vector2f p2 = path.get(i);
			// 检测是否为直线且无障碍物
			if (!isStraightLine(p0, p1, p2) || !isPathClear(p0, p2)) {
				smooth.add(p1);
				lastIndex = i - 1;
			}
		}
		smooth.add(path.get(path.size() - 1));
		return smooth;
	}

	/**
	 * 检测两点之间路径是否畅通
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean isPathClear(Vector2f start, Vector2f end) {
		int sx = (int) start.x;
		int sy = (int) start.y;
		int ex = (int) end.x;
		int ey = (int) end.y;

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
	 * 检测三点是否共线
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean isStraightLine(Vector2f p0, Vector2f p1, Vector2f p2) {
		return MathUtils.equal((p1.x - p0.x) * (p2.y - p0.y), (p2.x - p0.x) * (p1.y - p0.y));
	}

	private boolean isValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private int calculateHeuristic(int x, int y, int ex, int ey) {
		int dx = MathUtils.abs(x - ex);
		int dy = MathUtils.abs(y - ey);
		int manhattan = dx + dy;
		float euclidean = (float) MathUtils.sqrt(dx * dx + dy * dy);
		return (int) ((manhattan + euclidean) * heuristicWeight);
	}

	/**
	 * 重构路径
	 * 
	 * @param ex
	 * @param ey
	 * @return
	 */
	private TArray<Vector2f> reconstructPath(int ex, int ey) {
		TArray<Vector2f> path = new TArray<Vector2f>();
		int cx = ex, cy = ey;
		while (cx != -1 && cy != -1) {
			path.set(0, new Vector2f(cx, cy));
			int px = parentX[cx][cy];
			int py = parentY[cx][cy];
			cx = px;
			cy = py;
		}
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
}
