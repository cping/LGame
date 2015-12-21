package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public abstract class TowerButton extends DrawableGameComponent implements
		IGameComponent {

	private Vector2f drawPosition = new Vector2f();

	private MainGame game;

	private LTexture texture;

	private int textureOffsetX;
	private int textureOffsetY;

	public TowerButton(MainGame game, TowerType towerType) {
		super(game);
		this.game = game;
		this.setIsActive(true);
		this.setTowerType(towerType);
		switch (this.getTowerType()) {
		case Axe:
			this.textureOffsetX = 0;
			break;

		case Spear:
			this.textureOffsetX = 120;
			break;

		case AirDefence:
			this.textureOffsetX = 60;
			break;

		case Lur:
			this.textureOffsetX = 180;
			break;
		}
		this.drawPosition.x = this.textureOffsetX;
		this.Show();
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionArea() {
		rect.setBounds(this.drawPosition.x, this.drawPosition.y, 60, 60);
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.game.getIsTrialMode()
				&& ((this.getTowerType() == TowerType.Spear) || (this
						.getTowerType() == TowerType.Lur))) {
			batch.draw(this.texture, this.drawPosition.x, this.drawPosition.y,
					this.textureOffsetX, this.textureOffsetY, 60, 60,
					LColor.darkGray);
		} else {
			batch.draw(this.texture, this.drawPosition.x, this.drawPosition.y,
					this.textureOffsetX, this.textureOffsetY, 60, 60, this.game
							.getGameplayScreen().getGameOpacity());
		}
		super.draw(batch, gameTime);
	}

	public final void Hide() {
		this.drawPosition.y = -300f;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/factory_buttons.png");
	}

	public final void Show() {
		this.drawPosition.y = 420f;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
	}

	public final void UpdateStatus(int currentCash) {
		if (currentCash >= this.getTowerPrice()) {
			this.setIsActive(true);
			this.textureOffsetY = 0;
		} else {
			this.setIsActive(false);
			this.textureOffsetY = 0x41;
		}
	}

	private boolean privateIsActive;

	public final boolean getIsActive() {
		return privateIsActive;
	}

	public final void setIsActive(boolean value) {
		privateIsActive = value;
	}

	private int privateTowerPrice;

	public final int getTowerPrice() {
		return privateTowerPrice;
	}

	public final void setTowerPrice(int value) {
		privateTowerPrice = value;
	}

	private TowerType privateTowerType;

	public final TowerType getTowerType() {
		return privateTowerType;
	}

	public final void setTowerType(TowerType value) {
		privateTowerType = value;
	}
}