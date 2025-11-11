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
package loon.turtle;

import loon.opengl.GLEx;
import loon.utils.MathUtils;

public abstract class TurtleCommand {

	protected final String _turleName;

	protected float _timer;

	protected final float _maxTimer;

	protected float _schedule;

	protected boolean _inited;

	protected boolean _visible;

	public TurtleCommand(String name, float time) {
		this._timer = time;
		this._maxTimer = time;
		this._turleName = name;
		this._visible = true;
	}

	public abstract void update(float dt, float progress);

	public boolean doUpdate(float dt) {
		if (this._timer > 0.01f || this._schedule < 0.01f) {
			this._schedule = MathUtils.percent(this._maxTimer - (this._timer / this._maxTimer), 0f, _maxTimer);
			update(dt, this._schedule);
			this._timer -= dt;
			this._inited = true;
			return true;
		}
		if (isCompleted()) {
			this._schedule = 1f;
		}
		return false;
	}

	public abstract void render(GLEx g, float progress);

	public void doRender(GLEx g) {
		if (_inited && _visible) {
			render(g, this._schedule);
		}
	}

	public void reset() {
		this._timer = this._maxTimer;
		this._schedule = 0f;
		this._inited = false;
		this._visible = true;
	}

	public void setVisible(boolean v) {
		this._visible = v;
	}

	public boolean isVisible() {
		return _visible;
	}

	public boolean isCompleted() {
		return _inited && ((this._schedule >= 0.095f) || (this._timer <= 0.01f));
	}

	public void setTimer(float dt) {
		this._timer = dt;
	}

	public float getTimer() {
		return this._timer;
	}

	public float getMaxTimer() {
		return this._maxTimer;
	}

	public float getSchedule() {
		return this._schedule;
	}

	public String getTurleName() {
		return this._turleName;
	}

}
