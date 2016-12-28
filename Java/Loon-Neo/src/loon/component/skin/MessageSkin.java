package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;
import loon.font.LFont;

public class MessageSkin {

	private IFont font;

	private LTexture backgroundTexture;

	private LColor fontColor;

	public final static MessageSkin def() {
		return new MessageSkin();
	}

	public MessageSkin() {
		this(LFont.getDefaultFont(), LColor.white, DefUI.getDefaultTextures(2));
	}

	public MessageSkin(IFont font, LColor fontColor, LTexture back) {
		this.font = font;
		this.fontColor = fontColor;
		this.backgroundTexture = back;
	}

	public IFont getFont() {
		return font;
	}

	public void setFont(IFont font) {
		this.font = font;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackground(LTexture background) {
		this.backgroundTexture = background;
	}

	public LColor getFontColor() {
		return fontColor;
	}

	public void setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
	}

}
