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
package loon.action;

import loon.LSystem;
import loon.action.collision.CollisionFilter;
import loon.action.collision.CollisionResult;
import loon.action.map.CustomPath;
import loon.action.map.Field2D;
import loon.geom.Vector2f;
import loon.utils.CollectionUtils;
import loon.utils.Easing.EasingMode;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.timer.EaseTimer;

/**
 * 一个自定义移动路径使用的缓动效果类,它的实现需要CustomPath类配置配合
 */
public class DefineMoveTo extends ActionEvent {

	// 默认每帧的移动数值(象素)
	private final static float _INIT_MOVE_SPEED = 4f;

	private CustomPath initPath, layerPath;

	private Field2D layerMap;

	private boolean allDir, isMoved, moveByMode, synchroLayerField;

	private float startX = -1f, startY = -1f, endX = -1f, endY = -1f, moveX, moveY, speed;

	private int direction;

	private Vector2f pLocation = new Vector2f();

	private boolean isDirUpdate = false;

	/**
	 * 使用自定义路径移动
	 * 
	 * @param path
	 */
	public DefineMoveTo(final CustomPath path) {
		this(null, path, true);
	}

	/**
	 * 使用自定义路径移动,并设置移动速度
	 * 
	 * @param path
	 * @param speed
	 */
	public DefineMoveTo(final CustomPath path, final float speed) {
		this(null, path, true, speed);
	}

	/**
	 * 使用自定义路径移动,并设置8方向或4方向移动
	 * 
	 * @param path
	 * @param all
	 */
	public DefineMoveTo(final CustomPath path, final boolean all) {
		this(null, path, all);
	}

	/**
	 * 注入二维数组地图,使用自定义路径移动,并设置8方向或4方向移动
	 * 
	 * @param map
	 * @param path
	 * @param all
	 */
	public DefineMoveTo(final Field2D map, final CustomPath path, final boolean all) {
		this(map, path, all, _INIT_MOVE_SPEED);
	}

	public DefineMoveTo(final Field2D map, final CustomPath path, final boolean all, final float speed) {
		this.layerMap = map;
		this._easeTimer = EaseTimer.at(1f, EasingMode.Linear);
		if (path != null) {
			this.initPath = path;
			this.layerPath = initPath.cpy();
		}
		this.allDir = all;
		this.speed = speed;
		this.direction = Field2D.EMPTY;
		if (layerPath == null) {
			moveByMode = true;
		}
	}

	public float[] getBeginPath() {
		return new float[] { startX, startY };
	}

	public float[] getEndPath() {
		return new float[] { endX, endY };
	}

	public DefineMoveTo setMoveByMode(boolean m) {
		this.moveByMode = m;
		if (original != null && (startX == -1f && startY == -1f)) {
			this.startX = original.x();
			this.startY = original.y();
		}
		if (layerPath != null && (endX == -1f && endY == -1f)) {
			Vector2f end = layerPath.first();
			this.endX = end.x();
			this.endY = end.y();
		}
		return this;
	}

	protected float getMoveSpeed(long elapsedTime) {
		_easeTimer.update(elapsedTime);
		return speed * _easeTimer.getProgress();
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
	}

	public void clearPath() {
		if (layerPath != null) {
			layerPath.clear();
		}
	}

