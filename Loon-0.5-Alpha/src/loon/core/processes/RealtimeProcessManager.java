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

import loon.core.LRelease;

public class RealtimeProcessManager implements RealtimeProcessEvent, LRelease {

	private static RealtimeProcessManager instance;

	private LinkedList<Process> processes;

	public static RealtimeProcessManager get() {
		synchronized (RealtimeProcessManager.class) {
			if (instance == null) {
				instance = new RealtimeProcessManager();
			}
			return instance;
		}
	}

	private RealtimeProcessManager() {
		this.processes = new LinkedList<Process>();
	}

	public void addProcess(RealtimeProcess realtimeProcess) {
		synchronized (this.processes) {
			this.processes.add(realtimeProcess);
		}
	}

	public void tick(long time) {
		LinkedList<Process> toBeUpdated;
		synchronized (this.processes) {
			toBeUpdated = new LinkedList<Process>(this.processes);
		}
		LinkedList<Process> deadProcesses = new LinkedList<Process>();
		for (Process realtimeProcess : toBeUpdated) {
			realtimeProcess.tick(time);
			if (realtimeProcess.isDead()) {
				deadProcesses.add(realtimeProcess);
			}
		}
		for (Process realtimeProcess : deadProcesses) {
			realtimeProcess.finish();
		}
		synchronized (this.processes) {
			this.processes.removeAll(deadProcesses);
		}
	}

	public void delete(String id) {
		if (processes != null && processes.size() > 0) {
			synchronized (this.processes) {
				for (Process p : processes) {
					if (p.getId() == id || p.getId().equals(id)) {
						p.kill();
					}
				}
			}
		}
	}

	public void deleteIndex(String id) {
		if (processes != null && processes.size() > 0) {
			synchronized (this.processes) {
				for (Process p : processes) {
					if (p.getId() == id || p.getId().indexOf(id) != -1) {
						p.kill();
					}
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (processes != null && processes.size() > 0) {
			synchronized (this.processes) {
				for (Process p : processes) {
					p.finish();
				}
				processes.clear();
			}
		}
	}
}