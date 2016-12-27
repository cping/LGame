package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class CheckBoxSkin {
	
	private IFont font;
	private LColor fontColor;

	private LTexture uncheckedTexture;
	private LTexture checkedTexture;

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

	public LTexture getUncheckedTexture() {
		return uncheckedTexture;
	}

	public void setUncheckedTexture(LTexture uncheckedTexture) {
		this.uncheckedTexture = uncheckedTexture;
	}

	public LTexture getCheckedTexture() {
		return checkedTexture;
	}

	public void setCheckedTexture(LTexture checkedTexture) {
		this.checkedTexture = checkedTexture;
	}
}
