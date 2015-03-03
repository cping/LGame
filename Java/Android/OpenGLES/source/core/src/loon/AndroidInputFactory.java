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

import loon.core.EmulatorButtons;
import loon.core.event.ActionKey;
import loon.utils.collection.IntArray;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

@SuppressLint("ClickableViewAccessibility")
public class AndroidInputFactory implements OnKeyListener, OnTouchListener {

	public static void setOnscreenKeyboardVisible(final boolean visible) {
		LSystem.getOSHandler().post(new Runnable() {
			public void run() {
				android.view.inputmethod.InputMethodManager manager = (android.view.inputmethod.InputMethodManager) LSystem
						.getActivity().getSystemService(
								android.content.Context.INPUT_METHOD_SERVICE);
				if (visible) {
					AndroidView gameview = LSystem.getActivity().gameView();
					if (gameview != null) {
						View view = gameview.getView();
						view.setFocusable(true);
						view.setFocusableInTouchMode(true);
						manager.showSoftInput(view, 0);
					}
				} else {
					AndroidView gameview = LSystem.getActivity().gameView();
					if (gameview != null) {
						View view = gameview.getView();
						view.setFocusable(true);
						view.setFocusableInTouchMode(true);
						manager.hideSoftInputFromWindow(view.getWindowToken(),
								0);
					}

				}
			}
		});
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

	private float offsetTouchX, offsetMoveX, offsetTouchY, offsetMoveY;

	private final LProcess handler;

	final static LTouch finalTouch = new LTouch();

	final static LKey finalKey = new LKey();

	private long keyTimeMillis;

	static boolean isDraging;

	private int halfWidth, halfHeight;

	public AndroidInputFactory(LProcess handler) {
		this.handler = handler;
		this.halfWidth = handler.getWidth() / 2;
		this.halfHeight = handler.getHeight() / 2;
	}

	final static ActionKey only_key = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	public static ActionKey getOnlyKey() {
		return only_key;
	}

	private void callKey(KeyEvent e) {
		try {
			LSystem.AUTO_REPAINT = false;
			char charCode = (char) e.getUnicodeChar();
			if (e.getKeyCode() == 67) {
				charCode = '\b';
			}
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

	private boolean requestFocus = true;

	public boolean onTouch(View v, MotionEvent e) {
		if (handler == null) {
			return true;
		}
		if (requestFocus && LSystem.getActivity() != null) {
			if (requestFocus && LSystem.getActivity().gameView() != null) {
				View view = LSystem.getActivity().gameView().getView();
				view.requestFocus();
				view.requestFocusFromTouch();
				requestFocus = false;
			}
		}
		EmulatorButtons ebs = handler.emulatorButtons;
		if (ebs != null && ebs.isVisible()) {
			ebs.onEmulatorButtonEvent(e);
		}
		try {
			float touchX;
			float touchY;
			int code = e.getAction();
			if (AndroidMultitouchUtils.isMultitouch()) {
				final int action = code & AndroidMultitouchUtils.ACTION_MASK;
				int pointerIndex = (code & AndroidMultitouchUtils.ACTION_POINTER_ID_MASK) >> AndroidMultitouchUtils.ACTION_POINTER_ID_SHIFT;
				int pointerId = AndroidMultitouchUtils.getPointId(e,
						pointerIndex);
				int pointerCount = AndroidMultitouchUtils.getPointerCount(e);
				touchX = (AndroidMultitouchUtils.getX(e, pointerId) - handler
						.getX()) / LSystem.scaleWidth;
				touchY = (AndroidMultitouchUtils.getY(e, pointerId) - handler
						.getY()) / LSystem.scaleHeight;
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.pointer = pointerCount;
				finalTouch.id = pointerId;
				switch (action) {
				case AndroidMultitouchUtils.ACTION_DOWN:
				case AndroidMultitouchUtils.ACTION_POINTER_DOWN:
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
				case AndroidMultitouchUtils.ACTION_UP:
				case AndroidMultitouchUtils.ACTION_POINTER_UP:
				case AndroidMultitouchUtils.ACTION_OUTSIDE:
				case AndroidMultitouchUtils.ACTION_CANCEL:
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
				case AndroidMultitouchUtils.ACTION_MOVE:
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
