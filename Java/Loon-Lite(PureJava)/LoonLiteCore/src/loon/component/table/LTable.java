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
import loon.utils.TArray;
import loon.utils.ArrayMap;
import loon.utils.CollectionUtils;
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

	private static final int OFFSET = 5;

	private float _tableLineWidth = 2f;

	private float _offsetHeaderX = 0f;

	private float _offsetHeaderY = 0f;

	private int _tableWidth, _tableHeight, _tableSize;

	private int _offsetTableStringX = 0;

	private int _offsetTableStringY = 0;

	private LTexture _cacheFonts;

	private boolean _dragged;

	private ITableModel _model = null;

	private TableColumn[] _columns = null;

	private boolean[] _selected = null;

	private boolean[] _newSelecteds = null;

	private int _selectionCount = 0;

	private int _columnMinWidth = 15;

	private boolean _multipleSelection = false;

	private boolean _readOnly = false;

	private HeaderControl _header = new HeaderControl();

	private ArrayMap _bindIcons = new ArrayMap();

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

	private int _cellHeight = 20;

	private int _cellSpacing = 0;

	private boolean _gridVisible = true;

	private boolean _tableHeaderVisible = true;

	private LColor _headerBackgroundColor = LColor.gray.cpy();

	private LColor _gridColor = LColor.gray.cpy();

	private LColor _textColor = LColor.white.cpy();

	private LColor _selectionColor = LColor.red.darker().cpy();

	private LColor _headTextColor = LColor.orange.cpy();

	private IFont _font;

	private LTexture _headerTexture;

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
		this._cellHeight = (int) (font.getHeight() + font.getAscent());
		this._headerTexture = headerTexture;
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
		_bindIcons.put(name, new BindIcon(name, texture));
		addFlagDirty();
		return this;
	}

	public LTable bindIcon(String name, String fileName) {
		_bindIcons.put(name, new BindIcon(name, LSystem.loadTexture(fileName)));
		addFlagDirty();
		return this;
	}

	private BindIcon containsBindIcon(String name) {
		for (int i = 0; i < _bindIcons.size(); i++) {
			BindIcon icon = (BindIcon) _bindIcons.get(i);
			if (name.equalsIgnoreCase(icon.name)) {
				return icon;
			}
		}
		return null;
	}

	public LTable removeIcon(String name) {
		_bindIcons.remove(name);
		addFlagDirty();
		return this;
	}

	public LTable removeIcon(int idx) {
		_bindIcons.remove(idx);
		addFlagDirty();
		return this;
	}

	public Dimension getContentMinSizeHint() {
		final int rowHeight = _font.getHeight();
		ITableModel model = getModel();
		if (model == null) {
			return new Dimension(0, 0);
		}
		int numberOfRows = model.getRowCount();
		if (_tableHeaderVisible) {
			numberOfRows++;
		}
		return new Dimension(100, numberOfRows * rowHeight + numberOfRows * _cellSpacing);
	}

	public LTable mouseDragged(float x, float y) {
		if (isTableHeadVisible() && !_readOnly) {
			if (_header.columnResizeIndex > -1) {
				final int newWidth = MathUtils.ceil(_header.columnWidthBuffer + (x - _header.mouseX));
				final int sum = getColumnWidth(_header.columnResizeIndex)
						+ getColumnWidth(_header.columnResizeIndex + 1);
				if (newWidth < getColumnMinWidth() || sum - newWidth < getColumnMinWidth()) {
					return this;
				}
				_columns[_header.columnResizeIndex].setWidth(newWidth);
				_columns[_header.columnResizeIndex + 1].setWidth(sum - newWidth);
				addFlagDirty();
				_dragged = true;
			}
		}
		return this;
	}

	public LTable mouseReleased(float x, float y) {
		if (_header.columnResizeIndex > -1) {
			_header.columnResizeIndex = -1;
			_dragged = false;
		}
		return this;
	}

	public LTable mouseMoved(float x, float y) {
		if (!isTableHeadVisible()) {
			return this;
		}
		if (_header.headerY < (y + getCellHeight() + getCellHeight())) {
			final int column = isOnColumn((int) x);
			if (column >= 0) {
				_header.columnResizeIndex = column;
				return this;
			} else if (_header.columnResizeIndex > -1) {
				_header.columnResizeIndex = -1;
			}
		} else if (_header.columnResizeIndex > -1) {
			_header.columnResizeIndex = -1;
		}
		return this;
	}

	public LTable mousePressed(float x, float y) {
		if (isTableHeadVisible()) {
			if (_header.columnResizeIndex > -1) {
				_header.mouseX = MathUtils.floor(x);
				_header.columnWidthBuffer = getColumnWidth(_header.columnResizeIndex);
				return this;
			} else {
				mouseMoved(x, y);
			}
		}

		if (_readOnly) {
			return this;
		}

		assertSelectionArraySize();

		int mouseY = MathUtils.floor(y);
		if (!isTableHeadVisible()) {
			mouseY -= getCellHeight();
		}

		mouseY += getCellSpacing();

		int row = (mouseY / (getCellHeight() + getCellSpacing()));

		if (isTableHeadVisible()) {
			row--;
		}

		if (row < 0 || row >= _selected.length) {
			return this;
		}

		if (!_selected[row]) {
			_selectionCount++;
		} else {
			_selectionCount--;
		}

		if (_multipleSelection) {
			_selected[row] = !_selected[row];
		} else {
			clearSelection();
			_selected[row] = !_selected[row];
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
		if (_model == null) {
			return super.getHeight();
		}
		int height = 0;
		for (int i = 0; i < _model.getRowCount(); i++) {
			height += (_cellHeight + _cellSpacing);
		}
		if (isTableHeadVisible()) {
			height += (_cellHeight + _cellSpacing);
		}
		return height;
	}

	@Override
	public float getWidth() {
		if (_model == null) {
			return super.getWidth();
		}
		int width = 0;
		for (int i = 0; i < _model.getColumnCount(); i++) {
			width += getColumnWidth(i);
		}
		return width;
	}

	public boolean isDirty() {
		return _model == null ? false : _model.isDirty();
	}

	@Override
	public void process(long timer) {
		if (_model.isDirty()) {
			_tableWidth = _tableHeight = 0;
			for (int i = 0; i < _model.getColumnCount(); i++) {
				_tableWidth += getColumnWidth(i);
			}
			for (int i = 0; i < _model.getRowCount(); i++) {
				_tableHeight += (_cellHeight + _cellSpacing);
			}
			if (_tableWidth != getWidth() || _tableHeight + (_cellHeight + _cellSpacing) != getHeight()) {
				setSize(_tableWidth, _tableHeight + (_cellHeight + _cellSpacing));
			}
			_tableSize = MathUtils.floor(getHeight() / (_cellHeight + _cellSpacing));
		}
	}

	protected void createOtherFont(GLEx g, int displayX, int displayY) {
		final int rowCount = _model.getRowCount();
		final int colCount = _model.getColumnCount();
		int x = displayX;
		int y = displayY;
		y += _cellHeight;
		for (int row = 0; row < _tableSize && row < rowCount; row++) {
			x = displayX;
			for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
				final Object vl = _model.getValue(row, columnIndex);
				if (vl != null) {
					final ICellRenderer cellRenderer = getColumn(columnIndex).getCellRenderer();
					Dimension contentDimension = cellRenderer.getCellContentSize(vl);
					if (contentDimension == null) {
						contentDimension = new Dimension(getColumnWidth(columnIndex), _cellHeight);
					}
					final int alignedX = x + getColumn(columnIndex).getEntryAlignment()
							.alignX(getColumnWidth(columnIndex), contentDimension.getWidth()) + _offsetTableStringX;
					final int alignedY = y + getColumn(columnIndex).getEntryAlignment().alignY(_cellHeight,
							contentDimension.getHeight()) + _offsetTableStringY;

					if (_bindIcons.size() == 0) {
						cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
					} else {
						if (vl instanceof String) {
							g.setColor(_textColor);
							String v = (String) vl;
							BindIcon icon = containsBindIcon(v);
							if (icon != null) {
								cellRenderer.paint(g, icon, alignedX, alignedY, getColumnWidth(columnIndex),
										_cellHeight);
							} else {
								cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
							}
						} else {
							cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
						}
					}
				}
				x += getColumnWidth(columnIndex) + _cellSpacing;
			}
			y += (_cellHeight + _cellSpacing);
		}

	}

	protected void createSystemFont(Canvas g, int displayX, int displayY) {
		int x = displayX;
		int y = displayY;
		g.setColor(LColor.white);
		final int rowCount = _model.getRowCount();
		final int colCount = _model.getColumnCount();
		for (int row = 0; row < _tableSize && row < rowCount; row++) {
			x = displayX;
			for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
				final Object vl = _model.getValue(row, columnIndex);
				if (vl != null) {
					final ICellRenderer cellRenderer = getColumn(columnIndex).getCellRenderer();
					Dimension contentDimension = cellRenderer.getCellContentSize(vl);
					if (contentDimension == null) {
						contentDimension = new Dimension(getColumnWidth(columnIndex), _cellHeight);
					}
					final int alignedX = x + getColumn(columnIndex).getEntryAlignment()
							.alignX(getColumnWidth(columnIndex), contentDimension.getWidth()) + _offsetTableStringX;
					final int alignedY = y + getColumn(columnIndex).getEntryAlignment().alignY(_cellHeight,
							contentDimension.getHeight()) + _offsetTableStringY;

					if (_bindIcons.size() == 0) {
						cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
					} else {
						if (vl instanceof String) {
							g.setColor(_textColor);
							String v = (String) vl;
							BindIcon icon = containsBindIcon(v);
							if (icon != null) {
								cellRenderer.paint(g, icon, alignedX, alignedY, getColumnWidth(columnIndex),
										_cellHeight);
							} else {
								cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
							}
						} else {
							cellRenderer.paint(g, vl, alignedX, alignedY, getColumnWidth(columnIndex), _cellHeight);
						}
					}
				}
				x += getColumnWidth(columnIndex) + _cellSpacing;
			}
			y += (_cellHeight + _cellSpacing);
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
		final int old = g.color();
		try {
			int x = displayX;
			int y = displayY;
			y += _cellHeight;
			if (_gridVisible) {
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
					g.fillRect(x, y, _tableWidth, _cellHeight, _selectionColor);
				}
				for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
					if (_gridVisible) {
						g.drawRect(x, y, getColumnWidth(columnIndex), _cellHeight, _gridColor);
					}
					x += getColumnWidth(columnIndex) + _cellSpacing;
				}
				y += (_cellHeight + _cellSpacing);
			}
			if (!_dragged && !isClickDrag() && (_font instanceof LFont)) {
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
				g.draw(_cacheFonts, displayX, displayY + _cellHeight);
			} else {
				createOtherFont(g, displayX, displayY);
			}
			if (_tableHeaderVisible) {
				header.headerY = displayY;
				if (_headerTexture != null) {
					g.draw(_headerTexture, displayX, displayY, _tableWidth, _cellHeight, _headerBackgroundColor);
					if (_gridVisible) {
						g.drawRect(displayX, displayY, _tableWidth, _cellHeight, _gridColor);
					}
				} else {
					g.fillRect(displayX, displayY, _tableWidth, _cellHeight, _headerBackgroundColor);
				}
				x = displayX;
				for (int columnIndex = 0; columnIndex < colCount; columnIndex++) {
					String s = model.getColumnName(columnIndex);
					int columnWidth = getColumnWidth(columnIndex);
					s = _font.confineLength(s, columnWidth - OFFSET);
					int entryOffset = OFFSET + getColumn(columnIndex).getHeaderAlignment().alignX(columnWidth - OFFSET,
							_font.stringWidth(s));
					_font.drawString(g, s, x + entryOffset + _offsetHeaderX,
							(header.headerY + _font.getAscent() / 2 - 4) + _offsetHeaderY, _headTextColor);
					x += columnWidth + _cellSpacing;
				}
			}
		} finally {
			g.setColor(old);
		}
	}

	public void offsetTableHeaderString(float x) {
		setOffsetTableHeaderStringX(x);
		setOffsetTableHeaderStringY(x);
	}

	public void setOffsetTableHeaderStringX(float x) {
		_offsetHeaderX = x;
		addFlagDirty();
	}

	public void setOffsetTableHeaderStringY(float y) {
		_offsetHeaderY = y;
		addFlagDirty();
	}

	public float getOffsetTableHeaderStringX() {
		return _offsetHeaderX;
	}

	public float getOffsetTableHeaderStringY() {
		return _offsetHeaderY;
	}

	public void offsetTableString(int x) {
		setOffsetTableStringX(x);
		setOffsetTableStringY(x);
	}

	public void setOffsetTableStringX(int x) {
		_offsetTableStringX = x;
		addFlagDirty();
	}

	public void setOffsetTableStringY(int y) {
		_offsetTableStringY = y;
		addFlagDirty();
	}

	public int getOffsetTableStringX() {
		return _offsetTableStringX;
	}

	public int getOffsetTableStringY() {
		return _offsetTableStringY;
	}

	public boolean isDragged() {
		return _dragged;
	}

	public void setGridColor(LColor gridColor) {
		this._gridColor = gridColor;
		addFlagDirty();
	}

	public void setTextColor(LColor textColor) {
		this._textColor = textColor;
		addFlagDirty();
	}

	@Override
	public LTable setFont(IFont fn) {
		if (fn == null) {
			return this;
		}
		this._font = fn;
		this._cellHeight = _font.getHeight();
		this.addFlagDirty();
		return this;
	}

	@Override
	public IFont getFont() {
		return _font;
	}

	public LColor getHeadTextColor() {
		return _headTextColor;
	}

	public LTable setHeadTextColor(LColor headTextColor) {
		this._headTextColor = headTextColor;
		addFlagDirty();
		return this;
	}

	public LColor getSelectionColor() {
		return _selectionColor;
	}

	public LTable setSelectionColor(LColor selectionColor) {
		this._selectionColor = selectionColor;
		addFlagDirty();
		return this;
	}

	public LColor getGridColor() {
		return _gridColor;
	}

	public LColor getTextColor() {
		return _textColor;
	}

	public int getCellHeight() {
		return _cellHeight;
	}

	private void addFlagDirty() {
		if (_model != null) {
			_model.setDirty(true);
		}
	}

	public LTable setCellHeight(int cellHeight) {
		this._cellHeight = cellHeight;
		addFlagDirty();
		return this;
	}

	public LTable setGridVisible(boolean gridVisible) {
		this._gridVisible = gridVisible;
		addFlagDirty();
		return this;
	}

	public boolean isTableHeadVisible() {
		return _tableHeaderVisible;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public void setHeaderVisible(boolean drawTableHead) {
		this._tableHeaderVisible = drawTableHead;
		addFlagDirty();
	}

	public int getCellSpacing() {
		return _cellSpacing;
	}

	public LTable setCellSpacing(int cellSpacing) {
		this._cellSpacing = cellSpacing;
		addFlagDirty();
		return this;
	}

	public LColor getHeaderBackgroundColor() {
		return _headerBackgroundColor;
	}

	public LTable setHeaderBackgroundColor(LColor headerBackgroundColor) {
		this._headerBackgroundColor = headerBackgroundColor;
		addFlagDirty();
		return this;
	}

	protected HeaderControl getHeader() {
		return _header;
	}

	public LTable setSelected(int index, boolean b) {
		assertModel();
		assertSelectionArraySize();

		if (index < 0 || index >= _selected.length) {
			return this;
		}

		if (_multipleSelection) {
			if (_selected[index] != b) {
				_selected[index] = b;
				if (b) {
					_selectionCount++;
				} else {
					_selectionCount--;
				}
			}
		} else {
			clearSelection();
			_selected[index] = b;
			_selectionCount = 1;
		}
		return this;
	}

	public LTable setModel(ITableModel m, int width) {
		this._model = m;
		this._columns = new TableColumn[m.getColumnCount()];
		this._selected = new boolean[m.getRowCount()];
		for (int i = 0; i < _columns.length; i++) {
			_columns[i] = new TableColumn(m.getColumnName(i), width, this._font);
		}
		addFlagDirty();
		return this;
	}

	public ITableModel getModel() {
		return _model;
	}

	public boolean isSelected(int row) {
		assertModel();
		return row >= 0 && row < _selected.length ? _selected[row] : false;
	}

	public int getSelectionCount() {
		return _selectionCount;
	}

	public LTable distributeColumnWidthsEqually() {
		if (_model == null) {
			throw new LSysException("The table has no model!");
		}
		for (int i = 0; i < _columns.length; i++) {
			_columns[i].setWidth(MathUtils.ceil(getWidth() / _columns.length));
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
		return _multipleSelection;
	}

	public LTable setMultipleSelection(boolean multipleSelection) {
		this._multipleSelection = multipleSelection;
		addFlagDirty();
		return this;
	}

	public LTable clearSelection() {
		for (int i = 0; i < _selected.length; i++) {
			_selected[i] = false;
		}
		return this;
	}

	public LTable layout() {
		if (_model != null && _columns.length > 0 && getColumnWidth(0) == -1) {
			distributeColumnWidthsEqually();
		}
		return this;
	}

	public int getSelection() {
		assertModel();
		for (int i = 0; i < _selected.length; i++) {
			if (_selected[i] == true)
				return i;
		}
		return -1;
	}

	public LTable setReadOnly(boolean readOnly) {
		this._readOnly = readOnly;
		return this;
	}

	public TableColumn getColumn(int columnIndex) {
		assertModel();
		return _columns[columnIndex];
	}

	public int isOnColumn(int x) {
		float sum = 0;
		for (int col = 0; col < _columns.length - 1; col++) {
			sum += getColumnWidth(col) + _cellSpacing;
			if (MathUtils.abs(sum - x) < 5)
				return col;
		}

		return -1;
	}

	private void assertSelectionArraySize() {
		final int count = _model.getRowCount();
		if (_selected.length == count) {
			return;
		}
		if (_newSelecteds == null || _newSelecteds.length != count) {
			_newSelecteds = new boolean[count];
		}
		for (int i = 0; i < _selected.length && i < _newSelecteds.length; i++) {
			_newSelecteds[i] = _selected[i];
		}
		this._selected = CollectionUtils.copyOf(_newSelecteds, count);
	}

	private void assertModel() {
		if (_model == null) {
			throw new LSysException("No table model set!");
		}
	}

	public int getColumnMinWidth() {
		return _columnMinWidth;
	}

	public LTable setColumnMinWidth(int columnMinWidth) {
		this._columnMinWidth = columnMinWidth;
		addFlagDirty();
		return this;
	}

	public int getColumnWidth(int columnIndex) {
		final TableColumn column = _columns[columnIndex];
		if (column != null) {
			if (column.isRelative()) {
				return (int) (getWidth() * column.getRelativeWidth());
			}
			return column.getWidth();
		}
		return 0;
	}

	public LTexture getHeaderTexture() {
		return _headerTexture;
	}

	public LTable setHeaderTexture(LTexture headerTexture) {
		this._headerTexture = headerTexture;
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
		this._textColor = color;
		addFlagDirty();
		return this;
	}

	@Override
	public LColor getFontColor() {
		return _textColor.cpy();
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
		if (_headerTexture != null) {
			_headerTexture.close();
			_headerTexture = null;
		}
		_bindIcons.clear();
	}

}
