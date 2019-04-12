/**
 * Copyright 2008 - 2009
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
 * @version 0.1
 */
package loon.utils.timer;

import loon.utils.StringKeyValue;

/**
 * Loon的计时器类
 */
public class LTimer {

	protected static LTimer _instance = null;

	public static LTimer getInstance() {
		if (_instance == null) {
			_instance = new LTimer(0);
		}
		return _instance;
	}

	private boolean active;

	private long delay;

	private long currentTick;

	public static LTimer at() {
		return new LTimer();
	}

	public static LTimer at(long delay) {
		return new LTimer(delay);
	}

	public static LTimer at(Duration delay) {
		return new LTimer(delay);
	}

	public LTimer() {
		this(450);
	}

	public LTimer(Duration d) {
		this(d == null ? 0 : d.toMillisLong());
	}

	public LTimer(long delay) {
		this.delay = delay;
		this.active = true;
		currentTick = 0;
	}

	public boolean action(long elapsedTime) {
		if (this.active) {
			this.currentTick += elapsedTime;
			if (this.currentTick >= this.delay) {
				this.currentTick -= this.delay;
				return true;
			}
		}
		return false;
	}

	public boolean action(LTimerContext context) {
		if (this.active) {
			this.currentTick += context.timeSinceLastUpdate;
			if (this.currentTick >= this.delay) {
				this.currentTick -= this.delay;
				return true;
			}
		}
		return false;
	}

	public LTimer addPercentage(long elapsedTime) {
		this.currentTick += elapsedTime;
		return this;
	}

	public LTimer addPercentage(LTimerContext context) {
		this.currentTick += context.timeSinceLastUpdate;
		return this;
	}

	public LTimer refresh() {
		this.currentTick = 0;
		return this;
	}

	public LTimer setEquals(LTimer other) {
		this.active = other.active;
		this.delay = other.delay;
		this.currentTick = other.currentTick;
		return this;
	}

	public boolean isActive() {
		return this.active;
	}

	public LTimer start() {
		this.active = true;
		return this;
	}

	public LTimer stop() {
		this.active = false;
		return this;
	}

	public LTimer setActive(boolean bool) {
		this.active = bool;
		this.refresh();
		return this;
	}

	public long getDelay() {
		return this.delay;
	}

	public LTimer setDelay(Duration d) {
		return setDelay(d == null ? 0 : d.toMillisLong());
	}

	public LTimer setDelay(long delay) {
		this.delay = delay;
		this.refresh();
		return this;
	}

	public long getCurrentTick() {
		return this.currentTick;
	}

	public LTimer setCurrentTick(long tick) {
		this.currentTick = tick;
		return this;
	}

	public float getPercentage() {
		return (float) this.currentTick / (float) this.delay;
	}

	public float getRemaining() {
		return (float) (this.delay - this.currentTick);
	}

	public LTimer clamp() {
		if (this.currentTick > this.delay) {
			currentTick = delay;
		}
		return this;
	}

	public boolean isCompleted() {
		return this.currentTick >= this.delay;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("LTimer");
		builder.kv("currentTick", currentTick)
		.comma()
		.kv("delay", delay)
		.comma()
		.kv("active", active);
		return builder.toString();
	}
}
