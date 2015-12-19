package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class Sprite extends DrawableGameComponent implements IGameComponent {

	private Vector2f drawPosition = new Vector2f();
	private MainGame game;
	private int showMilliseconds;
	private String textureFile;
	private double timeLeft;

	public Sprite(MainGame game, String textureFile, int showMilliseconds,
			Vector2f drawPosition) {
		super(game);
		this.game = game;
		this.showMilliseconds = showMilliseconds;
		this.timeLeft = showMilliseconds;
		this.textureFile = textureFile;
		this.drawPosition = drawPosition.cpy();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.getTexture(), this.drawPosition, LColor.white);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.setTexture(LTextures.loadTexture(this.textureFile));
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.showMilliseconds > 0) {
			this.timeLeft -= gameTime.getMilliseconds();
			if (this.timeLeft < 0.0) {
				this.game.Components().remove(this);
			}
		}
	}

	private LTexture privateTexture;

	public final LTexture getTexture() {
		return privateTexture;
	}

	public final void setTexture(LTexture value) {
		privateTexture = value;
	}
}