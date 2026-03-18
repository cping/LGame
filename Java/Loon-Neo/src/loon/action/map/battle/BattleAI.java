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

import loon.utils.TimeUtils;

public class BattleAI {

	public static enum AIState {
		IDLE, MOVING_TO_ENEMY, ATTACKING, CASTING_SKILL, RETREATING, DEFENDING, FOLLOWING_ALLY, AMBUSHING, REINFORCING,
		RESTING, PATROLLING, AMBUSH_PREPARE, CHASING, FLANKING, SUPPORTING
	}

	private BattleMapObject controlledUnit;
	private BattleMapObject targetEnemy;
	private BattleMapObject supportTarget;
	private long lastActionTime;
	private int aiThinkInterval = 1500;
	private int currentAITurn;
	private AIState currentState = AIState.IDLE;
	private BattleTile targetTile;

	public BattleAI(BattleMapObject controlledUnit) {
		this.controlledUnit = controlledUnit;
		this.lastActionTime = TimeUtils.millis();
	}

	public BattleMapObject getControlledUnit() {
		return controlledUnit;
	}

	public void setControlledUnit(BattleMapObject controlledUnit) {
		this.controlledUnit = controlledUnit;
	}

	public BattleMapObject getTargetEnemy() {
		return targetEnemy;
	}

	public void setTargetEnemy(BattleMapObject targetEnemy) {
		this.targetEnemy = targetEnemy;
	}

	public BattleMapObject getSupportTarget() {
		return supportTarget;
	}

	public void setSupportTarget(BattleMapObject supportTarget) {
		this.supportTarget = supportTarget;
	}

	public long getLastActionTime() {
		return lastActionTime;
	}

	public void setLastActionTime(long lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	public int getAiThinkInterval() {
		return aiThinkInterval;
	}

	public void setAiThinkInterval(int aiThinkInterval) {
		this.aiThinkInterval = aiThinkInterval;
	}

	public int getCurrentAITurn() {
		return currentAITurn;
	}

	public void setCurrentAITurn(int currentAITurn) {
		this.currentAITurn = currentAITurn;
	}

	public AIState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(AIState currentState) {
		this.currentState = currentState;
	}

	public BattleTile getTargetTile() {
		return targetTile;
	}

	public void setTargetTile(BattleTile targetTile) {
		this.targetTile = targetTile;
	}

}
