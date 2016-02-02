package org.test.towerdefense;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ProgressBar extends DrawableGameComponent implements
		IGameComponent {
	private LColor backColor;
	private LColor frontColor;
	private LColor frontColorLow;
	private MainGame game;
	private float lowColorLimit;
	private LTexture texture;
	private int width;

	public ProgressBar(MainGame game, int width, boolean isHealthBarMode) {
		super(game);
		this.game = game;
		this.setCurrentPercent(100);
		this.width = width;
		this.setHeight(4);
		this.setDrawBorder(false);
		super.setDrawOrder(30);
		if (isHealthBarMode) {
			this.frontColor = new LColor(0f, 1f, 0f, 1f);
			this.frontColorLow = LColor.red;
			this.backColor = LColor.gray;
			this.lowColorLimit = 0.4f;
		} else {
			this.frontColor = LColor.white;
			this.frontColorLow = LColor.white;
			this.backColor = LColor.black;
			this.lowColorLimit = 0f;
		}
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		if (this.getCurrentPercent() < 100) {
			batch.draw(this.texture, this.getPosition().x,
					(int) this.getPosition().y, this.width, this.getHeight(),
					0, 5, this.texture.getWidth(), 4, this.backColor);
		}
		float num = ((float) this.getCurrentPercent()) / 100f;
		LColor color = (num < this.lowColorLimit) ? this.frontColorLow
				: this.frontColor;
		if (this.game
				.getGameplayScreen()
				.getGameOpacity()
				.equals(this.game.getGameplayScreen()
						.getGameOpacityWhenPaused())) {
			LColor col = new LColor(color);
			col.mul(0.3f);
			color = col;
		}
		batch.draw(this.texture, this.getPosition().x, this.getPosition().y,
				(this.width * num), this.getHeight(), 0, 5,
				this.texture.getWidth(), 4, color);
		if (this.getDrawBorder()) {
			batch.draw(this.texture, this.getPosition().x,
					this.getPosition().y, this.texture.getWidth(), 4, 0, 0,
					this.texture.getWidth(), 4, LColor.white);
		}
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/healthBar.png");
	}

	private int privateCurrentPercent;

	public final int getCurrentPercent() {
		return privateCurrentPercent;
	}

	public final void setCurrentPercent(int value) {
		privateCurrentPercent = value;
	}

	private boolean privateDrawBorder;

	public final boolean getDrawBorder() {
		return privateDrawBorder;
	}

	public final void setDrawBorder(boolean value) {
		privateDrawBorder = value;
	}

	private int privateHeight;

	public final int getHeight() {
		return privateHeight;
	}

	public final void setHeight(int value) {
		privateHeight = value;
	}

	private boolean privateIsHealthBarMode;

	public final boolean getIsHealthBarMode() {
		return privateIsHealthBarMode;
	}

	public final void setIsHealthBarMode(boolean value) {
		privateIsHealthBarMode = value;
	}

	private Vector2f privatePosition;

	public final Vector2f getPosition() {
		return privatePosition;
	}

	public final void setPosition(Vector2f value) {
		privatePosition = value;
	}
}