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
import loon.utils.Disposes;
import loon.utils.LIterable;
import loon.utils.SortedList;
import loon.utils.reply.ClosableIterator;
import loon.utils.reply.ObjRef;

public class Yielderable implements LIterable<WaitCoroutine>, ClosableIterator<WaitCoroutine> {

	private final Disposes _disposes = new Disposes();

	private final SortedList<YieldExecute> _executes;

	private Coroutine _coroutine;

	private WaitCoroutine _waitCoroutine;

	private boolean _returning;

	public Yielderable(YieldExecute... es) {
		_executes = new SortedList<YieldExecute>(es);
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

	protected void call() {
		if (_executes.size > 0) {
			this._waitCoroutine = _executes.element().execute(this);
		} else {
			this._waitCoroutine = null;
		}
		if (!_returning) {
			_executes.remove();
		}
	}

	public WaitCoroutine returning(Object t) {
		if (t instanceof Boolean) {
			this._returning = (Boolean) t;
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

	@Override
	public boolean hasNext() {
		return _executes.size > 0;
	}

	@Override
	public WaitCoroutine next() {
		call();
		if (_waitCoroutine == null) {
			return WaitCoroutine.seconds(0f);
		}
		return _waitCoroutine;
	}

	@Override
	public void remove() {
		_executes.remove();
		_returning = false;
	}

	@Override
	public void close() {
		_disposes.close();
		_executes.clear();
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

}
