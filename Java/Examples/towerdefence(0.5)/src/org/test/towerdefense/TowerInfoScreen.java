package org.test.towerdefense;

import loon.action.sprite.painting.IGameComponent;
import loon.event.SysInput;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class TowerInfoScreen extends MenuScreen {
	private java.util.ArrayList<AnimatedSpriteTower> animatedSprites;
	private MainGame game;
	private boolean isFirstExit;
	private TowerInfoScreenSpriteWithText towerInfoScreenSpriteWithText;

	public TowerInfoScreen(MainGame game, ScreenType prevScreen) {
		super("", game, prevScreen);
		this.isFirstExit = true;
		this.game = game;
		super.setScreenType(ScreenType.TowerInfoScreen);
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		MenuEntry item = new MenuEntry("");
		item.setuseButtonBackground(false);
		item.setPosition(new Vector2f(110f, 422f));
		item.setnoButtonBackgroundSize(new Vector2f(120f, 38f));

		item.Selected = new GameEvent() {

			@Override
			public void invoke(MenuEntry comp) {
				StartInstructionsMenuEntrySelected();
			}
		};
		super.getMenuEntries().add(item);
		this.towerInfoScreenSpriteWithText = new TowerInfoScreenSpriteWithText(
				game);
	}

	private void Exit() {
		if (this.towerInfoScreenSpriteWithText != null) {
			this.game.Components().remove(this.towerInfoScreenSpriteWithText);
		}
	}

	@Override
	public void HandleInput(GameTime gameTime, SysInput input) {
		super.HandleInput(gameTime, input);
	}

	@Override
	public void LoadContent() {
		this.animatedSprites = AnimatedSpriteTower
				.GetAllAnimatedSpriteTowers(this.game);
		for (AnimatedSpriteTower tower : this.animatedSprites) {
			tower.setOnlyAnimateIfGameStateStarted(false);
			tower.setObeyGameOpacity(false);
			super.getScreenManager().getGame().Components().add(tower);
		}
	}

	@Override
	protected void OnCancel() {
		this.Exit();
		super.OnCancel();
	}

	private void StartInstructionsMenuEntrySelected() {
		this.Exit();
		super.getScreenManager().ExitAllScreens();
		super.getScreenManager().AddScreen(
				new InstructionScreen(this.game, ScreenType.TowerInfoScreen));
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		if (super.getIsExiting() && this.isFirstExit) {
			for (IGameComponent component : this.animatedSprites) {
				super.getScreenManager().getGame().Components()
						.remove(component);
			}
			this.isFirstExit = false;
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}