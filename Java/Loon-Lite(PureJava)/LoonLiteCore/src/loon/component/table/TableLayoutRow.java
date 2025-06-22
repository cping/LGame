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

import loon.component.LComponent;
import loon.opengl.GLEx;
import loon.utils.TArray;

public class TableLayoutRow {

	private TArray<TableColumnLayout> _tempColumns;

	private int _x;

	private int _y;

	private int _width;

	private int _height;

	private TableColumnLayout[] columns;

	public TableLayoutRow(int x, int y, int width, int height) {
		this._x = x;
		this._y = y;
		this._width = width;
		this._height = height;
	}

	public TableLayoutRow(int x, int y, int width, int height, int columns) {
		this(x, y, width, height);
		this.columns = new TableColumnLayout[columns];
		initColumns();
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		this._x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		this._y = y;
	}

	public int getWidth() {
		return _width;
	}

	public boolean setWidth(int width) {
		if (width > getWidth()) {
			float newWidthDif = (float) (width - getWidth()) / columns.length;
			for (int i = 0; i < columns.length; i++) {
				columns[i].setWidth(columns[i].getWidthf() + newWidthDif);
			}
			adjustColumns();
			this._width = width;
			return true;
		}
		int difX = getWidth() - width;
		if (getMaxDifferenceX() < difX) {
			return false;
		}
		for (int i = columns.length - 1; i >= 0; i++) {
			if (columns[i].getWidth() > columns[i].getMinWidth()) {
				int maxDif = columns[i].getWidth() - columns[i].getMinWidth();
				if (maxDif >= difX) {
					columns[i].setWidth(columns[i].getWidth() - difX);
					this._width = width;
					adjustColumns();
					return true;
				} else {
					columns[i].setWidth(columns[i].getWidth() - maxDif);
					difX -= maxDif;
				}
			}
		}
		return false;
	}

	private int getMaxDifferenceX() {
		int dif = 0;
		for (int i = 0; i < columns.length; i++) {
			dif += columns[i].getWidth() - columns[i].getMinWidth();
		}
		return dif;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
		adjustColumns();
	}

	public boolean canSetHeight(int height) {
		return (height > getHeight() || height > columns[0].getMinWidth());
	}

	public void setColumns(int columns) {
		this.columns = new TableColumnLayout[columns];
		initColumns();
	}

	public int getCoulumnSize() {
		return columns.length;
	}

	public TableLayoutRow setSize(int x, int y, int w, int h) {
		setX(x);
		setY(y);
		setWidth(h);
		setHeight(h);
		return this;
	}

	private void initColumns() {
		int xStep = getWidth() / columns.length;
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumnLayout(null, getX() + (i * xStep), _y, xStep, _height);
		}
	}

	private void adjustColumns() {
		int startX = this._x;
		for (int i = 0; i < columns.length; i++) {
			columns[i].setX(startX);
			startX += columns[i].getWidth();
			columns[i].setY(getY());
			columns[i].setHeight(getHeight());
			columns[i].adjustComponent();
		}
	}

	public void setComponent(LComponent component, int column) {
		columns[column < columns.length - 1 ? column : columns.length - 1].setComponent(component);
	}

	public LComponent getComponent(int column) {
		return columns[column < columns.length - 1 ? column : columns.length - 1].getComponent();
	}

	public TableColumnLayout getColumn(int column) {
		return columns[column < columns.length - 1 ? column : columns.length - 1];
	}

	public boolean setColumnWidth(int width, int column) {
		if (width > columns[column].getWidth()) {
			int difX = width - columns[column].getWidth();
			int maxDif = getMaxDifferenceX() - (columns[column].getWidth() - columns[column].getMinWidth());
			if (maxDif >= difX) {
				for (int i = 0; i < columns.length; i++) {
					if (i != column) {
						int maxColumnDif = columns[i].getWidth() - columns[i].getMinWidth();
						if (maxColumnDif >= difX) {
							columns[i].setWidth(columns[i].getWidth() - difX);
							break;
						} else {
							columns[i].setWidth(columns[i].getWidth() - maxColumnDif);
							difX -= maxColumnDif;
						}
					}
				}
				columns[column].setWidth(width);
				adjustColumns();
				return true;
			}
			return false;
		}
		if (width < columns[column].getMinWidth()) {
			width = columns[column].getMinWidth();
		}
		int difXColumns = (columns[column].getWidth() - width) / (columns.length - 1);
		for (int i = 0; i < columns.length; i++) {
			if (i != column) {
				columns[i].setWidth(columns[i].getWidth() + difXColumns);
			}
		}
		columns[column].setWidth(width);
		adjustColumns();
		return true;
	}

	public void removeColumn(int column) {
		int columnAdd = columns[column].getWidth() / (columns.length - 1);
		if (_tempColumns == null) {
			_tempColumns = new TArray<TableColumnLayout>();
		} else {
			_tempColumns.clear();
		}
		for (int i = 0; i < columns.length; i++) {
			if (i != column) {
				columns[i].setWidth(columns[i].getWidth() + columnAdd);
				_tempColumns.add(columns[i]);
			}
		}
		if (columns.length != _tempColumns.size) {
			columns = (TableColumnLayout[]) _tempColumns.toArray();
		} else {
			for (int i = 0; i < _tempColumns.size; i++) {
				columns[i] = _tempColumns.get(i);
			}
		}
		adjustColumns();
	}

	public void paint(GLEx g) {
		for (int i = 0; i < columns.length; i++) {
			columns[i].paint(g);
		}
	}

}
