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
package loon.utils.processes;

import java.util.Iterator;

import loon.LRelease;
import loon.LSystem;
import loon.utils.SortedList;
import loon.utils.timer.LTimerContext;

/**
 * loon使用循环模拟的协程实现,效果是在不使用线程的情况下安全的使游戏异步(统一代码格式,方便换语言移植……)
 */
public class CoroutineProcess extends RealtimeProcess implements LRelease {

	private final SortedList<Coroutine> _cycles = new SortedList<Coroutine>();

	private Coroutine _mainCoroutine = null;

	private boolean _coroutineRunning = false;

	private boolean _allowAutoRemove = false;

	public CoroutineProcess() {
		this(0);
	}

	public CoroutineProcess(long delay) {
		super(delay);
		this._coroutineRunning = true;
	}

	/**
	 * 构建一组未命名的协程操作
	 * 
	 * @param es
	 * @return
	 */
	public Coroutine call(YieldExecute... es) {
		return startCoroutine(new Yielderable(es));
	}

	/**
	 * 构建并调用指定名称协程
	 * 
	 * @param name
	 * @param es
	 * @return
	 */
	public Coroutine call(String name, YieldExecute... es) {
		return startCoroutine(new Coroutine(name, this, new Yielderable(es)));
	}

	/**
	 * 添加一个指定名称协程(只添加,不调用)
	 * 
	 * @param name
	 * @param es
	 * @return
	 */
	public Coroutine putCall(String name, YieldExecute... es) {
		final Coroutine c = new Coroutine(name, this, new Yielderable(es));
		final boolean result = putCoroutine(c);
		return result ? c : null;
	}

	/**
	 * 调用指定名称协程
	 * 
	 * @param name 协程名称(需要缓存过的名称或者call过一次)
	 * @return
	 */
	public Coroutine call(String name) {
		return startCoroutine(name);
	}

	/**
	 * 注入一个协程,并替换协程内部实际执行对象
	 * 
	 * @param coroutine
	 * @param y
	 * @param put       是否真实添加
	 * @return
	 */
	protected Coroutine startCoroutine(Coroutine coroutine, Yielderable y, boolean put) {
		if (coroutine != null) {
			if (y != null) {
				coroutine.setup(y);
			}
			if (put) {
				putCoroutine(coroutine);
			}
			_mainCoroutine = coroutine;
		}
		return coroutine;
	}

	/**
	 * 注入协程并执行
	 * 
	 * @param coroutine 协程对象
	 * @param put       是否真实添加
	 * @return
	 */
	protected Coroutine startCoroutine(Coroutine coroutine, boolean put) {
		return startCoroutine(coroutine, null, put);
	}

	/**
	 * 注入一组未命名的协程
	 * 
	 * @param coroutine
	 * @return
	 */
	public Coroutine startCoroutine(Coroutine coroutine) {
		return startCoroutine(coroutine, true);
	}

	/**
	 * 注入一组未命名的协程
	 * 
	 * @param y
	 * @return
	 */
	public Coroutine startCoroutine(Yielderable y) {
		return startCoroutine(new Coroutine(getCoroutineName(1), this, y));
	}

	/**
	 * 重新调用指定loon协程
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine startCoroutine(String name) {
		final Coroutine coroutineSaved = getCoroutine(name);
		if (coroutineSaved != null) {
			coroutineSaved.reset();
			return startCoroutine(coroutineSaved, false);
		}
		return coroutineSaved;
	}

	/**
	 * 重新调用指定loon协程并注入新内容
	 * 
	 * @param name
	 * @param y
	 * @return
	 */
	public Coroutine startCoroutine(String name, Yielderable y) {
		final Coroutine coroutine = getCoroutine(name);
		if (coroutine != null) {
			return startCoroutine(coroutine, y, false);
		}
		return coroutine;
	}

