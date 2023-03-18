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

public class Item<T> {

	protected String _name;

	protected T _item;

	public Item(String name, T item) {
		this._name = name;
		this._item = item;
	}

	public String getName() {
		return this._name;
	}

	public Item<T> setName(String name) {
		this._name = name;
		return this;
	}

	public T getItem() {
		return this._item;
	}

	public Item<T> setItem(T o) {
		this._item = o;
		return this;
	}

	@Override
	public int hashCode() {
		return _item == null ? super.hashCode() : _item.hashCode();
	}

}
