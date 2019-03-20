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
package loon.action.map;

import loon.action.sprite.ISprite;
import loon.utils.TArray;

public class Character extends CharacterValue {

	private String name;

	private TArray<Attribute> attributes = new TArray<Attribute>();

	private TArray<Item> items = new TArray<Item>();

	private ISprite roleObject;

	public Character(String name) {
		this(null, name, 100, 100, 5, 5, 5, 5, 5, 5, 5);
	}

	public Character(CharacterInfo info, String name) {
		super(info, info.updateMaxHealth(0), info.updateManaPoints(0), info.updateAttack(0), info.updateDefence(0),
				info.updateStrength(0), info.updateIntelligence(0), info.updateFitness(0), info.updateDexterity(0),
				info.updateAgility(0));
		this.name = name;
	}

	public Character(CharacterInfo info, String name, int maxHealth, int maxMana, int attack, int defence, int strength,
			int intelligence, int fitness, int dexterity, int agility) {
		super(info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity, agility);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	public Attribute getAttribute(int index) {
		return this.attributes.get(index);
	}

	public Attribute getAttribute(String name) {
		int index = findAttribute(name);
		if (index == -1) {
			return null;
		}
		return getAttribute(index);
	}

	public int findAttribute(String name) {
		for (int i = 0; i < this.attributes.size; i++) {
			if (getAttribute(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public void removeAttribute(int index) {
		this.attributes.removeIndex(index);
	}

	public int countAttributes() {
		return this.attributes.size;
	}

	public void addItem(Item item) {
		this.items.add(item);
	}

	public Item getItem(int index) {
		return this.items.get(index);
	}

	public Item getItem(String name) {
		int index = findItem(name);
		if (index == -1) {
			return null;
		}
		return getItem(index);
	}

	public int findItem(String name) {
		for (int i = 0; i < this.items.size; i++) {
			if (getItem(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Item removeItem(int index) {
		return this.items.removeIndex(index);
	}

	public int countItems() {
		return this.items.size;
	}

	public float getX() {
		if (roleObject != null) {
			return roleObject.getX();
		}
		return 0f;
	}

	public float getY() {
		if (roleObject != null) {
			return roleObject.getY();
		}
		return 0f;
	}

	public ISprite getRoleObject() {
		return roleObject;
	}

	public void setRoleObject(ISprite roleObject) {
		this.roleObject = roleObject;
	}
}
