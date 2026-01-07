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
package loon.events;

import loon.LSystem;
import loon.utils.IntArray;
import loon.utils.StringUtils;

public class SysKey {

	public final static int toIntKey(String keyName) {
		if (StringUtils.isNullOrEmpty(keyName)) {
			return -1;
		}
		final String keyValue = StringUtils.replaces(keyName.trim().toLowerCase(), "", "_", "-", " ");
		if (keyValue.equals(LSystem.UNKNOWN)) {
			return UNKNOWN;
		} else if (keyValue.equals("keydown")) {
			return KEY_DOWN;
		} else if (keyValue.equals("keyup")) {
			return KEY_UP;
		} else if (keyValue.equals("keytyped")) {
			return KEY_TYPED;
		} else if (keyValue.equals("anykey")) {
			return ANY_KEY;
		} else if (keyValue.equals("0")) {
			return NUM_0;
		} else if (keyValue.equals("1")) {
			return NUM_1;
		} else if (keyValue.equals("2")) {
			return NUM_2;
		} else if (keyValue.equals("3")) {
			return NUM_3;
		} else if (keyValue.equals("4")) {
			return NUM_4;
		} else if (keyValue.equals("5")) {
			return NUM_5;
		} else if (keyValue.equals("6")) {
			return NUM_6;
		} else if (keyValue.equals("7")) {
			return NUM_7;
		} else if (keyValue.equals("8")) {
			return NUM_8;
		} else if (keyValue.equals("9")) {
			return NUM_9;
		} else if (keyValue.equals("q")) {
			return Q;
		} else if (keyValue.equals("w")) {
			return W;
		} else if (keyValue.equals("e")) {
			return E;
		} else if (keyValue.equals("r")) {
			return R;
		} else if (keyValue.equals("t")) {
			return T;
		} else if (keyValue.equals("y")) {
			return Y;
		} else if (keyValue.equals("u")) {
			return U;
		} else if (keyValue.equals("i")) {
			return I;
		} else if (keyValue.equals("o")) {
			return O;
		} else if (keyValue.equals("p")) {
			return P;
		} else if (keyValue.equals("a")) {
			return A;
		} else if (keyValue.equals("s")) {
			return S;
		} else if (keyValue.equals("d")) {
			return D;
		} else if (keyValue.equals("f")) {
			return F;
		} else if (keyValue.equals("g")) {
			return G;
		} else if (keyValue.equals("h")) {
			return H;
		} else if (keyValue.equals("j")) {
			return J;
		} else if (keyValue.equals("k")) {
			return K;
		} else if (keyValue.equals("l")) {
			return L;
		} else if (keyValue.equals("z")) {
			return Z;
		} else if (keyValue.equals("x")) {
			return X;
		} else if (keyValue.equals("c")) {
			return C;
		} else if (keyValue.equals("b")) {
			return B;
		} else if (keyValue.equals("n")) {
			return N;
		} else if (keyValue.equals("m")) {
			return M;
		} else if (keyValue.equals("tab")) {
			return TAB;
		} else if (keyValue.equals("space")) {
			return SPACE;
		} else if (keyValue.equals("altleft")) {
			return ALT_LEFT;
		} else if (keyValue.equals("altright")) {
			return ALT_RIGHT;
		} else if (keyValue.equals("apostrophe")) {
			return APOSTROPHE;
		} else if (keyValue.equals("at") || keyValue.equals("@")) {
			return AT;
		} else if (keyValue.equals("back")) {
			return BACK;
		} else if (keyValue.equals("backslash")) {
			return BACKSLASH;
		} else if (keyValue.equals("call")) {
			return CALL;
		} else if (keyValue.equals("camera")) {
			return CAMERA;
		} else if (keyValue.equals("clear")) {
			return CLEAR;
		} else if (keyValue.equals("comma")) {
			return COMMA;
		} else if (keyValue.equals("del") || keyValue.equals("remove")) {
			return DEL;
		} else if (keyValue.equals("backspace")) {
			return BACKSPACE;
		} else if (keyValue.equals("forwarddel")) {
			return FORWARD_DEL;
		} else if (keyValue.equals("dpadcenter")) {
			return DPAD_CENTER;
		} else if (keyValue.equals("dpaddown")) {
			return DPAD_DOWN;
		} else if (keyValue.equals("dpadleft")) {
			return DPAD_LEFT;
		} else if (keyValue.equals("dpadright")) {
			return DPAD_RIGHT;
		} else if (keyValue.equals("dpadup")) {
			return DPAD_RIGHT;
		} else if (keyValue.equals("center")) {
			return CENTER;
		} else if (keyValue.equals("down")) {
			return DOWN;
		} else if (keyValue.equals("left")) {
			return LEFT;
		} else if (keyValue.equals("right")) {
			return RIGHT;
		} else if (keyValue.equals("up")) {
			return UP;
		} else if (keyValue.equals("escape") || keyValue.equals("esc")) {
			return ESCAPE;
		} else if (keyValue.equals("endcall")) {
			return ENDCALL;
		} else if (keyValue.equals("enter")) {
			return ENTER;
		} else if (keyValue.equals("envelope")) {
			return ENVELOPE;
		} else if (keyValue.equals("equals")) {
			return EQUALS;
		} else if (keyValue.equals("explorer")) {
			return EXPLORER;
		} else if (keyValue.equals("focus")) {
			return FOCUS;
		} else if (keyValue.equals("grave")) {
			return GRAVE;
		} else if (keyValue.equals("headsethook")) {
			return HEADSETHOOK;
		} else if (keyValue.equals("home")) {
			return HOME;
		} else if (keyValue.equals("leftbracket")) {
			return LEFT_BRACKET;
		} else if (keyValue.equals("mediafastforward")) {
			return MEDIA_FAST_FORWARD;
		} else if (keyValue.equals("medianext")) {
			return MEDIA_NEXT;
		} else if (keyValue.equals("mediaplaypause")) {
			return MEDIA_PLAY_PAUSE;
		} else if (keyValue.equals("mediaprevious")) {
			return MEDIA_PREVIOUS;
		} else if (keyValue.equals("mediarewind")) {
			return MEDIA_REWIND;
		} else if (keyValue.equals("mediastop")) {
			return MEDIA_REWIND;
		} else if (keyValue.equals("menu")) {
			return MENU;
		} else if (keyValue.equals("minus")) {
			return MINUS;
		} else if (keyValue.equals("mute")) {
			return MUTE;
		} else if (keyValue.equals("notification")) {
			return NOTIFICATION;
		} else if (keyValue.equals("num")) {
			return NUM;
		} else if (keyValue.equals("period")) {
			return PERIOD;
		} else if (keyValue.equals("plus")) {
			return PLUS;
		} else if (keyValue.equals("pound")) {
			return POUND;
		} else if (keyValue.equals("power")) {
			return POWER;
		} else if (keyValue.equals("rightbracket")) {
			return RIGHT_BRACKET;
		} else if (keyValue.equals("search")) {
			return SEARCH;
		} else if (keyValue.equals("semicolon")) {
			return SEMICOLON;
		} else if (keyValue.equals("shiftleft")) {
			return SHIFT_LEFT;
		} else if (keyValue.equals("shiftright")) {
			return SHIFT_RIGHT;
		} else if (keyValue.equals("slash")) {
			return SLASH;
		} else if (keyValue.equals("softleft")) {
			return SOFT_LEFT;
		} else if (keyValue.equals("softright")) {
			return SOFT_RIGHT;
		} else if (keyValue.equals("softstar") || keyValue.equals("*")) {
			return STAR;
		} else if (keyValue.equals("sym")) {
			return SYM;
		} else if (keyValue.equals("volumedown")) {
			return VOLUME_DOWN;
		} else if (keyValue.equals("volumeup")) {
			return VOLUME_UP;
		} else if (keyValue.equals("metaaltlefton")) {
			return META_ALT_LEFT_ON;
		} else if (keyValue.equals("metaalton")) {
			return META_ALT_ON;
		} else if (keyValue.equals("metaaltrighton")) {
			return META_ALT_RIGHT_ON;
		} else if (keyValue.equals("metashiftlefton")) {
			return META_SHIFT_LEFT_ON;
		} else if (keyValue.equals("metashiftrighton")) {
			return META_SHIFT_RIGHT_ON;
		} else if (keyValue.equals("metashifton")) {
			return META_SHIFT_ON;
		} else if (keyValue.equals("metasymon")) {
			return META_SYM_ON;
		} else if (keyValue.equals("controlleft")) {
			return CONTROL_LEFT;
		} else if (keyValue.equals("controlright")) {
			return CONTROL_RIGHT;
		} else if (keyValue.equals("end")) {
			return END;
		} else if (keyValue.equals("insert")) {
			return INSERT;
		} else if (keyValue.equals("pageup")) {
			return PAGE_UP;
		} else if (keyValue.equals("pagedown")) {
			return PAGE_DOWN;
		} else if (keyValue.equals("pictsymbols")) {
			return PICTSYMBOLS;
		} else if (keyValue.equals("switchcharset")) {
			return SWITCH_CHARSET;
		} else if (keyValue.equals("buttona")) {
			return BUTTON_A;
		} else if (keyValue.equals("buttonb")) {
			return BUTTON_B;
		} else if (keyValue.equals("buttonc")) {
			return BUTTON_C;
		} else if (keyValue.equals("buttonx")) {
			return BUTTON_X;
		} else if (keyValue.equals("buttony")) {
			return BUTTON_Y;
		} else if (keyValue.equals("buttonz")) {
			return BUTTON_Z;
		} else if (keyValue.equals("buttonl1")) {
			return BUTTON_L1;
		} else if (keyValue.equals("buttonr1")) {
			return BUTTON_R1;
		} else if (keyValue.equals("buttonl2")) {
			return BUTTON_L2;
		} else if (keyValue.equals("buttonr2")) {
			return BUTTON_R2;
		} else if (keyValue.equals("buttonthumbl")) {
			return BUTTON_THUMBL;
		} else if (keyValue.equals("buttonthumbr")) {
			return BUTTON_THUMBR;
		} else if (keyValue.equals("buttonstart")) {
			return BUTTON_START;
		} else if (keyValue.equals("buttonselect")) {
			return BUTTON_SELECT;
		} else if (keyValue.equals("buttonmode")) {
			return BUTTON_MODE;
		} else if (keyValue.equals("buttoncircle")) {
			return BUTTON_CIRCLE;
		}
		return UNKNOWN;
	}

