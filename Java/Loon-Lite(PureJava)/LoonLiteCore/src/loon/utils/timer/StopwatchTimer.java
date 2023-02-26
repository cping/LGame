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

import loon.events.Updateable;
import loon.utils.StringKeyValue;
import loon.utils.TimeUtils;

/**
 * 计时器(也就是俗称的秒表，需要正常计算时间的游戏都会用到)
 */
public class StopwatchTimer {

	private long from;

	private long to;

	private long lastStop;

	private long target;

	public StopwatchTimer() {
		this(0);
	}

	public StopwatchTimer(long target) {
		this.target = target;
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
		return (currentTime() - from) >= target;
	}

	public StopwatchTimer reset() {
		start();
		return this;
	}

	public long start() {
		from = currentTime();
		to = from;
		lastStop = to;
		return from;
	}

	private long currentTime() {
		return TimeUtils.millis();
	}

	public StopwatchTimer end() {
		return stop();
	}
	
	public StopwatchTimer stop() {
		lastStop = to;
		to = currentTime();
		return this;
	}

	public long getDuration() {
		return to - from;
	}

	public long getLastDuration() {
		return to - lastStop;
	}

	public long getStartTime() {
		return from;
	}

	public long getEndTime() {
		return to;
	}
	
	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("StopwatchTimer");
		builder.kv("from", from)
		.comma()
		.kv("to", to)
		.comma()
		.kv("lastStop", lastStop)
		.comma()
		.kv("target", target);
		return builder.toString();
	}
}
