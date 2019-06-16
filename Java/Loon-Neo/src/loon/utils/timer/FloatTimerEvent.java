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

	private float _delay;
	private float _acc;
	
	private boolean _repeat;
	private boolean _done;
	private boolean _stopped;

	public FloatTimerEvent(float delay) {
		this(delay, false);
	}

	public FloatTimerEvent(float delay, boolean repeat) {
		this._delay = delay;
		this._repeat = repeat;
		this._acc = 0f;
	}

	public void update(LTimerContext context) {
		update(context.timeSinceLastUpdate);
	}

	public void update(long elapsedTime) {
		this.update(MathUtils.max(elapsedTime / 1000f, 0.01f));
	}

	public void update(float delta) {
		if ((!this._done) && (!this._stopped)) {
			this._acc += delta;

			if (this._acc >= this._delay) {
				this._acc -= this._delay;

				if (this._repeat)
					reset();
				else {
					this._done = true;
				}
				execute();
			}
		}
	}

	public FloatTimerEvent reset() {
		this._stopped = false;
		this._done = false;
		this._acc = 0f;
		return this;
	}

	public boolean isCompleted() {
		return this._done;
	}

	public boolean isRunning() {
		return (!this._done) && (this._acc < this._delay) && (!this._stopped);
	}

	public FloatTimerEvent stop() {
		this._stopped = true;
		return this;
	}

	public FloatTimerEvent setDelay(int delay) {
		this._delay = delay;
		return this;
	}

	public abstract void execute();

	public float getPercentage() {
		return this._acc / this._delay;
	}

	public float getRemaining() {
		return this._delay - this._acc;
	}

	public float getPercentageRemaining() {
		if (this._done)
			return 100f;
		if (this._stopped) {
			return 0f;
		}
		return 1f - (this._delay - this._acc) / this._delay;
	}

	public float getDelay() {
		return this._delay;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("FloatTimerEvent");
		builder.kv("delay", _delay).comma().kv("repeat", _repeat).comma().kv("acc", _acc).comma().kv("done", _done).comma()
				.kv("stopped", _stopped);
		return builder.toString();
	}
}
