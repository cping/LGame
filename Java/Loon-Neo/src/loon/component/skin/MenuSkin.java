package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.FontSet;
import loon.font.IFont;
import loon.font.LFont;

public class MenuSkin implements FontSet<MenuSkin>{

	private IFont font;
	private LColor fontColor;
	private LTexture mainTexture;
	private LTexture tabTexture;

	public final static MenuSkin def() {
		return new MenuSkin();
	}

	public MenuSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(2),
				DefUI.getDefaultTextures(4));
	}

	public MenuSkin(IFont font, LColor fontColor, LTexture mainTexture,
			LTexture tabTexture) {
		this.font = font;
		this.fontColor = fontColor;
		this.mainTexture = mainTexture;
		this.tabTexture = tabTexture;
	}
	
	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public MenuSkin setFont(IFont font) {
		this.font = font;
		return this;
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
