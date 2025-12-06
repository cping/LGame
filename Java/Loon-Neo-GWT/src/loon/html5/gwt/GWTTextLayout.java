/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;

import java.util.ArrayList;
import java.util.List;

import loon.LSystem;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.RectBox;
import loon.utils.MathUtils;

final class GWTTextLayout extends TextLayout {

	private final GWTFontMetrics _metrics;

	private final Context2d _ctx;

	private static int getTextWidth(Context2d ctx, TextFormat format, String message) {
		return MathUtils.min(((int) format.font.size * message.length()),
				fixFontSize(format, ctx.measureText(message).getWidth()));
	}

	private static int fixFontSize(TextFormat format, double size) {
		int result = (int) Math.round(size);
		int fontSize = (int) (format == null ? LSystem.getFontSize() : format.font.size);
		if (MathUtils.isOdd(result) && result < fontSize) {
			result += 1;
		}
		return result;
	}

	public static TextLayout layoutText(GWTGraphics gfx, Context2d ctx, String text, TextFormat format) {
		GWTFontMetrics metrics = gfx.getFontMetrics(getFont(format));
		configContext(ctx, format);
		float width = getTextWidth(ctx, format, text);
		return new GWTTextLayout(ctx, text, format, metrics, width);
	}

	public static TextLayout[] layoutText(GWTGraphics gfx, Context2d ctx, String text, TextFormat format,
			TextWrap wrap) {
		GWTFontMetrics metrics = gfx.getFontMetrics(getFont(format));
		configContext(ctx, format);
		List<TextLayout> layouts = new ArrayList<TextLayout>();
		text = normalizeEOL(text);
		for (String line : text.split("\\n")) {
			String[] words = line.split("\\s");
			for (int idx = 0; idx < words.length;) {
				idx = measureLine(ctx, format, wrap, metrics, words, idx, layouts);
			}
		}
		return layouts.toArray(new TextLayout[layouts.size()]);
	}

	GWTTextLayout(Context2d ctx, String text, TextFormat format, GWTFontMetrics metrics, float width) {
		super(text, format, new RectBox(0, 0, metrics.adjustWidth(width), metrics.height),
				metrics.ascent() + metrics.descent());
		this._ctx = ctx;
		this._metrics = metrics;
	}

	@Override
	public float ascent() {
		return _metrics.ascent();
	}

	@Override
	public float descent() {
		return _metrics.descent();
	}

	@Override
	public float leading() {
		return _metrics.leading();
	}

	void stroke(Context2d ctx, float x, float y) {
		configContext(ctx, format);
		ctx.strokeText(text, x, y);
	}

	void fill(Context2d ctx, float x, float y) {
		configContext(ctx, format);
		ctx.fillText(text, x, y);
	}

	static void configContext(Context2d ctx, TextFormat format) {
		Font font = getFont(format);
		ctx.setFont(GWTFont.toCSS(font));
		ctx.setTextBaseline(Context2d.TextBaseline.TOP);
		ctx.setTextAlign(Context2d.TextAlign.LEFT);
	}

	static Font getFont(TextFormat format) {
		return (format.font == null) ? GWTFont.DEFAULT : format.font;
	}

	static int measureLine(Context2d ctx, TextFormat format, TextWrap wrap, GWTFontMetrics metrics, String[] words,
			int idx, List<TextLayout> layouts) {
		String line = words[idx++];
		int startIdx = idx;
		for (; idx < words.length; idx++) {
			String nline = line + " " + words[idx];
			if (nline.length() * metrics.emwidth > wrap.width) {
				break;
			}
			line = nline;
		}
		int lineWidth = getTextWidth(ctx, format, line);
		if (lineWidth < wrap.width) {
			for (; idx < words.length; idx++) {
				String nline = line + " " + words[idx];
				int nlineWidth = getTextWidth(ctx, format, nline);
				if (nlineWidth > wrap.width) {
					break;
				}
				line = nline;
				lineWidth = nlineWidth;
			}
		}

		while (lineWidth > wrap.width && idx > (startIdx + 1)) {
			line = line.substring(0, line.length() - words[--idx].length() - 1);
			lineWidth = getTextWidth(ctx, format, line);
		}

		if (lineWidth > wrap.width) {
			StringBuilder remainder = new StringBuilder();
			while (lineWidth > wrap.width && line.length() > 1) {
				int lastIdx = line.length() - 1;
				remainder.insert(0, line.charAt(lastIdx));
				line = line.substring(0, lastIdx);
				lineWidth = getTextWidth(ctx, format, line);
			}
			words[--idx] = remainder.toString();
		}

		layouts.add(new GWTTextLayout(ctx, line, format, metrics, lineWidth));
		return idx;
	}

	public static Element getProperty(String font, String str) {
		Element e = DOM.createSpan();
		e.setInnerText(str);
		e.getStyle().setProperty("font", font);
		return e;
	}

	@Override
	public int stringWidth(String message) {
		if (_ctx != null) {
			return fixFontSize(format, getTextWidth(_ctx, format, message));
		}
		return getProperty(message, GWTFont.toCSS(format.font)).getOffsetWidth();
	}

	@Override
	public int getHeight() {
		if (_ctx != null) {
			return MathUtils.min((format == null ? LSystem.getFontSize() : (int) format.font.size),
					fixFontSize(format, _ctx.measureText(String.valueOf("H")).getWidth() * 2));
		}
		return getProperty("H", GWTFont.toCSS(format.font)).getOffsetHeight();
	}

	@Override
	public int charWidth(char ch) {
		if (_ctx != null) {
			return fixFontSize(format, _ctx.measureText(String.valueOf(ch)).getWidth());
		}
		return getProperty(String.valueOf(ch), GWTFont.toCSS(format.font)).getOffsetWidth();
	}
}
