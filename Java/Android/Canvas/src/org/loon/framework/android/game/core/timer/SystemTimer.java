package org.loon.framework.android.game.core.timer;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.3
 */
public class SystemTimer {

	private long lastTime = 0;

	private long virtualTime = 0;

	public SystemTimer() {
		start();
	}

	public void start() {
		lastTime = System.currentTimeMillis();
		virtualTime = 0;
	}

	public long sleepTimeMicros(long goalTimeMicros) {
		long time = goalTimeMicros - getTimeMicros();
		if (time > 100) {
			try {
				Thread.sleep((int) ((time + 100) / 1000));
			} catch (InterruptedException ex) {
			}
		}
		return getTimeMicros();
	}

	public static long sleepTimeMicros(long goalTimeMicros, SystemTimer timer) {
		long time = goalTimeMicros - timer.getTimeMicros();
		if (time > 100) {
			try {
				Thread.sleep((int) ((time + 100) / 1000));
			} catch (InterruptedException ex) {
			}
		}
		return timer.getTimeMicros();
	}

	public long getTimeMillis() {
		long time = System.currentTimeMillis();
		if (time > lastTime) {
			virtualTime += time - lastTime;
		}
		lastTime = time;

		return virtualTime;
	}

	public long getTimeMicros() {
		return getTimeMillis() * 1000;
	}
}
