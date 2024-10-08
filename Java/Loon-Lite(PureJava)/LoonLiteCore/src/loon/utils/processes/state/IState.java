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
package loon.utils.processes.state;

import loon.events.EventAction;
import loon.utils.timer.LTimer;

public interface IState {

	IState changeState(String stateName);

	IState pushState(String stateName);

	IState popState();

	LTimer getTimer();
	
	IState setDelay(float s);

	float getDelay();

	void update(long elapsedTime);

	void setParent(IState state);

	StateType getStateType();

	IState getParent();

	void onEnter();

	void onExit();

	void callEvent(String name);

	void callEvent(String name, EventAction eventAction);
	
	boolean isClosed();
	
	void close();

	String getName();
}
