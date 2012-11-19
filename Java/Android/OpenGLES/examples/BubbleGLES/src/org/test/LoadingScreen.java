package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.Drawable;
import loon.action.sprite.painting.DrawableScreen;
import loon.action.sprite.painting.DrawableState;
import loon.core.input.LInput;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class LoadingScreen extends Drawable {
	private boolean otherScreensAreGone;
	private Drawable[] screensToLoad;

	private LoadingScreen(Drawable[] screensToLoad) {
		this.screensToLoad = screensToLoad;
		super.IsPopup = true;
		super.transitionOnTime = 0.5f;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (super.getDrawableState() == DrawableState.Active) {
			this.otherScreensAreGone = true;
			for (int i = 0; i < super.drawableScreen.getDrawables().size(); i++) {
				if (!(super.drawableScreen.getDrawables().get(i) instanceof LoadingScreen)) {
					this.otherScreensAreGone = false;
				}
			}
		}
	}

	public static void Load(DrawableScreen draws, Drawable... screensToLoad) {
		for (int i = 0; i < draws.getDrawables().size(); i++) {
			draws.getDrawables().get(i).exitScreen();
		}
		LoadingScreen screen = new LoadingScreen(screensToLoad);
		draws.addDrawable(screen);
	}

	@Override
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
		if (this.otherScreensAreGone) {
			super.drawableScreen.removeDrawable(this);
			for (int i = 0; i < this.screensToLoad.length; i++) {
				super.drawableScreen.addDrawable(this.screensToLoad[i]);
			}
			super.drawableScreen.getGameTime().resetElapsedTime();
		}
	}

	@Override
	public void handleInput(LInput input) {
	}

	@Override
	public void loadContent() {
	}

	@Override
	public void unloadContent() {
	}

	@Override
	public void update(GameTime elapsedTime) {
	}

	@Override
	public void pressed(LTouch e) {
	}

	@Override
	public void released(LTouch e) {
	}

	@Override
	public void move(LTouch e) {
	}

	@Override
	public void pressed(LKey e) {
	}

	@Override
	public void released(LKey e) {

	}
}