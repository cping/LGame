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
}
