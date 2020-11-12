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
package loon.action.map.battle;

import loon.events.ActionUpdate;

public class BattleActive implements ActionUpdate {

	private boolean pause = false;
	private int current = 0;
	private int limitTime = 0;

	public BattleActive(int time) {
		this.limitTime = time;
	}

	public void pause(boolean pause) {
		this.pause = pause;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int value) {
		current = value > limitTime ? limitTime : value < 0 ? 0 : value;
	}
	
	public void reset(){
		current = 0;
	}

	@Override
	public void action(Object a) {
		if (!pause) {
			if (current < limitTime) {
				this.current++;
			}
		}
	}

	@Override
	public boolean completed() {
		return current >= limitTime;
	}
}
