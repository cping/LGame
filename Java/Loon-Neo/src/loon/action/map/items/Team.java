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
import loon.LSysException;
import loon.LSystem;
import loon.canvas.LColor;
import loon.events.QueryEvent;
import loon.utils.TArray;

/**
 * 队伍信息存储用类,用于把不同角色对象归类为统一的小队,集中管理专属阵营的对象
 */
public class Team implements LRelease {

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

	public final static int Unknown = -1;

	public final static int Player = 0;

	public final static int Enemy = 1;

	public final static int Npc = 2;

	public final static int Other = 3;

	protected boolean dirty;

	private final TArray<Role> _characters;

	private LColor _teamColor;

	private int _teamMode = -1;

	private int _totalAttacksUsed;

	private Role _leadRole;

	private String _name;

	public Team(int teamMode) {
		this(teamMode, LSystem.UNKNOWN);
	}

	public Team(int teamMode, String name) {
		this(teamMode, LColor.white, name);
	}

	public Team(int teamMode, LColor color, String name) {
		this._characters = new TArray<Role>();
		this._teamColor = color;
		this._teamMode = teamMode;
		this._name = name;
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

	public boolean contains(Role r) {
		return _characters.contains(r);
	}

	public int getTeam() {
		return _teamMode;
	}

	public Role getLeadRole() {
		return _leadRole;
	}

	public Team setLeadRole(int id) {
		return setLeadRole(findId(id));
	}

	public Team setLeadRole(String name) {
		return setLeadRole(findName(name));
	}

	public Team setLeadRole(Role lr) {
		if (lr == null) {
			throw new LSysException("The Role is null !");
		}
		this._leadRole = lr;
		return this;
	}

	public boolean isLeadRoleDead() {
		if (_leadRole != null) {
			return _leadRole.isDead;
		}
		return false;
	}

	public String getName() {
		return _name;
	}

	public TArray<Role> getActionPrioritySort() {
		TArray<Role> newRoles = new TArray<Role>(_characters);
		newRoles.sort(Role.ActionPrioritySort);
		return newRoles;
	}

	public Team sort() {
		_characters.sort(Role.ActionPrioritySort);
		return this;
	}

	public int getDeadCount() {
		int count = 0;
		for (Role c : _characters) {
			if (c != null && c.isDead) {
				count++;
			}
		}
		return count;
	}

	public Team checkAndDead() {
		for (Role c : _characters) {
			if (c != null && c.getHealth() <= 0) {
				c.isDead = true;
			}
		}
		return this;
	}

	public Team allDead() {
		for (Role c : _characters) {
			if (c != null) {
				c.isDead = true;
			}
		}
		return this;
	}

	public float getAverageMaxHealth() {
		float health = 0;
		for (Role c : _characters) {
			if (c != null) {
				health += c.maxHealth;
			}
		}
		if (_characters.size > 0) {
			health /= _characters.size;
		}
		return health;
	}

	public float getAverageHealth() {
		float health = 0;
		for (Role c : _characters) {
			if (c != null) {
				health += c.health;
			}
		}
		if (_characters.size > 0) {
			health /= _characters.size;
		}
		return health;
	}

	public float getAverageMana() {
		float mana = 0;
		for (Role c : _characters) {
			if (c != null) {
				mana += c.mana;
			}
		}
		if (_characters.size > 0) {
			mana /= _characters.size;
		}
		return mana;
	}

	public float getAverageMaxMana() {
		float mana = 0;
		for (Role c : _characters) {
			if (c != null) {
				mana += c.maxMana;
			}
		}
		if (_characters.size > 0) {
			mana /= _characters.size;
		}
		return mana;
	}

	public float getAverageStrength() {
		float strength = 0;
		for (Role c : _characters) {
			if (c != null) {
				strength += c.strength;
			}
		}
		if (_characters.size > 0) {
			strength /= _characters.size;
		}
		return strength;
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

	public TArray<Role> canAttackRoleList() {
		TArray<Role> list = new TArray<Role>();
		for (Role c : _characters) {
			if (c != null && !c.isAttack) {
				list.add(c);
			}
		}
		return list;
	}

	public boolean isAttack() {
		for (Role c : _characters) {
			if (c != null && !c.isAttack) {
				return false;
			}
		}
		return true;
	}

	public TArray<Role> canDefenseRoleList() {
		TArray<Role> list = new TArray<Role>();
		for (Role c : _characters) {
			if (c != null && !c.isDefense) {
				list.add(c);
			}
		}
		return list;
	}

	public boolean isDefense() {
		for (Role c : _characters) {
			if (c != null && !c.isDefense) {
				return false;
			}
		}
		return true;
	}

	public TArray<Role> canSkillRoleList() {
		TArray<Role> list = new TArray<Role>();
		for (Role c : _characters) {
			if (c != null && !c.isSkill) {
				list.add(c);
			}
		}
		return list;
	}

	public boolean isSkill() {
		for (Role c : _characters) {
			if (c != null && !c.isSkill) {
				return false;
			}
		}
		return true;
	}

	public TArray<Role> canMoveRoleList() {
		TArray<Role> list = new TArray<Role>();
		for (Role c : _characters) {
			if (c != null && !c.isMoved) {
				list.add(c);
			}
		}
		return list;
	}

	public boolean isMoved() {
		for (Role c : _characters) {
			if (c != null && !c.isMoved) {
				return false;
			}
		}
		return true;
	}

	public TArray<Role> canDeadRoleList() {
		TArray<Role> list = new TArray<Role>();
		for (Role c : _characters) {
			if (c != null && !c.isDead) {
				list.add(c);
			}
		}
		return list;
	}

	public boolean isAllDead() {
		for (Role c : _characters) {
			if (c != null && !c.isDead) {
				return false;
			}
		}
		return true;
	}

	public boolean isLeadDead() {
		if (_leadRole == null) {
			return false;
		}
		return _leadRole.isDead;
	}

	public boolean isLeadAttack() {
		if (_leadRole == null) {
			return false;
		}
		return _leadRole.isAttack;
	}

	public boolean isLeadDefense() {
		if (_leadRole == null) {
			return false;
		}
		return _leadRole.isDefense;
	}

	public boolean isLeadSkill() {
		if (_leadRole == null) {
			return false;
		}
		return _leadRole.isSkill;
	}

	public boolean isLeadMoved() {
		if (_leadRole == null) {
			return false;
		}
		return _leadRole.isMoved;
	}

	public boolean isOver() {
		return isAllDead();
	}

	public LColor getTeamColor() {
		return _teamColor;
	}

	public Team setTeamColor(LColor color) {
		this._teamColor = color;
		return this;
	}

	public Team setTotalAttacksUsed(int i) {
		_totalAttacksUsed = i;
		return this;
	}

	public int getTotalAttacksUsed() {
		return _totalAttacksUsed;
	}

	public Team clear() {
		_characters.clear();
		dirty = true;
		return this;
	}

	@Override
	public void close() {
		clear();
	}

}
