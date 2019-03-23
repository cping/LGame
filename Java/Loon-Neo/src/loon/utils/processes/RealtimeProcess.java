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
import loon.utils.TimeUtils;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public abstract class RealtimeProcess implements GameProcess, LRelease {

	protected boolean isDead;

	protected final String id;

	private LTimer timer = new LTimer(LSystem.SECOND);

	private RealtimeProcessHost processHost;

	private SortedList<GameProcess> processesToFireWhenFinished;

	public RealtimeProcess() {
		this("Process" + TimeUtils.millis());
	}

	public RealtimeProcess(String id) {
		this(id, LSystem.SECOND);
	}

	public RealtimeProcess(long delay) {
		this("Process" + TimeUtils.millis(), delay);
	}
	
	public RealtimeProcess(String id, long delay) {
		this.isDead = false;
		this.id = id;
		this.timer.setDelay(delay);
	}

	@Override
	public void setProcessHost(RealtimeProcessHost processHost) {
		this.processHost = processHost;
	}

	@Override
	public void fireThisWhenFinished(GameProcess realtimeProcess) {
		if (this.processesToFireWhenFinished == null) {
			this.processesToFireWhenFinished = new SortedList<GameProcess>();
		}
		this.processesToFireWhenFinished.add(realtimeProcess);
	}

	@Override
	public void tick(LTimerContext time) {
		if (timer.action(time)) {
			run(time);
		}
	}

	public RealtimeProcess sleep(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public RealtimeProcess setDelay(long delay) {
		timer.setDelay(delay);
		return this;
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public long getCurrentTick() {
		return timer.getCurrentTick();
	}

	public RealtimeProcess interrupt() {
		timer.stop();
		return this;
	}

	public RealtimeProcess stop() {
		timer.stop();
		return this;
	}

	public RealtimeProcess start() {
		timer.start();
		return this;
	}

	public boolean isActive() {
		return timer.isActive();
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
		if (this.processesToFireWhenFinished != null) {
			for (LIterator<GameProcess> it = this.processesToFireWhenFinished
					.listIterator(); it.hasNext();) {
				RealtimeProcessManager.get().addProcess(it.next());
			}
		}
		if (this.processHost != null) {
			this.processHost.processFinished(this.id, this);
		}
	}

	@Override
	public void close() {
		finish();
	}
}