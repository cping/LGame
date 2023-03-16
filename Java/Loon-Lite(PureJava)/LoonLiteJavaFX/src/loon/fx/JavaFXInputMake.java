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
package loon.fx;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;

import loon.LObject;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;
import loon.events.TouchMake;

public class JavaFXInputMake extends JavaFXInput {

	private boolean inDragSequence = false;
	private boolean isRequestingMouseLock;

	private boolean inTouchSequence = false;

	public JavaFXInputMake(JavaFXGame game) {
		super(game);
		JavaFXResizeCanvas canvas = this.game.getFxCanvas();

		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (isRequestingMouseLock) {
					return;
				}
				int btn = getMouseButton(e);
				if (btn != -1) {
					emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), -1, true, 0);
				}
			}
		});

		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (isRequestingMouseLock) {
					return;
				}

				inDragSequence = true;
				int btn = getMouseButton(e);
				if (btn != -1) {
					dispatch(new MouseMake.ButtonEvent(0, game.time(), (float) e.getX(), (float) e.getY(), btn, true),
							e);
				}

			}
		});
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (isRequestingMouseLock) {
					return;
				}
				if (inDragSequence) {
					inDragSequence = false;
					int btn = getMouseButton(e);
					if (btn != -1) {
						dispatch(new MouseMake.ButtonEvent(0, game.time(), (float) e.getX(), (float) e.getY(), btn,
								false), e);
					}
				}

			}
		});

		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (isRequestingMouseLock) {
					return;
				}
				if (!inDragSequence) {
					emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), -1, false, 0);
				}
			}

		});
		canvas.setOnTouchPressed(new EventHandler<TouchEvent>() {

			@Override
			public void handle(TouchEvent e) {
				inTouchSequence = true;
				dispatch(toTouchEvents(TouchMake.Event.Kind.START, e), e);
			}

		});
		canvas.setOnTouchReleased(new EventHandler<TouchEvent>() {

			@Override
			public void handle(TouchEvent e) {
				if (inTouchSequence) {
					dispatch(toTouchEvents(TouchMake.Event.Kind.END, e), e);
					if (e.getTouchCount() == 0) {
						inTouchSequence = false;
					}
				}
			}

		});
		canvas.setOnTouchStationary(new EventHandler<TouchEvent>() {

			@Override
			public void handle(TouchEvent e) {
				if (inTouchSequence) {
					dispatch(toTouchEvents(TouchMake.Event.Kind.CANCEL, e), e);
					if (e.getTouchCount() == 0) {
						inTouchSequence = false;
					}
				}
			}

		});
		canvas.requestFocus();
	}

	private TouchMake.Event[] toTouchEvents(TouchMake.Event.Kind kind, TouchEvent nevent) {
		int nativeTouchesLen = nevent.getTouchCount();
		TouchMake.Event[] touches = new TouchMake.Event[nativeTouchesLen];
		double time = game.time();
		List<TouchPoint> list = nevent.getTouchPoints();
		for (int t = 0; t < nativeTouchesLen; t++) {
			TouchPoint touch = list.get(t);
			float x = (float) touch.getX();
			float y = (float) touch.getY();
			int id = touch.getId();
			touches[t] = new TouchMake.Event(0, time, x, y, kind, id);
		}
		return touches;
	}

	protected static int getMouseButton(MouseEvent evt) {
		if (evt.getButton() == MouseButton.MIDDLE) {
			return SysTouch.MIDDLE;
		} else if (evt.getButton() == MouseButton.PRIMARY) {
			return SysTouch.LEFT;
		} else if (evt.getButton() == MouseButton.SECONDARY) {
			return SysTouch.RIGHT;
		} else {
			return -1;
		}
	}

	protected void onKeyDown(KeyEvent e) {
		int key = keyForCode(e.getCode());
		char ch = (char) e.getCharacter().charAt(0);
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, true), e);
	}

	protected void onKeyUp(KeyEvent e) {
		int key = keyForCode(e.getCode());
		char ch = (char) e.getCharacter().charAt(0);
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false), e);
	}

	protected void onKeyTyped(KeyEvent e) {
		int key = keyForCode(e.getCode());
		char ch = (char) e.getCharacter().charAt(0);
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false), e);
	}

	private static int keyForCode(KeyCode keyCode) {
		switch (keyCode) {
		case ALT:
			return SysKey.ALT_LEFT;
		case BACK_SPACE:
			return SysKey.BACKSPACE;
		case DELETE:
			return SysKey.DEL;
		case DOWN:
			return SysKey.DOWN;
		case END:
			return SysKey.END;
		case ENTER:
			return SysKey.ENTER;
		case ESCAPE:
			return SysKey.ESCAPE;
		case HOME:
			return SysKey.HOME;
		case LEFT:
			return SysKey.LEFT;
		case PAGE_DOWN:
			return SysKey.PAGE_DOWN;
		case PAGE_UP:
			return SysKey.PAGE_UP;
		case RIGHT:
			return SysKey.RIGHT;
		case SHIFT:
			return SysKey.SHIFT_LEFT;
		case TAB:
			return SysKey.TAB;
		case UP:
			return SysKey.UP;
		case SPACE:
			return SysKey.SPACE;
		case INSERT:
			return SysKey.INSERT;
		case SOFTKEY_0:
			return SysKey.NUM_0;
		case SOFTKEY_1:
			return SysKey.NUM_1;
		case SOFTKEY_2:
			return SysKey.NUM_2;
		case SOFTKEY_3:
			return SysKey.NUM_3;
		case SOFTKEY_4:
			return SysKey.NUM_4;
		case SOFTKEY_5:
			return SysKey.NUM_5;
		case SOFTKEY_6:
			return SysKey.NUM_6;
		case SOFTKEY_7:
			return SysKey.NUM_7;
		case SOFTKEY_8:
			return SysKey.NUM_8;
		case SOFTKEY_9:
			return SysKey.NUM_9;
		case A:
			return SysKey.A;
		case B:
			return SysKey.B;
		case C:
			return SysKey.C;
		case D:
			return SysKey.D;
		case E:
			return SysKey.E;
		case F:
			return SysKey.F;
		case G:
			return SysKey.G;
		case H:
			return SysKey.H;
		case I:
			return SysKey.I;
		case J:
			return SysKey.J;
		case K:
			return SysKey.K;
		case L:
			return SysKey.L;
		case M:
			return SysKey.M;
		case N:
			return SysKey.N;
		case O:
			return SysKey.O;
		case P:
			return SysKey.P;
		case Q:
			return SysKey.Q;
		case R:
			return SysKey.R;
		case S:
			return SysKey.S;
		case T:
			return SysKey.T;
		case U:
			return SysKey.U;
		case V:
			return SysKey.V;
		case W:
			return SysKey.W;
		case X:
			return SysKey.X;
		case Y:
			return SysKey.Y;
		case Z:
			return SysKey.Z;
		case NUMPAD0:
			return SysKey.NUM_0;
		case NUMPAD1:
			return SysKey.NUM_1;
		case NUMPAD2:
			return SysKey.NUM_2;
		case NUMPAD3:
			return SysKey.NUM_3;
		case NUMPAD4:
			return SysKey.NUM_4;
		case NUMPAD5:
			return SysKey.NUM_5;
		case NUMPAD6:
			return SysKey.NUM_6;
		case NUMPAD7:
			return SysKey.NUM_7;
		case NUMPAD8:
			return SysKey.NUM_8;
		case NUMPAD9:
			return SysKey.NUM_9;
		case ADD:
			return SysKey.PLUS;
		case F1:
			return SysKey.NUM_1;
		case F2:
			return SysKey.NUM_2;
		case F3:
			return SysKey.NUM_3;
		case F4:
			return SysKey.NUM_4;
		case F5:
			return SysKey.NUM_5;
		case F6:
			return SysKey.NUM_6;
		case F7:
			return SysKey.NUM_7;
		case F8:
			return SysKey.NUM_8;
		case F9:
			return SysKey.NUM_9;
		case EQUALS:
			return SysKey.EQUALS;
		case COMMA:
			return SysKey.COMMA;
		case MINUS:
			return SysKey.MINUS;
		case PERIOD:
			return SysKey.PERIOD;
		case SLASH:
			return SysKey.SLASH;
		case DEAD_GRAVE:
			return SysKey.GRAVE;
		case OPEN_BRACKET:
			return SysKey.LEFT_BRACKET;
		case BACK_SLASH:
			return SysKey.BACKSLASH;
		case CLOSE_BRACKET:
			return SysKey.RIGHT_BRACKET;
		default:
			return SysKey.UNKNOWN;
		}
	}

	private int mods(KeyEvent event) {
		return modifierFlags(event.isAltDown(), event.isControlDown(), event.isMetaDown(), event.isShiftDown());
	}

	private void dispatch(KeyMake.Event event, KeyEvent nevent) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				event.setFlag(mods(nevent));
				game.dispatchEvent(keyboardEvents, event);
			}
		});
	}

	private void dispatch(MouseMake.Event event, MouseEvent nevent) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				game.dispatchEvent(mouseEvents, event);
			}
		});
	}

	private void dispatch(TouchMake.Event[] events, TouchEvent nevent) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				game.dispatchEvent(touchEvents, events);
			}
		});
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
			}
		}
	}

	@Override
	public void callback(LObject<?> o) {

	}
}
