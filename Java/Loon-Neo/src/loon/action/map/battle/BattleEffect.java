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

public class BattleEffect {

	private IBattle target;
	
	private final DamagesState state;
	
	private BattleEffectState battleEffectState;
	
	private int value;

	private BattleEffectRenderer renderer;
	
	private BattleHealthRenderer updateRenderer;

	public BattleEffect(int val,IBattle target, DamagesState type,BattleEffectState state) {
		this.value = val;
		this.target = target;
		this.state = type;
		this.battleEffectState = state;
	}

	public IBattle getTarget() {
		return target;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void apply() {
		switch (state) {
		case Physical:
			target.updateHealthPoints(-value);
			break;
		case Magical:
			target.updateHealthPoints(-value);
			break;
		case Cure:
			target.updateHealthPoints(value);
			break;
		default:
			break;
		}
	}
	
	public BattleEffectRenderer getRenderer() {
		if (renderer == null) {
			updateRenderer();
		}
		return renderer;
	}

	private void updateRenderer() {
		switch (state) {
		case Cure:
		case Magical:
		case Physical:
			renderer = updateRenderer;
			break;
		default:
		}
	}

	public BattleEffectState getBattleEffectState() {
		return battleEffectState;
	}

	public BattleHealthRenderer getUpdateRenderer() {
		return updateRenderer;
	}

	public void setUpdateRenderer(BattleHealthRenderer renderer) {
		this.updateRenderer = renderer;
	}

}
