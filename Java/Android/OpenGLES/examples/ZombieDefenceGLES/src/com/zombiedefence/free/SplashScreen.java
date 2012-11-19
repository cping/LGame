package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

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