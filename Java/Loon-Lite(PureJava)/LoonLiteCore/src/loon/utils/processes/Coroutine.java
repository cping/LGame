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

import loon.LSystem;
import loon.utils.reply.ClosableIterator;

public class Coroutine {

	protected CoroutineStatus _status;

	protected CoroutineProcess _mainProcess;

	private Yielderable _mainEnumerator = null;
	private ClosableIterator<WaitCoroutine> _childEnumerator = null;
	private WaitCoroutine _currentCondition;
	private Exception _lastException;

	private final String _coroutineName;

	public Coroutine() {
		this(null, null);
	}

	public Coroutine(CoroutineProcess process, Yielderable y) {
		this(LSystem.UNKNOWN, process, y);
	}

	public Coroutine(String name, CoroutineProcess process, Yielderable y) {
		this._coroutineName = name;
		this._mainProcess = process;
		if (y != null) {
			setup(y);
		}
	}

	public void update(long elapsedTime) {
		if (this._status != CoroutineStatus.Running) {
			return;
		}
		try {
			if (_childEnumerator != null && _childEnumerator.hasNext()) {
				this._currentCondition = this._childEnumerator.next();
				if (this._currentCondition != null) {
					this._currentCondition.update(elapsedTime);
				}
			} else if (this._currentCondition != null && this._currentCondition.isCompleted()) {
				this._status = CoroutineStatus.Completed;
			}
		} catch (Exception e) {
			this._lastException = e;
			this._status = CoroutineStatus.Error;
			e.printStackTrace();
			LSystem.error(e.getMessage());
		}
	}

	public Coroutine startCoroutine(Coroutine coroutine) {
		if (_mainProcess != null) {
			return _mainProcess.startCoroutine(coroutine);
		}
		return null;
	}

	public Coroutine startCoroutine(String name) {
		if (_mainProcess != null) {
			return _mainProcess.startCoroutine(name);
		}
		return null;
	}

	public Yielderable getYielderable() {
		return _mainEnumerator;
	}

	public Coroutine setup(Yielderable y) {
		return setup(y, true);
	}

	Coroutine setup(Yielderable y, boolean closed) {
		this._mainEnumerator = y;
		if (this._mainEnumerator != null) {
			this._mainEnumerator.setCoroutine(this);
			this._status = CoroutineStatus.Running;
			this._lastException = null;
			if (closed && this._childEnumerator != null) {
				this._childEnumerator.close();
			}
			this._childEnumerator = y.iterator();
			this._currentCondition = null;
		}
		return this;
	}

	public WaitCoroutine getWaitCondition() {
		return _currentCondition;
	}

	public boolean isActive() {
		return this._status == CoroutineStatus.Paused || this._status == CoroutineStatus.Running;
	}

	public CoroutineStatus getStatus() {
		return _status;
	}

	public Exception getLastException() {
		return _lastException;
	}

	public Coroutine pause() {
		if (this._status == CoroutineStatus.Running) {
			this._status = CoroutineStatus.Paused;
		}
		return this;
	}

	public Coroutine resume() {
		if (this._status == CoroutineStatus.Paused) {
			this._status = CoroutineStatus.Running;
		}
		return this;
	}

	public Coroutine start() {
		this._status = CoroutineStatus.Running;
		return this;
	}

	public Coroutine reset() {
		if (_mainEnumerator != null) {
			_mainEnumerator.reset();
		}
		return this;
	}

	public Coroutine cancel() {
		this._status = CoroutineStatus.Cancel;
		return this;
	}

	public String getCoroutineName() {
		return _coroutineName;
	}
}
