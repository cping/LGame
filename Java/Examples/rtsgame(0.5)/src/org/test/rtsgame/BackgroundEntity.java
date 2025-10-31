package org.test.rtsgame;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColorPool;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class BackgroundEntity extends GameEntity {

	private LTexture backgroundTexture;

	public BackgroundEntity() {
		super.setTransitionOnTime(0.5f);
		super.setTransitionOffTime(0.5f);
	}

	@Override
	public void Draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.backgroundTexture, Vector2f.STATIC_ZERO,
				LColorPool.get().getColor(1f, 1f, 1f, super.getTransitionAlpha()));
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