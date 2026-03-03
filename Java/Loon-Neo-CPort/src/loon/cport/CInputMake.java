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

import loon.LObject;
import loon.LRelease;
import loon.cport.bridge.SDLCall;
import loon.cport.bridge.SDLMouse;
import loon.cport.bridge.SDLScanCode;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.SysKey;
import loon.events.TouchMake;
import loon.utils.TArray;

public class CInputMake extends InputMake implements LRelease {

	private static class CTouch {

		float x;

		float y;

		int pointer;

		TouchMake.Event.Kind kind;

		double timer;

		public CTouch(float tx, float ty, int pointer, TouchMake.Event.Kind k, double t) {
			this.x = tx;
			this.y = ty;
			this.pointer = pointer;
			this.kind = k;
			this.timer = t;
		}

		public boolean isMove() {
			return kind == TouchMake.Event.Kind.MOVE;
		}

		public boolean isDown() {
			return kind == TouchMake.Event.Kind.START;
		}

	}

	// 连续点击的最小间隔，单位毫秒(Loon默认down事件不up则一直触发，方便游戏射击或连按环节使用，做个间隔避免太频繁，部分游戏机有坑……)
	private final static long TOUCH_CLICK_INTERVAL = 200;
	private final static int DEF_MAX_TOUCHES = 16;

	private final TArray<CInputMake.CTouch> _touchs = new TArray<CInputMake.CTouch>(DEF_MAX_TOUCHES);
	private final CGame _game;
	private final CGamepad _gamepad;

	private final boolean[] _touched = new boolean[DEF_MAX_TOUCHES];

	private final int[] _keyData = new int[SDLCall.SDL_NUM_SCANCODES];
	private final int[] _touchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _previousTouchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _rawTouchIds = new int[DEF_MAX_TOUCHES];
	private final int[] _touchX = new int[DEF_MAX_TOUCHES];
	private final int[] _touchY = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaX = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaY = new int[DEF_MAX_TOUCHES];

	private final long[] _lastStartTime = new long[DEF_MAX_TOUCHES];

	private boolean _wasJustTouched = false;
	private boolean _useTouched = false;
	private boolean _useGamepad = false;
	private boolean _onlyGamepad = false;
	private boolean _initedGamepad = false;
	private boolean _convertGamepadToKeys = false;

	private int _lastKeyPressed = 0;
	private long _currentEventTimeStamp = 0;

