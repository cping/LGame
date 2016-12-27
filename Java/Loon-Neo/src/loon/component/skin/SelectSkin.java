package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class SelectSkin {

	private IFont font;

	private LTexture backgroundTexture;
	
	private LColor fontColor;

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture background) {
		this.backgroundTexture = background;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}


}
