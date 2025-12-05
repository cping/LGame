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
package loon.teavm;

import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.EventTarget;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.events.Touch;
import org.teavm.jso.dom.events.TouchEvent;
import org.teavm.jso.dom.events.WheelEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;

import loon.LObject;
import loon.LSystem;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.TouchMake;
import loon.geom.Vector2f;
import loon.teavm.dom.DocumentExt;
import loon.teavm.dom.ElementExt;
import loon.teavm.dom.HTMLElementExt;
import loon.utils.IntArray;

public class TeaInputMake extends InputMake implements EventListener<Event> {

	public static final int KEY_A = 65;

	public static final int KEY_B = 66;

	public static final int KEY_C = 67;

	public static final int KEY_D = 68;

	public static final int KEY_E = 69;

	public static final int KEY_F = 70;

	public static final int KEY_G = 71;

	public static final int KEY_H = 72;

	public static final int KEY_I = 73;

	public static final int KEY_J = 74;

	public static final int KEY_K = 75;

	public static final int KEY_L = 76;

	public static final int KEY_M = 77;

	public static final int KEY_N = 78;

	public static final int KEY_O = 79;

	public static final int KEY_P = 80;

	public static final int KEY_Q = 81;

	public static final int KEY_R = 82;

	public static final int KEY_S = 83;

	public static final int KEY_T = 84;

	public static final int KEY_U = 85;

	public static final int KEY_V = 86;

	public static final int KEY_W = 87;

	public static final int KEY_X = 88;

	public static final int KEY_Y = 89;

	public static final int KEY_Z = 90;

	public static final int KEY_ZERO = 48;

	public static final int KEY_ONE = 49;

	public static final int KEY_TWO = 50;

	public static final int KEY_THREE = 51;

	public static final int KEY_FOUR = 52;

	public static final int KEY_FIVE = 53;

	public static final int KEY_SIX = 54;

	public static final int KEY_SEVEN = 55;

	public static final int KEY_EIGHT = 56;

	public static final int KEY_NINE = 57;

	public static final int KEY_NUM_ZERO = 96;

	public static final int KEY_NUM_ONE = 97;

	public static final int KEY_NUM_TWO = 98;

	public static final int KEY_NUM_THREE = 99;

	public static final int KEY_NUM_FOUR = 100;

	public static final int KEY_NUM_FIVE = 101;

	public static final int KEY_NUM_SIX = 102;

	public static final int KEY_NUM_SEVEN = 103;

	public static final int KEY_NUM_EIGHT = 104;

	public static final int KEY_NUM_NINE = 105;

	public static final int KEY_NUM_MULTIPLY = 106;

	public static final int KEY_NUM_PLUS = 107;

	public static final int KEY_NUM_MINUS = 109;

	public static final int KEY_NUM_PERIOD = 110;

	public static final int KEY_NUM_DIVISION = 111;

	public static final int KEY_ALT = 18;

	public static final int KEY_BACKSPACE = 8;

	public static final int KEY_CTRL = 17;

	public static final int KEY_DELETE = 46;

	public static final int KEY_DOWN = 40;

	public static final int KEY_END = 35;

	public static final int KEY_ENTER = 13;

	public static final int KEY_ESCAPE = 27;

	public static final int KEY_HOME = 36;

	public static final int KEY_LEFT = 37;

	public static final int KEY_PAGEDOWN = 34;

	public static final int KEY_PAGEUP = 33;

	public static final int KEY_RIGHT = 39;

	public static final int KEY_SHIFT = 16;

	public static final int KEY_TAB = 9;

	public static final int KEY_UP = 38;

	public static final int KEY_F1 = 112;

	public static final int KEY_F2 = 113;

	public static final int KEY_F3 = 114;

	public static final int KEY_F4 = 115;

	public static final int KEY_F5 = 116;

	public static final int KEY_F6 = 117;

	public static final int KEY_F7 = 118;

	public static final int KEY_F8 = 119;

	public static final int KEY_F9 = 120;

	public static final int KEY_F10 = 121;

	public static final int KEY_F11 = 122;

	public static final int KEY_F12 = 123;

	public static final int KEY_WIN_KEY_FF_LINUX = 0;

