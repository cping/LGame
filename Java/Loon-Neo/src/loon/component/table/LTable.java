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
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.LContainer;
import loon.component.skin.SkinManager;
import loon.component.skin.TableSkin;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Dimension;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
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

	private float _tableLineWidth = 2f;

	private int _tableWidth, _tableHeight, _tableSize;

	private LTexture _cacheFonts;

	private boolean _dragged;

	private boolean _initNativeDraw;

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

	private LColor headerBackgroundColor = LColor.gray.cpy();

	private LColor gridColor = LColor.gray.cpy();

	private LColor textColor = LColor.white.cpy();

	private LColor selectionColor = LColor.red.darker().cpy();

	private LColor headTextColor = LColor.orange.cpy();

	private IFont font;

	private LTexture headerTexture;

	public LTable(int x, int y) {
		this(SkinManager.get().getTableSkin().getFont(), x, y);
	}

	public LTable(IFont font, int x, int y) {
		this(font, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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
		this.setFont(font);
		this.cellHeight = (int) (font.getHeight() + font.getAscent());
		this.headerTexture = headerTexture;
		this.onlyBackground(backgroundTexture);
		this.setElastic(false);
		this.setLocked(true);
	}

	public LTable setData(TArray<ListItem> list, int width) {
		setModel(new DefaultTableModel(list), width);
		return this;
	}

	public LTable setData(TableView view) {
		return setData(view, width() / view.size());
	}

	public LTable setData(TableView view, int width) {
		setModel(new DefaultTableModel(view), width);
		return this;
	}

	public LTable bindIcon(String name, LTexture texture) {
		bindIcons.put(name, new BindIcon(name, texture));
		addFlagDirty();
		return this;
	}

	public LTable bindIcon(String name, String fileName) {
		bindIcons.put(name, new BindIcon(name, LSystem.loadTexture(fileName)));
		addFlagDirty();
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
		addFlagDirty();
		return this;
	}

	public LTable removeIcon(int idx) {
		bindIcons.remove(idx);
		addFlagDirty();
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
		if (isTableHeadVisible() && !readOnly) {
			if (header.columnResizeIndex > -1) {
				int newWidth = (int) (header.columnWidthBuffer + (x - header.mouseX));
				int sum = getColumnWidth(header.columnResizeIndex) + getColumnWidth(header.columnResizeIndex + 1);
				if (newWidth < getColumnMinWidth() || sum - newWidth < getColumnMinWidth()) {
					return this;
				}
				columns[header.columnResizeIndex].setWidth(newWidth);
				columns[header.columnResizeIndex + 1].setWidth(sum - newWidth);
				addFlagDirty();
				_dragged = true;
			}
		}
		return this;
	}

	public LTable mouseReleased(float x, float y) {
		if (header.columnResizeIndex > -1) {
			header.columnResizeIndex = -1;
			_dragged = false;
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
				mouseMoved(x, y);
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
		if (!_dragLocked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			if (this._input != null) {
				this.move(this._input.getTouchDX(), this._input.getTouchDY());
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

	public boolean isDirty() {
		return model == null ? false : model.isDirty();
	}

	@Override
	public void process(long timer) {
		if (model.isDirty()) {
			_tableWidth = _tableHeight = 0;
			for (int i = 0; i < model.getColumnCount(); i++) {
				_tableWidth += getColumnWidth(i);
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				_tableHeight += (cellHeight + cellSpacing);
			}
			if (_tableWidth != getWidth() || _tableHeight + (cellHeight + cellSpacing) != getHeight()) {
				setSize(_tableWidth, _tableHeight + (cellHeight + cellSpacing));
			}
			_tableSize = MathUtils.floor(getHeight() / (cellHeight + cellSpacing));
		}
	}

	protected void createOtherFont(GLEx g, int displayX, int displayY) {
		final int rowCount = model.getRowCount();
		final int colCount = model.getColumnCount();
		int x = displayX;
		int y = displayY;
		y += cellHeight;
		for (int row = 0; row < _tableSize && row < rowCount; row++) {
			x = displayX;
			for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
				Object vl = model.getValue(row, columnIndex);
				if (vl != null) {
					ICellRenderer cellRenderer = getColumn(columnIndex).getCellRenderer();
					Dimension contentDimension = cellRenderer.getCellContentSize(vl);
					if (contentDimension == null) {
						contentDimension = new Dimension(getColumnWidth(columnIndex), cellHeight);
					}
					int alignedX = x + getColumn(columnIndex).getEntryAlignment().alignX(getColumnWidth(columnIndex),
							contentDimension.getWidth());
					int alignedY = y + getColumn(columnIndex).getEntryAlignment().alignY(cellHeight,
							contentDimension.getHeight());

					if (bindIcons.size() == 0) {
						cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
					} else {
						if (vl instanceof String) {
							g.setColor(textColor);
							String v = (String) vl;
							BindIcon icon = containsBindIcon(v);
							if (icon != null) {
								cellRenderer.paint(g, icon, alignedX, alignedY, getColumnWidth(columnIndex),
										cellHeight);
							} else {
								cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
							}
						} else {
							cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
						}
					}
				}
				x += getColumnWidth(columnIndex) + cellSpacing;
			}
			y += (cellHeight + cellSpacing);
		}

	}

	protected void createSystemFont(Canvas g, int displayX, int displayY) {
		int x = displayX;
		int y = displayY;
		g.setColor(LColor.white);
		final int rowCount = model.getRowCount();
		final int colCount = model.getColumnCount();
		for (int row = 0; row < _tableSize && row < rowCount; row++) {
			x = displayX;
			for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
				Object vl = model.getValue(row, columnIndex);
				if (vl != null) {
					ICellRenderer cellRenderer = getColumn(columnIndex).getCellRenderer();
					Dimension contentDimension = cellRenderer.getCellContentSize(vl);
					if (contentDimension == null) {
						contentDimension = new Dimension(getColumnWidth(columnIndex), cellHeight);
					}
					int alignedX = x + getColumn(columnIndex).getEntryAlignment().alignX(getColumnWidth(columnIndex),
							contentDimension.getWidth());
					int alignedY = y + getColumn(columnIndex).getEntryAlignment().alignY(cellHeight,
							contentDimension.getHeight());

					if (bindIcons.size() == 0) {
						cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
					} else {
						if (vl instanceof String) {
							g.setColor(textColor);
							String v = (String) vl;
							BindIcon icon = containsBindIcon(v);
							if (icon != null) {
								cellRenderer.paint(g, icon, alignedX, alignedY, getColumnWidth(columnIndex),
										cellHeight);
							} else {
								cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
							}
						} else {
							cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), cellHeight);
						}
					}
				}
				x += getColumnWidth(columnIndex) + cellSpacing;
			}
			y += (cellHeight + cellSpacing);
		}
	}

	@Override
	public void createUI(GLEx g, int displayX, int displayY) {
		if (!isVisible()) {
			return;
		}
		final ITableModel model = getModel();
		final HeaderControl header = getHeader();
		if (model == null) {
			return;
		}
		if (!_initNativeDraw) {
			if (font instanceof LFont) {
				LSTRDictionary.get().bind((LFont) font, model.message());
			}
			_initNativeDraw = true;
		}
		final int old = g.color();
		try {
			int x = displayX;
			int y = displayY;
			y += cellHeight;
			if (gridVisible) {
				g.setLineWidth(_tableLineWidth);
			}
			if (_background != null) {
				g.draw(_background, x, y, _tableWidth, _tableHeight);
			}
			final int rowCount = model.getRowCount();
			final int colCount = model.getColumnCount();
			for (int row = 0; row < _tableSize && row < rowCount; row++) {
				x = displayX;
				if (isSelected(row)) {
					g.fillRect(x, y, _tableWidth, cellHeight, selectionColor);
				}
				for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
					if (gridVisible) {
						g.drawRect(x, y, getColumnWidth(columnIndex), cellHeight, gridColor);
					}
					x += getColumnWidth(columnIndex) + cellSpacing;
				}
				y += (cellHeight + cellSpacing);
			}
			if (!_dragged && !isClickDrag() && (font instanceof LFont)) {
				if (model.isDirty()) {
					if (_cacheFonts != null) {
						_cacheFonts.cancalSubmit();
						_cacheFonts.close();
						_cacheFonts = null;
					}
					final Image image = Image.createImage(_tableWidth, _tableHeight);
					createSystemFont(image.getCanvas(), 0, 0);
					_cacheFonts = image.onHaveToClose(true).texture();
					model.setDirty(false);
				}
				g.draw(_cacheFonts, displayX, displayY + cellHeight);
			} else {
				createOtherFont(g, displayX, displayY);
			}
			if (tableHeaderVisible) {
				header.headerY = displayY;
				if (headerTexture != null) {
					g.draw(headerTexture, displayX, displayY, _tableWidth, cellHeight, headerBackgroundColor);
					if (gridVisible) {
						g.drawRect(displayX, displayY, _tableWidth, cellHeight, gridColor);
					}
				} else {
					g.fillRect(displayX, displayY, _tableWidth, cellHeight, headerBackgroundColor);
				}
				x = displayX;
				for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
					String s = model.getColumnName(columnIndex);
					int columnWidth = getColumnWidth(columnIndex);
					s = font.confineLength(s, columnWidth - OFFSET);
					int entryOffset = OFFSET + getColumn(columnIndex).getHeaderAlignment().alignX(columnWidth - OFFSET,
							font.stringWidth(s));
					font.drawString(g, s, x + entryOffset, header.headerY + font.getAscent() / 2 - 4, headTextColor);
					x += columnWidth + cellSpacing;
				}
			}
		} finally {
			g.setColor(old);
		}
	}

	public boolean isDragged() {
		return _dragged;
	}

	public void setGridColor(LColor gridColor) {
		this.gridColor = gridColor;
		addFlagDirty();
	}

	public void setTextColor(LColor textColor) {
		this.textColor = textColor;
		addFlagDirty();
	}

	@Override
	public LTable setFont(IFont fn) {
		if (fn == null) {
			return this;
		}
		this.font = fn;
		this.cellHeight = font.getHeight();
		this._initNativeDraw = false;
		this.addFlagDirty();
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
		addFlagDirty();
		return this;
	}

	public LColor getSelectionColor() {
		return selectionColor;
	}

	public LTable setSelectionColor(LColor selectionColor) {
		this.selectionColor = selectionColor;
		addFlagDirty();
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

	private void addFlagDirty() {
		if (model != null) {
			model.setDirty(true);
		}
	}

	public LTable setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
		addFlagDirty();
		return this;
	}

	public LTable setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
		addFlagDirty();
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
		addFlagDirty();
	}

	public int getCellSpacing() {
		return cellSpacing;
	}

	public LTable setCellSpacing(int cellSpacing) {
		this.cellSpacing = cellSpacing;
		addFlagDirty();
		return this;
	}

	public LColor getHeaderBackgroundColor() {
		return headerBackgroundColor;
	}

	public LTable setHeaderBackgroundColor(LColor headerBackgroundColor) {
		this.headerBackgroundColor = headerBackgroundColor;
		addFlagDirty();
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
		this.model = m;
		this.columns = new TableColumn[m.getColumnCount()];
		this.selected = new boolean[m.getRowCount()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumn(m.getColumnName(i), width, this.font);
		}
		addFlagDirty();
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
		addFlagDirty();
		return this;
	}

	public LTable setColumnWidth(int columnIndex, float relativeWidth) {
		getColumn(columnIndex).setRelativeWidth(relativeWidth);
		addFlagDirty();
		return this;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public LTable setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
		addFlagDirty();
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
		int count = model.getRowCount();
		if (selected.length == count) {
			return;
		}
		boolean[] newSelected = new boolean[count];
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
		addFlagDirty();
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
		addFlagDirty();
		return this;
	}

	public LTexture getBackgroundTexture() {
		return _background;
	}

	public LTable setBackgroundTexture(LTexture backgroundTexture) {
		this.onlyBackground(backgroundTexture);
		addFlagDirty();
		return this;
	}

	@Override
	public LTable setFontColor(LColor color) {
		this.textColor = color;
		addFlagDirty();
		return this;
	}

	@Override
	public LColor getFontColor() {
		return textColor.cpy();
	}

	public float getTableLineWidth() {
		return _tableLineWidth;
	}

	public LTable setTableLineWidth(float w) {
		this._tableLineWidth = w;
		return this;
	}

	@Override
	public String getUIName() {
		return "Table";
	}

	@Override
	public void destory() {
		if (_cacheFonts != null) {
			_cacheFonts.close(true);
			_cacheFonts = null;
		}
		if (headerTexture != null) {
			headerTexture.close();
			headerTexture = null;
		}
		bindIcons.clear();
	}

}
