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

import java.util.Comparator;
import java.util.Iterator;

import loon.LSystem;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.ObjectMap.Keys;
import loon.utils.ObjectMap.Values;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

/**
 * 时间线工具用类,用于构建和存储一组全局使用的时间线事件集合
 */
public class TimeLineProcess extends RealtimeProcess {

	private final static String METHOD_START = "start";

	private final static String METHOD_STOP = "stop";

	private final static String METHOD_COMPLETE = "complete";

	private final static String METHOD_CANCEL = "cancel";

	private final static String METHOD_PAUSE = "pause";

	private final static String METHOD_RESUME = "resume";

	private final static String METHOD_RESET = "reset";

	private final static String METHOD_RESET_ALL = "resetall";

	private final static String METHOD_OVER = "over";

	static class TimeComparator implements Comparator<TimeLineEventTarget> {

		@Override
		public int compare(TimeLineEventTarget o1, TimeLineEventTarget o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return o2.getPriority() - o1.getPriority();
		}

	}

	private final static TimeComparator _currentTimeComp = new TimeComparator();

	private final ObjectMap<String, TArray<TimeLineEventTarget>> _listeners;

	private final TArray<String> _disableLoopEvents;

	private final TArray<TimeLineEventTarget> _tempRemoved;

	private TArray<TimeLineEventTarget> _tempEvents;

	private float _frameBase = LSystem.MIN_SECONE_SPEED_FIXED;

	private int _frame;

	private int _maxLoopRepeat;

	private boolean _autoReverse;

	// 若此项为真,则事件必须顺序执行,一个完成才能进行下一个
	private boolean _isWaitLastEvent;

	private boolean _isFrameBased;

	public TimeLineProcess() {
		this(-1);
	}

	public TimeLineProcess(int loopCount) {
		super(0);
		this.setProcessType(GameProcessType.TimeLine);
		this._maxLoopRepeat = MathUtils.max(loopCount, -1);
		this._listeners = new ObjectMap<String, TArray<TimeLineEventTarget>>();
		this._disableLoopEvents = new TArray<String>();
		this._tempRemoved = new TArray<TimeLineEventTarget>();
	}

	public TimeLineProcess addEventListeners(final TimeLineEventTarget... es) {
		if (es == null || es.length == 0) {
			return this;
		}
		for (int i = 0; i < es.length; i++) {
			TimeLineEventTarget e = es[i];
			if (e != null) {
				addEventListener(e);
			}
		}
		return this;
	}

	public TimeLineProcess addEventListener(final TimeLineEventTarget e) {
		if (e == null) {
			return this;
		}
		if (StringUtils.isEmpty(e._eventName)) {
			return this;
		}
		TArray<TimeLineEventTarget> listeners = this._listeners.get(e._eventName);
		if (listeners == null) {
			listeners = new TArray<TimeLineEventTarget>();
			_listeners.put(e._eventName, listeners);
		}
		if (!listeners.contains(e)) {
			listeners.add(e);
			listeners.sort(_currentTimeComp);
		}
		return this;
	}

	public boolean removeEventListener(final TimeLineEventTarget e) {
		if (_listeners.size == 0) {
			return false;
		}
		if (e == null) {
			return false;
		}
		if (StringUtils.isEmpty(e._eventName)) {
			return false;
		}
		final TArray<TimeLineEventTarget> listeners = this._listeners.get(e._eventName);
		if (listeners != null) {
			boolean result = listeners.remove(e);
			listeners.sort(_currentTimeComp);
			return result;
		}
		return false;
	}

	public TimeLineEventTarget removeEventListenerIndex(final String eveName, final int idx) {
		if (_listeners.size == 0) {
			return null;
		}
		if (StringUtils.isEmpty(eveName)) {
			return null;
		}
		final TArray<TimeLineEventTarget> listeners = this._listeners.get(eveName);
		if (listeners != null) {
			TimeLineEventTarget result = listeners.removeIndex(idx);
			listeners.sort(_currentTimeComp);
			return result;
		}
		return null;
	}

	public TimeLineProcess clearEventListener() {
		_listeners.clear();
		return this;
	}

	public TimeLineProcess clearEventListener(final TimeLineEventTarget e) {
		if (_listeners.size == 0) {
			return this;
		}
		if (e == null) {
			return this;
		}
		return clearEventListener(e._eventName);
	}

	public TimeLineProcess clearEventListener(final String eventName) {
		if (_listeners.size == 0) {
			return this;
		}
		if (!StringUtils.isEmpty(eventName)) {
			final TArray<TimeLineEventTarget> listeners = _listeners.get(eventName);
			if (listeners != null) {
				listeners.clear();
			}
		} else {
			_listeners.clear();
		}
		return this;
	}