	public static final int KEY_MAC_ENTER = 3;

	public static final int KEY_PAUSE = 19;

	public static final int KEY_CAPS_LOCK = 20;

	public static final int KEY_SPACE = 32;

	public static final int KEY_PRINT_SCREEN = 44;

	public static final int KEY_INSERT = 45;

	public static final int KEY_NUM_CENTER = 12;

	public static final int KEY_WIN_KEY = 224;

	public static final int KEY_WIN_KEY_LEFT_META = 91;

	public static final int KEY_WIN_KEY_RIGHT = 92;

	public static final int KEY_CONTEXT_MENU = 93;

	public static final int KEY_MAC_FF_META = 224;

	public static final int KEY_NUMLOCK = 144;

	public static final int KEY_SCROLL_LOCK = 145;

	public static final int KEY_FIRST_MEDIA_KEY = 166;

	public static final int KEY_LAST_MEDIA_KEY = 183;

	public static final int KEY_WIN_IME = 229;

	private static final int KEY_0 = 48;

	private static final int KEY_1 = 49;

	private static final int KEY_2 = 50;

	private static final int KEY_3 = 51;

	private static final int KEY_4 = 52;

	private static final int KEY_5 = 53;

	private static final int KEY_6 = 54;

	private static final int KEY_7 = 55;

	private static final int KEY_8 = 56;

	private static final int KEY_9 = 57;

	private static final int KEY_LEFT_WINDOW_KEY = 91;

	private static final int KEY_RIGHT_WINDOW_KEY = 92;

	private static final int KEY_NUMPAD0 = 96;

	private static final int KEY_NUMPAD1 = 97;

	private static final int KEY_NUMPAD2 = 98;

	private static final int KEY_NUMPAD3 = 99;

	private static final int KEY_NUMPAD4 = 100;

	private static final int KEY_NUMPAD5 = 101;

	private static final int KEY_NUMPAD6 = 102;

	private static final int KEY_NUMPAD7 = 103;

	private static final int KEY_NUMPAD8 = 104;

	private static final int KEY_NUMPAD9 = 105;

	private static final int KEY_MULTIPLY = 106;

	private static final int KEY_ADD = 107;

	private static final int KEY_SUBTRACT = 109;

	private static final int KEY_DECIMAL_POINT_KEY = 110;

	private static final int KEY_DIVIDE = 111;

	private static final int KEY_NUM_LOCK = 144;

	private static final int KEY_SEMICOLON = 186;

	private static final int KEY_EQUALS = 187;

	private static final int KEY_COMMA = 188;

	private static final int KEY_DASH = 189;

	private static final int KEY_PERIOD = 190;

	private static final int KEY_FORWARD_SLASH = 191;

	private static final int KEY_GRAVE_ACCENT = 192;

	private static final int KEY_OPEN_BRACKET = 219;

	private static final int KEY_BACKSLASH = 220;

	private static final int KEY_CLOSE_BRACKET = 221;

	private static final int KEY_SINGLE_QUOTE = 222;

	private static final int BUTTON_LEFT = 0;

	private static final int BUTTON_MIDDLE = 1;

	private static final int BUTTON_RIGHT = 2;

	public static boolean isArrowKey(int code) {
		switch (code) {
		case KEY_DOWN:
		case KEY_RIGHT:
		case KEY_UP:
		case KEY_LEFT:
			return true;
		default:
			return false;
		}
	}

	public static int maybeSwapArrowSysKeyForRtl(int code, boolean isRtl) {
		if (isRtl) {
			if (code == KEY_RIGHT) {
				code = KEY_LEFT;
			} else if (code == KEY_LEFT) {
				code = KEY_RIGHT;
			}
		}
		return code;
	}

