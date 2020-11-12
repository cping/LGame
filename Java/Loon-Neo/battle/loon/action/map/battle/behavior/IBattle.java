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
package loon.action.map.battle.behavior;

import loon.action.map.battle.BattleActiveManager;
import loon.action.sprite.Animation;
import loon.utils.TArray;
import loon.utils.res.Texture;

public abstract class IBattle 
{
	public abstract int getPhysicAttack();
	public abstract int getMagicAttack();
	public abstract int getPhysicDefense();
	public abstract int getMagicDefense();
	public abstract int getAgility();
	public abstract int getHealth();
	public abstract int getHealtPoints();
	public abstract int getMaximumHealthPoints();
	
	public abstract void resetBattleActive();
	public abstract void launchActive();
	public abstract void updateHealthPoints(int val);
	
	public abstract boolean isAlive();	
	
	public abstract String getName();
	
	public abstract void setTargets(TArray<IBattle> tags);
	public abstract TArray<IBattle> getTargets();
	
	public abstract RoleAction getRoleAction();
	public abstract void setRoleAction(RoleAction action);
	public abstract void cancelRoleAction();

	public abstract Texture getBattleTexture();
	public abstract Animation getBattleAnimation();
	public abstract BattleActiveManager getBattleActive();
	
}
