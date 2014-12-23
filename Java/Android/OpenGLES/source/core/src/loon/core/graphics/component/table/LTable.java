package loon.core.graphics.component.table;

import loon.core.geom.Dimension;
import loon.core.graphics.LColor;
import loon.core.graphics.LComponent;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.collection.Array;

public class LTable extends LComponent {

	private ITableModel model = null;

	private TableColumn[] columns = null;

	private boolean[] selected = null;

	private int selectionCount = 0;

	private boolean multipleSelection = false;

	private boolean readOnly = false;

	private HeaderControl header = new HeaderControl();

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

	private LColor selectionColor = LColor.red;

	private LColor headTextColor = LColor.green;

	private LFont font = LFont.getDefaultFont();

	public LTable(LFont font, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.font = font;
		this.cellHeight = font.getHeight();
	}

	public void setData(Array<ListItem> list, int width) {
		setModel(new SimpleTableModel(list), width);
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

	@Override
	public void createUI(GLEx g, int displayX, int displayY,
			LComponent component, LTexture[] buttonImage) {
		ITableModel model = getModel();
		HeaderControl header = getHeader();
		if (model == null) {
			return;
		}

		int x = displayX;
		int y = displayY;

		y += cellHeight;

		int size = getHeight() / (cellHeight + cellSpacing);

		for (int row = 0; row < size && row < model.getRowCount(); row++) {
			x = displayX;
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

					cellRenderer.paint(g, value, alignedX, alignedY,
							getColumnWidth(columnIndex), cellHeight);
				}

				if (gridVisible) {
					g.setColor(gridColor);
					g.drawLine(x, y, x + getColumnWidth(columnIndex), y);
					g.drawLine(x + getColumnWidth(columnIndex), y, x
							+ getColumnWidth(columnIndex), y + cellHeight);
					g.drawLine(x + getColumnWidth(columnIndex), y + cellHeight,
							x, y + cellHeight);
					g.drawLine(x, y + cellHeight, x, y);
				}

				x += getColumnWidth(columnIndex) + cellSpacing;
			}
			y += (cellHeight + cellSpacing);
		}
		if (tableHeaderVisible) {
			header.headerY = displayY;

			g.setColor(headerBackgroundColor);
			g.fillRect(displayX, displayY, model.getColumnCount()
					* (getColumnWidth(0) + cellSpacing) + 1, font.getHeight());

			x = displayX;
			g.setColor(headTextColor);

			for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
				String s = model.getColumnName(columnIndex);
				int columnWidth = getColumnWidth(columnIndex);

				s = font.confineLength(s, columnWidth - OFFSET);

				int entryOffset = OFFSET
						+ getColumn(columnIndex).getHeaderAlignment().alignX(
								columnWidth - OFFSET, font.stringWidth(s));

				g.setFont(font);
				g.drawString(s, x + entryOffset,
						header.headerY + font.getHeight());
				x += columnWidth + cellSpacing;
			}
		}
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
		columns = new TableColumn[m.getColumnCount()];
		selected = new boolean[m.getRowCount()];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new TableColumn(m.getColumnName(i), width);
		}
		model = m;
	}

	public ITableModel getModel() {
		return model;
	}

	public boolean isSelected(int row) {
		assertModel();

		if (row >= 0 && row < selected.length)
			return selected[row];
		else
			return false;
	}

	public int getSelectionCount() {
		return selectionCount;
	}

	public void distributeColumnWidthsEqually() {
		if (model == null)
			throw new IllegalStateException("The table has no model!");

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
		if (selected.length == model.getRowCount())
			return;

		boolean[] newSelected = new boolean[model.getRowCount()];

		for (int i = 0; i < selected.length && i < newSelected.length; i++) {
			newSelected[i] = selected[i];
		}

		this.selected = newSelected;
	}

	private void assertModel() {
		if (model == null){
			throw new IllegalStateException("No table model set!");
		}
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

	@Override
	public String getUIName() {
		return "Table";
	}

}
