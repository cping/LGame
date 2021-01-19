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

import loon.action.sprite.ISprite;
import loon.utils.TArray;

/**
 * 角色模板,提供了一些基础的人物参数
 */
public class Character extends CharacterValue {

	private TArray<Attribute> _attributes = new TArray<Attribute>();

	private TArray<Item<Object>> _items = new TArray<Item<Object>>();

	private ISprite _roleObject;

	public Character(String name) {
		this(null, name, 100, 100, 5, 5, 5, 5, 5, 5, 5);
	}

	public Character(CharacterInfo info, String name) {
		super(name, info, info.updateMaxHealth(0), info.updateManaPoints(0), info.updateAttack(0),
				info.updateDefence(0), info.updateStrength(0), info.updateIntelligence(0), info.updateFitness(0),
				info.updateDexterity(0), info.updateAgility(0));
	}

	public Character(CharacterInfo info, String name, int maxHealth, int maxMana, int attack, int defence, int strength,
			int intelligence, int fitness, int dexterity, int agility) {
		super(name, info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity, agility);
	}

	public Character addAttribute(Attribute attribute) {
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

	public Character addItem(Item<Object> item) {
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

	public float getX() {
		if (_roleObject != null) {
			return _roleObject.getX();
		}
		return 0f;
	}

	public float getY() {
		if (_roleObject != null) {
			return _roleObject.getY();
		}
		return 0f;
	}

	public ISprite getRoleObject() {
		return _roleObject;
	}

	public Character setRoleObject(ISprite r) {
		this._roleObject = r;
		return this;
	}
}
