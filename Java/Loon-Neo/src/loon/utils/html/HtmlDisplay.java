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
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.html.command.DisplayCommand;
import loon.utils.html.command.DivCommand;
import loon.utils.html.command.ImageCommand;
import loon.utils.html.command.LineCommand;
import loon.utils.html.command.TextCommand;
import loon.utils.html.css.CssDimensions;
import loon.utils.html.css.CssKeyword;
import loon.utils.html.css.CssDimensions.EdgeSize;
import loon.utils.html.css.CssDimensions.Rect;
import loon.utils.html.css.CssElement;
import loon.utils.html.css.CssLength;
import loon.utils.html.css.CssStyleNode;
import loon.utils.html.css.CssStyleSheet;
import loon.utils.html.css.CssUnit;
import loon.utils.html.css.CssValue;

/**
 * 构建中,逐渐实现w3c标准……
 */
public class HtmlDisplay {

	private TArray<DisplayCommand> displays;

	private float width, height;

	private int fontSize = 0;

	private TArray<CssStyleSheet> cssSheets;

	private CssDimensions cssBlock;

	private Rect bodyRect;

	private EdgeSize bodyPadding;

	private LColor defaultColor;

	private LColor backgroundColor;

	private LColor foregroundColor;

	private String defaultFontName;

	public HtmlDisplay(float w, float h) {
		this(w, h, LColor.black, LColor.white);
	}

	public HtmlDisplay(float w, float h, LColor fontColor, LColor background) {
		this.cssBlock = CssDimensions.createDimension(w, h);
		this.cssSheets = new TArray<CssStyleSheet>();
		this.displays = new TArray<DisplayCommand>();
		this.width = w;
		this.height = h;
		this.backgroundColor = background;
		defaultColor = fontColor;
	}

	public void parse(HtmlElement element) {

		TArray<HtmlElement> looper = element.childs();

		Rect lastRect = null;

		bodyRect = new Rect();

		bodyPadding = new EdgeSize();

		fontSize = LSystem.getSystemGameFont().getSize();

		float lineWidth = 0;
		float lineHeight = 0;

		CssStyleSheet cssStyle = null;

		boolean newLine = false;

		int newLineAmount = 0;

		while (looper.size > 0) {

			TArray<HtmlElement> next = new TArray<HtmlElement>();

			int sysSize = fontSize + 5;

			for (HtmlElement node : looper) {

				String tagName = node.getName();
				DisplayCommand display = null;

				cssStyle = node.getStyleSheet();

				if (cssStyle.size() > 0) {
					cssSheets.add(cssStyle);
				} else {
					cssStyle = cssSheets.last();
				}

				if (lastRect != null) {
					if (lineWidth - lastRect.width > width) {
						lineWidth = 0;
					}
				} else {
					if (lineWidth > width) {
						lineWidth = 0;
					}
				}

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

				if (node.isBody()) {

					if (cssStyle != null && cssStyle.size() > 0) {

						CssElement cssElement = new CssElement(cssStyle, node, bodyRect, bodyPadding, width, height);

						cssElement.parse();

						defaultColor = cssElement.getFontColor();
						defaultFontName = cssElement.getFontName();

						fontSize = (int) cssElement.getFontSize();

						backgroundColor = cssElement.getBackgroundColor();
						foregroundColor = cssElement.getForegroundColor();

						bodyRect = cssElement.getMargin();
						bodyPadding = cssElement.getPadding();

					}

				} else if (node.isH()) {
					display = new TextCommand(cssStyle, width, height, defaultFontName, fontSize, defaultColor);
					display.parser(node);
					newLine = true;
					newLineAmount = sysSize;
				} else if (StringUtils.contains(tagName, "a", "b", "label", "font")) {
					display = new TextCommand(cssStyle, width, height, defaultFontName, fontSize, defaultColor);
					display.parser(node);
				} else if (StringUtils.contains(tagName, "span", "div")) {
					display = new DivCommand(cssStyle, width, height, defaultFontName, fontSize, defaultColor);
					display.parser(node);
				} else if ("img".equals(tagName)) {
					display = new ImageCommand(cssStyle, width, height, defaultColor);
					display.parser(node);
				} else if ("p".equals(tagName)) {
					display = new TextCommand(cssStyle, width, height, defaultFontName, fontSize, defaultColor);
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
				} else if ("hr".equals(tagName)) {
					display = new LineCommand(cssStyle, width, height, defaultColor);
					display.parser(node);
					newLine = true;
					newLineAmount = sysSize + 5;
				}

				if (display != null) {

					float height = (lastRect == null) ? 0 : lastRect.height;

					lastRect = display.getRect();

					display.addX(lineWidth);
					display.addY(lineHeight);

					display.setLimit(bodyRect);

					display.update();

					if (height != 0 && lineWidth != 0) {
						lastRect.height = MathUtils.max(lastRect.height, height);
					}

					lineWidth += lastRect.width;

					displays.add(display);
				}
				next.addAll(node.childs());
			}
			looper = next;
		}
	}

	public void paint(GLEx g, float x, float y) {
		g.fillRect(x, y, width, height, backgroundColor);
		for (int i = 0; i < displays.size; i++) {
			DisplayCommand command = displays.get(i);
			if (command != null) {
				command.paint(g, x, y);
			}
		}
	}

