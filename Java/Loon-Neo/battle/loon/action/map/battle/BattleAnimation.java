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

import loon.action.map.battle.behavior.IBattle;
import loon.opengl.GLEx;
import loon.utils.TArray;

public abstract class BattleAnimation 
{
	private IBattle caster;
	
	private TArray<IBattle> targets;
	
	public BattleAnimation(IBattle caster, TArray<IBattle> targets)
	{
		this.caster = caster;
		this.targets = targets;
		this.init();
	}

	public abstract void draw(GLEx g);
	
	public abstract void update(long elapsedTime);
	
	public abstract void init();
	
	public abstract boolean completed();
	
	public TArray<IBattle> getTargets(){
		return targets;
	}
	
	public IBattle getCaster(){
		return caster;
	}
}
