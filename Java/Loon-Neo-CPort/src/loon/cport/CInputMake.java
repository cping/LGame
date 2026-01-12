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
import loon.cport.bridge.SDLCall;
import loon.cport.bridge.SDLScanCode;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.TouchMake;
import loon.geom.Vector2f;
import loon.utils.TArray;
import loon.utils.reply.Port;

public class CInputMake extends InputMake {

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
	}

	private static final int DEF_MAX_TOUCHES = 16;
	private final CGame _game;
	private final TArray<CTouch> _touchs = new TArray<CInputMake.CTouch>(DEF_MAX_TOUCHES);
	private boolean _mouseDown;
	private Vector2f _pivot;
	private float _mouseX, _mouseY;
	private int _currentId;
	private int _lastKeyPressed;
	private long _currentEventTimeStamp;
	private final int[] _touchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _previousTouchData = new int[DEF_MAX_TOUCHES * 3];
	private final int[] _rawTouchIds = new int[DEF_MAX_TOUCHES];
	private final int[] _touchX = new int[DEF_MAX_TOUCHES];
	private final int[] _touchY = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaX = new int[DEF_MAX_TOUCHES];
	private final int[] _deltaY = new int[DEF_MAX_TOUCHES];
	private final boolean[] _touched = new boolean[DEF_MAX_TOUCHES];
	private boolean _wasJustTouched;

	public CInputMake(CGame game) {
		this._game = game;
		for (int i = 0; i < DEF_MAX_TOUCHES; i++) {
			_previousTouchData[i * 3] = -1;
			_rawTouchIds[i] = -1;
		}
	}

	public void update() {
		_currentEventTimeStamp = SDLCall.getTicks64();
		_wasJustTouched = false;
		SDLCall.getTouchData(_touchData);
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
						_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], j,
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
						_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], j,
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
						_touchs.add(new CTouch(_touchData[i * 3 + 1], _touchData[i * 3 + 2], j,
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
		System.arraycopy(_touchData, 0, _previousTouchData, 0, DEF_MAX_TOUCHES * 3);
		final int[] keyPressed = SDLCall.getPressedKeys();
		final int[] keyReleased = SDLCall.getReleasedKeys();
		_lastKeyPressed = SDLCall.getLastPressedScancode();
		for (int key = 0; key < keyPressed.length; key++) {
			if (keyPressed[key] == 1) {
				postKey(_currentEventTimeStamp, SDLScanCode.getLoonKeyCode(key), true, (char) key, 0);
			}
		}
		for (int unkey = 0; unkey < keyReleased.length; unkey++) {
			if (keyReleased[unkey] == 1) {
				postKey(_currentEventTimeStamp, SDLScanCode.getLoonKeyCode(unkey), false, (char) unkey, 0);
			}
		}
	}

	public final int getLastKeyPressed() {
		return _lastKeyPressed;
	}

	public final long getEventTimeStamp() {
		return _currentEventTimeStamp;
	}

	public void postKey(long time, int keyCode, boolean pressed, char typedCh, int modFlags) {
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

	private final void postTouchEvents(TArray<CTouch> touchs) {
		postTouchEvents(touchs, false);
	}

	private final void postTouchEvents(TArray<CTouch> touchs, boolean cancel) {
		final int touchsLenght = touchs.size;
		final TouchMake.Event[] events = new TouchMake.Event[touchsLenght];
		for (int t = 0; t < touchsLenght; t++) {
			CTouch touch = touchs.get(t);
			if (!cancel) {
				events[t] = new TouchMake.Event(0, touch.timer, touch.x, touch.y, touch.kind, touch.pointer);
			} else {
				events[t] = new TouchMake.Event(0, touch.timer, touch.x, touch.y, TouchMake.Event.Kind.CANCEL,
						touch.pointer);
			}
		}
		touchs.clear();
		touchEvents.emit(events);
	}

	protected void emulateTouch() {
		keyboardEvents.connect(new Port<KeyMake.Event>() {
			@Override
			public void onEmit(KeyMake.Event event) {
				if (event instanceof KeyMake.KeyEvent) {
					KeyMake.KeyEvent kevent = (KeyMake.KeyEvent) event;
					if (kevent.down) {
						_pivot = new Vector2f(_mouseX, _mouseY);
					}
				}
			}
		});
		mouseEvents.connect(new Port<MouseMake.Event>() {
			@Override
			public void onEmit(MouseMake.Event event) {
				if (event instanceof MouseMake.ButtonEvent) {
					MouseMake.ButtonEvent bevent = (MouseMake.ButtonEvent) event;
					if (bevent.button == SysTouch.LEFT) {
						if (_mouseDown = bevent.down) {
							_currentId += 2;
							dispatchEmulateTouch(event, TouchMake.Event.Kind.START);
						} else {
							_pivot = null;
							dispatchEmulateTouch(event, TouchMake.Event.Kind.END);
						}
					}
					if (_mouseDown) {
						dispatchEmulateTouch(event, TouchMake.Event.Kind.MOVE);
					}
					_mouseX = event.x;
					_mouseY = event.y;
				}
			}
		});
	}

	@Override
	public boolean hasHardwareKeyboard() {
		return true;
	}

	@Override
	public boolean hasMouse() {
		return true;
	}

	@Override
	public boolean hasTouch() {
		return _game.setting.emulateTouch;
	}

	void init() {
		if (_game.setting.emulateTouch) {
			emulateTouch();
		}
	}

	private void dispatchEmulateTouch(MouseMake.Event event, TouchMake.Event.Kind kind) {
		float ex = event.x, ey = event.y;
		TouchMake.Event main = toEmulateTouch(event.time, ex, ey, kind, 0);
		TouchMake.Event[] evs = (_pivot == null) ? new TouchMake.Event[] { main }
				: new TouchMake.Event[] { main,
						toEmulateTouch(event.time, 2 * _pivot.x - ex, 2 * _pivot.y - ey, kind, 1) };
		touchEvents.emit(evs);
	}

	private TouchMake.Event toEmulateTouch(double time, float x, float y, TouchMake.Event.Kind kind, int idoff) {
		return new TouchMake.Event(0, time, x, y, kind, _currentId + idoff);
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

}
