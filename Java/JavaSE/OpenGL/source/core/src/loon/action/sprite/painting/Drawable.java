/**
 * Copyright 2008 - 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.3.3
 */
package loon.action.sprite.painting;

import loon.LInput;
import loon.LKey;
import loon.LTouch;
import loon.action.sprite.SpriteBatch;
import loon.core.LRelease;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public abstract class Drawable implements LRelease {

	public boolean IsPopup = false;

	public float transitionOnTime = 0;

	public float transitionOffTime = 0;

	public Vector2f bottomLeftPosition = new Vector2f();

	public DrawableScreen drawableScreen;

	boolean otherScreenHasFocus;

	protected float _transitionPosition = 1f;

	protected DrawableState _drawableState = DrawableState.TransitionOn;

	protected boolean _enabled = true;

	protected boolean _isExiting = false;

	public DrawableScreen getDrawableScreen() {
		return drawableScreen;
	}

	public void exitScreen() {
		if (this.transitionOffTime == 0f) {
			this.drawableScreen.removeDrawable(this);
		} else {
			this._isExiting = true;
		}
	}

	public Vector2f getBottomLeftPosition() {
		return bottomLeftPosition;
	}

	public DrawableState getDrawableState() {
		return _drawableState;
	}

	public void setDrawableState(DrawableState state) {
		_drawableState = state;
	}

	public float getTransitionAlpha() {
		return (1f - this._transitionPosition);
	}

	public float getTransitionPosition() {
		return _transitionPosition;
	}

	public abstract void handleInput(LInput input);

	public boolean isActive() {
		return !otherScreenHasFocus
				&& (_drawableState == DrawableState.TransitionOn || _drawableState == DrawableState.Active);
	}

	public boolean isExiting() {
		return _isExiting;
	}

	public abstract void loadContent();

	public abstract void unloadContent();

	public abstract void draw(SpriteBatch batch, GameTime elapsedTime);

	public abstract void update(GameTime elapsedTime);

	public void update(GameTime gameTime, boolean otherScreenHasFocus,
			boolean coveredByOtherScreen) {
		this.otherScreenHasFocus = otherScreenHasFocus;
		if (this._isExiting) {
			this._drawableState = DrawableState.TransitionOff;
			if (!this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this.drawableScreen.removeDrawable(this);
			}
		} else if (coveredByOtherScreen) {
			if (this.updateTransition(gameTime, this.transitionOffTime, 1)) {
				this._drawableState = DrawableState.TransitionOff;
			} else {
				this._drawableState = DrawableState.Hidden;
			}
		} else if (this.updateTransition(gameTime, this.transitionOnTime, -1)) {
			this._drawableState = DrawableState.TransitionOn;
		} else {
			this._drawableState = DrawableState.Active;
		}

		update(gameTime);
	}

	private boolean updateTransition(GameTime gameTime, float time,
			int direction) {
		float num;
		if (time == 0f) {
			num = 1f;
		} else {
			num = (gameTime.getElapsedGameTime() / time);
		}

		this._transitionPosition += num * direction;
		if (((direction < 0) && (this._transitionPosition <= 0f))
				|| ((direction > 0) && (this._transitionPosition >= 1f))) {
			this._transitionPosition = MathUtils.clamp(
					this._transitionPosition, 0f, 1f);
			return false;
		}
		return true;
	}

	public abstract void pressed(LTouch e);

	public abstract void released(LTouch e);

	public abstract void move(LTouch e);

	public abstract void pressed(LKey e);

	public abstract void released(LKey e);

	public boolean isEnabled() {
		return _enabled;
	}

	public void setEnabled(boolean e) {
		this._enabled = e;
	}

	public void dispose() {
		this._enabled = false;
	}
}
