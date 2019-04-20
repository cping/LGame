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

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.component.skin.SkinManager;
import loon.component.skin.TableSkin;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Dimension;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;

/**
 * Loon的表格显示用UI组件,用以显示以及操作表格数据
 * 
 * Example:
 * 
 * TArray<ListItem> list=new TArray<ListItem>();
 * 
 * ListItem item=new ListItem(); item.name="test1"; item.list.add("ffffff");
 * item.list.add("gggggggg"); item.list.add("hhhhhhhhh"); list.add(item);
 * 
 * ListItem item2=new ListItem(); item2.name="test2"; item2.list.add("ffffff");
 * item2.list.add("gggggggg"); item2.list.add("hhhhhhhhh"); list.add(item2);
 * LTable table=new LTable(IFont.getDefaultFont(), 60,60, 300, 300);
 * table.setData(list, 100); add(table);
 * 
 */
public class LTable extends LContainer implements FontSet<LTable> {

	private ITableModel model = null;

	private TableColumn[] columns = null;

	private boolean[] selected = null;

	private int selectionCount = 0;

	private int columnMinWidth = 15;

	private boolean multipleSelection = false;

	private boolean readOnly = false;

	private HeaderControl header = new HeaderControl();

	private ArrayMap bindIcons = new ArrayMap();

	protected static class BindIcon {

		protected String name = "...";

		protected LTexture texture;

		BindIcon(String n, LTexture t) {
			this.name = n;
			this.texture = t;
		}
	}

	static class HeaderControl {
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

	private LColor headerBackgroundColor = LColor.gray.cpy();

	private LColor gridColor = LColor.gray.cpy();

	private LColor textColor = LColor.white.cpy();

	private LColor selectionColor = LColor.red.darker().cpy();

	private LColor headTextColor = LColor.orange.cpy();

	private IFont font;

	private LTexture headerTexture;

	private LTexture backgroundTexture;

