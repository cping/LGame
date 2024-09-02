/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import java.util.Comparator;

import loon.LSysException;
import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.map.battle.BattleProcess;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.events.EventActionN;
import loon.events.EventActionT;
import loon.geom.RectBox;
import loon.utils.TArray;
import loon.utils.reply.Callback;

/**
 * 角色模板,提供了一些基础的人物参数与行为调用函数
 */
public class Role extends RoleValue implements ActionBind, EventActionN {

	public static class ActionPriorityComparator implements Comparator<Role> {

		@Override
		public int compare(Role o1, Role o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return o2.getActionPriority() - o1.getActionPriority();
		}

	}

	public final static ActionPriorityComparator ActionPrioritySort = new ActionPriorityComparator();

	private final TArray<Attribute> _attributes = new TArray<Attribute>();

	private final TArray<Item<Object>> _items = new TArray<Item<Object>>();

	private BattleProcess _battleProcess;

	private ISprite _roleObject;

	private RoleActionType _lastActionType = null;

	private RoleActionType _actionType = RoleActionType.Other;

	private EventActionT<Role> _onActionUpdate;

	private Object _tag;

	public Role(int id, String name) {
		this(id, null, name, 100, 100, 5, 5, 5, 5, 5, 5, 5, 1);
	}

	public Role(int id, RoleEquip info, String name) {
		super(id, name, info, info.updateMaxHealth(0), info.updateManaPoints(0), info.updateAttack(0),
				info.updateDefence(0), info.updateStrength(0), info.updateIntelligence(0), info.updateFitness(0),
				info.updateDexterity(0), info.updateAgility(0), 1);
	}

	public Role(int id, RoleEquip info, String name, int maxHealth, int maxMana, int attack, int defence, int strength,
			int intelligence, int fitness, int dexterity, int agility, int lv) {
		super(id, name, info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity, agility,
				lv);
	}

	public <T> Role choseAttack(Callback<Role> v) {
		return choseAttack(v, this);
	}

