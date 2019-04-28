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
package loon.utils.html.css;

import loon.canvas.LColor;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.html.HtmlElement;
import loon.utils.html.css.CssDimensions.EdgeSize;
import loon.utils.html.css.CssDimensions.Rect;

public class CssElement {

	private float fontSize;

	private String fontName;

	private LColor fontColor;

	private LColor backgroundColor;

	private LColor foregroundColor;

	private Rect marginRect;

	private EdgeSize paddingSize;

	private HtmlElement node;

	private CssStyleSheet style;

	private float width;

	private float height;

	public CssElement(CssStyleSheet s, HtmlElement e, Rect m, EdgeSize p, float w, float h) {
		this.style = s;
		this.node = e;
		this.marginRect = m;
		this.paddingSize = p;
		this.width = w;
		this.height = h;
	}

	public void parse() {

		CssStyleBuilder builder = new CssStyleBuilder();
		CssStyleNode cssNode = builder.build(node, style);

		CssValue value = cssNode.getValueOf("background-color");
		if (value != null && value instanceof CssColor) {
			backgroundColor = ((CssColor) value).getLColor();
		} else if (value != null) {
			backgroundColor = new LColor(value.getValueString());
		}

		value = cssNode.getValueOf("foreground-color");
		if (value != null && value instanceof CssColor) {
			foregroundColor = ((CssColor) value).getLColor();
		} else if (value != null) {
			foregroundColor = new LColor(value.getValueString());
		}

		value = cssNode.getValueOf("color");

		if (value != null && value instanceof CssColor) {
			fontColor = ((CssColor) value).getLColor();
		} else if (value != null) {
			fontColor = new LColor(value.getValueString());
		}

		value = cssNode.getValueOf("font-family");

		if (value != null) {
			fontName = value.getValueString();
		}

		value = cssNode.getValueOf("font-size");

		if (value != null && value instanceof CssLength) {
			fontSize = MathUtils.max(1f, ((CssLength) value).toPx());
		} else if (value != null) {
			fontSize = MathUtils.max(1f, Rect.getValue(MathUtils.max(width, height), fontSize, value.getValueString()));
		}

		value = cssNode.getValueOf("margin");

		if (value != null) {
			String margin = value.getValueString();

			String[] items = StringUtils.split(margin, ' ');

			marginRect = Rect.analyze(marginRect, fontSize, width, height, items);

		}

		CssKeyword zeroLength = new CssKeyword("0");

		marginRect.top = MathUtils.max(marginRect.top,
				Rect.getValue(height, fontSize, cssNode.find(zeroLength, "margin-top").getValueString()));
		marginRect.right = MathUtils.max(marginRect.right,
				Rect.getValue(width, fontSize, cssNode.find(zeroLength, "margin-right").getValueString()));
		marginRect.bottom = MathUtils.max(marginRect.bottom,
				Rect.getValue(height, fontSize, cssNode.find(zeroLength, "margin-bottom").getValueString()));
		marginRect.left = MathUtils.max(marginRect.left,
				Rect.getValue(width, fontSize, cssNode.find(zeroLength, "margin-left").getValueString()));

		value = cssNode.getValueOf("padding");

		if (value != null) {
			String margin = value.getValueString();

			String[] items = StringUtils.split(margin, ' ');

			paddingSize = Rect.analyze(paddingSize, fontSize, width, height, items);

		}

		paddingSize.top = MathUtils.max(paddingSize.top,
				Rect.getValue(height, fontSize, cssNode.find(zeroLength, "padding-top").getValueString()));
		paddingSize.right = MathUtils.max(paddingSize.right,
				Rect.getValue(width, fontSize, cssNode.find(zeroLength, "padding-right").getValueString()));
		paddingSize.bottom = MathUtils.max(paddingSize.bottom,
				Rect.getValue(height, fontSize, cssNode.find(zeroLength, "padding-bottom").getValueString()));
		paddingSize.left = MathUtils.max(paddingSize.left,
				Rect.getValue(width, fontSize, cssNode.find(zeroLength, "padding-left").getValueString()));

	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public LColor getFontColor() {
		return fontColor == null ? LColor.black.cpy() : fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

	public LColor getBackgroundColor() {
		return backgroundColor == null ? LColor.white.cpy() : backgroundColor;
	}

	public void setBackgroundColor(LColor backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public LColor getForegroundColor() {
		return foregroundColor == null ? LColor.white.cpy() : foregroundColor;
	}

	public void setForegroundColor(LColor foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public Rect getMargin() {
		return marginRect;
	}

	public void setMargin(Rect margin) {
		this.marginRect = margin;
	}

	public EdgeSize getPadding() {
		return paddingSize;
	}

	public void setPadding(EdgeSize padding) {
		this.paddingSize = padding;
	}
}
