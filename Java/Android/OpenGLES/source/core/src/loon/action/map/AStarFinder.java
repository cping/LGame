package loon.action.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import loon.action.map.heuristics.Closest;
import loon.action.map.heuristics.ClosestSquared;
import loon.action.map.heuristics.Diagonal;
import loon.action.map.heuristics.DiagonalShort;
import loon.action.map.heuristics.Euclidean;
import loon.action.map.heuristics.EuclideanNoSQR;
import loon.action.map.heuristics.Manhattan;
import loon.action.map.heuristics.Mixing;
import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.geom.Vector2f;


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
public class AStarFinder implements Runnable, LRelease {

	public final static AStarFindHeuristic ASTAR_CLOSEST = new Closest();

	public final static AStarFindHeuristic ASTAR_CLOSEST_SQUARED = new ClosestSquared();

	public final static AStarFindHeuristic ASTAR_MANHATTAN = new Manhattan();

	public final static AStarFindHeuristic ASTAR_DIAGONAL = new Diagonal();

	public final static AStarFindHeuristic ASTAR_EUCLIDEAN = new Euclidean();

	public final static AStarFindHeuristic ASTAR_EUCLIDEAN_NOSQR = new EuclideanNoSQR();

	public final static AStarFindHeuristic ASTAR_MIXING = new Mixing();

	public final static AStarFindHeuristic ASTAR_DIAGONAL_SHORT = new DiagonalShort();

	private final static HashMap<Integer, LinkedList<Vector2f>> finderLazy = new HashMap<Integer, LinkedList<Vector2f>>(
			100);

	private final static int makeLazyKey(AStarFindHeuristic heuristic,
			int[][] map, int[] limits, int sx, int sy, int ex, int ey,
			boolean flag) {
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

	public static LinkedList<Vector2f> find(AStarFindHeuristic heuristic,
			int[][] maps, int[] limits, int x1, int y1, int x2, int y2,
			boolean flag) {
		heuristic = (heuristic == null ? ASTAR_MANHATTAN : heuristic);
		synchronized (finderLazy) {
			if (finderLazy.size() >= LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
				finderLazy.clear();
			}
			int key = makeLazyKey(heuristic, maps, limits, x1, y1, x2, y2, flag);
			LinkedList<Vector2f> result = finderLazy.get(key);
			if (result == null) {
				AStarFinder astar = new AStarFinder(heuristic);
				Field2D fieldMap = new Field2D(maps);
				if (limits != null) {
					fieldMap.setLimit(limits);
				}
				Vector2f start = new Vector2f(x1, y1);
				Vector2f over = new Vector2f(x2, y2);
				result = astar.calc(fieldMap, start, over, flag);
				finderLazy.put(key, result);
				astar.dispose();
			}
			if (result != null) {
				LinkedList<Vector2f> newResult = new LinkedList<Vector2f>();
				newResult.addAll(result);
				result = newResult;
			}
			return result;
		}
	}

	public static LinkedList<Vector2f> find(AStarFindHeuristic heuristic,
			int[][] maps, int x1, int y1, int x2, int y2, boolean flag) {
		return find(heuristic, maps, x1, y1, x2, y2, flag);
	}

	public static LinkedList<Vector2f> find(AStarFindHeuristic heuristic,
			Field2D maps, int x1, int y1, int x2, int y2, boolean flag) {
		return find(heuristic, maps.getMap(), maps.getLimit(), x1, y1, x2, y2,
				flag);
	}

	public static LinkedList<Vector2f> find(AStarFindHeuristic heuristic,
			Field2D maps, Vector2f start, Vector2f goal, boolean flag) {
		return find(heuristic, maps.getMap(), maps.getLimit(), start.x(),
				start.y(), goal.x(), goal.y(), flag);
	}

	public static LinkedList<Vector2f> find(AStarFindHeuristic heuristic,
			int[][] maps, Vector2f start, Vector2f goal, boolean flag) {
		return find(heuristic, maps, start.x(), start.y(), goal.x(), goal.y(),
				flag);
	}

	private Vector2f goal;

	private LinkedList<ScoredPath> pathes;

	private LinkedList<Vector2f> path;

	private HashSet<Vector2f> visitedCache;

	private ScoredPath spath;

	private boolean flying, flag;

	private Field2D field;

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

	public AStarFinder(AStarFindHeuristic heuristic, Field2D field, int startX,
			int startY, int endX, int endY, boolean flying, boolean flag,
			AStarFinderListener callback) {
		this.field = field;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.flying = flying;
		this.flag = flag;
		this.pathFoundListener = callback;
		this.findHeuristic = heuristic;
	}

	public AStarFinder(AStarFindHeuristic heuristic, Field2D field, int startX,
			int startY, int endX, int endY, boolean flying, boolean flag) {
		this(heuristic, field, startX, startY, endX, endY, flying, flag, null);
	}

	public void update(AStarFinder find) {
		this.field = find.field;
		this.startX = find.startX;
		this.startY = find.startY;
		this.endX = find.endX;
		this.endY = find.endY;
		this.flying = find.flying;
		this.flag = find.flag;
		this.findHeuristic = find.findHeuristic;
	}

	@Override
	public int hashCode(){
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AStarFinder) {
			return this.pathFoundListener == ((AStarFinder) o).pathFoundListener;
		}
		return false;
	}

