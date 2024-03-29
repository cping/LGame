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
import loon.LSystem;
import loon.utils.Properties;

public class ItemInfo implements LRelease {

	protected ItemType _itemType;

	protected float _gold;

	protected String _name;

	protected String _description;

	protected Properties<String, Attribute> _attributes;

	public ItemInfo() {
		this(LSystem.UNKNOWN);
	}

	public ItemInfo(String name) {
		this(name, name);
	}

	public ItemInfo(String name, String de) {
		this(name, de, new Properties<String, Attribute>());
	}

	public ItemInfo(String name, String de, Properties<String, Attribute> attributes) {
		this._attributes = attributes;
		this._name = name;
		this._description = de;
	}

	public ItemInfo set(ItemInfo info) {
		this._attributes = info._attributes;
		this._name = info._name;
		this._itemType = info._itemType;
		this._description = info._description;
		this._gold = info._gold;
		return this;
	}

	public ItemInfo cpy() {
		ItemInfo info = new ItemInfo(_name, _description);
		info._attributes = new Properties<String, Attribute>(_attributes);
		if (this._itemType == null) {
			info._itemType = this._itemType;
		} else {
			info._itemType = this._itemType.cpy();
		}
		info._gold = this._gold;
		return info;
	}

	public Attribute putAttribute(String name, Attribute a) {
		return _attributes.put(name, a);
	}

	public Attribute removeAttribute(String name) {
		return _attributes.remove(name);
	}

	public Attribute getAttribute(String name) {
		return _attributes.get(name);
	}

	public Properties<String, Attribute> getAttribute() {
		return _attributes;
	}

	public String getName() {
		return this._name;
	}

	public String getDescription() {
		return _description;
	}

	public ItemInfo setDescription(String d) {
		this._description = d;
		return this;
	}

	public ItemInfo addGold(float i) {
		_gold += i;
		return this;
	}

	public ItemInfo subGold(float i) {
		_gold -= i;
		return this;
	}

	public ItemInfo mulGold(float i) {
		_gold *= i;
		return this;
	}

	public ItemInfo divGold(float i) {
		_gold /= i;
		return this;
	}

	public ItemInfo setGold(float i) {
		_gold = i;
		return this;
	}

	public float getGold() {
		return _gold;
	}

	public ItemType getItemType() {
		return _itemType;
	}

	public ItemInfo setItemType(ItemType i) {
		this._itemType = i;
		return this;
	}

	@Override
	public void close() {
		_attributes.close();
	}

}
