package loon.core.graphics.component.table;

import loon.core.geom.Alignment;
import loon.core.graphics.LFont;

public class TableColumn {

	private ICellRenderer cellRenderer = new TextCellRenderer();

	private String name = "---";

	private int width = -1;

	private float relativeWidth = -1;

	private Alignment headingAlignment = Alignment.MIDDLE;
	
	private Alignment entryAlignment = Alignment.LEFT;

	protected TableColumn(String name,LFont font) {
		this.name = name;
		setWidth(font.stringWidth(name));
	}

	protected TableColumn(String name, int width) {
		this.name = name;
		setWidth(width);
	}

	protected TableColumn(String name, float relativeWidth) {
		this.name = name;
		setRelativeWidth(relativeWidth);
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.relativeWidth = -1;
	}

	public float getRelativeWidth() {
		return relativeWidth;
	}

	public void setRelativeWidth(float relativeWidth) {
		relativeWidth = Math.max(0, Math.min(1, relativeWidth));
		this.relativeWidth = relativeWidth;
		this.width = 0;
	}

	public boolean isRelative() {
		return relativeWidth != -1;
	}

	public Alignment getHeaderAlignment() {
		return headingAlignment;
	}

	public void setHeaderAlignment(Alignment headingAlignment) {
		if (headingAlignment == null) {
			return;
		}
		this.headingAlignment = headingAlignment;
	}

	public Alignment getEntryAlignment() {
		return entryAlignment;
	}

	public void setEntryAlignment(Alignment valueAlignment) {
		if (valueAlignment == null) {
			return;
		}
		this.entryAlignment = valueAlignment;
	}

	public ICellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(ICellRenderer cellRenderer) {
		if (cellRenderer == null) {
			throw new IllegalArgumentException("cellRenderer == null");
		}
		this.cellRenderer = cellRenderer;
	}

}
