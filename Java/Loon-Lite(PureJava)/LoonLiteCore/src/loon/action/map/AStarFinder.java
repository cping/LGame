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
import loon.action.map.heuristics.DiagonalMax;
import loon.action.map.heuristics.DiagonalMin;
import loon.action.map.heuristics.DiagonalShort;
import loon.action.map.heuristics.Euclidean;
import loon.action.map.heuristics.EuclideanNoSQR;
import loon.action.map.heuristics.Manhattan;
import loon.action.map.heuristics.Mixing;
import loon.action.map.heuristics.Octile;
import loon.events.Updateable;
import loon.geom.Vector2f;
import loon.utils.IntMap;
import loon.utils.ObjectSet;
import loon.utils.SortedList;
import loon.utils.TArray;

/**
 * A*寻径用类
 */
public class AStarFinder implements Updateable, LRelease {

	public static final int DIJKSTRA = 0;

	public static final int ASTAR = 1;

	private class ScoredPath {

		private float score;

		private TArray<Vector2f> pathList;

		private ScoredPath(float score, TArray<Vector2f> pathList) {
			this.score = score;
			this.pathList = pathList;
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

	public final static AStarFindHeuristic ASTAR_DIAGONAL_MAX = new DiagonalMax();

	private final static IntMap<TArray<Vector2f>> FINDER_LAZY = new IntMap<TArray<Vector2f>>(128);

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
		hashCode = LSystem.unite(hashCode, heuristic.getTypeCode());
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
				AStarFinder astar = new AStarFinder(heuristic, ASTAR);
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
		return find(heuristic, maps, null, x1, y1, x2, y2, flag);
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

	private Vector2f _goal;

	private SortedList<ScoredPath> _nextList;

	private TArray<Vector2f> _pathList;

	private ObjectSet<Vector2f> _openList;

	private ObjectSet<Vector2f> _closedList;

	private ScoredPath _spath;

	private boolean _flying, _alldirMove, _closed, _running;

	private Field2D _findMap;

	private int _algorithm = ASTAR;

	private int _overflow = 4096;

	private int _startX, _startY, _endX, _endY;

	private AStarFinderListener _pathFoundListener;

	private AStarFindHeuristic _findHeuristic;

	public AStarFinder(AStarFindHeuristic heuristic, int algorithm) {
		this(heuristic, false, algorithm);
	}

	public AStarFinder(AStarFindHeuristic heuristic, boolean flying, int algorithm) {
		this(heuristic, (Field2D) null, 0, 0, 0, 0, flying, false, algorithm);
	}

	public AStarFinder(AStarFindHeuristic heuristic, Field2D m, int startX, int startY, int endX, int endY,
			boolean flying, boolean flag, int algorithm) {
		this(heuristic, m, startX, startY, endX, endY, flying, flag, null, algorithm);
	}

	public AStarFinder(AStarFindHeuristic heuristic, Field2D m, int startX, int startY, int endX, int endY,
			boolean flying, boolean flag, AStarFinderListener callback, int algorithm) {
		this._findMap = m;
		this._startX = startX;
		this._startY = startY;
		this._endX = endX;
		this._endY = endY;
		this._flying = flying;
		this._alldirMove = flag;
		this._pathFoundListener = callback;
		this._findHeuristic = heuristic;
		this._algorithm = algorithm;
	}

	public AStarFinder update(AStarFinder find) {
		this._findMap = find._findMap;
		this._startX = find._startX;
		this._startY = find._startY;
		this._endX = find._endX;
		this._endY = find._endY;
		this._flying = find._flying;
		this._alldirMove = find._alldirMove;
		this._findHeuristic = find._findHeuristic;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof AStarFinder) {
			return this._pathFoundListener == ((AStarFinder) o)._pathFoundListener;
		}
		return false;
	}