	public TimeLineProcess dispatchEvent(final String eventName) {
		if (_listeners.size == 0) {
			return this;
		}
		if (StringUtils.isEmpty(eventName)) {
			return this;
		}
		_tempRemoved.clear();
		final TArray<TimeLineEventTarget> list = this._listeners.get(eventName);
		if (list != null) {
			for (int i = 0, len = list.size; i < len; i++) {
				final TimeLineEventTarget eve = list.get(i);
				if (eve != null) {
					if (!callEvent(_frameBase, eve, i == 0 ? null : list.get(i - 1))) {
						continue;
					}
				}
			}
			list.removeAll(_tempRemoved);
			if (_tempRemoved.size > 0) {
				_tempRemoved.clear();
				list.sort(_currentTimeComp);
			}
		}
		return this;
	}

	protected boolean callEvent(final float dt, final TimeLineEventTarget eve, final TimeLineEventTarget lastEvent) {
		if (eve.isNotInit()) {
			eve.start();
		}
		if (!eve._actived) {
			return false;
		}
		if (eve._paused || eve._stoped || eve._completed || eve._cancelled) {
			return false;
		}
		// 等待模式下上一个事件不完成则不会继续处理
		if (_isWaitLastEvent && lastEvent != null && !lastEvent.isOver()) {
			return false;
		}
		eve.onUpdate(eve._elapsed = dt);
		final boolean over = eve.isOver();
		if (over) {
			if (eve._cancelled) {
				eve.cancel();
			}
			if (eve._completed) {
				eve.complete();
			}
			if (eve._stoped) {
				eve.stop();
			}
		}
		if (over || (this._maxLoopRepeat > -1 && eve._age >= this._maxLoopRepeat)) {
			_tempRemoved.add(eve);
		} else {
			eve.reset();
		}
		return true;
	}

	@Override
	public void run(LTimerContext time) {
		if (_listeners.size == 0) {
			return;
		}
		synchronized (TimeLineProcess.class) {
			_tempRemoved.clear();
			final Keys<String> events = _listeners.keys();
			for (Iterator<String> keys = events.iterator(); keys.hasNext();) {
				final String keyName = keys.next();
				if (_disableLoopEvents.contains(keyName)) {
					continue;
				}
				final TArray<TimeLineEventTarget> list = _listeners.get(keyName);
				synchronized (list) {
					_tempEvents = new TArray<TimeLineEventTarget>(list);
				}
				for (int i = 0, size = _tempEvents.size; i < size; i++) {
					final TimeLineEventTarget eve = _tempEvents.get(i);
					if (eve == null) {
						continue;
					}
					synchronized (eve) {
						if (!callEvent(_isFrameBased ? _frameBase : time.getMilliseconds(), eve,
								i == 0 ? null : list.get(i - 1))) {
							continue;
						}
					}
				}

				list.removeAll(_tempRemoved);
				if (_tempRemoved.size > 0) {
					_tempRemoved.clear();
					list.sort(_currentTimeComp);
				}
				// 自动翻转时间线
				if (_autoReverse && isAllOver(keyName)) {
					list.reverse();
				}
			}
			_frame++;
		}
	}

	public TimeLineProcess setLoopRepeat(int loopCount) {
		this._maxLoopRepeat = MathUtils.max(loopCount, -1);
		if (_maxLoopRepeat > 0) {
			if (_listeners.size == 0) {
				return this;
			}
			final Keys<String> lines = _listeners.keys();
			for (Iterator<String> e = lines.iterator(); e.hasNext();) {
				callResetAllEvent(e.next());
			}
		}
		return this;
	}

	public TimeLineProcess loop() {
		return setLoopRepeat(-1);
	}

	public int getLoopRepeat() {
		return this._maxLoopRepeat;
	}

	public int getFrame() {
		return this._frame;
	}

	public TimeLineProcess callAllEvent(final String eventName, final String methodName) {
		if (_listeners.size == 0) {
			return this;
		}
		if (StringUtils.isEmpty(eventName)) {
			return this;
		}
		final TArray<TimeLineEventTarget> eves = _listeners.get(eventName);
		for (int i = eves.size - 1; i > -1; i--) {
			final TimeLineEventTarget eve = eves.get(i);
			if (eve != null) {
				if (METHOD_START.equals(methodName)) {
					eve.start();
				} else if (METHOD_STOP.equals(methodName)) {
					eve.stop();
				} else if (METHOD_COMPLETE.equals(methodName)) {
					eve.complete();
				} else if (METHOD_CANCEL.equals(methodName)) {
					eve.cancel();
				} else if (METHOD_PAUSE.equals(methodName)) {
					eve.pause();
				} else if (METHOD_RESUME.equals(methodName)) {
					eve.resume();
				} else if (METHOD_RESET.equals(methodName)) {
					eve.reset();
				} else if (METHOD_RESET_ALL.equals(methodName)) {
					eve.resetAll();
				}
			}
		}
		return this;
	}

