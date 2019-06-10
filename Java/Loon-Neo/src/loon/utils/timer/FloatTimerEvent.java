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
import loon.utils.StringKeyValue;

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
		this.acc = 0f;
	}

	public void update(LTimerContext context) {
		update(context.timeSinceLastUpdate);
	}

	public void update(long elapsedTime) {
		this.update(MathUtils.max(elapsedTime / 1000f, 0.01f));
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

	public FloatTimerEvent reset() {
		this.stopped = false;
		this.done = false;
		this.acc = 0f;
		return this;
	}

	public boolean isCompleted() {
		return this.done;
	}

	public boolean isRunning() {
		return (!this.done) && (this.acc < this.delay) && (!this.stopped);
	}

	public FloatTimerEvent stop() {
		this.stopped = true;
		return this;
	}

	public FloatTimerEvent setDelay(int delay) {
		this.delay = delay;
		return this;
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
			return 100f;
		if (this.stopped) {
			return 0f;
		}
		return 1f - (this.delay - this.acc) / this.delay;
	}

	public float getDelay() {
		return this.delay;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("FloatTimerEvent");
		builder.kv("delay", delay).comma().kv("repeat", repeat).comma().kv("acc", acc).comma().kv("done", done).comma()
				.kv("stopped", stopped);
		return builder.toString();
	}
}
