/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.utils.timer;

import loon.utils.MathUtils;
import loon.utils.StringKeyValue;

public class GameTime {

	private static GameTime _instance = null;

	public static void freeStatic() {
		_instance = null;
	}

	public static GameTime get() {
		return getInstance();
	}

	public static GameTime getInstance() {
		if (_instance == null) {
			synchronized (GameTime.class) {
				if (_instance == null) {
					_instance = new GameTime();
				}
			}
		}
		return _instance;
	}

	float _elapsedTime;
	float _totalTime;

	boolean _running;

	public static GameTime at() {
		return new GameTime();
	}

	public GameTime() {
		this(0f, 0f);
	}

	public GameTime(float totalGameTime, float elapsedGameTime) {
		this(totalGameTime, elapsedGameTime, false);
	}

	public GameTime(float totalRealTime, float elapsedRealTime, boolean isRunningSlowly) {
		this._totalTime = totalRealTime;
		this._elapsedTime = elapsedRealTime;
		_running = isRunningSlowly;
	}

	public void update(float elapsed) {
		this._elapsedTime = elapsed;
		this._totalTime += elapsed;
	}

	public void update(LTimerContext context) {
		update(context.getMilliseconds());
	}

	public GameTime resetElapsedTime() {
		_elapsedTime = 0f;
		return this;
	}

	public boolean isRunningSlowly() {
		return _running;
	}

	public float getMilliseconds() {
		return MathUtils.max(_elapsedTime * 1000, 10);
	}

	public float getElapsedGameTime() {
		return _elapsedTime;
	}

	public float getTotalGameTime() {
		return _totalTime;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("GameTime");
		builder.kv("elapsedTime", _elapsedTime).comma().kv("totalTime", _totalTime).comma().kv("running", _running);
		return builder.toString();
	}
}
