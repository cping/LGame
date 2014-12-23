
/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.core.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import loon.core.LSystem;
import loon.core.geom.Vector2f;
import loon.core.resource.Resources;

public class LFont {

	private static LFont defaultFont = LFont.getFont("Dialog", 0, 20);

	public static final int STYLE_PLAIN = 0;

	public static final int STYLE_BOLD = 1;

	public static final int STYLE_ITALIC = 2;

	public static final int STYLE_UNDERLINED = 4;

	public static final int SIZE_SMALL = 8;

	public static final int SIZE_MEDIUM = 0;

	public static final int SIZE_LARGE = 16;

	public static final int FACE_MONOSPACE = 32;

	public static final int FACE_PROPORTIONAL = 64;

	public static final int FACE_SYSTEM = 0;

	public static final int FONT_STATIC_TEXT = 0;

	public static final int FONT_INPUT_TEXT = 1;

	private final static Graphics2D g2d = (Graphics2D) new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_ARGB).getGraphics();

	private String name;

	private int style;

	private int size;

	private boolean antialiasing;

	private boolean initialized;

	private FontMetrics fontMetrics;

	final private static HashMap<String, LFont> fonts = new HashMap<String, LFont>(
			10);

	private HashMap<String, Vector2f> fontSizes = new HashMap<String, Vector2f>(
			50);

	public Vector2f getOrigin(String text) {
		Vector2f result = fontSizes.get(text);
		if (result == null) {
			result = new Vector2f(stringWidth(text) / 2f, getHeight() / 2f);
		}
		return result;
	}

	public static LFont getAssetsFont(String file, int style, int size) {
		try {
			String name = "assets" + (file + style + size).toLowerCase();
			LFont o = fonts.get(name);
			if (o == null) {
				o = new LFont();
				o.name = file;
				o.style = style;
				o.size = size;
				o.antialiasing = true;
				if (o.antialiasing) {
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				} else {
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				}
				Font trueFont = Font.createFont(0,
						Resources.openResource("assets/" + file));
				Font baseFont = trueFont.deriveFont(style, size);
				o.fontMetrics = g2d.getFontMetrics(baseFont);
				o.initialized = true;
				fonts.put(name, o);
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public static LFont getFileFont(String file, int style, int size) {
		try {
			String name = "file" + (file + style + size).toLowerCase();
			LFont o = fonts.get(name);
			if (o == null) {
				o = new LFont();
				o.name = file;
				o.style = style;
				o.size = size;
				o.antialiasing = true;
				if (o.antialiasing) {
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				} else {
					g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				}
				Font trueFont = Font
						.createFont(0, Resources.openResource(file));
				Font baseFont = trueFont.deriveFont(style, size);
				o.fontMetrics = g2d.getFontMetrics(baseFont);
				o.initialized = true;
				fonts.put(name, o);
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public static LFont getFont(int size) {
		return LFont.getFont(LSystem.FONT_NAME, 0, size);
	}

	public static LFont getFont(String familyName, int size) {
		return getFont(familyName, 0, size);
	}

	public LFont(int face, int style, int size) {
		this.style = style;
		this.size = size;
		switch (face) {
		case FACE_SYSTEM:
			this.name = "system";
			break;
		case FACE_PROPORTIONAL:
			this.name = "proportional";
			break;
		case FACE_MONOSPACE:
			this.name = "monospace";
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	LFont() {

	}

	public LFont(Font font) {
		this.name = font.getFontName();
		this.style = font.getStyle();
		this.size = font.getSize();
		this.antialiasing = true;
		this.initialized = false;
	}

	public LFont(String name, int style, int size, boolean antialiasing) {
		this.name = name;
		this.style = style;
		this.size = size;
		this.antialiasing = antialiasing;
		this.initialized = false;
	}

	public LFont(String name, int style, int size) {
		this(name, style, size, false);
	}

	public void setAntialiasing(boolean antialiasing) {
		if (this.antialiasing != antialiasing) {
			this.antialiasing = antialiasing;
			initialized = false;
		}
	}

	public String getFontName() {
		return this.name;
	}

	public int charWidth(char ch) {
		checkInitialized();
		return fontMetrics.charWidth(ch);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		checkInitialized();
		return fontMetrics.charsWidth(ch, offset, length);
	}

	public int getBaselinePosition() {
		checkInitialized();
		return fontMetrics.getAscent();
	}

	public int getLineHeight() {
		return getAscent() + getDescent();
	}

	public int getHeight() {
		checkInitialized();
		return fontMetrics.getHeight();
	}

	public int getDescent() {
		checkInitialized();
		return fontMetrics.getDescent();
	}

	public int stringWidth(String str) {
		checkInitialized();
		return fontMetrics.stringWidth(str);
	}

	public int subStringWidth(String str, int offset, int count) {
		checkInitialized();
		return fontMetrics.stringWidth(str.substring(offset, offset + count));
	}

	public static LFont getDefaultFont() {
		return defaultFont;
	}

	public static void setDefaultFont(LFont font) {
		defaultFont = font;
	}

	public static LFont getFont(int face, int style, int size) {
		return new LFont(face, style, size);
	}

	public static LFont getFont(String name, int style, int size) {
		return new LFont(name, style, size);
	}

	public Font getFont() {
		checkInitialized();
		return fontMetrics.getFont();
	}

	public int getSize() {
		return size;
	}

	public int getStyle() {
		return style;
	}

	public boolean isBold() {
		return (style & STYLE_BOLD) != 0;
	}

	public boolean isUnderlined() {
		return (style & STYLE_UNDERLINED) != 0;
	}

	public boolean isItalic() {
		return (style & STYLE_ITALIC) != 0;
	}

	public boolean isPlain() {
		return style == 0;
	}

	public int getAscent() {
		checkInitialized();
		return fontMetrics.getAscent();
	}

	public String confineLength(String s, int width) {
		int length = 0;
		for (int i = 0; i < s.length(); i++) {
			length += stringWidth(String.valueOf(s.charAt(i)));
			if (length >= width) {
				int pLength = stringWidth("...");
				while (length + pLength >= width && i >= 0) {
					length -= stringWidth(String.valueOf(s.charAt(i)));
					i--;
				}
				s = s.substring(0, ++i) + "...";
				break;
			}
		}
		return s;
	}

	private synchronized void checkInitialized() {
		if (!initialized) {
			if (antialiasing) {
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			} else {
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			fontMetrics = g2d.getFontMetrics(new Font(name, style, size));
			initialized = true;
		}
	}

}