	public LinkedList<Vector2f> findPath() {
		Vector2f start = new Vector2f(startX, startY);
		Vector2f over = new Vector2f(endX, endY);
		return calc(field, start, over, flag);
	}

	private LinkedList<Vector2f> calc(Field2D field, Vector2f start,
			Vector2f goal, boolean flag) {
		if (start.equals(goal)) {
			LinkedList<Vector2f> v = new LinkedList<Vector2f>();
			v.add(start);
			return v;
		}
		this.goal = goal;
		if (visitedCache == null) {
			visitedCache = new HashSet<Vector2f>();
		} else {
			visitedCache.clear();
		}
		if (pathes == null) {
			pathes = new LinkedList<ScoredPath>();
		} else {
			pathes.clear();
		}
		visitedCache.add(start);
		if (path == null) {
			path = new LinkedList<Vector2f>();
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
		return astar(field, flag);
	}

	private LinkedList<Vector2f> astar(Field2D field, boolean flag) {
		for (; pathes.size() > 0;) {
			ScoredPath spath = pathes.remove(0);
			Vector2f current = spath.path.get(spath.path.size() - 1);
			if (current.equals(goal)) {
				return spath.path;
			}
			ArrayList<Vector2f> list = field.neighbors(current, flag);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				Vector2f next = list.get(i);
				if (visitedCache.contains(next)) {
					continue;
				}
				visitedCache.add(next);
				if (!field.isHit(next) && !flying) {
					continue;
				}
				LinkedList<Vector2f> path = new LinkedList<Vector2f>(spath.path);
				path.add(next);
				float score = spath.score
						+ findHeuristic
								.getScore(goal.x, goal.y, next.x, next.y);
				insert(score, path);
			}
		}
		return null;
	}

	private void insert(float score, LinkedList<Vector2f> path) {
		int size = pathes.size();
		for (int i = 0; i < size; i += 1) {
			ScoredPath spath = pathes.get(i);
			if (spath.score >= score) {
				pathes.add(i, new ScoredPath(score, path));
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
	public void run() {
		if (pathFoundListener != null) {
			pathFoundListener.pathFound(findPath());
		}
	}

	private class ScoredPath {

		private float score;

		private LinkedList<Vector2f> path;

		ScoredPath(float score, LinkedList<Vector2f> path) {
			this.score = score;
			this.path = path;
		}

	}

	@Override
	public void dispose() {
		try {
			if (path == null) {
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
		} catch (Exception e) {
		}
	}
}
