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
package loon.utils.processes;

import loon.LRelease;
import loon.LSystem;
import loon.utils.SortedList;
import loon.utils.timer.Duration;
import loon.utils.timer.LTimerContext;

public class CoroutineProcess extends RealtimeProcess implements LRelease {

	private final SortedList<Coroutine> _cycles = new SortedList<Coroutine>();

	private boolean _running;

	public CoroutineProcess() {
		this(0);
	}

	public CoroutineProcess(long delay) {
		super(delay);
		this._running = true;
	}

	public CoroutineProcess reset() {
		this.clearCoroutine();
		this._running = true;
		return this;
	}

	public Coroutine call(YieldExecute... es) {
		return startCoroutine(new Yielderable(es));
	}

	public Coroutine startCoroutine(Yielderable y) {
		final Coroutine coroutine = new Coroutine();
		coroutine.setup(y);
		coroutine.update(Duration.ofS(LSystem.MIN_SECONE_SPEED_FIXED));
		this._cycles.add(coroutine);
		return coroutine;
	}

	public void clearCoroutine() {
		for (Coroutine c : this._cycles) {
			c.cancel();
		}
		this._cycles.clear();
	}

	@Override
	public void run(LTimerContext time) {
		if (_running) {
			updateCoroutine(time.timeSinceLastUpdate);
		}
	}

	public void updateCoroutine(long e) {
		if (_cycles.size > 0) {
			Coroutine c = this._cycles.element();
			c.update(e);
			if (c._status == CoroutineStatus.Cancel || c._status == CoroutineStatus.Completed) {
				_cycles.remove(c);
			}
		}
	}

	public boolean isCoroutineRunning() {
		return _running;
	}

	@Override
	public void close() {
		super.close();
		clearCoroutine();
		_running = false;
	}

}
