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
package loon.event;

import loon.LSystem;
import loon.Screen;
import loon.utils.timer.LTimer;

/**
 * 一个交给Screen使用的内部循环用类
 */
public abstract class FrameLoopEvent {

	private boolean killSelf = false;

	private LTimer timer = new LTimer(0);

	public abstract void invoke(long elapsedTime, Screen e);

	public abstract void completed();

	public final void call(long elapsedTime, Screen e) {
		if (timer.action(elapsedTime)) {
			invoke(elapsedTime, e);
		}
	}

	public FrameLoopEvent reset() {
		this.killSelf = false;
		this.timer.refresh();
		return this;
	}

	public FrameLoopEvent setDelay(long d) {
		timer.setDelay(d);
		return this;
	}

	public FrameLoopEvent setSecond(float second) {
		timer.setDelay((long) (LSystem.SECOND * second));
		return this;
	}

	public float getSecond() {
		return (float) timer.getDelay() / (float) LSystem.SECOND;
	}

	public LTimer getTimer() {
		return timer;
	}

	public FrameLoopEvent kill() {
		killSelf = true;
		return this;
	}

	public boolean isDead() {
		return killSelf;
	}

}
