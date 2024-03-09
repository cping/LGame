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
package loon;

import java.util.Comparator;

import loon.utils.ObjectMap;
import loon.utils.TArray;

/**
 * 系统任务的抽象实现管理器,也就是常见的的ECS模式的S
 */
public class ScreenSystemManager implements LRelease {

	public static interface ScreenSystemListener {

		void systemAdded(ScreenSystem ss);

		void systemRemoved(ScreenSystem ss);

	}

	private static class SystemComparator implements Comparator<ScreenSystem> {

		private SystemComparator() {
		}

		@Override
		public int compare(ScreenSystem a, ScreenSystem b) {
			if (a == null) {
				return -1;
			}
			if (b == null) {
				return -1;
			}
			return (a.getPriority() > b.getPriority()) ? 1 : ((a.getPriority() == b.getPriority()) ? 0 : -1);
		}
	}

	private final static SystemComparator sysComp = new SystemComparator();

	private final ObjectMap<Class<?>, ScreenSystem> _systemClazzs = new ObjectMap<Class<?>, ScreenSystem>();

	private final TArray<ScreenSystem> _systems = new TArray<ScreenSystem>(true, 16);

	private ScreenSystemListener _listener;

	public ScreenSystemManager() {
		this(null);
	}

	public ScreenSystemManager(ScreenSystemListener l) {
		this._listener = l;
	}

	public void update(long elapsedTime) {
		final int size = _systems.size;
		for (int i = 0; i < size; i++) {
			ScreenSystem sys = _systems.get(i);
			if (sys != null && sys.isRunning()) {
				sys.action(elapsedTime);
			}
		}
	}

	public ScreenSystemManager addSystemListener(ScreenSystemListener l) {
		this._listener = l;
		return this;
	}

	public ScreenSystemListener getSystemListener() {
		return this._listener;
	}

	public ScreenSystemManager addSystem(ScreenSystem system) {
		if (system == null) {
			return this;
		}
		Class<? extends ScreenSystem> systemClazz = (Class<? extends ScreenSystem>) system.getClass();
		ScreenSystem oldSytem = getSystem(systemClazz);
		if (oldSytem != null) {
			removeSystem(oldSytem);
		}
		this._systems.add(system);
		this._systemClazzs.put(systemClazz, system);
		this._systems.sort(sysComp);
		if (_listener != null) {
			this._listener.systemAdded(system);
		}
		if (system != null) {
			system.setSystemManager(this);
			system.init();
		}
		return this;
	}

	public ScreenSystemManager removeSystem(ScreenSystem system) {
		return removeSystem(system, true);
	}

	public ScreenSystemManager removeSystem(ScreenSystem system, boolean closed) {
		if (system == null) {
			return this;
		}
		if (this._systems.removeValue(system, true)) {
			this._systemClazzs.remove(system.getClass());
			if (this._listener != null) {
				this._listener.systemRemoved(system);
			}
			if (system != null && closed) {
				system.close();
				system.setSystemManager(null);
			}
		}
		return this;
	}

	public ScreenSystemManager clear() {
		return clear(true);
	}

	public ScreenSystemManager clear(boolean closed) {
		for (int i = _systems.size - 1; i > -1; i--) {
			ScreenSystem sys = this._systems.get(i);
			if (sys != null) {
				removeSystem(sys, closed);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ScreenSystem> T getSystem(Class<T> systemType) {
		return (T) this._systemClazzs.get(systemType);
	}

	public TArray<ScreenSystem> getSystems() {
		return new TArray<ScreenSystem>(_systems);
	}

	@Override
	public void close() {
		clear();
	}
}
