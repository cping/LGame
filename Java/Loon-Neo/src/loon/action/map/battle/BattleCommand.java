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

public class BattleCommand {

	public static class Join extends BattleBaseCommand {

		@Override
		public String getName() {
			return "Join";
		}
	}

	public static class Exit extends BattleBaseCommand {

		@Override
		public String getName() {
			return "Exit";
		}
	}

	public static class Info extends BattleBaseCommand {

		@Override
		public String getName() {
			return "Info";
		}

	}

	private BattleEvent eventHandler;

	private BattleStatus status;

	public BattleCommand(BattleEvent eventHandler) {
		this.eventHandler = eventHandler;
		this.status = new BattleStatus();
	}

	public boolean addPlayer(Join command) {
		BattlePlayer player = command.getPlayer();

		if (status.getPlayers().containsKey(player.getId())) {
			eventHandler.error(status, player, "PLAYER_IN");
			return false;
		}

		status.getPlayers().put(player.getId(), player);

		eventHandler.playerJoin(status, player);

		return true;
	}

	public boolean removePlayer(Exit command) {
		BattlePlayer player = command.getPlayer();

		if (!status.getPlayers().containsKey(player.getId())) {
			eventHandler.error(status, player, "PLAYER_NOT_IN");
			return false;
		}

		status.getPlayers().remove(player.getId());

		return true;
	}

	public void call(Info command) {
		BattlePlayer player = command.getPlayer();

		if (!status.getPlayers().containsKey(player.getId())) {
			eventHandler.error(status, player, "PLAYER_NOT_IN");

			return;
		}

		player.setHeads(command.isHeads());
	}

	public void getGameInfo(Info command) {
		eventHandler.gameInfoRequest(status, command.getPlayer());
	}

	public void startRound() {
		status.startRound();
		eventHandler.roundStart(status);
	}

	public void endRound() {
		status.endRound(MathUtils.nextBoolean());
		eventHandler.roundEnd(status);

		for (BattlePlayer player : status.getPlayers().values()) {
			eventHandler.call(status, player);
		}
	}

	public void reset() {
		status.reset();
	}

	public boolean isGameEmpty() {
		return status.getPlayers().isEmpty();
	}

	public boolean isAllPlayerCalls() {
		for (BattlePlayer player : status.getPlayers().values()) {
			if (!player.isCallMade()) {
				return false;
			}
		}
		return true;
	}

	public void otherCommand(BattleBaseCommand command) {
		eventHandler.error(status, command.getPlayer(),
				command.getName() + " not allowed " + status.getState().toString() + " state.");
	}
}
