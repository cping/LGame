package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class ClickButtonSkin {

	private LTexture idleClickTexture;
	private LTexture hoverClickTexture;
	private LTexture clickedTexture;
	private IFont font;
	private LColor fontColor;

	public final static ClickButtonSkin def() {
		return new ClickButtonSkin();
	}

	public ClickButtonSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(7),
				DefUI.getDefaultTextures(8), DefUI.getDefaultTextures(9));
	}

	public ClickButtonSkin(IFont font, LColor fontColor, LTexture idle,
			LTexture hover, LTexture clicked) {
		this.font = font;
		this.fontColor = fontColor;
		this.idleClickTexture = idle;
		this.hoverClickTexture = hover;
		this.clickedTexture = clicked;
	}

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
