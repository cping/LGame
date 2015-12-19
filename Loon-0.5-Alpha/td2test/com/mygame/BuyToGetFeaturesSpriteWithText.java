package com.mygame;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class BuyToGetFeaturesSpriteWithText extends Sprite {

	private AnimatedSprite animatedSpriteTowerLur;

	private AnimatedSprite animatedSpriteTowerSpear;

	private LFont font;

	private LFont fontHuge;

	private LFont fontMedium;

	private MainGame game;

	public BuyToGetFeaturesSpriteWithText(MainGame game) {
		super(game, "assets/buytogetfeatures.png", 0, new Vector2f(0f, 0f));
		this.game = game;
		this.animatedSpriteTowerSpear = AnimatedSpriteTower
				.GetAnimatedSpriteTowerForTowerToolbar(game, new Vector2f(109f,
						154f), TowerType.Spear, 0.4f);
		this.animatedSpriteTowerSpear.setDrawOrder(0x149);
		this.animatedSpriteTowerSpear.setAnimationSpeedRatio(3);
		this.animatedSpriteTowerSpear.setObeyGameOpacity(false);
		this.animatedSpriteTowerSpear.setOnlyAnimateIfGameStateStarted(false);
		this.animatedSpriteTowerLur = AnimatedSpriteTower
				.GetAnimatedSpriteTowerForTowerToolbar(game, new Vector2f(269f,
						132f), TowerType.Lur, 0.4f);
		this.animatedSpriteTowerLur.setDrawOrder(0x149);
		this.animatedSpriteTowerLur.setAnimationSpeedRatio(3);
		this.animatedSpriteTowerLur.setObeyGameOpacity(false);
		this.animatedSpriteTowerLur.setOnlyAnimateIfGameStateStarted(false);
		game.Components().add(this.animatedSpriteTowerSpear);
		game.Components().add(this.animatedSpriteTowerLur);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		super.draw(batch, gameTime);
		Utils.DrawStringAlignCenter(batch, this.fontHuge,
				LanguageResources.getBuyGame(), 160f, 3f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.fontMedium,
				LanguageResources.getPleaseUnlock(), new Vector2f(160f, 74f),
				LColor.white);
		int num = 8;
		int num2 = 0xae;
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getMediumDifficulty(), num, 102f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getSpearTower(), num, 132f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getHardDifficulty(), num, 162f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getLurTower(), num2, 102f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getTwoExtraLevels(), num2, 132f,
				LColor.white);
		Utils.DrawStringAlignLeft(batch, this.font,
				"* " + LanguageResources.getSupportTheGame(), num2, 162f,
				LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font, LanguageResources
				.getUnlockGame().toUpperCase(), 94f, 433f, LColor.white);
		Utils.DrawStringAlignCenter(batch, this.font,
				LanguageResources.getNotNow(), 242f, 433f, LColor.white);
	}

	public final void Exit() {
		if (this.animatedSpriteTowerSpear != null) {
			this.game.Components().remove(this.animatedSpriteTowerSpear);
		}
		if (this.animatedSpriteTowerLur != null) {
			this.game.Components().remove(this.animatedSpriteTowerLur);
		}
		this.game.Components().remove(this);
	}

	@Override
	protected void loadContent() {
		this.fontHuge = LFont.getFont(32);
		this.fontMedium = LFont.getFont(16);
		this.font = LFont.getFont(12);
		super.loadContent();
	}
}