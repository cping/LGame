package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.FontSet;
import loon.font.IFont;

public class CheckBoxSkin implements FontSet<CheckBoxSkin>{

	private IFont font;
	private LColor fontColor;

	private LTexture uncheckedTexture;
	private LTexture checkedTexture;

	public final static CheckBoxSkin def() {
		return new CheckBoxSkin();
	}

	public CheckBoxSkin() {
		this(LSystem.getSystemGameFont(), LColor.white, DefUI.get().getDefaultTextures(5),
				DefUI.get().getDefaultTextures(6));
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

	public CheckBoxSkin setFont(IFont font) {
		this.font = font;
		return this;
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
