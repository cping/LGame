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
import loon.utils.MathUtils;

public class SysInputFactory {

	private float _offsetTouchX, _offsetMoveX, _offsetTouchY, _offsetMoveY;

	private final LProcess _handler;

	final static GameTouch finalTouch = new GameTouch();

	final static GameKey finalKey = new GameKey();

	private int _halfWidth, _halfHeight;

	static boolean _isDraging;

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

	public void resetSysTouch() {
		if (finalTouch.button == SysTouch.TOUCH_UP) {
			finalTouch.id = -1;
			finalTouch.button = -1;
		}
	}

	public SysInputFactory(LProcess _handler) {
		this._handler = _handler;
		this._halfWidth = _handler.getWidth() / 2;
		this._halfHeight = _handler.getHeight() / 2;
	}

	public static ActionKey getOnlyKey() {
		return SysKey.only_key;
	}

	public void callKey(KeyMake.KeyEvent e) {
		if (e.down) {
			finalKey.timer = e.time;
			finalKey.keyChar = e.keyChar;
			finalKey.keyCode = e.keyCode;
			finalKey.type = SysKey.DOWN;
			SysKey.only_key.press();
			SysKey.addKey(finalKey.keyCode);
			_handler.keyDown(finalKey);
		} else {
			finalKey.timer = e.time;
			finalKey.keyChar = e.keyChar;
			finalKey.keyCode = e.keyCode;
			finalKey.type = SysKey.UP;
			SysKey.removeKey(finalKey.keyCode);
			_handler.keyUp(finalKey);
		}
	}

	private int buttons;

	private EmulatorButtons ebuttons;

	public void callMouse(MouseMake.ButtonEvent event) {
		float touchX = (event.getX() - _handler.getX())
				/ LSystem.getScaleWidth();
		float touchY = (event.getY() - _handler.getY())
				/ LSystem.getScaleHeight();

		int button = event.button;
		finalTouch.x = touchX;
		finalTouch.y = touchY;
		finalTouch.button = event.button;
		finalTouch.pointer = 0;
		finalTouch.id = 0;
		ebuttons = _handler.getEmulatorButtons();
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
			finalTouch.button = SysTouch.TOUCH_DOWN;
			if (useTouchCollection) {
				touchCollection.add(finalTouch.id, finalTouch.x, finalTouch.y);
			}
			_handler.mousePressed(finalTouch);
			buttons++;
			_isDraging = false;
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.hit(0, touchX, touchY, false);
			}
			break;
		case SysTouch.TOUCH_UP:
			finalTouch.button = SysTouch.TOUCH_UP;
			if (useTouchCollection) {
				touchCollection.update(finalTouch.id,
						LTouchLocationState.Released, finalTouch.x,
						finalTouch.y);
			}
			_handler.mouseReleased(finalTouch);
			buttons = 0;
			_isDraging = false;
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.unhit(0, touchX, touchY);
			}
			break;
		case SysTouch.TOUCH_MOVE:
			_offsetMoveX = touchX;
			_offsetMoveY = touchY;
			finalTouch.dx = _offsetTouchX - _offsetMoveX;
			finalTouch.dy = _offsetTouchY - _offsetMoveY;
			finalTouch.button = SysTouch.TOUCH_MOVE;
			if (!_isDraging) {
				if (useTouchCollection) {
					touchCollection.update(finalTouch.id,
							LTouchLocationState.Dragged, finalTouch.x,
							finalTouch.y);
				}
				_handler.mouseMoved(finalTouch);
			}
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.unhit(0, touchX, touchY);
			}
			break;
		case SysTouch.TOUCH_DRAG:
			_offsetMoveX = touchX;
			_offsetMoveY = touchY;
			finalTouch.dx = _offsetTouchX - _offsetMoveX;
			finalTouch.dy = _offsetTouchY - _offsetMoveY;
			finalTouch.button = SysTouch.TOUCH_DRAG;
			ebuttons = _handler.getEmulatorButtons();
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.hit(0, touchX, touchY, true);
			}
			if (useTouchCollection) {
				touchCollection
						.update(finalTouch.id, LTouchLocationState.Dragged,
								finalTouch.x, finalTouch.y);
			}
			_handler.mouseDragged(finalTouch);
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.hit(0, touchX, touchY, false);
			}
			_isDraging = true;
			break;
		default:
			if (useTouchCollection) {
				touchCollection
						.update(finalTouch.id, LTouchLocationState.Invalid,
								finalTouch.x, finalTouch.y);
			}
			if (ebuttons != null && ebuttons.isVisible()) {
				ebuttons.release();
			}
			break;
		}
	}

	public void callTouch(TouchMake.Event[] events) {

		ebuttons = _handler.getEmulatorButtons();
		int size = events.length;

		for (int i = 0; i < size; i++) {
			TouchMake.Event e = events[i];
			float touchX = (e.getX() - _handler.getX())
					/ LSystem.getScaleWidth();
			float touchY = (e.getY() - _handler.getY())
					/ LSystem.getScaleHeight();
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
				_offsetTouchX = touchX;
				_offsetTouchY = touchY;
				if ((touchX < _halfWidth) && (touchY < _halfHeight)) {
					finalTouch.type = SysTouch.UPPER_LEFT;
				} else if ((touchX >= _halfWidth) && (touchY < _halfHeight)) {
					finalTouch.type = SysTouch.UPPER_RIGHT;
				} else if ((touchX < _halfWidth) && (touchY >= _halfHeight)) {
					finalTouch.type = SysTouch.LOWER_LEFT;
				} else {
					finalTouch.type = SysTouch.LOWER_RIGHT;
				}
				finalTouch.button = SysTouch.TOUCH_DOWN;
				_handler.mousePressed(finalTouch);
				_isDraging = false;
				if (ebuttons != null && ebuttons.isVisible()) {
					ebuttons.hit(i, touchX, touchY, false);
				}
				break;
			case MOVE:

				_offsetMoveX = touchX;
				_offsetMoveY = touchY;
				finalTouch.dx = _offsetTouchX - _offsetMoveX;
				finalTouch.dy = _offsetTouchY - _offsetMoveY;
				if (MathUtils.abs(finalTouch.dx) > 0.1f
						|| MathUtils.abs(finalTouch.dy) > 0.1f) {
					if (useTouchCollection) {
						touchCollection.update(finalTouch.id,
								LTouchLocationState.Dragged, finalTouch.x,
								finalTouch.y);
					}

					// a few platforms no such behavior (ios or android)
					_handler.mouseMoved(finalTouch);
					if (!LSystem.NOT_DRAG) {
						_handler.mouseDragged(finalTouch);
					}
					_isDraging = true;
				}
				ebuttons = _handler.getEmulatorButtons();
				if (ebuttons != null && ebuttons.isVisible()) {
					ebuttons.hit(i, touchX, touchY, false);
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
				_handler.mouseReleased(finalTouch);
				_isDraging = false;
				if (ebuttons != null && ebuttons.isVisible()) {
					ebuttons.unhit(i, touchX, touchY);
				}
				break;
			case CANCEL:
			default:
				if (finalTouch.button == SysTouch.TOUCH_DOWN
						|| finalTouch.button == SysTouch.TOUCH_MOVE) {
					finalTouch.button = SysTouch.TOUCH_UP;
				}
				if (useTouchCollection) {
					touchCollection.update(finalTouch.id,
							LTouchLocationState.Invalid, finalTouch.x,
							finalTouch.y);
				}
				if (ebuttons != null && ebuttons.isVisible()) {
					ebuttons.release();
				}
				break;
			}

		}
	}

}
