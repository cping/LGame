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
package loon.javase;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import loon.BaseIO;
import loon.LSystem;
import loon.font.Font;
import loon.font.TextFormat;
import loon.font.TextWrap;
import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.PathUtils;

final class JavaSETextLayout extends loon.font.TextLayout {

	private final static HashMap<String, java.awt.Font> _fontPools = new HashMap<String, java.awt.Font>();

	protected static void putAWTFont(String name, java.awt.Font font) {
		if (_fontPools.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
			_fontPools.clear();
		}
		_fontPools.put(name, font);
	}

	protected static java.awt.Font convertLoonFontToAWTFont(Font loonFont) {
		try {
			if (_fontPools.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
				_fontPools.clear();
			}
			final String fontName = loonFont.name;
			final String keyName = fontName + loonFont.style.ordinal() + loonFont.size;
			java.awt.Font fontCache = _fontPools.get(keyName);
			if (fontCache == null) {
				final String ext = PathUtils.getExtension(fontName).trim().toLowerCase();
				if ("ttf".equals(ext)) {
					java.awt.Font font = null;
					final byte[] buffer = BaseIO.loadBytes(fontName);
					if (buffer == null) {
						font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
								new FileInputStream(new File(fontName)));
					} else {
						font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new ByteArrayInputStream(buffer));
					}
					fontCache = font.deriveFont(java.awt.Font.PLAIN, (int) loonFont.size);
				} else {
					fontCache = new java.awt.Font(loonFont.name,
							JavaSELwjglGraphics.STYLE_TO_JAVA[loonFont.style.ordinal()], (int) loonFont.size);
				}
				_fontPools.put(keyName, fontCache);
			}
			return fontCache;
		} catch (Exception e) {
			return new java.awt.Font(loonFont.name, JavaSELwjglGraphics.STYLE_TO_JAVA[loonFont.style.ordinal()],
					(int) loonFont.size);
		}
	}

	public static JavaSETextLayout layoutText(JavaSEGraphics gfx, String text, TextFormat format) {
		AttributedString astring = new AttributedString(text.length() == 0 ? " " : text);
		if (format.font != null) {
			astring.addAttribute(TextAttribute.FONT, gfx.resolveFont(format.font));
		}
		FontRenderContext frc = format.antialias ? gfx.aaFontContext : gfx.aFontContext;
		return new JavaSETextLayout(text, format, new TextLayout(astring.getIterator(), frc));
	}

	private static BufferedImage _tempBufferedImage;

	private final static BufferedImage getFontImage() {
		if (_tempBufferedImage == null) {
			_tempBufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
		return _tempBufferedImage;
	}

	public static JavaSETextLayout[] layoutText(JavaSEGraphics gfx, String text, TextFormat format, TextWrap wrap) {
		text = normalizeEOL(text);
		final String ltext = text.length() == 0 ? " " : text;

		final AttributedString astring = new AttributedString(ltext);
		if (format.font != null) {
			astring.addAttribute(TextAttribute.FONT, gfx.resolveFont(format.font));
		}

		final List<JavaSETextLayout> layouts = new ArrayList<JavaSETextLayout>();
		final FontRenderContext frc = format.antialias ? gfx.aaFontContext : gfx.aFontContext;
		final LineBreakMeasurer measurer = new LineBreakMeasurer(astring.getIterator(), frc);
		int lastPos = ltext.length(), curPos = 0;
		final char eol = '\n';
		while (curPos < lastPos) {
			int nextRet = ltext.indexOf(eol, measurer.getPosition() + 1);
			if (nextRet == -1) {
				nextRet = lastPos;
			}
			final TextLayout layout = measurer.nextLayout(wrap.width, nextRet, false);
			final int endPos = measurer.getPosition();
			while (curPos < endPos && ltext.charAt(curPos) == eol) {
				curPos += 1;
			}
			layouts.add(new JavaSETextLayout(ltext.substring(curPos, endPos), format, layout));
			curPos = endPos;
		}
		return layouts.toArray(new JavaSETextLayout[layouts.size()]);
	}

	private final TextLayout layout;

	private final Graphics2D g2d;

	private FontMetrics fontMetrics;

	JavaSETextLayout(String text, TextFormat format, TextLayout layout) {
		super(text, format, computeBounds(layout), layout.getAscent() + layout.getDescent());
		this.g2d = (Graphics2D) getFontImage().getGraphics();
		this.fontMetrics = g2d.getFontMetrics(convertLoonFontToAWTFont(format.font));
		this.layout = layout;
	}

	@Override
	public float ascent() {
		return layout.getAscent();
	}

	@Override
	public float descent() {
		return layout.getDescent();
	}

	@Override
	public float leading() {
		return layout.getLeading();
	}

	void stroke(Graphics2D gfx, float x, float y) {
		paint(gfx, x, y, true);
	}

	void fill(Graphics2D gfx, float x, float y) {
		paint(gfx, x, y, false);
	}

	void paint(Graphics2D gfx, float x, float y, boolean stroke) {
		final Object ohint = gfx.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		try {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					format.antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
			float yoff = y + layout.getAscent();
			if (stroke) {
				gfx.translate(x, yoff);
				gfx.draw(layout.getOutline(null));
				gfx.translate(-x, -yoff);
			} else {
				layout.draw(gfx, x, yoff);
			}
		} finally {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ohint);
		}
	}

	private final static RectBox computeBounds(TextLayout layout) {
		final Rectangle2D bounds = layout.getBounds();
		return new RectBox(MathUtils.floor(bounds.getX()), MathUtils.floor(bounds.getY()) + layout.getAscent(),
				MathUtils.floor(bounds.getWidth()), MathUtils.floor(bounds.getHeight() + layout.getDescent()));
	}

	public int charWidth(char ch) {
		return fontMetrics.charWidth(ch);
	}

	@Override
	public int stringWidth(String message) {
		return fontMetrics.stringWidth(message);
	}

	@Override
	public int getHeight() {
		return fontMetrics.getHeight();
	}
}
