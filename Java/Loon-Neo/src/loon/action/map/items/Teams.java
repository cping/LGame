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
package loon.action.map.items;

import loon.LRelease;
import loon.LSystem;
import loon.events.QueryEvent;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class Teams implements LRelease {

	protected class TeamQuery implements QueryEvent<Team> {

		private final int id;

		TeamQuery(int team) {
			this.id = team;
		}

		@Override
		public boolean hit(Team t) {
			return t != null && t.getTeam() == id;
		}

	}

	private final TArray<Team> _teams;

	private TArray<Role> _chars;

	private boolean _dirty;

	private String _name;

	public Teams() {
		this(LSystem.UNKNOWN);
	}

	public Teams(String name) {
		this._teams = new TArray<Team>();
		this._name = name;
		_dirty = false;
	}

	public void createPE() {
		_teams.clear();
		_teams.add(new Team(Team.Player));
		_teams.add(new Team(Team.Enemy));
		_dirty = true;
	}

	public void createPEN() {
		_teams.clear();
		_teams.add(new Team(Team.Player));
		_teams.add(new Team(Team.Enemy));
		_teams.add(new Team(Team.Npc));
		_dirty = true;
	}

	public void createPENO() {
		_teams.clear();
		_teams.add(new Team(Team.Player));
		_teams.add(new Team(Team.Enemy));
		_teams.add(new Team(Team.Npc));
		_teams.add(new Team(Team.Other));
	}

	public TArray<Role> all() {
		if (!_dirty) {
			for (Team team : _teams) {
				if (team.dirty) {
					this._dirty = true;
					team.dirty = false;
					break;
				}
			}
		}
		if (_dirty) {
			if (_chars == null) {
				_chars = new TArray<Role>();
			} else {
				_chars.clear();
			}
			for (Team team : _teams) {
				for (Role ch : team.list()) {
					_chars.add(ch);
				}
			}
			_dirty = false;
		} else {
			if (_chars == null) {
				_chars = new TArray<Role>();
			}
		}
		return _chars;
	}

	public TArray<Team> list() {
		return _teams;
	}

	public Teams remove(Role ch) {
		for (Team team : _teams) {
			team.remove(ch);
		}
		_dirty = false;
		return this;
	}

	public TArray<Team> find(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		TArray<Team> list = new TArray<Team>();
		for (Team team : _teams) {
			if (name.equalsIgnoreCase(name)) {
				list.add(team);
			}
		}
		return list;
	}

	public Role getRole(int teamId, int id) {
		for (Team team : _teams) {
			if (team.getTeam() == teamId) {
				for (Role ch : team.list()) {
					if (ch.getID() == id) {
						return ch;
					}
				}
			}
		}
		return null;
	}

	public boolean contains(Role r) {
		for (Team team : _teams) {
			if (team.contains(r)) {
				return true;
			}
		}
		return false;
	}

	public Role getRole(int id) {
		for (Team team : _teams) {
			for (Role ch : team.list()) {
				if (ch.getID() == id) {
					return ch;
				}
			}
		}
		return null;
	}

	public Teams add(int teamId, Role ch) {
		for (Team team : _teams) {
			if (team.getTeam() == teamId) {
				team.add(ch);
				_dirty = false;
				break;
			}
		}
		return this;
	}

	public TArray<Team> save(int team) {
		_dirty = true;
		return _teams.save(new TeamQuery(team));
	}

	public boolean isAllAttack() {
		for (Team team : _teams) {
			if (!team.isAttack()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllMoved() {
		for (Team team : _teams) {
			if (!team.isMoved()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllSkill() {
		for (Team team : _teams) {
			if (!team.isSkill()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllDefense() {
		for (Team team : _teams) {
			if (!team.isDefense()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllDead() {
		for (Team team : _teams) {
			if (!team.isDead()) {
				return false;
			}
		}
		return true;
	}

	public Team getPlayer() {
		return get(Team.Player);
	}

	public Team getEnemy() {
		return get(Team.Enemy);
	}

	public Team getNpc() {
		return get(Team.Npc);
	}

	public Team getOther() {
		return get(Team.Other);
	}

	public String getName() {
		return _name;
	}

	public Team get(int team) {
		return _teams.find(new TeamQuery(team));
	}

	public Teams add(Team team) {
		_teams.add(team);
		_dirty = true;
		return this;
	}

	public Teams remove(Team team) {
		_teams.remove(team);
		_dirty = true;
		return this;
	}

	public Teams clear() {
		_teams.clear();
		_dirty = true;
		return this;
	}

	public Teams save(final QueryEvent<Team> query) {
		TArray<Team> temp = _teams.save(query);
		_teams.clear();
		_teams.addAll(temp);
		_dirty = true;
		return this;
	}

	public Teams clean(final QueryEvent<Team> query) {
		TArray<Team> temp = _teams.clean(query);
		_teams.clear();
		_teams.addAll(temp);
		_dirty = true;
		return this;
	}

	public Team find(final QueryEvent<Team> query) {
		return _teams.find(query);
	}

	public Teams remove(final QueryEvent<Team> query) {
		_teams.remove(query);
		_dirty = true;
		return this;
	}

	public TArray<Team> where(final QueryEvent<Team> query) {
		return _teams.where(query);
	}

	@Override
	public void close() {
		clear();
	}
}
