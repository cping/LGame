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
 * @version 0.1
 */
package loon.srpg.field;

import loon.Screen;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.utils.CollectionUtils;


public class SRPGMoveStack {

	private int pos_x;

	private int pos_y;

	private int[] directions;

	private int[] speed;

	private boolean[] show;

	private boolean[] change;

	private int default_speed;

	private boolean default_show;

	private boolean default_change;

	public SRPGMoveStack(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
		this.setDefault();
		this.directions = new int[0];
		this.speed = new int[0];
		this.show = new boolean[0];
		this.change = new boolean[0];
	}

	private void setDefault() {
		this.default_speed = 10;
		this.default_show = true;
		this.default_change = true;
	}

	public void setDefaultSpeed(int i) {
		this.default_speed = i;
	}

	public void setDefaultShow(boolean flag) {
		this.default_show = flag;
	}

	public void setDefaultChange(boolean flag) {
		this.default_change = flag;
	}

	public void setDefault(int i, boolean flag, boolean flag1) {
		this.setDefaultSpeed(i);
		this.setDefaultShow(flag);
		this.setDefaultChange(flag1);
	}

	public void removeStack() {
		if (size() <= 0) {
			return;
		}
		this.directions = CollectionUtils.copyOf(directions,
				directions.length - 1);
		this.speed = CollectionUtils.copyOf(speed, speed.length - 1);
		this.show = CollectionUtils.copyOf(show, show.length - 1);
		this.change = CollectionUtils.copyOf(change, change.length - 1);
	}

	public void addStack(int i) {
		addStack(i, default_speed, default_show, default_change);
	}

	public void addStack(int d, int s, boolean isShow, boolean isChange) {
		int size = directions.length;
		this.directions = (int[]) CollectionUtils.expand(directions, 1);
		this.speed = (int[]) CollectionUtils.expand(speed, 1);
		this.show = (boolean[]) CollectionUtils.expand(show, 1);
		this.change = (boolean[]) CollectionUtils.expand(change, 1);
		this.directions[size] = d;
		this.speed[size] = s;
		this.show[size] = isShow;
		this.change[size] = isChange;
		switch (d) {
		case SRPGType.MOVE_UP:
			pos_y--;
			break;
		case SRPGType.MOVE_DOWN:
			pos_y++;
			break;
		case SRPGType.MOVE_LEFT:
			pos_x--;
			break;
		case SRPGType.MOVE_RIGHT:
			pos_x++;
			break;
		}
	}

	public int size() {
		return directions.length;
	}

	public int[] getVector() {
		return directions;
	}

	public int[] getSpeed() {
		return speed;
	}

	public boolean[] getShow() {
		return show;
	}

	public boolean[] getChange() {
		return change;
	}

	public int getPosX() {
		return pos_x;
	}

	public int getPosY() {
		return pos_y;
	}

	public void moveActor(SRPGActor actor, Screen screen) {
		boolean flag = actor.isAnimation();
		int size = directions.length;
		for (int i = 0; i < size; i++) {
			int d = directions[i];
			boolean isShow = show[i];
			boolean isChange = change[i];
			actor.setAnimation(isShow);
			if (isChange) {
				actor.setDirection(d);
			}
			actor.moveActorOnly(directions[i], speed[i]);
			actor.waitMove(screen);
		}
		actor.setAnimation(flag);
	}

}
