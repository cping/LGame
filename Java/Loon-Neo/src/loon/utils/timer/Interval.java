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
import loon.LSystem;
import loon.events.ActionUpdate;
import loon.events.EventAction;
import loon.events.EventActionN;
import loon.utils.HelperUtils;

/**
 * 这是一个指定循环事务延迟触发用抽象类,本身并没有实现关键的loop方法,loop部分需要用户根据实际需求实现.<br>
 * 此类作用是提交一个指定间隔的循环到游戏进程中去,每隔指定时间执行一次loop中内容.
 * 
 * ps:事实上这只是一个LTimer的延迟调用部分功能的抽象封装,简化了LTimer的delay相关方法的调用.
 * 不使用此类,而直接使用LTimer也可以得到完全等价的效果.
 */
public abstract class Interval implements ActionUpdate, LRelease {

	protected final LTimer _loop_timer;

	protected Runnable _runnable;

	protected EventAction _event;

	private Interval _waitInterval;

	public Interval() {
		this(0L);
	}

	public Interval(long delay) {
		this(delay, -1);
	}

	public Interval(Duration d) {
		this(LSystem.UNKNOWN, d);
	}

	public Interval(String name, Duration d) {
		this(name, d, null);
	}

	public Interval(String name, long delay) {
		this(delay, -1);
	}

	public Interval(Runnable e) {
		this(0, e);
	}

	public Interval(long delay, Runnable e) {
		this(delay, -1, e);
	}

	public Interval(long delay, int loopCount, Runnable e) {
		this(LSystem.UNKNOWN, delay, loopCount, e);
	}

	public Interval(long delay, int loopCount) {
		this(delay, loopCount, null);
	}

	public Interval(String name, long delay, int loopCount) {
		this(name, delay, loopCount, null);
	}

	public Interval(String name, long delay, int loopCount, Runnable e) {
		this._loop_timer = new LTimer(name, delay, loopCount);
		this._runnable = e;
	}

	public Interval(String name, Duration d, Runnable e) {
		this._loop_timer = new LTimer(name, d);
		this._runnable = e;
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

	public Interval cancel() {
		_loop_timer.cancel();
		return this;
	}

	public Interval pause() {
		_loop_timer.pause();
		return this;
	}

	public Interval resume() {
		return unpause();
	}

	public Interval unpause() {
		_loop_timer.unpause();
		return this;
	}

	public Interval setDelayS(float s) {
		_loop_timer.setDelayS(s);
		return this;
	}

	public float getDelayS() {
		return _loop_timer.getDelayS();
	}

	public Interval setDelay(long d) {
		_loop_timer.setDelay(d);
		return this;
	}

	public long getDelay() {
		return _loop_timer.getDelay();
	}

	public boolean isTimeOut() {
		return _loop_timer.isTimeOut();
	}

	public LTimer onTimeOut(EventActionN e, long delay) {
		return _loop_timer.onTimeOut(e, delay);
	}

	public LTimer onTimeOutS(EventActionN e, float sec) {
		return _loop_timer.onTimeOutS(e, sec);
	}

	public LTimer setTimeOutEvent(EventActionN e) {
		return _loop_timer.setTimeOutEvent(e);
	}

	public LTimer setTimeOut(long delay) {
		return _loop_timer.setTimeOut(delay);
	}

	public LTimer setTimeOutS(float sec) {
		return _loop_timer.setTimeOutS(sec);
	}

	public long getTimeOut() {
		return _loop_timer.getTimeOut();
	}

	public Interval dispose(LRelease r) {
		_loop_timer.dispose(r);
		return this;
	}

	public Interval onComplete(LRelease r) {
		_loop_timer.onComplete(r);
		return this;
	}

	public String getName() {
		return _loop_timer.getName();
	}

	public boolean looping() {
		return _loop_timer.looping();
	}

	public int getRepeatCount() {
		return _loop_timer.getTimesRepeated();
	}

	public boolean isActive() {
		return _loop_timer.isActive();
	}

	public boolean isClosed() {
		return _loop_timer.isClosed();
	}

	public Interval wait(Interval i) {
		this._waitInterval = i;
		return this;
	}

	public Interval freeWait() {
		return wait(null);
	}

	@Override
	public boolean completed() {
		return _loop_timer.isCompleted();
	}

	public boolean call(LTimerContext context) {
		return call(context.timeSinceLastUpdate);
	}

	public boolean call(long elapsedTime) {
		if (_loop_timer.action(elapsedTime)) {
			if (!_loop_timer.isCompleted()) {
				action(this);
			}
			return true;
		}
		return false;
	}

	@Override
	public void action(Object o) {
		if (_waitInterval != null && !_waitInterval.completed()) {
			return;
		}
		if (_runnable != null) {
			_runnable.run();
		}
		if (_event != null) {
			HelperUtils.callEventAction(_event, this);
		}
		loop();
	}

	public Runnable getRunnable() {
		return _runnable;
	}

	public Interval setRunnable(Runnable r) {
		this._runnable = r;
		return this;
	}

	public EventAction getEventAction() {
		return _event;
	}

	public Interval setEventAction(EventAction e) {
		this._event = e;
		return this;
	}

	public abstract void loop();

	@Override
	public void close() {
		_loop_timer.close();
		_waitInterval = null;
	}

}
