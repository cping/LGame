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

import java.util.Comparator;

import loon.LSystem;

public abstract class BattleAction {

	public class SpeedComparer implements Comparator<BattleAction> {

		@Override
		public int compare(BattleAction o1, BattleAction o2) {
			int speedA = o1.actionSpeed;
			int speedB = o2.actionSpeed;
			if (speedA > speedB) {
				return -1;
			}
			if (speedA < speedB) {
				return 1;
			}
			return 0;
		}

	}

	private String actionName;

	public boolean forcing;

	private int actionSpeed;
	private int turnCost;
	private int actionAnimationCode;
	private int basic;
	private int itemId;
	private int skillId;
	private int kind;
	private int speed;
	private int targetIndex;
	private int rating;
	
	private BattleStateMachine machine;

	public BattleAction() {
		this(null);
	}
	
	public BattleAction(BattleStateMachine m) {
		this.machine = m;
		this.reset();
	}

	public void reset() {
		this.actionName = LSystem.UNKOWN;
		this.actionSpeed = 0;
		this.turnCost = 0;
		this.actionAnimationCode = -1;
		this.speed = 0;
		this.kind = 0;
		this.basic = 0;
		this.skillId = 0;
		this.itemId = 0;
		this.targetIndex = -1;
		this.forcing = false;
	}

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

	public boolean isForcing() {
		return forcing;
	}

	public void setForcing(boolean forcing) {
		this.forcing = forcing;
	}

	public int getBasic() {
		return basic;
	}

	public void setBasic(int basic) {
		this.basic = basic;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getTargetIndex() {
		return targetIndex;
	}

	public void setTargetIndex(int targetIndex) {
		this.targetIndex = targetIndex;
	}

	public void setActionAnimationCode(int actionAnimationCode) {
		this.actionAnimationCode = actionAnimationCode;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public abstract int getInitiative();

	public BattleStateMachine getMachine() {
		return machine;
	}

	public void setMachine(BattleStateMachine machine) {
		this.machine = machine;
	}
}