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

import loon.action.map.battle.BattleAnimation;
import loon.action.map.battle.BattleEffect;
import loon.action.map.battle.BattleEffectsDisplay;
import loon.opengl.GLEx;
import loon.utils.TArray;

public abstract class RoleAction {
	
	protected IBattle caster;
	protected TArray<IBattle> targets;
	protected BattleAnimation currentAnimation;
	
	protected BattleEffectsDisplay effectsDisplayer;
	
	protected TArray<BattleEffect> effects;
	
	private boolean effectsCalculated = false;
	
	public RoleAction(IBattle caster, TArray<IBattle> targets)
	{
		this.caster = caster;
		this.targets = targets;
		this.effects = new TArray<BattleEffect>();
	}


	public void setTargets(TArray<IBattle> targets) {
		this.targets = targets;
	}
	
	public BattleEffectsDisplay getEffectsAnimation(){
		if(effectsDisplayer == null){
			effectsDisplayer = new BattleEffectsDisplay(effects);		
		}
		return effectsDisplayer;
	}
	
	public abstract void draw(GLEx g);
	
	public abstract void update(long e);

	public final void calculateEffectsOnce(){
		if(!effectsCalculated){
			calculateEffects();
		}
		effectsCalculated = true;
	}

	public final void applyEffects() {
		for(BattleEffect e : effects){
			e.apply();
		}
	}

	public IBattle getCaster() {
		return caster;
	}
	
	public abstract BattleAnimation getBattleAnimation();
	public abstract TArray<BattleEffect> getEffectsAction();
	public abstract boolean completed();
	public abstract void calculateEffects();
	
}