	@Override
	public int hashCode() {
		if (layerMap == null || original == null) {
			return super.hashCode();
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, allDir);
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesWidth(startX));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesHeight(startY));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesWidth(endX));
		hashCode = LSystem.unite(hashCode, layerMap.pixelsToTilesHeight(endY));
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
		startX = target.getX();
		startY = target.getY();
		if (layerPath != null) {
			Vector2f end = layerPath.first();
			this.endX = end.x();
			this.endY = end.y();
		}
		return this;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
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
		final float moveSpeed = getMoveSpeed(elapsedTime);
		isMoved = true;
		float newX = 0f;
		float newY = 0f;
		if (moveByMode) {
			int count = 0;
			float dirX = (endX - startX);
			float dirY = (endY - startY);
			int dir = Field2D.getDirection(startX, startY, endX, endY);
			if (allDir) {
				newX = original.getX();
				newY = original.getY();
				if (dirX > 0) {
					if (newX >= endX) {
						count++;
					} else {
						newX += moveSpeed;
					}
				} else if (dirX < 0) {
					if (newX <= endX) {
						count++;
					} else {
						newX -= moveSpeed;
					}
				} else {
					count++;
				}
				if (dirY > 0) {
					if (newY >= endY) {
						count++;
					} else {
						newY += moveSpeed;
					}
				} else if (dirY < 0) {
					if (newY <= endY) {
						count++;
					} else {
						newY -= moveSpeed;
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
					updateDirection((newX - lastX), (newY - lastY));
					movePos(newX, newY);
				}
				_isCompleted = (count == 2);
			} else {
				startX = original.getX() - offsetX;
				startY = original.getY() - offsetY;
				switch (dir) {
				case Field2D.TUP:
				case Field2D.UP:
					startY -= moveSpeed;
					if (startY < endY) {
						startY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TDOWN:
				case Field2D.DOWN:
					startY += moveSpeed;
					if (startY > endY) {
						startY = endY;
						isMoved = false;
					}
					break;
				case Field2D.TLEFT:
				case Field2D.LEFT:
					startX -= moveSpeed;
					if (startX < endX) {
						startX = endX;
						isMoved = false;
					}
					break;
				case Field2D.TRIGHT:
				case Field2D.RIGHT:
					startX += moveSpeed;
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
						updateDirection((newX - lastX), (newY - lastY));
					}
					movePos(newX, newY);
				}
				if (endX - startX == 0 && endY - startY == 0) {
					_isCompleted = true;
				}
			}
		} else {
			if (original == null || layerPath == null || layerPath.size() == 0) {
				return;
			}
			synchronized (layerPath) {
				if (synchroLayerField) {
					if (original != null) {
						Field2D field = original.getField2D();
						if (field != null && layerMap != field) {
							this.layerMap = field;
						}
					}
				}
				if (MathUtils.equal(startX, endX) && MathUtils.equal(startY, endY)) {
					if (layerPath.size() > 1) {
						Vector2f moveStart = layerPath.get(0);
						Vector2f moveEnd = layerPath.get(1);
						if (layerMap != null) {
							startX = layerMap.tilesToWidthPixels(moveStart.x());
							startY = layerMap.tilesToHeightPixels(moveStart.y());
							endX = layerMap.tilesToWidthPixels(moveEnd.x());
							endY = layerMap.tilesToHeightPixels(moveEnd.y());
						} else {
							startX = moveStart.getX();
							startY = moveStart.getY();
							endX = moveEnd.getX();
							endY = moveEnd.getY();
						}
						moveX = moveEnd.getX() - moveStart.getX();
						moveY = moveEnd.getY() - moveStart.getY();
						updateDirection(moveX, moveY);
					} else if (layerPath.size() == 1) {
						Vector2f moveEnd = layerPath.pop();
						float newEndX = endX;
						float newEndY = endY;
						if (layerMap != null) {
							newEndX = layerMap.tilesToWidthPixels(moveEnd.x());
							newEndY = layerMap.tilesToHeightPixels(moveEnd.y());
						} else {
							newEndX = moveEnd.getX();
							newEndY = moveEnd.getY();
						}
						moveX = newEndX - endX;
						moveY = newEndY - endY;
					}
					if (layerPath.size() > 0) {
						layerPath.removeIndex(0);
					}
				} else {
					moveX = endX - startX;
					moveY = endY - startY;
					updateDirection(moveX, moveY);
				}

				newX = original.getX() - offsetX;
				newY = original.getY() - offsetY;

				if (allDir) {
					switch (direction) {
					case Field2D.TUP:
						startY -= moveSpeed;
						newY -= moveSpeed;
						if (startY < endY) {
							startY = endY;
						}
						if (newY < endY) {
							newY = endY;
							isMoved = false;
						}
						break;
					case Field2D.TDOWN:
						startY += moveSpeed;
						newY += moveSpeed;
						if (startY > endY) {
							startY = endY;
						}
						if (newY > endY) {
							newY = endY;
							isMoved = false;
						}
						break;
					case Field2D.TLEFT:
						startX -= moveSpeed;
						newX -= moveSpeed;
						if (startX < endX) {
							startX = endX;
						}
						if (newX < endX) {
							newX = endX;
							isMoved = false;
						}
						break;
					case Field2D.TRIGHT:
						startX += moveSpeed;
						newX += moveSpeed;
						if (startX > endX) {
							startX = endX;
						}
						if (newX > endX) {
							newX = endX;
							isMoved = false;
						}
						break;
					case Field2D.UP:
						startX += moveSpeed;
						startY -= moveSpeed;
						newX += moveSpeed;
						newY -= moveSpeed;
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
						startX -= moveSpeed;
						startY += moveSpeed;
						newX -= moveSpeed;
						newY += moveSpeed;
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
						startX -= moveSpeed;
						startY -= moveSpeed;
						newX -= moveSpeed;
						newY -= moveSpeed;
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
						startX += moveSpeed;
						startY += moveSpeed;
						newX += moveSpeed;
						newY += moveSpeed;
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
				} else {
					switch (direction) {
					case Field2D.TUP:
					case Field2D.UP:
						startY -= moveSpeed;
						newY -= moveSpeed;
						if (startY < endY) {
							startY = endY;
						}
						if (newY < endY) {
							newY = endY;
							isMoved = false;
						}
						break;
					case Field2D.TDOWN:
					case Field2D.DOWN:
						startY += moveSpeed;
						newY += moveSpeed;
						if (startY > endY) {
							startY = endY;
						}
						if (newY > endY) {
							newY = endY;
							isMoved = false;
						}
						break;
					case Field2D.TLEFT:
					case Field2D.LEFT:
						startX -= moveSpeed;
						newX -= moveSpeed;
						if (startX < endX) {
							startX = endX;
						}
						if (newX < endX) {
							newX = endX;
							isMoved = false;
						}
						break;
					case Field2D.TRIGHT:
					case Field2D.RIGHT:
						startX += moveSpeed;
						newX += moveSpeed;
						if (startX > endX) {
							startX = endX;
						}
						if (newX > endX) {
							newX = endX;
							isMoved = false;
						}
						break;
					}
					if (!isMoved) {
						float offV = 0f;
						if (endX != startX) {
							offV = (endX - startX);
							if (offV > 0) {
								direction = Field2D.TRIGHT;
							}
							if (offV < 0) {
								direction = Field2D.TLEFT;
							}
						}
						if (endY != startY) {
							offV = (endY - startY);
							if (offV > 0) {
								direction = Field2D.TDOWN;
							}
							if (offV < 0) {
								direction = Field2D.TUP;
							}
						}
					}
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

	public DefineMoveTo movePathPos(float newX, float newY) {
		if (collisionWorld != null) {
			if (worldCollisionFilter == null) {
				worldCollisionFilter = CollisionFilter.getDefault();
			}
			CollisionResult.Result result = collisionWorld.move(original, newX, newY, worldCollisionFilter);
			if ((result.goalX != newX || result.goalY != newY)) {
				clearPath();
				updatePath();
				original.setLocation(result.goalX, result.goalY);
			} else {
				original.setLocation(newX, newY);
			}
		} else {
			original.setLocation(newX, newY);
		}
		return this;
	}

	public Vector2f nextPos() {
		if (layerPath != null) {
			synchronized (layerPath) {
				int size = layerPath.size();
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

	public DefineMoveTo setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	@Override
	public boolean isComplete() {
		return moveByMode ? _isCompleted
				: (layerPath == null || layerPath.size() == 0 || _isCompleted || original == null);
	}

	public boolean isDirectionUpdate() {
		return isDirUpdate;
	}

	public DefineMoveTo updateDirection(float x, float y) {
		int oldDir = direction;
		direction = Field2D.getDirection((int) x, (int) y, oldDir);
		isDirUpdate = (oldDir != direction);
		return this;
	}

	public boolean isSynchroLayerField() {
		return synchroLayerField;
	}

	public void setSynchroLayerField(boolean syn) {
		this.synchroLayerField = syn;
	}

	public float getMoveX() {
		return moveX;
	}

	public float getMoveY() {
		return moveY;
	}

	public boolean isMoved() {
		return isMoved;
	}

	public boolean isMoveByMode() {
		return moveByMode;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public boolean isAllDirection() {
		return allDir;
	}

	@Override
	public ActionEvent cpy() {
		DefineMoveTo defMove = new DefineMoveTo(layerMap, initPath, allDir, speed);
		defMove.set(this);
		return defMove;
	}

	@Override
	public ActionEvent reverse() {
		DefineMoveTo defMove = new DefineMoveTo(layerMap, initPath.cpyReverse(), allDir, speed);
		defMove.set(this);
		return defMove;
	}

	@Override
	public String getName() {
		return "defmove";
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue(getName());
		builder.kv("layerMap", layerMap).comma().kv("layerPath", layerPath).comma().kv("direction", direction).comma()
				.kv("speed", speed);
		return builder.toString();
	}

}