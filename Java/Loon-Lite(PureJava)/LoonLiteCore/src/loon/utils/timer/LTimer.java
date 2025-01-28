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
import loon.events.EventAction;
import loon.events.EventActionN;
import loon.events.EventActionT;
import loon.events.TimerEvent;
import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

/**
 * Loon的计时器用类
 * 
 * 此类主要作用有二:
 * 
 * 1, 直接以函数调用形式调用,利用action函数作真假条件判断,每当条件满足时执行一次以action函数检查后生效的if中内容.
 * 
 * 2, 提交一个指定执行次数的事件到游戏进程中去(不指定则默认无限循环),每隔指定时间执行一次.
 * 
 * <pre>
 *  //提交timer到游戏循环中
 *	LTimer time = new LTimer();
 *  //执行六次
 *  time.setRepeats(6);
 *  time.setUpdateable(...);
 *  time.submit();
 *  //关闭
 *  //time.close();
 * </pre>
 */
public class LTimer implements LTimerListener, LRelease {

	private static class TimerProcess extends RealtimeProcess {

		private LTimer timer = null;

		public TimerProcess(LTimer t) {
			this.timer = t;
			this.setProcessType(GameProcessType.Time);
		}

		@Override
		public void run(LTimerContext time) {
			if (timer != null) {
				timer.action(time);
				if (timer.isClosed() || timer.isCompleted()) {
					kill();
				}
			}
		}

	}

	private static LTimer _instance = null;

	public static void freeStatic() {
		_instance = null;
	}

	public static LTimer shared() {
		synchronized (LTimer.class) {
			if (_instance == null) {
				_instance = new LTimer("STATIC_TIME", 0);
			}
			return _instance;
		}
	}

	public static Task postTask(EventActionT<Task> e) {
		return postTask(LSystem.UNKNOWN, e, 0f);
	}

	public static Task postTask(String name, EventActionT<Task> e) {
		return postTask(name, e, 0f);
	}

	public static Task postTask(String name, EventActionT<Task> e, float seconds) {
		return postTask(name, e, seconds, -1);
	}

	public static Task postTask(EventActionT<Task> e, float seconds) {
		return postTask(e, seconds, -1);
	}

	public static Task postTask(EventActionT<Task> e, float seconds, int loopCount) {
		return postTask(LSystem.UNKNOWN, e, seconds, loopCount);
	}

	public static Task postTask(String name, EventActionT<Task> e, float seconds, int loopCount) {
		synchronized (RealtimeProcessManager.class) {
			synchronized (LTimer.class) {
				if (e != null) {
					synchronized (e) {
						final Task task = new Task(name, seconds, loopCount);
						if (e != null) {
							task.setEventAction(e);
							task.start();
						}
						return task;
					}
				} else {
					return new Task(name, seconds, loopCount);
				}
			}
		}
	}

	public static Task postTask(Runnable e) {
		return postTask(LSystem.UNKNOWN, e, 0f);
	}

	public static Task postTask(String name, Runnable e) {
		return postTask(name, e, 0f);
	}

	public static Task postTask(String name, Runnable e, float seconds) {
		return postTask(name, e, seconds, -1);
	}

	public static Task postTask(Runnable e, float seconds) {
		return postTask(e, seconds, -1);
	}

	public static Task postTask(Runnable e, float seconds, int loopCount) {
		return postTask(LSystem.UNKNOWN, e, seconds, loopCount);
	}

	/**
	 * 提交一个任务进程到游戏中去,并返回Task
	 * 
	 * @param name      任务名
	 * @param e         进程事件
	 * @param seconds   间隔描述
	 * @param loopCount 任务循环次数
	 * @return
	 */
	public static Task postTask(String name, Runnable e, float seconds, int loopCount) {
		synchronized (RealtimeProcessManager.class) {
			synchronized (LTimer.class) {
				if (e != null) {
					synchronized (e) {
						final Task task = new Task(name, seconds, loopCount, e);
						task.start();
						return task;
					}
				} else {
					return new Task(name, seconds, loopCount);
				}
			}
		}
	}

	public static Scheduler schedulerTask(float seconds, boolean sequence, Interval... tasks) {
		return schedulerTask(seconds, true, sequence, tasks);
	}

	public static Scheduler schedulerTask(float seconds, Interval... tasks) {
		return schedulerTask(seconds, true, true, tasks);
	}

	public static Scheduler schedulerTask(float seconds, boolean removeTask, boolean sequence, Interval... tasks) {
		return schedulerTask(LSystem.UNKNOWN, seconds, removeTask, sequence, tasks);
	}

