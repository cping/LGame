package loon.srpg.field;

import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;


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
public class SRPGTeams {

	private int phase;

	private int team;

	private int[] teams;

	private String[] name;

	private int turn;

	public SRPGTeams(int i) {
		this.set(i);
	}

	public SRPGTeams(SRPGActors actors) {
		this.set(getTeamsValue(actors));
	}

	public SRPGTeams(int[] res) {
		this(res.length);
		this.teams = res;
	}

	public void set(SRPGActors actors) {
		this.set(getTeamsValue(actors));
	}

	public void set(int team) {
		this.team = team;
		this.phase = 0;
		this.teams = new int[team];
		this.name = new String[team];
		for (int i = 0; i < team; i++) {
			teams[i] = i;
			name[i] = "Teams - " + String.valueOf(i + 1);
		}
		this.turn = 1;
	}

	public int getPhase() {
		return phase;
	}

	public void setPhase(int i) {
		this.phase = i;
	}

	public int getTeamPhase() {
		return teams[phase];
	}

	public int getTeamPhase(int i) {
		return teams[i];
	}

	public int getLength() {
		return team;
	}

	public void setLength(int i) {
		this.team = i;
	}

	public String[] getNameArray() {
		return name;
	}

	public String getName(int i) {
		if (i < name.length) {
			return name[i];
		} else {
			return "Name - " + String.valueOf(i);
		}
	}

	public String getName() {
		return getName(phase);
	}

	public void setTeams(String[] name) {
		int order[] = new int[name.length];
		for (int i = 0; i < order.length; i++) {
			order[i] = i;
		}
		setName(name);
		setTeams(order);
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public int[] getTeams() {
		return teams;
	}

	public void setTeams(int[] teams) {
		this.teams = teams;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int i) {
		this.turn = i;
	}

	public void changePhase(SRPGActors actors) {
		for (int i = 0; i < team; i++) {
			phase++;
			if (phase >= team) {
				turn++;
				phase = 0;
			}
			if (checkPhase(actors)) {
				return;
			}
		}
	}

	public boolean checkPhase(int team, SRPGActors actors) {
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (actor.isVisible() && actor.getActorStatus().team == team) {
				return true;
			}
		}
		return false;
	}

	public boolean checkPhase(SRPGActors actors) {
		return checkPhase(teams[phase], actors);
	}

	public boolean checkMoving(SRPGActors actors) {
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (actor.isVisible()
					&& actor.getActorStatus().team == teams[phase]
					&& actor.getActorStatus().action > 0
					&& actor.getActorStatus().actionCheck()) {
				return true;
			}
		}
		return false;
	}

	public void startTurn(SRPGActors actors, int team) {
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (!actor.isExist() || !actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			if (status.team == team) {
				status.startTurn();
			}
		}

	}

	public void startTurn(SRPGActors actors) {
		startTurn(actors, teams[phase]);
	}

	/**
	 * 回合结束
	 * 
	 * @param actors
	 * @param i
	 */
	public void endTurn(SRPGActors actors, int team) {
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (!actor.isExist() || !actor.isVisible()) {
				continue;
			}
			SRPGStatus status = actor.getActorStatus();
			if (status.team == team) {
				status.action = 0;
			}
		}

	}

	public void endTurn(SRPGActors actors) {
		endTurn(actors, teams[phase]);
	}

	public boolean leaderCheck(SRPGActors actors, int index) {
		SRPGActor actor = actors.find(index);
		int team = actor.getActorStatus().team;
		if (actor.getActorStatus().leader == SRPGType.LEADER_MAIN) {
			return false;
		}
		if (actor.getActorStatus().leader == SRPGType.LEADER_NO) {
			return checkPhase(team, actors);
		}
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor cactor = actors.find(i);
			SRPGStatus status = cactor.getActorStatus();
			if (status == null) {
				continue;
			}
			if (i != index && cactor.isVisible() && status.team == team
					&& status.leader == SRPGType.LEADER_NORMAL
					|| status.leader == SRPGType.LEADER_MAIN) {
				return true;
			}
		}
		return false;
	}

	public static int getTeamsValue(SRPGActors actors) {
		int index = 0;
		for (int i = 0; i < actors.size(); i++) {
			SRPGActor actor = actors.find(i);
			if (!actor.isExist() || !actor.isVisible()) {
				continue;
			}
			int team = actor.getActorStatus().team;
			if (index < team) {
				index = team;
			}
		}
		return ++index;
	}

	public static int getTeamsAlive(SRPGActors actors) {
		int index = 0;
		int team = getTeamsValue(actors);
		for (int i = 0; i < team; i++) {
			int actorIndex = 0;
			for (;;) {
				if (actorIndex >= actors.size()) {
					break;
				}
				SRPGActor actor = actors.find(actorIndex);
				if (actor.isVisible() && i == actor.getActorStatus().team) {
					index++;
					break;
				}
				actorIndex++;
			}
		}
		return index;
	}

}
