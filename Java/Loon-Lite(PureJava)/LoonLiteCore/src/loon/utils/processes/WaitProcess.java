/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import loon.events.ActionUpdate;
import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.utils.timer.LTimerContext;

public class WaitProcess extends RealtimeProcess {

	protected boolean isAutoKill = true;

	private Updateable _update;

	private BooleanValue _value = new BooleanValue(false);

	private RealtimeProcess _waitProcess;

	public WaitProcess(Updateable update) {
		this(getProcessName(), 60, update);
	}

	public WaitProcess(long delay, Updateable update) {
		this(getProcessName(), delay, update);
	}

	public WaitProcess(String id, long delay, Updateable update) {
		this(id, delay, GameProcessType.Other, update);
	}

	public WaitProcess(String id, long delay, GameProcessType pt, Updateable update) {
		super(id, delay);
		this.setProcessType(pt);
		this.isAutoKill = true;
		this._update = update;
	}

	@Override
	public void run(LTimerContext time) {
		if (_update != null) {
			final boolean existWait = isDeffered();
			if (!existWait) {
				if (_update instanceof ActionUpdate) {
					ActionUpdate update = (ActionUpdate) _update;
					update.action(this);
					if (update.completed()) {
						kill();
					}
				} else {
					_update.action(this);
					if (isAutoKill) {
						kill();
					}
				}
			}
		}
	}

	public boolean completed() {
		return _value.result();
	}

	public BooleanValue get() {
		return _value;
	}

	public WaitProcess wait(RealtimeProcess process) {
		this._waitProcess = process;
		return this;
	}

	public WaitProcess freeWait() {
		return wait(null);
	}

	public boolean isDeffered() {
		return _waitProcess != null && !_waitProcess.isDead;
	}

	public boolean isAutoKill() {
		return isAutoKill;
	}

	public WaitProcess setAutoKill(boolean k) {
		this.isAutoKill = k;
		return this;
	}

	@Override
	public void finish() {
		super.finish();
		_value.set(true);
	}

}