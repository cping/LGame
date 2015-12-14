package loon.action.sprite;

import loon.canvas.LColor;
import loon.font.IFont;
import loon.opengl.GLEx;
import loon.utils.StringUtils;

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
				if (index == '\n') {
					//lines++;
					//display.height = 0;
					continue;
				}
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

	@Override
	public void drawString(GLEx g, String text, float x, float y,
			float rotation, LColor c) {
		if (rotation == 0) {
			drawString(g, text, x, y, c);
			return;
		}
		try {
			g.saveTx();
			float centerX = x + stringWidth(text) / 2;
			float centerY = y + stringHeight(text) / 2;
			g.rotate(centerX, centerY, rotation);
			drawString(g, text, x, y, c);
		} finally {
			g.restoreTx();
		}
	}

	public float getFontScale() {
		return this.fontScale;
	}

	public void setFontScale(float s) {
		this.fontScale = s;
	}

	@Override
	public int stringHeight(String text) {
		int count = StringUtils.charCount(text, '\n');
		return (int) (charHeight * fontScale * count);
	}

	@Override
	public int stringWidth(String text) {
		return (int) (charWidth * fontScale * text.length());
	}

	@Override
	public int getHeight() {
		return (int) (charHeight * fontScale);
	}

	@Override
	public float getAscent() {
		return (charWidth + charHeight / 2) * this.fontScale;
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
	public int getSize() {
		return (int) ((charWidth + charHeight / 2) * this.fontScale);
	}

}