	public static int keyForCode(int keyCode) {
		switch (keyCode) {
		case KEY_ALT:
			return SysKey.ALT_LEFT;
		case KEY_BACKSPACE:
			return SysKey.BACKSPACE;
		case KEY_CTRL:
			return SysKey.CONTROL_LEFT;
		case KEY_DELETE:
			return SysKey.FORWARD_DEL;
		case KEY_DOWN:
			return SysKey.DOWN;
		case KEY_END:
			return SysKey.END;
		case KEY_ENTER:
			return SysKey.ENTER;
		case KEY_ESCAPE:
			return SysKey.ESCAPE;
		case KEY_HOME:
			return SysKey.HOME;
		case KEY_LEFT:
			return SysKey.LEFT;
		case KEY_PAGEDOWN:
			return SysKey.PAGE_DOWN;
		case KEY_PAGEUP:
			return SysKey.PAGE_UP;
		case KEY_RIGHT:
			return SysKey.RIGHT;
		case KEY_SHIFT:
			return SysKey.SHIFT_LEFT;
		case KEY_TAB:
			return SysKey.TAB;
		case KEY_UP:
			return SysKey.UP;
		case KEY_PAUSE:
			return SysKey.UNKNOWN;
		case KEY_CAPS_LOCK:
			return SysKey.UNKNOWN;
		case KEY_SPACE:
			return SysKey.SPACE;
		case KEY_INSERT:
			return SysKey.INSERT;
		case KEY_0:
			return SysKey.NUM_0;
		case KEY_1:
			return SysKey.NUM_1;
		case KEY_2:
			return SysKey.NUM_2;
		case KEY_3:
			return SysKey.NUM_3;
		case KEY_4:
			return SysKey.NUM_4;
		case KEY_5:
			return SysKey.NUM_5;
		case KEY_6:
			return SysKey.NUM_6;
		case KEY_7:
			return SysKey.NUM_7;
		case KEY_8:
			return SysKey.NUM_8;
		case KEY_9:
			return SysKey.NUM_9;
		case KEY_A:
			return SysKey.A;
		case KEY_B:
			return SysKey.B;
		case KEY_C:
			return SysKey.C;
		case KEY_D:
			return SysKey.D;
		case KEY_E:
			return SysKey.E;
		case KEY_F:
			return SysKey.F;
		case KEY_G:
			return SysKey.G;
		case KEY_H:
			return SysKey.H;
		case KEY_I:
			return SysKey.I;
		case KEY_J:
			return SysKey.J;
		case KEY_K:
			return SysKey.K;
		case KEY_L:
			return SysKey.L;
		case KEY_M:
			return SysKey.M;
		case KEY_N:
			return SysKey.N;
		case KEY_O:
			return SysKey.O;
		case KEY_P:
			return SysKey.P;
		case KEY_Q:
			return SysKey.Q;
		case KEY_R:
			return SysKey.R;
		case KEY_S:
			return SysKey.S;
		case KEY_T:
			return SysKey.T;
		case KEY_U:
			return SysKey.U;
		case KEY_V:
			return SysKey.V;
		case KEY_W:
			return SysKey.W;
		case KEY_X:
			return SysKey.X;
		case KEY_Y:
			return SysKey.Y;
		case KEY_Z:
			return SysKey.Z;
		case KEY_LEFT_WINDOW_KEY:
			return SysKey.UNKNOWN;
		case KEY_RIGHT_WINDOW_KEY:
			return SysKey.UNKNOWN;
		case KEY_NUMPAD0:
			return SysKey.NUM_0;
		case KEY_NUMPAD1:
			return SysKey.NUM_1;
		case KEY_NUMPAD2:
			return SysKey.NUM_2;
		case KEY_NUMPAD3:
			return SysKey.NUM_3;
		case KEY_NUMPAD4:
			return SysKey.NUM_4;
		case KEY_NUMPAD5:
			return SysKey.NUM_5;
		case KEY_NUMPAD6:
			return SysKey.NUM_6;
		case KEY_NUMPAD7:
			return SysKey.NUM_7;
		case KEY_NUMPAD8:
			return SysKey.NUM_8;
		case KEY_NUMPAD9:
			return SysKey.NUM_9;
		case KEY_MULTIPLY:
			return SysKey.UNKNOWN;
		case KEY_ADD:
			return SysKey.PLUS;
		case KEY_SUBTRACT:
			return SysKey.MINUS;
		case KEY_DECIMAL_POINT_KEY:
			return SysKey.PERIOD;
		case KEY_DIVIDE:
			return SysKey.UNKNOWN;
		case KEY_F1:
			return SysKey.NUM_1;
		case KEY_F2:
			return SysKey.NUM_2;
		case KEY_F3:
			return SysKey.NUM_3;
		case KEY_F4:
			return SysKey.NUM_4;
		case KEY_F5:
			return SysKey.NUM_5;
		case KEY_F6:
			return SysKey.NUM_6;
		case KEY_F7:
			return SysKey.NUM_7;
		case KEY_F8:
			return SysKey.NUM_8;
		case KEY_F9:
			return SysKey.NUM_9;
		case KEY_F10:
			return SysKey.STAR;
		case KEY_F11:
			return SysKey.POUND;
		case KEY_F12:
			return SysKey.DPAD_UP;
		case KEY_NUM_LOCK:
			return SysKey.NUM;
		case KEY_SCROLL_LOCK:
			return SysKey.UNKNOWN;
		case KEY_SEMICOLON:
			return SysKey.SEMICOLON;
		case KEY_EQUALS:
			return SysKey.EQUALS;
		case KEY_COMMA:
			return SysKey.COMMA;
		case KEY_DASH:
			return SysKey.MINUS;
		case KEY_PERIOD:
			return SysKey.PERIOD;
		case KEY_FORWARD_SLASH:
			return SysKey.SLASH;
		case KEY_GRAVE_ACCENT:
			return SysKey.UNKNOWN;
		case KEY_OPEN_BRACKET:
			return SysKey.LEFT_BRACKET;
		case KEY_BACKSLASH:
			return SysKey.BACKSLASH;
		case KEY_CLOSE_BRACKET:
			return SysKey.RIGHT_BRACKET;
		case KEY_SINGLE_QUOTE:
			return SysKey.APOSTROPHE;
		default:
			return SysKey.UNKNOWN;
		}
	}

