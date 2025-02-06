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

import loon.LSystem;
import loon.events.Updateable;
import loon.geom.Affine2f;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.Easing;
import loon.utils.MathUtils;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimerContext;

public class ShakeEffect implements ViewportEffect {

	private boolean _running;

	protected Viewport _viewport;

	protected Vector2f _intensity = new Vector2f(0.05f);

	protected float _destination = 0f;

	protected float _offsetX = 0f, _offsetY = 0f;

	protected EaseTimer _ease;

	private Updateable _onUpdate;

	public ShakeEffect(Easing.EasingMode mode, float offset, Viewport view) {
		this(mode, null, offset, offset, LSystem.DEFAULT_EASE_DELAY, view);
	}

	public ShakeEffect(Easing.EasingMode mode, Vector2f intensity, float offX, float offY, float timer, Viewport view) {
		this._ease = EaseTimer.at(timer, mode);
		if (intensity != null) {
			_intensity.set(intensity);
		}
		this._offsetX = offX;
		this._offsetY = offY;
		this._viewport = view;
	}

	@Override
	public boolean isRunning() {
		return _running;
	}

	public ShakeEffect setDestination(float d) {
		_destination = d;
		return this;
	}

	public float getDestination() {
		return _destination;
	}

	public ShakeEffect setOffset(float x, float y) {
		this._offsetX = x;
		this._offsetY = y;
		return this;
	}

	public float getOffsetX() {
		return this._offsetX;
	}

	public float getOffsetY() {
		return this._offsetY;
	}

	@Override
	public void start() {
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
			float width = _viewport.getDisplayWidth();
			float height = _viewport.getDisplayHeight();
			this._offsetX = (MathUtils.random() * _intensity.x * width * 2f - _intensity.x * width) * v;
			this._offsetY = (MathUtils.random() * _intensity.y * height * 2f - _intensity.y * height) * v;
			this._viewport.setDirty(true);
			if (this._onUpdate != null) {
				this._onUpdate.action(this);
			}
		} else {
			if (this._onUpdate != null) {
				this._onUpdate.action(this);
			}
			this.onEffectComplete();
		}
	}

	@Override
	public void draw(GLEx g, Affine2f view) {
		if (this._running) {
			view.translate(-this._offsetX, -this._offsetY);
		}
	}

	protected void onEffectComplete() {
		this._onUpdate = null;
		this._running = false;
		this._offsetX = this._offsetY = 0f;
		this._viewport.setDirty(true);
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
		this._intensity.set(0.05f);
		this._destination = 0f;
		this._running = false;
	}

}