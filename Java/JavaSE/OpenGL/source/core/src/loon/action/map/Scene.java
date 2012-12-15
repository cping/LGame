package loon.action.map;

import java.util.ArrayList;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class Scene {
	
	private String name;

	private ArrayList<Item> items = new ArrayList<Item>();

	private ArrayList<Character> characters = new ArrayList<Character>();

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
		for (int i = 0; i < this.items.size(); i++) {
			if (getItem(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Item removeItem(int index) {
		return this.items.remove(index);
	}

	public int countItems() {
		return this.items.size();
	}

	public void addCharacter(Character character) {
		this.characters.add(character);
	}

	public Character getCharacter(int index) {
		return this.characters.get(index);
	}

	public Character getCharacter(String name) {
		int index = findCharacter(name);
		if (index == -1) {
			return null;
		}
		return getCharacter(index);
	}

	public int findCharacter(String name) {
		for (int i = 0; i < this.characters.size(); i++) {
			if (getCharacter(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public Character removeCharacter(int index) {
		return this.characters.remove(index);
	}

	public int countCharacters() {
		return this.characters.size();
	}
}
