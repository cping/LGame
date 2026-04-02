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
import loon.action.map.battle.BattleMovementManager.CollisionResponse;
import loon.action.map.battle.BattleMovementManager.MovementEffect;
import loon.action.map.battle.BattleMovementManager.MovementListener;
import loon.action.map.battle.BattleMovementManager.MovementMode;
import loon.action.map.battle.BattleMovementManager.MovementState;
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.map.items.Role;
import loon.action.map.items.RoleEquip;
import loon.action.sprite.ISprite;
import loon.geom.Vector2f;
import loon.utils.Easing;
import loon.utils.ISOUtils;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.ISOUtils.IsoResult;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * 战斗地图专属的万能地图对象(所有地图对象相关操作功能全部内置，包括但不限于寻径移动，瓦片适配，动画切换，碰撞检查，队列行进之类，所以叫万能)
 */
public class BattleMapObject extends Role {

	public int gridX, gridY;

	public int targetX, targetY;

	public Direction currentDirection = Direction.DOWN;

	public ObjectState state = ObjectState.IDLE;

	public float renderPriority = 0f;

	public static final float MAX_INERTIA = 0.1f;

	private boolean isMoving;

	public float moveInertia = 0f;

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

	private final Vector2f movePixel = new Vector2f();

	private final Vector2f moveOffsetPixel = new Vector2f();

	// 移动系统
	private final BattleMovementManager moveManager;

	// 可碰撞对应集合
	private final TArray<BattleMapObject> otherCharacters = new TArray<BattleMapObject>();

	private MovementListener listener;

	private Vector2f currentMapTile;

	public float moveProgress = 0f;

	public BattleMapObject(IsoConfig cfg, BattleMap map, ISprite sprite, int id, String name, int gx, int gy, int w,
			int h, MovementListener l) {
		this(cfg, map, sprite, id, name, gx, gy, w, h, l, Easing.TIME_LINEAR);
	}

	public BattleMapObject(IsoConfig cfg, BattleMap map, ISprite sprite, int id, String name, int gx, int gy, int w,
			int h, MovementListener l, Easing ease) {
		this(cfg, map, sprite, id, new RoleEquip(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), name, gx, gy, w, h, l, ease);
	}

	public BattleMapObject(IsoConfig cfg, BattleMap map, ISprite sprite, int id, RoleEquip e, String name, int gx,
			int gy, int w, int h, MovementListener l) {
		this(cfg, map, sprite, id, e, name, gx, gy, w, h, l, Easing.TIME_LINEAR);
	}

	public BattleMapObject(IsoConfig cfg, BattleMap map, ISprite sprite, int id, RoleEquip e, String name, int gx,
			int gy, int w, int h, MovementListener l, Easing ease) {
		super(id, e, name);
		this.battleMap = map;
		this.easing = ease;
		this.gridX = gx;
		this.gridY = gy;
		this.targetX = gx;
		this.targetY = gy;
		this.charWidth = w;
		this.charHeight = h;
		this.listener = l;
		this.isoConfig = cfg;
		this.currentMapTile = new Vector2f(gridX, gridY);
		this.moveManager = new BattleMovementManager(listener);
		this.startPixel = getTileToScreen(gridX, gridY);
		this.targetPixel = startPixel.cpy();
		this.renderPriority = calculateRenderPriority();
		resetState();
		if (sprite != null) {
			sprite.setSize(w, h);
			sprite.setLocation(startPixel.x, startPixel.y);
			setRoleObject(sprite);
		}
	}

	private float calculateRenderPriority() {
		Vector2f screenPos = getScreenPosition();
		return screenPos.y + (getLayer() * 100) + (charHeight * isoConfig.heightScale);
	}

	public Vector2f getInterpolatedPosition() {
		if (state == ObjectState.MOVING && targetX != gridX && targetY != gridY) {
			float easedProgress = Easing.outCubicEase(moveProgress);
			float interpGridX = gridX + (targetX - gridX) * easedProgress;
			float interpGridY = gridY + (targetY - gridY) * easedProgress;
			return getTileToScreen(MathUtils.ifloor(interpGridX), MathUtils.ifloor(interpGridY));
		} else {
			// 非移动状态直接返回当前位置
			return getScreenPosition();
		}
	}

