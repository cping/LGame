/**
 * Copyright 2008 - 2023
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

import loon.utils.HelperUtils;

public class ActionKey {

	private EventAction _function;

	public static final int NORMAL = 0;

	public static final int DETECT_INITIAL_PRESS_ONLY = 1;

	private static final int STATE_RELEASED = 0;

	private static final int STATE_PRESSED = 1;

	private static final int STATE_WAITING_FOR_RELEASE = 2;

	private final int _mode;

	private long _elapsedTime;

	private int _amount;

	private int _state;

	private int _keyCallFunction = -1;

	private boolean _interrupt;

	private boolean _keyPressDown;

	public ActionKey() {
		this(null);
	}

	public ActionKey(EventAction ea) {
		this(NORMAL, ea);
	}

	public ActionKey(int mode) {
		this(mode, null);
	}

	public ActionKey(int mode, EventAction ea) {
		this._mode = mode;
		this._function = ea;
		this.reset();
	}

	public void act() {
		act(0);
	}

	public void act(long elapsed) {
		this._elapsedTime = elapsed;
		if (isAllStateCallFun()) {
			HelperUtils.callEventAction(_function, this);
		} else if (_keyPressDown && isPressCallFun()) {
			HelperUtils.callEventAction(_function, this);
		} else if (!_keyPressDown && isReleaseCallFun()) {
			HelperUtils.callEventAction(_function, this);
		}
	}

	public boolean isAllStateCallFun() {
		return _keyCallFunction == -1;
	}

	public boolean isPressCallFun() {
		return _keyCallFunction == 0;
	}

	public boolean isReleaseCallFun() {
		return _keyCallFunction == 1;
	}

	// user overload
	public void onPress() {

	}

	// user overload
	public void onRelease() {

	}

	public ActionKey setInterrupt(boolean stop) {
		this._interrupt = stop;
		return this;
	}

	public long getElapsedTime() {
		return _elapsedTime;
	}

	public boolean isInterrupt() {
		return _interrupt;
	}

	public ActionKey reset() {
		this._state = STATE_RELEASED;
		this._amount = 0;
		this._keyCallFunction = -1;
		return this;
	}

	public ActionKey press() {
		this._keyPressDown = true;
		this.onPress();
		if (_state != STATE_WAITING_FOR_RELEASE) {
			_amount++;
			_state = STATE_PRESSED;
		}
		return this;
	}

	public ActionKey release() {
		this._keyPressDown = false;
		this.onRelease();
		this._state = STATE_RELEASED;
		return this;
	}

	public boolean isReleased() {
		return _state == STATE_RELEASED;
	}

	public boolean isPressed() {
		if (_amount != 0) {
			if (_state == STATE_RELEASED) {
				_amount = 0;
			} else if (_mode == DETECT_INITIAL_PRESS_ONLY) {
				_state = STATE_WAITING_FOR_RELEASE;
				_amount = 0;
			}
			return true;
		}
		return false;
	}

	public boolean isKeyPressDown() {
		return _keyPressDown;
	}

	public EventAction getFunction() {
		return _function;
	}

	public ActionKey setFunction(EventAction f) {
		this._function = f;
		return this;
	}

	public ActionKey setKeyCallFunState(int f) {
		this._keyCallFunction = f;
		return this;
	}
}
