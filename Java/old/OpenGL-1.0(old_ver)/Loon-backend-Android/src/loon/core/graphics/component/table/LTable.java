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

import loon.LSystem;
import loon.core.geom.Dimension;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.component.DefUI;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.graphics.opengl.LTexture.Format;
import loon.utils.collection.Array;
import loon.utils.collection.ArrayMap;

/*
 * 
 * Example:
 * 
 * Array<ListItem> list=new Array<ListItem>();
 * 
 * ListItem item=new ListItem(); 
 * item.name="test1"; 
 * item.list.add("ffffff");
 * item.list.add("gggggggg"); 
 * item.list.add("hhhhhhhhh"); 
 * list.add(item);
 * 
 * ListItem item2=new ListItem(); 
 * item2.name="test2";
 * item2.list.add("ffffff");
 * item2.list.add("gggggggg");
 * item2.list.add("hhhhhhhhh");
 * list.add(item2);
 * LTable table=new LTable(LFont.getDefaultFont(), 60,60, 300, 300);
 * table.setData(list, 100); 
 * add(table);
 * 
 */
public class LTable extends LContainer {

	private ITableModel model = null;

	private TableColumn[] columns = null;

	private boolean[] selected = null;

	private int selectionCount = 0;

	private int columnMinWidth = 15;

	private boolean multipleSelection = false;

	private boolean readOnly = false;

	private HeaderControl header = new HeaderControl();

	private ArrayMap bindIcons = new ArrayMap();

	protected class BindIcon {

		protected String name = "...";

		protected LTexture texture;

		BindIcon(String n, LTexture t) {
			this.name = n;
			this.texture = t;
		}
	}

	class HeaderControl {
		int headerY;
		int mouseX = 0;
		int columnResizeIndex = 0;
		int columnWidthBuffer = 0;
	}

	private int cellHeight = 20;

	private static final int OFFSET = 5;

	private int cellSpacing = 0;

	private boolean gridVisible = true;

	private boolean tableHeaderVisible = true;

	private LColor headerBackgroundColor = LColor.gray;

	private LColor gridColor = LColor.gray;

	private LColor textColor = LColor.white;

	private LColor selectionColor = LColor.red.darker();

	private LColor headTextColor = LColor.orange;

	private LFont font;

	private LTexture headerTexture;

	private LTexture backgroundTexture;

	public LTable(int x, int y) {
		this(LFont.getDefaultFont(), DefUI.getDefaultTextures(7), DefUI
				.getDefaultTextures(4), x, y, LSystem.screenRect.width,
				LSystem.screenRect.height);
	}

	public LTable(int x, int y, int width, int height) {
		this(LFont.getDefaultFont(), DefUI.getDefaultTextures(7), DefUI
				.getDefaultTextures(4), x, y, width, height);
	}

	public LTable(LFont font, int x, int y, int width, int height) {
		this(font, DefUI.getDefaultTextures(7), DefUI.getDefaultTextures(4), x,
				y, width, height);
	}

	public LTable(LTexture headerTexture, LTexture backgroundTexture, int x,
			int y, int width, int height) {
		this(LFont.getDefaultFont(), headerTexture, backgroundTexture, x, y,
				width, height);
	}

	public LTable(LFont font, LTexture headerTexture,
			LTexture backgroundTexture, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.font = font;
		this.cellHeight = font.getHeight() + 2;
		this.headerTexture = headerTexture;
		this.backgroundTexture = backgroundTexture;
		this.setElastic(false);
		this.setLocked(true);
		this.setLayer(0);
	}

	public void setData(Array<ListItem> list, int width) {
		setModel(new SimpleTableModel(list), width);
	}

	public void bindIcon(String name, LTexture texture) {
		bindIcons.put(name, new BindIcon(name, texture));
	}

	public void bindIcon(String name, String fileName) {
		bindIcons.put(
				name,
				new BindIcon(name, LTextures.loadTexture(fileName,
						Format.LINEAR)));
	}

	private BindIcon containsBindIcon(String name) {
		for (int i = 0; i < bindIcons.size(); i++) {
			BindIcon icon = (BindIcon) bindIcons.get(i);
			if (name.equalsIgnoreCase(icon.name)) {
				return icon;
			}
		}
		return null;
	}

