package loon.action.sprite;

import loon.canvas.LColor;
import loon.font.IFont;
import loon.geom.Affine2f;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.StringUtils;

public class SpriteSheetFont implements IFont {

	private SpriteSheet font;

	private int _size = -1;

	private float _assent = -1;

	private char startingCharacter;

	private int charWidth;

	private int charHeight;

	private int horizontalCount;

	private int numChars;

	private float fontScaleX = 1f, fontScaleY = 1f;

	private PointI _offset = new PointI();

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
		if (StringUtils.isEmpty(text)) {
			return;
		}
		for (int i = 0, size = text.length(); i < size; i++) {
			int index = text.charAt(i) - startingCharacter;
			if (index < numChars) {
				int xPos = (index % horizontalCount);
				int yPos = (index / horizontalCount);
				if ((i >= startIndex) || (i <= endIndex)) {
					if (fontScaleX == 1f && fontScaleY == 1f) {
						font.getSubImage(xPos, yPos).draw(
								x + (i * charWidth) + _offset.x, y + _offset.y,
								col);
					} else {
						font.getSubImage(xPos, yPos).draw(
								x + (i * charWidth * fontScaleX) + _offset.x,
								y + _offset.y, charWidth * fontScaleX,
								charHeight * fontScaleY, col);
					}
				}
			}
		}
	}

	@Override
	public void drawString(GLEx gl, String text, float x, float y) {
		drawString(gl, text, x, y, LColor.white);
	}

	@Override
	public void drawString(GLEx gl, String text, float x, float y, LColor col) {
		drawString(gl, text, x, y, col, 0, text.length() - 1);
	}

	public void drawString(GLEx gl, String text, final float x, final float y,
			LColor col, int startIndex, int endIndex) {
		if (StringUtils.isEmpty(text)) {
			return;
		}
		int lines = 0;
		float sx = x + _offset.x;
		float sy = y + _offset.y;
		float widthSize = charWidth * fontScaleX;
		float heightSize = charHeight * fontScaleY;
		for (int i = 0, size = text.length(); i < size; i++) {
			char flag = text.charAt(i);
			int index = flag - startingCharacter;
			if (index < numChars) {
				int xPos = (index % horizontalCount);
				int yPos = (index / horizontalCount);
				if ('\n' == flag) {
					lines += getHeight();
					sx = x;
					continue;
				} else {
					sx += widthSize;
				}
				if ((i >= startIndex) || (i <= endIndex)) {
					if (font.contains(xPos, yPos)) {
						if (fontScaleX == 1f && fontScaleY == 1f) {
							gl.draw(font.getSubImage(xPos, yPos), sx, sy
									+ lines, col);
						} else {
							gl.draw(font.getSubImage(xPos, yPos), sx, sy
									+ lines, widthSize, heightSize, col);
						}
					}
				}
			}
		}
	}

	@Override
	public void drawString(GLEx g, String text, float x, float y,
			float rotation, LColor c) {
		if (StringUtils.isEmpty(text)) {
			return;
		}
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

	@Override
	public void drawString(GLEx gl, String text, float x, float y, float sx,
			float sy, float ax, float ay, float rotation, LColor c) {
		if (StringUtils.isEmpty(text)) {
			return;
		}
		final boolean anchor = ax != 0 || ay != 0;
		final boolean scale = sx != 1f || sy != 1f;
		final boolean angle = rotation != 0;
		final boolean update = scale || angle || anchor;
		try {
			if (update) {
				gl.saveTx();
				Affine2f xf = gl.tx();
				if (angle) {
					float centerX = x + this.stringWidth(text) / 2;
					float centerY = y + this.stringHeight(text) / 2;
					xf.translate(centerX, centerY);
					xf.preRotate(rotation);
					xf.translate(-centerX, -centerY);
				}
				if (scale) {
					float centerX = x + this.stringWidth(text) / 2;
					float centerY = y + this.stringHeight(text) / 2;
					xf.translate(centerX, centerY);
					xf.preScale(sx, sy);
					xf.translate(-centerX, -centerY);
				}
				if (anchor) {
					xf.translate(ax, ay);
				}
			}
			drawString(gl, text, x, y, c);
		} finally {
			if (update) {
				gl.restoreTx();
			}
		}
	}

	public float getFontScaleX() {
		return this.fontScaleX;
	}

	public float getFontScaleY() {
		return this.fontScaleX;
	}

	public void setFontScale(float s) {
		this.setFontScale(s, s);
	}

	public void setFontScale(float sx, float sy) {
		this.fontScaleX = sx;
		this.fontScaleY = sy;
	}

	public void setFontScaleX(float x) {
		this.fontScaleX = x;
	}

	public void setFontScaleY(float y) {
		this.fontScaleY = y;
	}

	@Override
	public int stringHeight(String text) {
		int count = StringUtils.charCount(text, '\n') + 1;
		return (int) (charHeight * fontScaleY * count);
	}

	@Override
	public int stringWidth(String text) {
		return (int) (charWidth * fontScaleX * text.length());
	}

	@Override
	public int getHeight() {
		return (int) (charHeight * fontScaleY);
	}

	@Override
	public float getAscent() {
		return _assent == -1 ? (charWidth + charHeight / 2) * this.fontScaleY
				: _assent;
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
		return _size == -1 ? (int) ((charWidth + charHeight / 2) * this.fontScaleY)
				: _size;
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
		this._assent = assent;
	}

	@Override
	public void setSize(int size) {
		this._size = size;
	}

	@Override
	public int charWidth(char c) {
		return charWidth;
	}

}
