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

import loon.geom.RectBox;
import loon.geom.Shape;
import loon.utils.TArray;

public class Inventory {

	private final TArray<IItem> _items;

	private float _gold;

	public Inventory() {
		_items = new TArray<IItem>(32);
		_gold = 0;
	}

	public Inventory addGold(float i) {
		_gold += i;
		return this;
	}

	public Inventory subGold(float i) {
		_gold -= i;
		return this;
	}

	public Inventory mulGold(float i) {
		_gold *= i;
		return this;
	}

	public Inventory divGold(float i) {
		_gold /= i;
		return this;
	}

	public Inventory setGold(float i) {
		_gold = i;
		return this;
	}

	public float getGold() {
		return _gold;
	}

	public Inventory swap(IItem a, IItem b) {
		_items.swap(a, b);
		return this;
	}

	public Inventory swap(int a, int b) {
		_items.swap(a, b);
		return this;
	}

	public IItem getItem(float x, float y) {
		for (int i = _items.size - 1; i > -1; i--) {
			final IItem item = _items.get(i);
			final RectBox rect = item.getArea();
			if (rect != null && rect.contains(x, y)) {
				return item;
			}
		}
		return null;
	}

	public boolean contains(float x, float y) {
		for (int i = _items.size - 1; i > -1; i--) {
			final RectBox rect = _items.get(i).getArea();
			if (rect != null) {
				return rect.contains(x, y);
			}
		}
		return false;
	}

	public boolean collided(Shape shape) {
		for (int i = _items.size - 1; i > -1; i--) {
			final RectBox rect = _items.get(i).getArea();
			if (rect != null) {
				return rect.collided(shape);
			}
		}
		return false;
	}

	public int getItemToIndex(IItem obj) {
		if (obj == null) {
			return -1;
		}
		int idx = 0;
		for (int i = 0; i < _items.size; i++) {
			final IItem item = _items.get(i);
			if (obj == item) {
				return idx;
			}
			idx++;
		}
		return -1;

	}

	public boolean addItem(IItem obj) {
		if (obj == null) {
			return false;
		}
		return _items.add(obj);
	}

	public boolean removeItem(IItem obj) {
		return _items.remove(obj);
	}

	public IItem removeItemIndex(int idx) {
		return _items.removeIndex(idx);
	}

	public IItem popItem() {
		return _items.pop();
	}

	public IItem peekItem() {
		return _items.peek();
	}

	public IItem getItem(int idx) {
		return _items.get(idx);
	}

	public int getItemCount() {
		return _items.size;
	}

	public Inventory sort() {
		_items.sort(new Comparator<IItem>() {

			@Override
			public int compare(final IItem o1, final IItem o2) {
				if (o1 != null && o2 != null) {
					return o1.getItemTypeId() - o2.getItemTypeId();
				}
				return 0;
			}
		});
		return this;
	}

	public Inventory sort(Comparator<IItem> comp) {
		_items.sort(comp);
		return this;
	}

	public String[] getItemList() {
		String[] names = new String[_items.size];
		for (int i = 0; i < _items.size; i++) {
			names[i] = _items.get(i).getName();
		}
		return names;
	}

	public Inventory merge(Inventory i) {
		_items.addAll(i._items);
		this.addGold(i.getGold());
		return this;
	}

	public Inventory clear() {
		_items.clear();
		return this;
	}

}
