package com.mygame;

import loon.utils.timer.GameTime;

public class LoadingScreen extends GameScreen {
	private double delay;

	private MainGame game;
	private boolean isLoading = true;
	private boolean mainMenuAdded;
	private MainMenuScreen mainMenuScreen;

	public LoadingScreen(MainGame game) {
		this.game = game;
		super.setTransitionOnTime(0f);
		super.setTransitionOffTime(0.5f);
		this.delay = 10.0;
	}

	@Override
	public void LoadContent() {
		this.mainMenuScreen = new MainMenuScreen(this.game,
				ScreenType.LoadingScreen);
		this.mainMenuScreen.setScreenManager(super.getScreenManager());
		this.PreloadResources();
	}

	private void PreloadResources() {
		mainMenuScreen.PreloadAssets();
		this.isLoading = false;
	}

	@Override
	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		this.delay -= gameTime.getMilliseconds();
		if (((!super.getIsExiting() && (this.delay < 0.0)))
				&& (!this.mainMenuAdded && !this.isLoading)) {
			super.getScreenManager().ExitAllScreens();
			super.getScreenManager().AddScreen(this.mainMenuScreen);
			this.mainMenuAdded = true;
		}
		super.Update(gameTime, otherScreenHasFocus, coveredByOtherScreen);
	}
}