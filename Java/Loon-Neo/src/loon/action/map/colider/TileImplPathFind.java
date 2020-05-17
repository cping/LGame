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
package loon.action.map.colider;

import loon.action.map.AStarFindHeuristic;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

/**
 * 针对TileImpl这个Tile接口实现的专属寻径用工具
 */
public class TileImplPathFind {

	public static TArray<TileImpl> find(TileManager tm, ObjectMap<Integer, ObjectMap<Integer, TileImpl>> list,
			TileImpl fromT, TileImpl toT) {
		return TileImplPathFind.find(tm, tm.getHeuristic(), list, fromT, toT, true);
	}

	public static TArray<TileImpl> find(TileManager tm, AStarFindHeuristic heuristic,
			ObjectMap<Integer, ObjectMap<Integer, TileImpl>> list, TileImpl fromT, TileImpl toT, boolean isPlayer) {
		TArray<TileImpl> open = new TArray<TileImpl>();

		TileImpl currentTile = null;
		boolean pathFound = false;

		for (Entries<Integer, ObjectMap<Integer, TileImpl>> it = list.iterator(); it.hasNext();) {
			Entry<Integer, ObjectMap<Integer, TileImpl>> entry = it.next();
			ObjectMap<Integer, TileImpl> tiles = entry.value;
			for (TileImpl tiley : tiles.values()) {
				tiley.open = false;
				tiley.closed = false;
				tiley.parent = null;
				tiley.H = TileImplPathFind.getDist(heuristic, tiley, toT);
			}
		}

		fromT.open = true;
		open.add(fromT);

		while (!pathFound) {

			currentTile = TileImplPathFind.getLowestF(open);

			if (currentTile == null) {
				break;
			}
			currentTile.closed = true;

			if (toT.closed) {
				pathFound = true;
				break;
			}

			for (Vector2f tc : currentTile.getNeighbours()) {
				if (list.containsKey(tc.x())) {
					ObjectMap<Integer, TileImpl> impl = list.get(tc.x());
					if (impl != null && impl.containsKey(tc.y())) {
						TileImpl neighbour = impl.get(tc.y());
						if (neighbour.solid) {
							continue;
						}
						if (neighbour.closed) {
							continue;
						}
						if (isPlayer && (tm.getLimits() != null && tm.getLimits().size > 0
								&& (tm.getLimit(neighbour.getX(), neighbour.getY()) == 0)
								|| tm.getLimit(neighbour.getX(), neighbour.getY()) == neighbour.idx)) {
							continue;
						}
						if (!neighbour.open) {
							neighbour.G = TileImplPathFind.getGScore(heuristic, neighbour, currentTile);
							neighbour.parent = currentTile;
							neighbour.open = true;
							open.add(neighbour);
						} else {
							float tmpG = TileImplPathFind.getGScore(heuristic, neighbour, currentTile);
							if ((neighbour.H + tmpG) < neighbour.getWeight()) {
								neighbour.G = tmpG;
								neighbour.parent = currentTile;
							}
						}
					}
				} else {
					TileImpl neighbour = new TileImpl(0, tc.x(), tc.y());
					neighbour.calcNeighbours(tm.getWidth(), tm.getHeight());
					float tmpG = TileImplPathFind.getGScore(heuristic, neighbour, currentTile);
					if ((neighbour.H + tmpG) < neighbour.getWeight()) {
						neighbour.G = tmpG;
						neighbour.parent = currentTile;
					}
				}
			}
		}

		TArray<TileImpl> path = new TArray<TileImpl>();
		path.add(toT);

		if (currentTile != null) {
			while (currentTile != fromT) {
				if (currentTile.parent != null) {
					currentTile = currentTile.parent;
					path.add(currentTile);
				}
			}
		}

		path.reverse();

		if (tm != null) {
			path = TileImplPathFind.optomisePath(tm, path);
		}

		path.removeIndex(0);

		return path;

	}

