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
package loon.se;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import loon.LObject;
import loon.events.KeyMake;
import loon.events.MouseMake;
import loon.events.SysKey;
import loon.events.SysTouch;

public class JavaSEInputMake extends JavaSEInput
		implements MouseListener, MouseMotionListener, KeyListener, FocusListener {

	private boolean inDragSequence = false;

	private boolean isRequestingMouseLock;

	public JavaSEInputMake(JavaSEGame game) {
		super(game);
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

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
		int key = keyForCode(e);
		char ch = e.getKeyChar();
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false), e);
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		int key = keyForCode(e);
		char ch = e.getKeyChar();
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, true), e);
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
		int key = keyForCode(e);
		char ch = e.getKeyChar();
		dispatch(new KeyMake.KeyEvent(0, game.time(), ch, key, false), e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		int btn = getMouseButton(e);
		if (btn != -1) {
			emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), -1, true, 0);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		if (!inDragSequence) {
			emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), -1, false, 0);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		int btn = getMouseButton(e);
		if (btn != -1) {
			emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), btn, true, 0);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		inDragSequence = true;
		int btn = getMouseButton(e);
		if (btn != -1) {
			dispatch(new MouseMake.ButtonEvent(0, game.time(), (float) e.getX(), (float) e.getY(), btn, true), e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		if (inDragSequence) {
			inDragSequence = false;
			int btn = getMouseButton(e);
			if (btn != -1) {
				dispatch(new MouseMake.ButtonEvent(0, game.time(), (float) e.getX(), (float) e.getY(), btn, false), e);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		int btn = getMouseButton(e);
		if (btn != -1) {
			emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), btn, true, 0);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (isRequestingMouseLock) {
			return;
		}
		int btn = getMouseButton(e);
		if (btn != -1) {
			emitMouseButton(game.time(), (float) e.getX(), (float) e.getY(), btn, false, 0);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {

	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	private static int keyForCode(java.awt.event.KeyEvent e) {
		switch (e.getKeyCode()) {
		case java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS:
			return SysKey.ALT_LEFT;
		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			return SysKey.BACKSPACE;
		case java.awt.event.KeyEvent.VK_DELETE:
			return SysKey.DEL;
		case java.awt.event.KeyEvent.VK_DOWN:
			return SysKey.DOWN;
		case java.awt.event.KeyEvent.VK_END:
			return SysKey.END;
		case java.awt.event.KeyEvent.VK_ENTER:
			return SysKey.ENTER;
		case java.awt.event.KeyEvent.VK_ESCAPE:
			return SysKey.ESCAPE;
		case java.awt.event.KeyEvent.VK_HOME:
			return SysKey.HOME;
		case java.awt.event.KeyEvent.VK_LEFT:
			return SysKey.LEFT;
		case java.awt.event.KeyEvent.VK_PAGE_DOWN:
			return SysKey.PAGE_DOWN;
		case java.awt.event.KeyEvent.VK_PAGE_UP:
			return SysKey.PAGE_UP;
		case java.awt.event.KeyEvent.VK_RIGHT:
			return SysKey.RIGHT;
		case java.awt.event.KeyEvent.SHIFT_DOWN_MASK:
			return SysKey.SHIFT_LEFT;
		case java.awt.event.KeyEvent.VK_TAB:
			return SysKey.TAB;
		case java.awt.event.KeyEvent.VK_UP:
			return SysKey.UP;
		case java.awt.event.KeyEvent.VK_SPACE:
			return SysKey.SPACE;
		case java.awt.event.KeyEvent.VK_INSERT:
			return SysKey.INSERT;
		case java.awt.event.KeyEvent.VK_0:
			return SysKey.NUM_0;
		case java.awt.event.KeyEvent.VK_1:
			return SysKey.NUM_1;
		case java.awt.event.KeyEvent.VK_2:
			return SysKey.NUM_2;
		case java.awt.event.KeyEvent.VK_3:
			return SysKey.NUM_3;
		case java.awt.event.KeyEvent.VK_4:
			return SysKey.NUM_4;
		case java.awt.event.KeyEvent.VK_5:
			return SysKey.NUM_5;
		case java.awt.event.KeyEvent.VK_6:
			return SysKey.NUM_6;
		case java.awt.event.KeyEvent.VK_7:
			return SysKey.NUM_7;
		case java.awt.event.KeyEvent.VK_8:
			return SysKey.NUM_8;
		case java.awt.event.KeyEvent.VK_9:
			return SysKey.NUM_9;
		case java.awt.event.KeyEvent.VK_A:
			return SysKey.A;
		case java.awt.event.KeyEvent.VK_B:
			return SysKey.B;
		case java.awt.event.KeyEvent.VK_C:
			return SysKey.C;
		case java.awt.event.KeyEvent.VK_D:
			return SysKey.D;
		case java.awt.event.KeyEvent.VK_E:
			return SysKey.E;
		case java.awt.event.KeyEvent.VK_F:
			return SysKey.F;
		case java.awt.event.KeyEvent.VK_G:
			return SysKey.G;
		case java.awt.event.KeyEvent.VK_H:
			return SysKey.H;
		case java.awt.event.KeyEvent.VK_I:
			return SysKey.I;
		case java.awt.event.KeyEvent.VK_J:
			return SysKey.J;
		case java.awt.event.KeyEvent.VK_K:
			return SysKey.K;
		case java.awt.event.KeyEvent.VK_L:
			return SysKey.L;
		case java.awt.event.KeyEvent.VK_M:
			return SysKey.M;
		case java.awt.event.KeyEvent.VK_N:
			return SysKey.N;
		case java.awt.event.KeyEvent.VK_O:
			return SysKey.O;
		case java.awt.event.KeyEvent.VK_P:
			return SysKey.P;
		case java.awt.event.KeyEvent.VK_Q:
			return SysKey.Q;
		case java.awt.event.KeyEvent.VK_R:
			return SysKey.R;
		case java.awt.event.KeyEvent.VK_S:
			return SysKey.S;
		case java.awt.event.KeyEvent.VK_T:
			return SysKey.T;
		case java.awt.event.KeyEvent.VK_U:
			return SysKey.U;
		case java.awt.event.KeyEvent.VK_V:
			return SysKey.V;
		case java.awt.event.KeyEvent.VK_W:
			return SysKey.W;
		case java.awt.event.KeyEvent.VK_X:
			return SysKey.X;
		case java.awt.event.KeyEvent.VK_Y:
			return SysKey.Y;
		case java.awt.event.KeyEvent.VK_Z:
			return SysKey.Z;
		case java.awt.event.KeyEvent.VK_NUMPAD0:
			return SysKey.NUM_0;
		case java.awt.event.KeyEvent.VK_NUMPAD1:
			return SysKey.NUM_1;
		case java.awt.event.KeyEvent.VK_NUMPAD2:
			return SysKey.NUM_2;
		case java.awt.event.KeyEvent.VK_NUMPAD3:
			return SysKey.NUM_3;
		case java.awt.event.KeyEvent.VK_NUMPAD4:
			return SysKey.NUM_4;
		case java.awt.event.KeyEvent.VK_NUMPAD5:
			return SysKey.NUM_5;
		case java.awt.event.KeyEvent.VK_NUMPAD6:
			return SysKey.NUM_6;
		case java.awt.event.KeyEvent.VK_NUMPAD7:
			return SysKey.NUM_7;
		case java.awt.event.KeyEvent.VK_NUMPAD8:
			return SysKey.NUM_8;
		case java.awt.event.KeyEvent.VK_NUMPAD9:
			return SysKey.NUM_9;
		case java.awt.event.KeyEvent.VK_ADD:
			return SysKey.PLUS;
		case java.awt.event.KeyEvent.VK_F1:
			return SysKey.NUM_1;
		case java.awt.event.KeyEvent.VK_F2:
			return SysKey.NUM_2;
		case java.awt.event.KeyEvent.VK_F3:
			return SysKey.NUM_3;
		case java.awt.event.KeyEvent.VK_F4:
			return SysKey.NUM_4;
		case java.awt.event.KeyEvent.VK_F5:
			return SysKey.NUM_5;
		case java.awt.event.KeyEvent.VK_F6:
			return SysKey.NUM_6;
		case java.awt.event.KeyEvent.VK_F7:
			return SysKey.NUM_7;
		case java.awt.event.KeyEvent.VK_F8:
			return SysKey.NUM_8;
		case java.awt.event.KeyEvent.VK_F9:
			return SysKey.NUM_9;
		case java.awt.event.KeyEvent.VK_EQUALS:
			return SysKey.EQUALS;
		case java.awt.event.KeyEvent.VK_COMMA:
			return SysKey.COMMA;
		case java.awt.event.KeyEvent.VK_MINUS:
			return SysKey.MINUS;
		case java.awt.event.KeyEvent.VK_PERIOD:
			return SysKey.PERIOD;
		case java.awt.event.KeyEvent.VK_SLASH:
			return SysKey.SLASH;
		case java.awt.event.KeyEvent.VK_DEAD_GRAVE:
			return SysKey.GRAVE;
		case java.awt.event.KeyEvent.VK_OPEN_BRACKET:
			return SysKey.LEFT_BRACKET;
		case java.awt.event.KeyEvent.VK_BACK_SLASH:
			return SysKey.BACKSLASH;
		case java.awt.event.KeyEvent.VK_CLOSE_BRACKET:
			return SysKey.RIGHT_BRACKET;
		default:
			return SysKey.UNKNOWN;
		}
	}

	protected static int getMouseButton(MouseEvent evt) {
		if (evt.getButton() == MouseEvent.BUTTON1) {
			return SysTouch.LEFT;
		} else if (evt.getButton() == MouseEvent.BUTTON2) {
			return SysTouch.MIDDLE;
		} else if (evt.getButton() == MouseEvent.BUTTON3) {
			return SysTouch.RIGHT;
		} else {
			return -1;
		}
	}

	private int mods(java.awt.event.KeyEvent event) {
		return modifierFlags(event.isAltDown(), event.isControlDown(), event.isMetaDown(), event.isShiftDown());
	}

	private void dispatch(KeyMake.Event event, java.awt.event.KeyEvent nevent) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				event.setFlag(mods(nevent));
				game.dispatchEvent(keyboardEvents, event);
			}
		});
	}

	private void dispatch(MouseMake.Event event, java.awt.event.MouseEvent nevent) {
		game.asyn().invokeLater(new Runnable() {
			@Override
			public void run() {
				game.dispatchEvent(mouseEvents, event);
			}
		});
	}
}
