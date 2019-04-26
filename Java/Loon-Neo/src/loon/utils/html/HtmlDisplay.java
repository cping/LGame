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
package loon.utils.html;

import loon.LSystem;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.html.command.DisplayCommand;
import loon.utils.html.command.ImageCommand;
import loon.utils.html.command.TextCommand;
import loon.utils.html.css.CssDimensions.Rect;

/**
 * 构建中,逐渐实现w3c标准……
 */
public class HtmlDisplay {

	private TArray<DisplayCommand> displays = new TArray<DisplayCommand>();

	private float width, height;

	public HtmlDisplay(float w, float h) {
		this.width = w;
		this.height = h;
	}

	public void parse(HtmlElement element) {

		TArray<HtmlElement> looper = element.childs();

		Rect lastRect = null;

		int sysSize = LSystem.getSystemGameFont().getHeight();

		float lineWidth = 0;
		float lineHeight = 0;

		boolean newLine = false;

		int newLineAmount = 0;

		while (looper.size > 0) {
			TArray<HtmlElement> next = new TArray<HtmlElement>();
			for (HtmlElement node : looper) {

				String tagName = node.getName();
				DisplayCommand display = null;

				if (newLine) {
					lineWidth = 0;
					if (lastRect != null) {
						lineHeight += lastRect.height;
					} else {
						lineHeight += sysSize;
					}
					lineHeight += newLineAmount;
					newLineAmount = 0;
					newLine = false;
				}

				if ("b".equals(tagName)) {
					display = new TextCommand(width, height);
					display.parser(node);
				} else if ("font".equals(tagName)) {
					display = new TextCommand(width, height);
					display.parser(node);
				} else if ("img".equals(tagName)) {
					display = new ImageCommand(width, height);
					display.parser(node);
				} else if ("p".equals(tagName)) {
					display = new TextCommand(width, height);
					display.parser(node);
					newLine = true;
					newLineAmount = sysSize;
				} else if ("br".equals(tagName)) {
					lineWidth = 0;
					if (lastRect != null) {
						lineHeight += lastRect.height;
					} else {
						lineHeight += sysSize;
					}
				}

				if (display != null) {

					lastRect = display.getRect();

					display.addX(lineWidth);
					display.addY(lineHeight);

					lineWidth += lastRect.width;

					displays.add(display);
				}
				next.addAll(node.childs());
			}
			looper = next;
		}
	}

	public void paint(GLEx g, float x, float y) {
		for (int i = 0; i < displays.size; i++) {
			DisplayCommand command = displays.get(i);
			if (command != null) {
				command.paint(g, x, y);
			}
		}
	}
}
