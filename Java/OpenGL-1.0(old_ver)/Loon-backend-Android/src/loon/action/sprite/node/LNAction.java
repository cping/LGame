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
package loon.action.sprite.node;

import loon.utils.MathUtils;

public abstract class LNAction {

	protected Easing _easing;

	protected float _duration;

	protected float _elapsed;

	protected boolean _firstTick = true;

	protected boolean _isEnd;

	protected boolean _isPause = false;

	protected LNNode _target;

	public final void assignTarget(LNNode node) {
		this._target = node;
	}

	public void pause() {
		this._isPause = true;
	}

	public void resume() {
		this._isPause = false;
	}

	public void setTarget(LNNode node) {
		this._firstTick = true;
		this._isEnd = false;
		this._target = node;
	}

	public abstract LNAction copy();

	public void start() {
		this.setTarget(this._target);
	}

	public void step(float dt) {
		if (!this._isPause) {
			if (this._firstTick) {
				this._firstTick = false;
				this._elapsed = 0f;
			} else {
				this._elapsed += dt;
			}
			float fx = 0;
			if (_easing != null) {
				fx = _easing.ease((this._elapsed / this._duration), 1f);
			} else {
				fx = MathUtils.min((this._elapsed / this._duration), 1f);
			}
			this.update(fx);
		}
	}

	public void reset() {
		this._elapsed = 0;
		this._isEnd = false;
	}

	public void stop() {
		this._isEnd = true;
	}

	public void update(float time) {
	}

	public final float getDuration() {
		return this._duration;
	}

	public final float getElapsed() {
		return this._elapsed;
	}

	public final boolean isEnd() {
		return this._isEnd;
	}

	public Easing getEasing() {
		return _easing;
	}

	public void setEasing(Easing e) {
		this._easing = e;
	}
}
