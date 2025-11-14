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
import loon.utils.MathUtils;
import loon.utils.TArray;

public class TableLayoutRow {

	private TArray<TableColumnLayout> _temp_columns;

	private int _x;

	private int _y;

	private int _width;

	private int _height;

	private TableColumnLayout[] _columns;

	public TableLayoutRow(int x, int y, int width, int height) {
		this._x = x;
		this._y = y;
		this._width = width;
		this._height = height;
	}

	public TableLayoutRow(int x, int y, int width, int height, int columns) {
		this(x, y, width, height);
		this._columns = new TableColumnLayout[columns];
		init_columns();
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
			final int newWidthDif = MathUtils.ceil(width - getWidth()) / _columns.length;
			for (int i = 0; i < _columns.length; i++) {
				_columns[i].setWidth(_columns[i].getWidth() + newWidthDif);
			}
			adjust_columns();
			this._width = width;
			return true;
		}
		int difX = getWidth() - width;
		if (getMaxDifferenceX() < difX) {
			return false;
		}
		for (int i = _columns.length - 1; i >= 0; i++) {
			if (_columns[i].getWidth() > _columns[i].getMinWidth()) {
				final int maxDif = _columns[i].getWidth() - _columns[i].getMinWidth();
				if (maxDif >= difX) {
					_columns[i].setWidth(_columns[i].getWidth() - difX);
					this._width = width;
					adjust_columns();
					return true;
				} else {
					_columns[i].setWidth(_columns[i].getWidth() - maxDif);
					difX -= maxDif;
				}
			}
		}
		return false;
	}

	private int getMaxDifferenceX() {
		int dif = 0;
		for (int i = 0; i < _columns.length; i++) {
			dif += _columns[i].getWidth() - _columns[i].getMinWidth();
		}
		return dif;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		this._height = height;
		adjust_columns();
	}

	public boolean canSetHeight(int height) {
		return (height > getHeight() || height > _columns[0].getMinWidth());
	}

	public void set_columns(int columns) {
		this._columns = new TableColumnLayout[columns];
		init_columns();
	}

	public int getCoulumnSize() {
		return _columns.length;
	}

	public TableLayoutRow setSize(int x, int y, int w, int h) {
		setX(x);
		setY(y);
		setWidth(h);
		setHeight(h);
		return this;
	}

	private void init_columns() {
		int xStep = getWidth() / _columns.length;
		for (int i = 0; i < _columns.length; i++) {
			_columns[i] = new TableColumnLayout(null, getX() + (i * xStep), _y, xStep, _height);
		}
	}

	private void adjust_columns() {
		int startX = this._x;
		for (int i = 0; i < _columns.length; i++) {
			_columns[i].setX(startX);
			startX += _columns[i].getWidth();
			_columns[i].setY(getY());
			_columns[i].setHeight(getHeight());
			_columns[i].adjustComponent();
		}
	}

	public void setComponent(LComponent component, int column) {
		_columns[column < _columns.length - 1 ? column : _columns.length - 1].setComponent(component);
	}

	public LComponent getComponent(int column) {
		return _columns[column < _columns.length - 1 ? column : _columns.length - 1].getComponent();
	}

	public TableColumnLayout getColumn(int column) {
		return _columns[column < _columns.length - 1 ? column : _columns.length - 1];
	}

	public boolean setColumnWidth(int width, int column) {
		if (width > _columns[column].getWidth()) {
			int difX = width - _columns[column].getWidth();
			int maxDif = getMaxDifferenceX() - (_columns[column].getWidth() - _columns[column].getMinWidth());
			if (maxDif >= difX) {
				for (int i = 0; i < _columns.length; i++) {
					if (i != column) {
						int maxColumnDif = _columns[i].getWidth() - _columns[i].getMinWidth();
						if (maxColumnDif >= difX) {
							_columns[i].setWidth(_columns[i].getWidth() - difX);
							break;
						} else {
							_columns[i].setWidth(_columns[i].getWidth() - maxColumnDif);
							difX -= maxColumnDif;
						}
					}
				}
				_columns[column].setWidth(width);
				adjust_columns();
				return true;
			}
			return false;
		}
		if (width < _columns[column].getMinWidth()) {
			width = _columns[column].getMinWidth();
		}
		int difX_columns = (_columns[column].getWidth() - width) / (_columns.length - 1);
		for (int i = 0; i < _columns.length; i++) {
			if (i != column) {
				_columns[i].setWidth(_columns[i].getWidth() + difX_columns);
			}
		}
		_columns[column].setWidth(width);
		adjust_columns();
		return true;
	}

	public void removeColumn(int column) {
		int columnAdd = _columns[column].getWidth() / (_columns.length - 1);
		if (_temp_columns == null) {
			_temp_columns = new TArray<TableColumnLayout>();
		} else {
			_temp_columns.clear();
		}
		for (int i = 0; i < _columns.length; i++) {
			if (i != column) {
				_columns[i].setWidth(_columns[i].getWidth() + columnAdd);
				_temp_columns.add(_columns[i]);
			}
		}
		if (_columns.length != _temp_columns.size) {
			_columns = new TableColumnLayout[_temp_columns.size];
		}
		for (int i = 0; i < _temp_columns.size; i++) {
			_columns[i] = _temp_columns.get(i);
		}
		adjust_columns();
	}

	public void paint(GLEx g) {
		for (int i = 0; i < _columns.length; i++) {
			_columns[i].paint(g);
		}
	}

}
