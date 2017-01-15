package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;

public class ProgressSkin {

	private LTexture progressTexture;
	private LTexture backgroundTexture;
	private LColor color;

	public final static ProgressSkin def() {
		return new ProgressSkin();
	}

	public ProgressSkin() {
		this(LSystem.getSystemGameFont(), LColor.white, DefUI.get().getDefaultTextures(4),
				DefUI.get().getDefaultTextures(2));
	}

	public ProgressSkin(IFont font, LColor c, LTexture progress,
			LTexture background) {
		this.color = c;
		this.progressTexture = progress;
		this.backgroundTexture = background;
	}

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

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor c) {
		this.color = c;
	}

}
