package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class TextBarSkin {

	private LTexture leftTexture;
	private LTexture rightTexture;
	private LTexture bodyTexture;
	private IFont font;
	private LColor fontColor;

	public final static TextBarSkin def() {
		return new TextBarSkin();
	}

	public TextBarSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(3),
				DefUI.getDefaultTextures(3), DefUI.getDefaultTextures(4));
	}

	public TextBarSkin(IFont font, LColor fontColor, LTexture left,
			LTexture right, LTexture body) {
		this.font = font;
		this.fontColor = fontColor;
		this.leftTexture = left;
		this.rightTexture = right;
		this.bodyTexture = body;
	}

	public LTexture getLeftTexture() {
		return leftTexture;
	}

	public void setLeftTexture(LTexture leftTexture) {
		this.leftTexture = leftTexture;
	}

	public LTexture getRightTexture() {
		return rightTexture;
	}

	public void setRightTexture(LTexture rightTexture) {
		this.rightTexture = rightTexture;
	}

	public LTexture getBodyTexture() {
		return bodyTexture;
	}

	public void setBodyTexture(LTexture bodyTexture) {
		this.bodyTexture = bodyTexture;
	}

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}
}
