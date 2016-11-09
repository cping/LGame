package loon.action.sprite;

import loon.canvas.LColor;
import loon.font.Font.Style;
import loon.font.IFont;
import loon.font.LFont;
import loon.opengl.GLEx;

public class SpriteLabel extends Entity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IFont font;

	private String label;

	public SpriteLabel(String label, int x, int y) {
		this(LFont.getDefaultFont(), label, x, y);
	}

	public SpriteLabel(String label, String font, Style type, int size, int x,
			int y) {
		this(LFont.getFont(font, type, size), label, x, y);
	}

	public SpriteLabel(IFont font, String label, int x, int y) {
		this.font = font;
		this.setRepaint(true);
		this.setColor(LColor.white);
		this.setLocation(x, y);
		this.setLabel(label);
	}

	public void setFont(String fontName, Style type, int size) {
		setFont(LFont.getFont(fontName, type, size));
	}

	public void setFont(IFont font) {
		this.font = font;
		this.setLabel(label);
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (font != null) {
			font.drawString(g, label, x() + offsetX, y() + offsetY, _baseColor);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(int label) {
		setLabel(String.valueOf(label));
	}

	public void setLabel(String label) {
		this.label = label;
		this.setSize(font.stringWidth(label) + 1, font.stringHeight(label) + 1);
	}

}
