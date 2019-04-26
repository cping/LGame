/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.utils.html.command;

import loon.HorizontalAlign;
import loon.LSystem;
import loon.canvas.LColor;
import loon.font.FontUtils;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.html.HtmlElement;
import loon.utils.html.css.CssDimensions.Rect;

public class TextCommand extends DisplayCommand {

	public TextCommand(float width, float height) {
		super("Text", width, height);
	}

	protected String text;

	protected LColor color;

	protected IFont font;

	private HorizontalAlign align;

	@Override
	public void parser(HtmlElement e) {

		int sysSize = LSystem.getSystemGameFont().getSize();
		String sysFont = LSystem.getSystemGameFontName().toLowerCase();

		if (e.isFont()) {
			text = e.getData();
			float size = (e.getFloatAttribute("size", 2.0f));
			int dsize = 20;
			if (size < 20) {
				dsize = MathUtils.floor(size * 8f);
			} else {
				dsize = MathUtils.floor(size);
			}
			String face = e.getAttribute("face", "dialog");
			if (sysFont.equals(face) && dsize == sysSize) {
				font = LFont.getFont(face, dsize);
			} else if (face != null) {
				font = LFont.getFont(face, dsize);
			} else {
				font = LFont.getFont(dsize);
			}
			String colorStr = e.getAttribute("color", null);
			if (colorStr == null) {
				color = LColor.white;
			} else {
				color = LColor.decode(colorStr);
			}
		} else {
			text = e.getData();
			color = LColor.black;
			font = LSystem.getSystemGameFont();
		}
		PointF fontSize = FontUtils.getTextWidthAndHeight(font, text);
		rect = new Rect(0, 0, fontSize.x, fontSize.y);

		String alignName = e.getAttribute("align", null);
		if (!StringUtils.isEmpty(alignName)) {
			alignName = alignName.trim().toLowerCase();
		}

		if ("right".equals(alignName)) {
			align = HorizontalAlign.RIGHT;
		} else if ("center".equals(alignName)) {
			align = HorizontalAlign.CENTER;
		} else {
			align = HorizontalAlign.LEFT;
		}

		if (e.isP()) {
			rect.y += sysSize;
			rect.height += sysSize;
		}
	}

	@Override
	public void paint(GLEx g, float x, float y) {
		IFont temp = g.getFont();
		g.setFont(font);
		switch (align) {
		case RIGHT:
			g.drawString(text, screenWidth - rect.width + x, rect.y + y, color);
			break;
		case CENTER:
			g.drawString(text, (screenWidth - rect.width) / 2 + x, rect.y + y, color);
			break;
		default:
			g.drawString(text, rect.x + x, rect.y + y, color);
			break;
		}
		g.setFont(temp);
	}

}