	protected static int getMouseButton(MouseEvent evt) {
		return getButton(evt.getButton());

	}

	public static int getButton(int button) {
		if (button == BUTTON_LEFT) {
			return SysTouch.LEFT;
		}
		if (button == BUTTON_RIGHT) {
			return SysTouch.RIGHT;
		}
		if (button == BUTTON_MIDDLE) {
			return SysTouch.MIDDLE;
		}
		return -1;
	}

	private final TeaGame game;
	private final HTMLCanvasElement rootElement;

	private final IntArray keysToCatch = new IntArray();
	private final Vector2f lastMousePt = new Vector2f();

	private boolean hasFocus;

	private boolean inDragSequence = false;
	private boolean isRequestingMouseLock;

	private boolean inTouchSequence = false;

	private float touchDX = -1, touchDY = -1;

	public TeaInputMake(TeaGame gwtgame, HTMLCanvasElement root) {
		this.game = gwtgame;
		this.rootElement = root;
		HTMLDocument document = root.getOwnerDocument();
		document.addEventListener("mousedown", this, false);
		document.addEventListener("mouseup", this, false);
		document.addEventListener("mousemove", this, false);
		document.addEventListener("wheel", this, false);

		document.addEventListener("keydown", this, false);
		document.addEventListener("keyup", this, false);
		document.addEventListener("keypress", this, false);

		root.addEventListener("touchstart", this, true);
		root.addEventListener("touchmove", this, true);
		root.addEventListener("touchcancel", this, true);
		root.addEventListener("touchend", this, true);

	}

	public boolean isCatchKey(int keycode) {
		return keysToCatch.contains(keycode);
	}

	public void setCatchKey(int keycode, boolean catchKey) {
		if (!catchKey) {
			keysToCatch.removeValue(keycode);
		} else {
			keysToCatch.add(keycode);
		}
	}

	@Override
	public void handleEvent(Event e) {
		if (game != null) {
			handleMouseEvents(e);
			handleKeyboardEvents(e);
		}
	}

	private float wheelDelta = -1;

	private float lastX = -1, lastY = -1;

	public float getWheelDelta() {
		return wheelDelta;
	}

