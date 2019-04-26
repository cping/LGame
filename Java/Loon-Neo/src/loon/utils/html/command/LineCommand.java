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
import loon.utils.html.HtmlElement;
import loon.utils.html.css.CssDimensions.Rect;

public class LineCommand extends DisplayCommand {

	private LTexture texture;

	private int space;

	private int offset;

	public LineCommand(float width, float height) {
		super("Line", width, height);
	}

	@Override
	public void parser(HtmlElement e) {
		offset = 4;
		space = LSystem.getSystemGameFont().getHeight();
		texture = LSystem.base().graphics().finalColorTex();
		if (e.isAttrEmpty()) {
			rect = new Rect(offset, 0, screenWidth - offset, 1);
		}
	}

	@Override
	public void paint(GLEx g, float x, float y) {
		boolean allTex = g.isAlltextures();
		g.setAlltextures(true);
		g.draw(texture, offset + x, rect.y + y + space, rect.width - offset, rect.height, LColor.black);
		g.setAlltextures(allTex);
	}

}
