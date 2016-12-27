package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;

public class ProgressSkin {

	private LTexture progressTexture;
	private LTexture backgroundTexture;
	private LColor fontColor;

	public LTexture getProgressTexture() {
		return progressTexture;
	}

	public void setProgressTexture(LTexture progressTexture) {
		this.progressTexture = progressTexture;
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
