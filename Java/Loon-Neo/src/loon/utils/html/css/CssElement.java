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
import loon.utils.html.css.CssDimensions.Border;
import loon.utils.html.css.CssDimensions.EdgeSize;
import loon.utils.html.css.CssDimensions.Rect;

public class CssElement {

	private float fontSize;

	private String fontStyle;

	private float lineHeight;

	private String fontName;

	private LColor fontColor;

	private LColor backgroundColor;

	private LColor foregroundColor;

	private String backgroundImageUrl;

	private String backgroundPosition;

	private String border;

	private Rect marginRect;

	private EdgeSize paddingSize;

	private Border borderSize;

	private HtmlElement node;

	private CssStyleSheet style;

	private float width;

	private float height;

	private float cssWidth;

	private float cssHeight;

	private CssStyleNode cssNode;

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

		this.cssNode = builder.build(node, style);

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

		value = cssNode.getValueOf("background-image");

		if (value != null) {
			backgroundImageUrl = value.getValueString();
		}

		value = cssNode.getValueOf("background-position");

		if (value != null) {
			backgroundPosition = value.getValueString();
		}

		value = cssNode.getValueOf("font-family");

		if (value != null) {
			fontName = value.getValueString();
		}

		value = cssNode.getValueOf("font-style");

		if (value != null) {
			fontStyle = value.getValueString();
		}

		value = cssNode.getValueOf("font-size");

		if (value != null && value instanceof CssLength) {
			fontSize = MathUtils.max(1f, ((CssLength) value).toPx());
		} else if (value != null) {
			fontSize = MathUtils.max(1f, Rect.getValue(MathUtils.max(width, height), fontSize, value.getValueString()));
		}

		value = cssNode.getValueOf("line_height");

		if (value != null && value instanceof CssLength) {
			lineHeight = MathUtils.max(1f, ((CssLength) value).toPx());
		} else if (value != null) {
			lineHeight = MathUtils.max(1f,
					Rect.getValue(MathUtils.max(width, height), fontSize, value.getValueString()));
		}

		value = cssNode.getValueOf("width");

		if (value != null && value instanceof CssLength) {
			cssWidth = MathUtils.max(1f, ((CssLength) value).toPx());
		} else if (value != null) {
			cssWidth = MathUtils.max(1f, Rect.getValue(width, fontSize, value.getValueString()));
		}

		value = cssNode.getValueOf("height");

		if (value != null && value instanceof CssLength) {
			cssHeight = MathUtils.max(1f, ((CssLength) value).toPx());
		} else if (value != null) {
			cssHeight = MathUtils.max(1f, Rect.getValue(height, fontSize, value.getValueString()));
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

		value = cssNode.getValueOf("border");

		if (value != null) {
			border = value.getValueString();
		}

		borderSize.top = cssNode.find(zeroLength, "border-top").getValueString();
		borderSize.right = cssNode.find(zeroLength, "border-right").getValueString();
		borderSize.bottom = cssNode.find(zeroLength, "border-bottom").getValueString();
		borderSize.left = cssNode.find(zeroLength, "border-left").getValueString();

		value = cssNode.getValueOf("border-top-color");
		if (value != null) {
			borderSize.top += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-top-width");
		if (value != null) {
			borderSize.top += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-top-style");
		if (value != null) {
			borderSize.top += ";" + value.getValueString();
		}

		value = cssNode.getValueOf("border-right-color");
		if (value != null) {
			borderSize.right += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-right-width");
		if (value != null) {
			borderSize.right += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-right-style");
		if (value != null) {
			borderSize.right += ";" + value.getValueString();
		}

		value = cssNode.getValueOf("border-bottom-color");
		if (value != null) {
			borderSize.bottom += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-bottom-width");
		if (value != null) {
			borderSize.bottom += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-bottom-style");
		if (value != null) {
			borderSize.bottom += ";" + value.getValueString();
		}

		value = cssNode.getValueOf("border-left-color");
		if (value != null) {
			borderSize.left += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-left-width");
		if (value != null) {
			borderSize.left += ";" + value.getValueString();
		}
		value = cssNode.getValueOf("border-left-style");
		if (value != null) {
			borderSize.left += ";" + value.getValueString();
		}
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

	public float getLineHeight() {
		return lineHeight;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	public String getBackgroundPosition() {
		return backgroundPosition;
	}

	public Border getBorderSize() {
		return borderSize;
	}

	public CssStyleNode getCssNode() {
		return cssNode;
	}

	public float getCssWidth() {
		return cssWidth;
	}

	public float getCssHeight() {
		return cssHeight;
	}

	public String getBorder() {
		return border;
	}

}