	public LTable(int x, int y) {
		this(SkinManager.get().getTableSkin().getFont(), x, y, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public LTable(int x, int y, int width, int height) {
		this(SkinManager.get().getTableSkin().getFont(), SkinManager.get().getTableSkin().getHeaderTexture(),
				SkinManager.get().getTableSkin().getBackgroundTexture(), x, y, width, height);
	}

	public LTable(IFont font, int x, int y, int width, int height) {
		this(font, SkinManager.get().getTableSkin().getHeaderTexture(),
				SkinManager.get().getTableSkin().getBackgroundTexture(), x, y, width, height);
	}

	public LTable(LTexture headerTexture, LTexture backgroundTexture, int x, int y, int width, int height) {
		this(SkinManager.get().getTableSkin().getFont(), headerTexture, backgroundTexture, x, y, width, height);
	}

	public LTable(IFont font, LTexture headerTexture, LTexture backgroundTexture, int x, int y, int width, int height) {
		this(font, headerTexture, backgroundTexture, x, y, width, height,
				SkinManager.get().getTableSkin().getFontColor());
	}

	public LTable(TableSkin skin, int x, int y, int width, int height) {
		this(skin.getFont(), skin.getHeaderTexture(), skin.getBackgroundTexture(), x, y, width, height,
				skin.getFontColor());
	}

	public LTable(IFont font, LTexture headerTexture, LTexture backgroundTexture, int x, int y, int width, int height,
			LColor fontColor) {
		super(x, y, width, height);
		this.font = font;
		this.cellHeight = (int) (font.getHeight() + font.getAscent());
		this.headerTexture = headerTexture;
		this.backgroundTexture = backgroundTexture;
		this.setElastic(false);
		this.setLocked(true);
	}

	public LTable setData(TArray<ListItem> list, int width) {
		setModel(new SimpleTableModel(list), width);
		return this;
	}

	public LTable bindIcon(String name, LTexture texture) {
		bindIcons.put(name, new BindIcon(name, texture));
		return this;
	}

	public LTable bindIcon(String name, String fileName) {
		bindIcons.put(name, new BindIcon(name, LSystem.loadTexture(fileName)));
		return this;
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

	public LTable removeIcon(String name) {
		bindIcons.remove(name);
		return this;
	}

	public LTable removeIcon(int idx) {
		bindIcons.remove(idx);
		return this;
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
		return new Dimension(100, numberOfRows * rowHeight + numberOfRows * cellSpacing);
	}

	public LTable mouseDragged(float x, float y) {
		if (isTableHeadVisible()) {
			if (header.columnResizeIndex > -1) {
				int newWidth = (int) (header.columnWidthBuffer + (x - header.mouseX));
				int sum = getColumnWidth(header.columnResizeIndex) + getColumnWidth(header.columnResizeIndex + 1);
				if (newWidth < getColumnMinWidth() || sum - newWidth < getColumnMinWidth()) {
					return this;
				}
				columns[header.columnResizeIndex].setWidth(newWidth);
				columns[header.columnResizeIndex + 1].setWidth(sum - newWidth);
			}
		}
		return this;
	}

	public LTable mouseReleased(float x, float y) {
		if (header.columnResizeIndex > -1) {
			header.columnResizeIndex = -1;
		}
		return this;
	}

	public LTable mouseMoved(float x, float y) {
		if (!isTableHeadVisible()) {
			return this;
		}
		if (header.headerY < (y + getCellHeight() + getCellHeight())) {
			int column = isOnColumn((int) x);
			if (column >= 0) {
				header.columnResizeIndex = column;
				return this;
			} else if (header.columnResizeIndex > -1) {
				header.columnResizeIndex = -1;
			}
		} else if (header.columnResizeIndex > -1) {
			header.columnResizeIndex = -1;
		}
		return this;
	}

	public LTable mousePressed(float x, float y) {
		if (isTableHeadVisible()) {
			if (header.columnResizeIndex > -1) {
				header.mouseX = (int) x;
				header.columnWidthBuffer = getColumnWidth(header.columnResizeIndex);
				return this;
			} else {
				header.columnResizeIndex = 0;
			}
		}

		if (readOnly) {
			return this;
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
			return this;
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
		return this;
	}

	@Override
	protected void processTouchDragged() {
		mouseDragged(getUITouchX(), getUITouchY());
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			if (this.input != null) {
				this.move(this.input.getTouchDX(), this.input.getTouchDY());
			}
		}
		super.dragClick();
	}

	@Override
	protected void processTouchPressed() {
		super.processTouchPressed();
		try {
			mousePressed(getUITouchX(), getUITouchY());
		} catch (Throwable t) {
			LSystem.error("LTable mousePressed() exception", t);
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		try {
			mouseReleased(getUITouchX(), getUITouchY());
		} catch (Throwable t) {
			LSystem.error("LTable mouseReleased() exception", t);
		}
	}

	@Override
	public float getHeight() {
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

	@Override
	public float getWidth() {
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
	public void createUI(GLEx g, int displayX, int displayY, LComponent component, LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		ITableModel model = getModel();
		HeaderControl header = getHeader();
		if (model == null) {
			return;
		}
		try {
			g.saveBrush();
			int x = displayX;
			int y = displayY;
			y += cellHeight;
			int size = (int) (getHeight() / (cellHeight + cellSpacing));
			int wid = 0;
			for (int i = 0; i < model.getColumnCount(); i++) {
				wid += getColumnWidth(i);
			}
			int hei = 0;
			for (int i = 0; i < model.getRowCount(); i++) {
				hei += (cellHeight + cellSpacing);
			}
			if (wid != getWidth() || hei + (cellHeight + cellSpacing) != getHeight()) {
				setSize(wid, hei + (cellHeight + cellSpacing));
			}
			if (gridVisible) {
				g.setLineWidth(2f);
			}
			if (backgroundTexture != null) {
				g.draw(backgroundTexture, x, y, wid, hei, LColor.white);
			}
			

			boolean useLFont = (font instanceof LFont);
			boolean supportPack = false;

			if (useLFont) {
				LFont newFont = (LFont) font;
				supportPack = newFont.isSupportCacheFontPack();
				newFont.setSupportCacheFontPack(false);
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
						ICellRenderer cellRenderer = getColumn(columnIndex).getCellRenderer();
						Dimension contentDimension = cellRenderer.getCellContentSize(value);
						if (contentDimension == null) {
							contentDimension = new Dimension(getColumnWidth(columnIndex), cellHeight);
						}
						int alignedX = x + getColumn(columnIndex).getEntryAlignment()
								.alignX(getColumnWidth(columnIndex), contentDimension.getWidth());
						int alignedY = y + getColumn(columnIndex).getEntryAlignment().alignY(cellHeight,
								contentDimension.getHeight());

						if (bindIcons.size() == 0) {
							cellRenderer.paint(g, value, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
						} else {
							if (value instanceof String) {
								String v = (String) value;
								BindIcon icon = containsBindIcon(v);
								if (icon != null) {
									cellRenderer.paint(g, icon, alignedX, alignedY, getColumnWidth(columnIndex),
											cellHeight);
								} else {
									cellRenderer.paint(g, value, alignedX, alignedY, getColumnWidth(columnIndex),
											cellHeight);
								}
							} else {
								cellRenderer.paint(g, value, alignedX, alignedY, getColumnWidth(columnIndex),
										cellHeight);
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
					g.draw(headerTexture, displayX, displayY, wid, cellHeight, headerBackgroundColor);
					if (gridVisible) {
						g.setColor(gridColor);
						g.drawRect(displayX, displayY, wid, cellHeight);
						g.setColor(LColor.white);
					}
				} else {
					g.setColor(headerBackgroundColor);
					g.fillRect(displayX, displayY, wid, cellHeight);
					g.setColor(LColor.white);
				}
				x = displayX;

				for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
					String s = model.getColumnName(columnIndex);
					int columnWidth = getColumnWidth(columnIndex);
					s = font.confineLength(s, columnWidth - OFFSET);
					int entryOffset = OFFSET + getColumn(columnIndex).getHeaderAlignment().alignX(columnWidth - OFFSET,
							font.stringWidth(s));

					font.drawString(g, s, x + entryOffset, header.headerY + font.getAscent() / 2 - 4, headTextColor);
					x += columnWidth + cellSpacing;
				}
			}

			if (useLFont && supportPack) {
				LFont newFont = (LFont) font;
				newFont.setSupportCacheFontPack(supportPack);
			}
			
		} finally {
			g.restoreBrush();
		}
	}

	public void setGridColor(LColor gridColor) {
		this.gridColor = gridColor;
	}

	public void setTextColor(LColor textColor) {
		this.textColor = textColor;
	}

	@Override
	public LTable setFont(IFont font) {
		this.font = font;
		this.cellHeight = font.getHeight();
		return this;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	public LColor getHeadTextColor() {
		return headTextColor;
	}

	public LTable setHeadTextColor(LColor headTextColor) {
		this.headTextColor = headTextColor;
		return this;
	}

	public LColor getSelectionColor() {
		return selectionColor;
	}

	public LTable setSelectionColor(LColor selectionColor) {
		this.selectionColor = selectionColor;
		return this;
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

	public LTable setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
		return this;
	}

	public LTable setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
		return this;
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

	public LTable setCellSpacing(int cellSpacing) {
		this.cellSpacing = cellSpacing;
		return this;
	}

	public LColor getHeaderBackgroundColor() {
		return headerBackgroundColor;
	}

	public LTable setHeaderBackgroundColor(LColor headerBackgroundColor) {
		this.headerBackgroundColor = headerBackgroundColor;
		return this;
	}

	protected HeaderControl getHeader() {
		return header;
	}

	public LTable setSelected(int index, boolean b) {
		assertModel();
		assertSelectionArraySize();

		if (index < 0 || index >= selected.length) {
			return this;
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
		return this;
	}

	public LTable setModel(ITableModel m, int width) {
		model = m;
		columns = new TableColumn[m.getColumnCount()];
		selected = new boolean[m.getRowCount()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumn(m.getColumnName(i), width);
		}
		return this;
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

	public LTable distributeColumnWidthsEqually() {
		if (model == null) {
			throw new LSysException("The table has no model!");
		}
		for (int i = 0; i < columns.length; i++) {
			columns[i].setWidth((int) (getWidth() / columns.length));
		}
		return this;
	}

	public LTable setColumnWidth(int columnIndex, int widthInPixel) {
		getColumn(columnIndex).setWidth(widthInPixel);
		return this;
	}

	public LTable setColumnWidth(int columnIndex, float relativeWidth) {
		getColumn(columnIndex).setRelativeWidth(relativeWidth);
		return this;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public LTable setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
		return this;
	}

	public LTable clearSelection() {
		for (int i = 0; i < selected.length; i++) {
			selected[i] = false;
		}
		return this;
	}

	public LTable layout() {
		if (model != null && columns.length > 0 && getColumnWidth(0) == -1) {
			distributeColumnWidthsEqually();
		}
		return this;
	}

	public int getSelection() {
		assertModel();

		for (int i = 0; i < selected.length; i++) {
			if (selected[i] == true)
				return i;
		}
		return -1;
	}

	public LTable setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public TableColumn getColumn(int columnIndex) {
		assertModel();

		return columns[columnIndex];
	}

	public int isOnColumn(int x) {
		float sum = 0;
		for (int col = 0; col < columns.length - 1; col++) {
			sum += getColumnWidth(col) + cellSpacing;
			if (MathUtils.abs(sum - x) < 5)
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
			throw new LSysException("No table model set!");
		}
	}

	public int getColumnMinWidth() {
		return columnMinWidth;
	}

	public LTable setColumnMinWidth(int columnMinWidth) {
		this.columnMinWidth = columnMinWidth;
		return this;
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

	public LTable setHeaderTexture(LTexture headerTexture) {
		this.headerTexture = headerTexture;
		return this;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public LTable setBackgroundTexture(LTexture backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
		return this;
	}

	@Override
	public LTable setFontColor(LColor color) {
		this.textColor = color;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return textColor.cpy();
	}

	@Override
	public String getUIName() {
		return "Table";
	}

}
