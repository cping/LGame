package loon.core.graphics.component.table;

import loon.utils.collection.Array;

public class SimpleTableModel implements ITableModel {
	
	private Array<ListItem> _list;

	public SimpleTableModel(Array<ListItem> list) {
		_list = list;
	}

	public String getColumnName(int columnIndex) {
		if (_list.size() == 0) {
			return "...";
		}
		return _list.get(columnIndex).name;
	}

	public int getColumnCount() {
		return _list.size();
	}

	public Object getValue(int row, int column) {
		if (column > _list.size() || row > _list.get(column).list.size()) {
			return "...";
		}
		return _list.get(column).list.get(row);
	}

	public int getRowCount() {
		if (_list.size() == 0) {
			return 0;
		}
		return _list.get(0).list.size();
	}

	public void clear() {
		_list.clear();
	}

	public Object getValue(int row) {
		if (_list.size() == 0) {
			return "...";
		}
		return _list.get(0).list.get(row);
	}
}
