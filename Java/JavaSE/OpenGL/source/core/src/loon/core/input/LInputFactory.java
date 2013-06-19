package loon.core.input;

import loon.core.LSystem;
import loon.core.event.ActionKey;
import loon.core.geom.Vector2f;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.utils.collection.IntArray;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
public class LInputFactory {

	private static boolean useTouchCollection = false;

	public static void startTouchCollection() {
		useTouchCollection = true;
	}

	public static void stopTouchCollection() {
		useTouchCollection = false;
	}

	private static LTouchCollection touchCollection = new LTouchCollection();

	public static LTouchCollection getTouchState() {
		LTouchCollection result = new LTouchCollection(touchCollection);
		touchCollection.update();
		return result;
	}

	public static void resetTouch() {
		touchCollection.clear();
	}

	final static IntArray keys = new IntArray();

	private final LProcess handler;

	private final static LTouch finalTouch = new LTouch();

	private final static LKey finalKey = new LKey();

	private char lastKeyCharPressed;

	private int buttons;

	static boolean isDraging;

	public static class Touch {

		public static final int TOUCH_DOWN = 0;

		public static final int TOUCH_UP = 1;

		public static final int TOUCH_MOVE = 2;

		public static final int TOUCH_DRAG = 3;

		public static final int LEFT = 0;

		public static final int RIGHT = 1;

		public static final int MIDDLE = 2;

		private static final Vector2f location = new Vector2f();

		public static Vector2f getLocation() {
			location.set(finalTouch.x, finalTouch.y);
			return location;
		}

		public static int getButton() {
			return finalTouch.button;
		}

		public static int getPointer() {
			return finalTouch.pointer;
		}

		public static int getType() {
			return finalTouch.type;
		}

		public static int x() {
			return (int) finalTouch.x;
		}

		public static int y() {
			return (int) finalTouch.y;
		}

		public static float getX() {
			return finalTouch.x;
		}

		public static float getY() {
			return finalTouch.y;
		}

		public static boolean isDown() {
			return finalTouch.isDown();
		}

		public static boolean isUp() {
			return finalTouch.isUp();
		}

		public static boolean isMove() {
			return finalTouch.isMove();
		}

		public static boolean isDrag() {
			return isDraging;
		}

		public static boolean isLeft() {
			return finalTouch.isLeft();
		}

		public static boolean isMiddle() {
			return finalTouch.isMiddle();
		}

		public static boolean isRight() {
			return finalTouch.isRight();
		}
	}

	public static class Key {

		public static final int KEY_DOWN = 0;

		public static final int KEY_UP = 1;

		public static final int KEY_TYPED = 2;

		public static final int ANY_KEY = -1;

		public static final int NUM_0 = 7;

		public static final int NUM_1 = 8;

		public static final int NUM_2 = 9;

		public static final int NUM_3 = 10;

		public static final int NUM_4 = 11;

		public static final int NUM_5 = 12;

		public static final int NUM_6 = 13;

		public static final int NUM_7 = 14;

		public static final int NUM_8 = 15;

		public static final int NUM_9 = 16;

		public static final int A = 29;

		public static final int ALT_LEFT = 57;

		public static final int ALT_RIGHT = 58;

		public static final int APOSTROPHE = 75;

		public static final int AT = 77;

		public static final int B = 30;

		public static final int BACK = 4;

		public static final int BACKSLASH = 73;

		public static final int C = 31;

		public static final int CALL = 5;

		public static final int CAMERA = 27;

		public static final int CLEAR = 28;

		public static final int COMMA = 55;

		public static final int D = 32;

		public static final int DEL = 67;

		public static final int BACKSPACE = 67;

		public static final int FORWARD_DEL = 112;

		public static final int DPAD_CENTER = 23;

		public static final int DPAD_DOWN = 20;

		public static final int DPAD_LEFT = 21;

		public static final int DPAD_RIGHT = 22;

		public static final int DPAD_UP = 19;

		public static final int CENTER = 23;

		public static final int DOWN = 20;

		public static final int LEFT = 21;

		public static final int RIGHT = 22;

		public static final int UP = 19;

		public static final int E = 33;

		public static final int ENDCALL = 6;

		public static final int ENTER = 66;

