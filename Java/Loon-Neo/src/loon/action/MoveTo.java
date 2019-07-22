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

/**
 * 缓动对象移动用效果类,内置寻径和碰撞接口,可以自动实现寻径和障碍物回避效果(当然,也可以不寻径不检查碰撞而单纯移动)
 */
public class MoveTo extends ActionEvent {

	// 寻径缓存，如果useCache为true时,moveTo将不理会实际寻径结果，全部按照缓存中的路线行走
	private final static IntMap<TArray<Vector2f>> _PATH_CACHE = new IntMap<TArray<Vector2f>>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	// 默认每帧的移动数值(象素)
	private final static float _INIT_MOVE_SPEED = 4f;

	private Vector2f startLocation, endLocation;

	private Field2D layerMap;

	private boolean allDir, isMoved, useCache, synchroLayerField;

	private TArray<Vector2f> pActorPath;

	private float startX, startY, endX, endY, moveX, moveY, speed;

	private int direction;

	private AStarFindHeuristic heuristic;

	private Vector2f pLocation = new Vector2f();

	private boolean moveByMode = false;

	private boolean isDirUpdate = false;

	public MoveTo(float x, float y, boolean all) {
		this(null, x, y, all, _INIT_MOVE_SPEED);
	}

	public MoveTo(final Field2D map, float x, float y, boolean all) {
		this(map, x, y, all, _INIT_MOVE_SPEED);
	}

	public MoveTo(float x, float y, boolean all, float speed) {
		this(null, x, y, all, speed);
	}

	public MoveTo(final Field2D map, float x, float y, boolean all, float speed) {
		this(map, -1f, -1f, x, y, all, speed, true, false);
	}

	public MoveTo(float sx, float sy, float x, float y, boolean all, float speed) {
		this(null, sx, sy, x, y, all, speed, true, false);
	}

	public MoveTo(final Field2D map, float sx, float sy, float x, float y, boolean all, float speed) {
		this(map, sx, sy, x, y, all, speed, true, false);
	}

	public MoveTo(float sx, float sy, float x, float y, boolean all, float speed, boolean cache, boolean synField) {
		this(null, sx, sy, x, y, all, speed, cache, synField);
	}

	public MoveTo(final Field2D map, float sx, float sy, float x, float y, boolean all, float speed, boolean cache,
			boolean synField) {
		this.startLocation = new Vector2f(sx, sy);
		this.endLocation = new Vector2f(x, y);
		this.layerMap = map;
		this.allDir = all;
		this.speed = speed;
		this.useCache = cache;
		this.synchroLayerField = synField;
		this.direction = Field2D.EMPTY;
		if (map == null) {
			moveByMode = true;
		}
	}

	public MoveTo(final Field2D map, Vector2f pos, boolean allDir) {
		this(map, pos, allDir, _INIT_MOVE_SPEED);
	}

