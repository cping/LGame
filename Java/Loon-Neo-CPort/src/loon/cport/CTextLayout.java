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

import java.util.HashMap;

import loon.LSystem;
import loon.cport.bridge.STBFont;
import loon.cport.bridge.STBFont.VMetric;
import loon.font.Font;
import loon.font.TextFormat;
import loon.geom.RectBox;
import loon.geom.RectI;
import loon.utils.MathUtils;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

public class CTextLayout extends loon.font.TextLayout {

	private final static int[] STYLE_TO_STBFONT = { 0, 1, 2, 4, 8 };

	private final static HashMap<String, STBFont> _fontPools = new HashMap<String, STBFont>();

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
				fontCache = STBFont.create(fontName);
			} else {
				fontCache = STBFont.create(fontName + ".ttf", fontName, STYLE_TO_STBFONT[loonFont.style.ordinal()]);
			}
			_fontPools.put(keyName, fontCache);
		}
		return fontCache;
	}

	private STBFont _stbFont;

	private float _fontSize;

	private VMetric _metrics;

	protected CTextLayout(String text, TextFormat format) {
		super(text, format, new RectBox(), 0);
		this._stbFont = convertLoonFontToSTBFont(format.font);
		this._fontSize = format.font.size;
		this._metrics = _stbFont.getFontVMetrics(_fontSize);
		final RectI size = _stbFont.getStringSize(_fontSize, text);
		this.setBounds(0, 0, size.getWidth(), size.getHeight());
		this.setHeight(_metrics.ascent + _metrics.descent);
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
		return (int) MathUtils.max(_fontSize, _stbFont.getStringSize(_fontSize, text).width);
	}

	@Override
	public int getHeight() {
		return (int) MathUtils.min(_fontSize, size.getHeight());
	}

	@Override
	public int charWidth(char ch) {
		return _stbFont.getCharSize(ch, ch).width;
	}
}
