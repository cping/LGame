/**
 * Copyright 2024 The Loon Game Engine Authors
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
package loon;

import loon.action.behaviors.ISystem;
import loon.utils.timer.LTimer;

/**
 * 系统任务的抽象实现,具体实现后可注入Screen实现多功能系统插件的作用
 */
public abstract class ScreenSystem implements ISystem {

	private int _priority;

	private boolean _running;

	private final LTimer _timer = new LTimer(0);

	private ScreenSystemManager _manager;

	public ScreenSystem() {
		this(0);
	}

	public ScreenSystem(int priority) {
		this._priority = priority;
		this._running = true;
	}

	void setSystemManager(ScreenSystemManager m) {
		this._manager = m;
	}

	public LTimer getTimer() {
		return this._timer;
	}

	public ScreenSystem setDelay(long d) {
		this._timer.setDelay(d);
		return this;
	}

	public ScreenSystem setDelayS(float s) {
		this._timer.setDelayS(s);
		return this;
	}

	public long getDelay() {
		return this._timer.getDelay();
	}

	public float getDelayS() {
		return this._timer.getDelayS();
	}

	void action(long elapsedTime) {
		if (_running && _timer.action(elapsedTime)) {
			loop();
			update(elapsedTime);
		}
	}

	public void update(long elapsedTime) {

	}

	public boolean isRunning() {
		return this._running;
	}

	public ScreenSystem setRunning(boolean r) {
		this._running = r;
		return this;
	}

	public ScreenSystemManager getManager() {
		return this._manager;
	}

	public int getPriority() {
		return _priority;
	}

	public ScreenSystem setPriority(int p) {
		this._priority = p;
		return this;
	}

	public ScreenSystem start() {
		return setRunning(true);
	}

	public ScreenSystem stop() {
		return setRunning(false);
	}

	public abstract void exit();

	@Override
	public void close() {
		exit();
		stop();
	}
}
