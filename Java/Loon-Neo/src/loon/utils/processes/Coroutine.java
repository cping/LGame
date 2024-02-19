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

	private Yielderable _mainEnumerator = null;
	private ClosableIterator<WaitCoroutine> _childEnumerator = null;
	private WaitCoroutine _currentCondition;
	private Exception _lastException;

	public void update(long elapsedTime) {
		if (this._status != CoroutineStatus.Running) {
			return;
		}
		try {
			if (_currentCondition == null) {
				this._currentCondition = this._childEnumerator.next();
			}
			this._currentCondition.update(elapsedTime);
			if (this._currentCondition.isCompleted()) {
				if (this._childEnumerator.hasNext()) {
					this._currentCondition = this._childEnumerator.next();
				} else {
					this._status = CoroutineStatus.Completed;
				}
			}
		} catch (Exception e) {
			this._lastException = e;
			this._status = CoroutineStatus.Error;
			LSystem.error(e.getMessage());
		}
	}

	public Yielderable getYielderable() {
		return _mainEnumerator;
	}

	public Coroutine setup(Yielderable y) {
		this._mainEnumerator = y;
		if (this._mainEnumerator != null) {
			this._mainEnumerator.setCoroutine(this);
			this._status = CoroutineStatus.Running;
			this._lastException = null;
			if (this._childEnumerator != null) {
				this._childEnumerator.close();
			}
			this._childEnumerator = y.iterator();
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

	public Coroutine cancel() {
		if (this._childEnumerator != null) {
			this._childEnumerator.close();
		}
		this._status = CoroutineStatus.Cancel;
		return this;
	}
}
