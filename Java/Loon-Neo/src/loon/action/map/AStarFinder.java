/**
 * Copyright 2008 - 2010
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.1
 */
package loon.action.map;

import loon.LRelease;
import loon.LSystem;
import loon.action.map.heuristics.BestFirst;
import loon.action.map.heuristics.Closest;
import loon.action.map.heuristics.ClosestSquared;
import loon.action.map.heuristics.Diagonal;
import loon.action.map.heuristics.DiagonalMin;
import loon.action.map.heuristics.DiagonalShort;
import loon.action.map.heuristics.Euclidean;
import loon.action.map.heuristics.EuclideanNoSQR;
import loon.action.map.heuristics.Manhattan;
import loon.action.map.heuristics.Mixing;
import loon.action.map.heuristics.Octile;
import loon.event.Updateable;
import loon.geom.Vector2f;
import loon.utils.IntMap;
import loon.utils.ObjectSet;
import loon.utils.TArray;

/**
 * A*寻径用类
 */
public class AStarFinder implements Updateable, LRelease {

	private static class ScoredPath {

		private float score;

		private TArray<Vector2f> path;

		ScoredPath(float score, TArray<Vector2f> path) {
			this.score = score;
			this.path = path;
		}

	}

	public final static AStarFindHeuristic ASTAR_CLOSEST = new Closest();

	public final static AStarFindHeuristic ASTAR_CLOSEST_SQUARED = new ClosestSquared();

	public final static AStarFindHeuristic ASTAR_MANHATTAN = new Manhattan();

	public final static AStarFindHeuristic ASTAR_DIAGONAL = new Diagonal();

	public final static AStarFindHeuristic ASTAR_EUCLIDEAN = new Euclidean();

	public final static AStarFindHeuristic ASTAR_EUCLIDEAN_NOSQR = new EuclideanNoSQR();

	public final static AStarFindHeuristic ASTAR_MIXING = new Mixing();

	public final static AStarFindHeuristic ASTAR_DIAGONAL_SHORT = new DiagonalShort();

	public final static AStarFindHeuristic ASTAR_BEST_FIRST = new BestFirst();

	public final static AStarFindHeuristic ASTAR_OCTILE = new Octile();

	public final static AStarFindHeuristic ASTAR_DIAGONAL_MIN = new DiagonalMin();

	private final static IntMap<TArray<Vector2f>> FINDER_LAZY = new IntMap<TArray<Vector2f>>(100);

