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
package loon.action.map.battle;

import loon.action.map.Direction;
import loon.action.map.battle.BattleMovementManager.AnimationState;
import loon.action.map.battle.BattleMovementManager.MovementListener;
import loon.action.map.battle.BattleMovementManager.MovementMode;
import loon.action.map.battle.BattleMovementManager.MovementState;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.map.items.Role;
import loon.action.map.items.RoleEquip;
import loon.geom.Vector2f;
import loon.utils.Easing;
import loon.utils.ISOUtils;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.ISOUtils.IsoResult;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 战斗地图专属的万能地图对象(所有地图对象相关操作功能全部内置，包括但不限于寻径移动，瓦片适配，动画切换，碰撞检查，队列行进之类，所以叫万能)
 */
public class BattleMapObject extends Role {

	public int gridX, gridY;

	public int targetX, targetY;

	public int layer;

	public Direction currentDirection = Direction.DOWN;

	public ObjectState state = ObjectState.IDLE;

	public float renderPriority = 0f;

	public static final float MAX_INERTIA = 0.1f;

	private boolean isMoving;

	public float moveInertia = 0f;

	public float baseMoveSpeed = 1f;

	public float moveSpeedMultiplier = 1f; // 地形/效果倍率

	public IsoConfig isoConfig;

	private int charWidth, charHeight;

	private float baseSpeed, currentSpeed, targetSpeed;

	// 路径与移动状态
	private TArray<Vector2f> path = new TArray<Vector2f>();
	private int currentStep;

	private boolean paused;

	private Easing easing;
	private Vector2f startPixel, targetPixel;

	// 移动模式
	private MovementMode currentMode = MovementMode.WALK;

	// 地图与地形特殊影响

	private TArray<Vector2f> blockedTiles = new TArray<Vector2f>();
	private TArray<Vector2f> allowedTiles = new TArray<Vector2f>();

	private final Vector2f gxTempResult = new Vector2f();

	private final IsoResult isoTempResult = new IsoResult();

	private BattleMap battleMap;

	// 移动资源
	private int maxMovementPoints;
	private int remainingMovementPoints;

	// 移动系统
	private final BattleMovementManager moveManager;

	// 可碰撞对应集合
	private final TArray<BattleMapObject> otherCharacters = new TArray<BattleMapObject>();

	private final float collisionRadius = 0.8f;

	// 网络同步预留(只传参，不实际联网，为以后扩展网络包打基础……)
	private boolean networkSyncEnabled = false;

	private MovementListener listener;

	public float moveProgress = 0f;

	public BattleMapObject(IsoConfig cfg, int id, String name, int gx, int gy, int w, int h, int layer) {
		this(cfg, id, new RoleEquip(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), name, gx, gy, w, h, layer);
	}

	public BattleMapObject(IsoConfig cfg, int id, RoleEquip e, String name, int gx, int gy, int w, int h, int layer) {
		super(id, e, name);
		this.gridX = gx;
		this.gridY = gy;
		this.targetX = gx;
		this.targetY = gy;
		this.charWidth = w;
		this.charHeight = h;
		this.moveManager = new BattleMovementManager(listener);
		this.startPixel = ISOUtils.isoTransform(gridX, gridY, charWidth, charHeight, isoConfig).screenPos;
		this.targetPixel = startPixel.cpy();
		this.layer = layer;
		this.isoConfig = cfg;
		this.renderPriority = calculateRenderPriority();

		resetState();
	}

	private float calculateRenderPriority() {
		Vector2f screenPos = getScreenPosition();
		return screenPos.y + (layer * 100) + (charHeight * isoConfig.heightScale);
	}

	public Vector2f getInterpolatedPosition() {
		if (state == ObjectState.MOVING && targetX != gridX && targetY != gridY) {
			float easedProgress = Easing.outCubicEase(moveProgress);
			float interpGridX = gridX + (targetX - gridX) * easedProgress;
			float interpGridY = gridY + (targetY - gridY) * easedProgress;
			return ISOUtils.isoTransform(MathUtils.ifloor(interpGridX), MathUtils.ifloor(interpGridY), charWidth,
					charHeight, isoConfig, gxTempResult, isoTempResult).screenPos;
		} else {
			// 非移动状态直接返回当前位置
			return getScreenPosition();
		}
	}

	public Vector2f getScreenPosition() {
		return ISOUtils.isoTransform(gridX, gridY, charWidth, charHeight, isoConfig, gxTempResult,
				isoTempResult).screenPos;
	}

	public Vector2f getTilePosition() {
		return ISOUtils.screenToGrid(startPixel.x, startPixel.y, charWidth, charHeight, isoConfig, gxTempResult);
	}

	public void setPath(TArray<Vector2f> newPath) {
		if (path == null || path.isEmpty()) {
			this.path = new TArray<Vector2f>();
			return;
		} else {
			this.path.clear();
		}
		this.path.addAll(newPath);
		if (!newPath.isEmpty() && state != ObjectState.DEAD) {
			Vector2f firstPos = newPath.peek();
			targetX = (int) firstPos.x;
			targetY = (int) firstPos.y;
		}
		// 过滤不可通行瓦片
		this.path = filterValidPath(path);
		this.currentStep = 0;
		this.paused = false;
		this.moveProgress = 0f;

		// 获得目标像素坐标
		targetPixel = ISOUtils.isoTransform(this.path.get(0).x(), this.path.get(0).y(), charWidth, charHeight,
				isoConfig, gxTempResult, isoTempResult).screenPos;
		if (listener != null) {
			listener.onPathUpdated(this.path);
		}

		// 触发移动动画变更，获得新状态后用户即可改编播放的纹理
		triggerAnimation(AnimationState.WALK);
	}

