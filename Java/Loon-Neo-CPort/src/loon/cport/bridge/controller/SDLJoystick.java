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
package loon.cport.bridge.controller;

import loon.LRelease;
import loon.cport.bridge.SDLCall;

public final class SDLJoystick implements LRelease {

	public final static SDLJoystick open(int idx) {
		long handle = SDLCall.joystickOpen(idx);
		return new SDLJoystick(handle);
	}

	private boolean _closed;

	private long _joystickHandle;

	protected SDLJoystick(long handle) {
		_joystickHandle = handle;
	}

	public int numAxes() {
		return SDLCall.joystickNumAxes(_joystickHandle);
	}

	public int numBalls() {
		return SDLCall.joystickNumBalls(_joystickHandle);
	}

	public int numHats() {
		return SDLCall.joystickNumHats(_joystickHandle);
	}

	public int numButtons() {
		return SDLCall.joystickNumButtons(_joystickHandle);
	}

	public String getGUIDString(char[] guids) {
		return SDLCall.joystickGetGUIDString(_joystickHandle, guids);
	}

	public long getHandle() {
		return _joystickHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_joystickHandle != 0 && !_closed) {
			SDLCall.joystickClose(_joystickHandle);
			_joystickHandle = 0;
			_closed = true;
		}
	}

}