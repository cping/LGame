package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;

public class TextBarSkin implements FontSet<TextBarSkin>{

	private LTexture leftTexture;
	private LTexture rightTexture;
	private LTexture bodyTexture;
	private IFont font;
	private LColor fontColor;

	public final static TextBarSkin def() {
		return new TextBarSkin();
	}

	public final static TextBarSkin defEmpty() {
		return new TextBarSkin(LFont.getDefaultFont(), LColor.white, null,
				null, null);
	}

	public TextBarSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.get().getDefaultTextures(7),
				DefUI.get().getDefaultTextures(7), DefUI.get().getDefaultTextures(7));
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

	public TextBarSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}
}
