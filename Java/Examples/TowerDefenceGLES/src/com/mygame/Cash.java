package com.mygame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.Vector2f;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class Cash extends DrawableGameComponent implements IGameComponent {
	private int cash;
	private LFont font;
	private MainGame game;
	private Vector2f position;
	private LTexture texture;
	private String textureFile;

	public Cash(MainGame game, int cash) {
		super(game);
		this.textureFile = "assets/cash.png";
		this.position = new Vector2f(250f, 0f);
		this.game = game;
		this.cash = cash;
	}

	public final int Decrease(int amount) {
		this.cash -= amount;
		this.game.getGameplayScreen().AvailableCashChanged();
		return this.cash;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.position, LColor.white);
		Utils.DrawStringAlignRight(batch, this.font,
				(new Integer(this.cash)).toString(), this.position.x + 46f,
				this.position.y + 3f, LColor.white);
		super.draw(batch, gameTime);
	}

	public final int Increase(int amount) {
		this.cash += amount;
		this.game.getGameplayScreen().AvailableCashChanged();
		return this.cash;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.font = LFont.getFont(12);
		this.texture = LTextures.loadTexture(this.textureFile);
	}

	public final int getCurrentCash() {
		return this.cash;
	}
}