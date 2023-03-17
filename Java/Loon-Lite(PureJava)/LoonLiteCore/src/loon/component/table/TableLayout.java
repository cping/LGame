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

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.opengl.GLEx;
import loon.utils.TArray;

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

	@Override
	protected void renderComponents(GLEx g) {
		for (int i = 0; i < getComponentCount(); i++) {
			super._childs[i].createUI(g);
		}
		if (grid) {
			int tmp = g.color();
			for (int i = 0; i < tableRows.length; i++) {
				tableRows[i].paint(g);
			}
			g.drawRect(getX(), getY(), getWidth(), getHeight(), LColor.gray);
			g.setColor(tmp);
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {

	}

	private void prepareTable(int cols, int rows) {
		tableRows = new TableLayoutRow[rows];
		if (rows > 0 && cols > 0) {
			int rowHeight = (int) (getHeight() / rows);
			for (int i = 0; i < rows; i++) {
				tableRows[i] = new TableLayoutRow(x(), y() + (i * rowHeight), (int) getWidth(), rowHeight, cols);
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
		TArray<TableLayoutRow> newRows = new TArray<TableLayoutRow>();
		int newRowHeight = (int) (getHeight() / (tableRows.length + 1));
		if (canAddRow(newRowHeight)) {
			if (position == 0) {
				newRows.add(new TableLayoutRow(x(), y(), (int) getWidth(), newRowHeight, column));
			}
			for (int i = 0; i < tableRows.length; i++) {
				if (i == position && position != 0) {
					newRows.add(new TableLayoutRow(x(), y(), (int) getWidth(), newRowHeight, column));
				}
				newRows.add(tableRows[i]);
			}
			if (position == tableRows.length && position != 0) {
				newRows.add(new TableLayoutRow(x(), y(), (int) getWidth(), newRowHeight, column));
			}
			for (int i = 0; i < newRows.size; i++) {
				((TableLayoutRow) newRows.get(i)).setY(y() + (i * newRowHeight));
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

	public void setMargin(int leftMargin, int rightMargin, int topMargin, int bottomMargin, int col, int row) {
		tableRows[row].getColumn(col).setMargin(leftMargin, rightMargin, topMargin, bottomMargin);
	}

	public void setAlignment(int horizontalAlignment, int verticalAlignment, int col, int row) {
		tableRows[row].getColumn(col).setHorizontalAlignment(horizontalAlignment);
		tableRows[row].getColumn(col).setVerticalAlignment(verticalAlignment);
	}

	public int getRows() {
		return tableRows.length;
	}

	public int getColumns(int row) {
		return tableRows[row].getCoulumnSize();
	}

	@Override
	public void setWidth(float width) {
		boolean couldShrink = true;
		for (int i = 0; i < tableRows.length; i++) {
			if (!tableRows[i].setWidth((int) width)) {
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
		for (int i = 0; i < tableRows.length; i++) {
			tableRows[i].setHeight((int) height);
		}
	}

	public boolean isGrid() {
		return grid;
	}

	public TableLayout setGrid(boolean grid) {
		this.grid = grid;
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
