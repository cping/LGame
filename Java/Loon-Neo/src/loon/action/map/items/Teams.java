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
import loon.events.QueryEvent;
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

	public Teams() {
		_teams = new TArray<Team>();
	}

	public TArray<Team> list() {
		return _teams;
	}

	public TArray<Team> save(int team) {
		return _teams.save(new TeamQuery(team));
	}

	public Teams add(Team team) {
		_teams.add(team);
		return this;
	}

	public Teams remove(Team team) {
		_teams.remove(team);
		return this;
	}

	public Teams clear() {
		_teams.clear();
		return this;
	}

	public Teams save(final QueryEvent<Team> query) {
		TArray<Team> temp = _teams.save(query);
		_teams.clear();
		_teams.addAll(temp);
		return this;
	}

	public Teams clean(final QueryEvent<Team> query) {
		TArray<Team> temp = _teams.clean(query);
		_teams.clear();
		_teams.addAll(temp);
		return this;
	}

	public Team find(final QueryEvent<Team> query) {
		return _teams.find(query);
	}

	public Teams remove(final QueryEvent<Team> query) {
		_teams.remove(query);
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
