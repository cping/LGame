package loon.action.sprite;

import loon.canvas.LColor;
import loon.font.IFont;
import loon.opengl.GLEx;

public class SpriteSheetFont implements IFont {

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

	public void drawString(String text, float x, float y) {
		drawString(text, x, y, LColor.white);
	}

	public void drawString(String text, float x, float y, LColor col) {
		drawString(text, x, y, col, 0, text.length() - 1);
	}

	public void drawString(String text, float x, float y, LColor col,
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

	public void drawString(GLEx gl, String text, float x, float y) {
		drawString(gl, text, x, y, LColor.white);
	}

	public void drawString(GLEx gl, String text, float x, float y, LColor col) {
		drawString(gl, text, x, y, col, 0, text.length() - 1);
	}

	public void drawString(GLEx gl, String text, float x, float y, LColor col,
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

	public int stringHeight(String text) {
		return (int) (charHeight * fontScale);
	}

	@Override
	public int stringWidth(String text) {
		return (int) (charWidth * fontScale * text.length());
	}

	public int getHeight() {
		return (int) (charHeight * fontScale);
	}

	@Override
	public float getAscent() {
		return (charWidth + charHeight/2) * this.fontScale;
	}

	@Override
	public int getSize() {
		return (int) ((charWidth + charHeight/2) * this.fontScale);
	}
}
