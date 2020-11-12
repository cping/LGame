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

import loon.action.map.battle.behavior.IActors;
import loon.utils.MathUtils;

public class BattleControl {

	protected IActors actorControls;

	private int saveId;

	private int sceneId;

	private int partyId;

	private int gold;

	private int exp;

	public static final int[] getItemEffectDispersion(int recover_hp, int recover_sp, int variance) {
		int num;
		if ((variance > 0) && (MathUtils.abs(recover_hp) > 0)) {
			num = MathUtils.max((MathUtils.abs(recover_hp) * variance) / 100, 1);
			recover_hp += (BattleRNG.random(num + 1) + BattleRNG.random(num + 1)) - num;
		}
		if ((variance > 0) && (MathUtils.abs(recover_sp) > 0)) {
			num = MathUtils.max((MathUtils.abs(recover_sp) * variance) / 100, 1);
			recover_sp += (BattleRNG.random(num + 1) + BattleRNG.random(num + 1)) - num;
		}
		return new int[] { recover_hp, recover_sp };
	}

	public IActors getActorControls() {
		return actorControls;
	}

	public void setActorControls(IActors controls) {
		this.actorControls = controls;
	}

	public int getSaveId() {
		return saveId;
	}

	public void setSaveId(int saveId) {
		this.saveId = saveId;
	}

	public int getSceneId() {
		return sceneId;
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public int getPartyId() {
		return partyId;
	}

	public void setPartyId(int partyId) {
		this.partyId = partyId;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

}
