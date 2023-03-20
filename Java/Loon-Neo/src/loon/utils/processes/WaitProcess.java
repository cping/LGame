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

import loon.LRelease;
import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.TimeUtils;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class WaitProcess implements GameProcess, LRelease {

	protected boolean isDead = false, isAutoKill = true;

	protected final String id;

	private int _priority;

	private LTimer _timer;

	private GameProcessType _processType = GameProcessType.Other;

	private RealtimeProcessHost _processHost;

	private SortedList<GameProcess> _processesToFireWhenFinished;

	private Updateable _update;

	private BooleanValue _value = new BooleanValue(false);

	private RealtimeProcess _waitProcess;

	private final static String getProcessName() {
		return "Process" + TimeUtils.millis();
	}

	public WaitProcess(Updateable _update) {
		this(getProcessName(), 60, _update);
	}

	public WaitProcess(long delay, Updateable _update) {
		this(getProcessName(), delay, _update);
	}

	public WaitProcess(String id, long delay, Updateable _update) {
		this(id, delay, GameProcessType.Other, _update);
	}

	public WaitProcess(String id, long delay, GameProcessType pt, Updateable _update) {
		this._timer = new LTimer(delay);
		this.isDead = false;
		this.isAutoKill = true;
		this.id = id;
		this._processType = pt;
		this._update = _update;
	}

	public boolean completed() {
		return _value.result();
	}

	public BooleanValue get() {
		return _value;
	}

	@Override
	public void setProcessHost(RealtimeProcessHost _processHost) {
		this._processHost = _processHost;
	}

	@Override
	public void fireThisWhenFinished(GameProcess realtimeProcess) {
		if (this._processesToFireWhenFinished == null) {
			this._processesToFireWhenFinished = new SortedList<GameProcess>();
		}
		this._processesToFireWhenFinished.add(realtimeProcess);
	}

	public WaitProcess wait(RealtimeProcess process) {
		this._waitProcess = process;
		return this;
	}

	@Override
	public void tick(LTimerContext time) {
		if (_timer.action(time)) {
			if (_update != null) {
				if (!(_waitProcess != null && !_waitProcess.isDead)) {
					_update.action(this);
					if (isAutoKill) {
						kill();
					}
				}
			}
		}
	}

	@Override
	public void kill() {
		this.isDead = true;
	}

	@Override
	public boolean isDead() {
		return this.isDead;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public boolean isAutoKill() {
		return isAutoKill;
	}

	public void setAutoKill(boolean isAutoKill) {
		this.isAutoKill = isAutoKill;
	}

	@Override
	public GameProcessType getProcessType() {
		return this._processType;
	}

	@Override
	public void setProcessType(GameProcessType pt) {
		this._processType = pt;
	}

	public WaitProcess setPriority(int p) {
		this._priority = p;
		return this;
	}

	@Override
	public int getPriority() {
		return _priority;
	}
	
	@Override
	public void finish() {
		if (!this.isDead) {
			kill();
		}
		if (this._processesToFireWhenFinished != null) {
			for (LIterator<GameProcess> it = this._processesToFireWhenFinished.listIterator(); it.hasNext();) {
				RealtimeProcessManager.get().addProcess(it.next());
			}
		}
		if (this._processHost != null) {
			this._processHost.processFinished(this.id, this);
		}
		_value.set(true);
	}

	@Override
	public void close() {
		finish();
	}
}