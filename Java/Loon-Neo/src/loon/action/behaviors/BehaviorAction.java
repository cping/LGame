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
package loon.action.behaviors;

import loon.LSysException;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.processes.WaitProcess;
import loon.utils.timer.Duration;
import loon.utils.ObjectMap.Values;

public class BehaviorAction extends Behavior<Object> implements IBaseActionBehavior {

	private final ObjectMap<AbstractCommand, WaitProcess> _process = new ObjectMap<AbstractCommand, WaitProcess>();

	protected final ObjectMap<String, ISystem> _systems = new ObjectMap<String, ISystem>();

	protected final ObjectMap<String, IModel> _models = new ObjectMap<String, IModel>();

	protected final ObjectMap<String, IUtility> _utilitys = new ObjectMap<String, IUtility>();

	private BehaviorBuilder<?> _builder;

	public BehaviorAction() {
		this(null);
	}

	public BehaviorAction(BehaviorBuilder<?> b) {
		setBehaviorBuilder(b);
	}

	public BehaviorBuilder<?> getBehaviorBuilder() {
		return this._builder;
	}

	public BehaviorAction setBehaviorBuilder(BehaviorBuilder<?> b) {
		this._builder = b;
		return this;
	}

	protected void preConfigInit() {
		preConfig(0);
	}

	protected void preConfigLoop() {
		preConfig(1);
	}

	protected void preConfigClose() {
		preConfig(2);
	}

	private void preSelected(int flag, ISystem sys) {
		switch (flag) {
		case 0:
			init(sys);
			break;
		case 1:
			loop(sys);
			break;
		case 2:
			close(sys);
			break;
		}
	}

	private void preConfig(int flag) {
		for (Values<ISystem> iter = _systems.values(); iter.hasNext();) {
			preSelected(flag, iter.next());
		}
		for (Values<IModel> iter = _models.values(); iter.hasNext();) {
			preSelected(flag, iter.next());
		}
		for (Values<IUtility> iter = _utilitys.values(); iter.hasNext();) {
			preSelected(flag, iter.next());
		}
	}

	@Override
	public void onInit() {
		if (!isInited()) {
			preConfigInit();
			status = TaskStatus.Running;
		}
	}

	protected void init(ISystem system) {
		if (system != null) {
			system.init();
		}
	}

	protected void loop(ISystem system) {
		if (system != null) {
			system.loop();
		}
	}

	protected void close(ISystem system) {
		if (system != null) {
			system.close();
		}
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {

	}

	@Override
	public TaskStatus update(Object context) {
		if (isInited()) {
			preConfigLoop();
		}
		return status;
	}

	private final static String preName(String name) {
		return name.trim().toLowerCase();
	}

	@Override
	public <T extends ISystem> ISystem unRegisterSystem(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return _systems.remove(preName(name));
	}

	@Override
	public <T extends IModel> IModel unRegisterModel(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return _models.remove(preName(name));
	}

	@Override
	public <T extends IUtility> IUtility unRegisterUtility(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return _utilitys.remove(preName(name));
	}

	@Override
	public <T extends ISystem> ISystem unRegisterSystem(Class<T> systemClass) {
		if (systemClass == null) {
			throw new LSysException("Can't inject an empty systemClass !");
		}
		return unRegisterSystem(systemClass.getName());
	}

	@Override
	public <T extends IModel> IModel unRegisterModel(Class<T> modelClass) {
		if (modelClass == null) {
			throw new LSysException("Can't inject an empty modelClass !");
		}
		return unRegisterModel(modelClass.getName());
	}

	@Override
	public <T extends IUtility> IUtility unRegisterUtility(Class<T> utilityClass) {
		if (utilityClass == null) {
			throw new LSysException("Can't inject an empty utilityClass !");
		}
		return unRegisterUtility(utilityClass.getName());
	}

	@Override
	public <T extends ISystem> void registerSystem(String name, T system) {
		if (StringUtils.isEmpty(name)) {
			return;
		}
		_systems.put(preName(name), system);
	}

	@Override
	public <T extends IModel> void registerModel(String name, T model) {
		if (StringUtils.isEmpty(name)) {
			return;
		}
		_models.put(preName(name), model);
	}

	@Override
	public <T extends IUtility> void registerUtility(String name, T utility) {
		if (StringUtils.isEmpty(name)) {
			return;
		}
		_utilitys.put(preName(name), utility);
	}

	@Override
	public <T extends ISystem> void registerSystem(T system) {
		if (system == null) {
			return;
		}
		registerSystem(system.getClass().getName(), system);
	}

	@Override
	public <T extends IModel> void registerModel(T model) {
		if (model == null) {
			return;
		}
		registerModel(model.getClass().getName(), model);
	}

	@Override
	public <T extends IUtility> void registerUtility(T utility) {
		if (utility == null) {
			return;
		}
		registerUtility(utility.getClass().getName(), utility);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends ISystem> T getSystem(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return (T) _systems.get(preName(name));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IModel> T getModel(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return (T) _models.get(preName(name));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IUtility> T getUtility(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return (T) _utilitys.get(preName(name));
	}

	@Override
	public <T extends ISystem> T getSystem(Class<T> systemClass) {
		if (systemClass == null) {
			throw new LSysException("Can't inject an empty systemClass !");
		}
		return getSystem(systemClass.getName());
	}

	@Override
	public <T extends IModel> T getModel(Class<T> modelClass) {
		if (modelClass == null) {
			throw new LSysException("Can't inject an empty modelClass !");
		}
		return getModel(modelClass.getName());
	}

	@Override
	public <T extends IUtility> T getUtility(Class<T> utilityClass) {
		if (utilityClass == null) {
			throw new LSysException("Can't inject an empty utilityClass !");
		}
		return getUtility(utilityClass.getName());
	}

	@Override
	public <T extends AbstractCommand> WaitProcess sendCommand(T command) {
		return sendCommand(command, 0f);
	}

	@Override
	public <T extends AbstractCommand> WaitProcess sendCommand(T command, float second) {
		if (command == null) {
			throw new LSysException("Can't inject an empty command !");
		}
		command.setBehaviorAction(this);
		WaitProcess process = null;
		if (_process.containsKey(command)) {
			process = _process.get(command);
			if (process != null && !process.completed()) {
				return process;
			}
		}
		process = new WaitProcess(Duration.ofS(second), command);
		RealtimeProcessManager.get().addProcess(process);
		_process.put(command, process);
		return process;
	}

	@Override
	public IBaseActionBehavior getBaseAction() {
		return this;
	}

	@Override
	public void clear() {
		_systems.clear();
		_utilitys.clear();
		_models.clear();
		for (Values<WaitProcess> iter = _process.values(); iter.hasNext();) {
			WaitProcess w = iter.next();
			if (w != null) {
				w.close();
			}
		}
		_process.clear();
	}

	@Override
	public void close() {
		super.close();
		this.preConfigClose();
		this.clear();
	}

}