	/**
	 * 将所有指定游戏任务集中提交到一个Scheduler中去,并返回Scheduler
	 * 
	 * @param name       调度器名称
	 * @param seconds    间隔秒数
	 * @param removeTask 是否执行后删除
	 * @param sequence   是否顺序执行
	 * @param tasks      注入的任务集合
	 * @return
	 */
	public static Scheduler schedulerTask(String name, float seconds, boolean removeTask, boolean sequence,
			Interval... tasks) {
		synchronized (RealtimeProcessManager.class) {
			synchronized (LTimer.class) {
				final Scheduler scheduler = new Scheduler(name, Duration.ofS(seconds), removeTask, sequence);
				if (tasks != null && tasks.length > 0) {
					synchronized (tasks) {
						scheduler.addAll(tasks);
					}
					scheduler.start();
				}
				return scheduler;
			}
		}
	}

	public static LTimer ZERO() {
		return at(0);
	}

	public static LTimer ONE() {
		return at(LSystem.SECOND);
	}

	public static LTimer HALF() {
		return at(LSystem.SECOND / 2L);
	}

	public static LTimer get() {
		return shared();
	}

	public static LTimer at() {
		return new LTimer();
	}

	public static LTimer at(long d) {
		return new LTimer(d);
	}

	public static LTimer at(Duration d) {
		return new LTimer(d);
	}

	private static int GLOBAL_ID = 0;

	private TimerProcess _process = null;

	private final int _idx;
	private int _maxNumberOfRepeats = -1;
	private int _numberOfTicks = 0;

	private boolean _repeats = true;
	private boolean _completed = false;
	private boolean _closed = false;

	private float _speedFactor = 1f;

	private long _delay = 0;
	private long _currentTick = 0;
	private boolean _active = true;

	private TArray<LTimerListener> _currentListeners = null;

	private EventAction _eventAction = null;

	private final String _name;

	public LTimer() {
		this(160);
	}

	public LTimer(String name) {
		this(name, 160);
	}

	public LTimer(String name, Duration d) {
		this(name, d == null ? 0 : d.toMillisLong(), 1f);
	}

	public LTimer(Duration d) {
		this(d == null ? 0 : d.toMillisLong());
	}

	public LTimer(String name, long delay) {
		this(name, delay, 1f);
	}

	public LTimer(long delay) {
		this(delay, 1f);
	}

	public LTimer(String name, long delay, float factor) {
		this(name, delay, -1, factor, true);
	}

	public LTimer(long delay, float factor) {
		this(delay, -1, factor, true);
	}

	public LTimer(long delay, int numberOfRepeats) {
		this(LSystem.UNKNOWN, delay, numberOfRepeats);
	}

	public LTimer(String name, long delay, int numberOfRepeats) {
		this(name, delay, numberOfRepeats, 1f, true);
	}

	public LTimer(String name, long delay, float factor, boolean repeats) {
		this(name, delay, -1, factor, repeats);
	}

	public LTimer(long delay, int numberOfRepeats, float factor, boolean repeats) {
		this(LSystem.UNKNOWN, delay, numberOfRepeats, factor, repeats);
	}

	public LTimer(String name, long delay, int numberOfRepeats, float factor, boolean repeats) {
		this._idx = GLOBAL_ID++;
		this._name = name;
		this._closed = false;
		this.reset(delay, numberOfRepeats, factor, repeats);
	}

	public boolean action(LTimerContext context) {
		return action(context.timeSinceLastUpdate);
	}

	public boolean action(float delta) {
		return action((MathUtils.max(Duration.ofS(delta), 8)));
	}

	@Override
	public boolean action(long elapsedTime) {
		if (this._closed) {
			return false;
		}
		if (this._active) {
			this._currentTick += (elapsedTime * _speedFactor);
			if (this._maxNumberOfRepeats > -1 && this._numberOfTicks >= this._maxNumberOfRepeats) {
				this._completed = true;
			}
			if (!this._completed && this._currentTick >= this._delay) {
				if (this._currentListeners != null) {
					final int size = _currentListeners.size;
					for (int i = 0; i < size; i++) {
						final LTimerListener listener = _currentListeners.get(i);
						if (listener != null) {
							listener.action(elapsedTime);
						}
					}
				}
				if (this._eventAction != null) {
					HelperUtils.callEventAction(_eventAction, this, elapsedTime);
				}
				if (this._repeats) {
					this._numberOfTicks++;
					this._currentTick = 0;
				} else {
					this._completed = true;
				}
				return true;
			}
		}
		return false;
	}

	public LTimer refresh() {
		return reset();
	}

	public LTimer reset() {
		return this.reset(this._delay, this._maxNumberOfRepeats, this._speedFactor, this._repeats);
	}