	public <T> Role choseAttack(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Attack);
	}

	public <T> Role choseDefend(Callback<Role> v) {
		return choseDefend(v, this);
	}

	public <T> Role choseDefend(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Defend);
	}

	public <T> Role choseAbility(Callback<Role> v) {
		return choseAbility(v, this);
	}

	public <T> Role choseAbility(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Ability);
	}

	public <T> Role choseMana(Callback<Role> v) {
		return choseMana(v, this);
	}

	public <T> Role choseMana(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Mana);
	}

	public <T> Role choseItem(Callback<Role> v) {
		return choseItem(v, this);
	}

	public <T> Role choseItem(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Item);
	}

	public <T> Role choseMove(Callback<Role> v) {
		return choseMove(v, this);
	}

	public <T> Role choseMove(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Move);
	}

	public <T> Role choseWait(Callback<Role> v) {
		return choseWait(v, this);
	}

	public <T> Role choseWait(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Wait);
	}

	public <T> Role choseEscape(Callback<Role> v) {
		return choseEscape(v, this);
	}

	public <T> Role choseEscape(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Escape);
	}

	public <T> Role choseSay(Callback<Role> v) {
		return choseSay(v, this);
	}

	public <T> Role choseSay(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Say);
	}

	public <T> Role choseChange(Callback<Role> v) {
		return choseChange(v, this);
	}

	public <T> Role choseChange(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Change);
	}

	public <T> Role choseDie(Callback<Role> v) {
		return choseDie(v, this);
	}

	public <T> Role choseDie(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Die);
	}

	public <T> Role choseLive(Callback<Role> v) {
		return choseLive(v, this);
	}

	public <T> Role choseLive(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Live);
	}

	public <T> Role choseOther(Callback<Role> v) {
		return choseOther(v, this);
	}

	public <T> Role choseOther(Callback<Role> v, Role enemy) {
		return choseActionCall(v, enemy, RoleActionType.Other);
	}

	public <T> Role choseActionCall(Callback<Role> v, Role enemy, RoleActionType actionType) {
		try {
			Role role = (enemy == null ? this : enemy);
			if (role != null && role == this) {
				switch (actionType) {
				case Attack:
					if(role.isAttack) {
						throw new LSysException("This role has completed the Attack !");
					}
					role.isAttack = true;
					break;
				case Defend:
					if(role.isDefense) {
						throw new LSysException("This role has completed the Defense !");
					}
					role.isDefense = true;
					break;
				case Mana:
				case Ability:
					if(role.isDefense) {
						throw new LSysException("This role has completed the Mana/Ability !");
					}
					role.isSkill = true;
					break;
				case Move:
					if(role.isMoved) {
						throw new LSysException("This role has completed the Move !");
					}
					role.isMoved = true;
					break;
				case Die:
					if(role.isDead) {
						throw new LSysException("This role has completed the Die !");
					}
					role.isDead = true;
					break;
				case Live:
					if(!role.isDead) {
						throw new LSysException("This role has completed the Live !");
					}
					role.isDead = false;
					break;
				default:
					break;
				}
			}
			v.onSuccess(role);
			onUpdateRoleAction(actionType);
		} catch (Exception e) {
			v.onFailure(e);
		}
		return this;
	}

	private void onUpdateRoleAction(RoleActionType actionType) {
		this._actionType = actionType;
		if (_onActionUpdate != null) {
			_onActionUpdate.update(this);
		}
		this._lastActionType = this._actionType;
	}

	public Role addAttribute(Attribute attribute) {
		this._attributes.add(attribute);
		return this;
	}

	public Attribute getAttribute(int index) {
		return this._attributes.get(index);
	}

	public Attribute getAttribute(String name) {
		int index = findAttribute(name);
		if (index == -1) {
			return null;
		}
		return getAttribute(index);
	}

	public int findAttribute(String name) {
		for (int i = 0; i < this._attributes.size; i++) {
			if (getAttribute(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Attribute removeAttribute(int index) {
		return this._attributes.removeIndex(index);
	}

	public int countAttributes() {
		return this._attributes.size;
	}

	public Role addItem(Item<Object> item) {
		this._items.add(item);
		return this;
	}

	public Item<Object> getItem(int index) {
		return this._items.get(index);
	}

	public Item<Object> getItem(String name) {
		int index = findItem(name);
		if (index == -1) {
			return null;
		}
		return getItem(index);
	}

	public int findItem(String name) {
		for (int i = 0; i < this._items.size; i++) {
			if (getItem(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Item<Object> removeItem(int index) {
		return this._items.removeIndex(index);
	}

	public int countItems() {
		return this._items.size;
	}

	public Object getTag() {
		return _tag == null ? ((_roleObject != null) ? _roleObject.getTag() : null) : null;
	}

	public Role setTag(Object t) {
		this._tag = t;
		return this;
	}

	@Override
	public void update() {

	}

	@Override
	public void setX(float x) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setX(x);
		}
	}

	@Override
	public void setY(float y) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setY(y);
		}
	}

	@Override
	public float getX() {
		if (_roleObject != null) {
			return _roleObject.getX();
		}
		return 0f;
	}

	@Override
	public float getY() {
		if (_roleObject != null) {
			return _roleObject.getY();
		}
		return 0f;
	}

	public ISprite getRoleObject() {
		return _roleObject;
	}

	public Role setRoleObject(ISprite r) {
		this._roleObject = r;
		return this;
	}

	@Override
	public Field2D getField2D() {
		if (_roleObject != null) {
			return _roleObject.getField2D();
		}
		return null;
	}

	@Override
	public void setVisible(boolean v) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setVisible(v);
		}
	}

	@Override
	public boolean isVisible() {
		if (_roleObject != null) {
			return _roleObject.isVisible();
		}
		return false;
	}

	@Override
	public int x() {
		if (_roleObject != null) {
			return _roleObject.x();
		}
		return 0;
	}

	@Override
	public int y() {
		if (_roleObject != null) {
			return _roleObject.y();
		}
		return 0;
	}

	@Override
	public float getScaleX() {
		if (_roleObject != null) {
			return _roleObject.getScaleX();
		}
		return 1f;
	}

	@Override
	public float getScaleY() {
		if (_roleObject != null) {
			return _roleObject.getScaleY();
		}
		return 1f;
	}

	@Override
	public void setColor(LColor color) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setColor(color);
		}
	}

	@Override
	public LColor getColor() {
		if (_roleObject != null) {
			return _roleObject.getColor();
		}
		return LColor.white.cpy();
	}

	@Override
	public void setScale(float sx, float sy) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setScale(sx, sy);
		}
	}

	@Override
	public Role setSize(float w, float h) {
		if (isLocked()) {
			return this;
		}
		if (_roleObject != null) {
			_roleObject.setSize(w, h);
		}
		return this;
	}

	@Override
	public float getRotation() {
		if (_roleObject != null) {
			return _roleObject.getRotation();
		}
		return 0f;
	}

	@Override
	public void setRotation(float r) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setRotation(r);
		}
	}

	@Override
	public float getWidth() {
		if (_roleObject != null) {
			return _roleObject.getWidth();
		}
		return 0f;
	}

	@Override
	public float getHeight() {
		if (_roleObject != null) {
			return _roleObject.getHeight();
		}
		return 0f;
	}

	@Override
	public float getAlpha() {
		if (_roleObject != null) {
			return _roleObject.getAlpha();
		}
		return 0f;
	}

	@Override
	public void setAlpha(float alpha) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setAlpha(alpha);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		if (isLocked()) {
			return;
		}
		if (_roleObject != null) {
			_roleObject.setLocation(x, y);
		}
	}

	@Override
	public boolean isBounded() {
		if (_roleObject != null) {
			return _roleObject.isBounded();
		}
		return false;
	}

	@Override
	public boolean isContainer() {
		if (_roleObject != null) {
			return _roleObject.isContainer();
		}
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		if (_roleObject != null) {
			return _roleObject.inContains(x, y, w, h);
		}
		return false;
	}

	@Override
	public RectBox getRectBox() {
		if (_roleObject != null) {
			return _roleObject.getRectBox();
		}
		return null;
	}

	@Override
	public float getContainerWidth() {
		if (_roleObject != null) {
			return _roleObject.getContainerWidth();
		}
		return 0f;
	}

	@Override
	public float getContainerHeight() {
		if (_roleObject != null) {
			return _roleObject.getContainerHeight();
		}
		return 0f;
	}

	@Override
	public ActionTween selfAction() {
		if (_roleObject != null) {
			return _roleObject.selfAction();
		}
		return null;
	}

	@Override
	public boolean isActionCompleted() {
		if (_roleObject != null) {
			return _roleObject.isActionCompleted();
		}
		return false;
	}

	public RoleActionType getActionType() {
		return _actionType;
	}

	public RoleActionType getLastActionType() {
		return _lastActionType;
	}

	public EventActionT<Role> getOnActionUpdate() {
		return _onActionUpdate;
	}

	public Role setOnActionUpdate(EventActionT<Role> u) {
		this._onActionUpdate = u;
		return this;
	}

	public BattleProcess getBattleProcess() {
		return _battleProcess;
	}

	public Role setBattleProcess(BattleProcess battleProcess) {
		this._battleProcess = battleProcess;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof Role) {
			Role ch = ((Role) o);
			if (ch._roleObject.equals(this._roleObject) && ch._tag.equals(this._tag)
					&& ch._attributes.equals(this._attributes) && ch._items.equals(this._items)) {
				return true;
			}
		}
		return false;
	}

}