	private void triggerAnimation(AnimationState state) {
		if (listener != null) {
			listener.onAnimationStateChanged(state.name());
		}
	}

	public boolean canMoveTo(Vector2f tile) {
		// 特殊移动
		for (MovementState state : moveManager.getActiveStates()) {
			if (state.canOverrideBlocked(tile)) {
				return true;
			}
		}
		// 地形阻挡
		if (battleMap != null) {
			BattleTile battleTile = battleMap.getMapTile(tile.x(), tile.y());

			if (battleTile != null && !battleTile.isPassable()) {
				return false;
			}
		}
		// 瓦片阻挡
		if (blockedTiles.contains(tile) && !allowedTiles.contains(tile)) {
			return false;
		}
		return true;
	}

	private TArray<Vector2f> filterValidPath(TArray<Vector2f> paths) {
		TArray<Vector2f> valid = new TArray<Vector2f>();
		int tempPoints = remainingMovementPoints;
		for (Vector2f tile : paths) {
			if (!canMoveTo(tile)) {
				break;
			}
			if (battleMap != null) {
				BattleTile battleTile = battleMap.getMapTile(tile.x(), tile.y());
				float cost = battleTile != null ? battleTile.getPathCost() : 1f;
				if (tempPoints < cost) {
					break;
				}
				tempPoints -= cost;
				valid.add(tile);
			}

		}
		return valid;
	}

	public void resetState() {
		currentStep = 0;
		moveProgress = 0f;
		paused = false;
		maxMovementPoints = 10;
		remainingMovementPoints = maxMovementPoints;
		path.clear();
	}

	public Direction getDirection() {
		return currentDirection;
	}

	public RoleEquip getRoleEquip() {
		return getInfo();
	}

	public ObjectState getState() {
		return state;
	}

	public void setState(ObjectState state) {
		this.state = state;
		if (ObjectState.MOVING == state) {
			isMoving = true;
		}
	}

	public boolean isMoving() {
		return isMoving;
	}

	private void handleMoveState(float deltaTime, BattleTile[][] map, int w, int h) {
		if (path.isEmpty()) {
			endMovement();
			return;
		}
		// 计算实际移动速度（基础速度 * 地形倍率）
		float tileSpeedMultiplier = 1f;
		if (gridX >= 0 && gridX < w && gridY >= 0 && gridY < h) {
			tileSpeedMultiplier = map[gridX][gridY].getTileType().moveSpeedMultiplier;
		}
		float actualSpeed = baseMoveSpeed * moveSpeedMultiplier * tileSpeedMultiplier;

		// 应用移动惯性
		actualSpeed += moveInertia * 5;

		// 更新移动进度（使用缓动函数）
		moveProgress += deltaTime * actualSpeed;
		moveProgress = Math.min(1.0f, moveProgress);

		// 计算插值位置（优化：使用缓动函数使移动更自然）
		float easedProgress = Easing.outCubicEase(moveProgress);

		// 更新目标坐标（如果是新路径段）
		if (targetX == gridX && targetY == gridY && !path.isEmpty()) {
			Vector2f nextPos = path.peek();
			targetX = (int) nextPos.x;
			targetY = (int) nextPos.y;

			// 更新移动方向
			currentDirection = Direction.fromDelta(targetX - gridX, targetY - gridY);

			// 检测目标位置是否可通行
			if (!isPositionPassable(targetX, targetY, w, h, map)) {
				path.pop(); // 移除不可通行的点
				state = ObjectState.BLOCKED;

				return;
			}
		}

		// 移动完成判断
		if (moveProgress >= 1.0f) {
			// 更新实际坐标
			gridX = targetX;
			gridY = targetY;
			path.pop(); // 移除已完成的路径点

			// 重置进度
			moveProgress = 0f;

			// 增加移动惯性
			moveInertia = MathUtils.min(MAX_INERTIA, moveInertia + 0.05f);

			// 检查路径是否结束
			if (path.isEmpty()) {
				endMovement();
			}
		}
	}

	// 待机状态处理
	protected void handleIdleState(float deltaTime) {
		// 重置移动惯性
		moveInertia = MathUtils.max(0, moveInertia - deltaTime * 2);
		// 检测是否有路径需要执行
		if (!path.isEmpty() && state != ObjectState.DEAD) {
			startMoving();
		}
	}

	protected void startMoving() {
		if (state != ObjectState.DEAD) {
			state = ObjectState.MOVING;
			isMoving = true;
			moveProgress = 0f;
		}
	}

	protected void endMovement() {
		state = ObjectState.IDLE;
		isMoving = false;
		moveProgress = 0f;
		targetX = gridX;
		targetY = gridY;
	}

	public boolean isPositionPassable(int x, int y, int w, int h, BattleTile[][] map) {
		if (x < 0 || x >= w || y < 0 || y >= h) {
			return false;
		}
		return map[x][y].isPassable();
	}

}
