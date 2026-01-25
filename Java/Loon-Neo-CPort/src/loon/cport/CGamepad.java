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
package loon.cport;

import loon.LRelease;
import loon.LSysException;
import loon.cport.bridge.SDLCall;
import loon.geom.PointF;

public class CGamepad implements LRelease {

	public static enum CGamepadKeyState {
		PRESS, RELEASE, HOLD, UNKNOWN
	}

	public static class CGamepadEvent {

		public int playerIndex;
		public int button;
		public CGamepadKeyState keyState;
		public int timeStamp;

		public CGamepadEvent(int player, int btn, int eventType, int time) {
			playerIndex = player;
			button = btn;
			switch (eventType) {
			case 0:
				keyState = CGamepadKeyState.PRESS;
				break;
			case 1:
				keyState = CGamepadKeyState.RELEASE;
				break;
			case 2:
				keyState = CGamepadKeyState.HOLD;
				break;
			default:
				keyState = CGamepadKeyState.UNKNOWN;
				break;
			}
			timeStamp = time;
		}

	}

	private static final int MAX_EVENTS = 1024;

	private final int _playerIndex;

	private float[] _axes = new float[4];
	private float[] _triggers = new float[2];

	private int[] _eventBuffer = new int[MAX_EVENTS * 4];
	private int[] _buttons = new int[5];

	private PointF _axesLeftPoint = new PointF();
	private PointF _axesRightPoint = new PointF();

	public CGamepad(int idx) {
		_playerIndex = idx;
	}

	public CGamepad start() {
		SDLCall.gamepadInit(true);
		return this;
	}

	public int pollEvents() {
		int eventCount = SDLCall.gamepadPollEvents(_eventBuffer);
		return eventCount;
	}

	public CGamepadEvent getEvent(int i) {
		if (i < 0 || i > MAX_EVENTS) {
			throw new LSysException("Data less than 0 or greater than " + MAX_EVENTS + " cannot be retrieved !");
		}
		int idx = i * 4;
		int playerIndex = _eventBuffer[idx];
		int btn = _eventBuffer[idx + 1];
		int eventType = _eventBuffer[idx + 2];
		int timestamp = _eventBuffer[idx + 3];
		return new CGamepadEvent(playerIndex, btn, eventType, timestamp);
	}

	public CGamepad getState() {
		return getState(_playerIndex);
	}

	public CGamepad getState(int playerIndex) {
		SDLCall.gamepadGetState(playerIndex, _axes, _triggers, _buttons);
		return this;
	}

	public PointF getLeftAxes() {
		_axesLeftPoint.set(_axes[0], _axes[1]);
		return _axesLeftPoint;
	}

	public PointF getRightAxes() {
		_axesRightPoint.set(_axes[2], _axes[3]);
		return _axesRightPoint;
	}

	public float getLeftTrigger() {
		return _triggers[0];
	}

	public float getRightTrigger() {
		return _triggers[1];
	}

	public boolean isConfirm() {
		return _buttons[0] == 1;
	}

	public boolean isUnConfirm() {
		return _buttons[0] != 1;
	}

	public boolean isCancel() {
		return _buttons[1] == 1;
	}

	public boolean isUnCancel() {
		return _buttons[1] != 1;
	}

	public boolean isJump() {
		return _buttons[2] == 1;
	}

	public boolean isUnJump() {
		return _buttons[2] != 1;
	}

	public boolean isShot() {
		return _buttons[3] == 1;
	}

	public boolean isUnShot() {
		return _buttons[3] != 1;
	}

	public boolean isMenu() {
		return _buttons[4] == 1;
	}

	public boolean isUnMenu() {
		return _buttons[4] != 1;
	}

	public float[] getAxes() {
		return _axes;
	}

	public float[] getTriggers() {
		return _triggers;
	}

	public int[] getButtons() {
		return _buttons;
	}

	public CGamepad stop() {
		SDLCall.gamepadClose();
		return this;
	}

	@Override
	public void close() {
		stop();
	}

}
