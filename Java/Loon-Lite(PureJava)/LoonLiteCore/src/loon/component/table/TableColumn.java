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
import loon.font.IFont;
import loon.geom.Alignment;
import loon.utils.MathUtils;

public class TableColumn {

	private ICellRenderer cellRenderer;

	private String name = "---";

	private int width = -1;

	private float relativeWidth = -1;

	private Alignment headingAlignment = Alignment.MIDDLE;

	private Alignment entryAlignment = Alignment.LEFT;

	protected TableColumn(String name, IFont font) {
		this.name = name;
		this.cellRenderer = new TextCellRenderer(font);
		setWidth(font.stringWidth(name));
	}

	protected TableColumn(String name, int width, IFont font) {
		this.name = name;
		this.cellRenderer = new TextCellRenderer(font);
		setWidth(width);
	}

	protected TableColumn(String name, float relativeWidth, IFont font) {
		this.name = name;
		this.cellRenderer = new TextCellRenderer(font);
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
		relativeWidth = MathUtils.max(0, MathUtils.min(1, relativeWidth));
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

	public TableColumn setEntryAlignment(Alignment valueAlignment) {
		if (valueAlignment == null) {
			return this;
		}
		this.entryAlignment = valueAlignment;
		return this;
	}

	public ICellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public TableColumn setCellRenderer(ICellRenderer cellRenderer) {
		if (cellRenderer == null) {
			throw new LSysException("cellRenderer == null");
		}
		this.cellRenderer = cellRenderer;
		return this;
	}

}
