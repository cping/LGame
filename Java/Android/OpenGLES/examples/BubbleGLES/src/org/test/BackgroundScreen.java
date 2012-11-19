package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.Drawable;
import loon.core.input.LInput;
import loon.core.input.LKey;
import loon.core.input.LTouch;
import loon.core.timer.GameTime;

public class BackgroundScreen extends Drawable {
	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.update(gameTime, otherScreenHasFocus, false);
	}

	public void handleInput(LInput input) {

	}

	public void loadContent() {

	}

	public void unloadContent() {

	}

	public void draw(SpriteBatch batch, GameTime elapsedTime) {

	}

	public void update(GameTime elapsedTime) {

	}

	public void pressed(LTouch e) {

	}

	public void released(LTouch e) {

	}

	public void move(LTouch e) {

	}

	public void pressed(LKey e) {

	}

	public void released(LKey e) {

	}
}
