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
import loon.action.map.items.Role;
import loon.action.map.items.RoleEquip;

public class BattleMapObject extends Role {

	public int gridX;

	public int gridY;

	public Direction currentDirection = Direction.DOWN;

	public BattleMapObject(int id, String name) {
		super(id, new RoleEquip(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), name);
	}

	public BattleMapObject(int id, RoleEquip e, String name) {
		super(id, e, name);
	}

	public Direction getDirection() {
		return currentDirection;
	}

	public RoleEquip getRoleEquip() {
		return getInfo();
	}
}
