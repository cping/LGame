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

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.html.HtmlElement;
import loon.utils.html.css.CssStyleSheet;
import loon.utils.html.css.CssDimensions.Rect;

public class ImageCommand extends DisplayCommand {

	private LTexture texture;

	private String altText;

	private int width;

	private int height;

	public ImageCommand(CssStyleSheet sheet, float width, float height, LColor color) {
		super(sheet, "Image", width, height, color);
	}

	@Override
	public void parser(HtmlElement e) {

		String src = e.getAttribute("src", null);

		if (!StringUtils.isEmpty(src)) {
			texture = LSystem.loadTexture(src);
			width = texture.getWidth();
			height = texture.getHeight();
		}

		altText = e.getAttribute("alt", null);

		String sw = e.getAttribute("width", "0");
		String sh = e.getAttribute("height", "0");

		int index = sw.indexOf("%");
		try {
			if (sw.indexOf("%") != -1) {
				width = (int) MathUtils.percent(screenWidth, Float.parseFloat(sw.substring(0, index)));
			} else {
				width = (int) Float.parseFloat(sw);
			}
			if (sh.indexOf("%") != -1) {
				height = (int) MathUtils.percent(screenHeight, Float.parseFloat(sh.substring(0, index)));
			} else {
				height = (int) Float.parseFloat(sh);
			}
		} catch (Throwable ex) {
			LSystem.error("Image command exception", ex);
		}

		if (width < 1) {
			width = 1;
		}
		if (height < 1) {
			height = 1;
		}

		this.rect = new Rect(0, 0, width, height);
	}

	public String alt() {
		return altText;
	}

	@Override
	public void paint(GLEx g, float x, float y) {
		g.draw(texture, rect.left + rect.x + x, rect.top + rect.y + y, rect.width, rect.height);
	}

	@Override
	public void update() {

	}

	@Override
	public void close() {
		if (texture != null) {
			texture.close();
			texture = null;
		}
	}

}
