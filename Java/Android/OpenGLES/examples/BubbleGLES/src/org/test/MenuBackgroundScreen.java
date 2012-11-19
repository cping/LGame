package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class MenuBackgroundScreen extends BackgroundScreen {
	private LTexture menuBackground;

	public MenuBackgroundScreen() {
		super.transitionOnTime = 0.5f;
		super.transitionOffTime = 0.5f;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.draw(this.menuBackground, 0f, 0f, 0, 0, 480, 800, LColor.white);
	}

	@Override
	public void loadContent() {
		super.loadContent();
		this.menuBackground = LTextures.loadTexture("assets/Background.png");
	}
}