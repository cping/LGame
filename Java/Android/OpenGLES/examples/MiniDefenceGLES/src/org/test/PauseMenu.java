package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class PauseMenu {
	public LTexture background;
	public LFont font;
	public RectBox MenuEntry_QUIT = new RectBox();
	public RectBox MenuEntry_RETURN = new RectBox();

	public final void Draw(SpriteBatch batch) {
		batch.draw(this.background, 20f, 20f,LColor.white);
		batch.drawString(this.font, "RETURN", this.MenuEntry_RETURN.x,
				this.MenuEntry_RETURN.y, LColor.white);
		batch.drawString(this.font, "QUIT GAME", this.MenuEntry_QUIT.x,
				this.MenuEntry_QUIT.y, LColor.white);
	}

	public final void Initialize() {
		this.background = LTextures.loadTexture("assets/blur.png");
		this.MenuEntry_RETURN.setBounds(350, 200, 100, 30);
		this.MenuEntry_QUIT.setBounds(330, 300, 140, 30);
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

	public final boolean Tap_MenuEntry_RETURN(float posX, float posY) {
		boolean flag;
		if ((((posX >= this.MenuEntry_RETURN.Left()) && (posX <= this.MenuEntry_RETURN
				.Right())) && (posY >= this.MenuEntry_RETURN.Top()))
				&& (posY <= this.MenuEntry_RETURN.Bottom())) {
			flag = true;
		} else {
			return false;
		}
		return flag;
	}
}