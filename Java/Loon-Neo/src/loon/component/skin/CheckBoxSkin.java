package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class CheckBoxSkin {

	private IFont font;
	private LColor fontColor;

	private LTexture uncheckedTexture;
	private LTexture checkedTexture;

	public final static CheckBoxSkin def() {
		return new CheckBoxSkin();
	}

	public CheckBoxSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(5),
				DefUI.getDefaultTextures(6));
	}

	public CheckBoxSkin(IFont font, LColor fontColor, LTexture unchecked,
			LTexture checked) {
		this.font = font;
		this.fontColor = fontColor;
		this.uncheckedTexture = unchecked;
		this.checkedTexture = checked;
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
