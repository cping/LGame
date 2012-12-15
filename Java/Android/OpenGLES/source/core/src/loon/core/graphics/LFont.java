package loon.core.graphics;

import java.util.HashMap;

import loon.core.LSystem;
import loon.core.geom.Vector2f;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;

/**
 * 
 * Copyright 2008 - 2009
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
 * @email javachenpeng@yahoo.com
 * @version 0.1.0
 */
public class LFont {

	private static LFont defaultFont;

	public static final int LEFT = 1;

	public static final int RIGHT = 2;

	public static final int CENTER = 3;

	public static final int JUSTIFY = 4;

	final static public int FACE_SYSTEM = 0;

	final static public int FACE_MONOSPACE = 32;

	final static public int FACE_PROPORTIONAL = 64;

	final static public int FONT_STATIC_TEXT = 0;

	final static public int FONT_INPUT_TEXT = 1;

	final static public int SIZE_SMALL = 8;

	final static public int SIZE_LARGE = 16;

	final static public int SIZE_MEDIUM = 0;

	final static public int STYLE_PLAIN = 0;

	final static public int STYLE_BOLD = 1;

	final static public int STYLE_ITALIC = 2;

	final static public int STYLE_UNDERLINED = 4;

	final private static String tmp = "H";

	final private static HashMap<String, LFont> fonts = new HashMap<String, LFont>(
			10);

	final private Rect rect = new Rect();

	private Paint typefacePaint;

	private int fontSize;

	private HashMap<String, Vector2f> fontSizes = new HashMap<String, Vector2f>(50);

	public Vector2f getOrigin(String text) {
		Vector2f result = fontSizes.get(text);
		if(result==null){
			result = new Vector2f(stringWidth(text) / 2f, getHeight() / 2f);
		}
		return result;
	}
	
	public static LFont getAssetsFont(String file, int style, int size) {
		String name = "assets" + (file + style + size).toLowerCase();
		LFont o = fonts.get(name);
		if (o == null) {
			Typeface face = Typeface.createFromAsset(LSystem.getActivity()
					.getAssets(), file);
			Paint paint = new Paint();
			paint.setTypeface(face);
			paint.setTextSize(size);
			paint.setAntiAlias(true);
			fonts.put(name, o = new LFont(paint, size));
		}
		return o;
	}

	public static LFont getFileFont(String file, int style, int size) {
		String name = "file" + (file + style + size).toLowerCase();
		LFont o = fonts.get(name);
		if (o == null) {
			Typeface face = Typeface.createFromFile(file);
			Paint paint = new Paint();
			paint.setTypeface(face);
			paint.setTextSize(size);
			paint.setAntiAlias(true);
			fonts.put(name, o = new LFont(paint, size));
		}
		return o;
	}

	public static LFont getDefaultFont() {
		if (defaultFont == null) {
			defaultFont = LFont.getFont(LSystem.FONT_NAME, 0, 20);
		}
		return defaultFont;
	}

	public static void setDefaultFont(LFont font) {
		defaultFont = font;
	}

	public static LFont getFont(int size) {
		return LFont.getFont(LSystem.FONT_NAME, 0, size);
	}

	public static LFont getFont(String familyName, int size) {
		return getFont(familyName, 0, size);
	}

	public static LFont getFont(String familyName, int style, int size) {
		String name = (familyName + style + size).toLowerCase();
		LFont o = fonts.get(name);
		if (o == null) {
			if (familyName != null) {
				if (familyName.equalsIgnoreCase("Serif")
						|| familyName.equalsIgnoreCase("TimesRoman")) {
					familyName = "serif";
				} else if (familyName.equalsIgnoreCase("SansSerif")
						|| familyName.equalsIgnoreCase("Helvetica")) {
					familyName = "sans-serif";
				} else if (familyName.equalsIgnoreCase("Monospaced")
						|| familyName.equalsIgnoreCase("Courier")
						|| familyName.equalsIgnoreCase("Dialog")) {
					familyName = "monospace";
				}
			}
			Typeface face = Typeface.create(familyName, style);
			Paint paint = new Paint();
			paint.setTypeface(face);
			paint.setTextSize(size);
			paint.setAntiAlias(true);
			fonts.put(name, o = new LFont(paint, size));
		}
		return o;
	}

	public static LFont getFromAssetFont(String path, int style, int fontSize) {
		return new LFont(Typeface.DEFAULT, path, fontSize);
	}

	public static LFont getFont(int face, int style, int fontSize) {
		LFont font = new LFont(fontSize);
		return getFont(font, face, style, fontSize);
	}

