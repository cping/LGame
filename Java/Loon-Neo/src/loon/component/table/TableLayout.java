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

import loon.canvas.LColor;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class TableLayout extends LContainer {

	private final TArray<TableLayoutRow> _newRows = new TArray<TableLayoutRow>();

	private TableLayoutRow[] _tableRows;

	private LColor _layoutDrawRectColor = LColor.gray;

	private boolean _grid = true;

	public TableLayout(int x, int y, int w, int h) {
		this(x, y, w, h, 4, 4);
	}

	public TableLayout(int x, int y, int w, int h, int cols, int rows) {
		super(x, y, w, h);
		prepareTable(cols, rows);
	}

	public TableLayout setLayoutDrawRectColor(LColor c) {
		_layoutDrawRectColor = c;
		return this;
	}

	public LColor getLayoutDrawRectColor() {
		return _layoutDrawRectColor;
	}

	@Override
	protected void renderComponents(GLEx g) {
		for (int i = 0; i < getComponentCount(); i++) {
			super._childs[i].createUI(g);
		}
		if (_grid) {
			int tmp = g.color();
			for (int i = 0; i < _tableRows.length; i++) {
				_tableRows[i].paint(g);
			}
			g.drawRect(getX(), getY(), getWidth(), getHeight(), _layoutDrawRectColor);
			g.setColor(tmp);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	private void prepareTable(int cols, int rows) {
		if (_tableRows == null || _tableRows.length != rows) {
			_tableRows = new TableLayoutRow[rows];
		}
		if (rows > 0 && cols > 0) {
			final int rowHeight = MathUtils.ceil(getHeight() / rows);
			for (int i = 0; i < rows; i++) {
				if (_tableRows[i] == null) {
					_tableRows[i] = new TableLayoutRow(x(), y() + (i * rowHeight), MathUtils.ceil(getWidth()),
							rowHeight, cols);
				} else {
					_tableRows[i].setSize(x(), y() + (i * rowHeight), MathUtils.ceil(getWidth()), rowHeight);
				}
			}
		}
	}

	public void setComponent(LComponent component, int col, int row) {
		add(component);
		remove(_tableRows[row].getComponent(col));
		_tableRows[row].setComponent(component, col);
	}

	public void removeComponent(int col, int row) {
		remove(_tableRows[row].getComponent(col));
		_tableRows[row].setComponent(null, col);
	}

	public void addRow(int column, int position) {
		_newRows.clear();
		int newRowHeight = MathUtils.ceil(getHeight() / (_tableRows.length + 1));
		if (canAddRow(newRowHeight)) {
			final int width = MathUtils.ceil(getWidth());
			if (position == 0) {
				_newRows.add(new TableLayoutRow(x(), y(), width, newRowHeight, column));
			}
			for (int i = 0; i < _tableRows.length; i++) {
				if (i == position && position != 0) {
					_newRows.add(new TableLayoutRow(x(), y(), width, newRowHeight, column));
				}
				_newRows.add(_tableRows[i]);
			}
			if (position == _tableRows.length && position != 0) {
				_newRows.add(new TableLayoutRow(x(), y(), width, newRowHeight, column));
			}
			for (int i = 0; i < _newRows.size; i++) {
				((TableLayoutRow) _newRows.get(i)).setY(y() + (i * newRowHeight));
				((TableLayoutRow) _newRows.get(i)).setHeight(newRowHeight);
			}
			if (_tableRows == null || _tableRows.length != _newRows.size) {
				_tableRows = new TableLayoutRow[_newRows.size];
			}
			for (int i = 0; i < _newRows.size; i++) {
				_tableRows[i] = _newRows.get(i);
			}
		}
	}

	public void addRow(int column) {
		addRow(column, _tableRows.length);
	}

	private boolean canAddRow(int newRowHeight) {
		if (_tableRows != null && _tableRows.length > 0) {
			return _tableRows[0].canSetHeight(newRowHeight);
		}
		return true;
	}

	public boolean setColumnWidth(int width, int col, int row) {
		return _tableRows[row].setColumnWidth(width, col);
	}

	public boolean setColumnHeight(int height, int row) {
		if (!_tableRows[row].canSetHeight(height)) {
			return false;
		}
		_tableRows[row].setHeight(height);
		return true;
	}

	public void setMargin(int leftMargin, int rightMargin, int topMargin, int bottomMargin, int col, int row) {
		_tableRows[row].getColumn(col).setMargin(leftMargin, rightMargin, topMargin, bottomMargin);
	}

	public void setAlignment(int horizontalAlignment, int verticalAlignment, int col, int row) {
		_tableRows[row].getColumn(col).setHorizontalAlignment(horizontalAlignment);
		_tableRows[row].getColumn(col).setVerticalAlignment(verticalAlignment);
	}

	public int getRows() {
		return _tableRows.length;
	}

	public int getColumns(int row) {
		return _tableRows[row].getCoulumnSize();
	}

	@Override
	public void setWidth(float width) {
		boolean couldShrink = true;
		for (int i = 0; i < _tableRows.length; i++) {
			if (!_tableRows[i].setWidth((int) width)) {
				couldShrink = false;
			}
		}
		if (couldShrink) {
			super.setWidth(width);
		}
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		for (int i = 0; i < _tableRows.length; i++) {
			_tableRows[i].setHeight((int) height);
		}
	}

	public boolean isGrid() {
		return _grid;
	}

	public TableLayout setGrid(boolean grid) {
		this._grid = grid;
		return this;
	}

	@Override
	public String getUIName() {
		return "TableLayout";
	}

	@Override
	public void destory() {

	}
}
