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
package loon;

import loon.core.event.ActionKey;
import loon.core.timer.LTimer;
import loon.core.timer.LTimerContext;
import loon.utils.collection.IntArray;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class JavaSEInputFactory {

	public static void setOnscreenKeyboardVisible(boolean visible) {

	}

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

	final static LTouch finalTouch = new LTouch();

	final static LKey finalKey = new LKey();

	private char lastKeyCharPressed;

	private int buttons;

	static boolean isDraging;

	public JavaSEInputFactory(LProcess handler) {
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

	final static ActionKey only_key = new ActionKey(
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
						finalKey.keyChar = keyChar;
						finalKey.type = Key.KEY_DOWN;
						finalKey.timer = time;
						lastKeyCharPressed = keyChar;
					} else {
						finalKey.keyCode = keyCode;
						finalKey.keyChar = lastKeyCharPressed;
						finalKey.type = Key.KEY_UP;
						finalKey.timer = time;
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
			return Key.NUM_0;
		case Keyboard.KEY_1:
			return Key.NUM_1;
		case Keyboard.KEY_2:
			return Key.NUM_2;
		case Keyboard.KEY_3:
			return Key.NUM_3;
		case Keyboard.KEY_4:
			return Key.NUM_4;
		case Keyboard.KEY_5:
			return Key.NUM_5;
		case Keyboard.KEY_6:
			return Key.NUM_6;
		case Keyboard.KEY_7:
			return Key.NUM_7;
		case Keyboard.KEY_8:
			return Key.NUM_8;
		case Keyboard.KEY_9:
			return Key.NUM_9;
		case Keyboard.KEY_A:
			return Key.A;
		case Keyboard.KEY_B:
			return Key.B;
		case Keyboard.KEY_C:
			return Key.C;
		case Keyboard.KEY_D:
			return Key.D;
		case Keyboard.KEY_E:
			return Key.E;
		case Keyboard.KEY_F:
			return Key.F;
		case Keyboard.KEY_G:
			return Key.G;
		case Keyboard.KEY_H:
			return Key.H;
		case Keyboard.KEY_I:
			return Key.I;
		case Keyboard.KEY_J:
			return Key.J;
		case Keyboard.KEY_K:
			return Key.K;
		case Keyboard.KEY_L:
			return Key.L;
		case Keyboard.KEY_M:
			return Key.M;
		case Keyboard.KEY_N:
			return Key.N;
		case Keyboard.KEY_O:
			return Key.O;
		case Keyboard.KEY_P:
			return Key.P;
		case Keyboard.KEY_Q:
			return Key.Q;
		case Keyboard.KEY_R:
			return Key.R;
		case Keyboard.KEY_S:
			return Key.S;
		case Keyboard.KEY_T:
			return Key.T;
		case Keyboard.KEY_U:
			return Key.U;
		case Keyboard.KEY_V:
			return Key.V;
		case Keyboard.KEY_W:
			return Key.W;
		case Keyboard.KEY_X:
			return Key.X;
		case Keyboard.KEY_Y:
			return Key.Y;
		case Keyboard.KEY_Z:
			return Key.Z;
		case Keyboard.KEY_LMETA:
			return Key.ALT_LEFT;
		case Keyboard.KEY_RMETA:
			return Key.ALT_RIGHT;
		case Keyboard.KEY_BACKSLASH:
			return Key.BACKSLASH;
		case Keyboard.KEY_COMMA:
			return Key.COMMA;
		case Keyboard.KEY_DELETE:
			return Key.FORWARD_DEL;
		case Keyboard.KEY_LEFT:
			return Key.DPAD_LEFT;
		case Keyboard.KEY_RIGHT:
			return Key.DPAD_RIGHT;
		case Keyboard.KEY_UP:
			return Key.DPAD_UP;
		case Keyboard.KEY_DOWN:
			return Key.DPAD_DOWN;
		case Keyboard.KEY_RETURN:
			return Key.ENTER;
		case Keyboard.KEY_HOME:
			return Key.HOME;
		case Keyboard.KEY_MINUS:
			return Key.MINUS;
		case Keyboard.KEY_PERIOD:
			return Key.PERIOD;
		case Keyboard.KEY_ADD:
			return Key.PLUS;
		case Keyboard.KEY_SEMICOLON:
			return Key.SEMICOLON;
		case Keyboard.KEY_LSHIFT:
			return Key.SHIFT_LEFT;
		case Keyboard.KEY_RSHIFT:
			return Key.SHIFT_RIGHT;
		case Keyboard.KEY_SLASH:
			return Key.SLASH;
		case Keyboard.KEY_SPACE:
			return Key.SPACE;
		case Keyboard.KEY_TAB:
			return Key.TAB;
		case Keyboard.KEY_LCONTROL:
			return Key.CONTROL_LEFT;
		case Keyboard.KEY_RCONTROL:
			return Key.CONTROL_RIGHT;
		case Keyboard.KEY_ESCAPE:
			return Key.ESCAPE;
		case Keyboard.KEY_END:
			return Key.END;
		case Keyboard.KEY_INSERT:
			return Key.INSERT;
		case Keyboard.KEY_NUMPAD5:
			return Key.DPAD_CENTER;
		case Keyboard.KEY_BACK:
			return Key.DEL;
		default:
			return Key.UNKNOWN;
		}
	}

	public static int toLwjglKeyCode(int keyCode) {
		switch (keyCode) {
		case Key.NUM_0:
			return Keyboard.KEY_0;
		case Key.NUM_1:
			return Keyboard.KEY_1;
		case Key.NUM_2:
			return Keyboard.KEY_2;
		case Key.NUM_3:
			return Keyboard.KEY_3;
		case Key.NUM_4:
			return Keyboard.KEY_4;
		case Key.NUM_5:
			return Keyboard.KEY_5;
		case Key.NUM_6:
			return Keyboard.KEY_6;
		case Key.NUM_7:
			return Keyboard.KEY_7;
		case Key.NUM_8:
			return Keyboard.KEY_8;
		case Key.NUM_9:
			return Keyboard.KEY_9;
		case Key.A:
			return Keyboard.KEY_A;
		case Key.B:
			return Keyboard.KEY_B;
		case Key.C:
			return Keyboard.KEY_C;
		case Key.D:
			return Keyboard.KEY_D;
		case Key.E:
			return Keyboard.KEY_E;
		case Key.F:
			return Keyboard.KEY_F;
		case Key.G:
			return Keyboard.KEY_G;
		case Key.H:
			return Keyboard.KEY_H;
		case Key.I:
			return Keyboard.KEY_I;
		case Key.J:
			return Keyboard.KEY_J;
		case Key.K:
			return Keyboard.KEY_K;
		case Key.L:
			return Keyboard.KEY_L;
		case Key.M:
			return Keyboard.KEY_M;
		case Key.N:
			return Keyboard.KEY_N;
		case Key.O:
			return Keyboard.KEY_O;
		case Key.P:
			return Keyboard.KEY_P;
		case Key.Q:
			return Keyboard.KEY_Q;
		case Key.R:
			return Keyboard.KEY_R;
		case Key.S:
			return Keyboard.KEY_S;
		case Key.T:
			return Keyboard.KEY_T;
		case Key.U:
			return Keyboard.KEY_U;
		case Key.V:
			return Keyboard.KEY_V;
		case Key.W:
			return Keyboard.KEY_W;
		case Key.X:
			return Keyboard.KEY_X;
		case Key.Y:
			return Keyboard.KEY_Y;
		case Key.Z:
			return Keyboard.KEY_Z;
		case Key.ALT_LEFT:
			return Keyboard.KEY_LMETA;
		case Key.ALT_RIGHT:
			return Keyboard.KEY_RMETA;
		case Key.BACKSLASH:
			return Keyboard.KEY_BACKSLASH;
		case Key.COMMA:
			return Keyboard.KEY_COMMA;
		case Key.FORWARD_DEL:
			return Keyboard.KEY_DELETE;
		case Key.DPAD_LEFT:
			return Keyboard.KEY_LEFT;
		case Key.DPAD_RIGHT:
			return Keyboard.KEY_RIGHT;
		case Key.DPAD_UP:
			return Keyboard.KEY_UP;
		case Key.DPAD_DOWN:
			return Keyboard.KEY_DOWN;
		case Key.ENTER:
			return Keyboard.KEY_RETURN;
		case Key.HOME:
			return Keyboard.KEY_HOME;
		case Key.MINUS:
			return Keyboard.KEY_MINUS;
		case Key.PERIOD:
			return Keyboard.KEY_PERIOD;
		case Key.PLUS:
			return Keyboard.KEY_ADD;
		case Key.SEMICOLON:
			return Keyboard.KEY_SEMICOLON;
		case Key.SHIFT_LEFT:
			return Keyboard.KEY_LSHIFT;
		case Key.SHIFT_RIGHT:
			return Keyboard.KEY_RSHIFT;
		case Key.SLASH:
			return Keyboard.KEY_SLASH;
		case Key.SPACE:
			return Keyboard.KEY_SPACE;
		case Key.TAB:
			return Keyboard.KEY_TAB;
		case Key.DEL:
			return Keyboard.KEY_BACK;
		case Key.CONTROL_LEFT:
			return Keyboard.KEY_LCONTROL;
		case Key.CONTROL_RIGHT:
			return Keyboard.KEY_RCONTROL;
		case Key.ESCAPE:
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
