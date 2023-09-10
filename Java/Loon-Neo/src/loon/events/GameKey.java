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

import loon.utils.StringKeyValue;

public class GameKey {

	protected int type;

	protected int keyCode;

	protected int presses;

	protected boolean down;

	protected char keyChar;

	protected double timer;

	GameKey() {
		reset();
	}

	GameKey(GameKey key) {
		this.type = key.type;
		this.keyCode = key.keyCode;
		this.keyChar = key.keyChar;
		this.timer = key.timer;
		this.presses = key.presses;
		this.down = key.down;
	}

	public void reset() {
		this.type = -1;
		this.keyCode = -1;
		this.keyChar = (char) -1;
		this.timer = 0;
		this.presses = 0;
		this.down = false;
	}

	public double getTimer() {
		return timer;
	}

	public boolean toggle() {
		return toggle(isDown());
	}

	public boolean toggle(boolean pressed) {
		if (pressed != this.down) {
			this.down = pressed;
		}
		if (pressed) {
			this.presses += 1;
		}
		return this.down;
	}

	public boolean equals(GameKey e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.type == type && e.keyCode == keyCode && e.keyChar == keyChar) {
			return true;
		}
		return false;
	}

	public char getKeyChar() {
		return keyChar;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public int getTypeCode() {
		return type;
	}

	public boolean isShift() {
		return type == SysKey.SHIFT_LEFT || type == SysKey.SHIFT_RIGHT;
	}

	public boolean isCtrl() {
		return type == SysKey.CONTROL_LEFT || type == SysKey.CONTROL_RIGHT;
	}

	public boolean isAlt() {
		return type == SysKey.ALT_LEFT || type == SysKey.ALT_RIGHT;
	}

	public boolean isDown() {
		return type == SysKey.DOWN;
	}

	public boolean isUp() {
		return type == SysKey.UP;
	}

	/**
	 * copy当前GameKey
	 * 
	 * @return
	 */
	public GameKey cpy() {
		return new GameKey(this);
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("GameKey");
		builder.kv("type", type).comma().kv("keyChar", keyChar).comma().kv("keyCode", keyCode).comma().kv("time",
				timer);
		return builder.toString();

	}

}