	private TArray<Vector2f> calc(Field2D m, Vector2f start, Vector2f goal, boolean flag) {
		if (start.equals(goal)) {
			TArray<Vector2f> v = new TArray<Vector2f>();
			v.add(start);
			return v;
		}
		this._goal = goal;
		if (_openList == null) {
			_openList = new ObjectSet<Vector2f>();
		} else {
			_openList.clear();
		}
		if (_closedList == null) {
			_closedList = new ObjectSet<Vector2f>();
		} else {
			_closedList.clear();
		}
		if (_nextList == null) {
			_nextList = new SortedList<ScoredPath>();
		} else {
			_nextList.clear();
		}
		_openList.add(start);
		if (_pathList == null) {
			_pathList = new TArray<Vector2f>();
		} else {
			_pathList.clear();
		}
		_pathList.add(start);
		if (_spath == null) {
			_spath = new ScoredPath(0, _pathList);
		} else {
			_spath.score = 0;
			_spath.pathList = _pathList;
		}
		_nextList.add(_spath);
		return findPath(m, flag, _algorithm);
	}

	public AStarFinder setOverflow(int over) {
		this._overflow = over;
		return this;
	}

	public int getOverflow() {
		return this._overflow;
	}

	public AStarFinder stop() {
		this._running = false;
		return this;
	}

	public boolean isRunning() {
		return this._running;
	}

	public TArray<Vector2f> findPath() {
		Vector2f start = new Vector2f(_startX, _startY);
		Vector2f over = new Vector2f(_endX, _endY);
		return calc(_findMap, start, over, _alldirMove);
	}

	public TArray<Vector2f> findPath(Field2D map, boolean diagonal, int algorithm) {
		_running = true;
		for (int j = 0; _nextList.size > 0; j++) {
			if (j > _overflow) {
				_nextList.clear();
				break;
			}
			if (!_running) {
				break;
			}
			ScoredPath spath = _nextList.pop();
			Vector2f current = spath.pathList.get(spath.pathList.size - 1);
			if (algorithm == ASTAR) {
				_closedList.add(current);
			}
			if (current.equals(_goal)) {
				return new TArray<Vector2f>(spath.pathList);
			}
			TArray<Vector2f> step = map.neighbors(current, diagonal);
			final int size = step.size;
			for (int i = 0; i < size; i++) {
				Vector2f next = step.get(i);
				if (!map.isHit(next) && !_flying) {
					continue;
				}
				if (algorithm == ASTAR) {
					if (_closedList.contains(next)) {
						continue;
					}
					if (!_openList.add(next)) {
						continue;
					}
				} else {
					_openList.add(next);
				}

				TArray<Vector2f> pathList = new TArray<Vector2f>(spath.pathList);
				pathList.add(next);
				float score = spath.score + _findHeuristic.getScore(_goal.x, _goal.y, next.x, next.y);
				insert(score, pathList);
			}
		}
		return null;
	}

	private void insert(float score, TArray<Vector2f> list) {
		int size = _nextList.size;
		for (int i = 0; i < size; i++) {
			ScoredPath spath = _nextList.get(i);
			if (spath.score >= score) {
				_nextList.add(new ScoredPath(score, list));
				return;
			}
		}
		_nextList.add(new ScoredPath(score, list));
	}

	public int getStartX() {
		return _startX;
	}

	public int getStartY() {
		return _startY;
	}

	public int getEndX() {
		return _endX;
	}

	public int getEndY() {
		return _endY;
	}

	public boolean isFlying() {
		return _flying;
	}

	public boolean isAllDirectionMove() {
		return _alldirMove;
	}

	@Override
	public void action(Object o) {
		if (_pathFoundListener != null) {
			_pathFoundListener.pathFound(findPath());
		}
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_pathList != null) {
			_pathList.clear();
			_pathList = null;
		}
		if (_nextList != null) {
			_nextList.clear();
			_nextList = null;
		}
		if (_openList != null) {
			_openList.clear();
			_openList = null;
		}
		if (_closedList != null) {
			_closedList.clear();
			_closedList = null;
		}
		_spath = null;
		_goal = null;
		_closed = true;
		_running = false;
	}

}