	final static IntArray keys = new IntArray();

	public static boolean USE_ONLY_DOWN = false;
	
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
		return SysInputFactory.finalKey.keyChar;
	}

	public static int getKeyCode() {
		return SysInputFactory.finalKey.keyCode;
	}

	public static int getTypeCode() {
		return SysInputFactory.finalKey.keyCode;
	}

	public boolean isShift() {
		return SysInputFactory.finalKey.isShift();
	}

	public boolean isCtrl() {
		return SysInputFactory.finalKey.isCtrl();
	}

	public boolean isAlt() {
		return SysInputFactory.finalKey.isAlt();
	}

	public static boolean isDown() {
		return SysInputFactory.finalKey.isDown();
	}

	public static boolean isUp() {
		return SysInputFactory.finalKey.isUp();
	}

	public static boolean isKey(String keyName) {
		return SysInputFactory.finalKey.keyCode == toIntKey(keyName);
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

	public static void resetKey() {
		clear();
	}

	public static ActionKey getOnlyKey() {
		return SysInputFactory.onlyKey;
	}

	public static boolean isKeyPressed(String keyName) {
		return isKeyPressed(toIntKey(keyName));
	}

	public static boolean isKeyPressed(int key) {
		if (USE_ONLY_DOWN) {
			if (key == SysKey.ANY_KEY) {
				return keys.length > 0 && SysInputFactory.onlyKey.isPressed();
			} else {
				return keys.contains(key) && SysInputFactory.onlyKey.isPressed();
			}
		} else {
			if (key == SysKey.ANY_KEY) {
				return keys.length > 0;
			} else {
				return keys.contains(key);
			}
		}
	}

	public static boolean isKeyReleased(String keyName) {
		return isKeyReleased(toIntKey(keyName));
	}

	public static boolean isKeyReleased(int key) {
		if (key == SysKey.ANY_KEY) {
			return keys.length > 0 && !SysInputFactory.onlyKey.isPressed();
		} else {
			return keys.contains(key) && !SysInputFactory.onlyKey.isPressed();
		}
	}

	public static GameKey cpy() {
		return SysInputFactory.finalKey.cpy();
	}
}
