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
package loon.events;

import loon.LRelease;
import loon.Screen;
import loon.utils.HelperUtils;
import loon.utils.timer.LTimer;

/**
 * 一个交给Screen使用的内部循环用类
 */
public abstract class FrameLoopEvent implements LRelease {

	private EventAction _eventAction;

	private boolean _killSelf = false;

	private LTimer _speedTimer = new LTimer(0);

	public abstract void invoke(long elapsedTime, Screen e);

	public abstract void completed();

	public final void call(long elapsedTime, Screen e) {
		if (_speedTimer.action(elapsedTime)) {
			invoke(elapsedTime, e);
			if (_eventAction != null) {
				HelperUtils.callEventAction(_eventAction, elapsedTime);
			}
		}
	}

	public FrameLoopEvent reset() {
		this._killSelf = false;
		this._speedTimer.reset();
		return this;
	}

	public FrameLoopEvent setDelay(long d) {
		_speedTimer.setDelay(d);
		return this;
	}

	public FrameLoopEvent setSecond(float second) {
		_speedTimer.setDelayS(second);
		return this;
	}

	public float getSecond() {
		return _speedTimer.getDelayS();
	}

	public LTimer getTimer() {
		return _speedTimer;
	}

	public FrameLoopEvent kill() {
		_killSelf = true;
		_speedTimer.kill();
		return this;
	}

	public boolean isDead() {
		return _killSelf;
	}

	public EventAction getEventAction() {
		return _eventAction;
	}

	public FrameLoopEvent setEventAction(EventAction e) {
		this._eventAction = e;
		return this;
	}

	@Override
	public void close() {
		_speedTimer.close();
	}

}