	public CInputMake(CGame game) {
		this._game = game;
		for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
			_previousTouchData[i * 3] = -1;
			_rawTouchIds[i] = -1;
		}
		this._useTouched = _game._csetting.emulateTouch || (SDLCall.getNumTouchDevices() > 0);
		this._useGamepad = _game._csetting.allowGamePad || SDLCall.gamepadIsSupported();
		this._onlyGamepad = _useGamepad && _game._csetting.onlyOpenGL;
		this._convertGamepadToKeys = _game._csetting.convertGamepadToKeys;
		this._gamepad = new CGamepad(0);
	}

	public void update() {
		if (_useGamepad && !_initedGamepad) {
			_gamepad.start();
			_initedGamepad = true;
		}
		_currentEventTimeStamp = SDLCall.getTicks();
		_wasJustTouched = false;

		SDLCall.getTouchData(_touchData);

		if (_useTouched) { // touch
			for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
				int id = _touchData[i * 3];
				int x = _touchData[i * 3 + 1];
				int y = _touchData[i * 3 + 2];
				if (id == -1) {
					continue;
				}
				int slot = -1;
				for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
					if (_rawTouchIds[j] == id) {
						slot = j;
						break;
					}
				}
				if (slot == -1) {
					_wasJustTouched = true;
					for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
						if (_rawTouchIds[j] == -1) {
							_rawTouchIds[j] = id;
							_touchX[j] = x;
							_touchY[j] = y;
							_deltaX[j] = 0;
							_deltaY[j] = 0;
							_touched[j] = true;
							_lastStartTime[j] = _currentEventTimeStamp;
							_touchs.add(new CTouch(x, y, id, TouchMake.Event.Kind.START, _currentEventTimeStamp));
							break;
						}
					}
				} else {
					final boolean moved = (x != _touchX[slot]) || (y != _touchY[slot]);
					if (moved) {
						_deltaX[slot] = x - _touchX[slot];
						_deltaY[slot] = y - _touchY[slot];
						_touchX[slot] = x;
						_touchY[slot] = y;
						_touchs.add(new CTouch(x, y, id, TouchMake.Event.Kind.MOVE, _currentEventTimeStamp));
					} else {
						if (_touched[slot] && (_currentEventTimeStamp - _lastStartTime[slot]) >= TOUCH_CLICK_INTERVAL) {
							_lastStartTime[slot] = _currentEventTimeStamp;
							_touchs.add(new CTouch(x, y, id, TouchMake.Event.Kind.START, _currentEventTimeStamp));
						}
					}
				}
			}
			for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
				int prevId = _previousTouchData[i * 3];
				int prevX = _previousTouchData[i * 3 + 1];
				int prevY = _previousTouchData[i * 3 + 2];
				if (prevId == -1) {
					continue;
				}
				boolean stillExists = false;
				for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
					if (_touchData[j * 3] == prevId) {
						stillExists = true;
						break;
					}
				}
				if (!stillExists) {
					for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
						if (_rawTouchIds[j] == prevId) {
							_touchs.add(
									new CTouch(prevX, prevY, prevId, TouchMake.Event.Kind.END, _currentEventTimeStamp));
							_rawTouchIds[j] = -1;
							_touched[j] = false;
							break;
						}
					}
				}
			}
			if (_touchs.size > 0) {
				postTouchEvents(_touchs);
			}
		} else { // mouse
			final int rawIndex = _touchData[0];
			final int touchX = _touchData[1];
			final int touchY = _touchData[2];
			final int buttonState = (_touchData.length > 3) ? _touchData[3] : 0;
			_deltaX[0] = touchX - _previousTouchData[1];
			_deltaY[0] = touchY - _previousTouchData[2];
			_touchX[0] = touchX;
			_touchY[0] = touchY;
			switch (rawIndex) {
			case 0:
				if (!_touched[0]) {
					_touched[0] = true;
					_rawTouchIds[0] = -1;
					_wasJustTouched = true;
					emitMouseButton(_currentEventTimeStamp, touchX, touchY, SDLMouse.getLoonButton(buttonState), true,
							0);
				}
				break;
			case -1:
				if (_touched[0] || _previousTouchData[0] != -1) {
					_touched[0] = false;
					_rawTouchIds[0] = -1;
					_wasJustTouched = false;
					emitMouseButton(_currentEventTimeStamp, touchX, touchY, SDLMouse.getLoonButton(buttonState), false,
							0);
				}
				break;
			case 1:
				if (touchX != _previousTouchData[1] || touchY != _previousTouchData[2]) {
					if (_touched[0] && buttonState != 0) {
						emitMouseButton(_currentEventTimeStamp, touchX, touchY, -1, true, 1);
					} else {
						if (_touched[0]) {
							_touched[0] = false;
							_rawTouchIds[0] = -1;
							_wasJustTouched = false;
							emitMouseButton(_currentEventTimeStamp, touchX, touchY, SDLMouse.getLoonButton(buttonState),
									false, 0);
						}
						emitMouseButton(_currentEventTimeStamp, touchX, touchY, -1, false, 2);
					}
				}
				break;
			}
			_previousTouchData[0] = rawIndex;
			_previousTouchData[1] = touchX;
			_previousTouchData[2] = touchY;
		}
		System.arraycopy(_touchData, 0, _previousTouchData, 0, DEF_MAX_TOUCHES * 3);
		if (!_onlyGamepad) {
			final int keyPressedLen = SDLCall.getPressedKeys(_keyData);
			_lastKeyPressed = SDLCall.getLastPressedScancode();
			for (int key = 0; key < keyPressedLen; key++) {
				if (_keyData[key] == 1) {
					postKey(_currentEventTimeStamp, SDLScanCode.getLoonKeyCode(key), true, (char) key, 0);
				}
			}
			final int keyReleasedLen = SDLCall.getReleasedKeys(_keyData);
			for (int unkey = 0; unkey < keyReleasedLen; unkey++) {
				if (_keyData[unkey] == 1) {
					postKey(_currentEventTimeStamp, SDLScanCode.getLoonKeyCode(unkey), false, (char) unkey, 0);
				}
			}
		}
		if (_initedGamepad) {
			if (!_onlyGamepad) {
				_gamepad.pollEvents();
			}
			_gamepad.getState();
			if (_convertGamepadToKeys) {
				// 这项属于Loon特有功能，默认会将游戏手柄0索引的按键Button映射到键盘事件上，方便开发，也可禁用
				convertGampadButtonToKeys(_gamepad);
			}
		}
	}

	protected final void convertGampadButtonToKeys(CGamepad pad) {
		if (pad != null) {

			if (_gamepad.isUp()) {
				postKey(_currentEventTimeStamp, SysKey.W, true, (char) 87, 0);
				postKey(_currentEventTimeStamp, SysKey.UP, true, (char) 38, 0);
			} else if (_gamepad.isUnUp()) {
				postKey(_currentEventTimeStamp, SysKey.W, false, (char) 87, 0);
				postKey(_currentEventTimeStamp, SysKey.UP, false, (char) 38, 0);
			}

			if (_gamepad.isDown()) {
				postKey(_currentEventTimeStamp, SysKey.S, true, (char) 83, 0);
				postKey(_currentEventTimeStamp, SysKey.DOWN, true, (char) 40, 0);
			} else if (_gamepad.isUnDown()) {
				postKey(_currentEventTimeStamp, SysKey.S, false, (char) 83, 0);
				postKey(_currentEventTimeStamp, SysKey.DOWN, false, (char) 40, 0);
			}

			if (_gamepad.isLeft()) {
				postKey(_currentEventTimeStamp, SysKey.A, true, (char) 65, 0);
				postKey(_currentEventTimeStamp, SysKey.LEFT, true, (char) 37, 0);
			} else if (_gamepad.isUnLeft()) {
				postKey(_currentEventTimeStamp, SysKey.A, false, (char) 65, 0);
				postKey(_currentEventTimeStamp, SysKey.LEFT, false, (char) 37, 0);
			}

			if (_gamepad.isRight()) {
				postKey(_currentEventTimeStamp, SysKey.D, true, (char) 68, 0);
				postKey(_currentEventTimeStamp, SysKey.RIGHT, true, (char) 39, 0);
			} else if (_gamepad.isUnRight()) {
				postKey(_currentEventTimeStamp, SysKey.D, false, (char) 68, 0);
				postKey(_currentEventTimeStamp, SysKey.RIGHT, false, (char) 39, 0);
			}

			if (_gamepad.isConfirm()) {
				postKey(_currentEventTimeStamp, SysKey.ENTER, true, (char) 13, 0);
			} else if (_gamepad.isUnConfirm()) {
				postKey(_currentEventTimeStamp, SysKey.ENTER, false, (char) 13, 0);
			}

			if (_gamepad.isCancel()) {
				postKey(_currentEventTimeStamp, SysKey.ESCAPE, true, (char) 27, 0);
			} else if (_gamepad.isUnCancel()) {
				postKey(_currentEventTimeStamp, SysKey.ESCAPE, false, (char) 27, 0);
			}

			if (_gamepad.isJump()) {
				postKey(_currentEventTimeStamp, SysKey.SPACE, true, (char) 32, 0);
			} else if (_gamepad.isUnJump()) {
				postKey(_currentEventTimeStamp, SysKey.SPACE, false, (char) 32, 0);
			}

			if (_gamepad.isShot()) {
				postKey(_currentEventTimeStamp, SysKey.X, true, (char) 88, 0);
			} else if (_gamepad.isUnShot()) {
				postKey(_currentEventTimeStamp, SysKey.X, false, (char) 88, 0);
			}

			if (_gamepad.isMenu()) {
				postKey(_currentEventTimeStamp, SysKey.MENU, true, (char) 93, 0);
			} else if (_gamepad.isUnMenu()) {
				postKey(_currentEventTimeStamp, SysKey.MENU, false, (char) 93, 0);
			}

			if (_gamepad.isBack()) {
				postKey(_currentEventTimeStamp, SysKey.BACK, true, (char) 8, 0);
			} else if (_gamepad.isUnBack()) {
				postKey(_currentEventTimeStamp, SysKey.BACK, false, (char) 8, 0);
			}

			if (_gamepad.isMisc()) {
				postKey(_currentEventTimeStamp, SysKey.P, true, (char) 44, 0);
			} else if (_gamepad.isUnMisc()) {
				postKey(_currentEventTimeStamp, SysKey.P, false, (char) 44, 0);
			}

			if (_gamepad.isLeftShoulder()) {
				postKey(_currentEventTimeStamp, SysKey.I, true, (char) 73, 0);
			} else if (_gamepad.isUnLeftShoulder()) {
				postKey(_currentEventTimeStamp, SysKey.I, false, (char) 73, 0);
			}

			if (_gamepad.isRightShoulder()) {
				postKey(_currentEventTimeStamp, SysKey.O, true, (char) 79, 0);
			} else if (_gamepad.isUnRightShoulder()) {
				postKey(_currentEventTimeStamp, SysKey.O, false, (char) 79, 0);
			}

			if (_gamepad.isLeftStick()) {
				postKey(_currentEventTimeStamp, SysKey.K, true, (char) 75, 0);
			} else if (_gamepad.isUnLeftStick()) {
				postKey(_currentEventTimeStamp, SysKey.K, false, (char) 75, 0);
			}

			if (_gamepad.isRightStick()) {
				postKey(_currentEventTimeStamp, SysKey.L, true, (char) 76, 0);
			} else if (_gamepad.isUnRightStick()) {
				postKey(_currentEventTimeStamp, SysKey.L, false, (char) 76, 0);
			}

		}
	}

	public final boolean isGamepadToKeys() {
		return _convertGamepadToKeys;
	}

	public final int getLastKeyPressed() {
		return _lastKeyPressed;
	}

	public final long getEventTimeStamp() {
		return _currentEventTimeStamp;
	}

	public synchronized final void postKey(long time, int keyCode, boolean pressed, char typedCh, int modFlags) {
		if (keyCode == SysKey.BACKSPACE) {
			typedCh = ((char) 8);
		}
		if (keyCode == SysKey.TAB) {
			typedCh = '\t';
		}
		if (keyCode == SysKey.ENTER) {
			typedCh = (char) 13;
		}
		if (keyCode == SysKey.FORWARD_DEL || keyCode == SysKey.DEL) {
			typedCh = (char) 127;
		}
		KeyMake.Event event = new KeyMake.KeyEvent(0, time, typedCh, keyCode, pressed);
		event.setFlag(modFlags);
		keyboardEvents.emit(event);
	}

	private synchronized final void postTouchEvents(TArray<CTouch> touchs) {
		postTouchEvents(touchs, false);
	}

	private synchronized final void postTouchEvents(TArray<CTouch> touchs, boolean cancel) {
		final int touchsLenght = touchs.size;
		if (touchsLenght > 0) {
			if (_useTouched) {
				final TouchMake.Event[] events = new TouchMake.Event[touchsLenght];
				for (int t = 0; t < touchsLenght; t++) {
					CTouch touch = touchs.get(t);
					if (touch != null) {
						if (!cancel) {
							events[t] = new TouchMake.Event(touch.isMove() ? -1 : 0, touch.timer, touch.x, touch.y,
									touch.kind, touch.pointer);
						} else {
							events[t] = new TouchMake.Event(touch.isMove() ? -1 : 0, touch.timer, touch.x, touch.y,
									TouchMake.Event.Kind.CANCEL, touch.pointer);
						}
					}
				}
				touchEvents.emit(events);
			} else {
				for (int t = 0; t < touchsLenght; t++) {
					CTouch touch = touchs.get(t);
					if (touch != null) {
						emitMouseButton(touch.timer, touch.x, touch.y, touch.isMove() ? -1 : 0, touch.isDown(),
								touch.pointer);
					}
				}
			}
			touchs.clear();
		}
	}

	@Override
	public boolean hasHardwareKeyboard() {
		return true;
	}

	@Override
	public boolean hasMouse() {
		return !_useTouched;
	}

	@Override
	public boolean hasTouch() {
		return _useTouched;
	}

	public boolean hasGamepad() {
		return _useGamepad;
	}

	@Override
	public void callback(LObject<?> o) {

	}

	public int mouseX() {
		return _touchX[0];
	}

	public int mouseX(int pointer) {
		return _touchX[pointer];
	}

	public int deltaX() {
		return _deltaX[0];
	}

	public int deltaX(int pointer) {
		return _deltaX[pointer];
	}

	public int touchY() {
		return _touchY[0];
	}

	public int touchY(int pointer) {
		return _touchY[pointer];
	}

	public int deltaY() {
		return _deltaY[0];
	}

	public int deltaY(int pointer) {
		return _deltaY[pointer];
	}

	public boolean isTouched() {
		return _touched[0];
	}

	public boolean justTouched() {
		return _wasJustTouched;
	}

	public boolean isTouched(int pointer) {
		return _touched[pointer];
	}

	public long getCurrentEventTime() {
		return _currentEventTimeStamp;
	}

	public CGamepad getGamepad() {
		return _gamepad;
	}

	@Override
	public void close() {
		if (_useGamepad) {
			_gamepad.close();
		}
		_initedGamepad = false;
	}

}
