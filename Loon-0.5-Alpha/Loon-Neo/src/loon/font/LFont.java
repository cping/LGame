package loon.font;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.StringUtils;

public class LFont implements IFont {

	private static LFont defaultFont;

	public static LFont getDefaultFont() {
		if (defaultFont == null) {
			defaultFont = LFont.getFont(LSystem.FONT_NAME, Style.PLAIN, 20);
		}
		return defaultFont;
	}

	public static void setDefaultFont(LFont font) {
		defaultFont = font;
	}

	private final static String tmp = "H";

	private TextFormat textFormat = null;

	private TextLayout textLayout = null;

	LFont() {
		this(LSystem.FONT_NAME, Style.PLAIN, 20, true);
	}

	LFont(String name, Style style, int size, boolean antialias) {
		this.textFormat = new TextFormat(new Font(name, style, size), antialias);
	}

	public static LFont getFont(int size) {
		return LFont.getFont(LSystem.FONT_NAME, size);
	}

	public static LFont getFont(String familyName, int size) {
		return new LFont(familyName, Style.PLAIN, size, true);
	}

	public static LFont getFont(String familyName, Style style, int size) {
		return new LFont(familyName, style, size, true);
	}

	public static LFont getFont(String familyName, Style style, int size,
			boolean antialias) {
		return new LFont(familyName, style, size, antialias);
	}

	public TextFormat getFormat() {
		return textFormat;
	}

	public TextLayout getTextLayout() {
		return textLayout;
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y) {
		drawString(g, string, x, y, LColor.white);
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		LSTRDictionary.drawString(g, this, string, x, y, 0, c);
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y,
			float angle, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		LSTRDictionary.drawString(g, this, string, x, y, angle, c);
	}

	@Override
	public void drawString(GLEx g, String string, float x, float y, float sx,
			float sy, float ax, float ay, float angle, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		LSTRDictionary.drawString(g, this, string, x, y, sx, sy, ax, ay, angle,
				c);
	}

	private String lastText = tmp;

	private void initLayout(String text) {
		if (textLayout == null || !text.equals(lastText)) {
			textLayout = LSystem.base().graphics()
					.layoutText(tmp, this.textFormat);
		}
	}

	public int charWidth(char ch) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(String.valueOf(ch));
		return textLayout.bounds.width;
	}

	public int stringWidth(String message) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(message);
		return textLayout.stringWidth(message);
	}

	public int charHeight(char ch) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(String.valueOf(ch));
		return textLayout.bounds.height;
	}

	public int stringHeight(String message) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(message);
		return textLayout.bounds.height;
	}

	public boolean isBold() {
		return textFormat.font.style == Style.BOLD;
	}

	public boolean isItalic() {
		return textFormat.font.style == Style.ITALIC;
	}

	public boolean isPlain() {
		return textFormat.font.style == Style.PLAIN;
	}

	public int getSize() {
		return (int) textFormat.font.size;
	}

	public int getStyle() {
		return textFormat.font.style.ordinal();
	}

	public String getFontName() {
		return textFormat.font.name;
	}

	@Override
	public int getHeight() {
		initLayout(tmp);
		return textLayout.bounds.height;
	}

	@Override
	public float getAscent() {
		initLayout(tmp);
		return textLayout.ascent();
	}

	public float getDescent() {
		initLayout(tmp);
		return textLayout.descent();
	}

	public float getLeading() {
		initLayout(tmp);
		return textLayout.leading();
	}

	public TextLayout getLayoutText(String text) {
		return LSystem.base().graphics().layoutText(text, this.textFormat);
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

}
