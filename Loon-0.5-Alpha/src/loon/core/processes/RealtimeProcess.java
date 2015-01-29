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
package loon.core.processes;

import java.util.LinkedList;

import loon.LSystem;
import loon.core.timer.LTimer;

public abstract class RealtimeProcess implements Process {

	protected boolean isDead;

	protected final String id;

	private LTimer timer = new LTimer(LSystem.SECOND);

	private RealtimeProcessHost processHost;

	private LinkedList<RealtimeProcess> processesToFireWhenFinished;

	public RealtimeProcess() {
		this("Process" + System.currentTimeMillis());
	}

	public RealtimeProcess(String id) {
		this.isDead = false;
		this.id = id;
	}

	public void setProcessHost(RealtimeProcessHost processHost) {
		this.processHost = processHost;
	}

	public void fireThisWhenFinished(RealtimeProcess realtimeProcess) {
		if (this.processesToFireWhenFinished == null) {
			this.processesToFireWhenFinished = new LinkedList<RealtimeProcess>();
		}
		this.processesToFireWhenFinished.add(realtimeProcess);
	}

	public void tick(long nanoTime) {
		if (timer.action(nanoTime)) {
			run();
		}
	}

	public void sleep(long delay) {
		timer.setDelay(delay);
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public long getCurrentTick() {
		return timer.getCurrentTick();
	}

	public void interrupt() {
		timer.stop();
	}

	public void stop() {
		timer.stop();
	}

	public void start() {
		timer.start();
	}

	public boolean isActive() {
		return timer.isActive();
	}

	public abstract void run();

	public void kill() {
		this.isDead = true;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public String getId() {
		return this.id;
	}

	public void finish() {
		if (!this.isDead) {
			kill();
		}
		if (this.processesToFireWhenFinished != null) {
			for (RealtimeProcess realtimeProcess : this.processesToFireWhenFinished) {
				RealtimeProcessManager.get().addProcess(realtimeProcess);
			}
		}
		if (this.processHost != null) {
			this.processHost.processFinished(this.id, this);
		}
	}
}