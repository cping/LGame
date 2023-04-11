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
package loon.component.table;

import loon.LSysException;
import loon.utils.HelperUtils;
import loon.utils.PageList;
import loon.utils.TArray;

public class TableView {

	private TArray<String> _names = new TArray<String>();

	private boolean _dirty;

	private PageList<ListItem> _pageList;

	public TableView() {
		this(0, -1);
	}

	public TableView(int pageSize) {
		this(0, pageSize);
	}

	public TableView(int pageNumber, int pageSize) {
		this(new TArray<ListItem>(), pageNumber, pageSize);
	}

	public TableView(TArray<ListItem> items) {
		this(items, 0, -1);
	}

	public TableView(TArray<ListItem> items, int pageNumber, int pageSize) {
		this._pageList = new PageList<ListItem>(items, pageNumber, pageSize);
		this._dirty = true;
	}

	public PageList<ListItem> page() {
		return _pageList;
	}

	public TableView setPage(int page) {
		_pageList.setPage(page);
		return this;
	}

	public TableView next() {
		_pageList.next();
		return this;
	}

	public TableView back() {
		_pageList.back();
		return this;
	}

	public TableView clear() {
		_pageList.clear();
		_dirty = true;
		return this;
	}

	public boolean isDirty() {
		return _dirty || _pageList.isDirty();
	}

	public TableView setDirty(boolean d) {
		this._dirty = d;
		_pageList.setDirty(d);
		return this;
	}

	public TableView updateDirty() {
		this._dirty = !_dirty;
		_pageList.updateDirty();
		return this;
	}

	public TArray<ListItem> all() {
		return _pageList.all();
	}

	public TArray<ListItem> getData() {
		return _pageList.getData();
	}

	public int size() {
		return _pageList.size();
	}

	public TableView removeLine(int lineNo) {
		if (lineNo > -1) {
			_pageList.removeIndex(lineNo);
			_dirty = true;
		}
		return this;
	}

	public TableView addRows(Object... rows) {
		ListItem itme = new ListItem();
		for (int i = 0; i < rows.length; i++) {
			itme._list.add(HelperUtils.toStr(rows[i]));
		}
		_pageList.add(itme);
		_dirty = true;
		return this;
	}

	public TableView setRows(int lineNo, Object... rows) {
		if (lineNo > -1) {
			ListItem itme = new ListItem();
			for (int i = 0; i < rows.length; i++) {
				itme._list.add(HelperUtils.toStr(rows[i]));
			}
			_pageList.setValue(lineNo, itme);
			_dirty = true;
		}
		return this;
	}

	public TableView addColumn(int idx, Object o) {
		ListItem item = _pageList.get(idx);
		if (item != null) {
			item._list.add(HelperUtils.toStr(o));
			_dirty = true;
		}
		return this;
	}

	public TableView setValue(int rowLine, int colLine, Object o) {
		ListItem item = _pageList.get(rowLine);
		if (item != null) {
			if (colLine >= item._list.size) {
				throw new LSysException("Object column:" + colLine + " out table size range !");
			} else {
				item._list.set(colLine, HelperUtils.toStr(o));
			}
			_dirty = true;
		}
		return this;
	}

	public ListItem getIndex(int idx) {
		return _pageList.get(idx);
	}

	public ListItem removeIndex(int idx) {
		ListItem item = _pageList.removeIndex(idx);
		if (item != null) {
			_dirty = true;
		}
		return item;
	}

	public TableView columns(String name, Object... cols) {
		_names.add(name);
		for (int i = 0; i < cols.length; i++) {
			if (i < _pageList.size()) {
				ListItem item = _pageList.get(i);
				item._list.add(HelperUtils.toStr(cols[i]));
			}
		}
		_dirty = true;
		return this;
	}

	public int getColumnCount() {
		return _names.size;
	}

	public String getColumnName(int columnIndex) {
		return _names.get(columnIndex);
	}

	public TableView columnNames(String... names) {
		for (int i = 0; i < names.length; i++) {
			_names.add(names[i]);
			_dirty = true;
		}
		return this;
	}

}