	public MoveTo(final Field2D map, Vector2f pos, boolean allDir, float speed) {
		this(map, pos.x(), pos.y(), allDir, speed);
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
				synchronized (_PATH_CACHE) {
					if (_PATH_CACHE.size > LSystem.DEFAULT_MAX_CACHE_SIZE * 10) {
						_PATH_CACHE.clear();
					}
					int key = hashCode();
					TArray<Vector2f> final_path = _PATH_CACHE.get(key);
					if (final_path == null) {
						final_path = AStarFinder.find(heuristic, layerMap,
								layerMap.pixelsToTilesWidth(startLocation.x()),
								layerMap.pixelsToTilesHeight(startLocation.y()),
								layerMap.pixelsToTilesWidth(endLocation.x()),
								layerMap.pixelsToTilesHeight(endLocation.y()), allDir);
						_PATH_CACHE.put(key, final_path);
					}
					pActorPath = new TArray<Vector2f>();
					pActorPath.addAll(final_path);
				}
			} else {
				pActorPath = AStarFinder.find(heuristic, layerMap, layerMap.pixelsToTilesWidth(startLocation.x()),
						layerMap.pixelsToTilesHeight(startLocation.y()), layerMap.pixelsToTilesWidth(endLocation.x()),
						layerMap.pixelsToTilesHeight(endLocation.y()), allDir);

			}
		}
	}

	public void clearPath() {
		if (pActorPath != null) {
			synchronized (pActorPath) {
				if (pActorPath != null) {
					pActorPath.clear();
				}
			}
			clearPathCache();
		}
	}

	public static void clearPathCache() {
		if (_PATH_CACHE != null) {
			synchronized (_PATH_CACHE) {
				if (_PATH_CACHE != null) {
					_PATH_CACHE.clear();
				}
			}
		}
	}

	@Override
	public int hashCode() {
		if (layerMap == null || original == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, allDir);
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

	public float getStartX() {
		return startX == 0 ? startLocation.x : startX;
	}

	public float getStartY() {
		return startY == 0 ? startLocation.y : startY;
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
		isMoved = true;
		float newX = 0f;
		float newY = 0f;
		if (moveByMode) {
			int count = 0;
			int dirX = (int) (endX - startX);
			int dirY = (int) (endY - startY);
			int dir = Field2D.getDirection(dirX, dirY, direction);
			if (allDir) {
				newX = original.getX();
				newY = original.getY();
				if (dirX > 0) {
					if (newX >= endX) {
						count++;
					} else {
						newX += speed;
					}
				} else if (dirX < 0) {
					if (newX <= endX) {
						count++;
					} else {
						newX -= speed;
					}
				} else {
					count++;
				}
				if (dirY > 0) {
					if (newY >= endY) {
						count++;
					} else {
						newY += speed;
					}
				} else if (dirY < 0) {
					if (newY <= endY) {
						count++;
					} else {
						newY -= speed;
					}
				} else {
					count++;
				}
				if (count > 0) {
					isMoved = false;
				}
				if (!checkTileCollision(layerMap, original, newX, newY)) {
					float lastX = original.getX();
					float lastY = original.getY();
					newX += offsetX;
					newY += offsetY;
					updateDirection((int) (newX - lastX), (int) (newY - lastY));
					movePos(newX, newY);
				}
				_isCompleted = (count == 2);
			} else {
				startX = original.getX() - offsetX;
				startY = original.getY() - offsetY;
				switch (dir) {
				case Field2D.TUP:
				case Field2D.UP:
					startY -= speed;
					if (startY < endY) {
						startY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TDOWN:
				case Field2D.DOWN:
					startY += speed;
					if (startY > endY) {
						startY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TLEFT:
				case Field2D.LEFT:
					startX -= speed;
					if (startX < endX) {
						startX = endX;
						isMoved = false;
					}
					break;
				case Field2D.TRIGHT:
				case Field2D.RIGHT:
					startX += speed;
					if (startX > endX) {
						startX = endX;
						isMoved = false;
					}
					break;
				}
				float lastX = original.getX();
				float lastY = original.getY();
				if (!checkTileCollision(layerMap, original, startX, startY)) {
					newX = startX + offsetX;
					newY = startY + offsetY;
					if (isMoved) {
						updateDirection((int) (newX - lastX), (int) (newY - lastY));
					}
					movePos(newX, newY);
				}
				if (endX - startX == 0 && endY - startY == 0) {
					_isCompleted = true;
				}
			}
		} else {
			if (original == null || pActorPath == null || pActorPath.size == 0) {
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
						if (layerMap != null) {
							startX = layerMap.tilesToWidthPixels(moveStart.x());
							startY = layerMap.tilesToHeightPixels(moveStart.y());
							endX = layerMap.tilesToWidthPixels(moveEnd.x());
							endY = layerMap.tilesToHeightPixels(moveEnd.y());
						} else {
							startX = moveStart.getX() * original.getWidth();
							startY = moveStart.getY() * original.getHeight();
							endX = moveEnd.getX() * original.getWidth();
							endY = moveEnd.getY() * original.getHeight();
						}
						moveX = moveEnd.x() - moveStart.x();
						moveY = moveEnd.y() - moveStart.y();
						updateDirection(moveX, moveY);
					}
					pActorPath.removeIndex(0);
				} else {
					moveX = endX - startX;
					moveY = endY - startY;
					updateDirection(moveX, moveY);
				}

				newX = original.getX() - offsetX;
				newY = original.getY() - offsetY;
				switch (direction) {
				case Field2D.TUP:
					startY -= speed;
					newY -= speed;
					if (startY < endY) {
						startY = endY;
					}
					if (newY < endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TDOWN:
					startY += speed;
					newY += speed;
					if (startY > endY) {
						startY = endY;
					}
					if (newY > endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TLEFT:
					startX -= speed;
					newX -= speed;
					if (startX < endX) {
						startX = endX;
					}
					if (newX < endX) {
						newX = endX;
						isMoved = false;
					}
					break;
				case Field2D.TRIGHT:
					startX += speed;
					newX += speed;
					if (startX > endX) {
						startX = endX;
					}
					if (newX > endX) {
						newX = endX;
						isMoved = false;
					}
					break;
				case Field2D.UP:
					startX += speed;
					startY -= speed;
					newX += speed;
					newY -= speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					if (newX > endX) {
						newX = endX;
						isMoved = false;
					}
					if (newY < endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				case Field2D.DOWN:
					startX -= speed;
					startY += speed;
					newX -= speed;
					newY += speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					if (newX < endX) {
						newX = endX;
						isMoved = false;
					}
					if (newY > endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				case Field2D.LEFT:
					startX -= speed;
					startY -= speed;
					newX -= speed;
					newY -= speed;
					if (startX < endX) {
						startX = endX;
					}
					if (startY < endY) {
						startY = endY;
					}
					if (newX < endX) {
						newX = endX;
						isMoved = false;
					}
					if (newY < endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				case Field2D.RIGHT:
					startX += speed;
					startY += speed;
					newX += speed;
					newY += speed;
					if (startX > endX) {
						startX = endX;
					}
					if (startY > endY) {
						startY = endY;
					}
					if (newX > endX) {
						newX = endX;
						isMoved = false;
					}
					if (newY > endY) {
						newY = endY;
						isMoved = false;
					}
					break;
				}
				if (!checkTileCollision(layerMap, original, newX, newY)) {
					synchronized (original) {
						newX += offsetX;
						newY += offsetY;
						movePathPos(newX, newY);
					}
				}
			}
		}
		isMoved = !_isCompleted;
	}

	public boolean isMoving() {
		return isMoved;
	}

	protected final boolean checkTileCollision(Field2D field2d, ActionBind bind, float newX, float newY) {
		if (field2d == null) {
			return false;
		}
		return field2d.checkTileCollision(bind.getX() - offsetX, bind.getY() - offsetY, bind.getWidth(),
				bind.getHeight(), newX, newY);
	}

	public void movePathPos(float newX, float newY) {
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(original, newX, newY, worldCollisionFilter);
			if ((result.goalX != newX || result.goalY != newY)) {
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

	public float getSpeed() {
		return speed;
	}

	public MoveTo setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	@Override
	public boolean isComplete() {
		return moveByMode ? _isCompleted
				: (pActorPath == null || pActorPath.size == 0 || _isCompleted || original == null);
	}

	public boolean isDirectionUpdate() {
		return isDirUpdate;
	}

	public void updateDirection(float x, float y) {
		int oldDir = direction;
		direction = Field2D.getDirection((int) x, (int) y, oldDir);
		isDirUpdate = (oldDir != direction);
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
		return endX == 0 ? endLocation.x : endX;
	}

	public float getEndY() {
		return endY == 0 ? endLocation.y : endY;
	}

	@Override
	public ActionEvent cpy() {
		MoveTo move = new MoveTo(layerMap, -1, -1, endLocation.x, endLocation.y, allDir, speed, useCache,
				synchroLayerField);
		move.set(this);
		move.heuristic = this.heuristic;
		return move;
	}

	@Override
	public ActionEvent reverse() {
		MoveTo move = new MoveTo(layerMap, -1, -1, oldX, oldY, allDir, speed, useCache, synchroLayerField);
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