	public LTimer reset(long newDelay, int newNumberOfRepeats, float newFactor, boolean newRepeats) {
		this._delay = MathUtils.max(newDelay, 0);
		this._maxNumberOfRepeats = MathUtils.max(newNumberOfRepeats, -1);
		this._speedFactor = MathUtils.max(newFactor, LSystem.MIN_SECONE_SPEED_FIXED);
		this._repeats = newRepeats;
		this._active = true;
		this._completed = false;
		this._currentTick = 0;
		this._numberOfTicks = 0;
		this._speedFactor = 1f;
		return this;
	}

	public LTimer restart() {
		this.reset();
		this.resume();
		return this;
	}

	public LTimer setEquals(LTimer other) {
		this._delay = MathUtils.max(other._delay, 0);
		this._maxNumberOfRepeats = MathUtils.max(other._maxNumberOfRepeats, -1);
		this._speedFactor = MathUtils.max(other._speedFactor, LSystem.MIN_SECONE_SPEED_FIXED);
		this._repeats = other._repeats;
		this._active = other._active;
		this._completed = other._completed;
		this._currentTick = other._currentTick;
		this._numberOfTicks = other._numberOfTicks;
		this._speedFactor = other._speedFactor;
		return this;
	}

	public LTimer addPercentage(long elapsedTime) {
		this._currentTick += elapsedTime;
		return this;
	}

	public LTimer addPercentage(LTimerContext context) {
		this._currentTick += context.timeSinceLastUpdate;
		return this;
	}

	public boolean looping() {
		if (_completed || _maxNumberOfRepeats == -1) {
			return false;
		}
		return this._numberOfTicks <= this._maxNumberOfRepeats;
	}

	public int getTimesRepeated() {
		return this._numberOfTicks;
	}

	public long getDelay() {
		return this._delay;
	}

	public float getDelayS() {
		return Duration.toS(this._delay);
	}

	public LTimer setDelay(Duration d) {
		return setDelay(d == null ? 0 : d.toMillisLong());
	}

	public LTimer setDelayS(float s) {
		return setDelay(Duration.ofS(s));
	}

	public LTimer setDelay(long delay) {
		return reset(delay, this._maxNumberOfRepeats, this._speedFactor, this._repeats);
	}

	public LTimer setRepeats(int amount) {
		return setRepeats(amount, true);
	}

	public LTimer setRepeats(int amount, boolean newRepats) {
		return this.reset(this._delay, amount, this._speedFactor, newRepats);
	}

	public LTimer setActive(boolean active) {
		this.reset();
		this._active = active;
		return this;
	}

	public boolean isActive() {
		return this._active;
	}

	public LTimer start() {
		this._active = true;
		this.setCompleted(false);
		return this;
	}

	public LTimer stop() {
		this._active = false;
		this.setCompleted(true);
		return this;
	}

	public LTimer pause() {
		this._active = false;
		return this;
	}

	public LTimer resume() {
		return unpause();
	}

	public LTimer unpause() {
		this._active = true;
		return this;
	}

	public boolean toggle() {
		if (this._active) {
			this.pause();
		} else {
			this.resume();
		}
		return this._active;
	}

	public boolean paused() {
		return !isActive();
	}

	public int getId() {
		return _idx;
	}

	public long getCurrentTick() {
		return this._currentTick;
	}

	public LTimer setCurrentTick(long tick) {
		this._currentTick = tick;
		return this;
	}

	public float getTimeToNextS() {
		return Duration.toS(getTimeToNext());
	}

	public long getTimeToNext() {
		if (this._completed) {
			return 0l;
		}
		return this._delay - this._currentTick;
	}

	public float getTickTowardNextS() {
		return Duration.toS(getTickTowardNext());
	}

	public long getTickTowardNext() {
		if (this._completed) {
			return 0l;
		}
		return this._currentTick;
	}

	public float getPercentage() {
		return (this._delay > 0) ? ((float) this._currentTick / (float) this._delay) : 0f;
	}

	public float getOverallPercentage() {
		if (this._numberOfTicks > 0) {
			float totalDuration = this._delay + (this._delay * this._numberOfTicks);
			float totalElapsed = this._currentTick + (this._delay * (this._numberOfTicks - this._maxNumberOfRepeats));
			return (totalElapsed / totalDuration);
		} else {
			return this.getPercentage();
		}
	}

	public float getElapsedSeconds() {
		return Duration.toS(this._currentTick);
	}

	public float getRemaining() {
		return (float) (this._delay - this._currentTick);
	}

	public boolean checkInterval(long interval) {
		return (_currentTick / interval) > ((_currentTick - _delay) / interval);
	}