		public static final int ENVELOPE = 65;

		public static final int EQUALS = 70;

		public static final int EXPLORER = 64;

		public static final int F = 34;

		public static final int FOCUS = 80;

		public static final int G = 35;

		public static final int GRAVE = 68;

		public static final int H = 36;

		public static final int HEADSETHOOK = 79;

		public static final int HOME = 3;

		public static final int I = 37;

		public static final int J = 38;

		public static final int K = 39;

		public static final int L = 40;

		public static final int LEFT_BRACKET = 71;

		public static final int M = 41;

		public static final int MEDIA_FAST_FORWARD = 90;

		public static final int MEDIA_NEXT = 87;

		public static final int MEDIA_PLAY_PAUSE = 85;

		public static final int MEDIA_PREVIOUS = 88;

		public static final int MEDIA_REWIND = 89;

		public static final int MEDIA_STOP = 86;

		public static final int MENU = 82;

		public static final int MINUS = 69;

		public static final int MUTE = 91;

		public static final int N = 42;

		public static final int NOTIFICATION = 83;

		public static final int NUM = 78;

		public static final int O = 43;

		public static final int P = 44;

		public static final int PERIOD = 56;

		public static final int PLUS = 81;

		public static final int POUND = 18;

		public static final int POWER = 26;

		public static final int Q = 45;

		public static final int R = 46;

		public static final int RIGHT_BRACKET = 72;

		public static final int S = 47;

		public static final int SEARCH = 84;

		public static final int SEMICOLON = 74;

		public static final int SHIFT_LEFT = 59;

		public static final int SHIFT_RIGHT = 60;

		public static final int SLASH = 76;

		public static final int SOFT_LEFT = 1;

		public static final int SOFT_RIGHT = 2;

		public static final int SPACE = 62;

		public static final int STAR = 17;

		public static final int SYM = 63;

		public static final int T = 48;

		public static final int TAB = 61;

		public static final int U = 49;

		public static final int UNKNOWN = 0;

		public static final int V = 50;

		public static final int VOLUME_DOWN = 25;

		public static final int VOLUME_UP = 24;

		public static final int W = 51;

		public static final int X = 52;

		public static final int Y = 53;

		public static final int Z = 54;

		public static final int META_ALT_LEFT_ON = 16;

		public static final int META_ALT_ON = 2;

		public static final int META_ALT_RIGHT_ON = 32;

		public static final int META_SHIFT_LEFT_ON = 64;

		public static final int META_SHIFT_ON = 1;

		public static final int META_SHIFT_RIGHT_ON = 128;

		public static final int META_SYM_ON = 4;

		public static final int CONTROL_LEFT = 129;

		public static final int CONTROL_RIGHT = 130;

		public static final int ESCAPE = 131;

		public static final int END = 132;

		public static final int INSERT = 133;

		public static final int PAGE_UP = 92;

		public static final int PAGE_DOWN = 93;

		public static final int PICTSYMBOLS = 94;

		public static final int SWITCH_CHARSET = 95;

		public static final int BUTTON_A = 96;

		public static final int BUTTON_B = 97;

		public static final int BUTTON_C = 98;

		public static final int BUTTON_X = 99;

		public static final int BUTTON_Y = 100;

		public static final int BUTTON_Z = 101;

		public static final int BUTTON_L1 = 102;

		public static final int BUTTON_R1 = 103;

		public static final int BUTTON_L2 = 104;

		public static final int BUTTON_R2 = 105;

		public static final int BUTTON_THUMBL = 106;

		public static final int BUTTON_THUMBR = 107;

		public static final int BUTTON_START = 108;

		public static final int BUTTON_SELECT = 109;

		public static final int BUTTON_MODE = 110;

		public static final int BUTTON_CIRCLE = 255;

		public static char getKeyChar() {
			return finalKey.keyChar;
		}

		public static int getKeyCode() {
			return finalKey.keyCode;
		}

		public static int getType() {
			return finalKey.keyCode;
		}

		public static boolean isDown() {
			return finalKey.isDown();
		}

		public static boolean isUp() {
			return finalKey.isUp();
		}

		public static void clear() {
			keys.clear();
		}

		public static void addKey(int key) {
			keys.add(key);
		}

