package org.test.towerdefense;

import loon.geom.Vector2f;

public class BuyToGetFeaturesScreen extends MenuScreen {

	private BuyToGetFeaturesSpriteWithText buyToGetFeaturesSpriteWithText;

	private Difficulty difficulty;

	private MainGame game;

	public BuyToGetFeaturesScreen(MainGame game, ScreenType prevScreen,
			Difficulty difficulty) {
		super("", game, prevScreen);
		this.game = game;
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0f);
		super.setIsPopup(true);
		Vector2f vector = new Vector2f(60f, 50f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(48f, 425f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(222f, 425f));
		entry2.setnoButtonBackgroundSize(vector);

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				YesSelected();
			}
		};

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				NoSelected();
			}
		};

		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);

		this.difficulty = difficulty;
		this.buyToGetFeaturesSpriteWithText = new BuyToGetFeaturesSpriteWithText(
				game);
		this.buyToGetFeaturesSpriteWithText.setDrawOrder(100);
		game.Components().add(this.buyToGetFeaturesSpriteWithText);
	}

	private void Exit() {
		if (this.buyToGetFeaturesSpriteWithText != null) {
			this.buyToGetFeaturesSpriteWithText.Exit();
		}
	}

	private void HandleNo() {
		this.Exit();
		super.ExitScreen();
		if (super.prevScreen == ScreenType.GameplayScreen) {
			this.game.getGameplayScreen().GameResumed();
		} else if (super.prevScreen == ScreenType.MainMenuScreen) {
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game,
							ScreenType.BuyToGetFeaturesScreen));
		}
	}

	private void NoSelected() {
		this.HandleNo();
	}

	@Override
	protected void OnCancel() {
		this.HandleNo();
	}

	private void YesSelected() {
		this.Exit();
		super.ExitScreen();
		if (super.prevScreen == ScreenType.MainMenuScreen) {
			super.getScreenManager().ExitAllScreens();
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game,
							ScreenType.BuyToGetFeaturesScreen));
		} else if (super.prevScreen == ScreenType.SelectLevelScreen) {
			super.getScreenManager().ExitAllScreens();
			super.getScreenManager()
					.AddScreen(
							new SelectLevelScreen(this.game,
									ScreenType.BuyToGetFeaturesScreen,
									this.difficulty));
		}

	}
}