	public TimeLineProcess callStartEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_START);
	}

	public TimeLineProcess callStopEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_STOP);
	}

	public TimeLineProcess callCompleteEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_COMPLETE);
	}

	public TimeLineProcess callCancelEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_CANCEL);
	}

	public TimeLineProcess callPauseEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_PAUSE);
	}

	public TimeLineProcess callResumeEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_RESUME);
	}

	public TimeLineProcess callResetEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_RESET);
	}

	public TimeLineProcess callResetAllEvent(final String eventName) {
		return callAllEvent(eventName, METHOD_RESET_ALL);
	}

	public boolean isAllEvent(final String eventName, final String methodName) {
		if (_listeners.size == 0) {
			return false;
		}
		if (StringUtils.isEmpty(eventName)) {
			return false;
		}
		final TArray<TimeLineEventTarget> eves = _listeners.get(eventName);
		for (int i = eves.size - 1; i > -1; i--) {
			final TimeLineEventTarget eve = eves.get(i);
			if (eve != null) {
				if (!eve._started) {
					return false;
				}
				if (METHOD_STOP.equals(methodName)) {
					if (!eve._stoped) {
						return false;
					}
				} else if (METHOD_COMPLETE.equals(methodName)) {
					if (!eve._completed) {
						return false;
					}
				} else if (METHOD_CANCEL.equals(methodName)) {
					if (!eve._cancelled) {
						return false;
					}
				} else if (METHOD_OVER.equals(methodName)) {
					if (!eve.isOver()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean isAllStop(final String eventName) {
		return isAllEvent(eventName, METHOD_STOP);
	}

	public boolean isAllCancel(final String eventName) {
		return isAllEvent(eventName, METHOD_CANCEL);
	}

	public boolean isAllComplete(final String eventName) {
		return isAllEvent(eventName, METHOD_COMPLETE);
	}

	public boolean isAllOver(final String eventName) {
		return isAllEvent(eventName, METHOD_OVER);
	}

	public TimeLineProcess reverse() {
		if (_listeners.size == 0) {
			return this;
		}
		final Values<TArray<TimeLineEventTarget>> lines = _listeners.values();
		for (Iterator<TArray<TimeLineEventTarget>> e = lines.iterator(); e.hasNext();) {
			final TArray<TimeLineEventTarget> eves = e.next();
			if (eves != null) {
				eves.reverse();
			}
		}
		return this;
	}

	public TimeLineProcess sort() {
		if (_listeners.size == 0) {
			return this;
		}
		final Values<TArray<TimeLineEventTarget>> lines = _listeners.values();
		for (Iterator<TArray<TimeLineEventTarget>> e = lines.iterator(); e.hasNext();) {
			final TArray<TimeLineEventTarget> eves = e.next();
			if (eves != null) {
				eves.sort(_currentTimeComp);
			}
		}
		return this;
	}

	public boolean isAutoReverse() {
		return _autoReverse;
	}

	public TimeLineProcess setAutoReverse(boolean a) {
		this._autoReverse = a;
		return this;
	}

	public float getFrameBase() {
		return _frameBase;
	}

	public TimeLineProcess setFrameBase(float f) {
		this.setFrameBased(f > 0);
		this._frameBase = MathUtils.max(LSystem.MIN_SECONE_SPEED_FIXED, f);
		return this;
	}

	public boolean isFrameBased() {
		return _isFrameBased;
	}

	public TimeLineProcess setFrameBased(boolean f) {
		this._isFrameBased = f;
		return this;
	}

	public boolean isWaitLastEvent() {
		return _isWaitLastEvent;
	}

	public TimeLineProcess setWaitLastEvent(boolean w) {
		this._isWaitLastEvent = w;
		return this;
	}

	public TimeLineProcess disableLoopEvents(final String eventName) {
		if (StringUtils.isEmpty(eventName)) {
			return this;
		}
		_disableLoopEvents.add(eventName);
		return this;
	}

	public TimeLineProcess enableLoopEvents(final String eventName) {
		if (StringUtils.isEmpty(eventName)) {
			return this;
		}
		_disableLoopEvents.remove(eventName);
		return this;
	}

	@Override
	public void close() {
		super.close();
		if (_listeners.size > 0) {
			final Values<TArray<TimeLineEventTarget>> lines = _listeners.values();
			for (Iterator<TArray<TimeLineEventTarget>> e = lines.iterator(); e.hasNext();) {
				final TArray<TimeLineEventTarget> eves = e.next();
				if (eves != null) {
					for (int i = eves.size - 1; i > -1; i--) {
						final TimeLineEventTarget tle = eves.get(i);
						if (tle != null) {
							tle.close();
						}
					}
				}
				eves.close();
			}
		}
		_listeners.close();
		_disableLoopEvents.close();
		_tempRemoved.clear();
	}

}
