package org.test;

import loon.core.geom.Vector2f;

public class ConfirmScreen extends MenuScreen {
	private ConfirmScreenSpriteWithText confirmScreenSpriteWithText;
	private ConfirmType confirmType = ConfirmType.values()[0];
	private MainGame game;

	public ConfirmScreen(MainGame game, ScreenType prevScreen,
			ConfirmType confirmType) {
		super("", game, prevScreen);
		this.game = game;
		this.confirmType = confirmType;
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0f);
		super.setIsPopup(true);
		Vector2f vector = new Vector2f(60f, 50f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(48f, 220f));
		item.setnoButtonBackgroundSize(vector);
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(206f, 220f));
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
		this.confirmScreenSpriteWithText = new ConfirmScreenSpriteWithText(game);
		this.confirmScreenSpriteWithText.setDrawOrder(100);
		game.Components().add(this.confirmScreenSpriteWithText);
	}

	private void Exit() {
		if (this.confirmScreenSpriteWithText != null) {
			this.game.Components().remove(this.confirmScreenSpriteWithText);
		}
	}

	private void HandleNo() {
		this.Exit();
		super.ExitScreen();
		this.game.getGameplayScreen().GameResumed();
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
		super.getScreenManager().ExitAllScreens();
		if (this.confirmType == ConfirmType.ExitToMainMenu) {
			this.game.setGameplayScreen(null);
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game, ScreenType.ConfirmScreen));
		} else if (this.confirmType == ConfirmType.RestartGame) {
			super.getScreenManager().AddScreen(
					new GameplayScreen(this.game, this.game.getGameplayScreen()
							.getDifficulty(), this.game.getGameplayScreen()
							.getLevel()));
		}
	}
}