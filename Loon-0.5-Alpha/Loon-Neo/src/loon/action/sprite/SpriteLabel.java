package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class SpriteLabel extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IFont font;

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
			this.width = font.stringWidth(label);
			this.height = (int) font.getHeight();
			if (_alpha > 0 && _alpha < 1) {
				float tmp = g.alpha();
				g.setAlpha(_alpha);
				font.drawString(g, label, x(), y(), color);
				g.setAlpha(tmp);
			} else {
				font.drawString(g, label, x(), y(), color);
			}
		}
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void update(long timer) {

	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
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
