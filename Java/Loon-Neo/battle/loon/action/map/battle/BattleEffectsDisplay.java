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
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class BattleEffectsDisplay {

	private TArray<BattleEffect> effects;
	private int index = 0;
	private int count;
	
	private ObjectMap<IBattle, TArray<BattleEffect>> targetsSet;
	
	public BattleEffectsDisplay(TArray<BattleEffect> effects){
		this.effects = effects;
		this.targetsSet = new ObjectMap<IBattle, TArray<BattleEffect>>();
		for(BattleEffect e : effects){
			if(!targetsSet.containsKey(e.getTarget())){
				targetsSet.put(e.getTarget(), new TArray<BattleEffect>());
			}
			targetsSet.get(e.getTarget()).add(e);
			
			if(count < targetsSet.get(e.getTarget()).size() ){
				count = targetsSet.get(e.getTarget()).size();
			}
			
		}
	}
	
	public boolean completed(){
		return index >= count;
	}
	
	public void draw(GLEx g){
		for(IBattle entity : targetsSet.keys()){
			if(index < targetsSet.get(entity).size()){
				BattleEffectRenderer renderer = targetsSet.get(entity).get(index).getRenderer();
				renderer.draw(g);
			}
		}
	}

	public void update(long elapsedTime){
		boolean finished = true;
		for(IBattle entity : targetsSet.keys()){
			if(index < targetsSet.get(entity).size()){
				BattleEffectRenderer renderer = targetsSet.get(entity).get(index).getRenderer();
				renderer.update(elapsedTime);
				finished &= renderer.completed();
			}
		}
		if(finished){
			index++;
		}
	}

	public TArray<BattleEffect> getEffects() {
		return effects;
	}
}
