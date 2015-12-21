package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

public class AnimatedSprite extends DrawableGameComponent implements
		IGameComponent {

	private int animationSpeedRatioCounter;
	private int columnCount;
	private MainGame game;
	private float scale;
	private int spriteCount;
	private int spriteIndex;
	private LTexture texture;

	public AnimatedSprite(MainGame game, String textureFile,
			Vector2f drawPosition, int columnCount, int spriteCount,
			int spriteWidth, int spriteHeight, float scale) {
		super(game);
		this.setTextureFile(textureFile);
		this.setDrawPosition(drawPosition.cpy());
		this.setSpriteWidth(spriteWidth);
		this.setSpriteHeight(spriteHeight);
		this.spriteCount = spriteCount;
		this.columnCount = columnCount;
		this.scale = scale;
		this.setObeyGameOpacity(true);
		this.setRotation(0f);
		this.setVerticalTextureOffset(0);
		this.setLayerDepth(1);
		this.setOnlyPlayOnceFeature(false);
		this.game = game;
		this.setAnimationSpeedRatio(1);
		this.setOnlyAnimateIfGameStateStarted(true);
	}

	public final RectBox CentralCollisionArea() {
		return new RectBox((int) this.getDrawPosition().x,
				(int) this.getDrawPosition().y, this.getSpriteWidth(),
				this.getSpriteHeight());
	}

	private RectBox rect = new RectBox();

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		if (this.getOnlyPlayOnceFeature()) {
			if (!this.getPlayNow()) {
				this.spriteIndex = 0;
			} else if (this.spriteIndex == (this.spriteCount - 1)) {
				this.spriteIndex = 0;
				this.setPlayNow(false);
			}
		}
		int num = (this.spriteIndex - (this.spriteIndex % this.columnCount))
				/ this.columnCount;
		int num2 = this.spriteIndex % this.columnCount;

		rect.setBounds(
				num2 * this.getSpriteWidth(),
				(num * this.getSpriteHeight())
						+ this.getVerticalTextureOffset(),
				this.getSpriteWidth(), this.getSpriteHeight());

		LColor color = (this.getObeyGameOpacity() && (this.game
				.getGameplayScreen() != null)) ? this.game.getGameplayScreen()
				.getGameOpacity() : LColor.white;

		batch.draw(this.texture, this.getDrawPosition(), rect, color,
				MathUtils.toDegrees(this.getRotation()), this.getOrigin(),
				this.scale, SpriteEffects.None);

		if (!this.getOnlyAnimateIfGameStateStarted()
				|| (GameplayScreen.getGameState() == GameState.Started)) {
			if ((this.getAnimationSpeedRatio() == 1)
					|| ((this.getAnimationSpeedRatio() > 1) && (this.animationSpeedRatioCounter == (this
							.getAnimationSpeedRatio() - 1)))) {
				this.spriteIndex++;
				if (this.getAnimationSpeedRatio() > 1) {
					this.animationSpeedRatioCounter = 0;
				}
			}
			this.animationSpeedRatioCounter++;
		}
		if (this.spriteIndex >= this.spriteCount) {
			this.spriteIndex = 0;
		}
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.getTextureFile());
	}

	private int privateAnimationSpeedRatio;

	public final int getAnimationSpeedRatio() {
		return privateAnimationSpeedRatio;
	}

	public final void setAnimationSpeedRatio(int value) {
		privateAnimationSpeedRatio = value;
	}

	private Vector2f privateDrawPosition = new Vector2f();

	public final Vector2f getDrawPosition() {
		return privateDrawPosition;
	}

	public final void setDrawPosition(Vector2f value) {
		privateDrawPosition.set(value);
	}

	public final void addDrawPosition(Vector2f value) {
		privateDrawPosition.addSelf(value);
	}

	private int privateLayerDepth;

	public final int getLayerDepth() {
		return privateLayerDepth;
	}

	public final void setLayerDepth(int value) {
		privateLayerDepth = value;
	}

	private boolean privateObeyGameOpacity;

	public final boolean getObeyGameOpacity() {
		return privateObeyGameOpacity;
	}

	public final void setObeyGameOpacity(boolean value) {
		privateObeyGameOpacity = value;
	}

	private boolean privateOnlyAnimateIfGameStateStarted;

	public final boolean getOnlyAnimateIfGameStateStarted() {
		return privateOnlyAnimateIfGameStateStarted;
	}

	public final void setOnlyAnimateIfGameStateStarted(boolean value) {
		privateOnlyAnimateIfGameStateStarted = value;
	}

	private boolean privateOnlyPlayOnceFeature;

	public final boolean getOnlyPlayOnceFeature() {
		return privateOnlyPlayOnceFeature;
	}

	public final void setOnlyPlayOnceFeature(boolean value) {
		privateOnlyPlayOnceFeature = value;
	}

	private Vector2f privateOrigin = new Vector2f();

	public final Vector2f getOrigin() {
		return privateOrigin;
	}

	public final void setOrigin(Vector2f value) {
		privateOrigin = value;
	}

	private boolean privatePlayNow;

	public final boolean getPlayNow() {
		return privatePlayNow;
	}

	public final void setPlayNow(boolean value) {
		privatePlayNow = value;
	}

	private float privateRotation;

	public final float getRotation() {
		return privateRotation;
	}

	public final void setRotation(float value) {
		privateRotation = value;
	}

	private int privateSpriteHeight;

	public final int getSpriteHeight() {
		return privateSpriteHeight;
	}

	public final void setSpriteHeight(int value) {
		privateSpriteHeight = value;
	}

	private int privateSpriteWidth;

	public final int getSpriteWidth() {
		return privateSpriteWidth;
	}

	public final void setSpriteWidth(int value) {
		privateSpriteWidth = value;
	}

	private String privateTextureFile;

	public final String getTextureFile() {
		return privateTextureFile;
	}

	public final void setTextureFile(String value) {
		privateTextureFile = value;
	}

	private int privateVerticalTextureOffset;

	public final int getVerticalTextureOffset() {
		return privateVerticalTextureOffset;
	}

	public final void setVerticalTextureOffset(int value) {
		privateVerticalTextureOffset = value;
	}
}