	private static TArray<TileImpl> optomisePath(TileManager tm, TArray<TileImpl> path) {

		TileImpl previousTile = null;
		TileImpl currentTile = null;

		TArray<TileImpl> returnList = new TArray<TileImpl>(path);

		for (TileImpl t : path) {

			if (currentTile == null) {
				currentTile = t;
				continue;
			}

			if (previousTile == null) {
				previousTile = t;
				continue;
			}

			if (inSight(tm, currentTile, t, true)) {

				returnList.remove(previousTile);
				previousTile = t;

				continue;
			} else {
				currentTile = previousTile;
				previousTile = t;
			}
		}

		return returnList;
	}

	private static TileImpl getLowestF(TArray<TileImpl> openList) {
		float lowest_w = 0;
		TileImpl lowest_tile = null;
		for (TileImpl t : openList) {
			float w = t.getWeight();
			if (lowest_tile != null && w >= lowest_w) {
				continue;
			}
			lowest_tile = t;
			lowest_w = w;
		}
		if (lowest_tile != null) {
			openList.remove(lowest_tile);
		}
		return lowest_tile;
	}

	public static float getGScore(AStarFindHeuristic heuristic, TileImpl tile, TileImpl ptile) {
		if (heuristic != null) {
			return heuristic.getScore(tile.getX(), tile.getY(), ptile.getX(), ptile.getY());
		}
		if (ptile.getX() != tile.getX() && ptile.getY() != tile.getY()) {
			return ptile.G + 15;
		}
		return ptile.G + 10;
	}

	public static float getDist(AStarFindHeuristic heuristic, TileImpl f, TileImpl t) {
		if (heuristic != null) {
			return heuristic.getScore(f.getX(), f.getY(), t.getX(), t.getY());
		}
		int xDiff = MathUtils.abs(f.getX() - t.getX());
		int yDiff = MathUtils.abs(f.getY() - t.getY());
		return (xDiff + yDiff) * 10;
	}

	public static boolean inSight(TileManager tm, TileImpl from, TileImpl to) {
		return inSight(tm, from, to, false);
	}

	public static boolean inSight(TileManager tm, TileImpl from, TileImpl to, boolean ignoreTransluscent) {
		if (from == to) {
			return true;
		}

		int fromX = _toPx(tm, from.getX()), fromY = _toPx(tm, from.getY()), toX = _toPx(tm, to.getX()),
				toY = _toPx(tm, to.getY());

		int xDiff = MathUtils.abs(fromX - toX);
		int yDiff = MathUtils.abs(fromY - toY);

		float xMove, yMove;
		float currentX = fromX, currentY = fromY;

		if (xDiff == 0) {
			xMove = 0;
			yMove = tm.getRate();
		} else if (yDiff == 0) {
			yMove = 0;
			xMove = tm.getRate();
		} else if (xDiff > yDiff) {
			float ratio = (yDiff * 1f) / (xDiff * 1f);
			xMove = tm.getRate();
			yMove = tm.getRate() * ratio;
		} else {
			float ratio = (xDiff * 1f) / (yDiff * 1f);
			yMove = tm.getRate();
			xMove = tm.getRate() * ratio;
		}

		if (fromX > toX) {
			xMove = -xMove;
		}
		if (fromY > toY) {
			yMove = -yMove;
		}

		boolean finding = true;
		while (finding) {
			currentX += xMove;
			currentY += yMove;

			TileImpl t = _toTile(tm, currentX, currentY);
			if (t == to) {
				finding = false;
			} else if (t.solid) {
				if (ignoreTransluscent || t.solidType == tm.getSolid()) {
					return false;
				}
			}
		}

		return true;
	}

	private static int _toPx(TileManager tm, int c) {
		return (int) ((c * tm.getScale()) + (tm.getScale() / 2));
	}

	private static TileImpl _toTile(TileManager tm, float cX, float cY) {
		int x = (int) cX, y = (int) cY;
		int tileX = (int) (x / tm.getScale());
		int tileY = (int) (y / tm.getScale());
		return tm.getTile(tileX, tileY);
	}

}
