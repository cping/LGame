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
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;

/**
 * 一个简单的Interval游戏事务延迟管理器,可以在其中存储多个Interval并统一提交到游戏循环中,进行统一管理.
 * 
 * 例如:
 * 
 * <pre>
 * 
 * // 构建一个延迟事务管理器(默认循环执行其中事务)
 * final Scheduler s = new Scheduler();
 * // 若removeTask项为true,则会删除已经执行过的事务,则只所有调度仅进行一次,不会循环执行
 * // final Scheduler s = new Scheduler(true);
 * // 添加事务1
 * s.add(new Interval() {
 * 
 * 	&#64;Override
 * 	public void loop() {
 * 		System.out.println("a");
 * 		// 跳到索引2(即第三个添加的事务)
 * 		s.setIndex(2);
 * 	}
 * });
 * // 添加事务2
 * s.add(new Interval() {
 * 
 * 	&#64;Override
 * 	public void loop() {
 * 		System.out.println("b");
 * 	}
 * });
 * // 添加事务3
 * s.add(new Interval() {
 * 
 * 	&#64;Override
 * 	public void loop() {
 * 		System.out.println("c");
 * 	}
 * });
 * // 延迟1秒
 * s.setDelay(LSystem.SECOND);
 * s.start();
 * </pre>
 * 
 *
 */
public class Scheduler implements LRelease {

	private static class SchedulerProcess extends RealtimeProcess {

		private Scheduler sched = null;

		public SchedulerProcess(Scheduler s) {
			this.sched = s;
			this.setProcessType(GameProcessType.Time);
		}

		@Override
		public void run(LTimerContext time) {
			if (sched != null) {
				sched.update(time);
				if (sched.completed()) {
					kill();
				}
			}
		}

	}

	private final LTimer _loop_timer;

	private SchedulerProcess _processScheduler;

	private TArray<Interval> _scheduled = new TArray<Interval>(32);

	private int _childIndex = 0;

	private boolean _removeSequenceTask = false;

	private boolean _forceWaitSequence = false;

	private boolean _closed = false;

	public Scheduler() {
		this(0L);
	}

	public Scheduler(long delay) {
		this(LSystem.UNKOWN, delay);
	}

	public Scheduler(boolean removeTask) {
		this(LSystem.UNKOWN, removeTask);
	}

	public Scheduler(String name, boolean removeTask) {
		this(name, 0, removeTask, true);
	}

	public Scheduler(String name, long delay) {
		this(name, delay, false);
	}

	public Scheduler(boolean removeTask, boolean sequence) {
		this(LSystem.UNKOWN, 0L, removeTask, sequence);
	}

	public Scheduler(String name, boolean removeTask, boolean sequence) {
		this(name, 0L, removeTask, sequence);
	}

	public Scheduler(String name, long delay, boolean removeTask) {
		this(name, delay, removeTask, true);
	}

	/**
	 * Scheduler事务管理器
	 * 
	 * @param name
	 *            事务调度管理器名称
	 * @param delay
	 *            延迟时间(默认0)
	 * @param removeTask
	 *            是否删除已运行的任务
	 * @param sequence
	 *            是否循环播放管理器中事务(此项为true,当前事务不完成不会进行下一个,若想同步进行可改为false)
	 */
	public Scheduler(String name, long delay, boolean removeTask, boolean sequence) {
		this._loop_timer = new LTimer(name, delay);
		this._removeSequenceTask = removeTask;
		this._forceWaitSequence = sequence;
		this._closed = false;
		_forceWaitSequence = sequence;
	}

	public boolean isActive() {
		return this._loop_timer.isActive();
	}

	public Scheduler start() {
		this.unpause();
		synchronized (RealtimeProcessManager.class) {
			if (_processScheduler != null) {
				RealtimeProcessManager.get().delete(_processScheduler);
			}
			if (_processScheduler == null || _processScheduler.isDead()) {
				_processScheduler = new SchedulerProcess(this);
			}
			_processScheduler.setDelay(0);
			RealtimeProcessManager.get().addProcess(_processScheduler);
		}
		return this;
	}