	public LTimer clamp() {
		if (this._currentTick > this._delay) {
			_currentTick = _delay;
		}
		return this;
	}

	public float getSpeedFactor() {
		return _speedFactor;
	}

	public LTimer setSpeedFactor(float factor) {
		this._speedFactor = factor;
		return this;
	}

	public boolean isCompleted() {
		return _completed;
	}

	public LTimer setCompleted(boolean completed) {
		this._completed = completed;
		return this;
	}

	public EventAction getUpdateable() {
		return _eventAction;
	}

	public LTimer setUpdateable(EventAction u) {
		this._eventAction = u;
		return this;
	}

	public TimerEvent makeTimeEvent() {
		return new TimerEvent(this._currentTick);
	}

	public LTimer submit() {
		synchronized (RealtimeProcessManager.class) {
			if (_process != null) {
				RealtimeProcessManager.get().delete(_process);
			}
			if (_process == null || _process.isDead()) {
				_process = new TimerProcess(this);
			}
			_process.setDelay(0);
			RealtimeProcessManager.get().addProcess(_process);
		}
		return this;
	}

	public LTimer cancel() {
		this.pause();
		synchronized (RealtimeProcessManager.class) {
			if (_process != null) {
				_process.cancel();
			}
		}
		return this;
	}

	public LTimer kill() {
		if (_process != null) {
			_process.kill();
		}
		return this;
	}

	public boolean isTimeOut() {
		if (_process != null) {
			return _process.isTimeOut();
		}
		return false;
	}

	public LTimer onTimeOut(EventActionN e, long delay) {
		if (_process != null) {
			_process.onTimeOut(e, delay);
		}
		return this;
	}

	public LTimer onTimeOutS(EventActionN e, float sec) {
		if (_process != null) {
			_process.onTimeOutS(e, sec);
		}
		return this;
	}

	public LTimer setTimeOutEvent(EventActionN e) {
		if (_process != null) {
			_process.setTimeOutEvent(e);
		}
		return this;
	}

	public LTimer setTimeOut(long delay) {
		if (_process != null) {
			_process.setTimeOut(delay);
		}
		return this;
	}

	public LTimer setTimeOutS(float sec) {
		if (_process != null) {
			_process.setTimeOutS(sec);
		}
		return this;
	}

	public long getTimeOut() {
		if (_process != null) {
			return _process.getTimeOut();
		}
		return 0l;
	}

	public LTimer onComplete(LRelease r) {
		if (_process != null) {
			_process.onComplete(r);
		}
		return this;
	}

	public LTimer dispose(LRelease r) {
		if (_process != null) {
			_process.dispose(r);
		}
		return this;
	}

	public boolean addListener(LTimerListener timerListener) {
		synchronized (LTimer.class) {
			if (_currentListeners == null) {
				_currentListeners = new TArray<LTimerListener>();
			}
			if (_currentListeners != null) {
				if (timerListener == null) {
					return false;
				}
				if (timerListener == this) {
					return false;
				}
				return _currentListeners.add(timerListener);
			}
			return false;
		}
	}

	public boolean removeListener(LTimerListener timerListener) {
		if (_currentListeners != null) {
			synchronized (LTimer.class) {
				return _currentListeners.remove(timerListener);
			}
		}
		return false;
	}

	public LTimerListener getFirstListener() {
		if (_currentListeners != null) {
			synchronized (LTimer.class) {
				return _currentListeners.first();
			}
		}
		return null;
	}

	public LTimerListener getLastListener() {
		if (_currentListeners != null) {
			synchronized (LTimer.class) {
				return _currentListeners.last();
			}
		}
		return null;
	}

	public LTimer clearListeners() {
		if (_currentListeners != null) {
			_currentListeners.clear();
			_currentListeners = null;
		}
		return this;
	}

	public String getName() {
		return _name;
	}

	public boolean isClosed() {
		return this._closed;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("LTimer");
		builder.kv("name", _name).comma().kv("currentTick", _currentTick).comma().kv("delay", _delay).comma()
				.kv("factor", _speedFactor).comma().kv("active", _active).comma().kv("repeats", _repeats).comma()
				.kv("maxNumberOfRepeats", _maxNumberOfRepeats).comma().kv("numberOfTicks", _numberOfTicks).comma()
				.kv("timerListeners", _currentListeners == null ? 0 : _currentListeners.size).comma()
				.kv("completed", _completed).comma().kv("process", _process);
		return builder.toString();
	}

	@Override
	public void close() {
		this.stop();
		this.clearListeners();
		this._eventAction = null;
		this._closed = true;
		if (_process != null) {
			_process.close();
			_process = null;
		}
	}

}
