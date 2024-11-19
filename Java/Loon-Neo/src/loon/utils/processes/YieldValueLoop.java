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

import loon.FloatCounter;

public class YieldValueLoop implements YieldLoop {

	private final FloatCounter _counter;

	private YieldLoop _loop;

	public YieldValueLoop(YieldLoop l) {
		this._counter = new FloatCounter();
		this._loop = l;
	}

	public float get() {
		return this._counter.get();
	}

	public YieldLoop clear() {
		return set(null);
	}

	public YieldValueLoop set(YieldLoop loop) {
		this._counter.clear();
		this._loop = loop;
		return this;
	}

	public YieldValueLoop increment() {
		this._counter.increment();
		return this;
	}

	public YieldValueLoop reduction() {
		this._counter.reduction();
		return this;
	}

	public FloatCounter getCounter() {
		return this._counter;
	}

	@Override
	public void loop() {
		if (_loop != null) {
			_loop.loop();
		}
		this._counter.increment();
	}

}
