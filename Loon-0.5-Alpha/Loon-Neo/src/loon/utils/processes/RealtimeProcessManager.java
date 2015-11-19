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
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

public class RealtimeProcessManager implements RealtimeProcessEvent, LRelease {

	private static RealtimeProcessManager instance;

	private SortedList<GameProcess> processes;

	public static RealtimeProcessManager get() {
		synchronized (RealtimeProcessManager.class) {
			if (instance == null) {
				instance = new RealtimeProcessManager();
			}
			return instance;
		}
	}

	private RealtimeProcessManager() {
		this.processes = new SortedList<GameProcess>();
	}

	public static RealtimeProcessManager newProcess() {
		return new RealtimeProcessManager();
	}

	public void addProcess(RealtimeProcess realtimeProcess) {
		synchronized (this.processes) {
			this.processes.add(realtimeProcess);
		}
	}

	public void tick(LTimerContext time) {
		if (processes.size > 0) {
			final SortedList<GameProcess> toBeUpdated;
			synchronized (this.processes) {
				toBeUpdated = new SortedList<GameProcess>(this.processes);
			}
			final SortedList<GameProcess> deadProcesses = new SortedList<GameProcess>();
			for (LIterator<GameProcess> it = toBeUpdated.listIterator(); it
					.hasNext();) {
				GameProcess realtimeProcess = it.next();
				realtimeProcess.tick(time);
				if (realtimeProcess.isDead()) {
					deadProcesses.add(realtimeProcess);
				}
			}
			for (LIterator<GameProcess> it = deadProcesses.listIterator(); it
					.hasNext();) {
				GameProcess realtimeProcess = it.next();
				realtimeProcess.finish();
			}
			synchronized (this.processes) {
				this.processes.removeAll(deadProcesses);
			}
		}
	}

	public GameProcess find(String id) {
		if (processes != null && processes.size > 0) {
			synchronized (this.processes) {
				for (LIterator<GameProcess> it = processes.listIterator(); it
						.hasNext();) {
					GameProcess p = it.next();
					if (p.getId() == id || p.getId().equals(id)) {
						return p;
					}
				}
			}
		}
		return null;
	}

	public void delete(String id) {
		if (processes != null && processes.size > 0) {
			synchronized (this.processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(
						processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p.getId() == id || p.getId().equals(id)) {
						p.kill();
						processes.remove(p);
					}
				}
			}
		}
	}

	public void deleteIndex(String id) {
		if (processes != null && processes.size > 0) {
			synchronized (this.processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(
						processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p.getId() == id || p.getId().indexOf(id) != -1) {
						p.kill();
						processes.remove(p);
					}
				}
			}
		}
	}

	@Override
	public void close() {
		if (processes != null && processes.size > 0) {
			synchronized (this.processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(
						processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					p.finish();
				}
				processes.clear();
			}
		}
	}

}