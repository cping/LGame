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
package loon.core.timer;

public class GameTime {

	float _elapsedTime;
	float _totalTime;

	boolean _running;

	public GameTime() {
		_elapsedTime = _totalTime = 0f;
	}

	public GameTime(float totalGameTime, float elapsedGameTime) {
		_totalTime = totalGameTime;
		_elapsedTime = elapsedGameTime;
	}

	public GameTime(float totalRealTime, float elapsedRealTime,
			boolean isRunningSlowly) {
		_totalTime = totalRealTime;
		_elapsedTime = elapsedRealTime;
		_running = isRunningSlowly;
	}

	public void update(float elapsed) {
		_elapsedTime = elapsed;
		_totalTime += elapsed;
	}

	public void update(LTimerContext context) {
		update(context.getMilliseconds());
	}

	public void resetElapsedTime() {
		_elapsedTime = 0f;
	}

	public boolean isRunningSlowly() {
		return _running;
	}

	public float getMilliseconds() {
		return _elapsedTime * 1000;
	}

	public float getElapsedGameTime() {
		return _elapsedTime;
	}

	public float getTotalGameTime() {
		return _totalTime;
	}
	
}
