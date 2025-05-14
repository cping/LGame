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

import java.util.Comparator;
import java.util.Iterator;

import loon.LRelease;
import loon.LSystem;
import loon.events.EventAction;
import loon.utils.HelperUtils;
import loon.utils.IArray;
import loon.utils.LIterator;
import loon.utils.SortedList;
import loon.utils.StrBuilder;
import loon.utils.TArray;
import loon.utils.timer.LTimerContext;

/**
 * loon围绕时间帧提供的进程管理用类(无关单独线程,纯依附于主渲染线程中循环实现)
 */
public class RealtimeProcessManager implements RealtimeProcessEvent, IArray, LRelease {

	private static class RealtimeProcessRunnable extends RealtimeProcess {

		private Runnable _runnable = null;

		public RealtimeProcessRunnable(final String name, final long delay, final Runnable r) {
			super(name, delay);
			this._runnable = r;
			this.setProcessType(GameProcessType.Other);
		}

		@Override
		public void run(LTimerContext time) {
			if (_runnable != null) {
				_runnable.run();
			}
		}

	}

	private static class RealtimeProcessEventAction extends RealtimeProcess {

		private EventAction _runnable = null;

		public RealtimeProcessEventAction(final String name, final long delay, final EventAction r) {
			super(name, delay);
			this._runnable = r;
			this.setProcessType(GameProcessType.Other);
		}

		@Override
		public void run(LTimerContext time) {
			if (_runnable != null) {
				HelperUtils.callEventAction(_runnable, time);
			}
		}

	}

	public final static RealtimeProcess ofProcess(final String name, final long delay, final EventAction runnable) {
		return new RealtimeProcessEventAction(name, delay, runnable);
	}

	public final static RealtimeProcess ofProcess(final String name, final long delay, final Runnable runnable) {
		return new RealtimeProcessRunnable(name, delay, runnable);
	}

	static class ProcessComparator implements Comparator<GameProcess> {

		@Override
		public int compare(GameProcess o1, GameProcess o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return o2.getPriority() - o1.getPriority();
		}

	}

	private static final ProcessComparator _processComparator = new ProcessComparator();

	private SortedList<GameProcess> _processes;

	private final TArray<GameProcess> deadProcesses = new TArray<GameProcess>();

	private final TArray<GameProcess> toBeUpdated = new TArray<GameProcess>();

	private static RealtimeProcessManager _instance;

	private GameProcess _currentProcess;

	public static void freeStatic() {
		_instance = null;
	}

	public static final RealtimeProcessManager get() {
		synchronized (RealtimeProcessManager.class) {
			if (_instance == null) {
				_instance = new RealtimeProcessManager();
			}
			return _instance;
		}
	}

	public static boolean isScheduled() {
		return _instance != null;
	}

	private RealtimeProcessManager() {
		this._processes = new SortedList<GameProcess>();
	}

	public static RealtimeProcessManager newProcess() {
		return new RealtimeProcessManager();
	}

	@Override
	public void addProcess(GameProcess realtimeProcess) {
		synchronized (this._processes) {
			this._processes.add(realtimeProcess);
		}
	}

	@Override
	public boolean containsProcess(GameProcess realtimeProcess) {
		synchronized (this._processes) {
			return this._processes.contains(realtimeProcess);
		}
	}

	@Override
	public void tick(LTimerContext time) {
		if (_processes.size > 0) {
			synchronized (this._processes) {
				toBeUpdated.clear();
				toBeUpdated.addAll(this._processes);
			}
			deadProcesses.clear();
			try {
				for (Iterator<GameProcess> it = toBeUpdated.iterator(); it.hasNext();) {
					GameProcess realtimeProcess = it.next();
					if (realtimeProcess != null) {
						synchronized (realtimeProcess) {
							_currentProcess = realtimeProcess;
							realtimeProcess.tick(time);
							if (realtimeProcess.isDead()) {
								deadProcesses.add(realtimeProcess);
							}
						}
					}
				}
				if (deadProcesses.size > 0) {
					for (Iterator<GameProcess> it = deadProcesses.iterator(); it.hasNext();) {
						GameProcess realtimeProcess = it.next();
						if (realtimeProcess != null) {
							synchronized (realtimeProcess) {
								realtimeProcess.finish();
							}
						}
					}
					synchronized (this._processes) {
						this._processes.removeAll(deadProcesses);
					}
				}
			} catch (Throwable cause) {
				LSystem.error("Process dispatch failure", cause);
			}
		}
	}

