package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.utils.timer.GameTime;

public class SplashScreen {
	public static LTexture bgTexture;


	public SplashScreen() {
	}

	public final void Draw(SpriteBatch batch) {
		batch.draw(bgTexture, 0f, 0f);
	}

	public final void LoadContent() {
		bgTexture = Global.Load("MainMenu");
	
	}

	protected final void UnloadContent() {
	}

	public final void Update(GameTime gameTime) {
	}
}