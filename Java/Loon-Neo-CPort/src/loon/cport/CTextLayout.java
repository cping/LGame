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
package loon.cport;

import java.util.ArrayList;
import java.util.List;

import loon.LSystem;
import loon.canvas.LColor;
import loon.canvas.Pixmap;
import loon.cport.bridge.STBFont;
import loon.cport.bridge.STBFont.VMetric;
import loon.cport.bridge.STBFontCache;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class CTextLayout extends loon.font.TextLayout {

	private final static int MAX_FIX_FONT = 20;

	private static int getTextWidth(STBFont font, TextFormat format, String message) {
		return MathUtils.min(((int) format.font.size * message.length()),
				fixFontSize(format, font.getStringSize(format.font.size, message).getWidth()));
	}

	private static int fixFontSize(TextFormat format, double size) {
		int result = (int) Math.round(size);
		int fontSize = (int) (format == null ? LSystem.getFontSize() : format.font.size);
		if (fontSize < MAX_FIX_FONT && MathUtils.isOdd(result) && result < fontSize) {
			result += 1;
		}
		return result;
	}

	private final static ObjectMap<String, STBFont> _fontPools = new ObjectMap<String, STBFont>();
	private final static int[] STYLE_TO_STBFONT = { 0, 1, 2, 4, 8 };

	protected static void putSTBFont(String name, STBFont font) {
		if (_fontPools.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			_fontPools.clear();
		}
		_fontPools.put(name, font);
	}

	protected static STBFont convertLoonFontToSTBFont(Font loonFont) {
		if (_fontPools.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			_fontPools.clear();
		}
		final String fontName = loonFont.name;
		final String keyName = fontName + loonFont.style.ordinal() + loonFont.size;
		STBFont fontCache = _fontPools.get(keyName);
		if (fontCache == null) {
			final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
			if ("ttf".equals(ext) || !StringUtils.isEmpty(ext)) {
				fontCache = STBFontCache.create(fontName);
			} else {
				fontCache = STBFontCache.create(fontName + ".ttf", fontName,
						STYLE_TO_STBFONT[loonFont.style.ordinal()]);
			}
			_fontPools.put(keyName, fontCache);
		}
		return fontCache;
	}

	public static TextLayout[] layoutText(String text, TextFormat format, TextWrap wrap) {
		STBFont stbFont = convertLoonFontToSTBFont(format.font);
		ArrayList<TextLayout> layouts = new ArrayList<TextLayout>();
		text = normalizeEOL(text);
		for (String line : text.split("\\n")) {
			String[] words = line.split("\\s");
			for (int idx = 0; idx < words.length;) {
				idx = measureLine(stbFont, format, wrap, stbFont.getFontVMetrics(format.font.size), words, idx,
						layouts);
			}
		}
		return layouts.toArray(new TextLayout[layouts.size()]);
	}

	static int measureLine(STBFont stbFont, TextFormat format, TextWrap wrap, VMetric metrics, String[] words, int idx,
			List<TextLayout> layouts) {
		String line = words[idx++];
		int startIdx = idx;
		int emwidth = (metrics.ascent - metrics.descent + metrics.lineGap);
		for (; idx < words.length; idx++) {
			String nline = line + " " + words[idx];
			if (nline.length() * emwidth > wrap.width) {
				break;
			}
			line = nline;
		}
		int lineWidth = getTextWidth(stbFont, format, line);
		if (lineWidth < wrap.width) {
			for (; idx < words.length; idx++) {
				String nline = line + " " + words[idx];
				int nlineWidth = getTextWidth(stbFont, format, nline);
				if (nlineWidth > wrap.width) {
					break;
				}
				line = nline;
				lineWidth = nlineWidth;
			}
		}

		while (lineWidth > wrap.width && idx > (startIdx + 1)) {
			line = line.substring(0, line.length() - words[--idx].length() - 1);
			lineWidth = getTextWidth(stbFont, format, line);
		}

		if (lineWidth > wrap.width) {
			final StringBuilder remainder = new StringBuilder();
			while (lineWidth > wrap.width && line.length() > 1) {
				int lastIdx = line.length() - 1;
				remainder.insert(0, line.charAt(lastIdx));
				line = line.substring(0, lastIdx);
				lineWidth = getTextWidth(stbFont, format, line);
			}
			words[--idx] = remainder.toString();
		}
		layouts.add(new CTextLayout(line, format, lineWidth));
		return idx;
	}

	public static TextLayout layoutText(String text, TextFormat format) {
		return new CTextLayout(text, format);
	}

	private final CPixmapFont _pixmapFont;

	private final STBFont _stbFont;

	private final VMetric _metrics;

	private final float _fontSize;

	protected CTextLayout(String text, TextFormat format) {
		this(text, format, 0);
	}

	protected CTextLayout(String text, TextFormat format, int width) {
		super(text, format, new RectBox(), 0);
		_stbFont = convertLoonFontToSTBFont(format.font);
		_pixmapFont = new CPixmapFont(_stbFont);
		_fontSize = format.font.size;
		_metrics = _stbFont.getFontVMetrics(_fontSize);
		setBounds(0, 0, width == 0 ? _stbFont.measureWidth(text, _fontSize) : width,
				_stbFont.measureHieght(text, _fontSize));
		setHeight(_metrics.ascent - _metrics.descent + _metrics.lineGap);
	}

	void stroke(Pixmap pixmap, float x, float y, LColor fontColor) {
		drawText(pixmap, text, MathUtils.ifloor(x), MathUtils.ifloor(y), fontColor);
	}

	void fill(Pixmap pixmap, float x, float y, LColor fontColor) {
		drawText(pixmap, text, MathUtils.ifloor(x), MathUtils.ifloor(y), fontColor);
	}

	public void drawText(Pixmap pixmap, String message, int x, int y, LColor fontColor) {
		if (pixmap == null) {
			return;
		}
		pixmap.drawPixmap(_pixmapFont.textToPixmap(message, _fontSize, fontColor), x, y);
	}

	public void drawChar(Pixmap pixmap, int point, int x, int y, LColor fontColor) {
		if (pixmap == null) {
			return;
		}
		pixmap.drawPixmap(_pixmapFont.charToPixmap(point, _fontSize, fontColor), x, y);
	}

	@Override
	public float ascent() {
		return _metrics.ascent;
	}

	@Override
	public float descent() {
		return _metrics.descent;
	}

	@Override
	public float leading() {
		return _metrics.lineGap;
	}

	@Override
	public int stringWidth(String message) {
		return (int) MathUtils.max(_fontSize, _stbFont.measureWidth(text, _fontSize));
	}

	@Override
	public int getHeight() {
		return (int) MathUtils.min(_fontSize, size.getHeight());
	}

	@Override
	public int charWidth(char ch) {
		return _stbFont.getCharSize(_fontSize, ch).width;
	}
}
