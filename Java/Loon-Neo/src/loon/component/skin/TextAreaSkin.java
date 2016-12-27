package loon.component.skin;

import loon.LTexture;
import loon.font.IFont;

public class TextAreaSkin {

	private IFont font;

	private LTexture backgroundTexture;

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

}