	private void handleMouseEvents(Event e) {
		String type = e.getType();

		if (type.equals("contextmenu")) {
			e.preventDefault();
			e.stopPropagation();
		} else if (type.equals("mousedown")) {
			Window.current().focus();
			MouseEvent mouseEvent = (MouseEvent) e;
			float x = getRelativeX(mouseEvent, rootElement);
			float y = getRelativeY(mouseEvent, rootElement);
			EventTarget target = e.getTarget();
			boolean equals = target == rootElement;
			if (!equals) {
				float mouseX = getRelativeX(mouseEvent, rootElement);
				float mouseY = getRelativeY(mouseEvent, rootElement);
				if (mouseX < 0 || mouseX > LSystem.viewSize.getWidth() || mouseY < 0
						|| mouseY > LSystem.viewSize.getHeight()) {
					hasFocus = false;
				}
				return;
			}
			hasFocus = true;
			inDragSequence = true;
			int btn = getMouseButton(mouseEvent);
			if (btn != -1) {
				dispatch(new MouseMake.ButtonEvent(0, game.time(), x, y, btn, true), mouseEvent);
			}
			e.preventDefault();
			e.stopPropagation();
		} else if (type.equals("mouseup")) {
			MouseEvent mouseEvent = (MouseEvent) e;
			float x = getRelativeX(mouseEvent, rootElement);
			float y = getRelativeY(mouseEvent, rootElement);
			if (inDragSequence) {
				inDragSequence = false;
				int btn = getMouseButton(mouseEvent);
				if (btn != -1) {
					dispatch(new MouseMake.ButtonEvent(0, game.time(), x, y, btn, false), mouseEvent);
				}
			}
			handleRequestsInUserEventContext();
		} else if (type.equals("mousemove")) {
			MouseEvent mouseEvent = (MouseEvent) e;
			float x = getRelativeX(mouseEvent, rootElement);
			float y = getRelativeY(mouseEvent, rootElement);
			if (lastX == -1) {
				lastX = x;
				lastY = y;
			}
			if (inDragSequence) {
				if (isMouseLocked()) {
					touchDX = (float) mouseEvent.getMovementX();
					touchDY = (float) mouseEvent.getMovementY();
				} else {
					touchDX = x - lastX;
					touchDY = y - lastY;
				}
			}
			dispatch(new MouseMake.ButtonEvent(0, game.time(), x, y, -1, false), mouseEvent);
			lastX = x;
			lastY = y;
			lastMousePt.set(x, y);
		} else if (type.equals("wheel")) {
			WheelEvent wheel = (WheelEvent) e;
			wheelDelta = getMouseWheelVelocity(wheel);
			dispatch(new MouseMake.ButtonEvent(0, game.time(), lastMousePt.x, lastMousePt.y, wheel.getButton(), true),
					wheel);
		} else if (type.equals("touchstart")) {
			TouchEvent touchEvent = (TouchEvent) e;
			inTouchSequence = true;
			dispatch(toTouchEvents(TouchMake.Event.Kind.START, touchEvent), touchEvent);
			e.preventDefault();
		}
		if (type.equals("touchmove")) {
			TouchEvent touchEvent = (TouchEvent) e;
			if (inTouchSequence) {
				dispatch(toTouchEvents(TouchMake.Event.Kind.MOVE, touchEvent), touchEvent);
			}
			e.preventDefault();
		}
		if (type.equals("touchcancel") || type.equals("touchend")) {
			TouchEvent touchEvent = (TouchEvent) e;
			if (inTouchSequence) {
				dispatch(toTouchEvents(
						type.equals("touchcancel") ? TouchMake.Event.Kind.CANCEL : TouchMake.Event.Kind.END,
						touchEvent), touchEvent);
				if (touchEvent.getTouches().getLength() == 0) {
					inTouchSequence = false;
				}
			}
			e.preventDefault();
		}
	}

	private TouchMake.Event[] toTouchEvents(TouchMake.Event.Kind kind, TouchEvent nevent) {
		JSArrayReader<Touch> nativeTouches = nevent.getChangedTouches();
		int nativeTouchesLen = nativeTouches.getLength();
		TouchMake.Event[] touches = new TouchMake.Event[nativeTouchesLen];
		double time = game.time();
		for (int t = 0; t < nativeTouchesLen; t++) {
			Touch touch = nativeTouches.get(t);
			float x = getRelativeX(touch, rootElement);
			float y = getRelativeY(touch, rootElement);
			int id = touch.getIdentifier();
			touches[t] = new TouchMake.Event(0, time, x, y, kind, id);
		}
		return touches;
	}

