package loon.action;

import java.util.HashMap;
import java.util.LinkedList;

import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Config;
import loon.action.map.Field2D;
import loon.core.LSystem;
import loon.core.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;


/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class MoveTo extends ActionEvent {

	private final static HashMap<Integer, LinkedList<Vector2f>> pathCache = new HashMap<Integer, LinkedList<Vector2f>>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private Vector2f startLocation, endLocation;

	private Field2D layerMap;

	private boolean flag, useCache, synchroLayerField;

	private LinkedList<Vector2f> pActorPath;

	private int startX, startY, endX, endY, moveX, moveY;

	private int direction, speed;

	private AStarFindHeuristic heuristic;

	private Vector2f pLocation = new Vector2f();

	public MoveTo(final Field2D map, int x, int y, boolean flag) {
		this.startLocation = new Vector2f();
		this.endLocation = new Vector2f(x, y);
		this.layerMap = map;
		this.flag = flag;
		this.speed = 4;
		this.useCache = true;
		this.synchroLayerField = false;
	}

	public MoveTo(final Field2D map, Vector2f pos, boolean flag) {
		this(map, pos.x(), pos.y(), flag);
	}

	public void randomPathFinder() {
		synchronized (MoveTo.class) {
			AStarFindHeuristic afh = null;
			int index = MathUtils.random(AStarFindHeuristic.MANHATTAN,
					AStarFindHeuristic.CLOSEST_SQUARED);
			switch (index) {
			case AStarFindHeuristic.MANHATTAN:
				afh = AStarFinder.ASTAR_EUCLIDEAN;
				break;
			case AStarFindHeuristic.MIXING:
				afh = AStarFinder.ASTAR_MIXING;
				break;
			case AStarFindHeuristic.DIAGONAL:
				afh = AStarFinder.ASTAR_DIAGONAL;
				break;
			case AStarFindHeuristic.DIAGONAL_SHORT:
				afh = AStarFinder.ASTAR_DIAGONAL_SHORT;
				break;
			case AStarFindHeuristic.EUCLIDEAN:
				afh = AStarFinder.ASTAR_EUCLIDEAN;
				break;
			case AStarFindHeuristic.EUCLIDEAN_NOSQR:
				afh = AStarFinder.ASTAR_EUCLIDEAN_NOSQR;
				break;
			case AStarFindHeuristic.CLOSEST:
				afh = AStarFinder.ASTAR_CLOSEST;
				break;
			case AStarFindHeuristic.CLOSEST_SQUARED:
				afh = AStarFinder.ASTAR_CLOSEST_SQUARED;
				break;
			}
			setHeuristic(afh);
		}
	}

	public float[] getBeginPath() {
		return new float[] { startX, startY };
	}

	public float[] getEndPath() {
		return new float[] { endX, endY };
	}

	@Override
	public void onLoad() {
		if (layerMap == null || original == null) {
			return;
		}
		if (!original.getRectBox().contains(endLocation.x(), endLocation.y())) {
			if (useCache) {
				synchronized (pathCache) {
					if (pathCache.size() > LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
						pathCache.clear();
					}
					int key = hashCode();
					LinkedList<Vector2f> final_path = pathCache.get(key);
					if (final_path == null) {
						final_path = AStarFinder
								.find(heuristic,
										layerMap,
										layerMap.pixelsToTilesWidth(startLocation
												.x()),
										layerMap.pixelsToTilesHeight(startLocation
												.y()),
										layerMap.pixelsToTilesWidth(endLocation
												.x()),
										layerMap.pixelsToTilesHeight(endLocation
												.y()), flag);
						pathCache.put(key, final_path);
					}
					pActorPath = new LinkedList<Vector2f>();
					pActorPath.addAll(final_path);
				}
			} else {
				pActorPath = AStarFinder.find(heuristic, layerMap,
						layerMap.pixelsToTilesWidth(startLocation.x()),
						layerMap.pixelsToTilesHeight(startLocation.y()),
						layerMap.pixelsToTilesWidth(endLocation.x()),
						layerMap.pixelsToTilesHeight(endLocation.y()), flag);

			}
		}
	}

	public void clearPath() {
		if (pActorPath != null) {
			synchronized (pActorPath) {
				pActorPath.clear();
				pActorPath = null;
			}
		}
	}

	public static void clearPathCache() {
		if (pathCache != null) {
			synchronized (pathCache) {
				pathCache.clear();
			}
		}
	}

	@Override
	public int hashCode() {
		if (layerMap == null || original == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, flag);
		hashCode = LSystem.unite(hashCode,
				layerMap.pixelsToTilesWidth(original.x()));
		hashCode = LSystem.unite(hashCode,
				layerMap.pixelsToTilesHeight(original.y()));
		hashCode = LSystem.unite(hashCode,
				layerMap.pixelsToTilesWidth(endLocation.x()));
		hashCode = LSystem.unite(hashCode,
				layerMap.pixelsToTilesHeight(endLocation.y()));
		hashCode = LSystem.unite(hashCode, layerMap.getWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getHeight());
		hashCode = LSystem.unite(hashCode, layerMap.getTileWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getTileHeight());
		hashCode = LSystem.unite(hashCode,
				CollectionUtils.hashCode(layerMap.getMap()));
		return hashCode;
	}

	@Override
	public void start(ActionBind target) {
		super.start(target);
		startLocation.set(target.getX(), target.getY());
	}

	public LinkedList<Vector2f> getPath() {
		return pActorPath;
	}

	public int getDirection() {
		return direction;
	}

	public void setField2D(Field2D field) {
		if (field != null) {
			this.layerMap = field;
		}
	}

	public Field2D getField2D() {
		return layerMap;
	}

	@Override
	public void update(long elapsedTime) {
		if (layerMap == null || original == null || pActorPath == null) {
			return;
		}
		synchronized (pActorPath) {
			if (synchroLayerField) {
				if (original != null) {
					Field2D field = original.getField2D();
					if (field != null && layerMap != field) {
						this.layerMap = field;
					}
				}
			}
			if (endX == startX && endY == startY) {
				if (pActorPath.size() > 1) {
					Vector2f moveStart = pActorPath.get(0);
					Vector2f moveEnd = pActorPath.get(1);
					startX = layerMap.tilesToWidthPixels(moveStart.x());
					startY = layerMap.tilesToHeightPixels(moveStart.y());
					endX = moveEnd.x() * layerMap.getTileWidth();
					endY = moveEnd.y() * layerMap.getTileHeight();
					moveX = moveEnd.x() - moveStart.x();
					moveY = moveEnd.y() - moveStart.y();
					if (moveX > -2 && moveY > -2 && moveX < 2 && moveY < 2) {
						direction = Field2D.getDirection(moveX, moveY,
								direction);
					}
				}
				pActorPath.remove(0);
			}
			switch (direction) {
			case Config.TUP:
				startY -= speed;
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Config.TDOWN:
				startY += speed;
				if (startY > endY) {
					startY = endY;
				}
				break;
			case Config.TLEFT:
				startX -= speed;
				if (startX < endX) {
					startX = endX;
				}
				break;
			case Config.TRIGHT:
				startX += speed;
				if (startX > endX) {
					startX = endX;
				}
				break;
			case Config.UP:
				startX += speed;
				startY -= speed;
				if (startX > endX) {
					startX = endX;
				}
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Config.DOWN:
				startX -= speed;
				startY += speed;
				if (startX < endX) {
					startX = endX;
				}
				if (startY > endY) {
					startY = endY;
				}
				break;
			case Config.LEFT:
				startX -= speed;
				startY -= speed;
				if (startX < endX) {
					startX = endX;
				}
				if (startY < endY) {
					startY = endY;
				}
				break;
			case Config.RIGHT:
				startX += speed;
				startY += speed;
				if (startX > endX) {
					startX = endX;
				}
				if (startY > endY) {
					startY = endY;
				}
				break;
			}
			synchronized (original) {
				original.setLocation(startX + offsetX, startY + offsetY);
			}
		}
	}

	public Vector2f nextPos() {
		if (pActorPath != null) {
			synchronized (pActorPath) {
				int size = pActorPath.size();
				if (size > 0) {
					pLocation.set(endX, endY);
				} else {
					pLocation.set(original.getX(), original.getY());
				}
				return pLocation;
			}
		} else {
			pLocation.set(original.getX(), original.getY());
			return pLocation;
		}
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public boolean isComplete() {
		return pActorPath == null || pActorPath.size() == 0 || isComplete
				|| original == null;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public boolean isSynchroLayerField() {
		return synchroLayerField;
	}

	public void setSynchroLayerField(boolean syn) {
		this.synchroLayerField = syn;
	}

	public AStarFindHeuristic getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(AStarFindHeuristic heuristic) {
		this.heuristic = heuristic;
	}

}
