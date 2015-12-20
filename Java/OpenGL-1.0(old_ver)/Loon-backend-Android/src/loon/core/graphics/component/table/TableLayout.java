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
import loon.core.graphics.LContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.collection.ArrayList;

public class TableLayout extends LContainer {

	private TableLayoutRow[] tableRows;

	private boolean grid = true;

	public TableLayout(int x, int y, int w, int h) {
		this(x, y, w, h, 4, 4);
	}

	public TableLayout(int x, int y, int w, int h, int cols, int rows) {
		super(x, y, w, h);
		prepareTable(cols, rows);
	}

	protected void renderComponents(GLEx g) {
		for (int i = 0; i < getComponentCount(); i++) {
			getComponents()[i].createUI(g);
		}
		if (grid) {
			for (int i = 0; i < tableRows.length; i++) {
				tableRows[i].paint(g);
			}
			g.drawRect(getX(), getY(), getWidth(), getHeight(), LColor.gray);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	private void prepareTable(int cols, int rows) {
		tableRows = new TableLayoutRow[rows];
		if (rows > 0 && cols > 0) {
			int rowHeight = getHeight() / rows;
			for (int i = 0; i < rows; i++) {
				tableRows[i] = new TableLayoutRow(x(), y() + (i * rowHeight),
						getWidth(), rowHeight, cols);
			}
		}
	}

	public void setComponent(LComponent component, int col, int row) {
		add(component);
		remove(tableRows[row].getComponent(col));
		tableRows[row].setComponent(component, col);
	}

	public void removeComponent(int col, int row) {
		remove(tableRows[row].getComponent(col));
		tableRows[row].setComponent(null, col);
	}

	public void addRow(int column, int position) {
		ArrayList newRows = new ArrayList();
		int newRowHeight = getHeight() / (tableRows.length + 1);
		if (canAddRow(newRowHeight)) {
			if (position == 0) {
				newRows.add(new TableLayoutRow(x(), y(), getWidth(),
						newRowHeight, column));
			}
			for (int i = 0; i < tableRows.length; i++) {
				if (i == position && position != 0) {
					newRows.add(new TableLayoutRow(x(), y(), getWidth(),
							newRowHeight, column));
				}
				newRows.add(tableRows[i]);
			}
			if (position == tableRows.length && position != 0) {
				newRows.add(new TableLayoutRow(x(), y(), getWidth(),
						newRowHeight, column));
			}
			for (int i = 0; i < newRows.size(); i++) {
				((TableLayoutRow) newRows.get(i))
						.setY(y() + (i * newRowHeight));
				((TableLayoutRow) newRows.get(i)).setHeight(newRowHeight);
			}
			tableRows = (TableLayoutRow[]) newRows.toArray();
		}
	}

	public void addRow(int column) {
		addRow(column, tableRows.length);
	}

	private boolean canAddRow(int newRowHeight) {
		if (tableRows != null && tableRows.length > 0) {
			return tableRows[0].canSetHeight(newRowHeight);
		}
		return true;
	}

	public boolean setColumnWidth(int width, int col, int row) {
		return tableRows[row].setColumnWidth(width, col);
	}

	public boolean setColumnHeight(int height, int row) {
		if (!tableRows[row].canSetHeight(height)) {
			return false;
		}
		tableRows[row].setHeight(height);
		return true;
	}

	public void setMargin(int leftMargin, int rightMargin, int topMargin,
			int bottomMargin, int col, int row) {
		tableRows[row].getColumn(col).setMargin(leftMargin, rightMargin,
				topMargin, bottomMargin);
	}

	public void setAlignment(int horizontalAlignment, int verticalAlignment,
			int col, int row) {
		tableRows[row].getColumn(col).setHorizontalAlignment(
				horizontalAlignment);
		tableRows[row].getColumn(col).setVerticalAlignment(verticalAlignment);
	}

	public int getRows() {
		return tableRows.length;
	}

	public int getColumns(int row) {
		return tableRows[row].getCoulumnSize();
	}

	@Override
	public void setWidth(int width) {
		boolean couldShrink = true;
		for (int i = 0; i < tableRows.length; i++) {
			if (!tableRows[i].setWidth(width)) {
				couldShrink = false;
			}
		}
		if (couldShrink) {
			super.setWidth(width);
		}
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		for (int i = 0; i < tableRows.length; i++) {
			tableRows[i].setHeight(height);
		}
	}

	public boolean isGrid() {
		return grid;
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
	}

	@Override
	public String getUIName() {
		return "TableLayout";
	}
}