	private final static int makeLazyKey(AStarFindHeuristic heuristic, int[][] map, int[] limits, int sx, int sy,
			int ex, int ey, boolean flag) {
		int hashCode = 1;
		int w = map.length;
		int h = map[0].length;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				hashCode = LSystem.unite(hashCode, map[i][j]);
			}
		}
		if (limits != null) {
			for (int i = 0; i < limits.length; i++) {
				hashCode = LSystem.unite(hashCode, limits[i]);
			}
		}
		hashCode = LSystem.unite(hashCode, heuristic.getType());
		hashCode = LSystem.unite(hashCode, sx);
		hashCode = LSystem.unite(hashCode, sy);
		hashCode = LSystem.unite(hashCode, ex);
		hashCode = LSystem.unite(hashCode, ey);
		hashCode = LSystem.unite(hashCode, flag);
		return hashCode;
	}

	public static TArray<Vector2f> find(AStarFindHeuristic heuristic, int[][] maps, int[] limits, int x1, int y1,
			int x2, int y2, boolean flag) {
		heuristic = (heuristic == null ? ASTAR_MANHATTAN : heuristic);
		synchronized (FINDER_LAZY) {
			if (FINDER_LAZY.size >= LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
				FINDER_LAZY.clear();
			}
			int key = makeLazyKey(heuristic, maps, limits, x1, y1, x2, y2, flag);
			TArray<Vector2f> result = FINDER_LAZY.get(key);
			if (result == null) {
				AStarFinder astar = new AStarFinder(heuristic);
				Field2D fieldMap = new Field2D(maps);
				if (limits != null) {
					fieldMap.setLimit(limits);
				}
				Vector2f start = new Vector2f(x1, y1);
				Vector2f over = new Vector2f(x2, y2);
				result = astar.calc(fieldMap, start, over, flag);
				FINDER_LAZY.put(key, result);
				astar.close();
			}
			if (result != null) {
				TArray<Vector2f> newResult = new TArray<Vector2f>();
				newResult.addAll(result);
				result = newResult;
			}
			if (result == null) {
				return new TArray<Vector2f>();
			}
			return new TArray<Vector2f>(result);
		}
	}

	public static TArray<Vector2f> find(int[][] maps, int x1, int y1, int x2, int y2, boolean flag) {
		return find(null, maps, x1, y1, x2, y2, flag);
	}

	public static TArray<Vector2f> find(AStarFindHeuristic heuristic, int[][] maps, int x1, int y1, int x2, int y2,
			boolean flag) {
		return find(heuristic, maps, x1, y1, x2, y2, flag);
	}

	public static TArray<Vector2f> find(HexagonMap map, int x1, int y1, int x2, int y2, boolean flag) {
		return find(null, map.getField2D().getMap(), map.getLimit(), x1, y1, x2, y2, flag);
	}
	
	public static TArray<Vector2f> find(TileMap map, int x1, int y1, int x2, int y2, boolean flag) {
		return find(null, map.getField2D().getMap(), map.getLimit(), x1, y1, x2, y2, flag);
	}

	public static TArray<Vector2f> find(Field2D maps, int x1, int y1, int x2, int y2, boolean flag) {
		return find(null, maps.getMap(), maps.getLimit(), x1, y1, x2, y2, flag);
	}

	public static TArray<Vector2f> find(AStarFindHeuristic heuristic, Field2D maps, int x1, int y1, int x2, int y2,
			boolean flag) {
		return find(heuristic, maps.getMap(), maps.getLimit(), x1, y1, x2, y2, flag);
	}

	public static TArray<Vector2f> find(AStarFindHeuristic heuristic, Field2D maps, Vector2f start, Vector2f goal,
			boolean flag) {
		return find(heuristic, maps.getMap(), maps.getLimit(), start.x(), start.y(), goal.x(), goal.y(), flag);
	}

	public static TArray<Vector2f> find(AStarFindHeuristic heuristic, int[][] maps, Vector2f start, Vector2f goal,
			boolean flag) {
		return find(heuristic, maps, start.x(), start.y(), goal.x(), goal.y(), flag);
	}

	private Vector2f goal;

	private TArray<ScoredPath> pathes;

	private TArray<Vector2f> path;

	private ObjectSet<Vector2f> visitedCache;

	private ScoredPath spath;

	private boolean flying, flag, closed;

	private Field2D findMap;

	private int startX, startY, endX, endY;

	private AStarFinderListener pathFoundListener;

	private AStarFindHeuristic findHeuristic;

	public AStarFinder(AStarFindHeuristic heuristic) {
		this(heuristic, false);
	}

	public AStarFinder(AStarFindHeuristic heuristic, boolean flying) {
		this.flying = flying;
		this.findHeuristic = heuristic;
	}

	public AStarFinder(AStarFindHeuristic heuristic, Field2D m, int startX, int startY, int endX, int endY,
			boolean flying, boolean flag, AStarFinderListener callback) {
		this.findMap = m;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.flying = flying;
		this.flag = flag;
		this.pathFoundListener = callback;
		this.findHeuristic = heuristic;
	}

	public AStarFinder(AStarFindHeuristic heuristic, Field2D m, int startX, int startY, int endX, int endY,
			boolean flying, boolean flag) {
		this(heuristic, m, startX, startY, endX, endY, flying, flag, null);
	}

	public void update(AStarFinder find) {
		this.findMap = find.findMap;
		this.startX = find.startX;
		this.startY = find.startY;
		this.endX = find.endX;
		this.endY = find.endY;
		this.flying = find.flying;
		this.flag = find.flag;
		this.findHeuristic = find.findHeuristic;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AStarFinder) {
			return this.pathFoundListener == ((AStarFinder) o).pathFoundListener;
		}
		return false;
	}

	public TArray<Vector2f> findPath() {
		Vector2f start = new Vector2f(startX, startY);
		Vector2f over = new Vector2f(endX, endY);
		return calc(findMap, start, over, flag);
	}

	private TArray<Vector2f> calc(Field2D m, Vector2f start, Vector2f goal, boolean flag) {
		if (start.equals(goal)) {
			TArray<Vector2f> v = new TArray<Vector2f>();
			v.add(start);
			return v;
		}
		this.goal = goal;
		if (visitedCache == null) {
			visitedCache = new ObjectSet<Vector2f>();
		} else {
			visitedCache.clear();
		}
		if (pathes == null) {
			pathes = new TArray<ScoredPath>();
		} else {
			pathes.clear();
		}
		visitedCache.add(start);
		if (path == null) {
			path = new TArray<Vector2f>();
		} else {
			path.clear();
		}
		path.add(start);
		if (spath == null) {
			spath = new ScoredPath(0, path);
		} else {
			spath.score = 0;
			spath.path = path;
		}
		pathes.add(spath);
		return astar(m, flag);
	}

	private int overflow = 4096;

	public AStarFinder setOverflow(int over) {
		this.overflow = over;
		return this;
	}

	public int getOverflow() {
		return this.overflow;
	}

	private TArray<Vector2f> astar(Field2D map, boolean flag) {
		for (int j = 0; pathes.size > 0; j++) {
			if (j > overflow) {
				pathes.clear();
				continue;
			}
			ScoredPath spath = pathes.removeIndex(0);
			Vector2f current = spath.path.get(spath.path.size - 1);
			if (current.equals(goal)) {
				return new TArray<Vector2f>(spath.path);
			}
			TArray<Vector2f> list = map.neighbors(current, flag);
			final int size = list.size;
			for (int i = 0; i < size; i++) {
				Vector2f next = list.get(i);
				if (!map.isHit(next) && !flying) {
					continue;
				}
				if (!visitedCache.add(next)) {
					continue;
				}
				TArray<Vector2f> path = new TArray<Vector2f>(spath.path);
				path.add(next);
				float score = spath.score + findHeuristic.getScore(goal.x, goal.y, next.x, next.y);
				insert(score, path);
			}
		}
		return null;
	}

	private void insert(float score, TArray<Vector2f> path) {
		int size = pathes.size;
		for (int i = 0; i < size; i++) {
			ScoredPath spath = pathes.get(i);
			if (spath.score >= score) {
				pathes.add(new ScoredPath(score, path));
				return;
			}
		}
		pathes.add(new ScoredPath(score, path));
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public boolean isFlying() {
		return flying;
	}

	@Override
	public void action(Object o) {
		if (pathFoundListener != null) {
			pathFoundListener.pathFound(findPath());
		}
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() {
		try {
			if (path != null) {
				path.clear();
				path = null;
			}
			if (pathes != null) {
				pathes.clear();
				pathes = null;
			}
			if (visitedCache != null) {
				visitedCache.clear();
				visitedCache = null;
			}
			spath = null;
			goal = null;
			closed = true;
		} catch (Throwable e) {
		}
	}

}
