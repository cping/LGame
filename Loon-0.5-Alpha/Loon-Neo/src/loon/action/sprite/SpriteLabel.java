package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.font.Font.Style;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class SpriteLabel extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LFont font;

	private boolean visible;

	private int width, height;

	private LColor color;

	private String label;

	public SpriteLabel(String label, int x, int y) {
		this(LFont.getDefaultFont(), label, x, y);
	}

	public SpriteLabel(String label, String font, Style type, int size, int x,
			int y) {
		this(LFont.getFont(font, type, size), label, x, y);
	}

	public SpriteLabel(LFont font, String label, int x, int y) {
		this.font = font;
		this.label = label;
		this.color = LColor.white;
		this.visible = true;
		this.setLocation(x, y);
	}

	public void setFont(String fontName, Style type, int size) {
		setFont(LFont.getFont(fontName, type, size));
	}

	public void setFont(LFont font) {
		this.font = font;
	}

	public void createUI(GLEx g) {
		if (visible) {
			LFont oldFont = g.getFont();
			int oldColor = g.color();
			g.setFont(font);
			g.setColor(color);
			this.width = font.stringWidth(label);
			this.height = (int) font.getHeight();
			if (_alpha > 0 && _alpha < 1) {
				float tmp = g.alpha();
				g.setAlpha(_alpha);
				g.drawString(label, x(), y());
				g.setAlpha(tmp);
			} else {
				g.drawString(label, x(), y());
			}
			g.setFont(oldFont);
			g.setColor(oldColor);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void update(long timer) {

	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(int label) {
		setLabel(String.valueOf(label));
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	public LTexture getBitmap() {
		return null;
	}

	public void close() {

	}

}
