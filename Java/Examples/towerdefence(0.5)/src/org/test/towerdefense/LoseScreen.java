package org.test.towerdefense;

import loon.geom.Vector2f;

public class LoseScreen extends MenuScreen {
	private MainGame game;
	private LoseScreenSpriteWithText loseScreenSpriteWithText;

	public LoseScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(96f, 396f));
		item.setnoButtonBackgroundSize(new Vector2f(140f, 50f));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ButtonSelected();

			}
		};
		super.getMenuEntries().add(item);
		this.loseScreenSpriteWithText = new LoseScreenSpriteWithText(game);
	}

	private void ButtonSelected() {
		this.HandleButtonSelected();
	}

	private void Exit() {
		if (this.loseScreenSpriteWithText != null) {
			this.game.Components().remove(this.loseScreenSpriteWithText);
		}
	}

	private void HandleButtonSelected() {
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.LoseScreen));
		this.Exit();
	}

	@Override
	protected void OnCancel() {
		this.HandleButtonSelected();
	}
}