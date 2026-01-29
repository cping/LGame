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
import loon.cport.bridge.STBCall;
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

	protected final static String getSystemFontName(final String fontName) {
		if (StringUtils.isEnglishAndNumeric(fontName)) {
			return fontName;
		}
		if ("微软雅黑".equals(fontName)) {
			return "Microsoft YaHei";
		} else if ("宋体".equals(fontName)) {
			return "SimSun";
		} else if ("黑体".equals(fontName)) {
			return "SimHei";
		} else if ("仿宋".equals(fontName)) {
			return "SimFang";
		} else if ("楷体".equals(fontName)) {
			return "SimKai";
		} else if ("隶书".equals(fontName)) {
			return "LiSu";
		} else if ("幼圆".equals(fontName)) {
			return "YouYuan";
		} else if ("华文细黑".equals(fontName)) {
			return "STXihei";
		} else if ("华文黑体".equals(fontName)) {
			return "STHeiti";
		} else if ("华文华文楷体黑".equals(fontName)) {
			return "STKaiti";
		} else if ("华文宋体".equals(fontName)) {
			return "STSong";
		} else if ("华文中宋".equals(fontName)) {
			return "STZhongsong";
		} else if ("华文仿宋".equals(fontName)) {
			return "STFangsong";
		} else if ("方正舒体".equals(fontName)) {
			return "FZShuTi";
		} else if ("方正姚体".equals(fontName)) {
			return "FZYaoti";
		} else if ("华文彩云".equals(fontName)) {
			return "STCaiyun";
		} else if ("华文琥珀".equals(fontName)) {
			return "STHupo";
		} else if ("华文隶书".equals(fontName)) {
			return "STLiti";
		} else if ("华文行楷".equals(fontName)) {
			return "STXingkai";
		} else if ("华文新魏".equals(fontName)) {
			return "STXinwei";
		} else if ("苹方".equals(fontName)) {
			return "PingFang";
		} else if ("Arial黑体".equals(fontName)) {
			return "Arial Black";
		} else if ("Times新罗马".equals(fontName)) {
			return "Times New Roman";
		} else if ("游明朝".equals(fontName)) {
			return "Yu Mincho";
		} else if ("游ゴシック".equals(fontName)) {
			return "Yu Gothic";
		} else if ("メイリオ".equals(fontName)) {
			return "Meiryo";
		} else if ("ヒラギノ角ゴ".equals(fontName)) {
			return "Hiragino Kaku Gothic Pro";
		} else if ("ヒラギノ明朝".equals(fontName)) {
			return "Hiragino Mincho Pro";
		} else if ("ＭＳ ゴシック".equals(fontName)) {
			return "MS Gothic";
		} else if ("ＭＳ 明朝".equals(fontName)) {
			return "MS Mincho";
		} else if ("맑은 고딕".equals(fontName)) {
			return "Malgun Gothic";
		} else if ("굴림".equals(fontName)) {
			return "Gulim";
		} else if ("돋움".equals(fontName)) {
			return "Dotum";
		} else if ("바탕".equals(fontName)) {
			return "Batang";
		} else if ("HY견고딕".equals(fontName)) {
			return "HYgothic";
		} else if ("HY중고딕".equals(fontName)) {
			return "HYHeadLine";
		} else if ("HY신명조".equals(fontName)) {
			return "HYMyeongJo";
		} else {
			return "sans-serif";
		}
	}

	protected static void inputDialog(STBFont font, int dialogType, int width, int height, String title, String text,
			String textA, String textB) {
		if (font != null && !font.isClosed()) {
			STBCall.inputDialog(font.getHandle(), dialogType, width, height, title, text, textA, textB);
		} else {
			STBCall.inputDialog(0, dialogType, width, height, title, text, textA, textB);
		}
	}

	protected static void inputDialog(int dialogType, int width, int height, String title, String text, String textA,
			String textB) {
		if (_fontPools.size() > 0) {
			inputDialog(_fontPools.values().next(), dialogType, width, height, title, text, textA, textB);
		} else {
			STBCall.inputDialog(0, dialogType, width, height, title, text, textA, textB);
		}
	}

	private final static int MAX_FIX_FONT = 20;

	private static int getTextWidth(STBFont font, TextFormat format, String message) {
		return MathUtils.min(((int) format.font.size * message.length()),
				fixFontSize(format, font.measureWidth(message, format.font.size)));
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
				fontCache = STBFontCache.create(fontName, PathUtils.getBaseFileName(fontName),
						STYLE_TO_STBFONT[loonFont.style.ordinal()]);
			} else {
				String sysFontName = fontName;
				if ("dialog".equalsIgnoreCase(fontName) || "default".equalsIgnoreCase(fontName)) {
					sysFontName = "SimHei";
				} else {
					sysFontName = getSystemFontName(sysFontName);
				}
				fontCache = STBFontCache.createSystemFont(sysFontName, fontName + ".ttf", sysFontName,
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
		setHeight(MathUtils.ifloor(_metrics.ascent - _metrics.descent + _metrics.lineGap));
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
	public int stringWidth(final String message) {
		if (message == null) {
			return 0;
		}
		return MathUtils.iceil(MathUtils.max(_fontSize, _stbFont.measureWidth(message, _fontSize))) + message.length();
	}

	@Override
	public int getHeight() {
		return MathUtils.iceil(MathUtils.min(_fontSize, size.getHeight()));
	}

	@Override
	public int charWidth(char ch) {
		return _stbFont.getCharSize(_fontSize, ch).width;
	}
}