	public static LFont getFont(LFont font, int face, int style, int fontSize) {
		int paintFlags = 0;
		int typefaceStyle = Typeface.NORMAL;
		Typeface base;
		switch (face) {
		case FACE_MONOSPACE:
			base = Typeface.MONOSPACE;
			break;
		case FACE_SYSTEM:
			base = Typeface.DEFAULT;
			break;
		case FACE_PROPORTIONAL:
			base = Typeface.SANS_SERIF;
			break;
		default:
			throw new IllegalArgumentException("unknown font " + face);
		}
		if ((style & STYLE_BOLD) != 0) {
			typefaceStyle |= Typeface.BOLD;
		}
		if ((style & STYLE_ITALIC) != 0) {
			typefaceStyle |= Typeface.ITALIC;
		}
		if ((style & STYLE_UNDERLINED) != 0) {
			paintFlags |= Paint.UNDERLINE_TEXT_FLAG;
		}
		Typeface typeface = Typeface.create(base, typefaceStyle);
		Paint paint = new Paint(paintFlags);
		paint.setTypeface(typeface);
		font.setTypefacePaint(paint);
		return font;
	}

	private static Paint createPaint(Typeface typeface) {
		Paint paint = new Paint();
		paint.setTypeface(typeface);
		return paint;
	}

	private LFont(int fontSize) {
		this.fontSize = fontSize;
	}

	private LFont(Typeface typeface, int fontSize) {
		this(createPaint(typeface), fontSize);
	}

	private LFont(Paint typefacePaint, int fontSize) {
		this.fontSize = fontSize;
		this.setTypefacePaint(typefacePaint);
	}

	private LFont(Typeface typeface, String path, int fontSize) {
		this(createPaint(typeface), path, fontSize);
	}

	private LFont(Paint typefacePaint, String path, int fontSize) {
		Typeface face = Typeface.createFromAsset(
				LSystem.screenActivity.getAssets(), path);
		this.fontSize = fontSize;
		this.typefacePaint.setTypeface(face);
		this.setTypefacePaint(typefacePaint);
	}

	public float getScale() {
		int fontSize = this.getSize();
		float scale;
		if (fontSize == LFont.SIZE_LARGE) {
			scale = 1.5F;
		} else if (fontSize == LFont.SIZE_SMALL) {
			scale = 0.8F;
		} else {
			scale = 1;
		}
		return scale;
	}

	public Paint getTypefacePaint() {
		return this.typefacePaint;
	}

	public float getAscent() {
		return typefacePaint.ascent();
	}

	public float getDescent() {
		return typefacePaint.descent();
	}

	public float getLeading() {
		return (typefacePaint.getFontMetrics().leading + 2) * 2;
	}

	public void setTypefacePaint(Paint typefacePaint) {
		this.typefacePaint = typefacePaint;
		this.typefacePaint.setTextSize(getSize());
	}

	public int getBaselinePosition() {
		return Math.round(-this.typefacePaint.ascent() * getSize());
	}

	public int getFace() {
		return FACE_SYSTEM;
	}

	public FontMetrics getFontMetrics() {
		return typefacePaint.getFontMetrics();
	}

	public int getLineHeight() {
		return ((int) Math.ceil(Math.abs(typefacePaint.getFontMetrics().ascent)
				+ Math.abs(typefacePaint.getFontMetrics().descent))) - 2;
	}

	public int getStyle() {
		int style = STYLE_PLAIN;
		Typeface typeface = this.typefacePaint.getTypeface();
		if (typeface.isBold()) {
			style |= STYLE_BOLD;
		}
		if (typeface.isItalic()) {
			style |= STYLE_ITALIC;
		}
		if (this.typefacePaint.isUnderlineText()) {
			style |= STYLE_UNDERLINED;
		}
		return style;
	}

	public boolean isBold() {
		return this.typefacePaint.getTypeface().isBold();
	}

	public boolean isItalic() {
		return this.typefacePaint.getTypeface().isItalic();
	}

	public boolean isPlain() {
		return this.getStyle() == STYLE_PLAIN;
	}

	public String getFontName() {
		return LSystem.FONT_NAME;
	}

	public int getSize() {
		return this.fontSize;
	}

	public boolean isUnderlined() {
		return this.typefacePaint.isUnderlineText();
	}

	public int charWidth(char ch) {
		char[] chars = Character.toChars(ch);
		int w = (int) typefacePaint.measureText(chars, 0, 1);
		return w;
	}

	public int stringWidth(String str) {
		return (int) typefacePaint.measureText(str);
	}

	public int subStringWidth(String str, int offset, int len) {
		return stringWidth(str.substring(offset, len));
	}

	public int getHeight() {
		return typefacePaint.getFontMetricsInt(typefacePaint
				.getFontMetricsInt());
	}

	public int getTextHeight() {
		return (getTextBounds(tmp).height() * 2);
	}

	public Rect getTextBounds(String text) {
		typefacePaint.getTextBounds(text, 0, text.length(), rect);
		return rect;
	}

}