	public CssDimensions getCssBlock() {
		return cssBlock;
	}

	public LColor getDefaultColor() {
		return defaultColor.cpy();
	}

	public void setDefaultColor(LColor defaultColor) {
		this.defaultColor = defaultColor;
	}

	public TArray<CssStyleSheet> getCssSheets() {
		return cssSheets;
	}

	public LColor getBackgroundColor() {
		return backgroundColor.cpy();
	}

	public LColor getForegroundColor() {
		return foregroundColor.cpy();
	}

	public String getDefaultFontName() {
		return defaultFontName;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public static CssDimensions getBlockHeight(CssDimensions cssBlock, CssStyleNode style) {
		CssValue height = style.getValueOf("height");
		if (height != null && height.toPx() != 0.0f) {
			cssBlock.content.height = height.toPx();
		}
		return cssBlock;
	}

	public static CssDimensions getBlockPosition(CssDimensions cssBlock, CssDimensions conBlock, CssStyleNode style) {

		CssLength zeroLength = new CssLength(0.0f, CssUnit.PX());

		cssBlock.margin.top = (style.find(zeroLength, "margin-top", "margin")).toPx();
		cssBlock.margin.bottom = (style.find(zeroLength, "margin-bottom", "margin")).toPx();

		cssBlock.border.top = (style.find(zeroLength, "border-top-width", "border-width")).toPx();
		cssBlock.border.bottom = ((CssLength) style.find(zeroLength, "border-bottom-width", "border-width")).toPx();

		cssBlock.padding.top = (style.find(zeroLength, "padding-top", "padding")).toPx();
		cssBlock.padding.bottom = (style.find(zeroLength, "padding-bottom", "padding")).toPx();

		cssBlock.content.x = conBlock.content.x + cssBlock.margin.left + cssBlock.border.left + cssBlock.padding.left;

		cssBlock.content.y = conBlock.content.y + conBlock.content.height + cssBlock.margin.top + cssBlock.border.top
				+ cssBlock.padding.top;

		return cssBlock;
	}

	public static CssDimensions getBlockWidth(CssDimensions cssBlock, CssDimensions conBlock, CssStyleNode style,
			int height) {

		CssValue initWidth = new CssKeyword("auto");
		CssValue width = style.find(initWidth, "width");
		CssLength zeroLength = new CssLength(0.0f, CssUnit.PX());

		CssValue leftMargin = style.find(zeroLength, "margin-left", "margin");
		CssValue rightMargin = style.find(zeroLength, "margin-right", "margin");

		CssValue leftBorder = style.find(zeroLength, "border-left-width", "border-width");
		CssValue rightBorder = style.find(zeroLength, "border-right-width", "border-width");

		CssValue leftPadding = style.find(zeroLength, "padding-left", "padding");
		CssValue rightPadding = style.find(zeroLength, "padding-right", "padding");

		float total = leftMargin.toPx() + rightMargin.toPx() + leftBorder.toPx() + rightBorder.toPx()
				+ leftPadding.toPx() + rightPadding.toPx() + width.toPx();

		if (!width.getValueString().equals("auto") && total > conBlock.content.width) {
			if (leftMargin.getValueString().equals("auto")) {
				leftMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (rightMargin.getValueString().equals("auto")) {
				rightMargin = new CssLength(0.0f, CssUnit.PX());
			}
		}

		float underflow = conBlock.content.width - total;

		boolean widthAuto, marginLeftAuto, marginRightAuto;

		widthAuto = width.getValueString().equals("auto");
		marginLeftAuto = leftMargin.getValueString().equals("auto");
		marginRightAuto = rightMargin.getValueString().equals("auto");

		if (!widthAuto & !marginLeftAuto & !marginRightAuto) {
			rightMargin = new CssLength(rightMargin.toPx() + underflow, CssUnit.PX());
		} else if ((!widthAuto & !marginLeftAuto & marginRightAuto)) {
			rightMargin = new CssLength(underflow, CssUnit.PX());
		} else if ((!widthAuto & marginLeftAuto & !marginRightAuto)) {
			leftMargin = new CssLength(underflow, CssUnit.PX());
		} else if ((!widthAuto & marginLeftAuto & marginRightAuto)) {
			leftMargin = new CssLength(underflow / 2, CssUnit.PX());
			rightMargin = new CssLength(underflow / 2, CssUnit.PX());
		} else if (widthAuto) {
			if (marginLeftAuto) {
				leftMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (marginRightAuto) {
				rightMargin = new CssLength(0.0f, CssUnit.PX());
			}
			if (underflow >= 0.0f) {
				width = new CssLength(underflow, CssUnit.PX());
			} else {
				width = new CssLength(0.0f, CssUnit.PX());
				rightMargin = new CssLength(rightMargin.toPx() + underflow, CssUnit.PX());
			}
		}

		cssBlock.content.width = width.toPx();
		cssBlock.content.height = height;
		cssBlock.padding.left = leftPadding.toPx();
		cssBlock.padding.right = rightPadding.toPx();
		cssBlock.margin.right = rightMargin.toPx();
		cssBlock.margin.left = leftMargin.toPx();

		return cssBlock;
	}

}
