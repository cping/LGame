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
package loon.action.camera;

import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.opengl.GLEx;
import loon.utils.Easing;
import loon.utils.MathUtils;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimerContext;

public class RotateEffect implements ViewportEffect {

	private boolean _running;

	protected Viewport _viewport;

	protected float _source = 0f;

	protected float _current = 0f;

	protected float _destination = 0f;

	protected float _radians = 0f;

	protected boolean _clockwise;

	protected boolean _shortestPath;

	protected EaseTimer _ease;

	private Updateable _onUpdate;

	public RotateEffect(Easing.EasingMode mode, float angle, boolean shortestPath, float timer, Viewport view) {
		this._ease = EaseTimer.at(timer, mode);
		this._shortestPath = shortestPath;
		this._radians = MathUtils.toRadians(angle);
		this._viewport = view;
	}

	@Override
	public boolean isRunning() {
		return _running;
	}

	public RotateEffect setSource(float f) {
		_source = f;
		return this;
	}

	public float getSource() {
		return _source;
	}

	public RotateEffect setDestination(float d) {
		_destination = d;
		return this;
	}

	public float getDestination() {
		return _destination;
	}

	@Override
	public void start() {
		this._source = _viewport.getRotation();
		float tmpDst = _radians;
		if (tmpDst < 0) {
			tmpDst = -1f * tmpDst;
			this._clockwise = false;
		} else {
			this._clockwise = true;
		}
		float maxRad = (360 * MathUtils.PI) / 180;
		tmpDst = tmpDst - (MathUtils.floor(tmpDst / maxRad) * maxRad);
		this._destination = tmpDst;
		if (this._shortestPath) {
			float cwDist = 0f;
			float acwDist = 0f;
			if (this._destination > this._source) {
				cwDist = MathUtils.abs(this._destination - this._source);
			} else {
				cwDist = (MathUtils.abs(this._destination + maxRad) - this._source);
			}
			if (this._source > this._destination) {
				acwDist = MathUtils.abs(this._source - this._destination);
			} else {
				acwDist = (MathUtils.abs(this._source + maxRad) - this._destination);
			}
			if (cwDist < acwDist) {
				this._clockwise = true;
			} else if (cwDist > acwDist) {
				this._clockwise = false;
			}
		}
		_ease.start();
		_running = true;
	}

	@Override
	public void stop() {
		_ease.stop();
		_running = false;
	}

	@Override
	public EaseTimer getEaseTimer() {
		return _ease;
	}

	@Override
	public void update(LTimerContext timer) {
		if (!_running) {
			return;
		}
		if (!_ease.action(timer)) {
			float v = _ease.getProgress();
			this._current = _viewport.getRotation();
			float distance = 0;
			float maxRad = (360 * MathUtils.PI) / 180;
			float target = this._destination;
			float current = this._current;
			if (!this._clockwise) {
				target = this._current;
				current = this._destination;
			}
			if (target >= current) {
				distance = MathUtils.abs(target - current);
			} else {
				distance = (MathUtils.abs(target + maxRad) - current);
			}
			float r = 0;
			if (this._clockwise) {
				r = (_viewport.getRotation() + distance) * v;
			} else {
				r = (_viewport.getRotation() - distance) * v;
			}
			_viewport.setRotation(r);
			if (this._onUpdate != null) {
				this._onUpdate.action(this);
			}
		} else {
			_viewport.setRotation(this._destination);
			if (this._onUpdate != null) {
				this._onUpdate.action(this);
			}
			this.onEffectComplete();
		}
	}

	@Override
	public void draw(GLEx g, Affine2f view) {

	}

	protected void onEffectComplete() {
		this._onUpdate = null;
		this._running = false;
		this._ease.stop();
	}

	@Override
	public ViewportEffect setUpdate(Updateable u) {
		this._onUpdate = u;
		return this;
	}

	@Override
	public void reset() {
		this._ease.reset();
		this._onUpdate = null;
		this._running = true;
	}

	@Override
	public void close() {
		this.reset();
		this._viewport = null;
		this._source = 0f;
		this._destination = 0f;
		this._running = false;
	}
}