/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed _to in writing, software
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

import loon.event.Updateable;
import loon.utils.StringKeyValue;
import loon.utils.TimeUtils;

/**
 * 计时器(也就是俗称的秒表，需要正常计算时间的游戏都会用到)
 */
public class StopwatchTimer {

	private long _from;

	private long _to;

	private long _lastStop;

	private long _target;

	public StopwatchTimer() {
		this(0);
	}

	public StopwatchTimer(long target) {
		this._target = target;
		this.reset();
	}

	public static StopwatchTimer begin() {
		StopwatchTimer sw = new StopwatchTimer();
		sw.start();
		return sw;
	}

	public static StopwatchTimer make() {
		return new StopwatchTimer();
	}

	public static StopwatchTimer run(Updateable u) {
		StopwatchTimer sw = begin();
		u.action(null);
		sw.stop();
		return sw;
	}

	public boolean isDoneAndReset() {
		if (isDone()) {
			reset();
			return true;
		}
		return false;
	}

	public boolean isDone() {
		return (currentTime() - _from) >= _target;
	}

	public boolean isPassedTime(long interval) {
		return currentTime() - _from >= interval;
	}

	public StopwatchTimer reset() {
		start();
		return this;
	}

	public long start() {
		this._from = currentTime();
		this._to = this._from;
		this._lastStop = this._to;
		return this._from;
	}

	private long currentTime() {
		return TimeUtils.millis();
	}

	public StopwatchTimer end() {
		return stop();
	}

	public StopwatchTimer stop() {
		this._lastStop = this._to;
		this._to = currentTime();
		return this;
	}

	public long getDuration() {
		return this._to - this._from;
	}

	public long getLastDuration() {
		return this._to - this._lastStop;
	}

	public long getStartTime() {
		return this._from;
	}

	public long getEndTime() {
		return this._to;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("StopwatchTimer");
		builder.kv("from", _from).comma().kv("to", _to).comma().kv("lastStop", _lastStop).comma().kv("target", _target);
		return builder.toString();
	}
}
