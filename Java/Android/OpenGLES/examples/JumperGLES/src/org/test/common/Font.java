package org.test.common;

import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class Font {

	private float[] charW = new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f, 1f, 1f, 0.25f, 0.25f, 0.38f, 0.47f, 0.4f, 0.79f,
			0.6f, 0.25f, 0.32f, 0.32f, 0.47f, 0.54f, 0.29f, 0.38f, 0.25f, 0.5f,
			0.63f, 0.35f, 0.5f, 0.44f, 0.54f, 0.4f, 0.47f, 0.47f, 0.47f, 0.47f,
			0.25f, 0.25f, 0.5f, 0.54f, 0.5f, 0.44f, 0.66f, 0.69f, 0.44f, 0.57f,
			0.63f, 0.38f, 0.4f, 0.6f, 0.66f, 0.22f, 0.35f, 0.57f, 0.4f, 0.88f,
			0.66f, 0.69f, 0.44f, 0.72f, 0.5f, 0.4f, 0.63f, 0.63f, 0.69f, 0.91f,
			0.63f, 0.66f, 0.6f, 0.32f, 0.5f, 0.32f, 0.6f, 0.5f, 0.22f, 0.72f,
			0.44f, 0.54f, 0.57f, 0.38f, 0.38f, 0.54f, 0.57f, 0.22f, 0.32f,
			0.5f, 0.4f, 0.79f, 0.6f, 0.63f, 0.44f, 0.66f, 0.5f, 0.38f, 0.6f,
			0.6f, 0.63f, 0.85f, 0.57f, 0.6f, 0.54f, 0.35f, 0.22f, 0.35f, 0.57f,
			1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.28f, 1f, 0.4f, 0.28f,
			1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.28f, 1f, 0.4f,
			0.28f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
			1f, 1f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 0.88f, 0.63f,
			0.57f, 0.57f, 0.57f, 0.57f, 0.45f, 0.45f, 0.45f, 0.45f, 0.57f,
			0.66f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 1f, 1f, 0.63f, 0.63f,
			0.63f, 0.63f, 1f, 1f, 1f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f,
			0.99f, 0.63f, 0.66f, 0.66f, 0.66f, 0.66f, 0.45f, 0.45f, 0.45f,
			0.45f, 0.6f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 0.63f, 1f, 1f,
			0.66f, 0.66f, 0.66f, 0.66f, 1f, 1f, 1f };

	private LColor m_Color = new LColor(1f, 1f, 1f, 1f);

	private float m_fFHeight = 16f;

	private float m_fFSpacing = 1f;

	private float m_fFWidth = 16f;

	private int m_iCountX = 0x10;

	private int m_iCountY = 0x10;

	private LTexture m_pFontTex;

	public Font(String texName) {
		this.m_pFontTex = LTextures.loadTexture("assets/" + texName + ".png");
	}

	public final float GetFontHeight() {
		return this.m_fFHeight;
	}

	public final float GetFontWidth() {
		return this.m_fFWidth;
	}

	public final float GetSpacing() {
		return this.m_fFSpacing;
	}

	public final float GetTextHeight(float scale_y) {
		return (this.m_fFHeight * scale_y);
	}

	public final float GetTextWidth(String text, float scale_x) {
		float num = 0f;
		for (int i = 0; i < text.length(); i++) {
			if (i != 0) {
				num += this.m_fFSpacing * scale_x;
			}
			num += (this.m_fFWidth * this.charW[text.charAt(i)]) * scale_x;
		}
		return num;
	}

	public final void Print(int x, int y, String text) {
		this.Print(x, y, 1f, 1f, text);
	}

	public final void Print(int x, int y, float scale_x, float scale_y,
			String text) {
		float startX = x;
		float startY = y;
		float num7 = 1f / ((float) this.m_iCountX);
		float num8 = 1f / ((float) this.m_iCountY);
		int length = text.length();
		float sx;
		float sy;
		float sw;
		float sh;
		m_pFontTex.glBegin();
		for (int i = 0; i < length; i++) {
			char index = text.charAt(i);
			if (index != ' ') {
				float num5 = (index % this.m_iCountX) * num7;
				float num6 = (index / this.m_iCountY) * num8;
				sx = (this.m_pFontTex.getWidth() * num5);
				sy = (this.m_pFontTex.getHeight() * num6);
				sw = ((this.m_pFontTex.getWidth() * num7) * this.charW[index]);
				sh = (this.m_pFontTex.getHeight() * num8);
				float width = (this.m_fFWidth * this.charW[index]) * scale_x;
				float height = this.m_fFHeight * scale_y;
				m_pFontTex.draw(startX, startY, width, height, sx, sy, sw + sx,
						sh + sy, m_Color);
			}
			startX += ((this.m_fFWidth * this.charW[index]) * scale_x)
					+ this.m_fFSpacing;
		}
		m_pFontTex.glEnd();
	}

	public final void Print(float x, float y, float scale_x, float scale_y,
			String text) {
		this.Print((int) x, (int) y, scale_x, scale_y, text);
	}

	public final void PrintCentered(int width, int y, float scale_x,
			float scale_y, String text) {
		this.Print(((width - this.GetTextWidth(text, scale_x)) * 0.5f), y,
				scale_x, scale_y, text);
	}

	public final void PrintWrap(int x, int y, float scale_x, float scale_y,
			String text, float maxWidth, float lineSpacing) {
		char[] chars = text.toCharArray();
		int length = chars.length;
		float num2 = x;
		float num3 = y;
		float num4 = 0f;
		StringBuffer sbr = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char ch = chars[i];
			int index = ch;
			float num7 = ((this.m_fFWidth * this.charW[index]) + this.m_fFSpacing)
					* scale_x;
			if (index > 0x20) {
				sbr.append(chars[i]);
				num4 += num7;
			} else {
				if (num4 > 0f) {
					if ((num2 + num4) > (x + maxWidth)) {
						num2 = x;
						num3 += this.GetTextHeight(scale_y) + lineSpacing;
					}
					this.Print(num2, num3, scale_x, scale_y, sbr.toString());
				}
				num2 += num4 + num7;
				sbr.delete(0, sbr.length());
				num4 = 0f;
			}
		}
		if (num4 > 0f) {
			if ((num2 + num4) > (x + maxWidth)) {
				num2 = x;
				num3 += this.GetTextHeight(scale_y) + lineSpacing;
			}
			this.Print((int) num2, (int) num3, scale_x, scale_y, sbr.toString());
		}
	}

	public final void PrintWrap(int x, int y, float scale_x, float scale_y,
			String text, int numChars, float maxWidth, float lineSpacing) {
		char[] chars = text.toCharArray();
		int length = chars.length;
		boolean flag = false;
		float num2 = x;
		float num3 = y;
		float num4 = 0f;
		StringBuffer sbr = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char ch = chars[i];
			int index = ch;
			float num7 = ((this.m_fFWidth * this.charW[index]) + this.m_fFSpacing)
					* scale_x;
			if (index > 0x20) {
				if (i <= numChars) {
					sbr.append(chars[i]);
				}
				num4 += num7;
			} else {
				if (num4 > 0f) {
					if ((num2 + num4) > (x + maxWidth)) {
						num2 = x;
						num3 += this.GetTextHeight(scale_y) + lineSpacing;
					}
					if (sbr.length() > 0) {
						this.Print((int) num2, (int) num3, scale_x, scale_y,
								sbr.toString());
					}
					if (i > numChars) {
						flag = true;
					}
				}
				num2 += num4 + num7;
				sbr.delete(0, sbr.length());
				num4 = 0f;
				if (flag) {
					break;
				}
			}
		}
		if ((num4 > 0f) && (sbr.length() > 0)) {
			if ((num2 + num4) > (x + maxWidth)) {
				num2 = x;
				num3 += this.GetTextHeight(scale_y) + lineSpacing;
			}
			this.Print((int) num2, (int) num3, scale_x, scale_y, sbr.toString());
		}
	}

	public final void SetColor(LColor clr) {
		this.m_Color.setColor(clr);
	}

	public final void SetColor(float r, float g, float b, float a) {
		this.m_Color.setColor(r, g, b, a);
	}

	public final void SetFontSize(float fWidth, float fHeight) {
		this.m_fFWidth = fWidth;
		this.m_fFHeight = fHeight;
	}

	public final void SetSpacing(float fFSpacing) {
		this.m_fFSpacing = fFSpacing;
	}
}