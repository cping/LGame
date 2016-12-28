package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class WindowSkin {

	private IFont font;
	private LTexture barTexture;
	private LTexture backgroundTexture;
	private LColor fontColor;

	public final static WindowSkin def() {
		return new WindowSkin();
	}

	public WindowSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(0),
				DefUI.getDefaultTextures(7));
	}

	public WindowSkin(IFont font, LColor fontColor, LTexture bar,
			LTexture background) {
		this.font = font;
		this.fontColor = fontColor;
		this.barTexture = bar;
		this.backgroundTexture = background;
	}

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
