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

import loon.LSystem;
import loon.events.EventAction;
import loon.utils.HelperUtils;
import loon.utils.StringKeyValue;
import loon.utils.TimeUtils;

/**
 * 计时器(也就是俗称的秒表，需要正常计算时间的游戏都会用到)
 */
public class StopwatchTimer {

	private String _currentName;

	private long _from;

	private long _to;

	private long _lastStop;

	private long _target;

	private long _timeOn = -1;

	private long _timeOff = -1;

	public StopwatchTimer() {
		this(LSystem.EMPTY);
	}

	public StopwatchTimer(String name) {
		this(name, 0);
	}

	public StopwatchTimer(long target) {
		this(LSystem.EMPTY, target);
	}

	public StopwatchTimer(String name, long target) {
		this._currentName = name;
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

	public static StopwatchTimer run(EventAction a) {
		StopwatchTimer sw = begin();
		HelperUtils.callEventAction(a, sw);
		sw.stop();
		return sw;
	}

	public boolean isWaiting() {
		return !isDone();
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
		this._from = (_timeOn == -1) ? currentTime() : _timeOn;
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
		this._to = (_timeOff == -1) ? currentTime() : _timeOff;
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

	public StopwatchTimer setName(String n) {
		this._currentName = n;
		return this;
	}

	public String getName() {
		return this._currentName;
	}

	public long getTimeOn() {
		return _timeOn;
	}

	public long getTimeOff() {
		return _timeOff;
	}

	public StopwatchTimer setTimeOn(long timeOn) {
		_timeOn = timeOn;
		return this;
	}

	public StopwatchTimer setTimeOff(long timeOff) {
		_timeOff = timeOff;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("StopwatchTimer");
		builder.kv("name", _currentName).comma().kv("from", _from).comma().kv("to", _to).comma()
				.kv("lastStop", _lastStop).comma().kv("target", _target);
		return builder.toString();
	}
}
