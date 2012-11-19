package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.Drawable;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class PublisherScreen extends MenuScreen {
	private LTexture logo;
	private Drawable[] screens;
	private float screenTime = 0f;

	public PublisherScreen(Drawable... firstScreens) {
		super.transitionOffTime = 1f;
		this.screens = firstScreens;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		batch.draw(this.logo, ScreenData.fullscreen, new LColor(
				255,255,255,super.getTransitionAlpha()));
	}

	@Override
	public void loadContent() {
	
		this.logo = LTextures.loadTexture("assets/DiNoLogo.png");
	}

	@Override
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		this.screenTime += gameTime.getElapsedGameTime();
		if (this.screenTime > 2f) {
			this.exitScreen();
		}
		if ((super.getTransitionAlpha() == 0f) && super.isExiting()) {
			for (int i = 0; i < this.screens.length; i++) {
				drawableScreen.addDrawable(this.screens[i]);
			}
		}
	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameTime elapsedTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub
		
	}
}