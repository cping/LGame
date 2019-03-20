/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.timer;

import loon.utils.MathUtils;

public abstract class FloatTimerEvent {

	private float delay;
	private boolean repeat;
	private float acc;
	private boolean done;
	private boolean stopped;

	public FloatTimerEvent(float delay) {
		this(delay, false);
	}

	public FloatTimerEvent(float delay, boolean repeat) {
		this.delay = delay;
		this.repeat = repeat;
		this.acc = 0.0F;
	}

	public void update(long elapsedTime) {
		this.update(MathUtils.min(elapsedTime / 1000f, 0.1f));
	}

	public void update(float delta) {
		if ((!this.done) && (!this.stopped)) {
			this.acc += delta;

			if (this.acc >= this.delay) {
				this.acc -= this.delay;

				if (this.repeat)
					reset();
				else {
					this.done = true;
				}

				execute();
			}
		}
	}

	public void reset() {
		this.stopped = false;
		this.done = false;
		this.acc = 0.0F;
	}

	public boolean isCompleted() {
		return this.done;
	}

	public boolean isRunning() {
		return (!this.done) && (this.acc < this.delay) && (!this.stopped);
	}

	public void stop() {
		this.stopped = true;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public abstract void execute();

	public float getPercentage() {
		return this.acc / this.delay;
	}

	public float getRemaining() {
		return this.delay - this.acc;
	}

	public float getPercentageRemaining() {
		if (this.done)
			return 100.0F;
		if (this.stopped) {
			return 0.0F;
		}
		return 1f - (this.delay - this.acc) / this.delay;
	}

	public float getDelay() {
		return this.delay;
	}
}
