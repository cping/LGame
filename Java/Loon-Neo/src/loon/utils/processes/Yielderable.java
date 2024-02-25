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

import loon.LRelease;
import loon.events.QueryEvent;
import loon.utils.Calculator;
import loon.utils.Disposes;
import loon.utils.LIterable;
import loon.utils.ObjectMap;
import loon.utils.SortedList;
import loon.utils.reply.ClosableIterator;
import loon.utils.reply.ObjRef;

public class Yielderable implements LIterable<WaitCoroutine>, ClosableIterator<WaitCoroutine> {

	private final Disposes _disposes = new Disposes();

	private final ObjectMap<String, Calculator> _varCachecalcs = new ObjectMap<String, Calculator>();

	private final SortedList<YieldLoop> _loops;

	private final SortedList<YieldExecute> _executes;

	private final SortedList<YieldExecute> _saveInitYields;

	private Coroutine _coroutine;

	private WaitCoroutine _waitCoroutine;

	private boolean _returning;

	public Yielderable(YieldExecute... es) {
		this(new SortedList<YieldExecute>(es));
	}

	public Yielderable(SortedList<YieldExecute> es) {
		this._executes = es;
		this._saveInitYields = new SortedList<YieldExecute>(es);
		this._loops = new SortedList<YieldLoop>();
	}

	public WaitCoroutine startCoroutine(Coroutine c) {
		if (_coroutine != null) {
			_coroutine.startCoroutine(c);
		}
		return returning(false);
	}

	public WaitCoroutine startCoroutine(String name) {
		if (_coroutine != null) {
			_coroutine.startCoroutine(name);
		}
		return returning(false);
	}

	public WaitCoroutine pauseCoroutine(String name) {
		if (_coroutine != null) {
			_coroutine.pauseCoroutine(name);
		}
		return returning(false);
	}

	public WaitCoroutine stopCoroutine(String name) {
		if (_coroutine != null) {
			_coroutine.stopCoroutine(name);
		}
		return returning(false);
	}

	public WaitCoroutine resetCoroutine(String name) {
		if (_coroutine != null) {
			_coroutine.resetCoroutine(name);
		}
		return returning(false);
	}

	public WaitCoroutine deleteCoroutine(String name) {
		if (_coroutine != null) {
			_coroutine.deleteCoroutine(name);
		}
		return returning(false);
	}

	protected void setCoroutine(Coroutine c) {
		this._coroutine = c;
	}

	public Coroutine coroutine() {
		return _coroutine;
	}

	public boolean isActive() {
		return _coroutine != null ? _coroutine.isActive() : false;
	}

	public Exception getException() {
		return _coroutine != null ? _coroutine.getLastException() : null;
	}

	public Yielderable cancal() {
		if (_coroutine != null) {
			_coroutine.cancel();
		}
		return this;
	}

	public Yielderable pause() {
		if (_coroutine != null) {
			_coroutine.pause();
		}
		return this;
	}

	public Yielderable resume() {
		if (_coroutine != null) {
			_coroutine.resume();
		}
		return this;
	}

	public ObjRef<?> getReturn() {
		if (_coroutine != null) {
			WaitCoroutine w = _coroutine.getWaitCondition();
			if (w != null) {
				return w.getRef();
			}
		}
		return ObjRef.empty();
	}

	/**
	 * 如果满足条件,进行此循环
	 * 
	 * @param condition
	 * @param yl
	 * @return
	 */
	public Yielderable loop(boolean condition, YieldLoop yl) {
		if (condition && yl != null) {
			this._loops.add(yl);
		}
		return this;
	}

	/**
	 * 如果存在的计算器变量满足条件,进行此循环
	 * 
	 * @param v
	 * @param c
	 * @param yl
	 * @return
	 */
	public Yielderable loopCalc(String v, QueryEvent<Calculator> c, YieldLoop yl) {
		final Calculator calc = _varCachecalcs.get(v);
		if (calc == null) {
			return this;
		}
		if (c.hit(calc) && yl != null) {
			this._loops.add(yl);
		}
		return this;
	}

	public boolean isLoop() {
		return _loops.size > 0;
	}

	protected boolean loop() {
		if (_loops.size > 0) {
			YieldLoop l = _loops.pop();
			if (l != null) {
				l.loop();
			}
			return true;
		} else {
			return false;
		}
	}

