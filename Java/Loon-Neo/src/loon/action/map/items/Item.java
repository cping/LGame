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

import loon.LTexture;
import loon.LTextures;
import loon.geom.RectBox;
import loon.geom.XYZW;

public class Item<T> implements IItem {

	protected RectBox _itemArea;

	protected LTexture _image;

	protected String _name;

	protected T _item;

	public Item(String name, String imagePath, XYZW rect, T item) {
		this(name, imagePath, rect.getX(), rect.getY(), rect.getZ(), rect.getW(), item);
	}

	public Item(String name, String imagePath, T item) {
		this(name, imagePath, 0f, 0f, 0f, 0f, item);
	}

	public Item(String name, String imagePath, float x, float y, float w, float h, T item) {
		this(name, LTextures.loadTexture(imagePath), x, y, w, h, item);
	}

	public Item(String name, T item) {
		this(name, (LTexture) null, item);
	}

	public Item(String name, LTexture tex, T item) {
		this(name, tex, 0f, 0f, 0f, 0f, item);
	}

	public Item(String name, LTexture tex, float x, float y, float w, float h, T item) {
		this._name = name;
		this._item = item;
		this._image = tex;
		if (x == 0f && y == 0f && w == 0f && h == 0f) {
			this._itemArea = null;
		} else {
			this._itemArea = new RectBox(x, y, w, h);
		}
	}

	public Item<T> setArea(float x, float y, float w, float h) {
		if (this._itemArea == null) {
			this._itemArea = new RectBox(x, y, w, h);
		} else {
			this._itemArea.setBounds(x, y, w, h);
		}
		return this;
	}

	@Override
	public String getName() {
		return this._name;
	}

	public Item<T> setName(String name) {
		this._name = name;
		return this;
	}

	@Override
	public T getItem() {
		return this._item;
	}

	public Item<T> setItem(T o) {
		this._item = o;
		return this;
	}

	@Override
	public LTexture getTexture() {
		return _image;
	}

	@Override
	public RectBox getArea() {
		return _itemArea;
	}

	public boolean equals(Item<T> e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		final boolean checkItem = e._item != null;
		final boolean checkName = e._name != null;
		final boolean checkTexture = e._image != null;
		final boolean checkRect = e._itemArea != null;
		if (checkItem && _item == null) {
			return false;
		}
		if (checkName && _name == null) {
			return false;
		}
		if (checkTexture && _image == null) {
			return false;
		}
		if (checkRect && _itemArea == null) {
			return false;
		}
		if (checkItem) {
			if (!e._item.equals(_item)) {
				return false;
			}
		}
		if (checkName) {
			if (!e._name.equals(_name)) {
				return false;
			}
		}
		if (checkTexture) {
			if (!e._image.equals(_image)) {
				return false;
			}
		}
		if (checkRect) {
			if (!e._itemArea.equals(_itemArea)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof Item) {
			return equals((Item<T>) o);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _item == null ? super.hashCode() : _item.hashCode();
	}

}
