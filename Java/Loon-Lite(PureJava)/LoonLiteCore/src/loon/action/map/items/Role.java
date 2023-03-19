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

import loon.action.ActionBind;
import loon.action.ActionTween;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.utils.TArray;

/**
 * 角色模板,提供了一些基础的人物参数
 */
public class Role extends RoleValue implements ActionBind {

	private final TArray<Attribute> _attributes = new TArray<Attribute>();

	private final TArray<Item<Object>> _items = new TArray<Item<Object>>();

	private ISprite _roleObject;

	private Object _tag;

	public Role(int id, String name) {
		this(id, null, name, 100, 100, 5, 5, 5, 5, 5, 5, 5);
	}

	public Role(int id, RoleInfo info, String name) {
		super(id, name, info, info.updateMaxHealth(0), info.updateManaPoints(0), info.updateAttack(0),
				info.updateDefence(0), info.updateStrength(0), info.updateIntelligence(0), info.updateFitness(0),
				info.updateDexterity(0), info.updateAgility(0));
	}

	public Role(int id, RoleInfo info, String name, int maxHealth, int maxMana, int attack, int defence,
			int strength, int intelligence, int fitness, int dexterity, int agility) {
		super(id, name, info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity, agility);

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
	public void setX(float x) {
		if (_roleObject != null) {
			_roleObject.setX(x);
		}
	}

	@Override
	public void setY(float y) {
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
		if (_roleObject != null) {
			_roleObject.setScale(sx, sy);
		}
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
		if (_roleObject != null) {
			_roleObject.setAlpha(alpha);
		}
	}

	@Override
	public void setLocation(float x, float y) {
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
}