	private void handleKeyboardEvents(Event e) {
		String type = e.getType();
		if (type.equals("keydown") && hasFocus) {
			KeyboardEvent keyboardEvent = (KeyboardEvent) e;
			int keyCode = keyForCode(keyboardEvent.getKeyCode());
			char keyChar = 0;
			switch (keyCode) {
			case SysKey.DEL:
				keyChar = 8;
				break;
			case SysKey.FORWARD_DEL:
				keyChar = 127;
				break;
			}
			if (isCatchKey(keyCode)) {
				e.preventDefault();
			}
			if (keyCode == SysKey.DEL || keyCode == SysKey.FORWARD_DEL) {
				e.preventDefault();
			}
			dispatch(new KeyMake.KeyEvent(0, game.time(), keyChar, keyCode, true), keyboardEvent);
			if (keyCode == SysKey.TAB) {
				e.preventDefault();
				e.stopPropagation();
			}
		} else if (type.equals("keypress") && hasFocus) {
			KeyboardEvent keyboardEvent = (KeyboardEvent) e;
			int keyCode = keyForCode(keyboardEvent.getKeyCode());
			char ch = (char) keyboardEvent.getCharCode();
			dispatch(new KeyMake.KeyEvent(0, game.time(), ch, keyCode, true), keyboardEvent);
			if (ch == '\t') {
				e.preventDefault();
				e.stopPropagation();
			}
		} else if (type.equals("keyup") && hasFocus) {
			KeyboardEvent keyboardEvent = (KeyboardEvent) e;
			int code = keyForCode(keyboardEvent.getKeyCode());
			if (isCatchKey(code)) {
				e.preventDefault();
			}
			int key = keyForCode(keyboardEvent.getKeyCode());
			char ch = (char) keyboardEvent.getCharCode();
			dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false), keyboardEvent);
			if (code == SysKey.TAB) {
				e.preventDefault();
				e.stopPropagation();
			}
		}
	}

	void handleRequestsInUserEventContext() {
		if (isRequestingMouseLock && !isMouseLocked()) {
			Loon.requestMouseLockImplJSNI(rootElement);
		}
	}

	private int mods(Event event) {
		return modifierFlags(Loon.eventGetAltKeyJSNI(event), Loon.eventGetCtrlKeyJSNI(event),
				Loon.eventGetMetaKeyJSNI(event), Loon.eventGetShiftKeyJSNI(event));
	}

	private int mods(MouseEvent event) {
		return modifierFlags(event.getAltKey(), event.getCtrlKey(), event.getMetaKey(), event.getShiftKey());
	}

	private void dispatch(KeyMake.Event event, KeyboardEvent nevent) {
		try {
			event.setFlag(mods(nevent));
			game.dispatchEvent(keyboardEvents, event);
		} finally {
			if (event.isSet(loon.events.Event.F_PREVENT_DEFAULT)) {
				nevent.preventDefault();
			}
		}
	}

	private void dispatch(MouseMake.Event event, MouseEvent nevent) {
		try {
			event.setFlag(mods(nevent));
			game.dispatchEvent(mouseEvents, event);
		} finally {
			if (event.isSet(loon.events.Event.F_PREVENT_DEFAULT)) {
				nevent.preventDefault();
			}
		}
	}

	private void dispatch(TouchMake.Event[] events, TouchEvent nevent) {
		try {
			game.dispatchEvent(touchEvents, events);
		} finally {
			for (TouchMake.Event event : events) {
				if (event.isSet(loon.events.Event.F_PREVENT_DEFAULT))
					nevent.preventDefault();
			}
		}
	}

	private Element getCompatMode(HTMLDocument target) {
		DocumentExt doc = (DocumentExt) target;
		String compatMode = doc.getCompatMode();
		boolean isComp = compatMode.equals("CSS1Compat");
		Element element = isComp ? target.getDocumentElement() : (Element) target;
		return element;
	}

	private int getScrollTop(Element target) {
		ElementExt elem = (ElementExt) target;
		int val = elem.getScrollTop();
		return (int) val;
	}

	private int getScrollTop(HTMLDocument target) {
		Element element = getCompatMode(target);
		return getScrollTop(element);
	}

	private int getScrollLeft(Element target) {
		ElementExt elem = (ElementExt) target;
		int val = elem.getScrollLeft();
		return (int) val;
	}

	private int getScrollLeft(HTMLDocument target) {
		Element element = getCompatMode(target);
		return getScrollLeft(element);
	}

	private int getRelativeX(HTMLCanvasElement target, Touch touch) {
		return (int) (touch.getClientX() - getAbsoluteLeft(target) + getScrollLeft(target)
				+ getScrollLeft(target.getOwnerDocument()));
	}

	private int getRelativeY(HTMLCanvasElement target, Touch touch) {
		return (int) (touch.getClientY() - getAbsoluteTop(target) + getScrollTop(target)
				+ getScrollTop(target.getOwnerDocument()));
	}

	protected int getRelativeX(MouseEvent e, HTMLCanvasElement target) {
		return Math.round((e.getClientX() - getAbsoluteLeft(target) + getScrollLeft(target)
				+ getScrollLeft(target.getOwnerDocument())));
	}

	protected int getRelativeY(MouseEvent e, HTMLCanvasElement target) {
		return Math.round((e.getClientY() - getAbsoluteTop(target) + getScrollTop(target)
				+ getScrollTop(target.getOwnerDocument())));
	}

	protected int getRelativeX(Touch touch, HTMLCanvasElement target) {
		return Math.round(getRelativeX(target, touch));
	}

	protected int getRelativeY(Touch touch, HTMLCanvasElement target) {
		return Math.round(getRelativeY(target, touch));
	}

	private int getAbsoluteTop(HTMLCanvasElement target) {
		return (int) getSubPixelAbsoluteTop(target);
	}

	private double getSubPixelAbsoluteTop(HTMLElement elem) {
		float top = 0;
		HTMLElementExt curr = (HTMLElementExt) elem;
		while (curr.getOffsetParent() != null) {
			top -= curr.getScrollTop();
			curr = (HTMLElementExt) curr.getParentNode();
		}
		while (elem != null) {
			top += elem.getOffsetTop();
			elem = curr.getOffsetParent();
		}
		return top;
	}

	private int getAbsoluteLeft(HTMLCanvasElement target) {
		return (int) getSubPixelAbsoluteLeft(target);
	}

	private double getSubPixelAbsoluteLeft(HTMLElement elem) {
		float left = 0;

		HTMLElementExt curr = (HTMLElementExt) elem;

		while (curr.getOffsetParent() != null) {
			left -= curr.getScrollLeft();
			curr = (HTMLElementExt) curr.getParentNode();
		}

		while (elem != null) {
			left += elem.getOffsetLeft();
			elem = curr.getOffsetParent();
		}
		return left;
	}

	private static float getMouseWheelVelocity(WheelEvent event) {
		double deltaY = event.getDeltaY();
		int deltaMode = event.getDeltaMode();
		if (deltaMode == 2) {
			return 0;
		}
		float delta = 0;
		if (deltaY < 0) {
			delta = -1;
		} else if (deltaY > 0) {
			delta = 1f;
		}
		return delta;
	}

	public float getTouchDX() {
		return touchDX;
	}

	public float getTouchDY() {
		return touchDY;
	}

	@Override
	public void setMouseLocked(boolean locked) {
		if (locked) {
			if (hasMouseLock()) {
				isRequestingMouseLock = true;
				game.log().debug("Requesting mouse lock (supported)");
			} else {
				game.log().debug("Requesting mouse lock -- but unsupported");
			}
		} else {
			game.log().debug("Requesting mouse unlock");
			if (hasMouseLock()) {
				isRequestingMouseLock = false;
				Loon.unlockImpl();
			}
		}
	}

	void emitFakeMouseUp() {
		mouseEvents.emit(new MouseMake.ButtonEvent(0, game.time(), 0, 0, SysTouch.LEFT, false));
	}

	@Override
	public boolean isMouseLocked() {
		return Loon.isMouseLockedJSNI();
	}

	@Override
	public boolean hasTouch() {
		return Loon.hasTouchJSNI();
	}

	@Override
	public void callback(LObject<?> o) {
	}
}
