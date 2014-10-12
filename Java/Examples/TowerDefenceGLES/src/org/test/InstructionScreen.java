package org.test;

import loon.core.geom.Vector2f;
import loon.core.input.LInput;
import loon.core.timer.GameTime;

public class InstructionScreen extends MenuScreen {
	private MainGame game;
	private InstructionsScreenSpriteWithText instructionsScreenSpriteWithText;

	public InstructionScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.game = game;
		super.setScreenType(ScreenType.InstructionsScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(7f, 425f));
		item.setnoButtonBackgroundSize(new Vector2f(94f, 38f));
		MenuEntry entry2 = new MenuEntry("");
		entry2.setuseButtonBackground(false);
		entry2.setPosition(new Vector2f(128f, 425f));
		entry2.setnoButtonBackgroundSize(new Vector2f(94f, 38f));
		MenuEntry entry3 = new MenuEntry("");
		entry3.setuseButtonBackground(false);
		entry3.setPosition(new Vector2f(250f, 425f));
		entry3.setnoButtonBackgroundSize(new Vector2f(58f, 38f));

		entry2.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMonsterInfoSelected();
			}
		};
		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartTowerInfoSelected();
			}
		};

		entry3.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartMainMenuSelected();

			}
		};
		super.getMenuEntries().add(item);
		super.getMenuEntries().add(entry2);
		super.getMenuEntries().add(entry3);
		this.instructionsScreenSpriteWithText = new InstructionsScreenSpriteWithText(
				game);
	}

	private void Exit() {
		if (this.instructionsScreenSpriteWithText != null) {
			this.game.Components()
					.remove(this.instructionsScreenSpriteWithText);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, LInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	protected void OnCancel() {
		this.Exit();
		super.OnCancel();
	}

	private void StartMainMenuSelected() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new MainMenuScreen(this.game, ScreenType.InstructionsScreen));
	}

	private void StartMonsterInfoSelected() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager()
				.AddScreen(
						new MonsterInfoScreen(this.game,
								ScreenType.InstructionsScreen));
	}

	private void StartTowerInfoSelected() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new TowerInfoScreen(this.game, ScreenType.InstructionsScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}