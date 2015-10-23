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
package loon.javase;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import loon.event.SysKey;
import loon.event.SysTouch;
import loon.geom.Vector2f;
import loon.utils.reply.GoFuture;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import static loon.event.KeyMake.*;

public class JavaSELwjglInputMake extends JavaSEInputMake {

	private JFrame frame;

	public JavaSELwjglInputMake(Loon loon) {
		super(loon);
	}

	@Override
	public GoFuture<String> getText(TextType textType, String label,
			String initVal) {
		Object result = JOptionPane.showInputDialog(frame, label, "",
				JOptionPane.QUESTION_MESSAGE, null, null, initVal);
		return GoFuture.success((String) result);
	}

	@Override
	public boolean hasMouseLock() {
		return true;
	}

	@Override
	public boolean isMouseLocked() {
		return Mouse.isGrabbed();
	}

	@Override
	public void setMouseLocked(boolean locked) {
		Mouse.setGrabbed(locked);
	}

	@Override
	void init() {
		try {
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	void update() {
		super.update();
		Keyboard.poll();
		int flags = modifierFlags(
				Keyboard.isKeyDown(Keyboard.KEY_LMENU)
						|| Keyboard.isKeyDown(Keyboard.KEY_RMENU),
				Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
						|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL),
				Keyboard.isKeyDown(Keyboard.KEY_LMETA)
						|| Keyboard.isKeyDown(Keyboard.KEY_RMETA),
				Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
						|| Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
		while (Keyboard.next()) {
			double time = (double) (Keyboard.getEventNanoseconds() / 1000000);
			int keyCode = Keyboard.getEventKey();
			char keyChar = Keyboard.getEventCharacter();
			if (Keyboard.getEventKeyState()) {
				emitKeyPress(time, keyForCode(keyCode), keyChar, true, flags);
			} else {
				emitKeyPress(time, keyForCode(keyCode), keyChar, false, flags);
			}
		}
		while (Mouse.next()) {
			double time = (double) (Mouse.getEventNanoseconds() / 1000000);
			Vector2f m = new Vector2f(Mouse.getEventX(), Display.getHeight()
					- Mouse.getEventY() - 1);
			int btnIdx = Mouse.getEventButton();
			int id = getButton(btnIdx);
			emitMouseButton(time, m.x, m.y, id, Mouse.getEventButtonState(),
					flags);
		}
	}

	private static int getButton(int lwjglButton) {
		switch (lwjglButton) {
		case 0:
			return SysTouch.LEFT;
		case 2:
			return SysTouch.MIDDLE;
		case 1:
			return SysTouch.RIGHT;
		default:
			return -1;
		}
	}

	private int keyForCode(int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_ESCAPE:
			return SysKey.ESCAPE;
		case Keyboard.KEY_1:
			return SysKey.NUM_1;
		case Keyboard.KEY_2:
			return SysKey.NUM_2;
		case Keyboard.KEY_3:
			return SysKey.NUM_3;
		case Keyboard.KEY_4:
			return SysKey.NUM_4;
		case Keyboard.KEY_5:
			return SysKey.NUM_5;
		case Keyboard.KEY_6:
			return SysKey.NUM_6;
		case Keyboard.KEY_7:
			return SysKey.NUM_7;
		case Keyboard.KEY_8:
			return SysKey.NUM_8;
		case Keyboard.KEY_9:
			return SysKey.NUM_9;
		case Keyboard.KEY_0:
			return SysKey.NUM_0;
		case Keyboard.KEY_MINUS:
			return SysKey.MINUS;
		case Keyboard.KEY_EQUALS:
			return SysKey.EQUALS;
		case Keyboard.KEY_BACK:
			return SysKey.BACK;
		case Keyboard.KEY_TAB:
			return SysKey.TAB;
		case Keyboard.KEY_Q:
			return SysKey.Q;
		case Keyboard.KEY_W:
			return SysKey.W;
		case Keyboard.KEY_E:
			return SysKey.E;
		case Keyboard.KEY_R:
			return SysKey.R;
		case Keyboard.KEY_T:
			return SysKey.T;
		case Keyboard.KEY_Y:
			return SysKey.Y;
		case Keyboard.KEY_U:
			return SysKey.U;
		case Keyboard.KEY_I:
			return SysKey.I;
		case Keyboard.KEY_O:
			return SysKey.O;
		case Keyboard.KEY_P:
			return SysKey.P;
		case Keyboard.KEY_LBRACKET:
			return SysKey.LEFT_BRACKET;
		case Keyboard.KEY_RBRACKET:
			return SysKey.RIGHT_BRACKET;
		case Keyboard.KEY_RETURN:
			return SysKey.ENTER;
		case Keyboard.KEY_LCONTROL:
			return SysKey.CONTROL_LEFT;
		case Keyboard.KEY_A:
			return SysKey.A;
		case Keyboard.KEY_S:
			return SysKey.S;
		case Keyboard.KEY_D:
			return SysKey.D;
		case Keyboard.KEY_F:
			return SysKey.F;
		case Keyboard.KEY_G:
			return SysKey.G;
		case Keyboard.KEY_H:
			return SysKey.H;
		case Keyboard.KEY_J:
			return SysKey.J;
		case Keyboard.KEY_K:
			return SysKey.K;
		case Keyboard.KEY_L:
			return SysKey.L;
		case Keyboard.KEY_SEMICOLON:
			return SysKey.SEMICOLON;
		case Keyboard.KEY_APOSTROPHE:
			return SysKey.APOSTROPHE;
		case Keyboard.KEY_GRAVE:
			return SysKey.GRAVE;
		case Keyboard.KEY_LSHIFT:
			return SysKey.SHIFT_LEFT;
		case Keyboard.KEY_BACKSLASH:
			return SysKey.BACKSLASH;
		case Keyboard.KEY_Z:
			return SysKey.Z;
		case Keyboard.KEY_X:
			return SysKey.X;
		case Keyboard.KEY_C:
			return SysKey.C;
		case Keyboard.KEY_V:
			return SysKey.V;
		case Keyboard.KEY_B:
			return SysKey.B;
		case Keyboard.KEY_N:
			return SysKey.N;
		case Keyboard.KEY_M:
			return SysKey.M;
		case Keyboard.KEY_COMMA:
			return SysKey.COMMA;
		case Keyboard.KEY_PERIOD:
			return SysKey.PERIOD;
		case Keyboard.KEY_SLASH:
			return SysKey.SLASH;
		case Keyboard.KEY_RSHIFT:
			return SysKey.SHIFT_RIGHT;
		case Keyboard.KEY_LMENU:
			return SysKey.MENU;
		case Keyboard.KEY_SPACE:
			return SysKey.SPACE;
		case Keyboard.KEY_AT:
			return SysKey.AT;
		case Keyboard.KEY_HOME:
			return SysKey.HOME;
		case Keyboard.KEY_UP:
			return SysKey.UP;
		case Keyboard.KEY_PRIOR:
			return SysKey.PAGE_UP;
		case Keyboard.KEY_LEFT:
			return SysKey.LEFT;
		case Keyboard.KEY_RIGHT:
			return SysKey.RIGHT;
		case Keyboard.KEY_END:
			return SysKey.END;
		case Keyboard.KEY_DOWN:
			return SysKey.DOWN;
		case Keyboard.KEY_NEXT:
			return SysKey.PAGE_DOWN;
		case Keyboard.KEY_INSERT:
			return SysKey.INSERT;
		case Keyboard.KEY_DELETE:
			return SysKey.DEL;
		case Keyboard.KEY_CLEAR:
			return SysKey.CLEAR;
		case Keyboard.KEY_POWER:
			return SysKey.POWER;
		}
		return keyCode;
	}
}
