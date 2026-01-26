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
import loon.utils.MathUtils;

public class CGamepad implements LRelease {

	public static enum CGamepadDirection {
		DIR_CENTER, DIR_UP, DIR_UP_RIGHT, DIR_RIGHT, DIR_DOWN_RIGHT, DIR_DOWN, DIR_DOWN_LEFT, DIR_LEFT, DIR_UP_LEFT
	}

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

	public static CGamepadDirection angleToDirection(float angleDeg) {
		if (angleDeg >= -22.5f && angleDeg < 22.5f) {
			return CGamepadDirection.DIR_RIGHT;
		}
		if (angleDeg >= 22.5f && angleDeg < 67.5f) {
			return CGamepadDirection.DIR_DOWN_RIGHT;
		}
		if (angleDeg >= 67.5f && angleDeg < 112.5f) {
			return CGamepadDirection.DIR_UP;
		}
		if (angleDeg >= 112.5f && angleDeg < 157.5f) {
			return CGamepadDirection.DIR_UP_LEFT;
		}
		if (angleDeg >= 157.5f || angleDeg < -157.5f) {
			return CGamepadDirection.DIR_LEFT;
		}
		if (angleDeg >= -157.5f && angleDeg < -112.5f) {
			return CGamepadDirection.DIR_UP_LEFT;
		}
		if (angleDeg >= -112.5f && angleDeg < -67.5f) {
			return CGamepadDirection.DIR_DOWN;
		}
		if (angleDeg >= -67.5f && angleDeg < -22.5f) {
			return CGamepadDirection.DIR_DOWN_RIGHT;
		}
		return CGamepadDirection.DIR_CENTER;
	}

	private static final int MAX_EVENTS = 1024;

	private static final float DEAD_ZONE = 0.25f;

	private int _playerIndex;

	private float[] _axes = new float[4];
	private float[] _triggers = new float[2];

	private int[] _eventBuffer = new int[MAX_EVENTS * 4];
	private int[] _buttons = new int[15];

	private PointF _axesLeftPoint = new PointF();
	private PointF _axesRightPoint = new PointF();

	public CGamepad(int idx) {
		_playerIndex = idx;
	}

	public boolean isSupported() {
		return SDLCall.gamepadIsSupported();
	}

	public CGamepad start() {
		SDLCall.gamepadInit(true);
		return this;
	}

	public int pollEvents() {
		int eventCount = SDLCall.gamepadPollEvents(_eventBuffer);
		return eventCount;
	}

	public CGamepadDirection getButtonDirection() {
		if (isUp()) {
			return CGamepadDirection.DIR_UP;
		}
		if (isDown()) {
			return CGamepadDirection.DIR_DOWN;
		}
		if (isLeft()) {
			return CGamepadDirection.DIR_LEFT;
		}
		if (isRight()) {
			return CGamepadDirection.DIR_RIGHT;
		}
		return CGamepadDirection.DIR_CENTER;
	}

	public CGamepadDirection getDirection() {
		CGamepadDirection dir = getButtonDirection();
		if (dir == CGamepadDirection.DIR_CENTER) {
			PointF axes = getLeftAxes();
			return getJoystickDirection(axes.x, axes.y);
		}
		return dir;
	}

	public CGamepadEvent getEvent() {
		return getEvent(_playerIndex);
	}

	public CGamepadEvent getEvent(int playIndex) {
		if (playIndex < 0 || playIndex > MAX_EVENTS) {
			throw new LSysException("Data less than 0 or greater than " + MAX_EVENTS + " cannot be retrieved !");
		}
		int idx = playIndex * 4;
		int playerIndex = _eventBuffer[idx];
		int btn = _eventBuffer[idx + 1];
		int eventType = _eventBuffer[idx + 2];
		int timestamp = _eventBuffer[idx + 3];
		return new CGamepadEvent(playerIndex, btn, eventType, timestamp);
	}

	public void setPlayerIndex(int idx) {
		_playerIndex = idx;
	}

	public int getPlayerIndex() {
		return _playerIndex;
	}

	public CGamepadDirection getJoystickDirection(float axisX, float axisY) {
		if (axisX == 0.0f && axisY == 0.0f) {
			return CGamepadDirection.DIR_CENTER;
		}

		float x = axisX;
		float y = axisY;

		float magnitude = MathUtils.sqrt(x * x + y * y);

		if (magnitude < DEAD_ZONE) {
			return CGamepadDirection.DIR_CENTER;
		}

		float angleRad = MathUtils.atan2(-y, x);
		float angleDeg = angleRad * 180.0f / MathUtils.PI;

		return angleToDirection(angleDeg);
	}

	public CGamepadDirection getLeftJoystickDirection() {
		PointF axes = getLeftAxes();
		return getJoystickDirection(axes.x, axes.y);
	}

	public CGamepadDirection getRightJoystickDirection() {
		PointF axes = getRightAxes();
		return getJoystickDirection(axes.x, axes.y);
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

	public boolean isUp() {
		return _buttons[0] == 1;
	}

	public boolean isUnUp() {
		return _buttons[0] == -1;
	}

	public boolean isDown() {
		return _buttons[1] == 1;
	}

	public boolean isUnDown() {
		return _buttons[1] == -1;
	}

	public boolean isLeft() {
		return _buttons[2] == 1;
	}

	public boolean isUnLeft() {
		return _buttons[2] == -1;
	}

	public boolean isRight() {
		return _buttons[3] == 1;
	}

	public boolean isUnRight() {
		return _buttons[3] == -1;
	}

	public boolean isConfirm() {
		return _buttons[4] == 1;
	}

	public boolean isUnConfirm() {
		return _buttons[4] == -1;
	}

	public boolean isCancel() {
		return _buttons[5] == 1;
	}

	public boolean isUnCancel() {
		return _buttons[5] == -1;
	}

	public boolean isJump() {
		return _buttons[6] == 1;
	}

	public boolean isUnJump() {
		return _buttons[6] == -1;
	}

	public boolean isShot() {
		return _buttons[7] == 1;
	}

	public boolean isUnShot() {
		return _buttons[7] == -1;
	}

	public boolean isMenu() {
		return _buttons[8] == 1;
	}

	public boolean isUnMenu() {
		return _buttons[8] == -1;
	}

	public boolean isBack() {
		return _buttons[9] == 1;
	}

	public boolean isUnBack() {
		return _buttons[9] == -1;
	}

	public boolean isLeftShoulder() {
		return _buttons[10] == 1;
	}

	public boolean isUnLeftShoulder() {
		return _buttons[10] == -1;
	}

	public boolean isRightShoulder() {
		return _buttons[11] == 1;
	}

	public boolean isUnRightShoulder() {
		return _buttons[11] == -1;
	}

	public boolean isLeftStick() {
		return _buttons[12] == 1;
	}

	public boolean isUnLeftStick() {
		return _buttons[12] == -1;
	}

	public boolean isRightStick() {
		return _buttons[13] == 1;
	}

	public boolean isUnRightStick() {
		return _buttons[13] == -1;
	}

	public boolean isMisc() {
		return _buttons[14] == 1;
	}

	public boolean isUnMisc() {
		return _buttons[14] == -1;
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
