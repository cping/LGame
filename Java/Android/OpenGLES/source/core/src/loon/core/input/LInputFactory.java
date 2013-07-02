package loon.core.input;

import loon.core.EmulatorButtons;
import loon.core.LSystem;
import loon.core.event.ActionKey;
import loon.core.geom.Vector2f;
import loon.utils.collection.IntArray;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

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
public class LInputFactory implements OnKeyListener, OnTouchListener {

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

	private float offsetTouchX, offsetMoveX, offsetTouchY, offsetMoveY;

	private final LProcess handler;

	private final static LTouch finalTouch = new LTouch();

	private final static LKey finalKey = new LKey();

	private long keyTimeMillis;

	static boolean isDraging;

	public static class Touch {

		public final static int UPPER_LEFT = 0;

		public final static int UPPER_RIGHT = 1;

		public final static int LOWER_LEFT = 2;

		public final static int LOWER_RIGHT = 3;

		public static final int TOUCH_DOWN = 0;

		public static final int TOUCH_UP = 1;

		public static final int TOUCH_MOVE = 2;

		private static final Vector2f location = new Vector2f();

		public static Vector2f getLocation() {
			location.set(finalTouch.x, finalTouch.y);
			return location;
		}

		public static int getType() {
			return finalTouch.type;
		}

		public static int getButton() {
			return finalTouch.button;
		}

