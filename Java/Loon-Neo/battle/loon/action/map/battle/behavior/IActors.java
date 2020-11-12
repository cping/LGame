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
package loon.action.map.battle.behavior;

public abstract class IActors {

	private IActor[] actors;

	private int cursorIndex;

	public IActors(int size) {
		this.cursorIndex = -1;
		this.actors = new IActor[size];
	}

	public IActor addActor(int index, IActor actor) {
		return this.actors[index] = actor;
	}

	public IActor getActor() {
		if (cursorIndex == -1) {
			return null;
		}
		return actors[cursorIndex];
	}

	public IActor find(int index) {
		if (index >= 0 && index < actors.length) {
			return actors[index];
		} else {
			return null;
		}
	}

	public int find(IActor actor) {
		for (int i = 0; i < actors.length; i++){
			if (actors[i] == actor) {
				return i;
			}
		}
		return -1;
	}

	public int checkActor(int x, int y) {
		for (int i = 0; i < actors.length; i++) {
			if (actors[i].getPosX() == x && actors[i].getPosY() == y && actors[i].isVisible()) {
				return cursorIndex = i;
			}
		}
		return cursorIndex = -1;
	}

	public int size() {
		return actors == null ? 0 : actors.length;
	}

	public int getNextNumber() {
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isExist()) {
				return i;
			}
		}
		return -1;
	}

	public int getGroupValue(int group) {
		return getGroupValue(group, true);
	}

	public int getGroupValue(int group, boolean flag) {
		int index = 0;
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isVisible()) {
				continue;
			}
			boolean ret = actors[i].getActorStatus().group == group;
			if (!flag) {
				ret = !ret;
			}
			if (ret) {
				index++;
			}
		}

		return index;
	}

	public int getTeamValue(int team) {
		return getTeamValue(team, true);
	}

	public int getTeamValue(int team, boolean flag) {
		int index = 0;
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isVisible()) {
				continue;
			}
			boolean ret = actors[i].getActorStatus().team == team;
			if (!flag) {
				ret = !ret;
			}
			if (ret) {
				index++;
			}
		}
		return index;
	}

	public int[] getGroupArray(int group) {
		return getGroupArray(group, true);
	}

	public int[] getGroupArray(int group, boolean flag) {
		int[] res = new int[getGroupValue(group, flag)];
		int index = 0;
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isVisible()) {
				continue;
			}
			boolean ret = actors[i].getActorStatus().group == group;
			if (!flag) {
				ret = !ret;
			}
			if (ret) {
				res[index] = i;
				index++;
			}
		}
		return res;
	}

	public int[] getTeamArray(int team) {
		return getTeamArray(team, true);
	}

	public int[] getTeamArray(int team, boolean flag) {
		int[] res = new int[getTeamValue(team, flag)];
		int index = 0;
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isVisible()) {
				continue;
			}
			boolean ret = actors[i].getActorStatus().team == team;
			if (!flag) {
				ret = !ret;
			}
			if (ret) {
				res[index] = i;
				index++;
			}
		}
		return res;
	}

	public void nextAction() {
		for (int i = 0; i < actors.length; i++) {
			actors[i].nextAction();
		}
	}

	public int getCursorIndex() {
		return cursorIndex;
	}

	public IActor[] getActors() {
		return actors;
	}

}