	public GameProcess currentProcess() {
		return _currentProcess;
	}

	public TArray<GameProcess> find(String id) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null && (p.getId() == id || p.getId().equals(id))) {
						list.add(p);
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> find(GameProcessType pt) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null && p.getProcessType() == pt) {
						list.add(p);
					}
				}
			}
		}
		return list;
	}

	public int findCount(GameProcessType pt) {
		int count = 0;
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null && p.getProcessType() == pt) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public TArray<GameProcess> findIndex(String id) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p.getId().equals(id) || p.getId().indexOf(id) != -1) {
						list.add(p);
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> delete(GameProcessType pt) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (pt == null) {
			return list;
		}
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getProcessType() == pt) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> delete(GameProcess process) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (process == null) {
			return list;
		}
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (process == p || process.getId() == p.getId() || process.getId().equals(p.getId())) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> delete(String id) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getId() == id || p.getId().equals(id)) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> deleteType(GameProcessType processType) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getProcessType() == processType) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> deleteIndex(String id) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getId().equals(id) || p.getId().indexOf(id) != -1) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 删除所有优先级小于指定数值的进程
	 * 
	 * @param minPriority
	 * @return
	 */
	public TArray<GameProcess> deleteAllWithMinPriority(int minPriority) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getPriority() < minPriority) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 删除所有优先级大于指定数值的进程
	 * 
	 * @param maxPriority
	 * @return
	 */
	public TArray<GameProcess> deleteAllWithMaxPriority(int maxPriority) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						if (p.getPriority() > maxPriority) {
							p.kill();
							_processes.remove(p);
							list.add(p);
						}
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> pause(GameProcessType pt) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null && p.getProcessType() == pt) {
						list.add(p.pause());
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> resume(GameProcessType pt) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null && p.getProcessType() == pt) {
						list.add(p.resume());
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> pause() {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null) {
						list.add(p.pause());
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> resume() {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					if (p != null) {
						list.add(p.resume());
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> pauseWithMinPriority(int min, int max) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					int pindex = p.getPriority();
					if (p != null && (pindex >= min && pindex <= max)) {
						list.add(p.pause());
					}
				}
			}
		}
		return list;
	}

	public TArray<GameProcess> resumeWithMinPriority(int min, int max) {
		TArray<GameProcess> list = new TArray<GameProcess>();
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
					GameProcess p = it.next();
					int pindex = p.getPriority();
					if (p != null && (pindex >= min && pindex <= max)) {
						list.add(p.resume());
					}
				}
			}
		}
		return list;
	}

	public RealtimeProcessManager sort() {
		synchronized (this._processes) {
			_processes.sort(_processComparator);
		}
		return this;
	}

	public boolean hasEvents() {
		return !isEmpty();
	}

	@Override
	public int size() {
		return _processes.size;
	}

	@Override
	public void clear() {
		_processes.clear();
		_currentProcess = null;
	}

	@Override
	public boolean isEmpty() {
		return _processes.size == 0;
	}

	@Override
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public void dispose() {
		close();
	}

	@Override
	public String toString() {
		return toString(LSystem.COMMA);
	}

	public String toString(char separator) {
		if (_processes.size == 0) {
			return "[]";
		}
		StrBuilder buffer = new StrBuilder(32);
		buffer.append(LSystem.BRACKET_START);
		for (LIterator<GameProcess> it = _processes.listIterator(); it.hasNext();) {
			GameProcess p = it.next();
			if (p != null) {
				buffer.append(p.toString());
				buffer.append(separator);
			}
		}
		buffer.append(LSystem.BRACKET_END);
		return buffer.toString();
	}

	@Override
	public void close() {
		_currentProcess = null;
		if (_processes != null && _processes.size > 0) {
			synchronized (this._processes) {
				final TArray<GameProcess> ps = new TArray<GameProcess>(_processes);
				for (int i = 0; i < ps.size; i++) {
					GameProcess p = ps.get(i);
					if (p != null) {
						synchronized (p) {
							p.finish();
						}
					}
				}
				_processes.clear();
			}
		}
	}

}