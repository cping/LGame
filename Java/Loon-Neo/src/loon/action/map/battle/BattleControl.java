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

import loon.utils.MathUtils;

public class BattleControl {

	private BattleState battleState = BattleState.START;

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

	public BattleState getBattleState() {
		return battleState;
	}

	public void setBattleState(BattleState battleState) {
		this.battleState = battleState;
	}

}