	public Vector2f getTileToScreen(int gx, int gy) {
		return ISOUtils.getTileToScreen(isoConfig, gx, gy, charWidth, charHeight, moveOffsetPixel.x, moveOffsetPixel.y,
				gxTempResult, isoTempResult);
	}

	public Vector2f getScreenPosition() {
		return getTileToScreen(gridX, gridY);
	}

	public Vector2f getTilePosition() {
		return ISOUtils.getScreenToTile(isoConfig, startPixel.x, startPixel.y, charWidth, charHeight, moveOffsetPixel.x,
				moveOffsetPixel.y, gxTempResult);
	}

	public void setLayer(int l) {
		if (_roleObject != null) {
			_roleObject.setLayer(l);
		}
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
		targetPixel = getTileToScreen(this.path.get(0).x(), this.path.get(0).y());
		if (listener != null) {
			listener.onPathUpdated(this.path);
		}

		// 触发移动动画变更，获得新状态后用户即可改编播放的纹理
		triggerAnimation(AnimationState.WALK);
	}

	public void update(float deltaTime) {
		if (paused || path.isEmpty()) {
			return;
		}
		// 更新移动状态
		moveManager.update(deltaTime);
		// 应用速度计算
		updateSpeed();

		// 传送状态直接生效
		for (MovementState skill : moveManager.getActiveStates()) {
			if (skill.isTeleport()) {
				performTeleport(this);
				return;
			}
		}

		// 平滑移动
		currentSpeed += (targetSpeed - currentSpeed) * 0.1f;
		moveProgress += currentSpeed * deltaTime;
		float eased = easing.apply(moveProgress, 1f);
		movePixel.set(startPixel.x + (targetPixel.x - startPixel.x) * eased,
				startPixel.y + (targetPixel.y - startPixel.y) * eased);

		setPixelPosition(movePixel);
		updateMapTile(charWidth, charHeight);

		// 完成单步移动
		if (moveProgress >= 1f) {
			completeStep();
		}
	}

	public void updateMapTile(int charW, int charH) {
		this.currentMapTile = ISOUtils.getScreenToTile(isoConfig, getX(), getY(), charW, charH, moveOffsetPixel.x,
				moveOffsetPixel.y, gxTempResult);
	}

	private void updateSpeed() {
		float multiplier = 1f;
		for (MovementState skill : moveManager.getActiveStates()) {
			multiplier = MathUtils.max(multiplier, skill.getSpeedMultiplier());
		}
		switch (currentMode) {
		case RUN:
			multiplier *= 2f;
			break;
		case SNEAK:
			multiplier *= 0.5f;
			break;
		case CHARGE:
			multiplier *= 2.5f;
			break;
		default:
			multiplier = 1f;
			break;
		}
		currentSpeed += (baseSpeed * multiplier - currentSpeed) * 0.1f;
	}

	private void performTeleport(BattleMapObject o) {
		Vector2f end = path.get(path.size() - 1);
		targetPixel = getTileToScreen(end.x(), end.y());
		o.setPixelPosition(targetPixel);
		o.setCurrentMapTile(end);
		currentStep = path.size();
		finishPath(o);
	}

	public int getMaxReachableSteps() {
		int steps = 0;
		int points = remainingMovementPoints;
		for (Vector2f tile : path) {
			int cost = 0;
			if (battleMap != null) {
				BattleTile battleTile = battleMap.getMapTile(tile.x(), tile.y());
				cost = battleTile == null ? 0 : (int) battleTile.getPathCost();
			}
			if (points < cost) {
				break;
			}
			points -= cost;
			steps++;
		}
		return steps;
	}

	/**
	 * 动态修改路径
	 * 
	 * @param extraPath
	 */
	public void updatePath(TArray<Vector2f> extraPath) {
		if (extraPath == null || extraPath.isEmpty()) {
			return;
		}
		TArray<Vector2f> filtered = filterValidPath(extraPath);
		path.addAll(filtered);
		if (listener != null) {
			listener.onPathUpdated(path);
		}
	}

	/**
	 * 添加新的移动路径
	 * 
	 * @param newPath
	 */
	public void appendPath(TArray<Vector2f> newPath) {
		if (newPath == null || newPath.isEmpty()) {
			return;
		}
		TArray<Vector2f> filtered = filterValidPath(newPath);
		path.addAll(filtered);
		if (listener != null) {
			listener.onPathUpdated(path);
		}
	}