	public Calculator newCalc(String k, String v) {
		Calculator c = getCalc(k);
		if (c == null) {
			_varCachecalcs.put(k, (c = new Calculator()));
		}
		c.set(v);
		return c;
	}

	public Calculator newCalc(String k) {
		return newCalc(k, 0f);
	}

	public Calculator newCalc(String k, float v) {
		Calculator c = getCalc(k);
		if (c == null) {
			_varCachecalcs.put(k, (c = new Calculator()));
		}
		c.set(v);
		return c;
	}

	public Calculator existCalc(String k) {
		Calculator c = getCalc(k);
		if (c == null) {
			_varCachecalcs.put(k, (c = new Calculator()));
		}
		return c;
	}

	public Calculator getCalc(String v) {
		return _varCachecalcs.get(v);
	}

	protected void call() {
		if (_executes.size > 0) {
			_loops.clear();
			this._waitCoroutine = _executes.element().execute(this);
		} else {
			this._waitCoroutine = null;
		}
		if (loop()) {
			return;
		}
		if (!_returning) {
			if (_executes.size > 0) {
				_executes.removeFirst();
			}
			_loops.clear();
		}
	}

	public WaitCoroutine breakSelf() {
		return breakSelf(null);
	}

	public WaitCoroutine breakSelf(Object t) {
		this._returning = false;
		if (_coroutine != null) {
			_coroutine.cancel();
		}
		return WaitCoroutine.frames(0, ObjRef.of(t));
	}

	public WaitCoroutine returning(Object t) {
		if (t instanceof Boolean) {
			this._returning = ((Boolean) t).booleanValue();
		} else {
			this._returning = true;
		}
		return WaitCoroutine.frames(0, ObjRef.of(t));
	}

	public WaitCoroutine returning(ObjRef<?> t) {
		this._returning = true;
		return WaitCoroutine.frames(0, t);
	}

	public WaitCoroutine returnings(float s) {
		this._returning = true;
		return WaitCoroutine.seconds(s);
	}

	public WaitCoroutine returnings(float s, ObjRef<?> t) {
		this._returning = true;
		return WaitCoroutine.seconds(s, t);
	}

	public WaitCoroutine returningf(int f) {
		this._returning = true;
		return WaitCoroutine.frames(f);
	}

	public WaitCoroutine returningf(int f, ObjRef<?> t) {
		this._returning = true;
		return WaitCoroutine.frames(f, t);
	}

	public WaitCoroutine empty() {
		return WaitCoroutine.empty();
	}

	public WaitCoroutine seconds(float s) {
		return WaitCoroutine.seconds(s);
	}

	public WaitCoroutine frames(int f) {
		return WaitCoroutine.frames(f);
	}

	public Yielderable dispose(LRelease... r) {
		_disposes.put(r);
		return this;
	}

	public Yielderable dispose(LRelease r) {
		_disposes.put(r);
		return this;
	}

	public int size() {
		return _executes.size;
	}

	public Yielderable cpy() {
		return new Yielderable(_saveInitYields);
	}

	@Override
	public boolean hasNext() {
		return _executes.size > 0;
	}

	@Override
	public WaitCoroutine next() {
		if (_waitCoroutine != null && !_waitCoroutine.isCompleted()) {
			return _waitCoroutine;
		}
		call();
		if (_waitCoroutine == null) {
			return WaitCoroutine.seconds(0f);
		}
		return _waitCoroutine;
	}

	@Override
	public void reset() {
		_loops.clear();
		_varCachecalcs.clear();
		_executes.clear();
		_executes.addAll(_saveInitYields);
		if (_coroutine != null) {
			_coroutine.setup(this, false);
		}
		if (_waitCoroutine != null) {
			_waitCoroutine.reset();
			_waitCoroutine = null;
		}
		_returning = false;
	}

	@Override
	public ClosableIterator<WaitCoroutine> iterator() {
		return this;
	}

	@Override
	public boolean isReturning() {
		return _returning;
	}

	@Override
	public void remove() {
		_executes.remove();
		_loops.clear();
		_varCachecalcs.clear();
		_returning = false;
	}

	@Override
	public void close() {
		_disposes.close();
		_loops.clear();
		_executes.clear();
		_saveInitYields.clear();
		_varCachecalcs.clear();
		_returning = false;
	}

}
