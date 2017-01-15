package loon.canvas;

import loon.LSystem;
import loon.font.IFont;
import loon.font.LFont;
import loon.utils.ObjectMap;

public class Paint {

	public enum Style {
		FILL, STROKE, FILL_AND_STROKE
	}

	private static ObjectMap<String, LFont> _paintTexts = new ObjectMap<String, LFont>(
			10);

	private int textSize = 20;

	private IFont textFont;

	public int alpha = 255;

	public int strokeWidth = 1;

	public int color = LColor.DEF_COLOR;

	public Style style = Style.FILL;

	public Paint() {
		this.textFont = LSystem.getSystemGameFont();
	}

	public void setRGB(int r, int g, int b) {
		this.color = LColor.getRGB(r, g, b);
	}
	
	public void setARGB(int r, int g, int b, int a) {
		this.color = LColor.getARGB(r, g, b, a);
	}

	public void setColor(int c) {
		this.color = c;
	}

	public void setColor(LColor c) {
		this.color = c.getARGB();
	}

	public void setStyle(Style s) {
		this.style = s;
	}

	public void setStrokeWidth(float s) {
		this.strokeWidth = (int) s;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getColor() {
		return color;
	}

	public Style getStyle() {
		return style;
	}

	public void setTextSize(int size) {
		String key = String.valueOf(size);
		LFont font = _paintTexts.get(key);
		if (font == null) {
			font = LFont.getFont(size);
			_paintTexts.put(key, font);
		}
		this.textFont = font;
		this.textSize = size;
	}

	public int getTextSize() {
		return this.textSize;
	}

	public IFont getFont() {
		return textFont;
	}

	public IFont setFont(LFont font) {
		this.textFont = font;
		if (font != null) {
			this.textSize = font.getSize();
		}
		return this.textFont;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int a) {
		this.alpha = a;
		this.color = (alpha << 24) | (color & 0xFFFFFF);
	}
}
