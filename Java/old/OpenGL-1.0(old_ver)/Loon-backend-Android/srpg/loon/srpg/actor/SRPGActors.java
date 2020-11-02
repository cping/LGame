package loon.srpg.actor;

import loon.action.sprite.AnimationHelper;
import loon.core.LRelease;
import loon.core.graphics.opengl.GLEx;
import loon.srpg.SRPGType;
import loon.srpg.ability.SRPGAbilityFactory;


/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1.1
 */
public class SRPGActors implements LRelease {

	private SRPGActor[] actors;

	private int tileWidth, tileHeight;

	private int cursorIndex;

	public SRPGActors(int size, int tileWidth, int tileHeight) {
		this.cursorIndex = -1;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.actors = new SRPGActor[size];
		for (int i = 0; i < size; i++) {
			actors[i] = new SRPGActor();
		}
	}

	public final void makeDefActors() {
		// LGame默认的技能设置(包含常见的传送魔法等)
		SRPGAbilityFactory.makeDefAbilitys();
		// LGame默认的角色设置(包含骑士等十个职业)
		SRPGActorFactory.makeDefActorStatus();
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param actor
	 * @return
	 */
	public SRPGActor putActor(int index, SRPGActor actor) {
		return this.actors[index] = actor;
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param name
	 * @param fileName
	 * @param statusIndex
	 * @param level
	 * @param team
	 * @return
	 */
	public SRPGActor putActor(int index, String name, String fileName,
			int statusIndex, int level, int team) {
		return this.actors[index] = new SRPGActor(name, fileName, statusIndex,
				level, team, tileWidth, tileHeight);
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param name
	 * @param animation
	 * @param statusIndex
	 * @param level
	 * @param team
	 * @return
	 */
	public SRPGActor putActor(int index, String name, AnimationHelper animation,
			int statusIndex, int level, int team) {
		return this.actors[index] = new SRPGActor(name, statusIndex, level,
				team, animation, tileWidth, tileHeight);
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param name
	 * @param fileName
	 * @param statusIndex
	 * @param level
	 * @param team
	 * @param x
	 * @param y
	 * @return
	 */
	public SRPGActor putActor(int index, String name, String fileName,
			int statusIndex, int level, int team, int x, int y) {
		SRPGActor actor = putActor(index, name, fileName, statusIndex, level,
				team);
		actor.setPos(x, y);
		return actor;
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param name
	 * @param animation
	 * @param statusIndex
	 * @param level
	 * @param team
	 * @param x
	 * @param y
	 * @return
	 */
	public SRPGActor putActor(int index, String name, AnimationHelper animation,
			int statusIndex, int level, int team, int x, int y) {
		SRPGActor actor = putActor(index, name, animation, statusIndex, level,
				team);
		actor.setPos(x, y);
		return actor;
	}

	/**
	 * 放置一个SRPG角色到指定的索引位置
	 * 
	 * @param index
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public SRPGActor putActor(int index, SRPGActor actor, int x, int y) {
		SRPGActor o = putActor(index, actor);
		o.setPos(x, y);
		return o;
	}

	/**
	 * 放置一个SRPG角色到游戏中，并返回自动获得的索引位置
	 * 
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public int putActor(SRPGActor actor, int x, int y) {
		int no = getNextNumber();
		if (no == -1) {
			return -1;
		} else {
			putActor(no, actor, x, y);
			return no;
		}
	}

	/**
	 * 返回当前选中的角色
	 * 
	 * @return
	 */
	public SRPGActor get() {
		if (cursorIndex == -1) {
			return null;
		}
		return actors[cursorIndex];
	}

	/**
	 * 查询指定索引所对应的游戏角色
	 * 
	 * @param index
	 * @return
	 */
	public SRPGActor find(int index) {
		if (index >= 0 && index < actors.length) {
			return actors[index];
		} else {
			return null;
		}
	}

	/**
	 * 查询指定角色对应的索引ID
	 * 
	 * @param actor
	 * @return
	 */
	public int find(SRPGActor actor) {
		for (int i = 0; i < actors.length; i++)
			if (actors[i] == actor) {
				return i;
			}
		return -1;
	}

	/**
	 * 检查指定坐标位置是否有游戏角色存在
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int checkActor(int x, int y) {
		for (int i = 0; i < actors.length; i++) {
			if (actors[i].getPosX() == x && actors[i].getPosY() == y
					&& actors[i].isVisible()) {
				return cursorIndex = i;
			}
		}
		return cursorIndex = -1;
	}

	/**
	 * 获得当前SRPG角色集合中的角色总数
	 * 
	 * @return
	 */
	public int size() {
		return actors == null ? 0 : actors.length;
	}

	/**
	 * 获得一个可用的角色索引ID
	 * 
	 * @return
	 */
	public int getNextNumber() {
		for (int i = 0; i < actors.length; i++) {
			if (!actors[i].isExist()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获得一个处于指定group标识分组中的角色索引
	 * 
	 * @param group
	 * @return
	 */
	public int getGroupValue(int group) {
		return getGroupValue(group, true);
	}

	/**
	 * 获得一个处于指定group标识分组中的角色索引
	 * 
	 * @param group
	 * @param flag
	 * @return
	 */
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

	/**
	 * 获得一个处于指定team标识分组中的角色索引
	 * 
	 * @param team
	 * @return
	 */
	public int getTeamValue(int team) {
		return getTeamValue(team, true);
	}

	/**
	 * 获得一个处于指定team标识分组中的角色索引
	 * 
	 * @param team
	 * @param flag
	 * @return
	 */
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

	/**
	 * 获得所有处于指定group标识分组中的角色索引
	 * 
	 * @param group
	 * @return
	 */
	public int[] getGroupArray(int group) {
		return getGroupArray(group, true);
	}

	/**
	 * 获得所有处于指定group标识分组中的角色索引
	 * 
	 * @param group
	 * @param flag
	 * @return
	 */
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

	/**
	 * 获得所有处于指定team标识分组中的角色索引
	 * 
	 * @param team
	 * @return
	 */
	public int[] getTeamArray(int team) {
		return getTeamArray(team, true);
	}

	/**
	 * 获得所有处于指定team标识分组中的角色索引
	 * 
	 * @param team
	 * @param flag
	 * @return
	 */
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

	/**
	 * 预测指定角色向指定X轴移动后的X轴位置
	 * 
	 * @param role
	 * @param d
	 * @return
	 */
	public int nextPosX(int role, int d) {
		int nx = actors[role].getPosX();
		switch (d) {
		case SRPGType.MOVE_RIGHT:
			nx++;
			break;
		case SRPGType.MOVE_LEFT:
			nx--;
			break;
		}
		return nx;
	}

	/**
	 * 预测指定角色向指定Y轴移动后的Y轴位置
	 * 
	 * @param role
	 * @param d
	 * @return
	 */
	public int nextPosY(int role, int d) {
		int ny = actors[role].getPosY();
		switch (d) {
		case SRPGType.MOVE_UP:
			ny--;
			break;
		case SRPGType.MOVE_DOWN:
			ny++;
			break;
		}
		return ny;
	}

	/**
	 * 遍历所有SRPG精灵的下一步动作
	 * 
	 */
	public void next() {
		for (int i = 0; i < actors.length; i++) {
			actors[i].next();
		}
	}

	/**
	 * 绘制所有战场精灵的图像
	 * 
	 * @param g
	 * @param x
	 * @param y
	 */
	public void draw(GLEx g, int x, int y) {
		for (int i = 0; i < actors.length; i++) {
			if (actors[i].isVisible()) {
				actors[i].draw(g, x, y);
			}
		}
	}

	public int getCursorIndex() {
		return cursorIndex;
	}

	public void reset() {
		dispose();
		reset(size());
	}

	public void reset(int size) {
		this.actors = new SRPGActor[size];
		for (int i = 0; i < size; i++) {
			actors[i] = new SRPGActor();
		}
	}

	public SRPGActor[] getActors() {
		return actors;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	@Override
	public void dispose() {
		for (int i = 0; i < actors.length; i++) {
			actors[i] = null;
		}
	}

}
