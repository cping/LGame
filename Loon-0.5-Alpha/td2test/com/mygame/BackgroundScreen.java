package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.utils.timer.GameTime;

public class BackgroundScreen extends GameScreen {

	private LTexture background;

	private String backgroundName;

	public BackgroundScreen(String backgroundName) {
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		this.backgroundName = backgroundName;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.background, 0f, 0f,
				PoolColor.getColor(1f, 1f, 1f, getTransitionAlpha()));
	}

	@Override
	public void LoadContent() {
		this.background = LTextures.loadTexture("assets/backgrounds/"
				+ this.backgroundName + ".png");
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, false);
	}
}