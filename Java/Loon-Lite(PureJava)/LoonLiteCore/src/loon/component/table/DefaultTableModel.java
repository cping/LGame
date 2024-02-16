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

import loon.LRelease;
import loon.LSystem;
import loon.utils.StrBuilder;
import loon.utils.TArray;

public class DefaultTableModel implements ITableModel, LRelease {

	private final static String OMIT = "...";

	private TableView _view;

	public DefaultTableModel(TArray<ListItem> items) {
		this(new TableView(items));
	}

	public DefaultTableModel(TableView list) {
		_view = list;
	}

	@Override
	public DefaultTableModel updateDirty() {
		if (_view != null) {
			for (ListItem item : _view.all()) {
				item.updateDirty();
			}
			_view.updateDirty();
		}
		return this;
	}

	@Override
	public ITableModel setDirty(boolean d) {
		if (_view != null) {
			for (ListItem item : _view.all()) {
				item.setDirty(d);
			}
			_view.setDirty(d);
		}
		return this;
	}

	@Override
	public boolean isDirty() {
		if (_view != null) {
			for (ListItem item : _view.all()) {
				if (item.isDirty()) {
					return true;
				}
			}
			return _view.isDirty();
		}
		return false;
	}

	@Override
	public String message() {
		if (_view == null) {
			return null;
		}
		StrBuilder sbr = new StrBuilder();
		for (ListItem item : _view.all()) {
			sbr.append(item.message());
		}
		return sbr.toString();
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (_view.size() == 0) {
			return OMIT;
		}
		return _view.getColumnName(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return _view.getColumnCount();
	}

	@Override
	public Object getValue(int row, int column) {
		final TArray<ListItem> list = _view.getData();
		if (row < list.size) {
			final ListItem item = list.get(row);
			final TArray<Object> objs = item._list;
			if (column < objs.size) {
				return objs.get(column);
			}
		}
		return LSystem.EMPTY;
	}

	@Override
	public int getRowCount() {
		return _view.getData().size();
	}

	public DefaultTableModel clear() {
		if (_view != null) {
			for (ListItem item : _view.all()) {
				item._dirty = true;
			}
			_view.clear();
		}
		return this;
	}

	public Object getValue(int row) {
		if (_view.size() == 0) {
			return OMIT;
		}
		return _view.getData().get(row);
	}

	@Override
	public String toString() {
		return message();
	}

	@Override
	public void close() {
		if (_view != null) {
			_view.close();
		}
	}

}