	public void removeIcon(String name) {
		bindIcons.remove(name);
	}

	public void removeIcon(int idx) {
		bindIcons.remove(idx);
	}

	public Dimension getContentMinSizeHint() {
		int rowHeight = font.getHeight();
		ITableModel model = getModel();
		if (model == null) {
			return new Dimension(0, 0);
		}
		int numberOfRows = model.getRowCount();
		if (tableHeaderVisible) {
			numberOfRows++;
		}
		return new Dimension(100, numberOfRows * rowHeight + numberOfRows
				* cellSpacing);
	}

	public void mouseDragged(float x, float y) {
		if (isTableHeadVisible()) {
			if (header.columnResizeIndex > -1) {
				int newWidth = (int) (header.columnWidthBuffer + (x - header.mouseX));
				int sum = getColumnWidth(header.columnResizeIndex)
						+ getColumnWidth(header.columnResizeIndex + 1);
				if (newWidth < getColumnMinWidth()
						|| sum - newWidth < getColumnMinWidth()) {
					return;
				}
				columns[header.columnResizeIndex].setWidth(newWidth);
				columns[header.columnResizeIndex + 1].setWidth(sum - newWidth);
			}
		}
	}

	public void mouseExited(float x, float y) {
		if (header.columnResizeIndex > -1) {
			header.columnResizeIndex = -1;
		}
	}

	public void mouseMoved(float x, float y) {
		if (!isTableHeadVisible()) {
			return;
		}
		if (header.headerY < (y + getCellHeight() + getCellHeight())) {
			int column = isOnColumn((int) x);
			if (column >= 0) {
				header.columnResizeIndex = column;
				return;
			} else if (header.columnResizeIndex > -1) {
				header.columnResizeIndex = -1;
			}
		} else if (header.columnResizeIndex > -1) {
			header.columnResizeIndex = -1;
		}
	}

	public void mousePressed(float x, float y) {
		if (isTableHeadVisible()) {
			if (header.columnResizeIndex > -1) {
				header.mouseX = (int) x;
				header.columnWidthBuffer = getColumnWidth(header.columnResizeIndex);
				return;
			} else {
				header.columnResizeIndex = 0;
			}
		}

		if (readOnly) {
			return;
		}

		assertSelectionArraySize();

		int mouseY = (int) y;
		if (!isTableHeadVisible()) {
			mouseY -= getCellHeight();
		}

		mouseY += getCellSpacing();

		int row = (mouseY / (getCellHeight() + getCellSpacing()));

		if (isTableHeadVisible()) {
			row--;
		}

		if (row < 0 || row >= selected.length) {
			return;
		}

		if (!selected[row]) {
			selectionCount++;
		} else {
			selectionCount--;
		}

		if (multipleSelection) {
			selected[row] = !selected[row];
		} else {
			clearSelection();
			selected[row] = !selected[row];
		}
	}

