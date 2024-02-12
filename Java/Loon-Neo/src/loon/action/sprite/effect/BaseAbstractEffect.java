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
import loon.utils.timer.LTimer;

public abstract class BaseAbstractEffect extends Entity implements BaseEffect {

	protected final LTimer _timer = new LTimer(0);

	protected boolean _completed;

	protected boolean _autoRemoved;

	private LRelease _removedDispose;

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

	public boolean checkAutoRemove() {
		if (this._completed) {
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

	@Override
	public void close() {
		super.close();
		_completed = true;
	}

	public LRelease getRemovedDispose() {
		return _removedDispose;
	}

}
