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

import loon.core.geom.Alignment;
import loon.core.graphics.device.LFont;

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
