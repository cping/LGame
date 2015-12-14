package loon.action.sprite;

import loon.canvas.LColor;
import loon.opengl.GLEx;

public class SpriteSheetFont {

	private SpriteSheet font;

	private char startingCharacter;

	private int charWidth;

	private int charHeight;

	private int horizontalCount;

	private int numChars;

	private float fontScale = 1f;

	public SpriteSheetFont(SpriteSheet font, char startingCharacter) {
		this.font = font;
		this.startingCharacter = startingCharacter;
		horizontalCount = font.getHorizontalCount();
		int verticalCount = font.getVerticalCount();
		charWidth = font.getWidth() / horizontalCount;
		charHeight = font.getHeight() / verticalCount;
		numChars = horizontalCount * verticalCount;
	}

	public void drawString(float x, float y, String text) {
		drawString(x, y, text, LColor.white);
	}

	public void drawString(float x, float y, String text, LColor col) {
		drawString(x, y, text, col, 0, text.length() - 1);
	}

	public void drawString(float x, float y, String text, LColor col,
			int startIndex, int endIndex) {
		char[] data = text.toCharArray();
		for (int i = 0; i < data.length; i++) {
			int index = data[i] - startingCharacter;
			if (index < numChars) {
				int xPos = (index % horizontalCount);
				int yPos = (index / horizontalCount);
				if ((i >= startIndex) || (i <= endIndex)) {
					if (fontScale == 1f) {
						font.getSubImage(xPos, yPos).draw(x + (i * charWidth),
								y, col);
					} else {
						font.getSubImage(xPos, yPos).draw(
								x + (i * charWidth * fontScale), y,
								charWidth * fontScale, charHeight * fontScale,
								col);
					}
				}
			}
		}
	}

	public void drawString(GLEx gl, float x, float y, String text) {
		drawString(x, y, text, LColor.white);
	}

	public void drawString(GLEx gl, float x, float y, String text, LColor col) {
		drawString(x, y, text, col, 0, text.length() - 1);
	}

	public void drawString(GLEx gl, float x, float y, String text, LColor col,
			int startIndex, int endIndex) {
		char[] data = text.toCharArray();
		for (int i = 0; i < data.length; i++) {
			int index = data[i] - startingCharacter;
			if (index < numChars) {
				int xPos = (index % horizontalCount);
				int yPos = (index / horizontalCount);
				if ((i >= startIndex) || (i <= endIndex)) {
					if (fontScale == 1f) {
						gl.draw(font.getSubImage(xPos, yPos), x
								+ (i * charWidth), y, col);
					} else {
						gl.draw(font.getSubImage(xPos, yPos), x
								+ (i * charWidth * fontScale), y, charWidth
								* fontScale, charHeight * fontScale, col);
					}
				}
			}
		}
	}

	public float getFontScale() {
		return this.fontScale;
	}

	public void setFontScale(float s) {
		this.fontScale = s;
	}

	public int getHeight(String text) {
		return (int) (charHeight * fontScale);
	}

	public int getWidth(String text) {
		return (int) (charWidth * fontScale * text.length());
	}

	public int getLineHeight() {
		return (int) (charHeight * fontScale);
	}
}
