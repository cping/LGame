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
package loon.events;

import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.TimeUtils;

public class GameTouch {

	private Orientation _orientation;

	private boolean _active;

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

	public GameTouch reset() {
		this._orientation = Orientation.Portrait;
		this._active = true;
		this.type = -1;
		this.x = 0;
		this.y = 0;
		this.dx = dy = 0;
		this.timeDown = this.timeUp = 0;
		this.duration = 0;
		this.button = -1;
		this.pointer = -1;
		this.id = -1;
		return this;
	}

	public GameTouch(float x, float y, int pointer, int id) {
		this.set(x, y, pointer, id);
	}

	public GameTouch set(float x, float y, int pointer, int id) {
		this.x = x;
		this.y = y;
		this.pointer = pointer;
		this.id = id;
		return this;
	}

	GameTouch(GameTouch touch) {
		if (touch == null) {
			this.reset();
		}
		this._orientation = touch._orientation;
		this._active = touch._active;
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

	public GameTouch convertOrientation() {
		return convertOrientation(this.x, this.y);
	}

	public GameTouch convertOrientation(float posX, float posY) {
		float tmpX = 0f;
		switch (_orientation) {
		case Portrait:
			this.x = posX;
			this.y = posY;
			return this;
		case PortraitUpsideDown:
			this.x = LSystem.viewSize.getWidth() - this.x;
			return this;
		case LandscapeRight:
			tmpX = this.x;
			this.x = this.y;
			this.y = tmpX;
			return this;
		case LandscapeLeft:
			tmpX = this.x;
			this.x = this.y;
			this.y = tmpX;
			this.x = LSystem.viewSize.getWidth() - this.x;
			this.y = LSystem.viewSize.getHeight() - this.y;
			return this;
		}
		this.x = posX;
		this.y = posY;
		return this;
	}

	public GameTouch offset(float posX, float posY) {
		this.x += posX;
		this.y += posY;
		return this;
	}

	public GameTouch offsetX(float posX) {
		this.x += posX;
		return this;
	}

	public GameTouch offsetY(float posY) {
		this.y += posY;
		return this;
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

	public GameTouch setState(int s) {
		this.button = s;
		return this;
	}

	public int getState() {
		return this.button;
	}

	public boolean isDrag() {
		return isDraging;
	}

	public Vector2f get() {
		return new Vector2f((int) x, (int) y);
	}

	public boolean lowerLeft() {
		return type == SysTouch.LOWER_LEFT;
	}

	public boolean lowerRight() {
		return type == SysTouch.LOWER_RIGHT;
	}

	public boolean upperLeft() {
		return type == SysTouch.UPPER_LEFT;
	}

	public boolean upperRight() {
		return type == SysTouch.UPPER_RIGHT;
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

	public boolean isActive() {
		return _active;
	}

	public GameTouch setActive(boolean active) {
		this._active = active;
		return this;
	}

	public Orientation getOrientation() {
		return _orientation;
	}

	public GameTouch setOrientation(Orientation ori) {
		if (ori == null) {
			this._orientation = Orientation.Portrait;
			return this;
		}
		this._orientation = ori;
		return this;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("GameTouch");
		builder.kv("id", id).comma()
		.kv("point", pointer).comma()
		.kv("button", button).comma()
		.kv("timeDown", timeDown).comma()
		.kv("timeUp", timeUp).comma()
		.kv("duration", duration).comma()
		.kv("active", _active).comma()
		.kv("orientation", _orientation);
		return builder.toString();
	}

}