		public static void removeKey(int key) {
			keys.removeValue(key);
		}

		public static boolean isKeyPressed(int key) {
			if (key == Key.ANY_KEY) {
				return keys.size > 0 && only_key.isPressed();
			} else {
				return keys.contains(key) && only_key.isPressed();
			}
		}

		public static boolean isKeyRelease(int key) {
			if (key == Key.ANY_KEY) {
				return keys.size > 0 && !only_key.isPressed();
			} else {
				return keys.contains(key) && !only_key.isPressed();
			}
		}
	}

	public LInputFactory(LProcess handler) {
		Keyboard.enableRepeatEvents(false);
		this.handler = handler;
	}

	private LTimer lock = new LTimer(10);

	public void runTimer(LTimerContext timerContext) {
		if (handler == null) {
			return;
		}
		if (lock.action(timerContext)) {
			updateMouse();
			updateKeyboard();
		}
	}

	void updateMouse() {
		if (Mouse.isCreated()) {
			while (Mouse.next()) {
				int touchX = Math.round(Mouse.getEventX() / LSystem.scaleWidth);
				int touchY = Math
						.round(((LSystem.screenRect.height * LSystem.scaleHeight)
								- Mouse.getEventY() - 1)
								/ LSystem.scaleHeight);
				int button = Mouse.getEventButton();
				finalTouch.x = touchX - (handler.getX() / LSystem.scaleWidth);
				finalTouch.y = touchY - (handler.getY() / LSystem.scaleHeight);
				finalTouch.button = toButton(button);
				finalTouch.pointer = 0;
				finalTouch.id = 0;
				if (button == -1) {
					if (buttons > 0) {
						finalTouch.type = Touch.TOUCH_DRAG;
					} else {
						finalTouch.type = Touch.TOUCH_MOVE;
					}
				} else {
					if (Mouse.getEventButtonState()) {
						finalTouch.type = Touch.TOUCH_DOWN;
					} else {
						if (finalTouch.type == Touch.TOUCH_DOWN
								|| finalTouch.type == Touch.TOUCH_DRAG) {
							finalTouch.type = Touch.TOUCH_UP;
						}
					}
				}
				switch (finalTouch.type) {
				case Touch.TOUCH_DOWN:
					if (useTouchCollection) {
						touchCollection.add(finalTouch.id, finalTouch.x,
								finalTouch.y);
					}
					if (handler.emulatorButtons != null
							&& handler.emulatorButtons.isVisible()) {
						handler.emulatorButtons.hit(0, touchX, touchY, false);
					}
					handler.mousePressed(finalTouch);
					buttons++;
					isDraging = false;
					break;
				case Touch.TOUCH_UP:
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Released, finalTouch.x,
								finalTouch.y);
					}
					if (handler.emulatorButtons != null
							&& handler.emulatorButtons.isVisible()) {
						handler.emulatorButtons.unhit(0, touchX, touchY);
					}
					handler.mouseReleased(finalTouch);
					buttons = 0;
					isDraging = false;
					break;
				case Touch.TOUCH_MOVE:
					if (!isDraging) {
						if (useTouchCollection) {
							touchCollection.update(finalTouch.id,
									LTouchLocationState.Dragged, finalTouch.x,
									finalTouch.y);
						}
						handler.mouseMoved(finalTouch);
					}
					break;
				case Touch.TOUCH_DRAG:
					if (handler.emulatorButtons != null
							&& handler.emulatorButtons.isVisible()) {
						handler.emulatorButtons.hit(0, touchX, touchY, true);
					}
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Dragged, finalTouch.x,
								finalTouch.y);
					}
					handler.mouseDragged(finalTouch);
					isDraging = true;
					break;
				default:
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Invalid, finalTouch.x,
								finalTouch.y);
					}
					break;
				}
			}

		}
	}

	private final static ActionKey only_key = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	public static ActionKey getOnlyKey() {
		return only_key;
	}

	void updateKeyboard() {
		try {
			LSystem.AUTO_REPAINT = false;
			if (lastKeyCharPressed != 0) {
				finalKey.keyCode = 0;
				finalKey.keyChar = lastKeyCharPressed;
				finalKey.type = Key.KEY_TYPED;
			}
			if (Keyboard.isCreated()) {
				while (Keyboard.next()) {
					double time = (double) (Keyboard.getEventNanoseconds() / 1000);
					int keyCode = toKeyCode(Keyboard.getEventKey());
					if (Keyboard.getEventKeyState()) {
						char keyChar = Keyboard.getEventCharacter();
						finalKey.keyCode = keyCode;
						finalKey.keyChar = Keyboard.getEventCharacter();
						finalKey.type = Key.KEY_DOWN;
						finalKey.timer = time;
						lastKeyCharPressed = keyChar;
					} else {
						finalKey.keyCode = keyCode;
						finalKey.keyChar = Keyboard.getEventCharacter();
						finalKey.type = Key.KEY_UP;
						finalKey.timer = time;
						lastKeyCharPressed = Keyboard.getEventCharacter();
					}
					switch (finalKey.type) {
					case Key.KEY_DOWN:
						only_key.press();
						handler.keyDown(finalKey);
						keys.add(finalKey.keyCode);
						break;
					case Key.KEY_UP:
						only_key.release();
						handler.keyUp(finalKey);
						keys.removeValue(finalKey.keyCode);
						break;
					case Key.KEY_TYPED:
						only_key.reset();
						handler.keyTyped(finalKey);
					default:
						only_key.reset();
						keys.clear();
					}
				}
			}
		} finally {
			LSystem.AUTO_REPAINT = true;
		}
	}

	public static int toKeyCode(int lwjglKeyCode) {
		switch (lwjglKeyCode) {
		case Keyboard.KEY_0:
			return LInputFactory.Key.NUM_0;
		case Keyboard.KEY_1:
			return LInputFactory.Key.NUM_1;
		case Keyboard.KEY_2:
			return LInputFactory.Key.NUM_2;
		case Keyboard.KEY_3:
			return LInputFactory.Key.NUM_3;
		case Keyboard.KEY_4:
			return LInputFactory.Key.NUM_4;
		case Keyboard.KEY_5:
			return LInputFactory.Key.NUM_5;
		case Keyboard.KEY_6:
			return LInputFactory.Key.NUM_6;
		case Keyboard.KEY_7:
			return LInputFactory.Key.NUM_7;
		case Keyboard.KEY_8:
			return LInputFactory.Key.NUM_8;
		case Keyboard.KEY_9:
			return LInputFactory.Key.NUM_9;
		case Keyboard.KEY_A:
			return LInputFactory.Key.A;
		case Keyboard.KEY_B:
			return LInputFactory.Key.B;
		case Keyboard.KEY_C:
			return LInputFactory.Key.C;
		case Keyboard.KEY_D:
			return LInputFactory.Key.D;
		case Keyboard.KEY_E:
			return LInputFactory.Key.E;
		case Keyboard.KEY_F:
			return LInputFactory.Key.F;
		case Keyboard.KEY_G:
			return LInputFactory.Key.G;
		case Keyboard.KEY_H:
			return LInputFactory.Key.H;
		case Keyboard.KEY_I:
			return LInputFactory.Key.I;
		case Keyboard.KEY_J:
			return LInputFactory.Key.J;
		case Keyboard.KEY_K:
			return LInputFactory.Key.K;
		case Keyboard.KEY_L:
			return LInputFactory.Key.L;
		case Keyboard.KEY_M:
			return LInputFactory.Key.M;
		case Keyboard.KEY_N:
			return LInputFactory.Key.N;
		case Keyboard.KEY_O:
			return LInputFactory.Key.O;
		case Keyboard.KEY_P:
			return LInputFactory.Key.P;
		case Keyboard.KEY_Q:
			return LInputFactory.Key.Q;
		case Keyboard.KEY_R:
			return LInputFactory.Key.R;
		case Keyboard.KEY_S:
			return LInputFactory.Key.S;
		case Keyboard.KEY_T:
			return LInputFactory.Key.T;
		case Keyboard.KEY_U:
			return LInputFactory.Key.U;
		case Keyboard.KEY_V:
			return LInputFactory.Key.V;
		case Keyboard.KEY_W:
			return LInputFactory.Key.W;
		case Keyboard.KEY_X:
			return LInputFactory.Key.X;
		case Keyboard.KEY_Y:
			return LInputFactory.Key.Y;
		case Keyboard.KEY_Z:
			return LInputFactory.Key.Z;
		case Keyboard.KEY_LMETA:
			return LInputFactory.Key.ALT_LEFT;
		case Keyboard.KEY_RMETA:
			return LInputFactory.Key.ALT_RIGHT;
		case Keyboard.KEY_BACKSLASH:
			return LInputFactory.Key.BACKSLASH;
		case Keyboard.KEY_COMMA:
			return LInputFactory.Key.COMMA;
		case Keyboard.KEY_DELETE:
			return LInputFactory.Key.FORWARD_DEL;
		case Keyboard.KEY_LEFT:
			return LInputFactory.Key.DPAD_LEFT;
		case Keyboard.KEY_RIGHT:
			return LInputFactory.Key.DPAD_RIGHT;
		case Keyboard.KEY_UP:
			return LInputFactory.Key.DPAD_UP;
		case Keyboard.KEY_DOWN:
			return LInputFactory.Key.DPAD_DOWN;
		case Keyboard.KEY_RETURN:
			return LInputFactory.Key.ENTER;
		case Keyboard.KEY_HOME:
			return LInputFactory.Key.HOME;
		case Keyboard.KEY_MINUS:
			return LInputFactory.Key.MINUS;
		case Keyboard.KEY_PERIOD:
			return LInputFactory.Key.PERIOD;
		case Keyboard.KEY_ADD:
			return LInputFactory.Key.PLUS;
		case Keyboard.KEY_SEMICOLON:
			return LInputFactory.Key.SEMICOLON;
		case Keyboard.KEY_LSHIFT:
			return LInputFactory.Key.SHIFT_LEFT;
		case Keyboard.KEY_RSHIFT:
			return LInputFactory.Key.SHIFT_RIGHT;
		case Keyboard.KEY_SLASH:
			return LInputFactory.Key.SLASH;
		case Keyboard.KEY_SPACE:
			return LInputFactory.Key.SPACE;
		case Keyboard.KEY_TAB:
			return LInputFactory.Key.TAB;
		case Keyboard.KEY_LCONTROL:
			return LInputFactory.Key.CONTROL_LEFT;
		case Keyboard.KEY_RCONTROL:
			return LInputFactory.Key.CONTROL_RIGHT;
		case Keyboard.KEY_ESCAPE:
			return LInputFactory.Key.ESCAPE;
		case Keyboard.KEY_END:
			return LInputFactory.Key.END;
		case Keyboard.KEY_INSERT:
			return LInputFactory.Key.INSERT;
		case Keyboard.KEY_NUMPAD5:
			return LInputFactory.Key.DPAD_CENTER;
		case Keyboard.KEY_BACK:
			return LInputFactory.Key.DEL;
		default:
			return LInputFactory.Key.UNKNOWN;
		}
	}

	public static int toLwjglKeyCode(int keyCode) {
		switch (keyCode) {
		case LInputFactory.Key.NUM_0:
			return Keyboard.KEY_0;
		case LInputFactory.Key.NUM_1:
			return Keyboard.KEY_1;
		case LInputFactory.Key.NUM_2:
			return Keyboard.KEY_2;
		case LInputFactory.Key.NUM_3:
			return Keyboard.KEY_3;
		case LInputFactory.Key.NUM_4:
			return Keyboard.KEY_4;
		case LInputFactory.Key.NUM_5:
			return Keyboard.KEY_5;
		case LInputFactory.Key.NUM_6:
			return Keyboard.KEY_6;
		case LInputFactory.Key.NUM_7:
			return Keyboard.KEY_7;
		case LInputFactory.Key.NUM_8:
			return Keyboard.KEY_8;
		case LInputFactory.Key.NUM_9:
			return Keyboard.KEY_9;
		case LInputFactory.Key.A:
			return Keyboard.KEY_A;
		case LInputFactory.Key.B:
			return Keyboard.KEY_B;
		case LInputFactory.Key.C:
			return Keyboard.KEY_C;
		case LInputFactory.Key.D:
			return Keyboard.KEY_D;
		case LInputFactory.Key.E:
			return Keyboard.KEY_E;
		case LInputFactory.Key.F:
			return Keyboard.KEY_F;
		case LInputFactory.Key.G:
			return Keyboard.KEY_G;
		case LInputFactory.Key.H:
			return Keyboard.KEY_H;
		case LInputFactory.Key.I:
			return Keyboard.KEY_I;
		case LInputFactory.Key.J:
			return Keyboard.KEY_J;
		case LInputFactory.Key.K:
			return Keyboard.KEY_K;
		case LInputFactory.Key.L:
			return Keyboard.KEY_L;
		case LInputFactory.Key.M:
			return Keyboard.KEY_M;
		case LInputFactory.Key.N:
			return Keyboard.KEY_N;
		case LInputFactory.Key.O:
			return Keyboard.KEY_O;
		case LInputFactory.Key.P:
			return Keyboard.KEY_P;
		case LInputFactory.Key.Q:
			return Keyboard.KEY_Q;
		case LInputFactory.Key.R:
			return Keyboard.KEY_R;
		case LInputFactory.Key.S:
			return Keyboard.KEY_S;
		case LInputFactory.Key.T:
			return Keyboard.KEY_T;
		case LInputFactory.Key.U:
			return Keyboard.KEY_U;
		case LInputFactory.Key.V:
			return Keyboard.KEY_V;
		case LInputFactory.Key.W:
			return Keyboard.KEY_W;
		case LInputFactory.Key.X:
			return Keyboard.KEY_X;
		case LInputFactory.Key.Y:
			return Keyboard.KEY_Y;
		case LInputFactory.Key.Z:
			return Keyboard.KEY_Z;
		case LInputFactory.Key.ALT_LEFT:
			return Keyboard.KEY_LMETA;
		case LInputFactory.Key.ALT_RIGHT:
			return Keyboard.KEY_RMETA;
		case LInputFactory.Key.BACKSLASH:
			return Keyboard.KEY_BACKSLASH;
		case LInputFactory.Key.COMMA:
			return Keyboard.KEY_COMMA;
		case LInputFactory.Key.FORWARD_DEL:
			return Keyboard.KEY_DELETE;
		case LInputFactory.Key.DPAD_LEFT:
			return Keyboard.KEY_LEFT;
		case LInputFactory.Key.DPAD_RIGHT:
			return Keyboard.KEY_RIGHT;
		case LInputFactory.Key.DPAD_UP:
			return Keyboard.KEY_UP;
		case LInputFactory.Key.DPAD_DOWN:
			return Keyboard.KEY_DOWN;
		case LInputFactory.Key.ENTER:
			return Keyboard.KEY_RETURN;
		case LInputFactory.Key.HOME:
			return Keyboard.KEY_HOME;
		case LInputFactory.Key.MINUS:
			return Keyboard.KEY_MINUS;
		case LInputFactory.Key.PERIOD:
			return Keyboard.KEY_PERIOD;
		case LInputFactory.Key.PLUS:
			return Keyboard.KEY_ADD;
		case LInputFactory.Key.SEMICOLON:
			return Keyboard.KEY_SEMICOLON;
		case LInputFactory.Key.SHIFT_LEFT:
			return Keyboard.KEY_LSHIFT;
		case LInputFactory.Key.SHIFT_RIGHT:
			return Keyboard.KEY_RSHIFT;
		case LInputFactory.Key.SLASH:
			return Keyboard.KEY_SLASH;
		case LInputFactory.Key.SPACE:
			return Keyboard.KEY_SPACE;
		case LInputFactory.Key.TAB:
			return Keyboard.KEY_TAB;
		case LInputFactory.Key.DEL:
			return Keyboard.KEY_BACK;
		case LInputFactory.Key.CONTROL_LEFT:
			return Keyboard.KEY_LCONTROL;
		case LInputFactory.Key.CONTROL_RIGHT:
			return Keyboard.KEY_RCONTROL;
		case LInputFactory.Key.ESCAPE:
			return Keyboard.KEY_ESCAPE;
		default:
			return Keyboard.KEY_NONE;
		}
	}

	private int toButton(int button) {
		if (button == 0) {
			return Touch.LEFT;
		}
		if (button == 1) {
			return Touch.RIGHT;
		}
		if (button == 2) {
			return Touch.MIDDLE;
		}
		return Touch.LEFT;
	}
}
