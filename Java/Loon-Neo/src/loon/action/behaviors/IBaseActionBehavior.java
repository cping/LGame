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

import loon.utils.processes.WaitProcess;

public interface IBaseActionBehavior extends IBaseAction {

	<T extends ISystem> ISystem unRegisterSystem(String name);

	<T extends IModel> IModel unRegisterModel(String name);

	<T extends IUtility> IUtility unRegisterUtility(String name);

	<T extends ISystem> ISystem unRegisterSystem(Class<T> systemClass);

	<T extends IModel> IModel unRegisterModel(Class<T> modelClass);

	<T extends IUtility> IUtility unRegisterUtility(Class<T> utilityClass);

	<T extends ISystem> void registerSystem(String name, T system);

	<T extends IModel> void registerModel(String name, T model);

	<T extends IUtility> void registerUtility(String name, T utility);

	<T extends ISystem> void registerSystem(T system);

	<T extends IModel> void registerModel(T model);

	<T extends IUtility> void registerUtility(T utility);

	<T extends ISystem> T getSystem(String name);

	<T extends IModel> T getModel(String name);

	<T extends IUtility> T getUtility(String name);

	<T extends ISystem> T getSystem(Class<T> systemClass);

	<T extends IModel> T getModel(Class<T> modelClass);

	<T extends IUtility> T getUtility(Class<T> utilityClass);

	<T extends AbstractCommand> WaitProcess sendCommand(T command, float second);

	<T extends AbstractCommand> WaitProcess sendCommand(T command);

	void clear();
}
