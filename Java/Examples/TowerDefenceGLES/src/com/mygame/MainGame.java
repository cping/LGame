package com.mygame;

import java.util.ArrayList;

import loon.LKey;
import loon.LTouch;
import loon.Touch;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableScreen;
import loon.core.timer.GameTime;

public class MainGame extends DrawableScreen {

	private ScreenManager screenManager;

	private java.util.ArrayList<CompletedLevel> privateCompletedLevels = new ArrayList<CompletedLevel>();

	public final java.util.ArrayList<CompletedLevel> getCompletedLevels() {
		return privateCompletedLevels;
	}

	public final void setCompletedLevels(
			java.util.ArrayList<CompletedLevel> value) {
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

		Touch.startTouchCollection();
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
	public void pressed(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drag(LTouch e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressed(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void released(LKey e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GameTime gameTime) {
		// TODO Auto-generated method stub

	}

}