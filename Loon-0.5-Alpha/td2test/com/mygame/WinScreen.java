package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class WinScreen extends MenuScreen {
	private MainGame game;
	private LTexture texture;
	private Sprite winScreenSpriteWithText;

	public WinScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		Vector2f vector;
		this.game = game;
		super.setTransitionOnTime(2f);
		super.setTransitionOffTime(0.5f);

		boolean flag = false;
		boolean flag2 = false;
		boolean flag3 = false;
		for (CompletedLevel level : game.getCompletedLevels()) {
			if (level.getDifficulty() == 2) {
				if (level.getLevel() == 1) {
					flag = true;
				} else {
					if (level.getLevel() == 2) {
						flag2 = true;
						continue;
					}
					if (level.getLevel() == 3) {
						flag3 = true;
					}
				}
			}
		}
		if (((game.getGameplayScreen().getDifficulty() == Difficulty.Hard) && flag)
				&& (flag2 && flag3)) {
			vector = new Vector2f(96f, 431f);
			this.winScreenSpriteWithText = new GameCompletedScreenSpriteWithText(
					game);
		} else {
			vector = new Vector2f(96f, 396f);
			this.winScreenSpriteWithText = new WinScreenSpriteWithText(game);
		}
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(vector);
		item.setnoButtonBackgroundSize(new Vector2f(140f, 50f));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				ButtonSelected();

			}
		};
		super.getMenuEntries().add(item);
	}

	private void ButtonSelected() {
		this.HandleButtonSelected();
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, new Vector2f(0f, 0f), LColor.white);
		super.draw(batch, gameTime);
	}

	private void Exit() {
		if (this.winScreenSpriteWithText != null) {
			this.game.Components().remove(this.winScreenSpriteWithText);
		}
	}

	private void HandleButtonSelected() {
		super.getScreenManager().ExitAllScreens();
		if (this.game.getIsTrialMode()) {
			super.getScreenManager().AddScreen(
					new BuyToGetFeaturesScreen(this.game,
							ScreenType.MainMenuScreen, null));
		} else {
			super.getScreenManager().AddScreen(
					new MainMenuScreen(this.game, ScreenType.WinScreen));
		}
		this.Exit();
	}

	@Override
	public void LoadContent() {
		this.texture = LTextures.loadTexture("assets/win.png");
		super.LoadContent();
	}

	@Override
	protected void OnCancel() {
		this.HandleButtonSelected();
	}
}