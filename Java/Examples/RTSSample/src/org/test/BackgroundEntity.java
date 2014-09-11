package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColorPool;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class BackgroundEntity extends GameEntity {
	
	private LTexture backgroundTexture;

	public BackgroundEntity() {
		super.setTransitionOnTime(0.5f);
		super.setTransitionOffTime(0.5f);
	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.backgroundTexture, Vector2f.Zero, LColorPool.$()
				.getColor(1f, 1f, 1f, super.getTransitionAlpha()));
	}

	@Override
	public void LoadContent() {
		this.backgroundTexture = super.getScreenManager().getGameContent().menuBackground;
	}

	@Override
	public void Update(GameTime gameTime, boolean coveredByOtherScreen) {
		super.Update(gameTime, false);
	}
}