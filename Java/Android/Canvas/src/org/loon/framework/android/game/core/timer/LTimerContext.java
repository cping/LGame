package org.loon.framework.android.game.core.timer;

/**
 * 
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
 * @email ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class LTimerContext {

	public long timeSinceLastUpdate, millisSleepTime;

	public LTimerContext() {
		timeSinceLastUpdate = 0;
	}

	public synchronized void setTimeSinceLastUpdate(long timeSinceLastUpdate) {
		this.timeSinceLastUpdate = timeSinceLastUpdate;
	}

	public synchronized long getTimeSinceLastUpdate() {
		return timeSinceLastUpdate;
	}

	public long getSleepTimeMillis() {
		return millisSleepTime;
	}

	public void setSleepTimeMillis(long millisSleepTime) {
		this.millisSleepTime = millisSleepTime;
	}

}
