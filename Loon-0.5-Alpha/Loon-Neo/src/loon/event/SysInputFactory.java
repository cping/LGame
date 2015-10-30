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
package loon.event;

import loon.EmulatorButtons;
import loon.LProcess;
import loon.LSystem;
import loon.utils.IntArray;

public class SysInputFactory {

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

	final static GameTouch finalTouch = new GameTouch();

	final static GameKey finalKey = new GameKey();

	private long keyTimeMillis;

	static boolean isDraging;

	private int halfWidth, halfHeight;

	final static ActionKey only_key = new ActionKey(
			ActionKey.DETECT_INITIAL_PRESS_ONLY);

	public SysInputFactory(LProcess handler) {
		this.handler = handler;
		this.halfWidth = handler.getWidth() / 2;
		this.halfHeight = handler.getHeight() / 2;
	}

	public static ActionKey getOnlyKey() {
		return only_key;
	}

	public void callKey(KeyMake.KeyEvent e) {
		if (e.down) {
			long curTime = System.currentTimeMillis();
			if ((curTime - keyTimeMillis) > LSystem.SECOND / 5) {
				keyTimeMillis = curTime;
				finalKey.timer = e.time;
				finalKey.keyChar = e.keyChar;
				finalKey.keyCode = e.keyCode;
				finalKey.type = SysKey.KEY_DOWN;
				only_key.press();
				handler.keyDown(finalKey);
				keys.add(finalKey.keyCode);
			}
		} else {
			finalKey.timer = e.time;
			finalKey.keyChar = e.keyChar;
			finalKey.keyCode = e.keyCode;
			finalKey.type = SysKey.KEY_UP;
			only_key.release();
			handler.keyUp(finalKey);
			keys.removeValue(finalKey.keyCode);
		}
	}

	private int buttons;

	private EmulatorButtons ebuttons;

	public void callMouse(MouseMake.ButtonEvent event) {
		float touchX = (event.getX() - handler.getX()) / LSystem.getScaleWidth();
		float touchY = (event.getY() - handler.getY()) / LSystem.getScaleHeight();
		int button = event.button;
		finalTouch.x = touchX;
		finalTouch.y = touchY;
		finalTouch.button = event.button;
		finalTouch.pointer = 0;
		finalTouch.id = 0;

		if (button == -1) {
			if (buttons > 0) {
				finalTouch.type = SysTouch.TOUCH_DRAG;
			} else {
				finalTouch.type = SysTouch.TOUCH_MOVE;
			}
		} else {
			if (event.down) {
				finalTouch.type = SysTouch.TOUCH_DOWN;
			} else {
				if (finalTouch.type == SysTouch.TOUCH_DOWN
						|| finalTouch.type == SysTouch.TOUCH_DRAG) {
					finalTouch.type = SysTouch.TOUCH_UP;
				}
			}
		}
		switch (finalTouch.type) {
		case SysTouch.TOUCH_DOWN:
			if (useTouchCollection) {
				touchCollection.add(finalTouch.id, finalTouch.x, finalTouch.y);
			}
			ebuttons = handler.getEmulatorButtons();
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.hit(0, touchX, touchY, false);
			}
			handler.mousePressed(finalTouch);
			buttons++;
			isDraging = false;
			break;
		case SysTouch.TOUCH_UP:
			if (useTouchCollection) {
				touchCollection.update(finalTouch.id,
						LTouchLocationState.Released, finalTouch.x,
						finalTouch.y);
			}
			ebuttons = handler.getEmulatorButtons();
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.unhit(0, touchX, touchY);
			}
			handler.mouseReleased(finalTouch);
			buttons = 0;
			isDraging = false;
			break;
		case SysTouch.TOUCH_MOVE:
			if (!isDraging) {
				if (useTouchCollection) {
					touchCollection.update(finalTouch.id,
							LTouchLocationState.Dragged, finalTouch.x,
							finalTouch.y);
				}
				handler.mouseMoved(finalTouch);
			}
			break;
		case SysTouch.TOUCH_DRAG:
			ebuttons = handler.getEmulatorButtons();
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.hit(0, touchX, touchY, true);
			}
			if (useTouchCollection) {
				touchCollection
						.update(finalTouch.id, LTouchLocationState.Dragged,
								finalTouch.x, finalTouch.y);
			}
			handler.mouseDragged(finalTouch);
			isDraging = true;
			break;
		default:
			if (useTouchCollection) {
				touchCollection
						.update(finalTouch.id, LTouchLocationState.Invalid,
								finalTouch.x, finalTouch.y);
			}
			break;
		}
	}

	public void callTouch(TouchMake.Event[] events) {
		int size = events.length;
		for (int i = 0; i < size; i++) {
			TouchMake.Event e = events[i];
			float touchX = (e.getX() - handler.getX()) / LSystem.getScaleWidth();
			float touchY = (e.getY() - handler.getY()) / LSystem.getScaleHeight();
			finalTouch.x = touchX;
			finalTouch.y = touchY;
			finalTouch.pointer = i;
			finalTouch.id = e.id;
			switch (e.kind) {
			case START:
				if (useTouchCollection) {
					touchCollection.add(finalTouch.id, finalTouch.x,
							finalTouch.y);
				}
				offsetTouchX = touchX;
				offsetTouchY = touchY;
				if ((touchX < halfWidth) && (touchY < halfHeight)) {
					finalTouch.type = SysTouch.UPPER_LEFT;
				} else if ((touchX >= halfWidth) && (touchY < halfHeight)) {
					finalTouch.type = SysTouch.UPPER_RIGHT;
				} else if ((touchX < halfWidth) && (touchY >= halfHeight)) {
					finalTouch.type = SysTouch.LOWER_LEFT;
				} else {
					finalTouch.type = SysTouch.LOWER_RIGHT;
				}
				finalTouch.button = SysTouch.TOUCH_DOWN;
				handler.mousePressed(finalTouch);
				isDraging = false;
				break;
			case MOVE:
				offsetMoveX = touchX;
				offsetMoveY = touchY;
				if (Math.abs(offsetTouchX - offsetMoveX) > 5
						|| Math.abs(offsetTouchY - offsetMoveY) > 5) {
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Dragged, finalTouch.x,
								finalTouch.y);
					}
					finalTouch.button = SysTouch.TOUCH_MOVE;
					//a few platforms no such behavior (ios or android)
					//handler.mouseMoved(finalTouch);
					handler.mouseDragged(finalTouch);
					isDraging = true;
				}
				break;
			case END:
				if (useTouchCollection) {
					touchCollection.update(finalTouch.id,
							LTouchLocationState.Released, finalTouch.x,
							finalTouch.y);
				}
				if (finalTouch.button == SysTouch.TOUCH_DOWN
						|| finalTouch.button == SysTouch.TOUCH_MOVE) {
					finalTouch.button = SysTouch.TOUCH_UP;
				}
				handler.mouseReleased(finalTouch);
				isDraging = false;
				break;
			case CANCEL:
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
