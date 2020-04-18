/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.event;

import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.TimeUtils;

public class GameTouch {

	protected int type;

	protected float x, y;

	protected float dx, dy;

	protected int button;

	protected int pointer;

	protected int id;

	protected long timeDown;

	protected long timeUp;

	protected long duration;

	GameTouch() {
		reset();
	}

	public void reset() {
		this.type = -1;
		this.x = 0;
		this.y = 0;
		this.dx = dy = 0;
		this.timeDown = this.timeUp = 0;
		this.duration = 0;
		this.button = -1;
		this.pointer = -1;
		this.id = -1;
	}

	public GameTouch(float x, float y, int pointer, int id) {
		this.set(x, y, pointer, id);
	}

	public void set(float x, float y, int pointer, int id) {
		this.x = x;
		this.y = y;
		this.pointer = pointer;
		this.id = id;
	}

	GameTouch(GameTouch touch) {
		this.type = touch.type;
		this.x = touch.x;
		this.y = touch.y;
		this.dx = touch.dx;
		this.dy = touch.dy;
		this.button = touch.button;
		this.pointer = touch.pointer;
		this.duration = touch.duration;
		this.id = touch.id;
		this.timeUp = touch.timeUp;
		this.timeDown = touch.timeDown;
	}

	public void offset(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void offsetX(float x) {
		this.x += x;
	}

	public void offsetY(float y) {
		this.y += y;
	}

	public boolean equals(GameTouch e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.type == type && e.x == x && e.y == y && e.button == button && e.pointer == pointer && e.id == id) {
			return true;
		}
		return false;
	}

	public int getButton() {
		return button;
	}

	public int getPointer() {
		return pointer;
	}

	public int getType() {
		return type;
	}

	public int getID() {
		return id;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public float getX() {
		return x;
	}

	public int getTileX(int tileX) {
		return (int) (x / tileX);
	}

	public float getY() {
		return y;
	}

	public int getTileY(int tileY) {
		return (int) (y / tileY);
	}

	public float getDX() {
		return dx;
	}

	public float getDY() {
		return dy;
	}

	boolean isDraging;

	public boolean isLeft() {
		return button == SysTouch.LEFT;
	}

	public boolean isMiddle() {
		return button == SysTouch.MIDDLE;
	}
	
	public boolean isRight() {
		return button == SysTouch.RIGHT;
	}
	
	public boolean isDown() {
		return button == SysTouch.TOUCH_DOWN;
	}

	public boolean isUp() {
		return button == SysTouch.TOUCH_UP;
	}

	public boolean isMove() {
		return button == SysTouch.TOUCH_MOVE;
	}

	public boolean isDrag() {
		return isDraging;
	}

	public Vector2f get() {
		return new Vector2f((int) x, (int) y);
	}
	
	/**
	 * 判断触屏按下事件是否超过了当前系统时间
	 * 
	 * @return
	 */
	public boolean justPressed() {
		return justPressed(TimeUtils.millis());
	}

	/**
	 * 判断触屏按下事件是否超过了指定的时间
	 * 
	 * @param time
	 * @return
	 */
	public boolean justPressed(long time) {
		return (this.isDown() && (this.timeDown + duration) > time);
	}

	/**
	 * 判断触屏松开事件是否超过了当前系统时间
	 * 
	 * @return
	 */
	public boolean justReleased() {
		return justReleased(TimeUtils.millis());
	}

	/**
	 * 判断触屏松开事件是否超过了指定的时间
	 * 
	 * @param time
	 * @return
	 */
	public boolean justReleased(long time) {
		return (this.isUp() && (this.timeUp + duration) > time);
	}

	/**
	 * 触屏（或鼠标）按下的具体时间
	 * 
	 * @return
	 */
	public long getTimeDown() {
		return timeDown;
	}

	/**
	 * 触屏(或鼠标)松开的具体时间
	 * 
	 * @return
	 */
	public long getTimeUp() {
		return timeUp;
	}

	/**
	 * 触屏(或鼠标)按下到松开的具体耗时
	 * 
	 * @return
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * copy当前GameTouch
	 * 
	 * @return
	 */
	public GameTouch cpy() {
		return new GameTouch(this);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("GameTouch");
		builder.kv("id", id).comma().kv("point", pointer).comma().kv("button", button).comma().kv("timeDown", timeDown)
				.comma().kv("timeUp", timeUp).comma().kv("duration", duration);
		return builder.toString();
	}

}
