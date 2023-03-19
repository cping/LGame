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

public class Team implements LRelease {

	public final static int Unknown = -1;

	public final static int Player = 0;

	public final static int Enemy = 1;

	public final static int Npc = 2;

	public final static int Other = 3;

	private int _teamMode = -1;

	protected boolean dirty;

	private final TArray<Role> _characters;

	public Team(int teamMode) {
		this._characters = new TArray<Role>();
		this._teamMode = teamMode;
	}

	public TArray<Role> list() {
		return _characters;
	}

	public int count() {
		return _characters.size;
	}

	public Role get(int idx) {
		return _characters.get(idx);
	}

	public boolean fellow(Team team) {
		if (team == null) {
			return false;
		}
		return _teamMode == team._teamMode;
	}

	public boolean fellow(Role role) {
		if (role == null) {
			return false;
		}
		return _teamMode == role.getTeam();
	}

	public Team add(Role role) {
		if (role != null && !_characters.contains(role)) {
			role.setTeam(_teamMode);
			_characters.add(role);
			dirty = true;
		}
		return this;
	}

	public Team save(final QueryEvent<Role> query) {
		TArray<Role> temp = _characters.save(query);
		_characters.clear();
		_characters.addAll(temp);
		dirty = true;
		return this;
	}

	public Team clean(final QueryEvent<Role> query) {
		TArray<Role> temp = _characters.clean(query);
		_characters.clear();
		_characters.addAll(temp);
		dirty = true;
		return this;
	}

	public Role find(final QueryEvent<Role> query) {
		return _characters.find(query);
	}

	public Team remove(Role c) {
		_characters.remove(c);
		dirty = true;
		return this;
	}

	public Team remove(final QueryEvent<Role> query) {
		_characters.remove(query);
		dirty = true;
		return this;
	}

	public TArray<Role> where(final QueryEvent<Role> query) {
		return _characters.where(query);
	}

	protected class IDQuery implements QueryEvent<Role> {

		private int id;

		IDQuery(int id) {
			this.id = id;
		}

		@Override
		public boolean hit(Role c) {
			return c != null && (c.getID() == this.id);
		}

	}

	protected class NameQuery implements QueryEvent<Role> {

		private String name;

		NameQuery(String name) {
			this.name = name;
		}

		@Override
		public boolean hit(Role c) {
			return c != null && (this.name.equals(c.getRoleName()));
		}

	}

	protected class MoveQuery implements QueryEvent<Role> {

		@Override
		public boolean hit(Role c) {
			return c != null && c.isMoved;
		}

	}

	protected class CleanQuery implements QueryEvent<Role> {

		@Override
		public boolean hit(Role c) {
			return c == null || c.isDead;
		}

	}

	// 其实可以简写成clean(c->c.isDead()),为了增加兼容性特意不那么写，方便移植,以下同……
	public Team cleanOver() {
		return clean(new CleanQuery());
	}

	public Team allMoved() {
		return save(new MoveQuery());
	}

	public Role findId(int id) {
		return find(new IDQuery(id));
	}

	public Role findName(String name) {
		return find(new NameQuery(name));
	}

	public int getTeam() {
		return _teamMode;
	}

	public Team clear() {
		_characters.clear();
		dirty = true;
		return this;
	}

	public boolean isAllDoneAction() {
		for (Role c : _characters) {
			if (c != null && !c.isAllDoneAction()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllUnDoneAction() {
		for (Role c : _characters) {
			if (c != null && !c.isAllUnDoneAction()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAttack() {
		for (Role c : _characters) {
			if (c != null && !c.isAttack) {
				return false;
			}
		}
		return true;
	}

	public boolean isDefense() {
		for (Role c : _characters) {
			if (c != null && !c.isDefense) {
				return false;
			}
		}
		return true;
	}

	public boolean isSkill() {
		for (Role c : _characters) {
			if (c != null && !c.isSkill) {
				return false;
			}
		}
		return true;
	}

	public boolean isMoved() {
		for (Role c : _characters) {
			if (c != null && !c.isMoved) {
				return false;
			}
		}
		return true;
	}

	public boolean isDead() {
		for (Role c : _characters) {
			if (c != null && !c.isDead) {
				return false;
			}
		}
		return true;
	}

	public boolean isOver() {
		return isDead();
	}

	@Override
	public void close() {
		clear();
	}

}
