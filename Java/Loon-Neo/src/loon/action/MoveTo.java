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
package loon.action;

import loon.LSystem;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionResult;
import loon.action.map.AStarFindHeuristic;
import loon.action.map.AStarFinder;
import loon.action.map.Field2D;
import loon.geom.Vector2f;
import loon.utils.IntMap;
import loon.utils.TArray;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class MoveTo extends ActionEvent {

	// 寻径缓存，如果useCache为true时,moveTo将不理会实际寻径结果，全部按照缓存中的路线行走
	private final static IntMap<TArray<Vector2f>> pathCache = new IntMap<TArray<Vector2f>>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private Vector2f startLocation, endLocation;

	private Field2D layerMap;

	private boolean flag, useCache, synchroLayerField;

	private TArray<Vector2f> pActorPath;

	private int startX, startY, endX, endY, moveX, moveY;

	private int direction, speed;

	private AStarFindHeuristic heuristic;

	private Vector2f pLocation = new Vector2f();

	private boolean moveByMode = false;

	public MoveTo(float x, float y, boolean flag) {
		this(LSystem.viewSize.newField2D(), x, y, flag, 4);
	}

	public MoveTo(final Field2D map, float x, float y, boolean flag) {
		this(map, x, y, flag, 4);
	}

	public MoveTo(float x, float y, boolean flag, int speed) {
		this(LSystem.viewSize.newField2D(), x, y, flag, speed);
	}

	public MoveTo(final Field2D map, float x, float y, boolean flag, int speed) {
		this(map, -1f, -1f, x, y, flag, speed, true, false);
	}

	public MoveTo(float sx, float sy, float x, float y, boolean flag, int speed) {
		this(LSystem.viewSize.newField2D(), sx, sy, x, y, flag, speed, true, false);
	}

	public MoveTo(final Field2D map, float sx, float sy, float x, float y, boolean flag, int speed) {
		this(map, sx, sy, x, y, flag, speed, true, false);
	}

	public MoveTo(float sx, float sy, float x, float y, boolean flag, int speed, boolean cache, boolean synField) {
		this(LSystem.viewSize.newField2D(), sx, sy, x, y, flag, speed, cache, synField);
	}

	public MoveTo(final Field2D map, float sx, float sy, float x, float y, boolean flag, int speed, boolean cache,
			boolean synField) {
		this.startLocation = new Vector2f(sx, sy);
		this.endLocation = new Vector2f(x, y);
		this.layerMap = map;
		this.flag = flag;
		this.speed = speed;
		this.useCache = cache;
		this.synchroLayerField = synField;
		if (map == null) {
			moveByMode = true;
		}
	}

	public MoveTo(final Field2D map, Vector2f pos, boolean flag) {
		this(map, pos, flag, 4);
	}

	public MoveTo(final Field2D map, Vector2f pos, boolean flag, int speed) {
		this(map, pos.x(), pos.y(), flag, speed);
	}

	public void randomPathFinder() {
		synchronized (MoveTo.class) {
			AStarFindHeuristic afh = null;
			int index = MathUtils.random(AStarFindHeuristic.MANHATTAN, AStarFindHeuristic.CLOSEST_SQUARED);
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

	public void setMoveByMode(boolean m) {
		this.moveByMode = m;
		if (original != null) {
			this.startX = original.x();
			this.startY = original.y();
		}
		this.endX = endLocation.x();
		this.endY = endLocation.y();
	}

	@Override
	public void onLoad() {
		updatePath();
	}

	public void updatePath() {
		if (!moveByMode && original != null && LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null
				&& !LSystem.getProcess().getScreen().getRectBox().contains(original.x(), original.y())
				&& layerMap != null && !layerMap.inside(original.x(), original.y())) { // 处理越界出Field2D二维数组的移动
			setMoveByMode(true);
			return;
		} else if (moveByMode) {
			setMoveByMode(true);
			return;
		}
		if (layerMap == null || original == null) {
			return;
		}
		if (!(original.x() == endLocation.x() && original.y() == endLocation.y())) {
			if (useCache) {
				synchronized (pathCache) {
					if (pathCache.size > LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
						pathCache.clear();
					}
					int key = hashCode();
					TArray<Vector2f> final_path = pathCache.get(key);
					if (final_path == null) {
						final_path = AStarFinder.find(heuristic, layerMap,
								layerMap.pixelsToTilesWidth(startLocation.x()),
								layerMap.pixelsToTilesHeight(startLocation.y()),
								layerMap.pixelsToTilesWidth(endLocation.x()),
								layerMap.pixelsToTilesHeight(endLocation.y()), flag);
						pathCache.put(key, final_path);
					}
					pActorPath = new TArray<Vector2f>();
					pActorPath.addAll(final_path);
				}
			} else {
				pActorPath = AStarFinder.find(heuristic, layerMap, layerMap.pixelsToTilesWidth(startLocation.x()),
						layerMap.pixelsToTilesHeight(startLocation.y()), layerMap.pixelsToTilesWidth(endLocation.x()),
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
			if (pathCache != null) {
				pathCache.clear();
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
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesWidth(original.x()));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesHeight(original.y()));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesWidth(endLocation.x()));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesHeight(endLocation.y()));
		hashCode = LSystem.unite(hashCode, layerMap.getWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getHeight());
		hashCode = LSystem.unite(hashCode, layerMap.getTileWidth());
		hashCode = LSystem.unite(hashCode, layerMap.getTileHeight());
		hashCode = LSystem.unite(hashCode, CollectionUtils.hashCode(layerMap.getMap()));
		return hashCode;
	}

	@Override
	public ActionEvent start(ActionBind target) {
		super.start(target);
		startLocation.set(target.getX(), target.getY());
		return this;
	}

	public TArray<Vector2f> getPath() {
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
		if (moveByMode) {
			float x = original.getX();
			float y = original.getY();
			int dirX = (int) (endX - startX);
			int dirY = (int) (endY - startY);
			int count = 0;
			if (dirX > 0) {
				if (x >= endX) {
					count++;
				} else {
					x += speed;
				}
			} else if (dirX < 0) {
				if (x <= endX) {
					count++;
				} else {
					x -= speed;
				}
			} else {
				count++;
			}
			if (dirY > 0) {
				if (y >= endY) {
					count++;
				} else {
					y += speed;
				}
			} else if (dirY < 0) {
				if (y <= endY) {
					count++;
				} else {
					y -= speed;
				}
			} else {
				count++;
			}
			movePos(x + offsetX, y + offsetY);
			_isCompleted = (count == 2);
		} else {
			if (layerMap == null || original == null || pActorPath == null || pActorPath.size == 0) {
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
					if (pActorPath.size > 1) {
						Vector2f moveStart = pActorPath.get(0);
						Vector2f moveEnd = pActorPath.get(1);
						startX = layerMap.tilesToWidthPixels(moveStart.x());
						startY = layerMap.tilesToHeightPixels(moveStart.y());
						endX = moveEnd.x() * layerMap.getTileWidth();
						endY = moveEnd.y() * layerMap.getTileHeight();
						moveX = moveEnd.x() - moveStart.x();
						moveY = moveEnd.y() - moveStart.y();
						if (moveX > -2 && moveY > -2 && moveX < 2 && moveY < 2) {
							direction = Field2D.getDirection(moveX, moveY, direction);
						}
					}
					pActorPath.removeIndex(0);
				}
				switch (direction) {
				case Field2D.TUP:
					startY -= speed;
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Field2D.TDOWN:
					startY += speed;
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Field2D.TLEFT:
					startX -= speed;
					if (startX < endX) {
						startX = endX;
					}
					break;
				case Field2D.TRIGHT:
					startX += speed;
					if (startX > endX) {
						startX = endX;
					}
					break;
				case Field2D.UP:
					startX += speed;
					startY -= speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Field2D.DOWN:
					startX -= speed;
					startY += speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					break;
				case Field2D.LEFT:
					startX -= speed;
					startY -= speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					break;
				case Field2D.RIGHT:
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
				if (!(original.x() != 0 && original.y() != 0 && startX == 0 && startY == 0 && endX == 0 && endY == 0)) {
					synchronized (original) {
						float newX = startX + offsetX;
						float newY = startY + offsetY;
						movePathPos(newX, newY);
					}
				}
			}
		}
	}

	public void movePathPos(float newX, float newY) {
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(original, newX, newY, worldCollisionFilter);
			if (result.goalX != newX || result.goalY != newY) {
				clearPath();
				endLocation.set(result.goalX, result.goalY);
				startLocation.set(newX, newY);
				updatePath();
				original.setLocation(result.goalX, result.goalY);
			} else {
				original.setLocation(newX, newY);
			}
		} else {
			original.setLocation(newX, newY);
		}
	}

	public Vector2f nextPos() {
		if (pActorPath != null) {
			synchronized (pActorPath) {
				int size = pActorPath.size;
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

	public boolean isComplete() {
		return moveByMode ? _isCompleted
				: (pActorPath == null || pActorPath.size == 0 || _isCompleted || original == null);
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

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	@Override
	public ActionEvent cpy() {
		MoveTo move = new MoveTo(layerMap, -1, -1, endLocation.x, endLocation.y, flag, speed, useCache,
				synchroLayerField);
		move.set(this);
		move.heuristic = this.heuristic;
		return move;
	}

	@Override
	public ActionEvent reverse() {
		MoveTo move = new MoveTo(layerMap, -1, -1, oldX, oldY, flag, speed, useCache, synchroLayerField);
		move.set(this);
		move.heuristic = this.heuristic;
		return move;
	}

	@Override
	public String getName() {
		return "move";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("startLocation", startLocation).comma().kv("endLocation", endLocation).comma()
				.kv("layerMap", layerMap).comma().kv("direction", direction).comma().kv("speed", speed).comma()
				.kv("heuristic", heuristic);
		return builder.toString();
	}

}
