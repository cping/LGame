package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class MenuSkin {

	private IFont font;
	private LColor fontColor;
	private LTexture mainTexture;
	private LTexture tabTexture;

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

	public LTexture getMainTexture() {
		return mainTexture;
	}

	public void setMainTexture(LTexture mainTexture) {
		this.mainTexture = mainTexture;
	}

	public LTexture getTabTexture() {
		return tabTexture;
	}

	public void setTabTexture(LTexture tabTexture) {
		this.tabTexture = tabTexture;
	}

}
