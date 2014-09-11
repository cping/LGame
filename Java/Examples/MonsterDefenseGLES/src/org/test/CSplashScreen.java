package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;


public class CSplashScreen implements CScreen
{
	private MainGame mainGame;
	private LTexture splash;

	public CSplashScreen(MainGame game)
	{
		this.mainGame = game;
	}

	public final void draw(SpriteBatch batch,LColor defaultSceneColor)
	{
		batch.draw(this.splash, this.mainGame.fullScreenRect,LColor.white);
	}

	public final void LoadContent()
	{
		this.splash = LTextures.loadTexture("assets\\LoadingScreen.png");
	}

	public final void update(float time)
	{
	}
}