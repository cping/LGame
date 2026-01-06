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

public final class SDLGameController implements LRelease {

	public final static SDLGameController open(int idx) {
		long handle = SDLCall.gameControllerOpen(idx);
		return new SDLGameController(handle);
	}

	private boolean _closed;

	private long _controllerHandle;

	private SDLGameController(long handle) {
		_controllerHandle = handle;
	}

	public String getName() {
		return SDLCall.gameControllerName(_controllerHandle);
	}

	public String getPath() {
		return SDLCall.gameControllerPath(_controllerHandle);
	}

	public int getCType() {
		return SDLCall.gameControllerGetType(_controllerHandle);
	}

	public int getPlayerIndex() {
		return SDLCall.gameControllerGetPlayerIndex(_controllerHandle);
	}

	public void setPlayerIndex(int joystickIndex) {
		SDLCall.gameControllerSetPlayerIndex(_controllerHandle, joystickIndex);
	}

	public short getVendor(int joystickIndex) {
		return SDLCall.gameControllerGetVendor(_controllerHandle);
	}

	public int gameControllerGetNumTouchpads() {
		return SDLCall.gameControllerGetNumTouchpads(_controllerHandle);
	}

	public long getHandle() {
		return _controllerHandle;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_controllerHandle != 0 && !_closed) {
			SDLCall.gameControllerClose(_controllerHandle);
			_controllerHandle = 0;
			_closed = true;
		}
	}

}
