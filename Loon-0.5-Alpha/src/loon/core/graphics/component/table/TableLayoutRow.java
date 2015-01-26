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
package loon.core.graphics.component.table;

import loon.core.graphics.LComponent;
import loon.core.graphics.opengl.GLEx;
import loon.utils.collection.ArrayList;

public class TableLayoutRow {

	private int x;

	private int y;

	private int width;

	private int height;

	private TableColumnLayout[] columns;

	public TableLayoutRow(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public TableLayoutRow(int x, int y, int width, int height, int columns) {
		this(x, y, width, height);
		this.columns = new TableColumnLayout[columns];
		initColumns();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public boolean setWidth(int width) {
		if (width > getWidth()) {
			double newWidthDif = (width - getWidth()) / (double) columns.length;
			for (int i = 0; i < columns.length; i++) {
				columns[i].setWidth(columns[i].getWidthf() + newWidthDif);
			}
			adjustColumns();
			this.width = width;
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
					this.width = width;
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
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
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

	private void initColumns() {
		int xStep = getWidth() / columns.length;
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumnLayout(null, getX() + (i * xStep), y,
					xStep, height);
		}
	}

	private void adjustColumns() {
		int startX = this.x;
		for (int i = 0; i < columns.length; i++) {
			columns[i].setX(startX);
			startX += columns[i].getWidth();
			columns[i].setY(getY());
			columns[i].setHeight(getHeight());
			columns[i].adjustComponent();
		}
	}

	public void setComponent(LComponent component, int column) {
		columns[column < columns.length - 1 ? column : columns.length - 1]
				.setComponent(component);
	}

	public LComponent getComponent(int column) {
		return columns[column < columns.length - 1 ? column
				: columns.length - 1].getComponent();
	}

	public TableColumnLayout getColumn(int column) {
		return columns[column < columns.length - 1 ? column
				: columns.length - 1];
	}

	public boolean setColumnWidth(int width, int column) {
		if (width > columns[column].getWidth()) {
			int difX = width - columns[column].getWidth();
			int maxDif = getMaxDifferenceX()
					- (columns[column].getWidth() - columns[column]
							.getMinWidth());
			if (maxDif >= difX) {
				for (int i = 0; i < columns.length; i++) {
					if (i != column) {
						int maxColumnDif = columns[i].getWidth()
								- columns[i].getMinWidth();
						if (maxColumnDif >= difX) {
							columns[i].setWidth(columns[i].getWidth() - difX);
							break;
						} else {
							columns[i].setWidth(columns[i].getWidth()
									- maxColumnDif);
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
		int difXColumns = (columns[column].getWidth() - width)
				/ (columns.length - 1);
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
		ArrayList newColmns = new ArrayList();
		for (int i = 0; i < columns.length; i++) {
			if (i != column) {
				columns[i].setWidth(columns[i].getWidth() + columnAdd);
				newColmns.add(columns[i]);
			}
		}
		columns = (TableColumnLayout[])newColmns.toArray();
		adjustColumns();
	}

	public void paint(GLEx g) {
		for (int i = 0; i < columns.length; i++) {
			columns[i].paint(g);
		}
	}

}
