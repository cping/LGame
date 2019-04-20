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

import loon.utils.ObjectMap;
import loon.utils.UUID;

public class BattleStatus {
	
	public static enum BaseState {
		
		WAITING_FOR_PLAYERS,
		
		PROCESS_CALLS,
		
		ENDING_ROUND
	}
	
	private ObjectMap<String, BattlePlayer> players;
	
	private String roundId;
	
	private BaseState state;
	
	private boolean flippedHeads;
	
	public BattleStatus() {
		players = new ObjectMap<String, BattlePlayer>();
	}
	
	public void reset() {
		roundId = "WAITING_FOR_PLAYERS";
		state = BaseState.WAITING_FOR_PLAYERS;
	}
	
	public void startRound() {
		
		for (BattlePlayer player : players.values()) {
			player.resetForNewRound();
		}
		
		roundId = new UUID().toString();
		
		state = BaseState.PROCESS_CALLS;
	}
	
	public void endRound(boolean flippedHeads) {
		state = BaseState.ENDING_ROUND;
		this.flippedHeads = flippedHeads;
	}

	public ObjectMap<String, BattlePlayer> getPlayers() {
		return players;
	}

	public String getRoundId() {
		return roundId;
	}

	public BaseState getState() {
		return state;
	}
	
	public boolean getFlippedHeads() {
		return flippedHeads;
	}
}
