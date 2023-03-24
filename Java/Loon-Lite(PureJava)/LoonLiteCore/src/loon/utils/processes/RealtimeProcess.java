/**
 * 
 * Copyright 2014
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
 * @version 0.4.1
 */
package loon.utils.processes;

import loon.LRelease;
import loon.LSystem;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public abstract class RealtimeProcess implements GameProcess, LRelease {

	private static int GLOBAL_ID = 0;

	protected boolean isDead;

	protected final String id;

	private int _priority;

	private final LTimer _timer = new LTimer(LSystem.SECOND);

	private GameProcessType _processType = GameProcessType.Other;

	private RealtimeProcessHost _processHost;

	private SortedList<GameProcess> _processesToFireWhenFinished;

	private final static String getProcessName() {
		return "Process" + (GLOBAL_ID++);
	}

	public RealtimeProcess() {
		this(getProcessName());
	}

	public RealtimeProcess(String id) {
		this(id, LSystem.SECOND);
	}

	public RealtimeProcess(long delay) {
		this(getProcessName(), delay);
	}

	public RealtimeProcess(String id, long delay) {
		this(id, delay, GameProcessType.Other);
	}

	public RealtimeProcess(String id, long delay, GameProcessType pt) {
		this.isDead = false;
		this.id = id;
		this._timer.setDelay(delay);
		this._processType = pt;
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

	@Override
	public void tick(LTimerContext time) {
		if (_timer.action(time)) {
			run(time);
		}
	}

	public RealtimeProcess sleep(long delay) {
		_timer.setDelay(delay);
		return this;
	}

	public RealtimeProcess setDelay(long delay) {
		_timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return _timer.getDelay();
	}

	public long getCurrentTick() {
		return _timer.getCurrentTick();
	}

	public RealtimeProcess pause() {
		_timer.pause();
		return this;
	}

	public RealtimeProcess unpause() {
		_timer.unpause();
		return this;
	}

	public RealtimeProcess interrupt() {
		_timer.stop();
		return this;
	}

	public RealtimeProcess stop() {
		_timer.stop();
		return this;
	}

	public RealtimeProcess start() {
		_timer.start();
		return this;
	}

	public RealtimeProcess setPriority(int p) {
		this._priority = p;
		return this;
	}

	@Override
	public int getPriority() {
		return this._priority;
	}

	public boolean isActive() {
		return _timer.isActive();
	}

	@Override
	public GameProcessType getProcessType() {
		return this._processType;
	}

	@Override
	public void setProcessType(GameProcessType pt) {
		this._processType = pt;
	}

	public abstract void run(LTimerContext time);

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
	}

	@Override
	public void close() {
		finish();
	}
}