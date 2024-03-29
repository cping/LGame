/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package loon.action.map.battle;

import loon.events.Updateable;
import loon.utils.processes.WaitProcess;

public interface BattleEvent {

	public boolean isLocked();

	public BattleEvent lock(boolean lock);

	public WaitProcess wait(Updateable update);

	public WaitProcess wait(Updateable update, float s);

	public WaitProcess wait(WaitProcess waitProcess);

	public BattleProcess getMainProcess();

	public BattleEvent setMainProcess(BattleProcess p);

	public boolean start(long elapsedTime);

	public boolean process(long elapsedTime);

	public boolean end(long elapsedTime);

	public BattleState getState();

	public boolean completed();

	public BattleEvent reset();

}
