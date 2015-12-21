package com.mygame;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColorPool;
import loon.event.SysInput;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

public abstract class GameScreen {

	public LColorPool PoolColor = new LColorPool();

	private boolean isExiting;
	private boolean isPopup;
	private boolean isSerializable = true;
	private boolean otherScreenHasFocus;
	private ScreenManager screenManager;
	private ScreenState screenState;
	private float transitionOffTime = 0f;
	private float transitionOnTime = 0f;
	private float transitionPosition = 1f;

	protected GameScreen() {
	}

	public void draw(SpriteBatch batch, GameTime gameTime) {
	}

	public final void ExitScreen() {
		if (this.getTransitionOffTime() == 0f) {
			this.getScreenManager().RemoveScreen(this);
		} else {
			this.isExiting = true;
		}
	}

	public void HandleInput(GameTime gameTime, SysInput input) {
	}

	public void LoadContent() {
	}

	public void UnloadContent() {
	}

	public void Update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		this.otherScreenHasFocus = otherScreenHasFocus;
		if (this.isExiting) {
			this.screenState = ScreenState.TransitionOff;
			if (!this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this.getScreenManager().RemoveScreen(this);
			}
		} else if (coveredByOtherScreen) {
			if (this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this.screenState = ScreenState.TransitionOff;
			} else {
				this.screenState = ScreenState.Hidden;
			}
		} else if (this.updateTransition(gameTime, this.transitionOnTime, -1)) {
			this.screenState = ScreenState.TransitionOn;
		} else {
			this.screenState = ScreenState.Active;
		}
	}

	private boolean updateTransition(GameTime gameTime, float time,
			int direction) {
		float num;
		if (time == 0f) {
			num = 1f;
		} else {
			num = (gameTime.getElapsedGameTime() / time);
		}

		this.transitionPosition += num * direction;
		if (((direction < 0) && (this.transitionPosition <= 0f))
				|| ((direction > 0) && (this.transitionPosition >= 1f))) {
			this.transitionPosition = MathUtils.clamp(this.transitionPosition,
					0f, 1f);
			return false;
		}
		return true;
	}

	private int privateDrawOrder;

	public final int getDrawOrder() {
		return privateDrawOrder;
	}

	public final void setDrawOrder(int value) {
		privateDrawOrder = value;
	}

	public final boolean getIsActive() {
		return (!this.otherScreenHasFocus && (this.screenState == ScreenState.Active));
	}

	public final boolean getIsExiting() {
		return this.isExiting;
	}

	protected final void setIsExiting(boolean value) {
		this.isExiting = value;
	}

	public final boolean getIsPopup() {
		return this.isPopup;
	}

	protected final void setIsPopup(boolean value) {
		this.isPopup = value;
	}

	public final boolean getIsSerializable() {
		return this.isSerializable;
	}

	protected final void setIsSerializable(boolean value) {
		this.isSerializable = value;
	}

	public final ScreenManager getScreenManager() {
		return this.screenManager;
	}

	public final void setScreenManager(ScreenManager value) {
		this.screenManager = value;
	}

	public final ScreenState getScreenState() {
		return this.screenState;
	}

	protected final void setScreenState(ScreenState value) {
		this.screenState = value;
	}

	public final float getTransitionAlpha() {
		return (1f - this.getTransitionPosition());
	}

	public final float getTransitionOffTime() {
		return this.transitionOffTime;
	}

	protected final void setTransitionOffTime(float value) {
		this.transitionOffTime = value;
	}

	public final float getTransitionOnTime() {
		return this.transitionOnTime;
	}

	protected final void setTransitionOnTime(float value) {
		this.transitionOnTime = value;
	}

	public final float getTransitionPosition() {
		return this.transitionPosition;
	}

	protected final void setTransitionPosition(float value) {
		this.transitionPosition = value;
	}
}