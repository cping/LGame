/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.sprite.effect;

import loon.LRelease;
import loon.LSystem;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.events.DrawLoop;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public abstract class BaseAbstractEffect extends Entity implements BaseEffect {

	private DrawLoop.Drawable _completedDrawable;

	protected final LTimer _timer = new LTimer(0);

	protected boolean _completed;

	protected boolean _autoRemoved;

	private boolean _completedAfterBlack;

	private boolean _completedEventOver;

	private LRelease _completedDispose;

	private LRelease _removedDispose;

	public BaseAbstractEffect() {
		super();
		setColor(LColor.black);
		setRepaint(true);
	}

	public LTimer getTimer() {
		return _timer;
	}

	public BaseAbstractEffect setDelay(long delay) {
		_timer.setDelay(delay);
		return this;
	}

	public BaseAbstractEffect setDelayS(float s) {
		_timer.setDelayS(s);
		return this;
	}

	public long getDelay() {
		return _timer.getDelay();
	}

	public float getDelayS() {
		return _timer.getDelayS();
	}

	public BaseAbstractEffect effectOver() {
		_completed = true;
		return this;
	}

	public boolean completedAfterBlackScreen(GLEx g, float x, float y) {
		final boolean result = _completed && _completedAfterBlack;
		if (result) {
			if (_completedDrawable == null) {
				g.fillRect(drawX(x), drawY(y), getWidth(), getHeight(), _baseColor);
			} else {
				_completedDrawable.draw(g, x, y);
			}
		}
		return result;
	}

	public boolean checkAutoRemove() {
		if (this._completed) {
			if (!_completedEventOver) {
				if (_completedDispose != null) {
					_completedDispose.close();
				}
				_completedEventOver = false;
			}
			if (_autoRemoved) {
				if (getSprites() != null) {
					getSprites().remove(this);
				} else if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
					LSystem.getProcess().getScreen().remove(this);
				}
				if (_removedDispose != null) {
					_removedDispose.close();
				}
			}
		}
		return this._completed;
	}

	@Override
	public BaseAbstractEffect reset() {
		super.reset();
		_completed = false;
		_completedAfterBlack = false;
		_completedEventOver = false;
		return this;
	}

	@Override
	public boolean isCompleted() {
		return _completed;
	}

	@Override
	public BaseAbstractEffect setStop(boolean c) {
		this._completed = c;
		return this;
	}

	public boolean isAutoRemoved() {
		return _autoRemoved;
	}

	public BaseAbstractEffect setAutoRemoved(boolean autoRemoved) {
		this._autoRemoved = autoRemoved;
		return this;
	}

	public BaseAbstractEffect removedDispose(LRelease rd) {
		this._removedDispose = rd;
		return this;
	}

	public BaseAbstractEffect completedDispose(LRelease rd) {
		this._completedDispose = rd;
		return this;
	}

	public boolean isCompletedAfterBlack() {
		return _completedAfterBlack;
	}

	public BaseAbstractEffect setCompletedAfterBlack(boolean c) {
		this._completedAfterBlack = c;
		return this;
	}

	public DrawLoop.Drawable getCompletedDrawable() {
		return _completedDrawable;
	}

	public BaseAbstractEffect setCompletedDrawable(DrawLoop.Drawable drawable) {
		this._completedDrawable = drawable;
		if (drawable != null) {
			setCompletedAfterBlack(true);
		}
		return this;
	}

	@Override
	public void _onDestroy() {
		super._onDestroy();
		_completed = true;
		_completedAfterBlack = false;
		_completedEventOver = false;
	}

}
