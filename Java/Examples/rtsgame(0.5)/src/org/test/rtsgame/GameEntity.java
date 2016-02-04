package org.test.rtsgame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableState;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

public abstract class GameEntity {

	private boolean isExiting;
	private boolean isPopup;

	private EntityManager screenManager;
	private DrawableState screenState = DrawableState.values()[0];
	private float transitionOffTime = 0f;
	private float transitionOnTime = 0f;
	private float transitionPosition = 1f;

	public void reset() {
		transitionOffTime = 0f;
		transitionOnTime = 0f;
		transitionPosition = 1f;

	}

	protected GameEntity() {
	}

	public void Draw(SpriteBatch batch, GameTime gameTime) {
	}

	public final void ExitScreen() {
		if (this.getTransitionOffTime() == 0f) {
			this.getScreenManager().RemoveScreen(this);
		} else {
			this.isExiting = true;
		}
	}

	public void HandleInput() {

	}

	public void LoadContent() {
	}

	public void UnloadContent() {
	}

	private boolean otherScreenHasFocus;

	public void Update(GameTime gameTime, boolean coveredByOtherScreen) {
		this.otherScreenHasFocus = false;
		if (this.isExiting) {
			this.screenState = DrawableState.TransitionOff;
			if (!this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this.screenManager.RemoveScreen(this);
				reset();
			}
		} else if (coveredByOtherScreen) {
			if (this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this.screenState = DrawableState.TransitionOff;
			} else {
				this.screenState = DrawableState.Hidden;
			}
		} else if (this.updateTransition(gameTime, this.transitionOnTime, -1)) {
			this.screenState = DrawableState.TransitionOn;
		} else {
			this.screenState = DrawableState.Active;
		}
	}

	private boolean updateTransition(GameTime gameTime, float time,
			int direction) {
		float num;
		if (time == 0f) {
			num = 1f;
		} else {
			num = (float) ((gameTime.getElapsedGameTime() * 1000f) / (time * 1000f));
		}
		this.transitionPosition += num * direction;

		if (((direction >= 0) || (this.transitionPosition > 0f))
				&& ((direction <= 0) || (this.transitionPosition < 1f))) {
			return true;
		}
		this.transitionPosition = MathUtils.clamp(this.transitionPosition, 0f,
				1f);
		return false;
	}

	public final boolean getIsActive() {
		if (this.otherScreenHasFocus) {
			return false;
		}
		if (this.screenState != DrawableState.TransitionOn) {
			return (this.screenState == DrawableState.Active);
		}
		return true;
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

	public final EntityManager getScreenManager() {
		return this.screenManager;
	}

	public final void setScreenManager(EntityManager value) {
		this.screenManager = value;
	}

	public final DrawableState getScreenState() {
		return this.screenState;
	}

	protected final void setScreenState(DrawableState value) {
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