	public Vector2f simulateFuturePosition(int steps) {
		int idx = MathUtils.min(currentStep + steps, path.size() - 1);
		return getTileToScreen(path.get(idx).x(), path.get(idx).y());
	}

	public TArray<Vector2f> previewFullPath() {
		TArray<Vector2f> pixels = new TArray<Vector2f>();
		for (Vector2f p : filterValidPath(path)) {
			pixels.add(getTileToScreen(p.x(), p.y()));
		}
		return pixels;
	}

	/**
	 * 清空移动路径
	 */
	public void clearPath() {
		path.clear();
		paused = true;
		triggerAnimation(AnimationState.IDLE);
	}

	public void addMoveEffect(MovementEffect effect) {
		moveManager.addEffect(effect);
	}

	public void setBlockedTiles(TArray<Vector2f> blocked) {
		blockedTiles.clear();
		blockedTiles.addAll(blocked);
	}

	public void setAllowedTiles(TArray<Vector2f> allowed) {
		allowedTiles.clear();
		allowedTiles.addAll(allowed);
	}

	public void addCharacter(BattleMapObject o) {
		if (!otherCharacters.contains(o)) {
			otherCharacters.add(o);
		}
	}

	public void removeCharacter(BattleMapObject o) {
		otherCharacters.remove(o);
	}

	public TArray<Vector2f> getPath() {
		return new TArray<Vector2f>(path);
	}

	public int getRemainingSteps() {
		return path.size() - currentStep;
	}

	public boolean isPaused() {
		return paused;
	}

	public Vector2f getCurrentMapTile() {
		return currentMapTile;
	}

	public void setCurrentMapTile(Vector2f tile) {
		this.currentMapTile = tile;
	}

	public Vector2f getPixelPosition() {
		return new Vector2f(getX(), getY());
	}

	public void setPixelPosition(Vector2f pos) {
		setLocation(pos);
	}

	public int getCharacterId() {
		return getID();
	}

	public void setMoving(boolean moving) {
		isMoving = moving;
	}

	private void triggerAnimation(AnimationState state) {
		if (listener != null) {
			listener.onAnimationStateChanged(state.name());
		}
	}

	private void deductMovementCost(Vector2f tile) {
		int cost = 0;
		if (battleMap != null) {
			BattleTile battleTile = battleMap.getMapTile(tile.x(), tile.y());
			cost = battleTile == null ? 0 : (int) battleTile.getPathCost();
		}
		remainingMovementPoints = MathUtils.max(0, remainingMovementPoints - cost);
		if (listener != null) {
			listener.onTerrainCostDeducted(cost, remainingMovementPoints);
			listener.onMovementPointChanged(remainingMovementPoints);
		}
		if (remainingMovementPoints <= 0) {
			paused = true;
			if (listener != null) {
				listener.onPathInterrupted();
			}
		}
	}

	private void applyTerrainEffects(Vector2f tile) {
		if (battleMap != null) {
			BattleTile battleTile = battleMap.getMapTile(tile.x(), tile.y());
			if (listener != null) {
				BattleTileType tileType = battleTile.getTileType();
				listener.onTerrainEffectApplied(tileType.getName(), tileType);
				targetSpeed = baseSpeed * tileType.getMoveSpeedMultiplier();
			}
		}

	}

	public CollisionResponse checkCollision() {
		Vector2f selfTile = getCurrentMapTile();
		for (BattleMapObject other : otherCharacters) {
			if (this == other) {
				continue;
			}
			if (selfTile.equals(other.getCurrentMapTile())) {
				if (listener != null) {
					listener.onCollision(this, other, CollisionResponse.STOP);
				}
				return CollisionResponse.STOP;
			}
		}
		return CollisionResponse.CONTINUE;
	}

	private boolean handleCollision(CollisionResponse response, BattleMapObject character) {
		switch (response) {
		case STOP:
			paused = true;
			triggerAnimation(AnimationState.IDLE);
			return true;
		case BACKWARD:
			currentStep = MathUtils.max(0, currentStep - 1);
			targetPixel = getTileToScreen(path.get(currentStep).x(), path.get(currentStep).y());
			character.setPixelPosition(targetPixel);
			return true;
		default:
			return false;
		}
	}

