/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.action.map.colider;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.Grid2D;
import loon.action.map.heuristics.Manhattan;
import loon.geom.Vector2f;
import loon.utils.TArray;

/**
 * Tile寻径用类
 */
public class TileImplFinder {

	private final Grid2D grid;

	private final AStarFindHeuristic heuristic;

	private final boolean alldirection;

	public TileImplFinder(Grid2D grid) {
		this(grid, null, true);
	}

	public TileImplFinder(Grid2D grid, boolean all) {
		this(grid, null, all);
	}

	public TileImplFinder(Grid2D grid, AStarFindHeuristic heuristic, boolean all) {
		this.grid = grid;
		if (heuristic == null) {
			this.heuristic = new Manhattan();
		} else {
			this.heuristic = heuristic;
		}
		this.alldirection = all;
	}

	public Grid2D getGrid() {
		return grid;
	}

	public TArray<Vector2f> findVectorPath(int sourceX, int sourceY, int targetX, int targetY) {
		TArray<TileImpl> tiles = findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY));
		int size = tiles.size;
		TArray<Vector2f> result = new TArray<Vector2f>(size);
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				result.add(tiles.get(i).getPos());
			}
		}
		return result;
	}

	public TArray<TileImpl> findPath(int sourceX, int sourceY, int targetX, int targetY) {
		return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY));
	}

	public TArray<TileImpl> findPath(int sourceX, int sourceY, int targetX, int targetY, TArray<TileImpl> busyNodes) {
		return findPath(grid.getData(), grid.get(sourceX, sourceY), grid.get(targetX, targetY),
				busyNodes.toArray(new TileImpl[0]));
	}

	public TArray<TileImpl> findPath(TileImpl[][] grid, TileImpl source, TileImpl target, TileImpl... busyNodes) {
		if (source == target || target.getState().getResult() == TileState.WALL) {
			return new TArray<TileImpl>();
		}

		for (int y = 0; y < grid[0].length; y++) {
			for (int x = 0; x < grid.length; x++) {
				grid[x][y].setHCost(Math.abs(target.getX() - x) + Math.abs(target.getY() - y));
				grid[x][y].setParent(null);
				grid[x][y].setGCost(0);
			}
		}

		TArray<TileImpl> open = new TArray<TileImpl>();
		TArray<TileImpl> closed = new TArray<TileImpl>();

		TileImpl current = source;

		boolean found = false;

		while (!found && !closed.contains(target)) {

			for (TileImpl neighbor : getValidNeighbors(current, grid, busyNodes)) {
				if (neighbor == target) {
					target.setParent(current);
					found = true;
					closed.add(target);
					break;
				}

				if (!closed.contains(neighbor)) {
					if (open.contains(neighbor)) {
						float newG = current.getGCost()
								+ heuristic.getScore(current.getX(), current.getY(), neighbor.getX(), neighbor.getY());

						if (newG < neighbor.getGCost()) {
							neighbor.setParent(current);
							neighbor.setGCost(newG);
						}
					} else {
						neighbor.setParent(current);
						neighbor.setGCost(current.getGCost()
								+ heuristic.getScore(current.getX(), current.getY(), neighbor.getX(), neighbor.getY()));
						open.add(neighbor);
					}
				}
			}

			if (!found) {
				closed.add(current);
				open.remove(current);

				if (open.isEmpty()) {
					return new TArray<TileImpl>();
				}

				TileImpl acc = open.get(0);

				for (TileImpl a : open) {
					acc = a.getFCost() < acc.getFCost() ? a : acc;
				}

				current = acc;
			}
		}

		return buildPath(source, target);
	}

	private TArray<TileImpl> buildPath(TileImpl start, TileImpl target) {
		TArray<TileImpl> path = new TArray<TileImpl>();
		TileImpl tmp = target;
		do {
			path.add(tmp);
			tmp = tmp.getParent();
		} while (tmp != start);
		return path.reverse();
	}

	protected TArray<TileImpl> getValidNeighbors(TileImpl node, TileImpl[][] grid, TileImpl... busyNodes) {
		int x = node.getX();
		int y = node.getY();
		int[] points = this.alldirection
				? new int[] { x - 1, y, x + 1, y, x, y - 1, x, y + 1, x - 1, y - 1, x + 1, y + 1, x + 1, y - 1, x - 1,
						y + 1 }
				: new int[] { x - 1, y, x + 1, y, x, y - 1, x, y + 1 };

		TArray<TileImpl> result = new TArray<TileImpl>();

		for (int i = 0; i < points.length; i++) {
			int x1 = points[i];
			int y1 = points[++i];
			if (x1 >= 0 && x1 < grid.length && y1 >= 0 && y1 < grid[0].length
					&& grid[x1][y1].getState().getResult() == TileState.NOT_WALL && !contains(x1, y1, busyNodes)) {
				result.add(grid[x1][y1]);
			}
		}

		return result;
	}

	private boolean contains(int x, int y, TileImpl... cells) {
		for (TileImpl n : cells) {
			if (n.getX() == x && n.getY() == y) {
				return true;
			}
		}
		return false;
	}
}
