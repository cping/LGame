package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class RemainingLives extends DrawableGameComponent implements
		IGameComponent {

	private Vector2f drawPosition;
	private LFont font;
	private LTexture texture;
	private String textureFile;

	public RemainingLives(MainGame game, int remainingLives) {
		super(game);
		this.textureFile = "assets/heart.png";
		this.drawPosition = new Vector2f(0f, 0f);
		this.setNumRemainingLives(remainingLives);
	}

	public final int Decrease() {
		int num;
		this.setNumRemainingLives(num = this.getNumRemainingLives() - 1);
		return num;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		if (this.getNumRemainingLives() >= 0) {
			batch.drawString(this.font, "" + this.getNumRemainingLives(),
					this.drawPosition.x + 15f, this.drawPosition.y + 3f,
					LColor.white);
		}
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(12);
		this.texture = LTextures.loadTexture(this.textureFile);
	}

	private int privateNumRemainingLives;

	public final int getNumRemainingLives() {
		return privateNumRemainingLives;
	}

	public final void setNumRemainingLives(int value) {
		privateNumRemainingLives = value;
	}
}