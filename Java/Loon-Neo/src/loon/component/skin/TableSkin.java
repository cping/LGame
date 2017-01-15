package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.FontSet;
import loon.font.IFont;

public class TableSkin implements FontSet<TableSkin>{

	private IFont font;

	private LTexture backgroundTexture;

	private LTexture headerTexture;

	private LColor fontColor;

	public final static TableSkin def() {
		return new TableSkin();
	}

	public TableSkin() {
		this(LSystem.getSystemGameFont(), LColor.white, DefUI.get().getDefaultTextures(7),
				DefUI.get().getDefaultTextures(4));
	}

	public TableSkin(IFont font, LColor fontColor, LTexture header,
			LTexture background) {
		this.font = font;
		this.fontColor = fontColor;
		this.headerTexture = header;
		this.backgroundTexture = background;
	}

	public IFont getFont() {
		return font;
	}

	public TableSkin setFont(IFont font) {
		this.font = font;
		return this;
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

	public LTexture getHeaderTexture() {
		return headerTexture;
	}

	public void setHeaderTexture(LTexture headerTexture) {
		this.headerTexture = headerTexture;
	}

}
