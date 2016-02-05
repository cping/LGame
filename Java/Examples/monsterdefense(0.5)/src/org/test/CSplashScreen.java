package org.test;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;

public class CSplashScreen implements CScreen {
	private MainGame mainGame;
	private LTexture splash;

	public CSplashScreen(MainGame game) {
		this.mainGame = game;
	}

	@Override
	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		batch.draw(this.splash, this.mainGame.fullScreenRect, LColor.white);
	}

	@Override
	public final void LoadContent() {
		this.splash = LTextures.loadTexture("LoadingScreen.png");
	}

	@Override
	public final void update(float time) {
	}
}