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
import loon.action.map.battle.BattleType.ObjectState;
import loon.action.map.items.Role;
import loon.action.map.items.RoleEquip;
import loon.geom.Vector2f;
import loon.utils.Easing;
import loon.utils.ISOUtils;
import loon.utils.ISOUtils.IsoConfig;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class BattleMapObject extends Role {

	public float moveProgress = 0f;

	public int gridX;

	public int gridY;

	public int targetX, targetY;

	public int width, height, layer;

	public Direction currentDirection = Direction.DOWN;

	public ObjectState state = ObjectState.IDLE;

	public float renderPriority = 0f;

	public IsoConfig isoConfig;

	public TArray<Vector2f> path = new TArray<Vector2f>();

	public BattleMapObject(IsoConfig cfg, int id, String name, int gx, int gy, int w, int h, int layer) {
		this(cfg, id, new RoleEquip(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), name, gx, gy, w, h, layer);
	}

	public BattleMapObject(IsoConfig cfg, int id, RoleEquip e, String name, int gx, int gy, int w, int h, int layer) {
		super(id, e, name);
		this.gridX = gx;
		this.gridY = gy;
		this.width = w;
		this.health = h;
		this.layer = layer;
		this.isoConfig = cfg;
		this.renderPriority = calculateRenderPriority();
	}

	private float calculateRenderPriority() {
		Vector2f screenPos = getScreenPosition();
		return screenPos.y + (layer * 100) + (height * isoConfig.heightScale);
	}

	public Vector2f getInterpolatedPosition() {
		if (state == ObjectState.MOVING && targetX != gridX && targetY != gridY) {
			float easedProgress = Easing.outCubicEase(moveProgress);
			float interpGridX = gridX + (targetX - gridX) * easedProgress;
			float interpGridY = gridY + (targetY - gridY) * easedProgress;
			return ISOUtils.isoTransform((int) interpGridX, (int) interpGridY, width, height, isoConfig).screenPos;
		} else {
			// 非移动状态直接返回当前位置
			return getScreenPosition();
		}
	}

	public Vector2f getScreenPosition() {
		return ISOUtils.isoTransform(gridX, gridY, width, height, isoConfig).screenPos;
	}

	public Direction getDirection() {
		return currentDirection;
	}

	public RoleEquip getRoleEquip() {
		return getInfo();
	}
    public static final float MAX_INERTIA = 0.1f;
	private boolean isMoving;
    public float moveInertia = 0f;
    public float baseMoveSpeed = 1f;
    public float moveSpeedMultiplier = 1f; // 地形/效果倍率
    private void handleMoveState(float deltaTime, BattleTile[][] map,int w,int h) {
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
            if (!isPositionPassable(targetX, targetY,w,h, map)) {
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

    public boolean isPositionPassable(int x, int y,int w,int h, BattleTile[][] map) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            return false;
        }
        return map[x][y].isPassable();
    }
    
	public void setPath(TArray<Vector2f> newPath) {
		this.path.clear();
		this.path.addAll(newPath);
		this.moveProgress = 0f;
		if (!newPath.isEmpty() && state != ObjectState.DEAD) {
			Vector2f firstPos = newPath.peek();
			targetX = (int) firstPos.x;
			targetY = (int) firstPos.y;
		}
	}
}
