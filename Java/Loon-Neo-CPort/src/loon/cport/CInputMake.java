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

	private static final int DEF_MAX_TOUCHES = 16;
	private final CGame _game;
	private final CGamepad _gamepad;
	private final TArray<CTouch> _touchs = new TArray<CInputMake.CTouch>(DEF_MAX_TOUCHES);
	private int _lastKeyPressed;
	private long _currentEventTimeStamp;
	private final int[] _keyData = new int[SDLCall.SDL_NUM_SCANCODES];
	private final int[] _touchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _previousTouchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _rawTouchIds = new int[DEF_MAX_TOUCHES];
	private final int[] _touchX = new int[DEF_MAX_TOUCHES];
	private final int[] _touchY = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaX = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaY = new int[DEF_MAX_TOUCHES];
	private final boolean[] _touched = new boolean[DEF_MAX_TOUCHES];
	private boolean _wasJustTouched;
	private boolean _useTouched;
	private boolean _useGamepad;
	private boolean _onlyGamepad;
	private boolean _initedGamepad;
	private boolean _convertGamepadToKeys;

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
		_currentEventTimeStamp = SDLCall.getTicks64();
		_wasJustTouched = false;
		SDLCall.getTouchData(_touchData);
		if (_useTouched) { // touch
			for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
				final int rawIndex = _touchData[i * 3];
				if (rawIndex == -1) {
					continue;
				}
				int previousIndex = -1;
				for (int j = 0; j < DEF_MAX_TOUCHES; j++)
					if (_previousTouchData[j * 3] == rawIndex) {
						previousIndex = j;
						break;
					}
				if (previousIndex == -1) {
					_wasJustTouched = true;
					for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
						if (_rawTouchIds[j] == -1) {
							_touchX[j] = _touchData[i * 3 + 1];
							_touchY[j] = _touchData[i * 3 + 2];
							_touched[j] = true;
							_rawTouchIds[j] = rawIndex;
							_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], rawIndex,
									TouchMake.Event.Kind.START, _currentEventTimeStamp));
							break;
						}
					}
					postTouchEvents(_touchs);
				} else if (_touchData[i * 3 + 1] != _previousTouchData[previousIndex * 3 + 1]
						|| _touchData[i * 3 + 2] != _previousTouchData[previousIndex * 3 + 2]) {
					for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
						if (_rawTouchIds[j] == rawIndex) {
							_deltaX[j] = _touchData[i * 3 + 1] - _touchX[j];
							_deltaY[j] = _touchData[i * 3 + 2] - _touchY[j];
							_touchX[j] = _touchData[i * 3 + 1];
							_touchY[j] = _touchData[i * 3 + 2];
							_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], rawIndex,
									TouchMake.Event.Kind.MOVE, _currentEventTimeStamp));
							break;
						}
					}
					postTouchEvents(_touchs);
				}
			}
			for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
				int rawPreviousIndex = _previousTouchData[i * 3];
				if (rawPreviousIndex == -1) {
					continue;
				}
				int index = -1;
				for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
					if (_touchData[j * 3] == rawPreviousIndex) {
						index = j;
						break;
					}
				}
				if (index == -1) {
					for (int j = 0; j < DEF_MAX_TOUCHES; j++) {
						if (_rawTouchIds[j] == rawPreviousIndex) {
							_touchX[j] = _previousTouchData[i * 3 + 1];
							_touchY[j] = _previousTouchData[i * 3 + 2];
							_deltaX[j] = 0;
							_deltaY[j] = 0;
							_touched[j] = false;
							_rawTouchIds[j] = -1;
							_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], -1,
									TouchMake.Event.Kind.END, _currentEventTimeStamp));
							break;
						}
					}
					postTouchEvents(_touchs);
				}
			}
			if (_touchs.size > 0) {
				postTouchEvents(_touchs, true);
			}
		} else { // mouse
			final int rawIndex = _touchData[0];
			if (!(rawIndex == -1 && rawIndex == _previousTouchData[0])) {
				final int touchX = _touchData[1];
				final int touchY = _touchData[2];
				_deltaX[1] = touchX - _previousTouchData[1];
				_deltaY[2] = touchY - _previousTouchData[2];
				_touched[0] = (rawIndex == 0);
				_rawTouchIds[0] = -1;
				if (rawIndex == -1) {
					_wasJustTouched = true;
				}
				if (rawIndex != 1) {
					emitMouseButton(_currentEventTimeStamp, touchX, touchY, 0, (rawIndex == 0), 0);
				} else if (touchX != _previousTouchData[1] || touchY != _previousTouchData[2]) {
					emitMouseButton(_currentEventTimeStamp, touchX, touchY, -1, false, 0);
				}
			}
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
