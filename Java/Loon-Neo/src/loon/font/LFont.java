package loon.font;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.geom.PointI;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.LSTRDictionary;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;

public class LFont implements IFont {

	private static LFont defaultFont;

	public static LFont newFont() {
		return newFont(20);
	}

	public static LFont newFont(int size) {
		return LFont.getFont(LSystem.FONT_NAME, Style.PLAIN, size);
	}

	public static LFont getDefaultFont() {
		return getDefaultFont(20);
	}

	public static LFont getDefaultFont(int size) {
		if (defaultFont == null || defaultFont.getSize() != size) {
			defaultFont = LFont.getFont(LSystem.FONT_NAME, Style.PLAIN, size);
		}
		return defaultFont;
	}

	public static void setDefaultFont(LFont font) {
		defaultFont = font;
	}

	private ObjectMap<String, Vector2f> fontSizes = new ObjectMap<String, Vector2f>(
			50);

	private final static String tmp = "H";

	private String lastText = tmp;

	private PointI _offset = new PointI();

	private TextFormat textFormat = null;

	private TextLayout textLayout = null;

	private int _size = -1;

	private float _ascent = -1;

	private boolean useCache;

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean u) {
		this.useCache = u;
	}

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

	public static LFont getFont(String familyName, int styleType, int size) {
		Style style = Style.PLAIN;
		switch (styleType) {
		default:
		case 0:
			style = Style.PLAIN;
			break;
		case 1:
			style = Style.BOLD;
			break;
		case 2:
			style = Style.ITALIC;
			break;
		case 3:
			style = Style.BOLD_ITALIC;
			break;
		}
		return new LFont(familyName, style, size, true);
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
	public void drawString(GLEx g, String string, float tx, float ty) {
		drawString(g, string, tx, ty, LColor.white);
	}

	@Override
	public void drawString(GLEx g, String string, float tx, float ty, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		if (useCache) {
			LSTRDictionary.get().drawString(this, string, _offset.x + tx,
					_offset.y + ty, 0, c);
		} else {
			LSTRDictionary.get().drawString(g, this, string, _offset.x + tx,
					_offset.y + ty, 0, c);
		}
	}

	@Override
	public void drawString(GLEx g, String string, float tx, float ty,
			float angle, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		if (useCache) {
			LSTRDictionary.get().drawString(this, string, _offset.x + tx,
					_offset.y + ty, angle, c);
		} else {
			LSTRDictionary.get().drawString(g, this, string, _offset.x + tx,
					_offset.y + ty, angle, c);
		}
	}

	@Override
	public void drawString(GLEx g, String string, float tx, float ty, float sx,
			float sy, float ax, float ay, float angle, LColor c) {
		if (c == null || c.a <= 0.01) {
			return;
		}
		if (StringUtils.isEmpty(string)) {
			return;
		}
		if (useCache) {
			LSTRDictionary.get().drawString(this, string, _offset.x + tx,
					_offset.y + ty, sx, sy, ax, ay, angle, c);
		} else {
			LSTRDictionary.get().drawString(g, this, string, _offset.x + tx,
					_offset.y + ty, sx, sy, ax, ay, angle, c);
		}
	}

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
		if (message.indexOf('\n') == -1) {
			return textLayout.stringWidth(message);
		} else {
			StringBuffer sbr = new StringBuffer();
			int width = 0;
			for (int i = 0, size = message.length(); i < size; i++) {
				char ch = message.charAt(i);
				if (ch == '\n') {
					width = MathUtils.max(
							textLayout.stringWidth(sbr.toString()), width);
					sbr.delete(0, sbr.length());
				} else {
					sbr.append(ch);
				}
			}

			return width;
		}
	}

	public int charHeight(char ch) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(String.valueOf(ch));
		return getHeight();
	}

	public int stringHeight(String message) {
		if (LSystem.base() == null) {
			return 0;
		}
		initLayout(message);
		if (message.indexOf('\n') == -1) {
			return getHeight();
		} else {
			String[] list = StringUtils.split(message, '\n');
			return list.length * getHeight();
		}
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
		return this._size == -1 ? (int) textFormat.font.size : this._size;
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
		return MathUtils.max(getSize(), textLayout.bounds.height);
	}

	@Override
	public float getAscent() {
		initLayout(tmp);
		return this._ascent == -1 ? textLayout.ascent() : this._ascent;
	}

	public float getDescent() {
		initLayout(tmp);
		return textLayout.descent();
	}

	public float getLeading() {
		initLayout(tmp);
		return textLayout.leading();
	}

	private int hash = 1;

	@Override
	public int hashCode() {
		if (hash == 1) {
			hash = LSystem.unite(textFormat.font.name.length(), hash);
			hash = LSystem.unite(textFormat.font.name.hashCode(), hash);
			hash = LSystem.unite(textFormat.font.style.ordinal(), hash);
			hash = LSystem.unite((int) textFormat.font.size, hash);
		}
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof LFont)) {
			return false;
		}
		LFont font = (LFont) o;
		if (this == font) {
			return true;
		}
		if (hashCode() == font.hashCode()) {
			return true;
		}
		if (font.textFormat == textFormat) {
			return true;
		}
		if (font.textFormat.font.name.equals(textFormat.font.name)
				&& font.textFormat.font.size == textFormat.font.size
				&& font.textFormat.font.style.equals(textFormat.font.style)) {
			return true;
		}
		return false;
	}

	public Vector2f getOrigin(String text) {
		Vector2f result = fontSizes.get(text);
		if (result == null) {
			result = new Vector2f(stringWidth(text) / 2f, getHeight() / 2f);
			fontSizes.put(text, result);
		}
		return result;
	}

	public TextLayout getLayoutText(String text) {
		return LSystem.base().graphics().layoutText(text, this.textFormat);
	}

	@Override
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

	@Override
	public PointI getOffset() {
		return _offset;
	}

	@Override
	public void setOffset(PointI val) {
		_offset.set(val);
	}

	@Override
	public void setOffsetX(int x) {
		_offset.x = x;
	}

	@Override
	public void setOffsetY(int y) {
		_offset.y = y;
	}

	@Override
	public void setAssent(float assent) {
		this._ascent = assent;
	}

	@Override
	public void setSize(int size) {
		this._size = size;
	}

	@Override
	public String toString() {
		return textFormat.toString();
	}
}
