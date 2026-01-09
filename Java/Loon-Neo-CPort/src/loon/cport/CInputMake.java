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
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysTouch;
import loon.events.TouchMake;
import loon.geom.Vector2f;
import loon.utils.SortedList;
import loon.utils.reply.Port;

public class CInputMake extends InputMake {

	private static final int MAX_TOUCHES = 16;

	protected final CGame _game;
	private final SortedList<KeyMake.Event> _kevQueue = new SortedList<KeyMake.Event>();
	private boolean _mouseDown;
	private Vector2f _pivot;
	private float _mouseX, _mouseY;
	private int _currentId;
	private long _currentEventTimeStamp;
	private final int[] _touchData = new int[MAX_TOUCHES * 3];
	private final int[] _previousTouchData = new int[MAX_TOUCHES * 3];
	private final int[] _rawTouchIds = new int[MAX_TOUCHES];
	private final int[] _touchX = new int[MAX_TOUCHES];
	private final int[] _touchY = new int[MAX_TOUCHES];
	private final int[] _deltaX = new int[MAX_TOUCHES];
	private final int[] _deltaY = new int[MAX_TOUCHES];
	private final boolean[] _touched = new boolean[MAX_TOUCHES];
	private final float[] _axes = new float[4];
	private boolean _wasJustTouched;
	private int _prevButtons;

	public CInputMake(CGame game) {
		this._game = game;
		for (int i = 0; i < MAX_TOUCHES; i++) {
			_previousTouchData[i * 3] = -1;
			_rawTouchIds[i] = -1;
		}
	}

	public void update() {
		_currentEventTimeStamp = SDLCall.getTicks64();
		_wasJustTouched = false;
		SDLCall.getTouchData(_touchData);
	}

	public void postKey(long time, int keyCode, boolean pressed, char typedCh, int modFlags) {
		KeyMake.Event event = new KeyMake.KeyEvent(0, time, typedCh, keyCode, pressed);
		event.setFlag(modFlags);
		_kevQueue.add(event);
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
