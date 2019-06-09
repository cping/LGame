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
import loon.event.Updateable;
import loon.geom.BooleanValue;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.TimeUtils;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class WaitProcess implements GameProcess, LRelease {

	protected boolean isDead = false, isAutoKill = true;

	protected final String id;

	private LTimer timer;

	private GameProcessType processType = GameProcessType.Other;

	private RealtimeProcessHost processHost;

	private SortedList<GameProcess> processesToFireWhenFinished;

	private Updateable update;

	private BooleanValue value = new BooleanValue(false);

	private RealtimeProcess _waitProcess;

	public WaitProcess(Updateable update) {
		this("Process" + TimeUtils.millis(), 60, update);
	}

	public WaitProcess(long delay, Updateable update) {
		this("Process" + TimeUtils.millis(), delay, update);
	}

	public WaitProcess(String id, long delay, Updateable update) {
		this(id, delay, GameProcessType.Other, update);
	}

	public WaitProcess(String id, long delay, GameProcessType pt, Updateable update) {
		this.timer = new LTimer(delay);
		this.isDead = false;
		this.isAutoKill = true;
		this.id = id;
		this.processType = pt;
		this.update = update;
	}

	public boolean completed() {
		return value.result();
	}

	public BooleanValue get() {
		return value;
	}

	@Override
	public void setProcessHost(RealtimeProcessHost processHost) {
		this.processHost = processHost;
	}

	public void fireThisWhenFinished(GameProcess realtimeProcess) {
		if (this.processesToFireWhenFinished == null) {
			this.processesToFireWhenFinished = new SortedList<GameProcess>();
		}
		this.processesToFireWhenFinished.add(realtimeProcess);
	}

	public WaitProcess wait(RealtimeProcess process) {
		this._waitProcess = process;
		return this;
	}

	@Override
	public void tick(LTimerContext time) {
		if (timer.action(time)) {
			if (update != null) {
				if (!(_waitProcess != null && !_waitProcess.isDead)) {
					update.action(this);
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
		return this.processType;
	}

	@Override
	public void setProcessType(GameProcessType pt) {
		this.processType = pt;
	}

	@Override
	public void finish() {
		if (!this.isDead) {
			kill();
		}
		if (this.processesToFireWhenFinished != null) {
			for (LIterator<GameProcess> it = this.processesToFireWhenFinished.listIterator(); it.hasNext();) {
				RealtimeProcessManager.get().addProcess(it.next());
			}
		}
		if (this.processHost != null) {
			this.processHost.processFinished(this.id, this);
		}
		value.set(true);
	}

	@Override
	public void close() {
		finish();
	}

}