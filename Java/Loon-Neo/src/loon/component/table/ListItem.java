/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.component.table;

import loon.LSystem;
import loon.utils.StrBuilder;
import loon.utils.TArray;

public class ListItem {

	protected boolean _dirty;

	protected String _name;

	protected TArray<Object> _list;

	public ListItem() {
		this(new TArray<Object>());
	}

	public ListItem(TArray<Object> list) {
		this._list = list;
		this._dirty = true;
	}

	public ListItem clear() {
		_list.clear();
		_dirty = true;
		return this;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public ListItem updateDirty() {
		this._dirty = !_dirty;
		return this;
	}

	public int size() {
		return _list.size;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
		this._dirty = true;
	}

	public String message() {
		if (_list == null) {
			return LSystem.EMPTY;
		}
		StrBuilder sbr = new StrBuilder(_name);
		for (Object o : _list) {
			sbr.append(o);
		}
		return sbr.toString();
	}

	public TArray<Object> getList() {
		return _list;
	}

	public ListItem setList(TArray<Object> list) {
		this._list = list;
		this._dirty = true;
		return this;
	}
}
