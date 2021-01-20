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

import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;

public class Inventory<T> {

	private ObjectMap<String, Item<T>> _items;

	private int _gold;

	public Inventory() {
		_items = new ObjectMap<String, Item<T>>(128);
		_gold = 0;
	}

	public Inventory<T> subtractGold(int i) {
		_gold -= i;
		return this;
	}

	public Inventory<T> addGold(int i) {
		_gold += i;
		return this;
	}

	public int getGold() {
		return _gold;
	}

	public Inventory<T> setGold(int i) {
		_gold = i;
		return this;
	}

	public boolean addItem(String key, Item<T> obj) {
		if (obj == null) {
			return false;
		}
		return _items.put(key, obj) == null;
	}

	public boolean removeItem(String key) {
		return _items.remove(key) == null;
	}

	public int getItemCount() {
		return _items.size;
	}

	public String[] getItemList() {
		Keys<String> keys = _items.keys();
		String[] names = new String[_items.size];
		int idx = 0;
		for (String name : keys) {
			names[idx++] = name;
		}
		return names;
	}

	public Inventory<T> merge(Inventory<T> i) {
		_items.putAll(i._items);
		this.addGold(i.getGold());
		return this;
	}

	public Inventory<T> clear() {
		_items.clear();
		return this;
	}

}
