package org.test.towerdefense;

import loon.LTransition;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.events.GameKey;
import loon.events.GameTouch;
import loon.events.SysTouch;
import loon.utils.TArray;
import loon.utils.timer.GameTime;

public class MainGame extends DrawableScreen {

	private ScreenManager screenManager;

	private TArray<CompletedLevel> privateCompletedLevels = new TArray<CompletedLevel>();

	public final TArray<CompletedLevel> getCompletedLevels() {
		return privateCompletedLevels;
	}

	public final void setCompletedLevels(
			TArray<CompletedLevel> value) {
		privateCompletedLevels = value;
	}

	private GameplayScreen privateGameplayScreen;

	public final GameplayScreen getGameplayScreen() {
		return privateGameplayScreen;
	}

	public final void setGameplayScreen(GameplayScreen value) {
		privateGameplayScreen = value;
	}

	private boolean privateIsTrialMode;

	public final boolean getIsTrialMode() {
		return privateIsTrialMode;
	}

	public final void setIsTrialMode(boolean value) {
		privateIsTrialMode = value;
	}

	private boolean privateSoundEnabled;

	public final boolean getSoundEnabled() {
		return privateSoundEnabled;
	}

	public final void setSoundEnabled(boolean value) {
		privateSoundEnabled = value;
	}

	private boolean privateVibrationEnabled;

	public final boolean getVibrationEnabled() {
		return privateVibrationEnabled;
	}

	public final void setVibrationEnabled(boolean value) {
		privateVibrationEnabled = value;
	}

	public void draw(SpriteBatch batch) {

	}

	@Override
	public void loadContent() {

		SysTouch.startTouchCollection();
		this.screenManager = new ScreenManager(this);
		this.setSoundEnabled(true);
		this.setVibrationEnabled(this.getSoundEnabled());
		this.screenManager.AddScreen(new BackgroundScreen("loading"));
		this.screenManager.AddScreen(new LoadingScreen(this));
		super.Components().add(this.screenManager);

	}

	@Override
	public void unloadContent() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(GameTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(GameKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime gameTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}