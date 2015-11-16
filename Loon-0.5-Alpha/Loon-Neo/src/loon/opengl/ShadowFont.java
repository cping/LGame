package loon.opengl;

import loon.canvas.LColor;
import loon.font.LFont;

public class ShadowFont {

	private boolean withShadow = false;
	private LColor shadowColor = new LColor(0f, 0f, 0f, 1f);
	private float shadowAlpha = 1f;
	private int offsetX = 2;
	private int offsetY = 2;
	private LSTRFont strfont;

	public LSTRFont getStrFont() {
		return strfont;
	}

	public LFont getFont() {
		return strfont.getFont();
	}

	public ShadowFont(LFont font, String[] messages, String append,
			boolean shadow) {
		if (append != null) {
			int size = messages.length + 1;
			String[] dest = new String[size];
			dest[size - 1] = append;
			System.arraycopy(messages, 0, dest, 0, messages.length);
			this.strfont = new LSTRFont(font, dest);
		} else {
			this.strfont = new LSTRFont(font, messages);
		}
		this.withShadow = shadow;
	}

	public ShadowFont(LFont font, String message, String append, boolean shadow) {
		this.strfont = new LSTRFont(font, message + append);
		this.withShadow = shadow;
	}

	public void drawString(float x, float y, String text, LColor color) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * color.a);
			strfont.drawString(text, x, y, shadowColor);
		}
		strfont.drawString(text, x, y, color);
	}

	public void drawString(GLEx g, float x, float y, String text, LColor color) {
		if (this.withShadow) {
			this.shadowColor.a = (this.shadowAlpha * color.a);
			strfont.drawString(g, text, x, y, shadowColor);
		}
		strfont.drawString(g, text, x, y, color);
	}

	public void setShadowColor(LColor color) {
		this.shadowColor = color;
	}

	public void setShadowOffset(int offset) {
		this.offsetX = offset;
		this.offsetY = offset;
	}

	public void setShadowAlpha(float alpha) {
		this.shadowAlpha = alpha;
	}

	public void setShadowOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setShadowOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setShadow(boolean shadow) {
		this.withShadow = shadow;
	}

	public boolean isShadowEffect() {
		return this.withShadow;
	}

	public LColor getShadowColor() {
		return this.shadowColor;
	}

	public boolean isWithShadow() {
		return withShadow;
	}

	public void setWithShadow(boolean withShadow) {
		this.withShadow = withShadow;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public float getShadowAlpha() {
		return shadowAlpha;
	}
}
