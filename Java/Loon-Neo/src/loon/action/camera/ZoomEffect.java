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
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.Easing;
import loon.utils.timer.EaseTimer;
import loon.utils.timer.LTimerContext;

public class ZoomEffect implements ViewportEffect {

	private boolean _running;

	protected Viewport _viewport;

	protected Vector2f _source = new Vector2f();

	protected Vector2f current = new Vector2f();

	protected Vector2f _destination = new Vector2f();

	protected EaseTimer _ease;

	private Updateable _onUpdate;

	public ZoomEffect(Easing.EasingMode mode, Vector2f zoom, float timer, Viewport view) {
		this._ease = EaseTimer.at(timer, mode);
		this._viewport = view;
		this._source.set(_viewport.getScale());
		this._destination.set(zoom);
	}

	@Override
	public boolean isRunning() {
		return _running;
	}

	public ZoomEffect setDestination(float x, float y) {
		_destination.set(x, y);
		return this;
	}

	public Vector2f getDestination() {
		return _destination;
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
			Vector2f src = this._source.add(((this._destination.sub(this._source)).mul(this._ease.getProgress())));
			_viewport.setScale(src);
			if (this._onUpdate != null) {
				this._onUpdate.action(this);
			}
		} else {
			_viewport.setScale(this._destination);
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
		this._source = null;
		this._destination = null;
		this._running = false;
	}

}