	public Scheduler kill() {
		if (_processScheduler != null) {
			_processScheduler.kill();
		}
		return this;
	}

	public Scheduler stop() {
		this.pause();
		this.kill();
		return this;
	}

	public Scheduler pause() {
		this._loop_timer.pause();
		return this;
	}

	public Scheduler unpause() {
		this._loop_timer.unpause();
		return this;
	}

	public boolean paused() {
		return !isActive();
	}

	public boolean add(Interval sched) {
		return _scheduled.add(sched);
	}

	public boolean remove(Interval sched) {
		return _scheduled.remove(sched);
	}

	public Interval removeIndex(int idx) {
		return _scheduled.removeIndex(idx);
	}

	public Interval getIndex(int idx) {
		return _scheduled.get(idx);
	}

	public TArray<Interval> findName(String name) {
		TArray<Interval> result = new TArray<Interval>();
		for (int i = _scheduled.size - 1; i > -1; i--) {
			Interval u = _scheduled.get(i);
			if (u != null && name.equals(u.getName())) {
				result.add(u);
			}
		}
		return result.reverse();
	}

	public Scheduler removeName(String name) {
		for (int i = _scheduled.size - 1; i > -1; i--) {
			Interval u = _scheduled.get(i);
			if (u != null && name.equals(u.getName())) {
				_scheduled.removeIndex(i);
			}
		}
		return this;
	}

	public Scheduler clear() {
		_scheduled.clear();
		return this;
	}

	public boolean completed() {
		boolean c = _scheduled.isEmpty();
		if (c) {
			return true;
		} else {
			final int size = _scheduled.size;
			int count = 0;
			for (int i = _scheduled.size - 1; i > -1; i--) {
				Interval u = _scheduled.get(i);
				if (u != null && u.completed()) {
					count++;
				}
			}
			c = (count >= size);
		}
		return c;
	}

	public void update(LTimerContext context) {
		if (_closed) {
			return;
		}
		if (_loop_timer.action(context)) {
			if (_scheduled.size > 0) {
				final boolean seq = (_forceWaitSequence && _removeSequenceTask);
				int index = seq ? 0 : MathUtils.max(0, _childIndex);
				Interval i = _scheduled.get(index);
				if (i != null) {
					if (i._loop_timer.action(context)) {
						i.loop();
					}
					if (_forceWaitSequence) {
						if (i.completed()) {
							if (_removeSequenceTask) {
								_scheduled.removeFirst();
							} else {
								_childIndex++;
							}
						} else if (i.completed() && !seq) {
							_childIndex++;
						}
					} else {
						if (_removeSequenceTask) {
							_scheduled.removeFirst();
						} else {
							_childIndex++;
						}
					}
				}
				if (_childIndex >= _scheduled.size) {
					_childIndex = 0;
				}
			}
		}
	}

	public Scheduler reset() {
		_childIndex = 0;
		_loop_timer.reset();
		for (int i = _scheduled.size - 1; i > -1; i--) {
			Interval u = _scheduled.get(i);
			if (u != null) {
				u._loop_timer.reset();
			}
		}
		return this;
	}

	public Scheduler setIndex(int idx) {
		this._childIndex = idx;
		this._forceWaitSequence = false;
		return this;
	}

	public int getIndex() {
		return this._childIndex;
	}

	public int size() {
		return _scheduled.size;
	}

	public long getDelay() {
		return _loop_timer.getDelay();
	}

	public Scheduler setDelay(long delay) {
		_loop_timer.setDelay(delay);
		return this;
	}

	public String getName() {
		return this._loop_timer.getName();
	}

	public LTimer currentTimer() {
		return _loop_timer;
	}

	public boolean isClosed() {
		return this._closed;
	}

	@Override
	public void close() {
		clear();
		stop();
		_closed = true;
	}

}
