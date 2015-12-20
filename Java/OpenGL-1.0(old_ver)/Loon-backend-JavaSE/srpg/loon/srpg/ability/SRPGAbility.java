package loon.srpg.ability;


import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.effect.SRPGEffect;
import loon.srpg.field.SRPGField;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public abstract class SRPGAbility {

	public String abilityName;

	public String abilityAbout;

	public int minLength;

	public int maxLength;

	public int mp;

	public int range;

	public int target;

	public int counter;

	public int direct;

	public int selectNeed;

	public int baseDamage;

	public int genre;

	public SRPGAbility() {
		initConfig();
	}

	public abstract void initConfig();
	
	public abstract SRPGDamageData dataInput(SRPGDamageAverage damageaverage,SRPGDamageData damageData, SRPGStatus status);

	public abstract int[] getAbilitySkill();
	
	public abstract SRPGEffect runAbilityEffect(int index, SRPGActor actor,
			int j, int k);

	public abstract void runDamageExpect(SRPGActor attacker,SRPGActor defender,SRPGAbilityFactory factory, SRPGField field,
			SRPGDamageData d,
			SRPGActors actors);
}
