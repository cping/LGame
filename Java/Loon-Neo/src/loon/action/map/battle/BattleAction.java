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

public abstract class BattleAction {
	
	private String actionName;
	
	private int actionSpeed;
	private int turnCost;
	private int actionAnimationCode;
	
	public abstract void act(Character player, Character enemy);

	public int getActionSpeed() {
		return actionSpeed;
	}

	public void setActionSpeed(int actionSpeed) {
		this.actionSpeed = actionSpeed;
	}

	public int getTurnCost() {
		return turnCost;
	}

	public void setTurnCost(int turnCost) {
		this.turnCost = turnCost;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public int getActionAnimationCode() {	
		return actionAnimationCode;
	}

	public void setActionAnimation(int code) {
		this.actionAnimationCode = code;
	}
	
}