	private void finishPath(BattleMapObject character) {
		paused = true;
		character.setMoving(false);
		triggerAnimation(AnimationState.ARRIVED);
		if (listener != null) {
			listener.onPathCompleted();
		}
	}

	private void updateDirection() {
		if (currentStep <= 0 || currentStep >= path.size()) {
			return;
		}
		Vector2f prev = path.get(currentStep - 1);
		Vector2f curr = path.get(currentStep);
		Direction dir = Direction.fromDelta(curr.x() - prev.x(), curr.y() - prev.y());
		if (dir != currentDirection) {
			currentDirection = dir;
			if (listener != null) {
				listener.onDirectionChanged(dir);
			}
		}
	}

	public void setMovementMode(MovementMode newMode) {
		MovementMode old = this.currentMode;
		this.currentMode = newMode;
		if (listener != null) {
			listener.onMovementModeChanged(old, newMode);
		}
	}

	public ObjectMap<String, Object> getSyncPacket() {
		ObjectMap<String, Object> packet = new ObjectMap<String, Object>();
		packet.put("speed", currentSpeed);
		packet.put("step", currentStep);
		packet.put("paused", paused);
		packet.put("mode", currentMode.name());
		packet.put("points", remainingMovementPoints);
		packet.put("x", startPixel.x);
		packet.put("y", startPixel.y);
		return packet;
	}

	public void applySyncPacket(ObjectMap<String, Object> packet, Character character) {
		this.currentSpeed = (float) packet.get("speed");
		this.currentStep = (int) packet.get("step");
		this.paused = (boolean) packet.get("paused");
		this.currentMode = MovementMode.valueOf((String) packet.get("mode"));
		this.remainingMovementPoints = (int) packet.get("points");
		float x = (float) packet.get("x");
		float y = (float) packet.get("y");
		setPixelPosition(new Vector2f(x, y));
	}

	private void completeStep() {
		Vector2f tile = path.get(currentStep);
		setPixelPosition(targetPixel);
		setCurrentMapTile(tile);

		// 触发事件
		if (listener != null) {
			listener.onStepReached(tile.x(), tile.y());
			listener.onTileEntered(tile.x(), tile.y());
		}

		// 消耗移动力
		deductMovementCost(tile);
		// 应用地形效果
		applyTerrainEffects(tile);
		// 碰撞检测
		CollisionResponse response = checkCollision();
		if (handleCollision(response, this)) {
			return;
		}
		// 更新方向
		updateDirection();

		// 进入下一步
		currentStep++;
		moveProgress = 0f;

		if (currentStep >= path.size()) {
			finishPath(this);
		} else {
			startPixel = targetPixel;
			targetPixel = getTileToScreen(path.get(currentStep).x(), path.get(currentStep).y());
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

	protected void handleMoveState(float deltaTime, int w, int h) {
		if (path.isEmpty()) {
			endMovement();
			return;
		}
		// 计算实际移动速度（基础速度 * 地形倍率）
		float tileSpeedMultiplier = 1f;
		if (gridX >= 0 && gridX < w && gridY >= 0 && gridY < h) {
			tileSpeedMultiplier = battleMap == null ? 1f
					: battleMap.getMapTile(gridX, gridY).getTileType().moveSpeedMultiplier;
		}
		float actualSpeed = baseSpeed * moveSpeedMultiplier * tileSpeedMultiplier;

		// 应用移动惯性
		actualSpeed += moveInertia * 5;

		// 更新移动进度（使用缓动函数）
		moveProgress += deltaTime * actualSpeed;
		moveProgress = MathUtils.min(1.0f, moveProgress);

		// 计算插值位置
		float easedProgress = easing.apply(moveProgress, 1f);

		// 更新目标坐标
		if (targetX == gridX && targetY == gridY && !path.isEmpty()) {
			Vector2f nextPos = path.peek();
			targetX = (int) (nextPos.x * easedProgress);
			targetY = (int) (nextPos.y * easedProgress);

			// 更新移动方向
			currentDirection = Direction.fromDelta(targetX - gridX, targetY - gridY);

			// 检测目标位置是否可通行
			if (!isPositionPassable(targetX, targetY, w, h, battleMap.getTileMap())) {
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

	public BattleMap getBattleMap() {
		return battleMap;
	}

	public void setBattleMap(BattleMap battleMap) {
		this.battleMap = battleMap;
	}

}
