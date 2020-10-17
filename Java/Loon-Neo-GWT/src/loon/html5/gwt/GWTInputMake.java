/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt;

import loon.LObject;
import loon.events.Event;
import loon.events.InputMake;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.TouchMake;
import loon.geom.Vector2f;
import loon.jni.EventHandler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;

public class GWTInputMake extends InputMake {

	private final GWTGame game;
	private final Element rootElement;

	private final Vector2f lastMousePt = new Vector2f();

	private boolean inDragSequence = false;
	private boolean isRequestingMouseLock;

	private boolean inTouchSequence = false;

	private float touchDX = -1, touchDY = -1;

	public float getTouchDX() {
		return touchDX;
	}

	public float getTouchDY() {
		return touchDY;
	}

	public GWTInputMake(GWTGame gwtgame, Element root) {

		this.game = gwtgame;
		this.rootElement = root;

		capturePageEvent("keydown", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				int key = keyForCode(nevent.getKeyCode());
				char ch = (char) nevent.getCharCode();
				dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, true),
						nevent);
			}
		});
		capturePageEvent("keypress", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				int key = keyForCode(nevent.getKeyCode());
				char ch = (char) nevent.getCharCode();
				dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, true),
						nevent);
			}
		});
		capturePageEvent("keyup", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				int key = keyForCode(nevent.getKeyCode());
				char ch = (char) nevent.getCharCode();
				dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false),
						nevent);
			}
		});

		abstract class XYEventHandler implements EventHandler {
			public void handleEvent(NativeEvent ev) {
				handleEvent(ev, getRelativeX(ev, rootElement),
						getRelativeY(ev, rootElement));
			}

			public abstract void handleEvent(NativeEvent ev, float x, float y);
		}

		abstract class MoveEventHandler extends XYEventHandler {

			private float lastX = -1, lastY = -1;

			@Override
			public void handleEvent(NativeEvent ev, float x, float y) {
				if (lastX == -1) {
					lastX = x;
					lastY = y;
				}

				if (inDragSequence == wantDragSequence()) {
					if (isMouseLocked()) {
						touchDX = getMovementX(ev);
						touchDY = getMovementY(ev);
					} else {
						touchDX = x - lastX;
						touchDY = y - lastY;
					}
				}

				dispatch(new MouseMake.ButtonEvent(0, game.time(), x, y, -1,
						false), ev);

				lastX = x;
				lastY = y;
				lastMousePt.set(x, y);
			}

			protected abstract boolean wantDragSequence();
		}

		addEventListener(Document.get(), "contextmenu", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent evt) {
				evt.preventDefault();
				evt.stopPropagation();
			}
		}, false);

		captureEvent(rootElement, "mousedown", new XYEventHandler() {
			@Override
			public void handleEvent(NativeEvent ev, float x, float y) {

				inDragSequence = true;
				int btn = getMouseButton(ev);
				if (btn != -1) {
					dispatch(new MouseMake.ButtonEvent(0, game.time(), x, y,
							btn, true), ev);
				}
			}
		});

		capturePageEvent("mouseup", new XYEventHandler() {
			@Override
			public void handleEvent(NativeEvent ev, float x, float y) {
				if (inDragSequence) {
					inDragSequence = false;
					int btn = getMouseButton(ev);
					if (btn != -1) {
						dispatch(new MouseMake.ButtonEvent(0, game.time(), x,
								y, btn, false), ev);
					}
				}
				handleRequestsInUserEventContext();
			}
		});

		capturePageEvent("mousemove", new MoveEventHandler() {
			@Override
			protected boolean wantDragSequence() {
				return true;
			}
		});

		captureEvent(rootElement, "mousemove", new MoveEventHandler() {
			@Override
			protected boolean wantDragSequence() {
				return false;
			}
		});

		captureEvent(rootElement, getMouseWheelEvent(), new EventHandler() {
			@Override
			public void handleEvent(NativeEvent ev) {
				dispatch(new MouseMake.ButtonEvent(0, game.time(),
						lastMousePt.x, lastMousePt.y, ev.getButton(), true), ev);
			}
		});

		captureEvent(rootElement, "touchstart", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				inTouchSequence = true;
				dispatch(toTouchEvents(TouchMake.Event.Kind.START, nevent),
						nevent);
			}
		});

		capturePageEvent("touchmove", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				if (inTouchSequence)
					dispatch(toTouchEvents(TouchMake.Event.Kind.MOVE, nevent),
							nevent);
			}
		});

		capturePageEvent("touchend", new EventHandler() {
			@Override
			public void handleEvent(NativeEvent nevent) {
				if (inTouchSequence) {
					dispatch(toTouchEvents(TouchMake.Event.Kind.END, nevent),
							nevent);
					if (nevent.getTouches().length() == 0) {
						inTouchSequence = false;
					}
				}
			}
		});
	}

	@Override
	public boolean hasHardwareKeyboard() {
		return true;
	}

	@Override
	public native boolean hasTouch() /*-{
										return ('ontouchstart' in $doc.documentElement)
										|| ($wnd.navigator.userAgent.match(/ipad|iphone|android/i) != null);
										}-*/;

	@Override
	public native boolean hasMouse() /*-{
										return ('onmousedown' in $doc.documentElement)
										&& ($wnd.navigator.userAgent.match(/ipad|iphone|android/i) == null);
										}-*/;

	@Override
	public native boolean hasMouseLock() /*-{
											return !!($doc.body.requestPointerLock
											|| $doc.body.webkitRequestPointerLock || $doc.body.mozRequestPointerLock);
											}-*/;

	void emitFakeMouseUp() {
		mouseEvents.emit(new MouseMake.ButtonEvent(0, game.time(), 0, 0,
				SysTouch.LEFT, false));
	}

	@Override
	public native boolean isMouseLocked() /*-{
											return !!($doc.pointerLockElement || $doc.webkitPointerLockElement || $doc.mozPointerLockElement);
											}-*/;

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
				unlockImpl();
			}
		}
	}

	static class EventCloseHandler implements HandlerRegistration {
		private final JavaScriptObject target;
		private final String name;
		private final boolean capture;
		private JavaScriptObject listener;

		EventCloseHandler(JavaScriptObject target, String name,
				EventHandler eventHandler, boolean capture) {
			this.target = target;
			this.name = name;
			this.capture = capture;
			addEventListener(this, target, name, eventHandler, capture);
		}

		void setListener(JavaScriptObject listener) {
			this.listener = listener;
		}

		@Override
		public void removeHandler() {
			removeEventListener(target, name, listener, capture);
		}

		private native void addEventListener(EventCloseHandler closeHandler,
				JavaScriptObject target, String name, EventHandler handler,
				boolean capture) /*-{
									var listener = function(e) {
									handler.@loon.jni.EventHandler::handleEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
									};
									target.addEventListener(name, listener, capture);
									closeHandler.@loon.html5.gwt.GWTInputMake.EventCloseHandler::setListener(Lcom/google/gwt/core/client/JavaScriptObject;)(listener);
									}-*/;

		private native void removeEventListener(JavaScriptObject target,
				String name, JavaScriptObject listener, boolean capture)/*-{
																		target.removeEventListener(name, listener, capture);
																		}-*/;
	}

	static HandlerRegistration addEventListener(JavaScriptObject target,
			String name, EventHandler handler, boolean capture) {
		return new EventCloseHandler(target, name, handler, capture);
	};

	static HandlerRegistration capturePageEvent(String name,
			EventHandler handler) {
		return addEventListener(Document.get(), name, handler, true);
	}

	static HandlerRegistration captureEvent(Element target, String name,
			EventHandler handler) {
		return addEventListener(target, name, handler, true);
	}

	static float getRelativeX(NativeEvent e, Element target) {
		return (e.getClientX() - target.getAbsoluteLeft()
				+ target.getScrollLeft() + target.getOwnerDocument()
				.getScrollLeft()) / GWTGraphics.experimentalScale;
	}

	static float getRelativeY(NativeEvent e, Element target) {
		return (e.getClientY() - target.getAbsoluteTop()
				+ target.getScrollTop() + target.getOwnerDocument()
				.getScrollTop()) / GWTGraphics.experimentalScale;
	}

	void handleRequestsInUserEventContext() {
		if (isRequestingMouseLock && !isMouseLocked()) {
			requestMouseLockImpl(rootElement);
		}
	}

	private int mods(NativeEvent event) {
		return modifierFlags(event.getAltKey(), event.getCtrlKey(),
				event.getMetaKey(), event.getShiftKey());
	}

	private void dispatch(KeyMake.Event event, NativeEvent nevent) {
		try {
			event.setFlag(mods(nevent));
			game.dispatchEvent(keyboardEvents, event);
		} finally {
			if (event.isSet(Event.F_PREVENT_DEFAULT)) {
				nevent.preventDefault();
			}
		}
	}

	private void dispatch(MouseMake.Event event, NativeEvent nevent) {
		try {
			event.setFlag(mods(nevent));
			game.dispatchEvent(mouseEvents, event);
		} finally {
			if (event.isSet(Event.F_PREVENT_DEFAULT)) {
				nevent.preventDefault();
			}
		}
	}

	private void dispatch(TouchMake.Event[] events, NativeEvent nevent) {
		try {
			game.dispatchEvent(touchEvents, events);
		} finally {
			for (TouchMake.Event event : events) {
				if (event.isSet(Event.F_PREVENT_DEFAULT))
					nevent.preventDefault();
			}
		}
	}

	private native int getMovementX(NativeEvent nevent) /*-{
														return nevent.webkitMovementX;
														}-*/;

	private native int getMovementY(NativeEvent nevent) /*-{
														return nevent.webkitMovementY;
														}-*/;

	native void requestMouseLockImpl(Element element) /*-{
														element.requestPointerLock = (element.requestPointerLock
														|| element.webkitRequestPointerLock || element.mozRequestPointerLock);
														if (element.requestPointerLock)
														element.requestPointerLock();
														}-*/;

	private static native float getMouseWheelVelocity(NativeEvent evt) /*-{
																		var delta = 0.0;
																		var agentInfo = @loon.html5.gwt.GWTGame::agentInfo;

																		if (agentInfo.isFirefox) {
																		if (agentInfo.isMacOS) {
																		delta = 1.0 * evt.detail;
																		} else {
																		delta = 1.0 * evt.detail / 3;
																		}
																		} else if (agentInfo.isOpera) {
																		if (agentInfo.isLinux) {
																		delta = -1.0 * evt.wheelDelta / 80;
																		} else {
																		// on mac
																		delta = -1.0 * evt.wheelDelta / 40;
																		}
																		} else if (agentInfo.isChrome || agentInfo.isSafari || agentInfo.isIE) {
																		delta = -1.0 * evt.wheelDelta / 120;
																		// handle touchpad for chrome
																		if (Math.abs(delta) < 1) {
																		if (agentInfo.isWindows) {
																		delta = -1.0 * evt.wheelDelta;
																		} else if (agentInfo.isMacOS) {
																		delta = -1.0 * evt.wheelDelta / 3;
																		}
																		}
																		}
																		return delta;
																		}-*/;

	protected static native String getMouseWheelEvent() /*-{
														if (navigator.userAgent.toLowerCase().indexOf('firefox') != -1) {
														return "DOMMouseScroll";
														} else {
														return "mousewheel";
														}
														}-*/;

	protected static int getMouseButton(NativeEvent evt) {
		switch (evt.getButton()) {
		case (NativeEvent.BUTTON_LEFT):
			return SysTouch.LEFT;
		case (NativeEvent.BUTTON_MIDDLE):
			return SysTouch.MIDDLE;
		case (NativeEvent.BUTTON_RIGHT):
			return SysTouch.RIGHT;
		default:
			return -1;
		}
	}

	private native void unlockImpl() /*-{
										$doc.exitPointerLock = $doc.exitPointerLock
										|| $doc.webkitExitPointerLock || $doc.mozExitPointerLock;
										$doc.exitPointerLock && $doc.exitPointerLock();
										}-*/;

	private TouchMake.Event[] toTouchEvents(TouchMake.Event.Kind kind,
			NativeEvent nevent) {
		JsArray<com.google.gwt.dom.client.Touch> nativeTouches = nevent
				.getChangedTouches();
		int nativeTouchesLen = nativeTouches.length();
		TouchMake.Event[] touches = new TouchMake.Event[nativeTouchesLen];
		double time = game.time();
		for (int t = 0; t < nativeTouchesLen; t++) {
			com.google.gwt.dom.client.Touch touch = nativeTouches.get(t);
			float x = touch.getRelativeX(rootElement);
			float y = touch.getRelativeY(rootElement);
			int id = getTouchIdentifier(nevent, t);
			touches[t] = new TouchMake.Event(0, time, x, y, kind, id);
		}
		return touches;
	}

	private static native int getTouchIdentifier(NativeEvent evt, int index) /*-{
																				return evt.changedTouches[index].identifier || 0;
																				}-*/;

	private static int keyForCode(int keyCode) {
		switch (keyCode) {
		case KeyCodes.KEY_ALT:
			return SysKey.ALT_LEFT;
		case KeyCodes.KEY_BACKSPACE:
			return SysKey.BACKSPACE;
		case KeyCodes.KEY_DELETE:
			return SysKey.DEL;
		case KeyCodes.KEY_DOWN:
			return SysKey.DOWN;
		case KeyCodes.KEY_END:
			return SysKey.END;
		case KeyCodes.KEY_ENTER:
			return SysKey.ENTER;
		case KeyCodes.KEY_ESCAPE:
			return SysKey.ESCAPE;
		case KeyCodes.KEY_HOME:
			return SysKey.HOME;
		case KeyCodes.KEY_LEFT:
			return SysKey.LEFT;
		case KeyCodes.KEY_PAGEDOWN:
			return SysKey.PAGE_DOWN;
		case KeyCodes.KEY_PAGEUP:
			return SysKey.PAGE_UP;
		case KeyCodes.KEY_RIGHT:
			return SysKey.RIGHT;
		case KeyCodes.KEY_SHIFT:
			return SysKey.SHIFT_LEFT;
		case KeyCodes.KEY_TAB:
			return SysKey.TAB;
		case KeyCodes.KEY_UP:
			return SysKey.UP;
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
		case KEY_ADD:
			return SysKey.PLUS;
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
			return SysKey.GRAVE;
		case KEY_OPEN_BRACKET:
			return SysKey.LEFT_BRACKET;
		case KEY_BACKSLASH:
			return SysKey.BACKSLASH;
		case KEY_CLOSE_BRACKET:
			return SysKey.RIGHT_BRACKET;
		default:
			return SysKey.UNKNOWN;
		}
	}

	private static final int KEY_PAUSE = 19;
	private static final int KEY_CAPS_LOCK = 20;
	private static final int KEY_SPACE = 32;
	private static final int KEY_INSERT = 45;
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
	private static final int KEY_A = 65;
	private static final int KEY_B = 66;
	private static final int KEY_C = 67;
	private static final int KEY_D = 68;
	private static final int KEY_E = 69;
	private static final int KEY_F = 70;
	private static final int KEY_G = 71;
	private static final int KEY_H = 72;
	private static final int KEY_I = 73;
	private static final int KEY_J = 74;
	private static final int KEY_K = 75;
	private static final int KEY_L = 76;
	private static final int KEY_M = 77;
	private static final int KEY_N = 78;
	private static final int KEY_O = 79;
	private static final int KEY_P = 80;
	private static final int KEY_Q = 81;
	private static final int KEY_R = 82;
	private static final int KEY_S = 83;
	private static final int KEY_T = 84;
	private static final int KEY_U = 85;
	private static final int KEY_V = 86;
	private static final int KEY_W = 87;
	private static final int KEY_X = 88;
	private static final int KEY_Y = 89;
	private static final int KEY_Z = 90;
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
	private static final int KEY_F1 = 112;
	private static final int KEY_F2 = 113;
	private static final int KEY_F3 = 114;
	private static final int KEY_F4 = 115;
	private static final int KEY_F5 = 116;
	private static final int KEY_F6 = 117;
	private static final int KEY_F7 = 118;
	private static final int KEY_F8 = 119;
	private static final int KEY_F9 = 120;
	private static final int KEY_F10 = 121;
	private static final int KEY_F11 = 122;
	private static final int KEY_F12 = 123;
	private static final int KEY_NUM_LOCK = 144;
	private static final int KEY_SCROLL_LOCK = 145;
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

	@Override
	public void callback(LObject<?> o) {

	}
}
