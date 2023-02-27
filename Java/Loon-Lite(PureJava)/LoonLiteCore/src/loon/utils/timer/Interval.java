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
package loon.utils.timer;

import loon.LRelease;
import loon.events.ActionUpdate;

/**
 * 这是一个指定循环事务延迟触发用抽象类,本身并没有实现关键的loop方法,loop部分需要用户根据实际需求实现.<br>
 * 此类作用是提交一个指定间隔的循环到游戏进程中去,每隔指定时间执行一次loop中内容.
 * 
 * ps:事实上这只是一个LTimer的延迟调用部分功能的抽象封装,简化了LTimer的delay相关方法的调用.
 * 不使用此类,而直接使用LTimer也可以得到完全等价的效果.
 */
public abstract class Interval implements ActionUpdate, LRelease {

	protected final LTimer _loop_timer;

	public Interval() {
		this._loop_timer = new LTimer(0L);
	}

	public Interval(long delay) {
		this._loop_timer = new LTimer(delay);
	}

	public Interval(Duration d) {
		this._loop_timer = new LTimer(d);
	}

	public Interval(String name, long delay) {
		this._loop_timer = new LTimer(name, delay);
	}

	public Interval(long delay, int loopCount) {
		this._loop_timer = new LTimer(delay, loopCount);
	}

	public Interval(String name, long delay, int loopCount) {
		this._loop_timer = new LTimer(name, delay, loopCount);
	}

	public Interval(String name, Duration d) {
		this._loop_timer = new LTimer(name, d);
	}

	public Interval start() {
		_loop_timer.start();
		_loop_timer.setUpdateable(this);
		_loop_timer.submit();
		return this;
	}

	public Interval stop() {
		_loop_timer.stop();
		_loop_timer.kill();
		return this;
	}

	public Interval pause() {
		_loop_timer.pause();
		return this;
	}

	public Interval unpause() {
		_loop_timer.unpause();
		return this;
	}

	public Interval setDelay(long d) {
		_loop_timer.setDelay(d);
		return this;
	}

	public long getDelay() {
		return _loop_timer.getDelay();
	}

	public String getName() {
		return _loop_timer.getName();
	}

	public boolean isActive() {
		return _loop_timer.isActive();
	}

	public boolean isClosed() {
		return _loop_timer.isClosed();
	}

	@Override
	public boolean completed() {
		return _loop_timer.isCompleted();
	}

	public LTimer currentTimer() {
		return _loop_timer;
	}

	@Override
	public void action(Object o) {
		loop();
	}

	public abstract void loop();

	@Override
	public void close() {
		_loop_timer.close();
	}

}
