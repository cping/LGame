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

import loon.LRelease;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.html.HtmlElement;
import loon.utils.html.css.CssStyleSheet;
import loon.utils.html.css.CssDimensions.Rect;

/**
 * 命令存储用类,转换html标记字符串为对应loon渲染指令并保存,在需要的场合渲染展示
 */
public abstract class DisplayCommand implements LRelease {

	protected CssStyleSheet styleSheet;

	protected Rect rect;

	protected LColor defaultColor;

	private final String name;

	protected final float screenWidth;

	protected final float screenHeight;

	public DisplayCommand(CssStyleSheet sheet, String name, float width, float height, LColor color) {
		this.styleSheet = sheet;
		this.name = name;
		this.defaultColor = color;
		this.screenWidth = width;
		this.screenHeight = height;
	}

	public String getName() {
		return this.name;
	}

	public Rect getRect() {
		if (this.rect == null) {
			this.rect = new Rect();
		}
		return this.rect;
	}

	public void addX(float width) {
		if (rect != null) {
			rect.x += width + 1;
		}
	}

	public void addY(float height) {
		if (rect != null) {
			rect.y += height + 1;
		}
	}

	public void setLimit(Rect r) {
		if (rect != null) {
			rect.setLimit(r);
		}
	}

	public CssStyleSheet getStyleSheet() {
		return styleSheet;
	}

	public void setStyleSheet(CssStyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}

	public abstract void update();

	public abstract void parser(HtmlElement e);

	public abstract void paint(GLEx g, float x, float y);

}