	/**
	 * 停止指定名称协程的执行
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine stopCoroutine(String name) {
		final Coroutine coroutine = getCoroutine(name);
		if (coroutine != null) {
			coroutine.cancel();
		}
		return coroutine;
	}

	/**
	 * 暂停指定名称协程的执行
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine pauseCoroutine(String name) {
		final Coroutine coroutine = getCoroutine(name);
		if (coroutine != null) {
			coroutine.pause();
		}
		return coroutine;
	}

	/**
	 * 重启指定名称协程的执行
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine resetCoroutine(String name) {
		final Coroutine coroutine = getCoroutine(name);
		if (coroutine != null) {
			coroutine.reset();
		}
		return coroutine;
	}

	/**
	 * 删除指定名称的协程
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine deleteCoroutine(String name) {
		final Coroutine coroutine = getCoroutine(name);
		if (coroutine != null) {
			coroutine.cancel();
			removeCoroutine(coroutine);
			if (_mainCoroutine == coroutine) {
				_mainCoroutine = null;
			}
		}
		return coroutine;
	}

	/**
	 * 获得指定名称的协程
	 * 
	 * @param name
	 * @return
	 */
	public Coroutine getCoroutine(String name) {
		if (_cycles.size == 0) {
			return null;
		}
		for (Iterator<Coroutine> it = _cycles.iterator(); it.hasNext();) {
			Coroutine c = it.next();
			if (c != null && c.getCoroutineName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * 添加一个协程到程序中(不执行,仅添加)
	 * 
	 * @param c
	 * @return
	 */
	public boolean putCoroutine(Coroutine c) {
		if (c != null && !_cycles.contains(c)) {
			if (_cycles.size > 0) {
				for (Iterator<Coroutine> it = _cycles.iterator(); it.hasNext();) {
					Coroutine ele = it.next();
					if (ele.getCoroutineName().equals(c.getCoroutineName())) {
						_cycles.remove(ele);
					}
				}
			}
			return this._cycles.add(c);
		}
		return false;
	}

	/**
	 * 删除指定协程对象
	 * 
	 * @param c
	 * @return
	 */
	public boolean removeCoroutine(Coroutine c) {
		if (c != null) {
			return this._cycles.remove(c);
		}
		return false;
	}

	/**
	 * 删除指定名称的协程
	 * 
	 * @param name
	 * @return
	 */
	public boolean removeCoroutine(String name) {
		int count = 0;
		if (_cycles.size > 0) {
			for (Iterator<Coroutine> it = _cycles.iterator(); it.hasNext();) {
				Coroutine ele = it.next();
				if (ele.getCoroutineName().equals(name)) {
					_cycles.remove(ele);
					count++;
				}
			}
		}
		return count > 0;
	}

	/**
	 * 删除全部协程对象
	 * 
	 * @return
	 */
	public CoroutineProcess clearCoroutine() {
		this._cycles.clear();
		return this;
	}

	protected String getCoroutineName(int idx) {
		return LSystem.UNKNOWN + LSystem.DASHED + (_cycles.size + idx);
	}

	@Override
	public void kill() {
		super.kill();
		_coroutineRunning = false;
	}

	@Override
	public void run(LTimerContext time) {
		if (_coroutineRunning) {
			updateCoroutine(time.unscaledTimeSinceLastUpdate);
		}
	}

	public void updateCoroutine(long e) {
		if (_mainCoroutine != null) {
			_mainCoroutine.update(e);
			if (isCompleted(_mainCoroutine)) {
				return;
			} else {
				if (_allowAutoRemove) {
					removeCoroutine(_mainCoroutine);
				}
				_mainCoroutine = null;
			}
		}
		if (_cycles.size > 0) {
			final Coroutine c = this._cycles.element();
			if (c != null && c.isActive()) {
				c.update(e);
				if (_allowAutoRemove && isCompleted(c)) {
					removeCoroutine(c);
				}
			}
		}
	}

	protected boolean isCompleted(Coroutine c) {
		return c == null ? true : c.isActive();
	}

	/**
	 * 协程运行中
	 * 
	 * @return
	 */
	public boolean isCoroutineRunning() {
		return _coroutineRunning;
	}

	/**
	 * 是否自定删除已完成协程
	 * 
	 * @return
	 */
	public boolean isAllowAutoRemove() {
		return _allowAutoRemove;
	}

	/**
	 * 允许删除已执行完毕的协程(默认false)
	 * 
	 * @param a
	 * @return
	 */
	public CoroutineProcess setAllowAutoRemove(boolean a) {
		this._allowAutoRemove = a;
		return this;
	}

	@Override
	public CoroutineProcess reset() {
		super.reset();
		this.clearCoroutine();
		this._mainCoroutine = null;
		this._coroutineRunning = true;
		this._allowAutoRemove = false;
		return this;
	}

	@Override
	public void close() {
		super.close();
		this.clearCoroutine();
		this._mainCoroutine = null;
		this._coroutineRunning = false;
		this._allowAutoRemove = false;
	}

}
