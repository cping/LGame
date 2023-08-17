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
package loon.component;

import loon.LSystem;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.html.HtmlDisplay;
import loon.utils.html.HtmlParser;

/**
 * HTML内容渲染用组件,用html+css脚本的方式构建游戏画面(HtmlDisplay构建中……此类完成度取决于HtmlDisplay的完成度……)
 */
public class LHtmlView extends LContainer {

	private HtmlDisplay display;

	public LHtmlView(int x, int y) {
		this(x, y, LColor.black, LColor.white);
	}

	public LHtmlView(int x, int y, LColor f, LColor b) {
		this(x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), f, b);
	}

	public LHtmlView(int x, int y, int w, int h, LColor f, LColor b) {
		super(x, y, w, h);
		this.display = new HtmlDisplay(w, h, f, b);
	}

	public void loadText(String text) {
		display.parse(HtmlParser.loadText(text));
	}

	public void loadPath(String path) {
		display.parse(HtmlParser.parse(path));
	}

	@Override
	public void createUI(GLEx g, int x, int y) {
		display.paint(g, x, y);
	}

	public HtmlDisplay getHtmlDisplay() {
		return display;
	}

	@Override
	public String getUIName() {
		return "HtmlView";
	}

	@Override
	public void destory() {

	}

}
