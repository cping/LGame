package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class ClickButtonSkin {

	private LTexture idleClickTexture;
	private LTexture hoverClickTexture;
	private LTexture clickedTexture;
	private IFont font;
	private LColor fontColor;

	public LTexture getIdleClickTexture() {
		return idleClickTexture;
	}

	public void setIdleClickTexture(LTexture idleClickTexture) {
		this.idleClickTexture = idleClickTexture;
	}

	public LTexture getHoverClickTexture() {
		return hoverClickTexture;
	}

	public void setHoverClickTexture(LTexture hoverClickTexture) {
		this.hoverClickTexture = hoverClickTexture;
	}

	public LTexture getClickedTexture() {
		return clickedTexture;
	}

	public void setClickedTexture(LTexture c) {
		this.clickedTexture = c;
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