		public static int getPointer() {
			return finalTouch.pointer;
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

	private int halfWidth, halfHeight;

	public LInputFactory(LProcess handler) {
		this.handler = handler;
		this.halfWidth = handler.getWidth() / 2;
		this.halfHeight = handler.getHeight() / 2;
	}

	private final static ActionKey only_key = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	public static ActionKey getOnlyKey() {
		return only_key;
	}

	private void callKey(KeyEvent e) {
		try {
			LSystem.AUTO_REPAINT = false;
			char charCode = (char) e.getUnicodeChar();
			switch (e.getAction()) {
			case android.view.KeyEvent.ACTION_DOWN:
				long curTime = System.currentTimeMillis();
				// 让每次执行键盘事件，至少间隔1/5秒
				if ((curTime - keyTimeMillis) > LSystem.SECOND / 5) {
					keyTimeMillis = curTime;
					finalKey.timer = e.getEventTime();
					finalKey.keyChar = charCode;
					finalKey.keyCode = e.getKeyCode();
					finalKey.type = Key.KEY_DOWN;
					if (finalKey.keyCode == android.view.KeyEvent.KEYCODE_BACK
							&& e.isAltPressed()) {
						finalKey.keyCode = Key.BUTTON_CIRCLE;
					}
					only_key.press();
					handler.keyDown(finalKey);
					keys.add(finalKey.keyCode);
				}
				break;
			case android.view.KeyEvent.ACTION_UP:
				finalKey.timer = e.getEventTime();
				finalKey.keyChar = charCode;
				finalKey.keyCode = e.getKeyCode();
				finalKey.type = Key.KEY_UP;
				if (finalKey.keyCode == android.view.KeyEvent.KEYCODE_BACK
						&& e.isAltPressed()) {
					finalKey.keyCode = Key.BUTTON_CIRCLE;
				}
				only_key.release();
				handler.keyUp(finalKey);
				keys.removeValue(finalKey.keyCode);
				break;
			default:
				only_key.reset();
				keys.clear();
			}
		} finally {
			LSystem.AUTO_REPAINT = true;
		}
	}

	private ActionKey _close_key = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent e) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (!LSystem.isBackLocked) {
				if (android.view.KeyEvent.ACTION_UP == e.getAction()
						&& _close_key.isPressed()) {
					_close_key.release();
				} else if (android.view.KeyEvent.ACTION_DOWN == e.getAction()
						&& !_close_key.isPressed()) {

					_close_key.press();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							LSystem.screenActivity
									.showAndroidYesOrNo(
											"Exit",
											"Do you want to Quit ? ",
											true,
											"Yes",
											"No",
											new android.content.DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														android.content.DialogInterface dialog,
														int which) {
													LSystem.AUTO_REPAINT = false;
													LSystem.exit();
												}
											},
											new android.content.DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														android.content.DialogInterface dialog,
														int which) {
													LSystem.AUTO_REPAINT = true;
												}
											});
						}
					};
					LSystem.runOnUiThread(runnable);
				}
			}
			callKey(e);
			return true;
		} else {
			_close_key.release();
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		if (handler == null) {
			return true;
		}
		synchronized (this) {
			callKey(e);
		}
		return false;
	}

	public void callKeyboard(final boolean visible) {
		LSystem.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				android.view.inputmethod.InputMethodManager manager = (android.view.inputmethod.InputMethodManager) LSystem
						.getActivity().getSystemService(
								android.content.Context.INPUT_METHOD_SERVICE);
				View view = LSystem.getActivity().gameView().getView();
				if (visible) {
					view.setFocusable(true);
					view.setFocusableInTouchMode(true);
					manager.showSoftInput(view, 0);
				} else {
					manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if (handler == null) {
			return true;
		}
		EmulatorButtons ebs = handler.emulatorButtons;
		if (ebs != null && ebs.isVisible()) {
			ebs.onEmulatorButtonEvent(e);
		}
		try {
			float touchX;
			float touchY;
			int code = e.getAction();
			if (MultitouchUtils.isMultitouch()) {
				final int action = code & MultitouchUtils.ACTION_MASK;
				int pointerIndex = (code & MultitouchUtils.ACTION_POINTER_ID_MASK) >> MultitouchUtils.ACTION_POINTER_ID_SHIFT;
				int pointerId = MultitouchUtils.getPointId(e, pointerIndex);
				int pointerCount = MultitouchUtils.getPointerCount(e);
				touchX = (MultitouchUtils.getX(e, pointerId) - handler.getX())
						/ LSystem.scaleWidth;
				touchY = (MultitouchUtils.getY(e, pointerId) - handler.getY())
						/ LSystem.scaleHeight;
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.pointer = pointerCount;
				finalTouch.id = pointerId;
				switch (action) {
				case MultitouchUtils.ACTION_DOWN:
				case MultitouchUtils.ACTION_POINTER_DOWN:
					if (useTouchCollection) {
						touchCollection.add(finalTouch.id, finalTouch.x,
								finalTouch.y);
					}
					offsetTouchX = touchX;
					offsetTouchY = touchY;
					if ((touchX < halfWidth) && (touchY < halfHeight)) {
						finalTouch.type = Touch.UPPER_LEFT;
					} else if ((touchX >= halfWidth) && (touchY < halfHeight)) {
						finalTouch.type = Touch.UPPER_RIGHT;
					} else if ((touchX < halfWidth) && (touchY >= halfHeight)) {
						finalTouch.type = Touch.LOWER_LEFT;
					} else {
						finalTouch.type = Touch.LOWER_RIGHT;
					}
					finalTouch.button = Touch.TOUCH_DOWN;
					handler.mousePressed(finalTouch);
					isDraging = false;
					break;
				case MultitouchUtils.ACTION_UP:
				case MultitouchUtils.ACTION_POINTER_UP:
				case MultitouchUtils.ACTION_OUTSIDE:
				case MultitouchUtils.ACTION_CANCEL:
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Released, finalTouch.x,
								finalTouch.y);
					}
					if (finalTouch.button == Touch.TOUCH_DOWN
							|| finalTouch.button == Touch.TOUCH_MOVE) {
						finalTouch.button = Touch.TOUCH_UP;
					}
					handler.mouseReleased(finalTouch);
					isDraging = false;
					break;
				case MultitouchUtils.ACTION_MOVE:
					offsetMoveX = touchX;
					offsetMoveY = touchY;
					if (Math.abs(offsetTouchX - offsetMoveX) > 5
							|| Math.abs(offsetTouchY - offsetMoveY) > 5) {
						if (useTouchCollection) {
							touchCollection.update(finalTouch.id,
									LTouchLocationState.Dragged, finalTouch.x,
									finalTouch.y);
						}
						finalTouch.button = Touch.TOUCH_MOVE;
						handler.mouseMoved(finalTouch);
						isDraging = true;
					}
					break;
				default:
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Invalid, finalTouch.x,
								finalTouch.y);
					}
					break;
				}
				return true;
			} else {
				touchX = (e.getX() - handler.getX()) / LSystem.scaleWidth;
				touchY = (e.getY() - handler.getY()) / LSystem.scaleHeight;
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.pointer = 1;
				finalTouch.id = 0;
				switch (code) {
				case MotionEvent.ACTION_DOWN:
					if (useTouchCollection) {
						touchCollection.add(finalTouch.id, finalTouch.x,
								finalTouch.y);
					}
					offsetTouchX = touchX;
					offsetTouchY = touchY;
					if ((touchX < halfWidth) && (touchY < halfHeight)) {
						finalTouch.type = Touch.UPPER_LEFT;
					} else if ((touchX >= halfWidth) && (touchY < halfHeight)) {
						finalTouch.type = Touch.UPPER_RIGHT;
					} else if ((touchX < halfWidth) && (touchY >= halfHeight)) {
						finalTouch.type = Touch.LOWER_LEFT;
					} else {
						finalTouch.type = Touch.LOWER_RIGHT;
					}
					finalTouch.button = Touch.TOUCH_DOWN;
					handler.mousePressed(finalTouch);
					isDraging = false;
					return true;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Released, finalTouch.x,
								finalTouch.y);
					}
					finalTouch.button = Touch.TOUCH_UP;
					handler.mouseReleased(finalTouch);
					isDraging = false;
					return true;
				case MotionEvent.ACTION_MOVE:
					offsetMoveX = touchX;
					offsetMoveY = touchY;
					if (Math.abs(offsetTouchX - offsetMoveX) > 5
							|| Math.abs(offsetTouchY - offsetMoveY) > 5) {
						if (useTouchCollection) {
							touchCollection.update(finalTouch.id,
									LTouchLocationState.Dragged, finalTouch.x,
									finalTouch.y);
						}
						finalTouch.button = Touch.TOUCH_MOVE;
						handler.mouseMoved(finalTouch);
						isDraging = true;
						return true;
					}
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
		} catch (Exception ex) {
			Log.d("on Touch !", ex.getMessage());
		} finally {
			try {
				Thread.sleep(16);
			} catch (InterruptedException ei) {
			}
		}
		return false;
	}
}
