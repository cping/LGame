package org.loon.framework.javase.game.action.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.loon.framework.javase.game.core.geom.Vector2D;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class AStarFinder implements Runnable {

	private Vector2D goal;

	private LinkedList<ScoredPath> pathes;

	private LinkedList<Vector2D> path;

	private Set<Vector2D> visitedCache;

	private ScoredPath spath;

	private static Vector2D start, over;

	private static Field2D fieldMap;

	private static AStarFinder astar;

	private boolean flying, flag;

	private Field2D field;

	private int startX, startY, endX, endY;

	private AStarFinderListener pathFoundListener;

	public AStarFinder() {
		this(false);
	}

	public AStarFinder(boolean flying) {
		this.flying = flying;
	}

	public AStarFinder(Field2D field, int startX, int startY, int endX,
			int endY, boolean flying, boolean flag, AStarFinderListener callback) {
		this.field = field;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.flying = flying;
		this.flag = flag;
		this.pathFoundListener = callback;
	}

	public AStarFinder(Field2D field, int startX, int startY, int endX,
			int endY, boolean flying, boolean flag) {
		this(field, startX, startY, endX, endY, flying, flag, null);
	}

	public void update(AStarFinder find) {
		this.field = find.field;
		this.startX = find.startX;
		this.startY = find.startY;
		this.endX = find.endX;
		this.endY = find.endY;
		this.flying = find.flying;
		this.flag = find.flag;
	}

	public boolean equals(Object o) {
		if (o instanceof AStarFinder) {
			return this.pathFoundListener == ((AStarFinder) o).pathFoundListener;
		}
		return false;
	}

	public static LinkedList<Vector2D> find(int[][] maps, int x1, int y1,
			int x2, int y2, boolean flag) {
		if (start == null) {
			start = new Vector2D(x1, y1);
		} else {
			start.set(x1, y1);
		}
		if (over == null) {
			over = new Vector2D(x2, y2);
		} else {
			over.set(x2, y2);
		}
		return find(maps, start, over, flag);
	}

	public static LinkedList<Vector2D> find(Field2D maps, int x1, int y1,
			int x2, int y2, boolean flag) {
		if (astar == null) {
			astar = new AStarFinder();
		}
		if (start == null) {
			start = new Vector2D(x1, y1);
		} else {
			start.set(x1, y1);
		}
		if (over == null) {
			over = new Vector2D(x2, y2);
		} else {
			over.set(x2, y2);
		}
		return astar.calc(maps, start, over, flag);
	}

	public static LinkedList<Vector2D> find(Field2D maps, Vector2D start,
			Vector2D goal, boolean flag) {
		if (astar == null) {
			astar = new AStarFinder();
		}
		return astar.calc(maps, start, goal, flag);
	}

	public static LinkedList<Vector2D> find(int[][] maps, Vector2D start,
			Vector2D goal, boolean flag) {
		if (astar == null) {
			astar = new AStarFinder();
		}
		if (fieldMap == null) {
			fieldMap = new Field2D(maps);
		} else {
			fieldMap.setMap(maps);
		}
		return astar.calc(fieldMap, start, goal, flag);
	}

	public LinkedList<Vector2D> findPath() {
		if (start == null) {
			start = new Vector2D(startX, startY);
		} else {
			start.set(startX, startY);
		}
		if (over == null) {
			over = new Vector2D(endX, endY);
		} else {
			over.set(endX, endY);
		}
		return calc(field, start, over, flag);
	}

	private LinkedList<Vector2D> calc(Field2D field, Vector2D start,
			Vector2D goal, boolean flag) {
		if (start.equals(goal)) {
			LinkedList<Vector2D> v = new LinkedList<Vector2D>();
			v.add(start);
			return v;
		}
		this.goal = goal;
		if (visitedCache == null) {
			visitedCache = new HashSet<Vector2D>();
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
			path = new LinkedList<Vector2D>();
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

	private LinkedList<Vector2D> astar(Field2D field, boolean flag) {
		for (; pathes.size() > 0;) {
			ScoredPath spath = pathes.remove(0);
			Vector2D current = spath.path.get(spath.path.size() - 1);
			if (current.equals(goal)) {
				return spath.path;
			}
			ArrayList<Vector2D> list = field.neighbors(current, flag);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				Vector2D next = list.get(i);
				if (visitedCache.contains(next)) {
					continue;
				}
				visitedCache.add(next);
				if (!field.isHit(next) && !flying) {
					continue;
				}
				LinkedList<Vector2D> path = new LinkedList<Vector2D>(spath.path);
				path.add(next);
				int score = spath.score + field.score(goal, next);
				insert(score, path);
			}
		}
		return null;
	}

	private void insert(int score, LinkedList<Vector2D> path) {
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

	public void run() {
		if (pathFoundListener != null) {
			pathFoundListener.pathFound(findPath());
		}
	}

	private class ScoredPath {

		private int score;

		private LinkedList<Vector2D> path;

		ScoredPath(int score, LinkedList<Vector2D> path) {
			this.score = score;
			this.path = path;
		}

	}
}
