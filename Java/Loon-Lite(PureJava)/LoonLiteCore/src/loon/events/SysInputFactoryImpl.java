/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.events;

import loon.EmulatorButtons;
import loon.LProcess;
import loon.LSystem;
import loon.events.TouchMake.Event.Kind;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.TimeUtils;

public class SysInputFactoryImpl extends SysInputFactory {

	private final static float DragMinValue = 0.5f;

	private int _buttons, _halfWidth, _halfHeight;

	private float _offsetTouchX, _offsetMoveX, _offsetTouchY, _offsetMoveY;

	private boolean _useTouchCollection = false;

	private Kind _lastKind = null;

	private EmulatorButtons _ebuttons;

	private LTouchCollection _touchCollection = new LTouchCollection();

	public SysInputFactoryImpl() {
		this.update();
	}

	protected void update() {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			this._halfWidth = process.getWidth() / 2;
			this._halfHeight = process.getHeight() / 2;
		} else if (LSystem.viewSize != null) {
			this._halfWidth = LSystem.viewSize.getWidth() / 2;
			this._halfHeight = LSystem.viewSize.getHeight() / 2;
		}
	}

	@Override
	public void startTouchCollection() {
		_useTouchCollection = true;
	}

	@Override
	public void stopTouchCollection() {
		_useTouchCollection = false;
	}

	@Override
	public LTouchCollection getTouchState() {
		LTouchCollection result = new LTouchCollection(_touchCollection);
		_touchCollection.update();
		return result;
	}

	@Override
	public void reset() {
		_offsetTouchX = 0;
		_offsetTouchY = 0;
		_offsetMoveX = 0;
		_offsetMoveY = 0;
		_touchCollection.clear();
		isDraging = false;
		finalTouch.reset();
		finalKey.reset();
		update();
	}

	@Override
	public void resetTouch() {
		_touchCollection.clear();
	}

	@Override
	public void resetSysTouch() {
		if (finalTouch.button == SysTouch.TOUCH_UP) {
			finalTouch.id = -1;
			finalTouch.button = -1;
		}
	}

	@Override
	public void callKey(KeyMake.KeyEvent e) {
		LProcess process = LSystem.getProcess();
		if (process != null) {
			if (e.down) {
				finalKey.timer = e.time;
				finalKey.keyChar = e.keyChar;
				finalKey.keyCode = e.keyCode;
				finalKey.type = SysKey.DOWN;
				onlyKey.press();
				SysKey.addKey(finalKey.keyCode);
				process.keyDown(finalKey);
			} else {
				finalKey.timer = e.time;
				// finalKey.keyChar = e.keyChar;
				// finalKey.keyCode = e.keyCode;
				finalKey.type = SysKey.UP;
				SysKey.removeKey(finalKey.keyCode);
				process.keyUp(finalKey);
			}
		}
	}

	/**
	 * 鼠标移动事件监听(仅在有鼠标的设备上生效,没有触屏的设备上(主要是台式机)触屏会由此监听模拟)
	 */
	@Override
	public void callMouse(MouseMake.ButtonEvent event) {
		if (LSystem.isLockAllTouchEvent()) {
			return;
		}
		LProcess process = LSystem.getProcess();
		if (process == null) {
			return;
		}

		final boolean stopMoveDrag = LSystem.isNotAllowDragAndMove();
		final Vector2f pos = process.convertXY(event.getX(), event.getY());

		final float touchX = pos.x;
		final float touchY = pos.y;
		final int button = event.button;
		finalTouch.isDraging = isDraging;
		finalTouch.x = touchX;
		finalTouch.y = touchY;
		finalTouch.button = event.button;
		finalTouch.pointer = 0;
		finalTouch.id = 0;
		_ebuttons = process.getEmulatorButtons();
		if (button == -1) {
			if (_buttons > 0) {
				finalTouch.type = SysTouch.TOUCH_DRAG;
			} else {
				finalTouch.type = SysTouch.TOUCH_MOVE;
			}
		} else {
			if (event.down) {
				finalTouch.type = SysTouch.TOUCH_DOWN;
			} else {
				if (finalTouch.type == SysTouch.TOUCH_DOWN || finalTouch.type == SysTouch.TOUCH_DRAG) {
					finalTouch.type = SysTouch.TOUCH_UP;
				}
			}
		}

		switch (finalTouch.type) {
		case SysTouch.TOUCH_DOWN:
			finalTouch.button = SysTouch.TOUCH_DOWN;
			finalTouch.duration = 0;
			finalTouch.timeDown = TimeUtils.millis();
			if (_useTouchCollection) {
				_touchCollection.add(finalTouch.id, finalTouch.x, finalTouch.y);
			}
			process.mousePressed(finalTouch);
			_buttons++;
			isDraging = false;
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.hit(0, touchX, touchY, false);
			}
			break;
		case SysTouch.TOUCH_UP:
			finalTouch.button = SysTouch.TOUCH_UP;
			finalTouch.timeUp = TimeUtils.millis();
			finalTouch.duration = finalTouch.timeUp - finalTouch.timeDown;
			if (_useTouchCollection) {
				_touchCollection.update(finalTouch.id, LTouchLocationState.Released, finalTouch.x, finalTouch.y);
			}
			process.mouseReleased(finalTouch);
			_buttons = 0;
			isDraging = false;
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.unhit(0, touchX, touchY);
			}
			break;
		case SysTouch.TOUCH_MOVE:
			_offsetMoveX = touchX;
			_offsetMoveY = touchY;
			finalTouch.dx = _offsetTouchX - _offsetMoveX;
			finalTouch.dy = _offsetTouchY - _offsetMoveY;
			finalTouch.button = SysTouch.TOUCH_MOVE;
			finalTouch.duration = TimeUtils.millis() - finalTouch.timeDown;
			if (!isDraging) {
				if (_useTouchCollection) {
					_touchCollection.update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
				}
				if (!stopMoveDrag) {
					process.mouseMoved(finalTouch);
				}
			}
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.unhit(0, touchX, touchY);
			}
			break;
		case SysTouch.TOUCH_DRAG:
			_offsetMoveX = touchX;
			_offsetMoveY = touchY;
			finalTouch.dx = _offsetTouchX - _offsetMoveX;
			finalTouch.dy = _offsetTouchY - _offsetMoveY;
			finalTouch.button = SysTouch.TOUCH_DRAG;
			finalTouch.duration = TimeUtils.millis() - finalTouch.timeDown;
			_ebuttons = process.getEmulatorButtons();
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.hit(0, touchX, touchY, true);
			}
			if (_useTouchCollection) {
				_touchCollection.update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
			}
			if (!stopMoveDrag) {
				process.mouseDragged(finalTouch);
			}
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.hit(0, touchX, touchY, false);
			}
			isDraging = true;
			break;
		default:
			finalTouch.duration = 0;
			if (_useTouchCollection) {
				_touchCollection.update(finalTouch.id, LTouchLocationState.Invalid, finalTouch.x, finalTouch.y);
			}
			if (_ebuttons != null && _ebuttons.isVisible()) {
				_ebuttons.release();
			}
			break;
		}
		if (_filterTouch != null) {
			_filterTouch.update(finalTouch);
		}
	}

	/**
	 * 触屏事件监听
	 *
	 * @param events
	 */
	@Override
	public void callTouch(TouchMake.Event[] events) {
		if (LSystem.isLockAllTouchEvent()) {
			return;
		}
		LProcess process = LSystem.getProcess();
		if (process == null) {
			return;
		}

		final boolean stopMoveDrag = LSystem.isNotAllowDragAndMove();

		_ebuttons = process.getEmulatorButtons();

		int dragCount = 0;

		final float posX = process.getX();
		final float posY = process.getY();
		final int size = events.length;

		for (int i = 0; i < size; i++) {

			final TouchMake.Event e = events[i];
			final Vector2f pos = process.convertXY(posX, posY, e.getX(), e.getY());
			final float touchX = pos.x;
			final float touchY = pos.y;

			finalTouch.isDraging = isDraging;
			finalTouch.pointer = i;
			finalTouch.id = e.id;
			final Kind kind = e.kind;

			switch (kind) {
			case START:
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.dx = 0f;
				finalTouch.dy = 0f;
				if (_useTouchCollection) {
					_touchCollection.add(finalTouch.id, finalTouch.x, finalTouch.y);
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
				finalTouch.duration = 0;
				finalTouch.button = SysTouch.TOUCH_DOWN;
				finalTouch.timeDown = TimeUtils.millis();
				process.mousePressed(finalTouch);
				if (_ebuttons != null && _ebuttons.isVisible()) {
					_ebuttons.hit(i, touchX, touchY, false);
				}
				break;
			case MOVE:
				final boolean moved = (_lastKind == Kind.MOVE && isDraging
						&& (_offsetTouchX != -1f && _offsetTouchY != -1f));
				if (moved) {
					finalTouch.button = SysTouch.TOUCH_MOVE;
					finalTouch.x = touchX - posX;
					finalTouch.y = touchY - posY;
					finalTouch.dx = _offsetTouchX - _offsetMoveX;
					finalTouch.dy = _offsetTouchY - _offsetMoveY;
				} else {
					finalTouch.dx = 0f;
					finalTouch.dy = 0f;
				}
				finalTouch.duration = TimeUtils.millis() - finalTouch.timeDown;
				if (moved && (MathUtils.abs(finalTouch.dx) > DragMinValue
						|| MathUtils.abs(finalTouch.dy) > DragMinValue)) {
					if (_useTouchCollection) {
						_touchCollection.update(finalTouch.id, LTouchLocationState.Dragged, finalTouch.x, finalTouch.y);
					}
					if (moved && !stopMoveDrag) {
						// a few platforms no such behavior (ios or android)
						finalTouch.button = SysTouch.TOUCH_MOVE;
						process.mouseMoved(finalTouch);
						finalTouch.button = SysTouch.TOUCH_DRAG;
						process.mouseDragged(finalTouch);
						_offsetMoveX = finalTouch.x;
						_offsetMoveY = finalTouch.y;
					}
					_ebuttons = process.getEmulatorButtons();
					if (_ebuttons != null && _ebuttons.isVisible()) {
						_ebuttons.hit(i, touchX, touchY, false);
					}
				} else {
					finalTouch.button = SysTouch.TOUCH_DOWN;
					finalTouch.x = touchX;
					finalTouch.y = touchY;
					process.mousePressed(finalTouch);
				}
				dragCount++;
				break;
			case END:
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.dx = 0f;
				finalTouch.dy = 0f;
				_offsetTouchX = _offsetTouchY = -1f;
				if (_useTouchCollection) {
					_touchCollection.update(finalTouch.id, LTouchLocationState.Released, finalTouch.x, finalTouch.y);
				}
				if (finalTouch.button == SysTouch.TOUCH_DOWN || finalTouch.button == SysTouch.TOUCH_MOVE) {
					finalTouch.button = SysTouch.TOUCH_UP;
				}
				finalTouch.timeUp = TimeUtils.millis();
				finalTouch.duration = finalTouch.timeUp - finalTouch.timeDown;
				process.mouseReleased(finalTouch);
				if (_ebuttons != null && _ebuttons.isVisible()) {
					_ebuttons.unhit(i, touchX, touchY);
				}
				dragCount--;
				break;
			case CANCEL:
			default:
				finalTouch.x = touchX;
				finalTouch.y = touchY;
				finalTouch.dx = 0f;
				finalTouch.dy = 0f;
				_offsetTouchX = _offsetTouchY = -1f;
				if (finalTouch.button == SysTouch.TOUCH_DOWN || finalTouch.button == SysTouch.TOUCH_MOVE) {
					finalTouch.button = SysTouch.TOUCH_UP;
				}
				finalTouch.duration = 0;
				if (_useTouchCollection) {
					_touchCollection.update(finalTouch.id, LTouchLocationState.Invalid, finalTouch.x, finalTouch.y);
				}
				if (_ebuttons != null && _ebuttons.isVisible()) {
					_ebuttons.release();
				}
				dragCount--;
				break;
			}
			_lastKind = kind;
			isDraging = (dragCount > 0);
		}
		isDraging = (dragCount > 0);
		if (_filterTouch != null) {
			_filterTouch.update(finalTouch);
		}
	}


}
