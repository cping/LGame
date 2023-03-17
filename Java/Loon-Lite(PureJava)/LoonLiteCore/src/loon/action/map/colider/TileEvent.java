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
package loon.action.map.colider;

import loon.opengl.GLEx;
import loon.utils.timer.StopwatchTimer;

public abstract class TileEvent {

	private boolean live = true;
	public boolean repeat = false;
	public boolean triggered = false;

	private long ms;

	private StopwatchTimer sTimer;

	public void startWatch() {
		if (sTimer != null) {
			sTimer.start();
			init();
		}
	}

	public void stopWatch() {
		if (sTimer != null) {
			sTimer.stop();
		}
	}

	public void update() {
		if (sTimer != null && sTimer.isDone()) {
			this.live = false;
			completed();
		}
	}

	public abstract void init();

	public abstract void completed();

	public abstract void draw(GLEx g);

	public boolean isLive() {
		return this.live;
	}

	public void begin() {
		startWatch();
	}

	public void end() {
		stopWatch();
	}

	public void triggered() {
		if (this.repeat || !this.triggered) {
			this.sTimer = new StopwatchTimer(this.ms);
			this.live = true;
			this.triggered = true;
			this.begin();
		}
	}
}
