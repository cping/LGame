package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class WindowSkin {

	private IFont font;
	private LTexture barTexture;
	private LTexture backgroundTexture;
	private LColor fontColor;

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public LTexture getBarTexture() {
		return barTexture;
	}

	public void setBarTexture(LTexture barTexture) {
		this.barTexture = barTexture;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}
}
