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
package loon.lwjgl;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_APOSTROPHE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import loon.events.SysKey;
import loon.events.SysTouch;
import loon.geom.Vector2f;

public class Lwjgl3InputMake extends Lwjgl3Input {

	private final long window;

	private int toModifierFlags(int mods) {
		return modifierFlags((mods & GLFW_MOD_ALT) != 0, (mods & GLFW_MOD_CONTROL) != 0, (mods & GLFW_MOD_SUPER) != 0,
				(mods & GLFW_MOD_SHIFT) != 0);
	}

	private int toModifierFlags() {
		return modifierFlags(isKeyDown(GLFW_KEY_LEFT_ALT) || isKeyDown(GLFW_KEY_LEFT_ALT),
				isKeyDown(GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW_KEY_RIGHT_CONTROL),
				isKeyDown(GLFW_KEY_LEFT_SUPER) || isKeyDown(GLFW_KEY_RIGHT_SUPER),
				isKeyDown(GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW_KEY_RIGHT_SHIFT));
	}

	private boolean isKeyDown(int key) {
		return glfwGetKey(window, key) == GLFW_PRESS;
	}

	private final GLFWCursorPosCallback movePosCallback = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long handle, double xpos, double ypos) {
			double time = System.currentTimeMillis();
			float x = (float) xpos, y = (float) ypos;
			emitMouseButton(time, x, y, -1, false, toModifierFlags());
		}
	};

	private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int keyCode, int scancode, int action, int mods) {
			double time = System.currentTimeMillis();
			boolean pressed = action == GLFW_PRESS || action == GLFW_REPEAT;
			int codepoint = keyCode;
			if ((codepoint & 0xff00) == 0xf700) {
				return;
			}
			emitKeyPress(time, keyForCode(keyCode), (char) codepoint, pressed, toModifierFlags(mods));
		}
	};

	private final GLFWMouseButtonCallback mouseBtnCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long handle, int btnIdx, int action, int mods) {
			double time = System.currentTimeMillis();
			Vector2f m = queryCursorPosition();
			int id = getButton(btnIdx);
			emitMouseButton(time, m.x, m.y, id, action == GLFW_PRESS, toModifierFlags(mods));
		}
	};
	private DoubleBuffer xpos = BufferUtils.createByteBuffer(8).asDoubleBuffer();
	private DoubleBuffer ypos = BufferUtils.createByteBuffer(8).asDoubleBuffer();
	private Vector2f cpos = new Vector2f();

	private Vector2f queryCursorPosition() {
		xpos.rewind();
		ypos.rewind();
		glfwGetCursorPos(window, xpos, ypos);
		cpos.set((float) xpos.get(), (float) ypos.get());
		return cpos;
	}

	public Lwjgl3InputMake(Lwjgl3Game game, long window) {
		super(game);
		this.window = window;
		glfwSetKeyCallback(window, keyCallback);
		glfwSetMouseButtonCallback(window, mouseBtnCallback);
		glfwSetCursorPosCallback(window, movePosCallback);
	}

	@Override
	public boolean hasMouseLock() {
		return true;
	}

	@Override
	public boolean isMouseLocked() {
		return glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
	}

	@Override
	public void setMouseLocked(boolean locked) {
		glfwSetInputMode(window, GLFW_CURSOR, locked ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}

	@Override
	void update() {
		glfwPollEvents();
		super.update();
	}

	void shutdown() {
		keyCallback.close();
		mouseBtnCallback.close();
		movePosCallback.close();
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
		case GLFW_KEY_ESCAPE:
			return SysKey.ESCAPE;
		case GLFW_KEY_1:
			return SysKey.NUM_1;
		case GLFW_KEY_2:
			return SysKey.NUM_2;
		case GLFW_KEY_3:
			return SysKey.NUM_3;
		case GLFW_KEY_4:
			return SysKey.NUM_4;
		case GLFW_KEY_5:
			return SysKey.NUM_5;
		case GLFW_KEY_6:
			return SysKey.NUM_6;
		case GLFW_KEY_7:
			return SysKey.NUM_7;
		case GLFW_KEY_8:
			return SysKey.NUM_8;
		case GLFW_KEY_9:
			return SysKey.NUM_9;
		case GLFW_KEY_0:
			return SysKey.NUM_0;
		case GLFW_KEY_MINUS:
			return SysKey.MINUS;
		case GLFW_KEY_EQUAL:
			return SysKey.EQUALS;
		case GLFW_KEY_BACKSPACE:
			return SysKey.BACK;
		case GLFW_KEY_TAB:
			return SysKey.TAB;
		case GLFW_KEY_Q:
			return SysKey.Q;
		case GLFW_KEY_W:
			return SysKey.W;
		case GLFW_KEY_E:
			return SysKey.E;
		case GLFW_KEY_R:
			return SysKey.R;
		case GLFW_KEY_T:
			return SysKey.T;
		case GLFW_KEY_Y:
			return SysKey.Y;
		case GLFW_KEY_U:
			return SysKey.U;
		case GLFW_KEY_I:
			return SysKey.I;
		case GLFW_KEY_O:
			return SysKey.O;
		case GLFW_KEY_P:
			return SysKey.P;
		case GLFW_KEY_LEFT_BRACKET:
			return SysKey.LEFT_BRACKET;
		case GLFW_KEY_RIGHT_BRACKET:
			return SysKey.RIGHT_BRACKET;
		case GLFW_KEY_ENTER:
			return SysKey.ENTER;
		case GLFW_KEY_LEFT_CONTROL:
			return SysKey.CONTROL_LEFT;
		case GLFW_KEY_RIGHT_CONTROL:
			return SysKey.CONTROL_RIGHT;
		case GLFW_KEY_A:
			return SysKey.A;
		case GLFW_KEY_S:
			return SysKey.S;
		case GLFW_KEY_D:
			return SysKey.D;
		case GLFW_KEY_F:
			return SysKey.F;
		case GLFW_KEY_G:
			return SysKey.G;
		case GLFW_KEY_H:
			return SysKey.H;
		case GLFW_KEY_J:
			return SysKey.J;
		case GLFW_KEY_K:
			return SysKey.K;
		case GLFW_KEY_L:
			return SysKey.L;
		case GLFW_KEY_SEMICOLON:
			return SysKey.SEMICOLON;
		case GLFW_KEY_APOSTROPHE:
			return SysKey.APOSTROPHE;
		case GLFW_KEY_GRAVE_ACCENT:
			return SysKey.GRAVE;
		case GLFW_KEY_LEFT_SHIFT:
			return SysKey.SHIFT_LEFT;
		case GLFW_KEY_BACKSLASH:
			return SysKey.BACKSLASH;
		case GLFW_KEY_Z:
			return SysKey.Z;
		case GLFW_KEY_X:
			return SysKey.X;
		case GLFW_KEY_C:
			return SysKey.C;
		case GLFW_KEY_V:
			return SysKey.V;
		case GLFW_KEY_B:
			return SysKey.B;
		case GLFW_KEY_N:
			return SysKey.N;
		case GLFW_KEY_M:
			return SysKey.M;
		case GLFW_KEY_COMMA:
			return SysKey.COMMA;
		case GLFW_KEY_PERIOD:
			return SysKey.PERIOD;
		case GLFW_KEY_SLASH:
			return SysKey.SLASH;
		case GLFW_KEY_RIGHT_SHIFT:
			return SysKey.SHIFT_RIGHT;
		case GLFW_KEY_KP_MULTIPLY:
			return SysKey.MENU;
		case GLFW_KEY_SPACE:
			return SysKey.SPACE;
		case GLFW_KEY_RIGHT_ALT:
			return SysKey.AT;
		case GLFW_KEY_HOME:
			return SysKey.HOME;
		case GLFW_KEY_UP:
			return SysKey.UP;
		case GLFW_KEY_PAGE_UP:
			return SysKey.PAGE_UP;
		case GLFW_KEY_LEFT:
			return SysKey.LEFT;
		case GLFW_KEY_RIGHT:
			return SysKey.RIGHT;
		case GLFW_KEY_END:
			return SysKey.END;
		case GLFW_KEY_DOWN:
			return SysKey.DOWN;
		case GLFW_KEY_PAGE_DOWN:
			return SysKey.PAGE_DOWN;
		case GLFW_KEY_INSERT:
			return SysKey.INSERT;
		case GLFW_KEY_DELETE:
			return SysKey.DEL;
		// case GLFW_KEY_CLEAR:
		// return SysKey.CLEAR;
		// case GLFW_KEY_POWER:
		// return SysKey.POWER;
		}
		return keyCode;
	}
}
