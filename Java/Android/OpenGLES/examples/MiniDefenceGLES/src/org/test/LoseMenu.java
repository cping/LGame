package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class LoseMenu {
	public LTexture background;
	public LFont font;
	public LTexture loseTitle;
	public RectBox MenuEntry_QUIT = new RectBox();
	public RectBox MenuEntry_RETRY = new RectBox();

	public final void Draw(SpriteBatch batch) {
		batch.draw(this.background, 20f, 20f, LColor.white);
		batch.draw(this.loseTitle, 185f, 100f, LColor.white);
		batch.drawString(this.font, "RETRY", this.MenuEntry_RETRY.x,
				this.MenuEntry_RETRY.y, LColor.white);
		batch.drawString(this.font, "QUIT GAME", this.MenuEntry_QUIT.x,
				this.MenuEntry_QUIT.y, LColor.white);
	}

	public final void Initialize() {
		this.background = LTextures.loadTexture("assets/blur.png");
		this.loseTitle = LTextures.loadTexture("assets/youlose.png");
		this.MenuEntry_RETRY.setBounds(350, 320, 100, 30);
		this.MenuEntry_QUIT.setBounds(330, 400, 140, 30);
		this.font = LFont.getFont("黑体", 1,28);
	}

	public final boolean Tap_MenuEntry_QUIT(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_QUIT.Left()) && (posX <= this.MenuEntry_QUIT
				.Right())) && (posY >= this.MenuEntry_QUIT.Top()))
				&& (posY <= this.MenuEntry_QUIT.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}

	public final boolean Tap_MenuEntry_RETRY(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_RETRY.Left()) && (posX <= this.MenuEntry_RETRY
				.Right())) && (posY >= this.MenuEntry_RETRY.Top()))
				&& (posY <= this.MenuEntry_RETRY.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}
}