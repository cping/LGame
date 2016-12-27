package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class TextBarSkin {

	private LTexture leftTexture;
	private LTexture rightTexture;
	private LTexture bodyTexture;
	private IFont font;
	private LColor fontColor;

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
