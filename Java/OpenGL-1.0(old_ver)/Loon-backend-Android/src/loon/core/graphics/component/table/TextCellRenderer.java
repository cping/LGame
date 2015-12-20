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
import loon.core.geom.Dimension;
import loon.core.graphics.component.table.LTable.BindIcon;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;

public class TextCellRenderer implements ICellRenderer {
	private LFont font = LFont.getDefaultFont();
	private LColor textColor = LColor.white;
	private Alignment alignment = Alignment.LEFT;

	public void paint(GLEx g, Object value, int x, int y, int width, int height) {
		if (value instanceof BindIcon) {
			int size = font.getHeight() - 4;
			BindIcon icon = (BindIcon) value;
			String s = icon.name;
			s = font.confineLength(s, width - size - 4);
			int entryOffset = 4 + alignment.alignX(width - 4,
					font.stringWidth(s));
			g.setFont(font);
			g.setColor(textColor);
			if (icon.texture != null) {
				g.drawTexture(icon.texture, x + 4, y
						+ (font.getHeight() - size) / 2, size, size);
				g.drawString(s, x + size + entryOffset + 4,
						y + font.getHeight() - 4);
			} else {
				g.drawString(s, x + entryOffset, y + font.getHeight() - 4);
			}
		} else {
			String s = value.toString();
			s = font.confineLength(s, width - 4);
			int entryOffset = 4 + alignment.alignX(width - 4,
					font.stringWidth(s));
			g.setFont(font);
			g.setColor(textColor);
			g.drawString(s, x + entryOffset, y + font.getHeight() - 4);
		}
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public void setTextColor(LColor textColor) {
		this.textColor = textColor;
	}

	public Dimension getCellContentSize(Object value) {
		if (value == null) {
			return null;
		}
		String s = value.toString();
		return new Dimension(font.stringWidth(s), font.getHeight());
	}
}