	protected void processTouchDragged() {
		super.processTouchDragged();
		mouseDragged(getTouchX(), getTouchY());
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			if (this.input != null) {
				this.move(this.input.getTouchDX(), this.input.getTouchDY());
			}
		}
	}

	protected void processTouchPressed() {
		super.processTouchPressed();
		mousePressed(getTouchX(), getTouchY());
	}

	protected void processTouchReleased() {
		super.processTouchReleased();
		mouseExited(getTouchX(), getTouchY());
	}

	public int getHeight() {
		if (model == null) {
			return super.getHeight();
		}
		int height = 0;
		for (int i = 0; i < model.getRowCount(); i++) {
			height += (cellHeight + cellSpacing);
		}
		if (isTableHeadVisible()) {
			height += (cellHeight + cellSpacing);
		}
		return height;
	}

	public int getWidth() {
		if (model == null) {
			return super.getWidth();
		}
		int width = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			width += getColumnWidth(i);
		}
		return width;
	}

	@Override
	public void createUI(GLEx g, int displayX, int displayY,
			LComponent component, LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		ITableModel model = getModel();
		HeaderControl header = getHeader();
		if (model == null) {
			return;
		}

		int x = displayX;
		int y = displayY;

		y += cellHeight;

		int size = getHeight() / (cellHeight + cellSpacing);

		int wid = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			wid += getColumnWidth(i);
		}
		int hei = 0;
		for (int i = 0; i < model.getRowCount(); i++) {
			hei += (cellHeight + cellSpacing);
		}

		if (wid != getWidth()
				|| hei + (cellHeight + cellSpacing) != getHeight()) {
			setSize(wid, hei + (cellHeight + cellSpacing));
		}

		if (gridVisible) {
			g.setLineWidth(2f);
		}
		if (backgroundTexture != null) {
			g.drawTexture(backgroundTexture, x, y, wid, hei, LColor.white);
		}
		for (int row = 0; row < size && row < model.getRowCount(); row++) {
			x = displayX;
			if (isSelected(row)) {
				g.setColor(selectionColor);
				g.fillRect(x, y, wid, cellHeight);
				g.setColor(LColor.white);
			}
			for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {

				g.setColor(textColor);
				Object value = model.getValue(row, columnIndex);

				if (value != null) {
					ICellRenderer cellRenderer = getColumn(columnIndex)
							.getCellRenderer();
					Dimension contentDimension = cellRenderer
							.getCellContentSize(value);
					if (contentDimension == null) {
						contentDimension = new Dimension(
								getColumnWidth(columnIndex), cellHeight);
					}
					int alignedX = x
							+ getColumn(columnIndex).getEntryAlignment()
									.alignX(getColumnWidth(columnIndex),
											contentDimension.getWidth());
					int alignedY = y
							+ getColumn(columnIndex).getEntryAlignment()
									.alignY(cellHeight,
											contentDimension.getHeight());

					if (bindIcons.size() == 0) {
						cellRenderer.paint(g, value, alignedX, alignedY,
								getColumnWidth(columnIndex), cellHeight);
					} else {
						if (value instanceof String) {
							String v = (String) value;
							BindIcon icon = containsBindIcon(v);
							if (icon != null) {
								cellRenderer
										.paint(g, icon, alignedX, alignedY,
												getColumnWidth(columnIndex),
												cellHeight);
							} else {
								cellRenderer.paint(g, value, alignedX,
										alignedY, getColumnWidth(columnIndex),
										cellHeight);
							}
						} else {
							cellRenderer.paint(g, value, alignedX, alignedY,
									getColumnWidth(columnIndex), cellHeight);
						}
					}
				}

				if (gridVisible) {
					g.setColor(gridColor);
					g.drawRect(x, y, getColumnWidth(columnIndex), cellHeight);
					g.setColor(LColor.white);
				}

				x += getColumnWidth(columnIndex) + cellSpacing;
			}
			y += (cellHeight + cellSpacing);
		}
		if (tableHeaderVisible) {
			header.headerY = displayY;

			if (headerTexture != null) {
				g.drawTexture(headerTexture, displayX, displayY, wid,
						font.getHeight(), headerBackgroundColor);
				if (gridVisible) {
					g.setColor(gridColor);
					g.drawRect(displayX, displayY, wid, font.getHeight());
					g.setColor(LColor.white);
				}
			} else {
				g.setColor(headerBackgroundColor);
				g.fillRect(displayX, displayY, wid, font.getHeight());
				g.setColor(LColor.white);
			}
			x = displayX;

			for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
				String s = model.getColumnName(columnIndex);
				int columnWidth = getColumnWidth(columnIndex);
				s = font.confineLength(s, columnWidth - OFFSET);
				int entryOffset = OFFSET
						+ getColumn(columnIndex).getHeaderAlignment().alignX(
								columnWidth - OFFSET, font.stringWidth(s));
				g.setFont(font);
				g.drawString(s, x + entryOffset,
						header.headerY + font.getHeight() - 4, headTextColor);
				x += columnWidth + cellSpacing;
			}
		}
		if (gridVisible) {
			g.resetLineWidth();
		}
		g.resetColor();
		g.resetFont();
	}

	public void setGridColor(LColor gridColor) {
		this.gridColor = gridColor;
	}

	public void setTextColor(LColor textColor) {
		this.textColor = textColor;
	}

	public void setFont(LFont font) {
		this.font = font;
		this.cellHeight = font.getHeight();
	}

	public LFont getFont() {
		return font;
	}

	public LColor getHeadTextColor() {
		return headTextColor;
	}

	public void setHeadTextColor(LColor headTextColor) {
		this.headTextColor = headTextColor;
	}

	public LColor getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(LColor selectionColor) {
		this.selectionColor = selectionColor;
	}

	public LColor getGridColor() {
		return gridColor;
	}

	public LColor getTextColor() {
		return textColor;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	public boolean isTableHeadVisible() {
		return tableHeaderVisible;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setHeaderVisible(boolean drawTableHead) {
		this.tableHeaderVisible = drawTableHead;
	}

	public int getCellSpacing() {
		return cellSpacing;
	}

	public void setCellSpacing(int cellSpacing) {
		this.cellSpacing = cellSpacing;
	}

	public LColor getHeaderBackgroundColor() {
		return headerBackgroundColor;
	}

	public void setHeaderBackgroundColor(LColor headerBackgroundColor) {
		this.headerBackgroundColor = headerBackgroundColor;
	}

	protected HeaderControl getHeader() {
		return header;
	}

	public void setSelected(int index, boolean b) {
		assertModel();
		assertSelectionArraySize();

		if (index < 0 || index >= selected.length) {
			return;
		}

		if (multipleSelection) {
			if (selected[index] != b) {
				selected[index] = b;
				if (b) {
					selectionCount++;
				} else {
					selectionCount--;
				}
			}
		} else {
			clearSelection();
			selected[index] = b;
			selectionCount = 1;
		}
	}

	public void setModel(ITableModel m, int width) {
		model = m;
		columns = new TableColumn[m.getColumnCount()];
		selected = new boolean[m.getRowCount()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumn(m.getColumnName(i), width);
		}
	}

	public ITableModel getModel() {
		return model;
	}

	public boolean isSelected(int row) {
		assertModel();
		return row >= 0 && row < selected.length ? selected[row] : false;
	}

	public int getSelectionCount() {
		return selectionCount;
	}

	public void distributeColumnWidthsEqually() {
		if (model == null) {
			throw new IllegalStateException("The table has no model!");
		}
		for (int i = 0; i < columns.length; i++) {
			columns[i].setWidth(getWidth() / columns.length);
		}
	}

	public void setColumnWidth(int columnIndex, int widthInPixel) {
		getColumn(columnIndex).setWidth(widthInPixel);
	}

	public void setColumnWidth(int columnIndex, float relativeWidth) {
		getColumn(columnIndex).setRelativeWidth(relativeWidth);
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public void clearSelection() {
		for (int i = 0; i < selected.length; i++) {
			selected[i] = false;
		}
	}

	public void layout() {
		if (model != null && columns.length > 0 && getColumnWidth(0) == -1) {
			distributeColumnWidthsEqually();
		}
	}

	public int getSelection() {
		assertModel();

		for (int i = 0; i < selected.length; i++) {
			if (selected[i] == true)
				return i;
		}
		return -1;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public TableColumn getColumn(int columnIndex) {
		assertModel();

		return columns[columnIndex];
	}

	public int isOnColumn(int x) {
		double sum = 0;
		for (int col = 0; col < columns.length - 1; col++) {
			sum += getColumnWidth(col) + cellSpacing;
			if (Math.abs(sum - x) < 5)
				return col;
		}

		return -1;
	}

	private void assertSelectionArraySize() {
		if (selected.length == model.getRowCount()) {
			return;
		}

		boolean[] newSelected = new boolean[model.getRowCount()];

		for (int i = 0; i < selected.length && i < newSelected.length; i++) {
			newSelected[i] = selected[i];
		}

		this.selected = newSelected;
	}

	private void assertModel() {
		if (model == null) {
			throw new IllegalStateException("No table model set!");
		}
	}

	public int getColumnMinWidth() {
		return columnMinWidth;
	}

	public void setColumnMinWidth(int columnMinWidth) {
		this.columnMinWidth = columnMinWidth;
	}

	public int getColumnWidth(int columnIndex) {
		TableColumn column = columns[columnIndex];
		if (column != null) {
			if (column.isRelative()) {
				return (int) (getWidth() * column.getRelativeWidth());
			}
			return column.getWidth();
		}
		return 0;
	}

	public LTexture getHeaderTexture() {
		return headerTexture;
	}

	public void setHeaderTexture(LTexture headerTexture) {
		this.headerTexture = headerTexture;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public String getUIName() {
		return "Table";
	}

}
