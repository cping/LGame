package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.font.IFont;

public class TextListSkin {

	private LTexture backgoundTexture;
	private LTexture choiceTexture;
	private LTexture scrollTexture;
	private LTexture scrollFlagATexture;
	private LTexture scrollFlagBTexture;
	private IFont font;
	private LColor fontColor;

	public LTexture getBackgoundTexture() {
		return backgoundTexture;
	}

	public void setBackgoundTexture(LTexture bgTexture) {
		this.backgoundTexture = bgTexture;
	}

	public LTexture getChoiceTexture() {
		return choiceTexture;
	}

	public void setChoiceTexture(LTexture choiceTexture) {
		this.choiceTexture = choiceTexture;
	}

	public LTexture getScrollTexture() {
		return scrollTexture;
	}

	public void setScrollTexture(LTexture scrollTexture) {
		this.scrollTexture = scrollTexture;
	}

	public LTexture getScrollFlagATexture() {
		return scrollFlagATexture;
	}

	public void setScrollFlagATexture(LTexture scrollFlagATexture) {
		this.scrollFlagATexture = scrollFlagATexture;
	}

	public LTexture getScrollFlagBTexture() {
		return scrollFlagBTexture;
	}

	public void setScrollFlagBTexture(LTexture scrollFlagBTexture) {
		this.scrollFlagBTexture = scrollFlagBTexture;
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

}
